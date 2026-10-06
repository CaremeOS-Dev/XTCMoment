package com.xtc.moment.module.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.module.bean.CloudFileResource;
import com.xtc.moment.module.bean.MultiPhotoContent;
import com.xtc.moment.module.publish.multi.SaveDynamic;
import com.xtc.moment.module.publish.multi.adapter.BaseOverlayPageAdapter;
import com.xtc.moment.module.publish.multi.view.PointerViewPager;
import com.xtc.moment.net.MomentPhotoServeHttpProxy;
import com.xtc.moment.serve.bean.DownloadUrlVo;
import com.xtc.moment.serve.bean.FileBatchUrlParam;
import com.xtc.utils.encode.JSONUtil;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Comment page row for a multi photo moment: a view pager over the batch resolved photo urls.
 */
public class MomentPhotosViewComment extends MomentPhotoView {

    private static final String TAG = "MomentPhotosViewComment";

    private PointerViewPager viewPage;
    private TextView mNewLikeContent;
    private MomentPhotoServeHttpProxy momentPhotoServeHttpProxy;

    public MomentPhotosViewComment(Context context) {
        super(context);
    }

    public MomentPhotosViewComment(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MomentPhotosViewComment(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LogUtil.i(TAG, "MomentPhotosViewComment momentPhotoServeHttpProxy =?");
        this.momentPhotoServeHttpProxy = new MomentPhotoServeHttpProxy(context);
    }

    @Override
    public void initView() {
        super.initView();
        this.rootView = LayoutInflater.from(getContext()).inflate(R.layout.view_photos_moment, this);
        this.mIcon = (ImageView) this.rootView.findViewById(R.id.iv_account_icon);
        this.ivAccountIconBg = (ImageView) this.rootView.findViewById(R.id.iv_account_icon_bg);
        this.mTvName = (TextView) this.rootView.findViewById(R.id.tv_account_name);
        this.viewPage = (PointerViewPager) this.rootView.findViewById(R.id.new_like_vp);
        this.rlMomentSender = (RelativeLayout) this.rootView.findViewById(R.id.rl_moment_sender);
        this.mIvReport = (ImageView) this.rootView.findViewById(R.id.iv_account_report);
        this.mNewLikeContent = (TextView) this.rootView.findViewById(R.id.new_like_content);
    }

    @Override
    public void loadImage(final Context context, DbMoment moment) {
        super.loadImage(context, moment);
        ArrayList<String> keys = new ArrayList<String>();
        String content = moment.getContent();
        LogUtil.i(TAG, "content " + content);
        String resource = moment.getResource();
        SaveDynamic.saveIsMomentPhotoView(context, false);
        String[] split = resource.split(",");
        for (String key : split) {
            keys.add(key);
            LogUtil.i(TAG, "split 拆分 " + key);
        }
        this.mNewLikeContent.setVisibility(GONE);
        if (!TextUtils.isEmpty(content)) {
            MultiPhotoContent multiPhotoContent = (MultiPhotoContent) JSONUtil.fromJSON(content, MultiPhotoContent.class);
            if (multiPhotoContent != null && !TextUtils.isEmpty(multiPhotoContent.getContent())) {
                this.mNewLikeContent.setVisibility(VISIBLE);
                this.mNewLikeContent.setText(multiPhotoContent.getContent());
            }
        }
        if (this.momentPhotoServeHttpProxy == null) {
            this.momentPhotoServeHttpProxy = new MomentPhotoServeHttpProxy(context);
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
                        adapter.refreshView(viewPage, (ArrayList<String>) paths);
                        viewPage.setAdapter(adapter);
                        viewPage.setOffscreenPageLimit(3);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable e) {
                        LogUtil.e(TAG, "loadImage error ", e);
                    }
                });
    }
}