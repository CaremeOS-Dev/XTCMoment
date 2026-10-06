package com.xtc.moment.module.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.module.like.likerule.LikeRuleShowActivity;
import com.xtc.moment.module.personalinfo.bigdata.PersonalCenterBehavior;
import com.xtc.moment.module.personalinfo.net.PersonInfoHttpProxy;
import com.xtc.moment.module.personalinfo.net.bean.LikeRequest;
import com.xtc.moment.serve.AccountInfoServerImpl;
import com.xtc.moment.util.SystemUtil;
import com.xtc.ui.widget.util.PressAnimHelper;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 个人主页点赞按钮，支持连点累计上报。
 */
public class PraiseLinearLayout extends LinearLayout implements View.OnClickListener {

    private static final String TAG = "PraiseLinearLayout";
    private static final int LIKE_FRIEND_CODE = 1;
    private static final long DELAY_TIME = 800;

    private String mFriendWatchId;
    private int mLikeLimit = 5;
    private int mTotalLikes = 0;
    private int mRestLikes = 0;
    private int mNeedReportCount = 0;
    private int likesLimitNumber;
    private String fuzzyLikes;
    private boolean isRequesting = false;
    private TextView mTvPraiseCount;
    private ImageView mIvPraise;
    private LikeRequest mLikeRequest;
    private PersonInfoHttpProxy mPersonInfoHttpProxy;
    private OnPraiseClickListener mOnPraiseClickListener;
    private final PressAnimHelper pressAnimHelper;
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            likeFriend();
        }
    };

    public interface OnPraiseClickListener {
        void onPraiseClick();
    }

    public PraiseLinearLayout(Context context) {
        super(context);
        this.pressAnimHelper = new PressAnimHelper(this, true, true);
        initView();
    }

    public PraiseLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.pressAnimHelper = new PressAnimHelper(this, true, true);
        initView();
    }

    public PraiseLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.pressAnimHelper = new PressAnimHelper(this, true, true);
        initView();
    }

    public void initView() {
        setOrientation(VERTICAL);
        this.pressAnimHelper.setSmallAlpha(0.7f);
        this.pressAnimHelper.setSmallScale(0.85f);
        View content = LayoutInflater.from(getContext()).inflate(R.layout.view_praise, this);
        this.mTvPraiseCount = (TextView) content.findViewById(R.id.tv_praise_count);
        this.mIvPraise = (ImageView) content.findViewById(R.id.iv_praise);
        this.mTvPraiseCount.setText(String.valueOf(0));
        setOnClickListener(this);
        initData();
    }

    private void initData() {
        this.mPersonInfoHttpProxy = new PersonInfoHttpProxy(getContext());
    }

    @Deprecated
    @Override
    public void setOnClickListener(View.OnClickListener listener) {
        super.setOnClickListener(listener);
    }

    public void setData(String friendWatchId, int totalLikes, int likeLimit, int restLikes, String fuzzyLikes,
            int likesLimitNumber) {
        LogUtil.d(TAG, "setData: friendWatchId = " + friendWatchId + ", totalLikes = " + totalLikes + ", likeLimit = "
                + likeLimit + ", restLikes = " + restLikes + ", fuzzyLikes = " + fuzzyLikes
                + ", likesLimitNumber = " + likesLimitNumber);
        this.mFriendWatchId = friendWatchId;
        this.mLikeLimit = likeLimit;
        this.mTotalLikes = totalLikes;
        this.mRestLikes = restLikes;
        this.fuzzyLikes = fuzzyLikes;
        this.likesLimitNumber = likesLimitNumber;
        this.mIvPraise.setSelected(this.mLikeLimit > this.mRestLikes);
        this.mTvPraiseCount.setText(getLikesNumberString(this.mTotalLikes));
    }

    @Override
    public void onClick(View view) {
        int restLikes = this.mRestLikes;
        if (restLikes <= 0) {
            if (SystemUtil.isFastDoubleClick()) {
                return;
            }
            LikeRuleShowActivity.start(getContext());
            LogUtil.d(TAG, "点赞达到极限");
            return;
        }
        this.mRestLikes = restLikes - 1;
        this.mNeedReportCount++;
        if (!this.mIvPraise.isSelected()) {
            this.mIvPraise.setSelected(true);
        }
        this.mTvPraiseCount.setText(getLikesNumberString(this.mTotalLikes + this.mNeedReportCount));
        this.mHandler.removeMessages(LIKE_FRIEND_CODE);
        this.mHandler.sendEmptyMessageDelayed(LIKE_FRIEND_CODE, DELAY_TIME);
        OnPraiseClickListener listener = this.mOnPraiseClickListener;
        if (listener != null) {
            listener.onPraiseClick();
        }
        if (getTag() == null) {
            PersonalCenterBehavior.likeFriendWay(2);
            setTag("");
        }
    }

    private String getLikesNumberString(long likes) {
        if (likes >= this.likesLimitNumber && !TextUtils.isEmpty(this.fuzzyLikes)) {
            return this.fuzzyLikes;
        }
        return String.valueOf(likes);
    }

    private void likeFriend() {
        if (this.isRequesting || isActivated()) {
            return;
        }
        this.isRequesting = true;
        if (this.mLikeRequest == null) {
            this.mLikeRequest = new LikeRequest();
            this.mLikeRequest.setWatchId(AccountInfoServerImpl.getInstance(getContext())
                    .getWatchAccountInfo().getWatchId(getContext()));
            this.mLikeRequest.setLikeWatchId(this.mFriendWatchId);
        }
        this.mLikeRequest.setCount(this.mNeedReportCount);
        this.mPersonInfoHttpProxy.like(this.mLikeRequest).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
                    @Override
                    public void call(String result) {
                        PraiseLinearLayout.this.isRequesting = false;
                        int count = PraiseLinearLayout.this.mLikeRequest.getCount();
                        PraiseLinearLayout.this.mTotalLikes += count;
                        PraiseLinearLayout.this.mNeedReportCount -= count;
                        PraiseLinearLayout.this.mLikeRequest.setCount(0);
                        PersonalCenterBehavior.likeFriendCount(count);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        PraiseLinearLayout.this.isRequesting = false;
                        PraiseLinearLayout.this.mLikeRequest.setCount(0);
                        LogUtil.e(TAG, "likeFriend#error: ", throwable);
                        LogUtil.d(TAG, "likeFriend#msg: " + throwable.getMessage());
                    }
                });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            this.pressAnimHelper.press();
        } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            this.pressAnimHelper.release();
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mHandler.removeCallbacksAndMessages(null);
    }

    public void setOnPraiseClickListener(OnPraiseClickListener listener) {
        this.mOnPraiseClickListener = listener;
    }
}