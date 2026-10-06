package com.xtc.moment.module.barrage;

import android.animation.ObjectAnimator;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.xtc.architecture.mvp.BaseFragment;
import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.db.bean.DbMomentComment;
import com.xtc.moment.module.Constants;
import com.xtc.moment.module.bean.ChangePlayStateEvent;
import com.xtc.moment.module.bean.ShareVideoMoment;
import com.xtc.moment.module.gift.GiftDetailsActivity;
import com.xtc.moment.module.widget.BarrageDrawerView;
import com.xtc.moment.module.widget.VideoAnimView;
import com.xtc.moment.net.bean.SearchGiftResponse;
import com.xtc.moment.serve.AccountInfoServerImpl;
import com.xtc.moment.serve.bean.WatchAccountInfo;
import com.xtc.moment.third.behavior.MomentBehavior;
import com.xtc.moment.util.ChatVideoUtil;
import com.xtc.moment.util.HandlerUtil;
import com.xtc.moment.util.IntentUtils;
import com.xtc.moment.util.MomentFileUtils;
import com.xtc.moment.util.MomentTypeUtil;
import com.xtc.moment.util.SystemUtil;
import com.xtc.moment.util.ToastUtil;
import com.xtc.moment.util.Utils;
import com.xtc.utils.common.CollectionUtil;
import com.xtc.utils.encode.JSONUtil;
import com.xtc.utils.ui.DimenUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.android.BaseCacheStuffer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.model.android.SpannedCacheStuffer;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.ui.widget.DanmakuView;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 动态详情里的弹幕与礼物页面。
 *
 * <p>弹幕数据来自服务端，按视频时长决定弹幕条数；用户可发送鲜花与彩蛋，自己发布的动态还能
 * 进入礼物详情查看赠送记录。
 */
