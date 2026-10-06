package com.xtc.moment.module.comment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.data.IMomentsDataSource;
import com.xtc.moment.data.MomentsRepository;
import com.xtc.moment.data.local.MomentsLocalDataSource;
import com.xtc.moment.data.remote.MomentsRemoteDataSource;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.module.bean.LbsStarEvent;
import com.xtc.moment.module.widget.LoadingPupWindowHolder;
import com.xtc.moment.net.bean.LbsStarBean;
import com.xtc.moment.net.bean.MomentLbs;
import com.xtc.moment.serve.IMomentServe;
import com.xtc.moment.serve.MomentServeImpl;
import com.xtc.moment.share.other.BehaviorEvent;
import com.xtc.moment.util.HandlerUtil;
import com.xtc.moment.util.ToastUtil;
import com.xtc.moment.widget.LbsStarLayout;
import com.xtc.utils.common.CollectionUtil;
import com.xtc.virtualselfapi.constants.Constants;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 给动态位置评价星级的页面。
 */
public class CommentLbsActivity extends Activity
        implements View.OnClickListener, RequestListener<GifDrawable>, LbsStarLayout.OnLevelChangeListener {

    private static final String TAG = "CommentLbsActivity";
    private static final String EXTRA_MOMENT_ID = "MOMENT_ID";
    private static final String EXTRA_WATCH_ID = "WATCH_ID";
    private static final String EXTRA_LOCATION = "LOCATION";

    private LbsStarLayout llStar;
    private TextView tvText;
    private ImageView ivEmoji;
    private View vMask;
    private LoadingPupWindowHolder loadingPupWindowHolder;
    private IMomentServe momentServe;
    private IMomentsDataSource momentsDataSource;

    public static void start(Context context, String momentId, String watchId, String location) {
        Intent intent = new Intent(context, CommentLbsActivity.class);
        intent.putExtra(EXTRA_MOMENT_ID, momentId);
        intent.putExtra(EXTRA_WATCH_ID, watchId);
        intent.putExtra(EXTRA_LOCATION, location);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_lbs);
        this.llStar = (LbsStarLayout) findViewById(R.id.ll_star);
        this.tvText = (TextView) findViewById(R.id.tv_text);
        this.llStar.setLevelchangelistener(this);
        findViewById(R.id.btn_confirm).setOnClickListener(this);
        this.ivEmoji = (ImageView) findViewById(R.id.iv_emoji);
        this.vMask = findViewById(R.id.v_mask);
        initData();
    }

    private void initData() {
        LogUtil.d(TAG, "initData");
        this.loadingPupWindowHolder = new LoadingPupWindowHolder(this);
        this.loadingPupWindowHolder.setOnSuccessAction(new Runnable() {
            @Override
            public void run() {
                CommentLbsActivity.this.finish();
            }
        });
        this.momentServe = MomentServeImpl.getInstance(this);
        this.momentsDataSource = MomentsRepository.getInstance(
                MomentsRemoteDataSource.getInstance(getApplicationContext()),
                MomentsLocalDataSource.getInstance(getApplicationContext()));
    }

    @Override
    public void onLevelChange(int level) {
        LogUtil.d(TAG, "onLevelChange: level = [" + level + "]");
        Glide.with(this).asGif().load(getStarEmoji(level)).listener(this).into(this.ivEmoji);
        this.tvText.setText(getStarLabel(level));
    }

    private int getStarEmoji(int level) {
        if (level == 1) {
            return R.drawable.big_face_emoji_014;
        }
        if (level == 2) {
            return R.drawable.big_face_emoji_019;
        }
        if (level == 3) {
            return R.drawable.big_face_emoji_013;
        }
        if (level == 5) {
            return R.drawable.big_face_emoji_008;
        }
        return R.drawable.big_face_emoji_002;
    }

    private String getStarLabel(int level) {
        if (level == 1) {
            return getString(R.string.lbs_star_1);
        }
        if (level == 2) {
            return getString(R.string.lbs_star_2);
        }
        if (level == 3) {
            return getString(R.string.lbs_star_3);
        }
        if (level == 4) {
            return getString(R.string.lbs_star_4);
        }
        if (level == 5) {
            return getString(R.string.lbs_star_5);
        }
        return getString(R.string.lbs_star_1);
    }

    @Override
    public void onClick(View view) {
        if (this.llStar.getLevel() == 0) {
            return;
        }
        this.loadingPupWindowHolder.showLoading(getWindow().getDecorView());
        this.loadingPupWindowHolder.setText(getString(R.string.lbs_star_success));
        final String momentId = getIntent().getStringExtra(EXTRA_MOMENT_ID);
        this.momentsDataSource.doLbsStar(new LbsStarBean(momentId,
                        getIntent().getStringExtra(EXTRA_WATCH_ID),
                        getIntent().getStringExtra(EXTRA_LOCATION), this.llStar.getLevel()))
                .subscribeOn(Schedulers.io())
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String result) {
                        List<DbMoment> moments = CommentLbsActivity.this.momentServe.getMomentById(momentId);
                        if (CollectionUtil.isEmpty(moments)) {
                            LogUtil.e(TAG, "doLbsStar() dbMoments is empty");
                            return result;
                        }
                        DbMoment moment = moments.get(0);
                        MomentLbs momentLbs = moment.getMomentLbs();
                        momentLbs.setStar(CommentLbsActivity.this.llStar.getLevel());
                        momentLbs.setState(1);
                        moment.setMomentLbs(momentLbs);
                        CommentLbsActivity.this.momentServe.updateMoment(moment);
                        EventBus.getDefault().post(new LbsStarEvent(moment));
                        return result;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String result) {
                        LogUtil.i(TAG, "doLbsStar() " + result);
                        BehaviorEvent.lbsStarLevel(CommentLbsActivity.this, CommentLbsActivity.this.llStar.getLevel());
                        CommentLbsActivity.this.loadingPupWindowHolder.showSuccess();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        LogUtil.e(TAG, "doLbsStar()", throwable);
                        CommentLbsActivity.this.loadingPupWindowHolder.dismissLoading();
                        ToastUtil.showNoConnected(CommentLbsActivity.this);
                    }
                });
    }

    @Override
    public boolean onLoadFailed(GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
        LogUtil.e(TAG, "onLoadFailed()", e);
        return false;
    }

    @Override
    public boolean onResourceReady(final GifDrawable resource, Object model, Target<GifDrawable> target,
            DataSource dataSource, boolean isFirstResource) {
        LogUtil.d(TAG, "onResourceReady: resource = [" + resource + "]");
        resource.stop();
        if (!resource.isRunning()) {
            try {
                resource.startFromFirstFrame();
            } catch (Exception e) {
                LogUtil.e(TAG, "startFromFirstFrame error ", e);
                resource.start();
            }
            resource.setLoopCount(1);
        }
        this.vMask.setVisibility(View.VISIBLE);
        this.ivEmoji.setVisibility(View.VISIBLE);
        this.tvText.setVisibility(View.VISIBLE);
        this.tvText.setAlpha(0.0f);
        this.vMask.setAlpha(0.0f);
        this.ivEmoji.setAlpha(0.0f);
        this.vMask.animate().alpha(0.8f).setDuration(200L);
        this.ivEmoji.animate().alpha(1.0f).setDuration(200L);
        this.tvText.animate().alpha(1.0f).setDuration(200L);
        HandlerUtil.runOnUIThreadDelay(new Runnable() {
            @Override
            public void run() {
                LogUtil.d(TAG, "run");
                resource.stop();
                CommentLbsActivity.this.vMask.animate().alpha(0.0f).setDuration(200L)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                CommentLbsActivity.this.vMask.setVisibility(View.GONE);
                            }
                        });
                CommentLbsActivity.this.tvText.animate().alpha(0.0f).setDuration(200L)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                CommentLbsActivity.this.tvText.setVisibility(View.GONE);
                            }
                        });
                CommentLbsActivity.this.ivEmoji.animate().alpha(0.0f).setDuration(200L)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                CommentLbsActivity.this.ivEmoji.setVisibility(View.INVISIBLE);
                            }
                        });
            }
        }, Constants.DEFAULT_INIT_DELAY_TIME);
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.loadingPupWindowHolder.dismissLoading();
    }
}