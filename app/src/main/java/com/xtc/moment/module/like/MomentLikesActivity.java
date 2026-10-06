package com.xtc.moment.module.like;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.xtc.architecture.mvp.BaseActivity;
import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.db.bean.DbLikeMessage;
import com.xtc.moment.db.bean.prerogative.DbMomentPrerogativeLike;
import com.xtc.moment.module.Constants;
import com.xtc.moment.module.bean.EventType;
import com.xtc.moment.module.bean.Friend;
import com.xtc.moment.prerogative.MomentPrerogativeServeImpl;
import com.xtc.moment.util.DressUtil;
import com.xtc.moment.util.HandlerUtil;
import com.xtc.moment.util.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 单条动态的点赞列表页面。
 */
public class MomentLikesActivity extends BaseActivity<IMomentLikeView, MomentLikePresenter> implements IMomentLikeView {

    private static final String TAG = MomentLikesActivity.class.getSimpleName();
    private static final int ITEM_TYPE_HEAD = 1;
    private static final int ITEM_TYPE = 2;

    private RecyclerView mRv;
    private ImageView noLikeImg;
    private TextView noLikeTv;
    private TextView titleTv;

    private String watchID;
    private String momentID;
    private int likeTotal;
    private List<DbLikeMessage> dbLikeMessageList;
    private List<Friend> friendList = new ArrayList<>();

    private final RecyclerView.Adapter adapter = new RecyclerView.Adapter() {

        @Override
        public int getItemViewType(int position) {
            return position == 0 ? ITEM_TYPE_HEAD : ITEM_TYPE;
        }

        class ItemType extends RecyclerView.ViewHolder {

            private ImageView friendIcon;
            private TextView friendName;
            private final ImageView friendLikeView;

            public ItemType(View itemView) {
                super(itemView);
                this.friendIcon = (ImageView) itemView.findViewById(R.id.moment_like_friend_icon);
                this.friendName = (TextView) itemView.findViewById(R.id.moment_like_friend_name);
                this.friendLikeView = (ImageView) itemView.findViewById(R.id.moment_like_friend_like);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LogUtil.d("moment", "viewType:" + viewType);
            if (viewType == ITEM_TYPE_HEAD) {
                return new RecyclerView.ViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.view_head_moment_like, parent, false)) {
                };
            }
            return new ItemType(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_moment_like, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (getItemViewType(position) != ITEM_TYPE) {
                return;
            }
            ItemType itemHolder = (ItemType) holder;
            itemHolder.friendName.getPaint().setShader(null);
            DbLikeMessage likeMessage = MomentLikesActivity.this.dbLikeMessageList
                    .get(itemHolder.getAdapterPosition() - 1);
            int emotionId = likeMessage.getEmotionId();
            LogUtil.i(TAG, "emotionId" + emotionId);
            if (emotionId == 0) {
                itemHolder.friendLikeView.setImageResource(R.drawable.circle_like_pressed);
            } else {
                DbMomentPrerogativeLike prerogativeLike = MomentPrerogativeServeImpl
                        .getInstance(MomentLikesActivity.this.getApplicationContext())
                        .getPrerogativeLikeByEmotionId(emotionId);
                if (prerogativeLike == null) {
                    itemHolder.friendLikeView.setImageResource(R.drawable.circle_like_pressed);
                    return;
                }
                Glide.with(MomentLikesActivity.this.getApplication())
                        .load(prerogativeLike.getPraisedPic())
                        .apply(new RequestOptions().error(R.drawable.circle_like_pressed)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .placeholder(R.drawable.circle_like_pressed))
                        .into(itemHolder.friendLikeView);
            }
            if (MomentLikesActivity.this.friendList == null || MomentLikesActivity.this.friendList.isEmpty()) {
                bindDefaultIcon(itemHolder, likeMessage);
                return;
            }
            boolean matched = false;
            for (Friend friend : MomentLikesActivity.this.friendList) {
                if (friend.getWatchId() != null && friend.getWatchId().equals(likeMessage.getWatchId())) {
                    if (!TextUtils.isEmpty(friend.getFriendIcon())) {
                        Glide.with(MomentLikesActivity.this.getApplication())
                                .load(friend.getFriendIcon())
                                .apply(new RequestOptions().circleCrop()
                                        .error(R.drawable.default_custom_default)
                                        .signature(new ObjectKey(String.valueOf(Math.random()))))
                                .into(itemHolder.friendIcon);
                    } else {
                        bindDefaultIcon(itemHolder, likeMessage);
                    }
                    itemHolder.friendName.setText(friend.getName());
                    DressUtil.setNicknameSource(friend.getWatchId(), itemHolder.friendName);
                    matched = true;
                    break;
                }
            }
            if (!matched) {
                bindDefaultIcon(itemHolder, likeMessage);
            }
        }

        private void bindDefaultIcon(ItemType itemHolder, DbLikeMessage likeMessage) {
            Glide.with(MomentLikesActivity.this.getApplication())
                    .load(MomentLikesActivity.this.presenter.getDefault())
                    .apply(new RequestOptions().circleCrop()
                            .error(R.drawable.default_custom_default)
                            .signature(new ObjectKey(String.valueOf(Math.random()))))
                    .into(itemHolder.friendIcon);
            itemHolder.friendName.setText(likeMessage.getWatchName());
            DressUtil.setNicknameSource(likeMessage.getWatchId(), itemHolder.friendName);
        }

        @Override
        public int getItemCount() {
            if (MomentLikesActivity.this.dbLikeMessageList == null) {
                return 0;
            }
            return MomentLikesActivity.this.dbLikeMessageList.size() + 1;
        }
    };