public class BarrageFragment extends BaseFragment<IBarrageView, BarragePresenter>
        implements LifecycleOwner, View.OnClickListener, IBarrageView {

    private static final String TAG = "BarrageFragment";

    public static final String MOMENT_DATA = "moment_data";

    private static final int VIBRATOR_TIME = 100;
    private static final int BARRAGE_MIN_SIZE = 6;
    private static final int BARRAGE_MAX_SIZE = 12;
    private static final int VIDEO_MAX_LENGTH = 5;
    private static final int BARRAGE_MIN_LENGTH = 3;
    private static final int BARRAGE_MAX_LENGTH = 5;
    private static final int RANDOM_INT = 1000;
    private static final int MAX_GIFT_COUNT = 99;

    private int dp_18 = 18;
    private int dp_0 = 0;

    private LifecycleRegistry lifecycleRegistry;
    private DanmakuView danmakuView;
    private DanmakuContext danmakuContext;
    private BarrageDrawerView llBottomContentView;
    private VideoAnimView videoAnimation;
    private FrameLayout frameLayout;
    private ImageView ivBarrage;
    private ImageView ivArrow;
    private TextView tvFlowerCount;
    private TextView tvEggCount;
    private Vibrator vibrator;

    private DbMoment dbMoment;
    private SearchGiftResponse response;
    private String selfWatchId;
    private String selfName;
    private int videoLength;
    private boolean bottomViewIsVisible = false;
    private volatile boolean showDanmu;
    private volatile List<DbMomentComment> momentComments = new ArrayList<>();
    private ExecutorService executorService;

    volatile boolean isPlaySuc = true;
    volatile boolean isInitPlayController = false;

    private final BaseDanmakuParser parser = new BaseDanmakuParser() {
        @Override
        protected IDanmakus parse() {
            return new Danmakus();
        }
    };

    private final BaseCacheStuffer.Proxy mCacheStufferAdapter = new BaseCacheStuffer.Proxy() {
        @Override
        public void prepareDrawing(BaseDanmaku danmaku, boolean fromWorkerThread) {
            LogUtil.d(TAG, "prepareDrawing: ");
        }

        @Override
        public void releaseResource(BaseDanmaku danmaku) {
            LogUtil.d(TAG, "releaseResource: ");
        }
    };

    @Override
    public void initView() {
    }

    @Subscribe
    public void receive(ChangePlayStateEvent event) {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_barrage, container, false);
        initView(contentView);
        initData();
        initDanMu(contentView);
        return contentView;
    }

    private void initView(View contentView) {
        this.vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        EventBus.getDefault().register(this);
        contentView.findViewById(R.id.iv_send_egg).setOnClickListener(this);
        contentView.findViewById(R.id.iv_send_flower).setOnClickListener(this);
        contentView.findViewById(R.id.rl_send_egg_parent).setOnClickListener(this);
        contentView.findViewById(R.id.rl_send_flower_parent).setOnClickListener(this);
        contentView.findViewById(R.id.ll_hide_danmu).setOnClickListener(this);
        if (this.lifecycleRegistry == null) {
            this.lifecycleRegistry = new LifecycleRegistry(this);
        }
        this.lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
        this.videoAnimation = (VideoAnimView) contentView.findViewById(R.id.video_anim);
        this.ivBarrage = (ImageView) contentView.findViewById(R.id.cb_hide_barrage);
        this.tvFlowerCount = (TextView) contentView.findViewById(R.id.tv_flower_count);
        this.tvEggCount = (TextView) contentView.findViewById(R.id.tv_egg_count);
        this.ivBarrage.setOnClickListener(this);
        this.ivBarrage.setBackground(ContextCompat.getDrawable(getActivity(),
                R.drawable.ic_props_barrage_turn_on));
        this.llBottomContentView = (BarrageDrawerView) contentView.findViewById(R.id.ll_bottom_contentView);
        this.llBottomContentView.setOnClickListener(this);
        this.llBottomContentView.setOnScrollListener(new BarrageDrawerView.OnScrollListener() {
            @Override
            public void scrollToBottom() {
                BarrageFragment.this.hideBottomView();
            }
        });
        this.ivArrow = (ImageView) contentView.findViewById(R.id.iv_arrow);
        this.ivArrow.setOnClickListener(this);
        this.presenter.checkModuleSwitch();
        this.frameLayout = (FrameLayout) contentView.findViewById(R.id.fl_take_same_parent);
        this.frameLayout.setVisibility(View.GONE);
        this.frameLayout.setOnClickListener(this);
    }
    @Override
    public void showBtn() {
        LogUtil.d(TAG, "showBtn: ");
        HandlerUtil.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                BarrageFragment.this.frameLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    /** 播放送礼动画，播放器未初始化时先初始化再播放。 */
    private void playAnimation(final String animationDir, final String animationName) {
        if (!this.isPlaySuc) {
            LogUtil.e(TAG, "playAnimation: 还未播放完毕！！");
            return;
        }
        this.videoAnimation.setVisibility(View.VISIBLE);
        if (this.isInitPlayController) {
            this.videoAnimation.startAnimation(animationDir, animationName);
            return;
        }
        this.videoAnimation.setLooping(false);
        this.videoAnimation.initPlayerController(getActivity(), this, new VideoAnimView.VideoAnimationListener() {
            @Override
            public void onPlayerReady() {
                LogUtil.w(TAG, "onPlayerReady: ");
                HandlerUtil.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        BarrageFragment.this.isInitPlayController = true;
                        BarrageFragment.this.videoAnimation.startAnimation(animationDir, animationName);
                    }
                });
            }

            @Override
            public void onFirstFrame() {
                BarrageFragment.this.isPlaySuc = false;
                LogUtil.w(TAG, "onFirstFrame: ");
            }

            @Override
            public void onEnd() {
                LogUtil.w(TAG, "onEnd: ");
                BarrageFragment.this.isPlaySuc = true;
            }

            @Override
            public void onError(String playType, int what, int extra, String errorInfo) {
                LogUtil.w(TAG, "onError: playType=" + playType + " what=" + what + "  extra=" + extra);
            }
        });
    }

    private void showBottomView() {
        BarrageDrawerView bottomView = this.llBottomContentView;
        if (bottomView == null) {
            return;
        }
        bottomView.setVisibility(View.VISIBLE);
        ObjectAnimator.ofFloat(this.llBottomContentView, "translationY", 360.0f, 0.0f)
                .setDuration(200L).start();
        this.bottomViewIsVisible = true;
        hidePullArrow();
    }

    public void hideBottomView() {
        BarrageDrawerView bottomView = this.llBottomContentView;
        if (bottomView == null || !this.bottomViewIsVisible) {
            return;
        }
        ObjectAnimator.ofFloat(bottomView, "translationY", 0.0f, 360.0f).setDuration(800L).start();
        this.bottomViewIsVisible = false;
        showPullArrow();
    }

    private void hidePullArrow() {
        ImageView arrow = this.ivArrow;
        if (arrow != null) {
            ObjectAnimator.ofFloat(arrow, "alpha", 1.0f, 0.0f).setDuration(200L).start();
        }
    }

    private void showPullArrow() {
        ImageView arrow = this.ivArrow;
        if (arrow != null) {
            ObjectAnimator.ofFloat(arrow, "alpha", 0.0f, 1.0f).setDuration(200L).start();
        }
    }


    @Override
    public void initData() {
        WatchAccountInfo watchAccountInfo = AccountInfoServerImpl.getInstance(getContext()).getWatchAccountInfo();
        this.selfWatchId = watchAccountInfo.getWatchId(getContext());
        this.selfName = watchAccountInfo.getName(getActivity());
        String momentJson = getArguments().getString(MOMENT_DATA);
        if (TextUtils.isEmpty(momentJson)) {
            LogUtil.d(TAG, "string is null");
            return;
        }
        Observable.just(momentJson)
                .map(new Func1<String, DbMoment>() {
                    @Override
                    public DbMoment call(String json) {
                        DbMoment moment = JSONUtil.fromJSON(json, DbMoment.class);
                        if (moment != null) {
                            BarrageFragment.this.videoLength = (int)
                                    (JSONUtil.fromJSON(moment.getContent(), ShareVideoMoment.class)
                                            .getVideoLength() / 1000.0f);
                            LogUtil.d(TAG, "initData: videoLength = " + BarrageFragment.this.videoLength);
                        }
                        return moment;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<DbMoment>() {
                    @Override
                    public void call(DbMoment moment) {
                        BarrageFragment.this.dbMoment = moment;
                        if (BarrageFragment.this.dbMoment != null) {
                            BarrageFragment.this.momentComments = moment.getComments();
                            BarrageFragment.this.presenter.getBarrageData(BarrageFragment.this.dbMoment);
                        }
                        LogUtil.d(TAG, "initData: dbMoment：" + BarrageFragment.this.dbMoment);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        LogUtil.e(TAG, "initData: ", throwable);
                    }
                });
    }
    private void initDanMu(View contentView) {
        this.danmakuView = (DanmakuView) contentView.findViewById(R.id.danmaku_view);
        this.danmakuContext = DanmakuContext.create();
        HashMap<Integer, Integer> maxLinesMap = new HashMap<>();
        maxLinesMap.put(BaseDanmaku.TYPE_SCROLL_RL, BARRAGE_MAX_LENGTH);
        HashMap<Integer, Boolean> overlappingMap = new HashMap<>();
        overlappingMap.put(BaseDanmaku.TYPE_SCROLL_RL, true);
        overlappingMap.put(BaseDanmaku.TYPE_FIX_BOTTOM, true);
        BackgroundCacheStuffer backgroundCacheStuffer = new BackgroundCacheStuffer();
        backgroundCacheStuffer.setBgColor(getActivity().getResources().getColor(R.color.reasons_color_4d));
        this.danmakuContext.setDanmakuStyle(2, 30.0f)
                .setDuplicateMergingEnabled(false)
                .setScrollSpeedFactor(0.9f)
                .setScaleTextSize(1.5f)
                .setCacheStuffer(new SpannedCacheStuffer(), this.mCacheStufferAdapter)
                .setCacheStuffer(backgroundCacheStuffer, this.mCacheStufferAdapter)
                .setMaximumLines(maxLinesMap)
                .setOverlapping(overlappingMap);
        this.danmakuView.enableDanmakuDrawingCache(true);
        this.danmakuView.setCallback(new DrawHandler.Callback() {
            @Override
            public void updateTimer(DanmakuTimer timer) {
            }

            @Override
            public void prepared() {
                LogUtil.d(TAG, "prepared: ");
                BarrageFragment.this.showDanmu = true;
                BarrageFragment.this.danmakuView.start();
            }

            @Override
            public void danmakuShown(BaseDanmaku danmaku) {
                LogUtil.d(TAG, "danmakuShown: ");
            }

            @Override
            public void drawingFinished() {
                LogUtil.d(TAG, "drawingFinished: " + BarrageFragment.this.showDanmu);
                if (BarrageFragment.this.showDanmu) {
                    BarrageFragment.this.danmakuView.seekTo(0L);
                }
            }
        });
        this.danmakuView.prepare(this.parser, this.danmakuContext);
    }

    @Override
    public Lifecycle getLifecycle() {
        return this.lifecycleRegistry;
    }

    /** 带背景色的弹幕缓存绘制器。 */
    private static class BackgroundCacheStuffer extends SpannedCacheStuffer {

        private static final float CORNER_RADIUS = 60;

        int bgColor;
        final Paint paint;
        final Paint strokePaint;

        private BackgroundCacheStuffer() {
            this.paint = new Paint();
            this.strokePaint = new Paint();
        }

        public void setBgColor(int bgColor) {
            this.bgColor = bgColor;
            this.strokePaint.setStyle(Paint.Style.STROKE);
            this.strokePaint.setColor(-1);
            this.strokePaint.setStrokeWidth(2.0f);
            this.strokePaint.setAntiAlias(true);
            this.paint.setAntiAlias(true);
        }

        @Override
        public void drawStroke(BaseDanmaku danmaku, String text, Canvas canvas, float x, float y, Paint paint) {
        }

        @Override
        public void measure(BaseDanmaku danmaku, TextPaint paint, boolean fromWorkerThread) {
            super.measure(danmaku, paint, fromWorkerThread);
        }

        @Override
        public void drawBackground(BaseDanmaku danmaku, Canvas canvas, float left, float top) {
            if (!((Boolean) danmaku.tag).booleanValue()) {
                return;
            }
            this.paint.setColor(this.bgColor);
            RectF rect = new RectF(left + 2.0f, top + 2.0f,
                    (left + danmaku.paintWidth) - 2.0f, (top + danmaku.paintHeight) - 2.0f);
            canvas.drawRoundRect(rect, CORNER_RADIUS, CORNER_RADIUS, this.paint);
            canvas.drawRoundRect(rect, CORNER_RADIUS, CORNER_RADIUS, this.strokePaint);
        }
    }

    /** 循环把评论作为弹幕发送出去。 */
    private synchronized void loopDisplayBarrage() {
        if (CollectionUtil.isEmpty(this.momentComments)) {
            LogUtil.d(TAG, "generateSomeDanmaku: 评论为空 ");
            return;
        }
        if (this.executorService == null) {
            this.executorService = Executors.newSingleThreadExecutor();
        }
        this.executorService.execute(getAddBarrageRunnable(this.selfWatchId));
    }

    private Runnable getAddBarrageRunnable(final String selfWatchId) {
        return new Runnable() {
            @Override
            public void run() {
                if (BarrageFragment.this.executorService.isShutdown()) {
                    LogUtil.w(TAG, "getAddBarrageRunnable run: shutdown ! !");
                    return;
                }
                int size = BarrageFragment.this.momentComments.size();
                if (BarrageFragment.this.videoLength < VIDEO_MAX_LENGTH) {
                    size = BARRAGE_MIN_SIZE;
                } else if (BarrageFragment.this.videoLength > VIDEO_MAX_LENGTH) {
                    size = BARRAGE_MAX_SIZE;
                }
                if (BarrageFragment.this.momentComments.size() < size) {
                    size = BarrageFragment.this.momentComments.size();
                }
                Context context = BarrageFragment.this.getContext();
                if (!Utils.isContextEffective(context)) {
                    LogUtil.d(TAG, "context 无效");
                    return;
                }
                LogUtil.e(TAG, "getAddBarrageRunnable run: size = " + size
                        + " momentComments.size() = " + BarrageFragment.this.momentComments.size());
                for (int i = 0; i < size; i++) {
                    int sleepTime = new Random().nextInt(RANDOM_INT);
                    if (!BarrageFragment.this.showDanmu) {
                        return;
                    }
                    DbMomentComment comment = BarrageFragment.this.momentComments.get(i);
                    if (!comment.isCommentFlag()) {
                        BarrageFragment.this.addDanmaku(comment.getWatchName(), false, false, comment.getGiftType());
                    } else {
                        if (!TextUtils.isEmpty(comment.getWatchId()) && selfWatchId.equals(comment.getWatchId())) {
                            comment.setWatchName(context.getString(R.string.me));
                        }
                        if (!TextUtils.isEmpty(comment.getReplyId()) && selfWatchId.equals(comment.getReplyId())) {
                            comment.setReplyName(context.getString(R.string.me));
                        }
                        String replyName = comment.getReplyName();
                        String replyText = TextUtils.isEmpty(replyName)
                                ? "" : context.getString(R.string.reply) + replyName;
                        LogUtil.d(TAG, "run: " + comment.getWatchId() + " - " + selfWatchId);
                        String content = comment.getComment();
                        if (!TextUtils.isEmpty(content) && content.length() > 10) {
                            content = content.substring(0, 10) + "...";
                        }
                        boolean isSelfComment = !TextUtils.isEmpty(selfWatchId)
                                && selfWatchId.equals(comment.getWatchId());
                        BarrageFragment.this.addDanmaku(
                                comment.getWatchName() + replyText + ":" + content, false, isSelfComment, 0);
                    }
                    try {
                        Thread.sleep(sleepTime);
                        if (i == BarrageFragment.this.momentComments.size()) {
                            Thread.sleep(sleepTime * 2);
                        }
                    } catch (InterruptedException e) {
                        LogUtil.e(TAG, "getAddBarrageRunnable error: ", e);
                    }
                }
            }
        };
    }
    private void addDanmaku(String content, boolean border, boolean isSelfComment, int giftType) {
        LogUtil.d(TAG, "addDanmaku run: border= " + border + " isMeComment= " + isSelfComment
                + "  content=" + content + "  giftType=" + giftType);
        if (getActivity() == null || getActivity().isFinishing() || getActivity().isDestroyed()
                || this.danmakuView == null) {
            return;
        }
        BaseDanmaku danmaku = this.danmakuContext.mDanmakuFactory.createDanmaku(
                BaseDanmaku.TYPE_SCROLL_RL,
                this.danmakuContext.getDisplayer().getWidth(),
                this.danmakuContext.getDisplayer().getHeight(), 1.5f, 0.9f);
        if (giftType != 0) {
            danmaku.text = createSpannable(getActivity(), "     " + content, giftType);
        } else {
            danmaku.text = "     " + content;
        }
        danmaku.padding = DimenUtil.px2dp(getActivity(), 20.0f);
        danmaku.textSize = DimenUtil.px2sp(getActivity(), 35.0f);
        danmaku.textColor = -1;
        danmaku.tag = isSelfComment;
        danmaku.setTime(this.danmakuView.getCurrentTime());
        this.danmakuView.addDanmaku(danmaku);
        this.danmakuView.show();
    }

    /** 礼物弹幕文本前拼接礼物小图标。 */
    public static SpannableStringBuilder createSpannable(Context context, String content, int giftType) {
        try {
            if (content.contains("\n")) {
                content = content.replaceAll("\n", " ");
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "createSpannable error: ", e);
        }
        SpannableStringBuilder builder = new SpannableStringBuilder(content);
        Drawable drawable = context.getResources().getDrawable(
                giftType == 1 ? R.drawable.ic_props_flower_small : R.drawable.ic_props_egg_small);
        drawable.setBounds(0, 0, DimenUtil.dp2px(context, 15.0f), DimenUtil.dp2px(context, 15.0f));
        ImageSpan imageSpan = new ImageSpan(drawable);
        builder.append(":  ");
        builder.setSpan(imageSpan, builder.length() - 1, builder.length(), 17);
        return builder;
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.cb_hide_barrage || viewId == R.id.ll_hide_danmu) {
            if (SystemUtil.isFastDoubleClick()) {
                LogUtil.d(TAG, "onClick: click too fast");
            } else if (this.danmakuView.isShown() && this.showDanmu) {
                LogUtil.d(TAG, "onClick: hide DanmuView");
                hideBarrage();
            } else {
                LogUtil.d(TAG, "onClick: show DanmuView");
                showBarrage();
            }
            return;
        }
        if (viewId == R.id.fl_take_same_parent) {
            if (SystemUtil.isFastDoubleClick()) {
                LogUtil.d(TAG, "onClick: click too fast");
                return;
            }
            HandlerUtil.runOnBackground(new Runnable() {
                @Override
                public void run() {
                    BarrageFragment.this.takeSameParent();
                }
            });
            return;
        }
        if (viewId == R.id.iv_arrow) {
            if (this.bottomViewIsVisible) {
                hideBottomView();
            } else {
                showBottomView();
                if (getContext() != null) {
                    MomentBehavior.clickInteractButton(getContext());
                }
            }
            return;
        }
        if (viewId == R.id.iv_send_egg || viewId == R.id.rl_send_egg_parent) {
            if (SystemUtil.isFastDoubleClick()) {
                LogUtil.d(TAG, "onClick: click too fast");
                return;
            }
            if (this.dbMoment != null && this.selfWatchId.equals(this.dbMoment.getWatchId())
                    && getActivity() != null) {
                Intent intent = new Intent(getActivity(), GiftDetailsActivity.class);
                intent.putExtra(GiftDetailsActivity.STRING_TITLE, getString(R.string.string_eggs_received));
                intent.putExtra(GiftDetailsActivity.GIFT_TYPE, GiftDetailsActivity.GIFT_TYPE_EGG);
                intent.putExtra(GiftDetailsActivity.GIFT_MOMENT, JSONUtil.toJSON(this.dbMoment));
                getActivity().startActivity(intent);
            } else if (this.response != null) {
                startVibrator();
                this.presenter.sendGift(this.dbMoment, 2, this.response.isSendegg());
            }
            return;
        }
        if (viewId == R.id.iv_send_flower || viewId == R.id.rl_send_flower_parent) {
            if (SystemUtil.isFastDoubleClick()) {
                LogUtil.d(TAG, "onClick: click too fast");
                return;
            }
            LogUtil.d(TAG, "onClick: " + this.dbMoment);
            LogUtil.d(TAG, "onClick: " + this.selfWatchId);
            if (this.dbMoment != null && this.selfWatchId.equals(this.dbMoment.getWatchId())
                    && getActivity() != null) {
                Intent intent = new Intent(getActivity(), GiftDetailsActivity.class);
                intent.putExtra(GiftDetailsActivity.STRING_TITLE, getString(R.string.string_flowes_received));
                intent.putExtra(GiftDetailsActivity.GIFT_TYPE, GiftDetailsActivity.GIFT_TYPE_FLOWER);
                intent.putExtra(GiftDetailsActivity.GIFT_MOMENT, JSONUtil.toJSON(this.dbMoment));
                getActivity().startActivity(intent);
            } else if (this.response != null) {
                startVibrator();
                this.presenter.sendGift(this.dbMoment, 1, this.response.isSendflower());
            }
            return;
        }
        if (viewId == R.id.ll_bottom_contentView) {
            hideBottomView();
        }
    }

    /** 点击拍同款：跳到同款短视频拍摄页。 */
    private void takeSameParent() {
        if (!MomentTypeUtil.isShareVideo(this.dbMoment.getType().intValue())) {
            return;
        }
        ShareVideoMoment shareVideoMoment = JSONUtil.fromJSON(this.dbMoment.getContent(), ShareVideoMoment.class);
        LogUtil.d(TAG, "onClick: " + shareVideoMoment);
        if (shareVideoMoment == null || shareVideoMoment.getFunVideoParam() == null || getActivity() == null) {
            return;
        }
        ShareVideoMoment.FunVideoParam funVideoParam = shareVideoMoment.getFunVideoParam();
        int modelType = funVideoParam.getModelType();
        IntentUtils.startFunVideoActivity(getActivity(), funVideoParam);
        getActivity().finish();
        MomentBehavior.clickTakeSameButton(getActivity(), modelType, this.dbMoment.getWatchId());
    }

    private void startVibrator() {
        Vibrator localVibrator = this.vibrator;
        if (localVibrator == null) {
            return;
        }
        localVibrator.vibrate(VIBRATOR_TIME);
    }

    private void showBarrage() {
        if (getActivity() == null) {
            return;
        }
        this.ivBarrage.setBackground(ContextCompat.getDrawable(getActivity(),
                R.drawable.ic_props_barrage_turn_on));
        this.danmakuView.show();
        this.danmakuView.seekTo(0L);
        this.showDanmu = true;
        MomentBehavior.clickBarrageButton(getActivity(), 1);
    }

    private void hideBarrage() {
        if (getActivity() == null) {
            return;
        }
        this.ivBarrage.setBackground(ContextCompat.getDrawable(getActivity(),
                R.drawable.ic_props_barrage_shut_down));
        this.danmakuView.hide();
        this.showDanmu = false;
        MomentBehavior.clickBarrageButton(getActivity(), 2);
    }
    @Override
    public void sendGiftSuccess(int giftId, boolean success) {
        Context context = getContext();
        if (!Utils.isContextEffective(context)) {
            LogUtil.d(TAG, "sendGiftSuccess : context 无效");
            return;
        }
        if (giftId == 2) {
            SearchGiftResponse giftResponse = this.response;
            if (giftResponse != null && success) {
                giftResponse.setSendegg(false);
                if (this.selfWatchId.equals(this.dbMoment.getWatchId())) {
                    this.tvEggCount.setVisibility(View.VISIBLE);
                    setEggTextView(giftResponse.getEgg() + 1);
                }
                addDanmaku(this.selfName, false, false, giftId);
                MomentBehavior.sendGiftRecord(context, 2, this.dbMoment.getWatchId());
            }
            playAnimation(MomentFileUtils.getAnimPathByCate(context, Constants.FileName.SEND_EGG),
                    Constants.Suffix.SUFFIX_VIDEO_ANIM);
            return;
        }
        if (giftId == 1) {
            SearchGiftResponse giftResponse = this.response;
            if (giftResponse != null && success) {
                giftResponse.setSendflower(false);
                if (this.selfWatchId.equals(this.dbMoment.getWatchId())) {
                    this.tvFlowerCount.setVisibility(View.VISIBLE);
                    setFlowerTextView(giftResponse.getFlower() + 1);
                }
                addDanmaku(this.selfName, false, false, giftId);
                MomentBehavior.sendGiftRecord(context, 1, this.dbMoment.getWatchId());
            }
            playAnimation(MomentFileUtils.getAnimPathByCate(context, Constants.FileName.SEND_FLOWER),
                    Constants.Suffix.SUFFIX_VIDEO_ANIM);
        }
    }

    private void setFlowerTextView(int count) {
        if (count > MAX_GIFT_COUNT) {
            this.tvFlowerCount.setText(getString(R.string.string_max_count));
            return;
        }
        Context context = getContext();
        if (!Utils.isContextEffective(context)) {
            LogUtil.d(TAG, "setFlowerTextView ：context 无效");
            return;
        }
        this.tvFlowerCount.setWidth(DimenUtil.dp2px(context, this.dp_18));
        this.tvFlowerCount.setHeight(DimenUtil.dp2px(context, this.dp_18));
        this.tvFlowerCount.setPadding(this.dp_0, this.dp_0, this.dp_0, this.dp_0);
        this.tvFlowerCount.setText(String.valueOf(count));
    }

    private void setEggTextView(int count) {
        if (count > MAX_GIFT_COUNT) {
            this.tvEggCount.setText(getString(R.string.string_max_count));
            return;
        }
        Context context = getContext();
        if (!Utils.isContextEffective(context)) {
            LogUtil.d(TAG, "setEggTextView ：context 无效");
            return;
        }
        this.tvEggCount.setWidth(DimenUtil.dp2px(context, this.dp_18));
        this.tvEggCount.setHeight(DimenUtil.dp2px(context, this.dp_18));
        this.tvEggCount.setPadding(this.dp_0, this.dp_0, this.dp_0, this.dp_0);
        this.tvEggCount.setText(String.valueOf(count));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void sendGiftFail() {
        if (Utils.isContextEffective(getContext())) {
            ToastUtil.showShortCover(getContext(), getString(R.string.string_send_more));
        }
    }

    @Override
    public void getBarrageDataSuccess(List<DbMomentComment> comments, SearchGiftResponse giftResponse) {
        Utils.logSize(TAG, "getBarrageDataSuccess: list ", comments);
        Utils.logSize(TAG, "getBarrageDataSuccess: momentComments", this.momentComments);
        this.response = giftResponse;
        if (this.selfWatchId.equals(this.dbMoment.getWatchId())) {
            this.tvEggCount.setVisibility(View.VISIBLE);
            this.tvFlowerCount.setVisibility(View.VISIBLE);
            setEggTextView(giftResponse.getEgg());
            setFlowerTextView(giftResponse.getFlower());
        }
        if (CollectionUtil.isEmpty(comments)) {
            return;
        }
        if (CollectionUtil.isEmpty(this.momentComments) || comments.size() > this.momentComments.size()) {
            this.momentComments = new ArrayList<>();
            Utils.logSize(TAG, "getBarrageDataSuccess: b = "
                    + this.momentComments.addAll(comments) + ", ", this.momentComments);
        }
        setBarrage();
    }

    private void setBarrage() {
        if (!this.showDanmu) {
            HandlerUtil.runOnUIThreadDelay(new Runnable() {
                @Override
                public void run() {
                    BarrageFragment.this.loopDisplayBarrage();
                }
            }, 150L);
        } else {
            loopDisplayBarrage();
        }
    }

    @Override
    public void getBarrageDataFail(List<DbMomentComment> comments) {
        Utils.logSize(TAG, "getBarrageDataFail: list ", comments);
        if (CollectionUtil.isEmpty(comments)) {
            return;
        }
        if (CollectionUtil.isEmpty(this.momentComments)) {
            this.momentComments = new ArrayList<>();
            Utils.logSize(TAG, "getBarrageDataFail: b = "
                    + this.momentComments.addAll(comments) + ", ", this.momentComments);
        }
        setBarrage();
    }

    @Override
    public BarragePresenter createPresenter() {
        return new BarragePresenter(getActivity());
    }

    @Override
    public void onStop() {
        super.onStop();
        if (this.ivBarrage == null || this.danmakuView == null) {
            return;
        }
        hideBarrage();
    }

    @Override
    public void onResume() {
        super.onResume();
        ChatVideoUtil.keepScreenOn(getActivity().getWindow());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        this.showDanmu = false;
        ExecutorService executor = this.executorService;
        if (executor != null) {
            executor.shutdownNow();
        }
        if (this.danmakuView != null) {
            HandlerUtil.runOnBackground(new Runnable() {
                @Override
                public void run() {
                    LogUtil.d(TAG, "run: release start");
                    BarrageFragment.this.danmakuView.release();
                    LogUtil.d(TAG, "run: release end");
                    BarrageFragment.this.danmakuView = null;
                }
            });
        }
        VideoAnimView videoAnimView = this.videoAnimation;
        if (videoAnimView != null) {
            videoAnimView.releasePlayerController();
        }
    }
}