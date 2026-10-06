package com.xtc.moment.module.widget.livephotoView;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xtc.database.ormlite.CollectionUtil;
import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.module.bean.CloudFileResource;
import com.xtc.moment.module.bean.MultiPhotoContent;
import com.xtc.moment.module.publish.multi.SaveDynamic;
import com.xtc.moment.module.publish.multi.adapter.BaseOverlayPageAdapter;
import com.xtc.moment.module.publish.multi.view.PointerViewPager;
import com.xtc.moment.module.report.adapter.AbsInteractionAdapter;
import com.xtc.moment.module.widget.AbsMomentView;
import com.xtc.moment.module.widget.ExpandTextView;
import com.xtc.moment.net.MomentPhotoServeHttpProxy;
import com.xtc.moment.serve.AccountInfoServerImpl;
import com.xtc.moment.serve.IMomentServe;
import com.xtc.moment.serve.MomentServeImpl;
import com.xtc.moment.serve.bean.DownloadUrlVo;
import com.xtc.moment.serve.bean.FileBatchUrlParam;
import com.xtc.moment.util.HandlerUtil;
import com.xtc.utils.encode.JSONUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Moment row for a set of live photos: shows a view pager over the photos and pre-plays the
 * paired clips.
 */
public class MomentPhotosView extends AbsMomentView {

    private static final String TAG = "MomentPhotosView";
    private static final String TMP = "_tmp";

    private PointerViewPager vp;
    private ExpandTextView expandTextView;
    private View rootView;
    private ArrayList<String> photosPathKey = new ArrayList<String>();
    private volatile BaseOverlayPageAdapter simpleOverlayAdapter;
    private MomentPhotoServeHttpProxy momentPhotoServeHttpProxy;
    private IMomentServe iMomentServe;
    private String selfWatchId;

    public MomentPhotosView(Context context) {
        this(context, null);
    }

