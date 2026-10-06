package com.xtc.moment.module.publish.visible.friends;

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
import com.xtc.moment.util.SystemUtil;
import com.xtc.moment.widget.TouchScrollbarRecyclerView;
import com.xtc.ui.widget.button.LongSolidButton;
import com.xtc.utils.ui.DimenUtil;

import java.util.List;

/**
 * Page that lets the user pick the friends included in (or excluded from) a visible range.
 */
public class VisibleFriendsListActivity
        extends BaseActivity<IVisibleFrienListView, VisibleFriendListPresenter>
        implements IVisibleFrienListView {

    public static final String EXTRA_VISIBLE_BEAN = "visible";

    private static final String TAG = "VisibleFriendsListActivity";
    private static final long BTN_DELAY_TIME = 400L;
    /** Request code used when this page is opened from the range page. */
    private static final int REQUEST_CODE = 7;
    /** Width of the confirm button label, in dp. */
    private static final float BTN_TEXT_WIDTH_DP = 75.0f;

    private final Context context = this;

    private TouchScrollbarRecyclerView touchScrollbarRecyclerView;
    private VisibleFriendsAdapter visibleFriendsAdapter;
    private LongSolidButton btnCorrect;
    private FriendsVisibleBean friendsVisibleBean;
    private boolean isScrolling;
    private boolean isBtnAbleShow;

    /** Opens the friend picker and returns the updated bean as an activity result. */
    public static void startForResult(Activity activity, FriendsVisibleBean friendsVisibleBean) {
        Intent intent = new Intent(activity, VisibleFriendsListActivity.class);
        intent.putExtra(EXTRA_VISIBLE_BEAN, friendsVisibleBean);
        activity.startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    public VisibleFriendListPresenter createPresenter() {
        return new VisibleFriendListPresenter(this);
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
        AsyncLayoutLoader.getInstance().setContentView(this, R.layout.layout_frineds_visible_choose);
        this.touchScrollbarRecyclerView = (TouchScrollbarRecyclerView) findViewById(R.id.rv_visible_friends_list);
        this.touchScrollbarRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.btnCorrect = (LongSolidButton) findViewById(R.id.btn_choose_friends);
        this.btnCorrect.setBgColorIdArray(Constants.ColorResource.GRADIENT_ORIG);
        this.btnCorrect.getTv().setWidth(DimenUtil.dp2px(this.context, BTN_TEXT_WIDTH_DP));
        this.btnCorrect.getTv().setText(this.context.getResources().getString(R.string.chose_correct));
        this.btnCorrect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemUtil.isFastDoubleClick()) {
                    LogUtil.i(TAG, "onClick: btnCorrect click too fast.");
                    return;
                }
                endThis();
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
        if (this.friendsVisibleBean == null) {
            this.friendsVisibleBean = new FriendsVisibleBean();
        }
        this.visibleFriendsAdapter = new VisibleFriendsAdapter(this.context,
                this.presenter.getAllFriend(), this.friendsVisibleBean,
                new VisibleFriendsAdapter.SelectedFriendsCallback() {
                    @Override
                    public void onDataSelected(List<String> selectedWatchIds) {
                        friendsVisibleBean.setFriends(selectedWatchIds);
                        isBtnAbleShow = !CollectionUtil.isEmpty(selectedWatchIds);
                        btnCorrect.setVisibility(isBtnAbleShow ? View.VISIBLE : View.GONE);
                    }
                });
        this.touchScrollbarRecyclerView.setAdapter(this.visibleFriendsAdapter);
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
                        if (isBtnAbleShow) {
                            animatedView.setVisibility(View.VISIBLE);
                        }
                    }
                })
                .setDuration(BTN_DELAY_TIME)
                .start();
    }

    private void endThis() {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_VISIBLE_BEAN, this.friendsVisibleBean);
        setResult(RESULT_OK, intent);
        finish();
    }
}