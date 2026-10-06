package com.xtc.moment.module.publish.moodorstate;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xtc.architecture.mvp.BaseActivity;
import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.asynclayout.AsyncLayoutLoader;
import com.xtc.moment.behavior.DigitalManager;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.db.bean.DbTemplate;
import com.xtc.moment.module.Constants;
import com.xtc.moment.module.publish.text.PushTextActivity;
import com.xtc.moment.module.widget.MomentContentView;
import com.xtc.moment.third.behavior.MomentBehavior;
import com.xtc.moment.util.EventData;
import com.xtc.moment.util.HandlerUtil;
import com.xtc.moment.util.SystemUtil;
import com.xtc.moment.util.ToastUtil;
import com.xtc.ui.widget.ptrrefresh.adpter.ClassicRecyclerAdapter;
import com.xtc.ui.widget.ptrrefresh.footer.LoadMoreHelper;
import com.xtc.ui.widget.ptrrefresh.footer.OnLoadMoreListener;
import com.xtc.ui.widget.ptrrefresh.footer.PushLoadMoreView;
import com.xtc.ui.widget.ptrrefresh.layout.PullRefreshFrameLayout;
import com.xtc.utils.encode.JSONUtil;
import com.xtc.utils.system.NetworkUtils;
import com.xtc.utils.ui.DimenUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Page listing the mood or state templates; tapping one jumps to the text publish page.
 */
