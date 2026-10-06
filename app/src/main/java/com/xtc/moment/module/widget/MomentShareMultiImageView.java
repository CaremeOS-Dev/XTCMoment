package com.xtc.moment.module.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.module.bean.CloudFileResource;
import com.xtc.moment.module.bean.ShareImageMoment;
import com.xtc.moment.module.publish.multi.adapter.BaseOverlayPageAdapter;
import com.xtc.moment.module.publish.multi.view.PointerViewPager;
import com.xtc.moment.net.MomentPhotoServeHttpProxy;
import com.xtc.moment.serve.bean.DownloadUrlVo;
import com.xtc.moment.serve.bean.FileBatchUrlParam;
import com.xtc.moment.util.HandlerUtil;
import com.xtc.moment.util.RxUtils;
import com.xtc.utils.encode.JSONUtil;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Moment row for a set of images that were shared into moments from another app.
 */
public class MomentShareMultiImageView extends MomentPhotoView {

    private static final String TAG = "MomentShareMultiImageVi";

    private PointerViewPager vp;
    private ImageView ivAppIcon;
    private TextView tvAppName;
    private MomentContentView mNoSupport;
    private RelativeLayout mRlShareContent;
    private MomentPhotoServeHttpProxy momentPhotoServeHttpProxy;

    public MomentShareMultiImageView(Context context) {
        this(context, null);
    }

    public MomentShareMultiImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MomentShareMultiImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.momentPhotoServeHttpProxy = new MomentPhotoServeHttpProxy(context);
    }

    @Override
    public void initView() {
        this.rootView = LayoutInflater.from(getContext()).inflate(R.layout.item_share_multi_photo_moement, this);
        this.mIcon = (ImageView) this.rootView.findViewById(R.id.iv_account_icon);
        this.ivAccountIconBg = (ImageView) this.rootView.findViewById(R.id.iv_account_icon_bg);
        this.mTvName = (TextView) this.rootView.findViewById(R.id.tv_account_name);
        this.ivOfficialLabel = (ImageView) this.rootView.findViewById(R.id.iv_official_label);
        this.vp = (PointerViewPager) this.rootView.findViewById(R.id.photos_view_page);
        this.rlMomentSender = (RelativeLayout) this.rootView.findViewById(R.id.rl_moment_sender);
        this.ivAppIcon = (ImageView) this.rootView.findViewById(R.id.iv_app_icon);
        this.tvAppName = (TextView) this.rootView.findViewById(R.id.tv_app_name);
        this.mNoSupport = (MomentContentView) this.rootView.findViewById(R.id.tv_no_support);
        this.mRlShareContent = (RelativeLayout) this.rootView.findViewById(R.id.rl_share_content);
        this.mIvReport = (ImageView) this.rootView.findViewById(R.id.iv_account_report);
    }

    @Override
    public void loadImage(final Context context, DbMoment moment) {
        super.loadImage(context, moment);
        ArrayList<String> keys = new ArrayList<String>();
        String resource = moment.getResource();
        LogUtil.i(TAG, "loadImage photoKey" + resource);
        String[] split = resource.split(",");
        String content = moment.getContent();
        final ShareImageMoment shareImageMoment = (ShareImageMoment) JSONUtil.fromJSON(content, ShareImageMoment.class);
        if (shareImageMoment != null) {
            HandlerUtil.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    Glide.with(context).load(shareImageMoment.getAppIcon()).into(ivAppIcon);
                    tvAppName.setText(shareImageMoment.getAppName());
                }
            });
        }
        LogUtil.i(TAG, "loadImage photoKey" + content);
        for (String key : split) {
            keys.add(key);
            LogUtil.i(TAG, "split 拆分 " + key);
        }
        this.momentPhotoServeHttpProxy.getDownloadBatchUrl(new FileBatchUrlParam(keys))
                .map(new Func1<DownloadUrlVo, List<String>>() {
                    @Override
                    public List<String> call(DownloadUrlVo vo) {
                        List<CloudFileResource> urls = vo.getUrls();
                        ArrayList<String> paths = new ArrayList<String>();
                        for (int i = 0; i < urls.size(); i++) {
                            String downloadUrl = urls.get(i).getDownloadUrl();
                            LogUtil.i(TAG, "getDownloadUrl" + downloadUrl);
                            paths.add(downloadUrl);
                        }
                        return paths;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<String>>() {
                    @Override
                    public void call(List<String> paths) {
                        LogUtil.i(TAG, "path批量获取网络图片 " + paths.toString());
                        BaseOverlayPageAdapter adapter = new BaseOverlayPageAdapter(context, true);
                        adapter.refreshView(vp, (ArrayList<String>) paths);
                        vp.setAdapter(adapter);
                        vp.setOffscreenPageLimit(3);
                    }
                }, RxUtils.logError(TAG));
    }
}