    @Override
    public void initData() {
    }

    @Override
    public void initView() {
    }

    @Override
    public MomentLikePresenter createPresenter() {
        return new MomentLikePresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moment_like);
        this.mRv = (RecyclerView) findViewById(R.id.rv_moment_like);
        this.mRv.setLayoutManager(new LinearLayoutManager(getApplication()));
        this.mRv.setAdapter(this.adapter);
        this.noLikeImg = (ImageView) findViewById(R.id.no_moment_no_like_img);
        this.noLikeTv = (TextView) findViewById(R.id.no_moment_no_like_title);
        this.titleTv = (TextView) findViewById(R.id.moment_like_title);
        this.watchID = getIntent().getStringExtra(Constants.INTENT_EXTRA_WATCH_ID);
        this.momentID = getIntent().getStringExtra(Constants.INTENT_EXTRA_MOMENT_ID);
        this.likeTotal = getIntent().getIntExtra(Constants.INTENT_EXTRA_LIKE_TOTAL, 0);
        LogUtil.i(TAG, "likeTotal: " + this.likeTotal);
        if (this.likeTotal == 0) {
            showNoMomentsLike();
        } else {
            HandlerUtil.runOnBackground(new Runnable() {
                @Override
                public void run() {
                    MomentLikesActivity.this.friendList = MomentLikesActivity.this.presenter.getAllFriend();
                    MomentLikesActivity.this.dbLikeMessageList =
                            MomentLikesActivity.this.presenter.getLikeMessageFromDb(MomentLikesActivity.this.momentID);
                    Utils.logSize(TAG, "dbLikeMessageList size ", MomentLikesActivity.this.dbLikeMessageList);
                    if (MomentLikesActivity.this.dbLikeMessageList == null
                            || MomentLikesActivity.this.dbLikeMessageList.isEmpty()
                            || MomentLikesActivity.this.likeTotal != MomentLikesActivity.this.dbLikeMessageList.size()) {
                        ArrayList<String> momentIds = new ArrayList<>();
                        momentIds.add(MomentLikesActivity.this.momentID);
                        MomentLikesActivity.this.presenter.loadMomentLike(momentIds, MomentLikesActivity.this.watchID);
                        return;
                    }
                    HandlerUtil.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            if (MomentLikesActivity.this.dbLikeMessageList == null
                                    || MomentLikesActivity.this.dbLikeMessageList.size() <= 0) {
                                return;
                            }
                            MomentLikesActivity.this.adapter.notifyDataSetChanged();
                        }
                    });
                }
            });
        }
        EventBus.getDefault().register(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEvent(EventType eventType) {
        if (eventType.getType() == 2) {
            LogUtil.i(TAG, "receive contace update event");
            this.friendList = this.presenter.getAllFriend();
            HandlerUtil.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    MomentLikesActivity.this.adapter.notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public void showMomentLikes(Map<String, List<DbLikeMessage>> likeMessageMap) {
        if (likeMessageMap != null && likeMessageMap.size() > 0) {
            this.dbLikeMessageList = likeMessageMap.get(this.momentID);
            LogUtil.i(TAG, "dbLikeMessageList: " + this.dbLikeMessageList.size() + " " + this.dbLikeMessageList);
            this.adapter.notifyDataSetChanged();
            return;
        }
        showNoMomentsLike();
    }

    @Override
    public void showNoMomentsLike() {
        this.titleTv.setVisibility(View.VISIBLE);
        this.noLikeTv.setVisibility(View.VISIBLE);
        this.noLikeImg.setVisibility(View.VISIBLE);
    }

    @Override
    public void showGetMomentLikesError() {
        Toast.makeText(getApplication(), R.string.get_praise_fail, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, "onDestroy() called");
        EventBus.getDefault().unregister(this);
    }
}