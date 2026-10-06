package com.xtc.aitext.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xtc.aitext.R;
import com.xtc.aitext.bean.AIRecordDetailBean;
import com.xtc.aitext.util.AIModuleUtil;
import com.xtc.database.ormlite.CollectionUtil;
import com.xtc.log.LogUtil;
import com.xtc.utils_screenshot_carry_data.ScreenshotUtils;

import java.util.List;

/**
 * AI 创作记录列表适配器，包含标题、内容与页脚三种条目。
 */
public class AIRecordAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "ai_text_AIRecordAdapter";

    private static final int TYPE_TITLE = 0;
    private static final int TYPE_CONTENT = 1;
    private static final int TYPE_FOOT = 2;

    private final Context context;
    private final List<AIRecordDetailBean> recordList;
    private boolean needFoot;
    private ClickItem clickItem;

    /** 条目点击回调。 */
    public interface ClickItem {
        void onItemClick(AIRecordDetailBean recordBean);
    }

    public AIRecordAdapter(Context context, List<AIRecordDetailBean> recordList) {
        this.context = context;
        this.recordList = recordList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_TITLE) {
            return new TitleViewHolder(LayoutInflater.from(context).inflate(R.layout.item_record_title, parent, false));
        }
        if (viewType == TYPE_FOOT) {
            return new FootViewHolder(LayoutInflater.from(context).inflate(R.layout.item_record_foot, parent, false));
        }
        return new ContentViewHolder(LayoutInflater.from(context).inflate(R.layout.item_record, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ContentViewHolder) {
            ((ContentViewHolder) holder).bind(recordList.get(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_TITLE;
        }
        return (needFoot && position == recordList.size() - 1) ? TYPE_FOOT : TYPE_CONTENT;
    }

    /** 刷新数据。 */
    public void refreshData(List<AIRecordDetailBean> list, boolean needFoot) {
        LogUtil.d(TAG, "refreshData: isNeedFoot = " + needFoot);
        this.needFoot = needFoot;
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        int startPosition;
        int itemCount;
        if (CollectionUtil.isEmpty(recordList)) {
            recordList.add(new AIRecordDetailBean());
            recordList.addAll(list);
            startPosition = 0;
            itemCount = recordList.size();
        } else {
            startPosition = recordList.size();
            itemCount = list.size();
            recordList.addAll(list);
        }
        if (needFoot) {
            recordList.add(new AIRecordDetailBean());
            itemCount++;
        }
        notifyItemRangeChanged(startPosition, itemCount);
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }

    public void setClickItem(ClickItem clickItem) {
        this.clickItem = clickItem;
    }

    /**
     * 标题条目。
     */
    class TitleViewHolder extends RecyclerView.ViewHolder {
        TitleViewHolder(View itemView) {
            super(itemView);
        }
    }

    /**
     * 页脚条目。
     */
    class FootViewHolder extends RecyclerView.ViewHolder {
        FootViewHolder(View itemView) {
            super(itemView);
        }
    }

    /**
     * 内容条目。
     */
    class ContentViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvTitle;
        private final TextView tvContent;
        private AIRecordDetailBean recordBean;

        ContentViewHolder(View itemView) {
            super(itemView);
            this.tvTitle = itemView.findViewById(R.id.tv_title);
            this.tvContent = itemView.findViewById(R.id.tv_content);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (AIRecordAdapter.this.clickItem != null) {
                        AIRecordAdapter.this.clickItem.onItemClick(recordBean);
                    }
                }
            });
        }

        void bind(AIRecordDetailBean recordBean) {
            this.recordBean = recordBean;
            String styleName = recordBean.getAiStyleTextName();
            String createTime = recordBean.getCreateTime();
            String requestText = recordBean.getRequestText();
            try {
                this.tvTitle.setText(styleName + ScreenshotUtils.SEPARATOR + AIModuleUtil.formatMonthDay(Long.parseLong(createTime)));
            } catch (Exception e) {
                this.tvTitle.setText(styleName);
            }
            this.tvContent.setText(requestText);
        }
    }
}