public class MoodOrStateActivity extends BaseActivity<IMoodOrStateView, MoodOrStatePresenter>
        implements IMoodOrStateView {

    private static final String TAG = "XTC_MOMENT_MoodOrStateActivity";

    /** Page size used when the mood list is loaded in batches. */
    private static final int PAGE_SIZE = 12;
    /** Delay before the load-more callback completes, in milliseconds. */
    private static final long LOAD_MORE_DELAY_MS = 1000L;
    /** Height of the load more footer, in dp. */
    private static final float FOOTER_HEIGHT_DP = 50.0f;

    /** Publish type: mood. */
    private static final int TYPE_MOOD = 0;
    /** Publish type: state. */
    private static final int TYPE_STATE = 1;

    private final Context mContext = this;
    private final List<DbTemplate> mData = new ArrayList<DbTemplate>();
    private final List<DbTemplate> mShuffledData = new ArrayList<DbTemplate>();

    private PullRefreshFrameLayout mLayout;
    private RecyclerView mRv;
    private LoadMoreHelper mLoadMoreHelper;
    private int mType;
    private int offset;
    private boolean isLoadMoreNull;
    private long enterTime;
    private long leaveTime;

    @Override
    public MoodOrStatePresenter createPresenter() {
        return new MoodOrStatePresenter(this);
    }

    @Override
    public void initData() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AsyncLayoutLoader.getInstance().setContentView(this, R.layout.activity_mood_state);
        getWindow().setBackgroundDrawable(null);
        this.enterTime = System.currentTimeMillis();
        this.mType = getIntent().getIntExtra(Constants.INTENT_EXTRA_PUBLISH_TYPE, -1);
        this.presenter.getTemplatesByTypeFromDb(this.mType);
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void initView() {
        EventBus.getDefault().register(this);
        this.mLayout = (PullRefreshFrameLayout) findViewById(R.id.layout);
        this.mRv = (RecyclerView) findViewById(R.id.rv);
        RecyclerView.Adapter<RecyclerView.ViewHolder> adapter = createBaseAdapter();
        this.mRv.setLayoutManager(new LinearLayoutManager(this));
        if (this.mType == TYPE_MOOD) {
            this.mRv.setAdapter(new ClassicRecyclerAdapter(adapter));
            initLoadMore();
        } else if (this.mType == TYPE_STATE) {
            this.mRv.setAdapter(adapter);
        }
    }

    private void initLoadMore() {
        this.mLoadMoreHelper = new LoadMoreHelper.RecyclerLoadMoreHelper();
        PushLoadMoreView loadMoreView = new PushLoadMoreView();
        this.mLoadMoreHelper.attachToView(this.mRv, loadMoreView);
        loadMoreView.setCirCleColor(Color.parseColor("#fec02b"));
        loadMoreView.setFooterHeight(DimenUtil.dp2px(this, FOOTER_HEIGHT_DP));
        this.mLoadMoreHelper.setLoadMoreEnable(true);
        this.mLoadMoreHelper.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void loadMore() {
                mLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isLoadMoreNull) {
                            mLoadMoreHelper.loadMoreComplete(false);
                        } else {
                            notifyMoodData(mShuffledData);
                            mLoadMoreHelper.loadMoreComplete(true);
                        }
                    }
                }, LOAD_MORE_DELAY_MS);
            }
        });
    }

    private RecyclerView.Adapter<RecyclerView.ViewHolder> createBaseAdapter() {
        return new RecyclerView.Adapter<RecyclerView.ViewHolder>() {

            @Override
            public int getItemViewType(int position) {
                return position == 0 ? 3 : 1;
            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                if (viewType == 3) {
                    return new RecyclerView.ViewHolder(LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.header_recycle_share, parent, false)) {
                    };
                }
                return new RecyclerView.ViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_recycle_mood_state, parent, false)) {
                };
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                if (getItemViewType(position) == 3) {
                    ((TextView) holder.itemView.findViewById(R.id.tv_header)).setText(
                            mType == TYPE_MOOD ? R.string.publish_mood : R.string.publish_state);
                    return;
                }
                final DbTemplate template = mData.get(holder.getAdapterPosition() - 1);
                MomentContentView contentView = (MomentContentView) holder.itemView.findViewById(R.id.tv_moment_content);
                contentView.setContext(mContext);
                contentView.setRichText(template.getResource(), template.getContent());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (SystemUtil.isFastDoubleClick()) {
                            LogUtil.i(TAG, "onClick: click too fast.");
                            return;
                        }
                        if (!NetworkUtils.isNetworkAvailable(MoodOrStateActivity.this)) {
                            ToastUtil.showShortCover(MoodOrStateActivity.this,
                                    getString(R.string.net_work_exception));
                            return;
                        }
                        DigitalManager.getInstance().clearDigitalEntity();
                        DigitalManager.getInstance().getDigitalEntity().startPushTime = SystemClock.elapsedRealtime();
                        DigitalManager.getInstance().getDigitalEntity().momentContent = template.getContent();
                        jumpToPublicActivity(mType, template);
                        LogUtil.d("moment", template.getContent());
                    }
                });
            }

            @Override
            public int getItemCount() {
                int size;
                if (mType < 0) {
                    size = PAGE_SIZE;
                } else {
                    if (mData == null) {
                        return 1;
                    }
                    size = mData.size();
                }
                return 1 + size;
            }

            private void jumpToPublicActivity(final int type, final DbTemplate template) {
                HandlerUtil.runOnBackground(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(MoodOrStateActivity.this, PushTextActivity.class);
                        intent.putExtra(Constants.INTENT_EXTRA_PUBLISH_TYPE, type);
                        intent.putExtra(Constants.INTENT_EXTRA_DB_TEMPLATE, JSONUtil.toJSON(template));
                        mContext.startActivity(intent);
                    }
                });
            }
        };
    }

    @Override
    public void finish() {
        this.leaveTime = System.currentTimeMillis();
        if (this.mType == TYPE_MOOD) {
            MomentBehavior.moodEnter(getApplication(), this.enterTime, this.leaveTime);
        } else if (this.mType == TYPE_STATE) {
            MomentBehavior.stateEnter(getApplication(), this.enterTime, this.leaveTime);
        }
        super.finish();
    }

    @Override
    public void loadSuccess(List<DbTemplate> templates) {
        LogUtil.i(TAG, "load mood data:" + (templates == null ? 0 : templates.size()));
        if (templates == null || templates.isEmpty()) {
            if (NetworkUtils.isNetworkAvailable(this)) {
                return;
            }
            ToastUtil.showNoConnected(this);
            return;
        }
        if (this.mType == TYPE_MOOD) {
            dealData(this.mShuffledData, templates);
            Collections.shuffle(this.mShuffledData);
            notifyMoodData(this.mShuffledData);
        } else if (this.mType == TYPE_STATE) {
            dealData(this.mData, templates);
            this.mRv.getAdapter().notifyDataSetChanged();
        }
    }

    /** Replaces the target list content with the freshly loaded templates. */
    private void dealData(List<DbTemplate> target, List<DbTemplate> templates) {
        if (target.isEmpty()) {
            target.addAll(templates);
        } else {
            target.clear();
            target.addAll(templates);
        }
    }

    private void notifyMoodData(List<DbTemplate> templates) {
        LogUtil.d("moment", "随机后的数据 —— data = " + templates);
        if (templates != null && templates.isEmpty()) {
            loadFail();
            return;
        }
        int end = this.offset + PAGE_SIZE;
        this.offset = end;
        int size = templates.size();
        if (end > size) {
            this.isLoadMoreNull = true;
            end = size;
        }
        this.mData.addAll(templates.subList(this.offset - PAGE_SIZE, end));
        LogUtil.i(TAG, "notifyMoodData size:" + this.mData.size());
        ClassicRecyclerAdapter adapter = (ClassicRecyclerAdapter) this.mRv.getAdapter();
        adapter.notifyItemRangeInsertedHF(adapter.getItemCountHF(), size);
    }

    @Override
    public void loadFail() {
        if (NetworkUtils.isNetworkAvailable(this)) {
            return;
        }
        ToastUtil.showNoConnected(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventData eventData) {
        LogUtil.i(TAG, "onEventMoodOrState data " + eventData);
        if (eventData.getType() == 5 && eventData.getData() != null
                && eventData.getData() instanceof DbMoment) {
            finish();
        }
    }
}