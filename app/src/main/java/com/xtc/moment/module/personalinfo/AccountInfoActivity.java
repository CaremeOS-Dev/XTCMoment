package com.xtc.moment.module.personalinfo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.badlogic.gdx.backends.android.AndroidFragmentApplication;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.opensource.svgaplayer.SVGAImageView;
import com.xtc.contactapi.contact.bean.ContactBean;
import com.xtc.contactapi.contact.manager.ContactManager;
import com.xtc.contactapi.contacthead.config.ContactHeadManagerConfig;
import com.xtc.contactapi.contacthead.impl.ContactHeadManager;
import com.xtc.contactapi.contacthead.interfaces.IShowHeadToViewStrategy;
import com.xtc.funlist.util.FuncUtil;
import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.constants.ModuleSwitchConstant;
import com.xtc.moment.module.Constants;
import com.xtc.moment.module.personalinfo.bigdata.PersonalCenterBehavior;
import com.xtc.moment.module.personalinfo.constant.PersonalInfoConstant;
import com.xtc.moment.module.personalinfo.manager.DressHelper;
import com.xtc.moment.module.personalinfo.manager.DressManage;
import com.xtc.moment.module.personalinfo.net.bean.BadgeBean;
import com.xtc.moment.module.personalinfo.net.bean.PersonalInfoAndLikeRuleResponse;
import com.xtc.moment.module.personalinfo.net.bean.PersonalInfoResponse;
import com.xtc.moment.module.personalinfo.util.BirthdayUtil;
import com.xtc.moment.module.personalinfo.widget.GeniusNumberIntroduceDialog;
import com.xtc.moment.module.personalinfo.widget.ItemScalableWithBigCorner;
import com.xtc.moment.module.personalinfo.widget.LoadingViewHolder;
import com.xtc.moment.module.personalinfo.widget.PersonalBadgeItem;
import com.xtc.moment.module.prerogative.bean.PersonalState;
import com.xtc.moment.module.widget.BaseMomentActivity;
import com.xtc.moment.module.widget.ContentScrollView;
import com.xtc.moment.module.widget.FriendFrameLayout;
import com.xtc.moment.module.widget.PraiseLinearLayout;
import com.xtc.moment.module.widget.ScrollLayout;
import com.xtc.moment.module.widget.like.KsgLikeView;
import com.xtc.moment.serve.AccountInfoServerImpl;
import com.xtc.moment.share.view.ShowH5Activity;
import com.xtc.moment.util.BroadcastReceiverUtil;
import com.xtc.moment.util.ClickUtils;
import com.xtc.moment.util.DressUtil;
import com.xtc.moment.util.SystemUtil;
import com.xtc.moment.util.ToastUtil;
import com.xtc.moment.util.switchs.ModuleSwitchUtil;
import com.xtc.system.account.constant.NotificationFlag;
import com.xtc.ui.widget.button.LongSolidButton;
import com.xtc.ui.widget.dialog.DoubleFlatBtnDialog;
import com.xtc.ui.widget.dialog.DoubleFlatBtnWithTitleDialog;
import com.xtc.ui.widget.dialog.bean.noIcon.DoubleFlatBtnBean;
import com.xtc.ui.widget.dialog.bean.noIcon.DoubleFlatBtnWithTitleBean;
import com.xtc.ui.widget.item.clickScale.LeftIconItemWithExtraInfo;
import com.xtc.ui.widget.util.DialogUtil;
import com.xtc.utils.ui.DimenUtil;
import com.xtc.virtualselfapi.interfaces.ViewLoadCallBack;
import com.xtc.virtualselfapi.module.friend.FriendVirtualSelfFragment;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import master.flame.danmaku.danmaku.model.android.DanmakuFactory;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;

/**
 * 好友个人中心页面。
 *
 * <p>展示好友头像/昵称/性别/生日星座/天才号/等级/勋章/状态与签名，支持点赞、发消息、删除好友，
 * 并在支持好友天才秀时下拉展开虚拟形象。
 */
