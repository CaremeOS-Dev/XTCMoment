package com.xtc.moment.module.like;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xtc.architecture.mvp.BaseActivity;
import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.asynclayout.AsyncLayoutLoader;
import com.xtc.moment.db.bean.DbLikeMessage;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.helper.UnreadHelper;
import com.xtc.moment.module.bean.EventType;
import com.xtc.moment.module.bean.Friend;
import com.xtc.moment.module.bean.MomentNewMsgBean;
import com.xtc.moment.third.behavior.MomentBehavior;
import com.xtc.moment.util.HandlerUtil;
import com.xtc.moment.util.NotifyUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * 新消息（点赞/评论）页面。
 */
public class NewLikeActivity extends BaseActivity<INewLikeView, NewLikePresenter> implements INewLikeView {

    private RecyclerView mRv;
    private NewLikeAdapter newLikeAdapter;
    private TextView noNewLike;

    private long enterTime;
    private long leaveTime;
    List<Friend> friendList;
    List<DbLikeMessage> likeDatas;

    @Override
    public void initData() {
    }

    @Override
    public void initView() {
    }

    @Override
    public NewLikePresenter createPresenter() {
        return new NewLikePresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AsyncLayoutLoader.getInstance().setContentView(this, R.layout.activity_new_like);
        getWindow().setBackgroundDrawable(null);
        this.enterTime = System.currentTimeMillis();
        EventBus.getDefault().register(this);
        this.noNewLike = (TextView) findViewById(R.id.no_new_like);
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                NewLikeActivity.this.presenter.initData();
                NewLikeActivity.this.presenter.checkLikeMessage();
                NewLikeActivity.this.presenter.checkMomentComment();
                UnreadHelper.getInstance(NewLikeActivity.this).showUnreadNumber();
                NotifyUtils.notifyLikeUnReadNum(NewLikeActivity.this);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEvent(EventType eventType) {
        if (eventType.getType() == 2) {
            LogUtil.i("NewLikeActivity", "receive contace update event");
            this.friendList = this.presenter.getAllFriend();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.leaveTime = System.currentTimeMillis();
        this.likeDatas = null;
        MomentBehavior.newLikeEnter(getApplication(), this.enterTime, this.leaveTime);
    }

    @Override
    public void showNewLike(List<Friend> friends, List<DbLikeMessage> likeMessages) {
        this.friendList = friends;
        this.likeDatas = likeMessages;
        this.mRv = (RecyclerView) findViewById(R.id.rv_new_like);
        this.mRv.setLayoutManager(new LinearLayoutManager(getApplication()));
    }

    @Override
    public void showNewMsg(List<MomentNewMsgBean<DbMoment>> messages) {
        this.mRv = (RecyclerView) findViewById(R.id.rv_new_like);
        this.mRv.setLayoutManager(new LinearLayoutManager(getApplication()));
        this.newLikeAdapter = new NewLikeAdapter(this, messages);
        this.newLikeAdapter.setHeaderView(LayoutInflater.from(this)
                .inflate(R.layout.view_head_new_msg, (ViewGroup) this.mRv, false));
        this.newLikeAdapter.setFooterView(LayoutInflater.from(this)
                .inflate(R.layout.view_foot_new_msg, (ViewGroup) this.mRv, false));
        this.mRv.setAdapter(this.newLikeAdapter);
    }

    @Override
    public void showNoNewLike() {
        this.mRv = (RecyclerView) findViewById(R.id.rv_new_like);
        this.mRv.setLayoutManager(new LinearLayoutManager(getApplication()));
        this.newLikeAdapter = new NewLikeAdapter(this, null);
        this.newLikeAdapter.setHeaderView(LayoutInflater.from(this)
                .inflate(R.layout.view_head_new_msg, (ViewGroup) this.mRv, false));
        this.newLikeAdapter.setFooterView(LayoutInflater.from(this)
                .inflate(R.layout.view_foot_new_msg, (ViewGroup) this.mRv, false));
        this.mRv.setAdapter(this.newLikeAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}