package com.xtc.moment.module.publish.visible;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.xtc.architecture.mvp.BaseActivity;
import com.xtc.database.ormlite.CollectionUtil;
import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.asynclayout.AsyncLayoutLoader;
import com.xtc.moment.module.Constants;
import com.xtc.moment.module.bean.FriendsVisibleBean;
import com.xtc.moment.module.widget.LoadingPupWindowHolder;
import com.xtc.moment.util.HandlerUtil;
import com.xtc.moment.util.SystemUtil;
import com.xtc.moment.util.ToastUtil;
import com.xtc.moment.widget.TouchScrollbarRecyclerView;
import com.xtc.ui.widget.button.LongSolidButton;
import com.xtc.utils.ui.DimenUtil;

import java.util.List;

/**
 * Page that lets the user pick the visible range of a moment.
 */
public class VisibleTypeActivity extends BaseActivity<IVisibleTypeView, VisibleTypePresenter>
        implements IVisibleTypeView {

    public static final String EXTRA_VISIBLE_BEAN = "visible";
    public static final String EXTRA_MOMENT_CHANGE_TYPE = "moment_visible_type";

    private static final String TAG = "VisibleTypeActivity";
    private static final long BTN_DELAY_TIME = 400L;
    /** Request code used when the friend picker returns. */
    private static final int REQUEST_CODE = 7;
    /** Request code used when this page is opened from the moment page. */
    private static final int REQUEST_FROM_MOMENT = 6;
    /** Width of the confirm button label, in dp. */
    private static final float BTN_TEXT_WIDTH_DP = 75.0f;

    /** Visible range: all friends. */
    private static final int TYPE_ALL = 0;
    /** Visible range: nobody. */
    private static final int TYPE_NONE = 3;

    /** Opens this page for the result; {@code changeType} 2 pushes the range to the server. */
    public static void startForResult(Activity activity, FriendsVisibleBean friendsVisibleBean, int changeType) {
        Intent intent = new Intent(activity, VisibleTypeActivity.class);
        intent.putExtra(EXTRA_VISIBLE_BEAN, friendsVisibleBean);
        intent.putExtra(EXTRA_MOMENT_CHANGE_TYPE, changeType);
        activity.startActivityForResult(intent, REQUEST_FROM_MOMENT);
    }

    private final Context context = this;

    private TouchScrollbarRecyclerView touchScrollbarRecyclerView;
    private VisibleTypeAdapter visibleTypeAdapter;
    private LongSolidButton btnCorrect;
    private LoadingPupWindowHolder loadingPupWindowHolder;
    private FriendsVisibleBean friendsVisibleBean;
    private int publicType;
    private boolean isScrolling;

    /** Returns true for the two "selected friends" ranges. */
    boolean isShowPart(int type) {
        return type == 1 || type == 2;
    }

    @Override
    public VisibleTypePresenter createPresenter() {
        return new VisibleTypePresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    @Override
    public void initView() {
        LogUtil.d(TAG, "initView");
        AsyncLayoutLoader.getInstance().setContentView(this, R.layout.layout_visible_type);
        this.touchScrollbarRecyclerView = (TouchScrollbarRecyclerView) findViewById(R.id.rv_visible_type_list);
        this.touchScrollbarRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.btnCorrect = (LongSolidButton) findViewById(R.id.btn_choose_visible_type);
        this.btnCorrect.getTv().setWidth(DimenUtil.dp2px(this.context, BTN_TEXT_WIDTH_DP));
        this.btnCorrect.getTv().setText(this.context.getResources().getString(R.string.chose_correct));
        this.loadingPupWindowHolder = new LoadingPupWindowHolder(this);
        this.loadingPupWindowHolder.setOnSuccessAction(new Runnable() {
            @Override
            public void run() {
                setResult(RESULT_OK, new Intent());
                finish();
            }
        });
        this.touchScrollbarRecyclerView.setScrollCallback(new TouchScrollbarRecyclerView.OnScrollCallback() {
            @Override
            public void onScrolling() {
                if (isScrolling) {
                    return;
                }
                isScrolling = true;
                hideInputLayout(btnCorrect);
            }

            @Override
            public void onScrollIdle(boolean idle) {
                isScrolling = false;
                showInputLayout(btnCorrect);
            }
        });
    }

    @Override
    public void initData() {
        this.friendsVisibleBean = (FriendsVisibleBean) getIntent().getParcelableExtra(EXTRA_VISIBLE_BEAN);
        this.publicType = getIntent().getIntExtra(EXTRA_MOMENT_CHANGE_TYPE, 0);
        if (this.friendsVisibleBean == null) {
            this.friendsVisibleBean = new FriendsVisibleBean();
        }
        refreshBtn();
        this.presenter.recordAdd(this.friendsVisibleBean);
        this.visibleTypeAdapter = new VisibleTypeAdapter(this, this.friendsVisibleBean);
        this.visibleTypeAdapter.setFriendVisibleBeanCallBack(new VisibleTypeAdapter.FriendVisibleBeanCallBack() {
            @Override
            public void setFriendVisibleBean(FriendsVisibleBean visibleBean) {
                if (visibleBean != null) {
                    friendsVisibleBean = visibleBean;
                    refreshBtn();
                }
            }
        });
        this.visibleTypeAdapter.setGetRecordCallback(new VisibleTypeAdapter.GetRecordCallback() {
            @Override
            public List<String> onGetRecord(int type) {
                return presenter.getRecord(type);
            }
        });
        this.touchScrollbarRecyclerView.setAdapter(this.visibleTypeAdapter);
    }

    /** Slides the confirm button out while the list is scrolling. */
    protected synchronized void hideInputLayout(View view) {
        ViewCompat.setTranslationY(view, 0.0f);
        ViewCompat.animate(view).cancel();
        ViewCompat.animate(view).alpha(0.0f).translationY(view.getHeight())
                .setListener(new ViewPropertyAnimatorListener() {
                    @Override
                    public void onAnimationCancel(View animatedView) {
                    }

                    @Override
                    public void onAnimationStart(View animatedView) {
                    }

                    @Override
                    public void onAnimationEnd(View animatedView) {
                        animatedView.setVisibility(View.GONE);
                    }
                })
                .setDuration(BTN_DELAY_TIME)
                .start();
    }

    /** Slides the confirm button back in when the list stops scrolling. */
    protected synchronized void showInputLayout(View view) {
        ViewCompat.setTranslationY(view, view.getHeight());
        ViewCompat.animate(view).cancel();
        ViewCompat.animate(view).alpha(1.0f).translationY(0.0f)
                .setListener(new ViewPropertyAnimatorListener() {
                    @Override
                    public void onAnimationCancel(View animatedView) {
                    }

                    @Override
                    public void onAnimationEnd(View animatedView) {
                    }

                    @Override
                    public void onAnimationStart(View animatedView) {
                        animatedView.setVisibility(View.VISIBLE);
                    }
                })
                .setDuration(BTN_DELAY_TIME)
                .start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQUEST_CODE || resultCode != RESULT_OK) {
            return;
        }
        int previousType = this.friendsVisibleBean.getType();
        this.friendsVisibleBean = (FriendsVisibleBean) data.getParcelableExtra(EXTRA_VISIBLE_BEAN);
        if (this.friendsVisibleBean == null) {
            this.friendsVisibleBean = new FriendsVisibleBean();
        }
        this.presenter.recordAdd(this.friendsVisibleBean);
        if (previousType != this.friendsVisibleBean.getType()
                && !CollectionUtil.isEmpty(this.friendsVisibleBean.getFriends())) {
            this.friendsVisibleBean.getFriends().clear();
        }
        refreshBtn();
        this.visibleTypeAdapter.setFriendsVisibleBean(this.friendsVisibleBean);
    }

    /** Finishes the page or pushes the range to the server depending on the request type. */
    private void endVisibleTypeActivity() {
        if (this.friendsVisibleBean.getType() == TYPE_ALL || this.friendsVisibleBean.getType() == TYPE_NONE) {
            this.friendsVisibleBean.setFriends(null);
        }
        int changeType = this.publicType;
        if (changeType == 1) {
            Intent intent = new Intent();
            intent.putExtra(EXTRA_VISIBLE_BEAN, this.friendsVisibleBean);
            setResult(RESULT_OK, intent);
            finish();
        } else if (changeType == 2) {
            this.loadingPupWindowHolder.showLoading(getWindow().getDecorView());
            this.presenter.changeRange(this.friendsVisibleBean);
        }
    }

    @Override
    public void changeResult(final boolean success) {
        HandlerUtil.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                if (success) {
                    loadingPupWindowHolder.setText(context.getString(R.string.change_success));
                    loadingPupWindowHolder.showSuccess();
                } else {
                    loadingPupWindowHolder.dismissLoading();
                    finish();
                }
            }
        });
    }

    /** Updates the confirm button according to the current range and selected friends. */
    private void refreshBtn() {
        final int type = this.friendsVisibleBean.getType();
        if (!isShowPart(type)) {
            this.btnCorrect.setBgColorIdArray(Constants.ColorResource.GRADIENT_ORIG);
            setBtnCorrectEndActivity();
        } else if (!CollectionUtil.isEmpty(this.friendsVisibleBean.getFriends())) {
            this.btnCorrect.setBgColorIdArray(Constants.ColorResource.GRADIENT_ORIG);
            setBtnCorrectEndActivity();
        } else {
            this.btnCorrect.setBgColorIdArray(Constants.ColorResource.GRADIENT_GRAY);
            this.btnCorrect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemUtil.isFastLongDoubleClick()) {
                        LogUtil.i(TAG, "onClick: click too fast.");
                        return;
                    }
                    if (type == 1) {
                        ToastUtil.showShort(context, getString(R.string.please_choose_visible_friends));
                    } else if (type == 2) {
                        ToastUtil.showShort(context, getString(R.string.please_choose_unvisible_friends));
                    }
                }
            });
        }
    }

    private void setBtnCorrectEndActivity() {
        this.btnCorrect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!SystemUtil.isFastLongDoubleClick()) {
                    endVisibleTypeActivity();
                } else {
                    LogUtil.i(TAG, "onClick: click too fast.");
                }
            }
        });
    }
}