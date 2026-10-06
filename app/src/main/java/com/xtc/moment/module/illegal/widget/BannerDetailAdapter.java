package com.xtc.moment.module.illegal.widget;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xtc.database.ormlite.CollectionUtil;
import com.xtc.log.LogUtil;
import com.xtc.moment.MomentApp;
import com.xtc.moment.R;
import com.xtc.moment.module.adapter.BannerContentAdapter;
import com.xtc.moment.module.illegal.handler.IllegalMessageHandler;
import com.xtc.moment.module.illegal.net.IllegalHttpProxy;
import com.xtc.moment.module.illegal.net.bean.request.BannerNetBean;
import com.xtc.moment.module.illegal.net.bean.request.HighRiskRequestBean;
import com.xtc.moment.module.illegal.util.ConfigTimeFormatUtil;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 违规详情弹窗的列表适配器：顶部说明、敏感内容列表、影响说明与时间说明。
 */
public class BannerDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "BannerDetailAdapter";

    private static final int PAGE_SIZE = 10;
    private static final int BANNER_TIME_TYPE = 253;
    private static final int BANNER_EFFECT_TYPE = 254;
    private static final int CONTENT_TYPE = 256;
    private static final int TOP_TYPE = 7898;
    private static final int FOOTER_TYPE = 4;
    private static final int ERROR_TYPE = -1;

    private BannerNetBean bannerNetBeans;
    private Context context;
    private long endTime;
    private BannerContentAdapter contentAdapter;
    private boolean isLoading = false;
    private int currentPage = 1;

    List<Integer> myListType = new ArrayList<>();

    public BannerDetailAdapter(BannerNetBean bannerNetBeans, Context context) {
        this.bannerNetBeans = bannerNetBeans;
        this.context = context;
    }

    public BannerDetailAdapter(BannerNetBean bannerNetBeans, Context context, long endTime) {
        this.bannerNetBeans = bannerNetBeans;
        this.endTime = endTime;
        this.context = context;
    }

    public void initData() {
        if (!noContentData()) {
            this.myListType.add(TOP_TYPE);
            this.myListType.add(CONTENT_TYPE);
        }
        this.myListType.add(BANNER_EFFECT_TYPE);
        this.myListType.add(BANNER_TIME_TYPE);
        this.myListType.add(FOOTER_TYPE);
    }

    public void setBannerNetBeans(BannerNetBean bannerNetBeans) {
        this.bannerNetBeans = bannerNetBeans;
        initData();
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context itemContext = parent.getContext();
        if (viewType == FOOTER_TYPE) {
            return new FooterViewHolder(LayoutInflater.from(itemContext)
                    .inflate(R.layout.item_banner_footer, parent, false));
        }
        if (viewType == TOP_TYPE) {
            return new TopViewHolder(LayoutInflater.from(itemContext)
                    .inflate(R.layout.item_banner_top, parent, false));
        }
        if (viewType == CONTENT_TYPE) {
            return new ContentViewHolder(LayoutInflater.from(itemContext)
                    .inflate(R.layout.item_banner_detail_content, parent, false));
        }
        if (viewType == BANNER_EFFECT_TYPE) {
            return new BannerEffectViewHolder(LayoutInflater.from(itemContext)
                    .inflate(R.layout.item_banner_effect, parent, false));
        }
        return new BannerTimeViewHolder(LayoutInflater.from(itemContext)
                .inflate(R.layout.item_banner_time, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int itemViewType = getItemViewType(position);
        LogUtil.d(TAG, "onBindViewHolder: position=" + position + " viewType=" + itemViewType);
        if (itemViewType == TOP_TYPE) {
            TopViewHolder topHolder = (TopViewHolder) holder;
            topHolder.tvReasonBanner.setText(R.string.banner_reason);
            topHolder.tvSendNext.setText(R.string.you_send_sensitive_content);
            if (noContentData()) {
                topHolder.tvReasonBanner.setVisibility(View.GONE);
                topHolder.tvSendNext.setVisibility(View.GONE);
            } else {
                topHolder.tvReasonBanner.setVisibility(View.VISIBLE);
                topHolder.tvSendNext.setVisibility(View.VISIBLE);
            }
            return;
        }
        if (itemViewType == CONTENT_TYPE) {
            ContentViewHolder contentHolder = (ContentViewHolder) holder;
            LogUtil.d(TAG, "敏感内容bannerNetBeans：" + this.bannerNetBeans.getContentVos());
            this.contentAdapter = new BannerContentAdapter(this.bannerNetBeans.getContentVos(), this.context);
            contentHolder.recyclerView.setLayoutManager(new LinearLayoutManager(this.context));
            contentHolder.recyclerView.setAdapter(this.contentAdapter);
            setLoadMoreListener(contentHolder.recyclerView);
            if (noContentData()) {
                contentHolder.recyclerView.setVisibility(View.GONE);
            } else {
                contentHolder.recyclerView.setVisibility(View.VISIBLE);
            }
            return;
        }
        if (itemViewType == BANNER_EFFECT_TYPE) {
            BannerEffectViewHolder effectHolder = (BannerEffectViewHolder) holder;
            effectHolder.tvEffectBanner.setText(R.string.banner_effect);
            effectHolder.tvTestEffectBanner.setText(R.string.banner_detail_hint);
        } else if (itemViewType == BANNER_TIME_TYPE) {
            BannerTimeViewHolder timeHolder = (BannerTimeViewHolder) holder;
            timeHolder.tvTimeBanner.setText(R.string.banner_time);
            timeHolder.tvTestTiemeBanner.setText(this.context.getResources().getString(
                    R.string.banner_detail_time_hint,
                    ConfigTimeFormatUtil.formatDisableSendExpireTime(this.context, this.endTime)));
        } else if (itemViewType == FOOTER_TYPE) {
            // 底部占位，无需绑定数据。
        }
    }

    private void setLoadMoreListener(final RecyclerView recyclerView) {
        if (!IllegalMessageHandler.getInstance(this.context).isHighRiskIllegal()) {
            return;
        }
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (layoutManager.getChildCount() <= 0 || newState != RecyclerView.SCROLL_STATE_IDLE
                        || recyclerView.getChildLayoutPosition(
                                recyclerView.getChildAt(recyclerView.getChildCount() - 1))
                                < layoutManager.getItemCount() - 1) {
                    return;
                }
                LogUtil.d(TAG, "加载更多 —— 正在加载");
                if (BannerDetailAdapter.this.isLoading) {
                    return;
                }
                BannerDetailAdapter.this.loadMoreHighRiskData();
            }
        });
    }

    private void loadMoreHighRiskData() {
        this.isLoading = true;
        IllegalHttpProxy httpProxy = new IllegalHttpProxy(this.context);
        HighRiskRequestBean requestBean = new HighRiskRequestBean(MomentApp.getWatchId());
        requestBean.setPageSize(PAGE_SIZE);
        int nextPage = this.currentPage + 1;
        this.currentPage = nextPage;
        requestBean.setPageNum(nextPage);
        httpProxy.getHighRiskBannerContent(requestBean)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BannerNetBean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        BannerDetailAdapter.this.isLoading = false;
                        LogUtil.e(TAG, "getHighRiskBannerContent error", throwable);
                    }

                    @Override
                    public void onNext(BannerNetBean bannerNetBean) {
                        BannerDetailAdapter.this.isLoading = false;
                        if (bannerNetBean == null || CollectionUtil.isEmpty(bannerNetBean.getContentVos())) {
                            return;
                        }
                        BannerDetailAdapter.this.contentAdapter.addDatas(bannerNetBean.getContentVos());
                    }
                });
    }

    @Override
    public int getItemCount() {
        return this.myListType.size();
    }

    private boolean noContentData() {
        BannerNetBean bannerNetBean = this.bannerNetBeans;
        return bannerNetBean == null || CollectionUtil.isEmpty(bannerNetBean.getContentVos());
    }

    @Override
    public int getItemViewType(int position) {
        return this.myListType.get(position).intValue();
    }

    private class TopViewHolder extends RecyclerView.ViewHolder {

        public TextView tvReasonBanner;
        public TextView tvSendNext;

        public TopViewHolder(View itemView) {
            super(itemView);
            this.tvReasonBanner = (TextView) itemView.findViewById(R.id.reason_banner);
            this.tvSendNext = (TextView) itemView.findViewById(R.id.send_sensitive_content);
        }
    }

    private class FooterViewHolder extends RecyclerView.ViewHolder {

        public FooterViewHolder(View itemView) {
            super(itemView);
        }
    }

    private class ContentViewHolder extends RecyclerView.ViewHolder {

        public RecyclerView recyclerView;

        public ContentViewHolder(View itemView) {
            super(itemView);
            this.recyclerView = (RecyclerView) itemView.findViewById(R.id.recyclerView);
        }
    }

    private class BannerEffectViewHolder extends RecyclerView.ViewHolder {

        public TextView tvEffectBanner;
        public TextView tvTestEffectBanner;

        public BannerEffectViewHolder(View itemView) {
            super(itemView);
            this.tvEffectBanner = (TextView) itemView.findViewById(R.id.effect_banner);
            this.tvTestEffectBanner = (TextView) itemView.findViewById(R.id.banner_effect_text);
        }
    }

    private class BannerTimeViewHolder extends RecyclerView.ViewHolder {

        public TextView tvTimeBanner;
        public TextView tvTestTiemeBanner;

        public BannerTimeViewHolder(View itemView) {
            super(itemView);
            this.tvTimeBanner = (TextView) itemView.findViewById(R.id.time_banner);
            this.tvTestTiemeBanner = (TextView) itemView.findViewById(R.id.banner_time_text);
        }
    }
}