    public MomentPhotosView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MomentPhotosView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.momentPhotoServeHttpProxy = new MomentPhotoServeHttpProxy(context);
        this.selfWatchId = AccountInfoServerImpl.getInstance(context).getWatchAccountInfo().getWatchId(context);
        this.iMomentServe = MomentServeImpl.getInstance(context.getApplicationContext());
    }

    @Override
    public void initView() {
        if (this.rootView == null) {
            this.rootView = LayoutInflater.from(getContext()).inflate(R.layout.view_page_photos_moment, this);
        }
        this.mIcon = (ImageView) this.rootView.findViewById(R.id.iv_account_icon);
        this.ivAccountIconBg = (ImageView) this.rootView.findViewById(R.id.iv_account_icon_bg);
        this.mTvName = (TextView) this.rootView.findViewById(R.id.tv_account_name);
        this.rlMomentSender = (RelativeLayout) this.rootView.findViewById(R.id.rl_moment_sender);
        this.mIvReport = (ImageView) this.rootView.findViewById(R.id.iv_account_report);
        this.vp = (PointerViewPager) this.rootView.findViewById(R.id.photos_view_page);
        this.expandTextView = (ExpandTextView) this.rootView.findViewById(R.id.ev_text);
    }

    @Override
    public void loadImage(Context context, DbMoment moment) {
        super.loadImage(context, moment);
        refreshOverlayAdapter(context);
        this.simpleOverlayAdapter.setCurrentDbMoment(moment);
        if (checkContextIsNull(context)) {
            return;
        }
        this.photosPathKey = splitDownloadKeys(moment.getResource());
        SaveDynamic.saveIsMomentPhotoView(context, false);
        HandlerUtil.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                expandTextView.setVisibility(GONE);
            }
        });
        MultiPhotoContent multiPhotoContent = (MultiPhotoContent) JSONUtil.fromJSON(moment.getContent(), MultiPhotoContent.class);
        if (multiPhotoContent == null) {
            LogUtil.i(TAG, "loadImage, photoMsg is null");
            displayNetPhoto(moment, null);
            return;
        }
        showContentTv(multiPhotoContent.getContent());
        ArrayList<String> localPaths = splitDownloadKeys(multiPhotoContent.getLocalPaths());
        LogUtil.i(TAG, "localPaths = " + localPaths);
        if (CollectionUtil.isEmpty(localPaths)) {
            displayNetPhoto(moment, multiPhotoContent);
            return;
        }
        Iterator<String> iterator = localPaths.iterator();
        while (iterator.hasNext()) {
            if (!new File(iterator.next()).exists()) {
                displayNetPhoto(moment, multiPhotoContent);
                return;
            }
        }
        LogUtil.i(TAG, "use Local cache");
        startLoadImage(localPaths);
    }

    private void showContentTv(final String content) {
        HandlerUtil.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                if (TextUtils.isEmpty(content)) {
                    return;
                }
                expandTextView.setVisibility(VISIBLE);
                if (isTextSupportExpand()) {
                    expandTextView.setMaxLines(3);
                    expandTextView.setSupportExpand(true);
                }
                expandTextView.setText(content, new ExpandTextView.ClickCheckAllListener() {
                    @Override
                    public void click() {
                        MomentPhotosView view = MomentPhotosView.this;
                        view.startDetailActivity(view.getDbMoment());
                    }
                });
            }
        });
    }

    private void displayNetPhoto(DbMoment moment, MultiPhotoContent multiPhotoContent) {
        if (multiPhotoContent == null) {
            LogUtil.i(TAG, "dislplayNetPhoto, photoMsg is null");
            loadImageWithKey(moment, null);
            return;
        }
        CloudFileResource resource = multiPhotoContent.getResource();
        if (resource == null || TextUtils.isEmpty(resource.getDownloadUrl())) {
            LogUtil.i(TAG, "dislplayNetPhoto, photoMsg source is empty");
            loadImageWithKey(moment, multiPhotoContent);
            return;
        }
        if (System.currentTimeMillis() > resource.getUrlDeadline()) {
            LogUtil.i(TAG, "dowmLoadurl deadline past due");
            loadImageWithKey(moment, multiPhotoContent);
            return;
        }
        ArrayList<String> netUrls = splitDownloadKeys(resource.getDownloadUrl());
        LogUtil.i(TAG, "dislplayNetPhoto, netUrls = " + netUrls);
        startLoadImage(netUrls);
    }

    private void startLoadImage(final ArrayList<String> paths) {
        HandlerUtil.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                LogUtil.i(TAG, "startLoadImage");
                if (simpleOverlayAdapter == null) {
                    return;
                }
                simpleOverlayAdapter.refreshViewWithUrlCheck(vp, paths);
            }
        });
    }

    private void loadImageWithKey(final DbMoment moment, MultiPhotoContent multiPhotoContent) {
        LogUtil.i(TAG, "getDownloadBatchUrl");
        if (CollectionUtil.isEmpty(this.photosPathKey)) {
            ArrayList<String> paths = new ArrayList<String>();
            paths.add(TAG);
            startLoadImage(paths);
            return;
        }
        final MultiPhotoContent content = multiPhotoContent == null ? new MultiPhotoContent() : multiPhotoContent;
        this.momentPhotoServeHttpProxy.getDownloadBatchUrl(new FileBatchUrlParam(this.photosPathKey))
                .map(new Func1<DownloadUrlVo, ArrayList<String>>() {
                    @Override
                    public ArrayList<String> call(DownloadUrlVo vo) {
                        if (content.getResource() == null) {
                            content.setResource(new CloudFileResource());
                        }
                        List<CloudFileResource> urls = vo.getUrls();
                        ArrayList<String> paths = new ArrayList<String>();
                        StringBuilder builder = new StringBuilder();
                        for (int i = 0; i < urls.size(); i++) {
                            String downloadUrl = urls.get(i).getDownloadUrl();
                            LogUtil.i(TAG, "getDownloadUrl" + downloadUrl + " urlDeadline = ");
                            paths.add(downloadUrl);
                            builder.append(downloadUrl);
                            builder.append(",");
                            content.getResource().setUrlDeadline(urls.get(i).getUrlDeadline());
                        }
                        String joined = builder.toString();
                        content.getResource().setDownloadUrl(joined.substring(0, joined.length() - 1));
                        moment.setContent(JSONUtil.toJSON(content));
                        iMomentServe.updateMoment(moment);
                        return paths;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ArrayList<String>>() {
                    @Override
                    public void call(ArrayList<String> paths) {
                        LogUtil.i(TAG, "path批量获取网络图片 " + paths.toString());
                        startLoadImage(paths);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        LogUtil.e(TAG, "getDownloadBatchUrl error ", throwable);
                        startLoadImage(photosPathKey);
                    }
                });
    }

    private ArrayList<String> splitDownloadKeys(String keys) {
        LogUtil.i(TAG, "splitDowmLoadKeys");
        return TextUtils.isEmpty(keys)
                ? new ArrayList<String>()
                : new ArrayList<String>(Arrays.asList(keys.split(",")));
    }

    @Override
    public void setContentOnLongClickListener(Context context, final DbMoment moment,
                                              final AbsMomentView.OnContentOnLongClickListener listener) {
        refreshOverlayAdapter(context);
        if (this.simpleOverlayAdapter != null) {
            this.simpleOverlayAdapter.setOnClickReport(new BaseOverlayPageAdapter.onClickReport() {
                @Override
                public void report(final int position) {
                    if (!moment.getWatchId().equals(selfWatchId)) {
                        showReportBtnDialog(new AbsInteractionAdapter.IOnDialogClickLister() {
                            @Override
                            public void onRightBtnClick() {
                                if (position >= 0 && position < photosPathKey.size()) {
                                    startReportActivity(moment, "", photosPathKey.get(position), null, 2);
                                } else {
                                    LogUtil.i(TAG, "report button click, position unvalid");
                                }
                            }
                        });
                        return;
                    }
                    listener.deleteItem(moment);
                }
            });
        }
        this.expandTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!moment.getWatchId().equals(selfWatchId)) {
                    if (!supportReportType(moment)) {
                        return false;
                    }
                    showReportBtnDialog(new AbsInteractionAdapter.IOnDialogClickLister() {
                        @Override
                        public void onRightBtnClick() {
                            String text = expandTextView.getText();
                            LogUtil.i(TAG, "momentContent " + text);
                            startReportActivity(moment, "", text, null, 1);
                        }
                    });
                    return false;
                }
                listener.deleteItem(moment);
                return false;
            }
        });
    }

    private synchronized void refreshOverlayAdapter(Context context) {
        if (this.simpleOverlayAdapter == null) {
            this.simpleOverlayAdapter = new BaseOverlayPageAdapter(context, true);
        }
    }
}