public class AccountInfoActivity extends BaseMomentActivity<IAccountInfoView, AccountPresenter>
        implements AndroidFragmentApplication.Callbacks, IShowHeadToViewStrategy, IAccountInfoView,
        DressHelper.OnLoadDataListener {

    private static final String TAG = "AccountInfoActivity";

    /** 蓝色主按钮的渐变色资源。 */
    public static final int[] BLUE_BUTTON = {R.color.color_end_blue, R.color.color_start_blue};
    /** 灰色次按钮的渐变色资源。 */
    public static final int[] GRAY_BUTTON = {R.color.color_end_gray, R.color.color_start_gray};

    private ContactManager contactManager;
    private PersonalInfoResponse curInfo;
    private DoubleFlatBtnDialog deleteConfirmDlg;
    private String geniusNumber;
    private GeniusNumberIntroduceDialog geniusNumberIntroduceDialog;
    private boolean isFragmentShow;
    private boolean isNeedShowVirtualSelf;
    private boolean isSelf;
    private PersonalBadgeItem itemBadge;
    private ItemScalableWithBigCorner itemGeniusNumber;
    private ImageView ivConstellation;
    private ImageView ivGender;
    private ImageView ivGenderBackground;
    private ImageView ivHead;
    private ImageView ivMyState;
    private ImageView ivPraise;
    private SVGAImageView ivSvgaAvatarDress;
    private LeftIconItemWithExtraInfo levelItem;
    private LinearLayout llBirthdayAndConstellation;
    private LinearLayout llMyState;
    private LoadingViewHolder loadingHolder;
    private View mBottomShadowView;
    private ContentScrollView mContentScrollView;
    DoubleFlatBtnWithTitleDialog mDialog;
    private FrameLayout mFlContent;
    private FriendFrameLayout mFlVirtualSelf;
    private FriendVirtualSelfFragment mFriendVirtualSelfFragment;
    private KsgLikeView mKsgLikeView;
    private PraiseLinearLayout mPraiseLinearLayout;
    private Runnable mSayHiRunnable;
    private ScrollLayout mScrollLayout;
    private String mSignature;
    private AnimatorSet mSignatureAnimatorSet;
    private TextView mTvSignatureBubbling;
    private ContactHeadManager manager;
    private String nickName;
    private LeftIconItemWithExtraInfo personalizedSignatureItem;
    private MyStateRefreshReceiver receiver;
    private TextView tvBirthday;
    private TextView tvConstellation;
    private TextView tvMyState;
    private TextView tvNickName;
    private TextView tvNoBirthday;
    private String watchId;

    /** 签名气泡的三种背景，随机选取。 */
    private final int[] mToastBackground = {
            R.drawable.shape_friend_signature_bubble_bg_1,
            R.drawable.shape_friend_signature_bubble_bg_2,
            R.drawable.shape_friend_signature_bubble_bg_3};
    /** 下拉展开动画时长，0 表示当前不需要播放签名动画。 */
    private int mSignatureAnimDuration = 0;

    /** 装扮服务连接回调，连接成功后加载我的头像装扮。 */
    private final DressManage.DressServiceListener mDressServiceListener = new DressManage.DressServiceListener() {
        @Override
        public void onServiceDisconnected() {
        }

        @Override
        public void onServiceConnected() {
            LogUtil.i(TAG, "装扮服务连接成功，设置我的头像装扮");
            ((AccountPresenter) AccountInfoActivity.this.presenter).loadDress(AccountInfoActivity.this);
        }
    };

    /** 下拉抽屉状态变化监听。 */
    private final ScrollLayout.OnScrollChangedListener mOnScrollChangedListener = new ScrollLayout.OnScrollChangedListener() {
        @Override
        public void onScrollProgressChanged(float progress) {
        }

        @Override
        public void onScrollFinished(ScrollLayout.Status status) {
            LogUtil.d(TAG, "onScrollFinished: currentStatus = " + status);
            if (ScrollLayout.Status.CLOSED.equals(status)) {
                AccountInfoActivity.this.mContentScrollView.setScrollEnable(true);
                return;
            }
            AccountInfoActivity.this.showSignatureView();
            AccountInfoActivity.this.mContentScrollView.setScrollEnable(false);
            if (AccountInfoActivity.this.mContentScrollView.getTag() == null) {
                AccountInfoActivity.this.mContentScrollView.setTag("");
            } else {
                PersonalCenterBehavior.pullDownVirtualself();
            }
        }

        @Override
        public void onChildScroll(int top) {
            LogUtil.d(TAG, "onChildScroll: top = " + top);
        }
    };

    @Override
    public void exit() {
    }

    public static void start(Context context, String watchId, String nickName, boolean isSelf) {
        Intent intent = new Intent(context, AccountInfoActivity.class);
        intent.putExtra(Constants.INTENT_EXTRA_WATCH_ID, watchId);
        intent.putExtra(Constants.INTENT_EXTRA_NAME, nickName);
        intent.putExtra(Constants.INTENT_EXTRA_IS_SELF, isSelf);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_info);
        this.contactManager = ContactManager.getInstance(this);
        this.manager = new ContactHeadManagerConfig.Builder()
                .showHeadToViewStrategy(this).context(this).build();
        Intent intent = getIntent();
        this.watchId = intent.getStringExtra(Constants.INTENT_EXTRA_WATCH_ID);
        this.nickName = intent.getStringExtra(Constants.INTENT_EXTRA_NAME);
        this.isSelf = intent.getBooleanExtra(Constants.INTENT_EXTRA_IS_SELF, false);
        if (TextUtils.isEmpty(this.watchId)) {
            finish();
        } else {
            initView();
            initData();
        }
    }

    @Override
    public AccountPresenter createPresenter() {
        return new AccountPresenter(getApplicationContext(),
                AccountInfoServerImpl.getInstance(getApplicationContext()).getWatchAccountInfo()
                        .getWatchId(getApplicationContext()));
    }
    public void initView() {
        this.mFlContent = (FrameLayout) findViewById(R.id.fl_content);
        this.mFlVirtualSelf = (FriendFrameLayout) findViewById(R.id.fl_virtual_self);
        this.loadingHolder = new LoadingViewHolder(this);
        this.ivGenderBackground = (ImageView) findViewById(R.id.iv_gender_background);
        this.ivGender = (ImageView) findViewById(R.id.iv_gender);
        this.ivPraise = (ImageView) findViewById(R.id.iv_praise);
        this.ivConstellation = (ImageView) findViewById(R.id.iv_constellation);
        this.ivHead = (ImageView) findViewById(R.id.iv_info_head);
        this.tvNickName = (TextView) findViewById(R.id.tv_info_nick_name);
        TextView tvRealName = (TextView) findViewById(R.id.tv_info_real_name);
        this.mTvSignatureBubbling = (TextView) findViewById(R.id.tv_signature_bubbling);
        this.tvNoBirthday = (TextView) findViewById(R.id.tv_no_birthday);
        this.tvBirthday = (TextView) findViewById(R.id.tv_birthday);
        this.tvConstellation = (TextView) findViewById(R.id.tv_constellation);
        this.tvMyState = (TextView) findViewById(R.id.my_state_tv);
        this.ivMyState = (ImageView) findViewById(R.id.my_state_iv);
        this.llMyState = (LinearLayout) findViewById(R.id.ll_my_State);
        this.llBirthdayAndConstellation = (LinearLayout) findViewById(R.id.ll_birthday_and_constellation);
        this.ivSvgaAvatarDress = (SVGAImageView) findViewById(R.id.iv_personal_head_dress);
        this.itemGeniusNumber = (ItemScalableWithBigCorner) findViewById(R.id.item_genius_number);
        this.itemGeniusNumber.getTextView().setText(getString(R.string.genius_number, new Object[]{getString(R.string.not_set)}));
        this.itemGeniusNumber.setOnClickListener(new ItemScalableWithBigCorner.OnClickListener() {
            @Override
            public void onClick() {
                if (ClickUtils.isFastClick()) {
                    AccountInfoActivity.this.showGeniusNumIntroduceDialog();
                } else {
                    LogUtil.w(TAG, "itemGeniusNumber click too fast");
                }
            }
        });
        initBadge();
        initScrollLayout();
        ContactBean contactBean = this.contactManager.getContactWithoutShortNumberByWatchIdSync(this.watchId);
        String realName;
        if (contactBean != null) {
            this.manager.setContactPortrait(this, contactBean, this.ivHead,
                    getResources().getDimensionPixelSize(R.dimen.dp_44),
                    getResources().getDimensionPixelSize(R.dimen.dp_44));
            this.nickName = contactBean.getName();
            realName = contactBean.getRealName();
            initFriendVirtualSelf(contactBean.getOpenId());
        } else {
            Glide.with((FragmentActivity) this).load(this.contactManager.getDefaultPortraitPath(getApplicationContext()))
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE)
                            .error(R.drawable.default_custom_default).placeholder(R.drawable.default_custom_default))
                    .into(this.ivHead);
            this.mScrollLayout.setEnable(false);
            realName = null;
        }
        this.tvNickName.setText(TextUtils.isEmpty(this.nickName) ? getString(R.string.unknown_watch) : this.nickName);
        if (ModuleSwitchUtil.queryModuleSwitchByBoolean(this, ModuleSwitchConstant.MODULE_SWITCH_REAL_NAME, false)) {
            tvRealName.setVisibility(View.VISIBLE);
            if (TextUtils.isEmpty(realName)) {
                realName = getString(R.string.not_set);
            }
            tvRealName.setText(getString(R.string.info_real_name, new Object[]{realName}));
        } else {
            tvRealName.setVisibility(View.GONE);
        }
        initPersonalizedSignatureItem();
        initLevelItem();
        initGoToChatButton();
        initDeleteFriendButton();
        initPraiseView();
        initLikeView();
    }

    private void initBadge() {
        if (((AccountPresenter) this.presenter).isSupportBadge()) {
            this.itemBadge = (PersonalBadgeItem) findViewById(R.id.item_badge);
            this.itemBadge.setClickListener(new PersonalBadgeItem.OnClickListener() {
                @Override
                public void onClick() {
                    if (ClickUtils.isFastLongClick()) {
                        LogUtil.w(TAG, "勋章墙点击过快");
                        return;
                    }
                    HashMap<String, String> params = new HashMap<>();
                    params.put("fId", AccountInfoActivity.this.watchId);
                    ShowH5Activity.start(AccountInfoActivity.this, 160, params);
                    PersonalCenterBehavior.viewFriendBadge();
                }
            });
            this.itemBadge.setVisibility(View.VISIBLE);
        }
    }

    private void initLikeView() {
        this.mKsgLikeView = (KsgLikeView) findViewById(R.id.ksgLikeView);
        this.mKsgLikeView.addLikeImages(Integer.valueOf(R.drawable.ic_like_1),
                Integer.valueOf(R.drawable.ic_like_2), Integer.valueOf(R.drawable.ic_like_3));
    }

    /** 初始化好友天才秀，加载成功后允许下拉展开。 */
    private void initFriendVirtualSelf(String openId) {
        if (!FuncUtil.supportFriendVirtualSelf()) {
            LogUtil.d(TAG, "initFriendVirtualSelf : 不支持好友的天才秀显示");
            this.mScrollLayout.setEnable(false);
            return;
        }
        LogUtil.d(TAG, "initFriendVirtualSelf : openId = " + openId);
        if (TextUtils.isEmpty(openId)) {
            LogUtil.d(TAG, "initFriendVirtualSelf : openId is null ");
            this.mScrollLayout.setEnable(false);
            return;
        }
        if (this.mFriendVirtualSelfFragment != null) {
            return;
        }
        this.mFriendVirtualSelfFragment = FriendVirtualSelfFragment.newInstance(openId);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fl_virtual_self, this.mFriendVirtualSelfFragment);
        transaction.commitAllowingStateLoss();
        this.mFriendVirtualSelfFragment.setViewLoadCallBack(new ViewLoadCallBack() {
            @Override
            public void onLoadSuccess() {
                LogUtil.d(TAG, "friendVirtualSelf onLoadSuccess");
                AccountInfoActivity.this.isNeedShowVirtualSelf = true;
                AccountInfoActivity.this.mScrollLayout.setEnable(true);
                AccountInfoActivity.this.showVirtualSelfView();
            }

            @Override
            public void onLoadFail() {
                AccountInfoActivity.this.mScrollLayout.setEnable(false);
                AccountInfoActivity.this.isNeedShowVirtualSelf = false;
                LogUtil.d(TAG, "friendVirtualSelf onLoadFail");
            }
        });
    }
    private void showVirtualSelfView() {
        LogUtil.d(TAG, "showVirtualSelfView:  isFragmentShow = " + this.isFragmentShow
                + ", isVirtualSelfLoadSuccess = " + this.isNeedShowVirtualSelf);
        if (this.isFragmentShow && this.isNeedShowVirtualSelf) {
            this.isNeedShowVirtualSelf = false;
            this.mScrollLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    AccountInfoActivity.this.mFriendVirtualSelfFragment.sayHi(1000L);
                    AccountInfoActivity.this.mFlVirtualSelf.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AccountInfoActivity.this.mFriendVirtualSelfFragment.sayHi(0L);
                        }
                    });
                    AccountInfoActivity accountInfoActivity = AccountInfoActivity.this;
                    accountInfoActivity.mSignatureAnimDuration = accountInfoActivity.mScrollLayout.scrollToOpenSlowly();
                    AccountInfoActivity.this.mContentScrollView.scrollTo(0, 0);
                    if (AccountInfoActivity.this.mSayHiRunnable == null) {
                        AccountInfoActivity.this.mSayHiRunnable = new Runnable() {
                            @Override
                            public void run() {
                                AccountInfoActivity.this.mFriendVirtualSelfFragment.sayHi(0L);
                            }
                        };
                    }
                }
            }, 100L);
        }
    }

    /** 下拉展开后播放签名气泡动画。 */
    private void showSignatureView() {
        if (this.mSignatureAnimDuration == 0) {
            LogUtil.d(TAG, "showSignatureView : 动画时长不能为0");
            return;
        }
        if (TextUtils.isEmpty(this.mSignature)) {
            LogUtil.d(TAG, "mSignature is empty");
            return;
        }
        this.mSignatureAnimDuration = 0;
        LogUtil.d(TAG, "showSignatureView");
        this.mTvSignatureBubbling.setText(this.mSignature);
        repeatSignatureAnim();
    }

    private void repeatSignatureAnim() {
        Observable.zip(Observable.range(0, 3), Observable.interval(200L, 4100L, TimeUnit.MILLISECONDS),
                new Func2<Integer, Long, Integer>() {
                    @Override
                    public Integer call(Integer count, Long interval) {
                        return count;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onError(Throwable throwable) {
                    }

                    @Override
                    public void onCompleted() {
                        AccountInfoActivity.this.mFlContent.setLayoutTransition(new LayoutTransition());
                    }

                    @Override
                    public void onNext(Integer count) {
                        LogUtil.d(TAG, "playSignatureAnim count = " + count);
                        AccountInfoActivity.this.mTvSignatureBubbling.setBackgroundResource(
                                AccountInfoActivity.this.mToastBackground[new Random().nextInt(100) % 3]);
                        AccountInfoActivity.this.mTvSignatureBubbling.setTranslationY(0.0f);
                        AccountInfoActivity.this.mTvSignatureBubbling.setScaleX(1.0f);
                        AccountInfoActivity.this.mTvSignatureBubbling.setScaleY(1.0f);
                        AccountInfoActivity.this.mTvSignatureBubbling.setVisibility(View.VISIBLE);
                        AccountInfoActivity.this.playSignatureAnim();
                    }
                });
    }

    private void playSignatureAnim() {
        AnimatorSet animatorSet = this.mSignatureAnimatorSet;
        if (animatorSet != null) {
            animatorSet.start();
            return;
        }
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(this.mTvSignatureBubbling, "scaleX", 0.5f, 1.0f, 1.2f, 1.0f);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(this.mTvSignatureBubbling, "scaleY", 0.5f, 1.0f, 1.2f, 1.0f);
        ObjectAnimator translationAnimator = ObjectAnimator.ofFloat(this.mTvSignatureBubbling, "translationY", 0.0f, -350.0f);
        scaleXAnimator.setDuration(400L);
        scaleYAnimator.setDuration(400L);
        translationAnimator.setDuration(DanmakuFactory.MIN_DANMAKU_DURATION);
        this.mSignatureAnimatorSet = new AnimatorSet();
        this.mSignatureAnimatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        this.mSignatureAnimatorSet.playTogether(scaleXAnimator, scaleYAnimator, translationAnimator);
        this.mSignatureAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                super.onAnimationEnd(animator);
                AccountInfoActivity.this.mTvSignatureBubbling.setVisibility(View.GONE);
            }
        });
        this.mSignatureAnimatorSet.start();
    }

    public void initData() {
        ((AccountPresenter) this.presenter).initData(this.watchId);
        ((AccountPresenter) this.presenter).bindDressService(this.mDressServiceListener, this.watchId);
        this.receiver = new MyStateRefreshReceiver();
        BroadcastReceiverUtil.registerReceiver(this, this.receiver, new IntentFilter(Constants.State.REFRESH_LIKES));
    }

    @Override
    public void startToChatActivity(String conversation) {
        if (TextUtils.isEmpty(conversation)) {
            LogUtil.w(TAG, "conversationString is empty");
            startToWeiChat();
            return;
        }
        Intent intent = new Intent();
        intent.setAction(PersonalInfoConstant.WeiChatIntent.INTENT_ACTION);
        intent.addCategory("android.intent.category.DEFAULT");
        intent.putExtra(PersonalInfoConstant.WeiChatIntent.KEY_CONVERSATION, conversation);
        intent.putExtra(PersonalInfoConstant.WeiChatIntent.KEY_CONVERSATION_SAVED, true);
        intent.setFlags(NotificationFlag.NOTIFICATION_FLAG_BOOT_USE);
        if (intent.resolveActivity(getPackageManager()) == null) {
            LogUtil.w(TAG, "无法打开微聊聊天页，跳转微聊即可");
            startToWeiChat();
        } else {
            startActivity(intent);
            finish();
        }
    }
    @Override
    public void updateState(final PersonalState personalState) {
        if (!ModuleSwitchUtil.queryModuleSwitchByBoolean(this, ModuleSwitchConstant.MODULE_SWITCH_SET_STATE, false)
                || personalState == null || personalState.getStatusId() == 0
                || TextUtils.isEmpty(personalState.getDesc())
                || System.currentTimeMillis() > personalState.getExpireTime()) {
            return;
        }
        this.llMyState.setVisibility(View.VISIBLE);
        Glide.with((FragmentActivity) this).load(personalState.getUrl()).into(this.ivMyState);
        this.tvMyState.setText(personalState.getDesc());
        this.llMyState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AccountInfoActivity.this.goOtherPackage(personalState);
            }
        });
    }

    private void goOtherPackage(PersonalState personalState) {
        if (!SystemUtil.isAppInstalled(this, "com.xtc.personalcenter")) {
            LogUtil.i(TAG, "未安装");
            showNoInstallDialog("com.xtc.personalcenter");
        } else {
            ((AccountPresenter) this.presenter).gotoState("com.xtc.personalcenter", personalState);
        }
    }

    private void showNoInstallDialog(final String packageName) {
        if (this.mDialog == null) {
            this.mDialog = DialogUtil.makeDoubleFlatBtnWithTitleDialog(this,
                    new DoubleFlatBtnWithTitleBean(this, false, getString(R.string.download_dec_title),
                            getString(R.string.download_personal_center), R.string.cancel, R.string.go_to_download));
            this.mDialog.getBottomBtn().getLeftButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DialogUtil.dismissDialog(AccountInfoActivity.this.mDialog);
                }
            });
            this.mDialog.getBottomBtn().getRightButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((AccountPresenter) AccountInfoActivity.this.presenter).gotoDownloadAPP(packageName);
                    DialogUtil.dismissDialog(AccountInfoActivity.this.mDialog);
                }
            });
        }
        DialogUtil.showDialog(this.mDialog);
    }

    public void startToWeiChat() {
        ComponentName componentName = new ComponentName("com.xtc.weichat", PersonalInfoConstant.WeiChatIntent.WEICHAT_ACTIVITY);
        Intent intent = new Intent();
        intent.setComponent(componentName);
        if (intent.resolveActivityInfo(getPackageManager(), 65536) != null) {
            LogUtil.i(TAG, "start startToWeiChat");
            startActivity(intent);
        } else {
            LogUtil.e(TAG, "targetComponent is null");
        }
        finish();
    }

    @Override
    public void showContactPortrait(Context context, ContactHeadManager contactHeadManager, ContactBean contactBean,
                                    View view, BitmapDrawable headBitmap) {
        view.setTag(R.string.contact_head_dislocation_tag, null);
        view.setTag(R.string.contact_head_last_update_tag, null);
        Glide.with(context).load(headBitmap)
                .apply(new RequestOptions().dontAnimate().circleCrop().skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .error(R.drawable.default_custom_default).placeholder(R.drawable.default_custom_default))
                .into((ImageView) view);
    }

    private void initScrollLayout() {
        this.mBottomShadowView = findViewById(R.id.tv_bottom_shadow);
        this.mContentScrollView = (ContentScrollView) findViewById(R.id.contentScrollView);
        this.mContentScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView scrollView, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > 20) {
                    AccountInfoActivity.this.mPraiseLinearLayout.setVisibility(View.GONE);
                } else {
                    AccountInfoActivity.this.mPraiseLinearLayout.setVisibility(View.VISIBLE);
                }
            }
        });
        this.mContentScrollView.setOnScrollStatusListener(new ContentScrollView.OnScrollStatusListener() {
            @Override
            public void onScrollStop() {
            }

            @Override
            public void onScrolling() {
            }

            @Override
            public void onFromTopScrollDown() {
                if (AccountInfoActivity.this.mScrollLayout == null
                        || AccountInfoActivity.this.mScrollLayout.isEnable()
                        || !FuncUtil.supportFriendVirtualSelf()) {
                    return;
                }
                AccountInfoActivity accountInfoActivity = AccountInfoActivity.this;
                ToastUtil.showShort(accountInfoActivity, accountInfoActivity.getString(R.string.no_virtual_self_tip));
            }
        });
        this.mScrollLayout = (ScrollLayout) findViewById(R.id.scrollLayout);
        this.mScrollLayout.setMinOffset(0);
        this.mScrollLayout.setMaxOffset(DimenUtil.dp2px(this, 10.0f));
        this.mScrollLayout.setExitOffset(DimenUtil.dp2px(this, 10.0f));
        this.mScrollLayout.setToClosed();
        this.mScrollLayout.setIsSupportExit(true);
        this.mScrollLayout.setAllowHorizontalScroll(false);
        this.mScrollLayout.setOnScrollChangedListener(this.mOnScrollChangedListener);
        this.mBottomShadowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogUtil.d(TAG, "mTvBottomShadow click");
                AccountInfoActivity.this.mScrollLayout.scrollToCloseSlowly();
            }
        });
        this.ivGenderBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogUtil.d(TAG, "ivGenderBackground click");
                if (ScrollLayout.Status.CLOSED.equals(AccountInfoActivity.this.mScrollLayout.getCurrentStatus())) {
                    return;
                }
                AccountInfoActivity.this.mScrollLayout.scrollToCloseSlowly();
            }
        });
        this.mFlVirtualSelf.setScrollLayout(this.mScrollLayout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.isFragmentShow = true;
        showVirtualSelfView();
        KsgLikeView ksgLikeView = this.mKsgLikeView;
        if (ksgLikeView != null) {
            ksgLikeView.onResume();
        }
        sayHi();
    }

    private void sayHi() {
        ScrollLayout scrollLayout;
        if (this.mSayHiRunnable == null || (scrollLayout = this.mScrollLayout) == null || !scrollLayout.isEnable()) {
            return;
        }
        this.mScrollLayout.postDelayed(this.mSayHiRunnable, 100L);
    }

    @Override
    protected void onPause() {
        super.onPause();
        KsgLikeView ksgLikeView = this.mKsgLikeView;
        if (ksgLikeView != null) {
            ksgLikeView.onPause();
        }
        ScrollLayout scrollLayout;
        if (this.mSayHiRunnable == null || (scrollLayout = this.mScrollLayout) == null || !scrollLayout.isEnable()) {
            return;
        }
        this.mScrollLayout.removeCallbacks(this.mSayHiRunnable);
    }
    private void initPraiseView() {
        this.mPraiseLinearLayout = (PraiseLinearLayout) findViewById(R.id.praiseView);
        this.mPraiseLinearLayout.setOnPraiseClickListener(new PraiseLinearLayout.OnPraiseClickListener() {
            @Override
            public void onPraiseClick() {
                LogUtil.i(TAG, "onPraiseClick");
                if (AccountInfoActivity.this.isFragmentShow) {
                    AccountInfoActivity.this.mKsgLikeView.addFavor();
                }
            }
        });
    }

    @Override
    public void updatePersonalInfo(PersonalInfoResponse personalInfoResponse) {
        this.curInfo = personalInfoResponse;
        ((AccountPresenter) this.presenter).dealPrerogativeBg(this.watchId);
        dealBirthdayAndConstellation(personalInfoResponse.getBirthday());
        dealGender(personalInfoResponse.getGender());
        dealGeniusNumber(personalInfoResponse.getGeniusNumber());
        dealIntegralAndLevel(personalInfoResponse.getScore(), personalInfoResponse.getLevel());
    }

    @Override
    public void updateSignatureAndLike(PersonalInfoAndLikeRuleResponse personalInfoAndLikeRuleResponse) {
        this.mPraiseLinearLayout.setVisibility(View.VISIBLE);
        if (personalInfoAndLikeRuleResponse == null) {
            LogUtil.d(TAG, "updateSignatureAndLike: response is null");
            return;
        }
        this.mSignature = personalInfoAndLikeRuleResponse.getSignature();
        if (!TextUtils.isEmpty(this.mSignature)) {
            this.personalizedSignatureItem.getTvExtra().setText(this.mSignature);
        } else {
            this.mSignature = getString(R.string.welcome_to_my_home_page);
        }
        showSignatureView();
        this.mPraiseLinearLayout.setData(this.watchId, personalInfoAndLikeRuleResponse.getLikes(),
                personalInfoAndLikeRuleResponse.getLikeLimit(), personalInfoAndLikeRuleResponse.getRestLikes(),
                personalInfoAndLikeRuleResponse.getFuzzyLikes(),
                personalInfoAndLikeRuleResponse.getLikesLimitNumber());
    }

    @Override
    public void showLoading() {
        LoadingViewHolder loadingViewHolder = this.loadingHolder;
        if (loadingViewHolder != null) {
            loadingViewHolder.showLoading(getWindow().getDecorView());
        }
    }

    @Override
    public void setHelperStr(String loadingStr, String failedStr, String successStr) {
        LoadingViewHolder loadingViewHolder = this.loadingHolder;
        if (loadingViewHolder != null) {
            loadingViewHolder.setHelperStr(loadingStr, failedStr, successStr);
        }
    }

    @Override
    public void loadSuccess() {
        LoadingViewHolder loadingViewHolder = this.loadingHolder;
        if (loadingViewHolder != null) {
            loadingViewHolder.loadSuccess();
        }
    }

    @Override
    public void loadFailed(String message) {
        if (this.loadingHolder != null) {
            if (!TextUtils.isEmpty(message) && message.contains("1003")) {
                this.loadingHolder.setFailedStr(getString(R.string.frequent_request));
            }
            this.loadingHolder.loadFailed();
        }
    }

    @Override
    public void deleteFriendSuccess() {
        LoadingViewHolder loadingViewHolder = this.loadingHolder;
        if (loadingViewHolder != null) {
            loadingViewHolder.setSuccessAutoDismiss(false);
            this.loadingHolder.setOnSuccessAction(new Runnable() {
                @Override
                public void run() {
                    AccountInfoActivity.this.finish();
                }
            });
        }
        loadSuccess();
    }

    @Override
    public void updateBadge(List<BadgeBean> badgeBeanList) {
        PersonalBadgeItem personalBadgeItem = this.itemBadge;
        if (personalBadgeItem != null) {
            personalBadgeItem.updateBadge(badgeBeanList);
        }
    }

    @Override
    public void onContactRemove(String removedWatchId) {
        if (TextUtils.equals(removedWatchId, this.watchId)) {
            finish();
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        DressUtil.removeDressHead(this.ivSvgaAvatarDress);
    }

    /** 根据生日展示生日文案与对应星座的图标、颜色与名称。 */
    private void dealBirthdayAndConstellation(long birthday) {
        if (birthday <= 0) {
            this.tvNoBirthday.setVisibility(View.VISIBLE);
            this.llBirthdayAndConstellation.setVisibility(View.GONE);
            return;
        }
        this.tvBirthday.setText(BirthdayUtil.getBirthdayString(this, birthday));
        int constellationIcon;
        int constellationColor;
        String constellationName;
        switch (BirthdayUtil.getConstellationByBirthday(birthday)) {
            case 2:
                constellationIcon = R.drawable.ic_constellation_shuiping;
                constellationColor = R.color.color_aquarius;
                constellationName = getString(R.string.aquarius);
                break;
            case 3:
                constellationIcon = R.drawable.ic_constellation_shuangyu;
                constellationColor = R.color.color_pisces;
                constellationName = getString(R.string.pisces);
                break;
            case 4:
                constellationIcon = R.drawable.ic_constellation_baiyang;
                constellationColor = R.color.color_aries;
                constellationName = getString(R.string.aries);
                break;
            case 5:
                constellationIcon = R.drawable.ic_constellation_jinniu;
                constellationColor = R.color.color_taurus;
                constellationName = getString(R.string.taurus);
                break;
            case 6:
                constellationIcon = R.drawable.ic_constellation_shuangzi;
                constellationColor = R.color.color_gemini;
                constellationName = getString(R.string.gemini);
                break;
            case 7:
                constellationIcon = R.drawable.ic_constellation_juxie;
                constellationColor = R.color.color_cancer;
                constellationName = getString(R.string.cancer);
                break;
            case 8:
                constellationIcon = R.drawable.ic_constellation_shizi;
                constellationColor = R.color.color_leo;
                constellationName = getString(R.string.leo);
                break;
            case 9:
                constellationIcon = R.drawable.ic_constellation_chunv;
                constellationColor = R.color.color_virgo;
                constellationName = getString(R.string.virgo);
                break;
            case 10:
                constellationIcon = R.drawable.ic_constellation_tiancheng;
                constellationColor = R.color.color_libra;
                constellationName = getString(R.string.libra);
                break;
            case 11:
                constellationIcon = R.drawable.ic_constellation_tianxie;
                constellationColor = R.color.color_scorpio;
                constellationName = getString(R.string.scorpio);
                break;
            case 12:
                constellationIcon = R.drawable.ic_constellation_sheshou;
                constellationColor = R.color.color_sagittarius;
                constellationName = getString(R.string.sagittarius);
                break;
            default:
                constellationIcon = R.drawable.ic_constellation_mojie;
                constellationColor = R.color.color_capricornus;
                constellationName = getString(R.string.capricornus);
                break;
        }
        this.tvConstellation.setText(constellationName);
        this.tvConstellation.setTextColor(getResources().getColor(constellationColor));
        this.ivConstellation.setImageResource(constellationIcon);
        this.llBirthdayAndConstellation.setVisibility(View.VISIBLE);
        this.tvNoBirthday.setVisibility(View.GONE);
    }

    /** 性别：1 女、0 男，其它值不展示。 */
    private void dealGender(int gender) {
        if (gender == 1) {
            this.ivGender.setImageResource(R.drawable.ic_girl);
            this.ivGender.setVisibility(View.VISIBLE);
        } else if (gender == 0) {
            this.ivGender.setImageResource(R.drawable.ic_boy);
            this.ivGender.setVisibility(View.VISIBLE);
        } else {
            this.ivGender.setVisibility(View.GONE);
        }
    }

    private void dealGeniusNumber(String geniusNumber) {
        this.geniusNumber = geniusNumber;
        this.itemGeniusNumber.getTextView().setText(TextUtils.isEmpty(geniusNumber)
                ? getString(R.string.genius_number, new Object[]{getString(R.string.not_set)})
                : getString(R.string.genius_number, new Object[]{geniusNumber}));
    }

    private void dealIntegralAndLevel(int score, int level) {
        this.levelItem.getTvExtra().setText(String.valueOf(level));
    }
    @Override
    public void refreshMainBg(String bgUrl) {
        LogUtil.d(TAG, "refreshMainBg: " + bgUrl + ";");
        if (TextUtils.isEmpty(bgUrl)) {
            Drawable genderBg = getResources().getDrawable(R.drawable.bg_gender_boy);
            if (this.curInfo != null && this.curInfo.getGender() == 1) {
                genderBg = getResources().getDrawable(R.drawable.bg_gender_girl);
            }
            Glide.with((FragmentActivity) this).load(genderBg).into(this.ivGenderBackground);
            return;
        }
        Drawable genderBg = getResources().getDrawable(R.drawable.bg_gender_boy);
        if (this.curInfo != null && this.curInfo.getGender() == 1) {
            genderBg = getResources().getDrawable(R.drawable.bg_gender_girl);
        }
        Glide.with((FragmentActivity) this).load(bgUrl)
                .apply(new RequestOptions()
                        .signature(new ObjectKey(Long.valueOf(System.currentTimeMillis())))
                        .error(genderBg)
                        .placeholder(genderBg))
                .into(this.ivGenderBackground);
    }

    private void initPersonalizedSignatureItem() {
        this.personalizedSignatureItem = (LeftIconItemWithExtraInfo) findViewById(R.id.item_personalized_signature);
        this.personalizedSignatureItem.getImageView().setImageResource(R.drawable.ic_personalized_signature);
        this.personalizedSignatureItem.getTvTitle().setText(getString(R.string.personalized_signature));
        this.personalizedSignatureItem.getTvExtra().setText(getString(R.string.not_set));
    }

    private void initLevelItem() {
        this.levelItem = (LeftIconItemWithExtraInfo) findViewById(R.id.item_level);
        this.levelItem.getImageView().setImageResource(R.drawable.ic_level);
        this.levelItem.getTvTitle().setText(getString(R.string.level));
        TextView tvExtra = this.levelItem.getTvExtra();
        tvExtra.setTextSize(0, getResources().getDimensionPixelOffset(R.dimen.sp_19));
        tvExtra.setTextColor(getResources().getColor(R.color.color_level));
        tvExtra.setText(String.valueOf(1));
    }

    private void initGoToChatButton() {
        LongSolidButton goToChatButton = (LongSolidButton) findViewById(R.id.lsb_go_to_chat);
        if (this.isSelf) {
            goToChatButton.setVisibility(View.GONE);
            return;
        }
        goToChatButton.setBgColorIdArray(BLUE_BUTTON);
        goToChatButton.getTv().setText(getString(R.string.start_to_chat));
        goToChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((AccountPresenter) AccountInfoActivity.this.presenter).gotoChat(AccountInfoActivity.this.watchId);
            }
        });
    }

    private void initDeleteFriendButton() {
        LongSolidButton deleteFriendButton = (LongSolidButton) findViewById(R.id.lsb_delete_friend);
        if (this.isSelf) {
            deleteFriendButton.setVisibility(View.GONE);
            return;
        }
        deleteFriendButton.setBgColorIdArray(GRAY_BUTTON);
        deleteFriendButton.getTv().setText(getString(R.string.delete_friend));
        if (ModuleSwitchUtil.queryModuleSwitchByBoolean(this, 116, true) && FuncUtil.supportLocalDeleteFriendWatch()) {
            deleteFriendButton.setVisibility(View.VISIBLE);
        } else {
            deleteFriendButton.setVisibility(View.GONE);
        }
        deleteFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AccountInfoActivity.this.confirmDelete();
            }
        });
    }

    private void confirmDelete() {
        this.deleteConfirmDlg = DialogUtil.makeDoubleFlatBtnDialog(this,
                new DoubleFlatBtnBean(this, true, getString(R.string.remind_delete_friend),
                        R.string.cancel, R.string.sure));
        this.deleteConfirmDlg.getBottomBtn().getLeftButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AccountInfoActivity.this.deleteConfirmDlg.cancel();
            }
        });
        this.deleteConfirmDlg.getBottomBtn().getRightButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AccountInfoActivity.this.deleteConfirmDlg.dismiss();
                ((AccountPresenter) AccountInfoActivity.this.presenter).deleteFriend(AccountInfoActivity.this.watchId);
            }
        });
        DialogUtil.showDialog(this.deleteConfirmDlg);
    }

    private void showGeniusNumIntroduceDialog() {
        DialogUtil.dismissDialog(this.geniusNumberIntroduceDialog);
        this.geniusNumberIntroduceDialog = new GeniusNumberIntroduceDialog(this, this.geniusNumber);
        this.geniusNumberIntroduceDialog.setGeniusNumber(this.geniusNumber);
        DialogUtil.showDialog(this.geniusNumberIntroduceDialog);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DialogUtil.dismissDialog(this.deleteConfirmDlg);
        DialogUtil.dismissDialog(this.geniusNumberIntroduceDialog);
        LoadingViewHolder loadingViewHolder = this.loadingHolder;
        if (loadingViewHolder != null) {
            loadingViewHolder.dismissLoading();
        }
        MyStateRefreshReceiver myStateRefreshReceiver = this.receiver;
        if (myStateRefreshReceiver != null) {
            BroadcastReceiverUtil.unregisterReceiver(this, myStateRefreshReceiver);
        }
    }

    @Override
    public void loadHeadDressSuccess() {
        DressUtil.setDressHead(this, this.watchId, this.ivSvgaAvatarDress);
    }

    @Override
    public void loadNickNameSuccess() {
        DressUtil.setNicknameSource(this.watchId, this.tvNickName);
    }

    /** 好友状态刷新广播接收器。 */
    public class MyStateRefreshReceiver extends BroadcastReceiver {
        public MyStateRefreshReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtil.d(TAG, "refresh friend information");
            ((AccountPresenter) AccountInfoActivity.this.presenter).refreshPersonalState(AccountInfoActivity.this.watchId);
        }
    }
}