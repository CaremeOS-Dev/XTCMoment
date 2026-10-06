package com.xtc.moment.module.scope;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xtc.architecture.mvp.BaseActivity;
import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.asynclayout.AsyncLayoutLoader;
import com.xtc.moment.module.bean.FriendsVisibleBean;
import com.xtc.moment.util.HandlerUtil;
import com.xtc.moment.util.ToastUtil;
import com.xtc.utils.common.CollectionUtil;
import com.xtc.utils.ui.DimenUtil;

import java.util.List;

/**
 * 好友可见范围详情页面。
 */
public class FriendsVisibleRangeActivity
        extends BaseActivity<IFriendsVisibleRangeView, FriendsVisibleRangePresenter>
        implements IFriendsVisibleRangeView {

    private static final String TAG = "FriendsVisibleRangeActivity";

    public static final String MOMENT_ID = "moment_id";

    private final Context context = this;

    private ImageView ivVisibleType;
    private TextView tvVisibleDetails;
    private TextView tvTittle;
    private RecyclerView rvFriendList;
    private LinearLayout llChangeVisible;
    private VisibleFriendsAdapter visibleFriendsAdapter;
    private String momentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    @Override
    public FriendsVisibleRangePresenter createPresenter() {
        return new FriendsVisibleRangePresenter(this.context);
    }

    @Override
    public void initView() {
        LogUtil.d(TAG, "initView");
        AsyncLayoutLoader.getInstance().setContentView(this, R.layout.activity_friends_visible_range);
        this.ivVisibleType = (ImageView) findViewById(R.id.iv_type_visible);
        this.tvVisibleDetails = (TextView) findViewById(R.id.tv_these_rang_details);
        this.tvTittle = (TextView) findViewById(R.id.tv_rang_details_tittle);
        this.rvFriendList = (RecyclerView) findViewById(R.id.rv_friends_list);
        this.llChangeVisible = (LinearLayout) findViewById(R.id.ll_look_range);
        this.llChangeVisible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FriendsVisibleRangeActivity.this.changeVisibleRange();
            }
        });
    }

    @Override
    public void initData() {
        this.momentId = getIntent().getStringExtra(MOMENT_ID);
        if (TextUtils.isEmpty(this.momentId)) {
            finish();
            return;
        }
        this.presenter.getAllVisibleFriends(this.momentId);
    }

    @Override
    public void showPermissons(final FriendsVisibleBean friendsVisibleBean, final String errorMessage) {
        HandlerUtil.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                FriendsVisibleRangeActivity.this.showPermissonsUi(friendsVisibleBean, errorMessage);
            }
        });
    }

    private void showPermissonsUi(FriendsVisibleBean friendsVisibleBean, String errorMessage) {
        if (friendsVisibleBean == null) {
            if (!TextUtils.isEmpty(errorMessage) && errorMessage.contains("1003")) {
                ToastUtil.showShort(this.context, getString(R.string.frequent_request));
            } else {
                ToastUtil.showShort(this.context, getString(R.string.net_work_exception));
            }
            finish();
            return;
        }
        this.llChangeVisible.setVisibility(View.VISIBLE);
        List<String> friends = friendsVisibleBean.getFriends();
        int type = friendsVisibleBean.getType();
        if (type == 0) {
            this.ivVisibleType.setImageResource(R.drawable.bg_friends_visible);
            this.ivVisibleType.setVisibility(View.VISIBLE);
            this.rvFriendList.setVisibility(View.GONE);
            this.tvVisibleDetails.setText(this.context.getString(R.string.down_all_friends_able_see));
            return;
        }
        if (type == 1) {
            dealPartsFRriendsShow();
            this.tvTittle.setText(dealTittle(this.context.getString(R.string.down_friends_able_see)));
            if (CollectionUtil.isEmpty(friends)) {
                this.tvVisibleDetails.setText(this.context.getString(R.string.no_visible_friends_at_the_moment));
            } else {
                dealFriendsAdapter(friends);
            }
            return;
        }
        if (type == 2) {
            dealPartsFRriendsShow();
            this.tvTittle.setText(dealTittle(this.context.getString(R.string.down_friends_unable_see)));
            if (CollectionUtil.isEmpty(friends)) {
                this.tvVisibleDetails.setText(this.context.getString(R.string.no_unvisible_friends_at_the_moment));
            } else {
                dealFriendsAdapter(friends);
            }
            return;
        }
        if (type == 3) {
            this.ivVisibleType.setImageResource(R.drawable.bg_friends_oneself);
            this.ivVisibleType.setVisibility(View.VISIBLE);
            this.rvFriendList.setVisibility(View.GONE);
            this.tvVisibleDetails.setText(this.context.getString(R.string.down_onlyme_see));
        }
    }

    private void dealPartsFRriendsShow() {
        this.tvTittle.setVisibility(View.VISIBLE);
        this.ivVisibleType.setImageResource(R.drawable.bg_friends_visible);
        this.ivVisibleType.setVisibility(View.VISIBLE);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.ivVisibleType.getLayoutParams();
        layoutParams.topMargin = DimenUtil.dp2px(this.context, 25.0f);
        this.ivVisibleType.setLayoutParams(layoutParams);
    }

    /** 把文案里的占位符去掉并高亮中间的好友范围描述。 */
    private SpannableString dealTittle(String title) {
        int firstIndex = title.indexOf("%1$s");
        String withoutFirst = title.replace("%1$s", "");
        int secondIndex = withoutFirst.indexOf("%2$s");
        SpannableString spannableString = new SpannableString(withoutFirst.replace("%2$s", ""));
        spannableString.setSpan(new ForegroundColorSpan(
                this.context.getResources().getColor(R.color.color_24df1a)), firstIndex, secondIndex, 33);
        return spannableString;
    }

    private void dealFriendsAdapter(List<String> friends) {
        this.tvVisibleDetails.setVisibility(View.GONE);
        this.ivVisibleType.setVisibility(View.GONE);
        this.rvFriendList.setVisibility(View.VISIBLE);
        this.rvFriendList.setLayoutManager(new LinearLayoutManager(this));
        this.visibleFriendsAdapter = new VisibleFriendsAdapter(this.context,
                this.presenter.getFriends(friends));
        this.rvFriendList.setAdapter(this.visibleFriendsAdapter);
    }

    private void changeVisibleRange() {
        this.presenter.resetVisibleRange();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtil.i(TAG, "onActivityResult requestCode = " + requestCode + " resultCode = " + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 6) {
            finish();
        }
    }
}