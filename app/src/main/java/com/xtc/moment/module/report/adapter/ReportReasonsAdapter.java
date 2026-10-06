package com.xtc.moment.module.report.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xtc.moment.R;
import com.xtc.moment.module.report.interfaces.OnItemClickListener;
import com.xtc.ui.widget.button.LongSolidButton;
import com.xtc.ui.widget.scalablecontainer.AppRelativeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 举报原因列表适配器，第 0 项为头部说明。
 */
public class ReportReasonsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_HEADER = 1;

    private Context context;
    private List<String> dataString = new ArrayList<>();
    private View mHeaderView;
    private OnItemClickListener<String> onItemClickListener;

    public ReportReasonsAdapter(Context context) {
        this.context = context;
    }

    public void setDataString(List<String> dataString) {
        this.dataString = dataString;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener<String> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setmHeaderView(View headerView) {
        this.mHeaderView = headerView;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? TYPE_HEADER : TYPE_NORMAL;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            return new HeaderViewHolder(this.mHeaderView);
        }
        return new ReasonsViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_report_reasons, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (!(holder instanceof ReasonsViewHolder)) {
            return;
        }
        final ReasonsViewHolder reasonsHolder = (ReasonsViewHolder) holder;
        reasonsHolder.rlLayout.setBackgroundResource(R.drawable.item_backgroud_grey_report);
        reasonsHolder.tvReasons.setTextColor(this.context.getResources().getColor(R.color.reasons_color_check));
        reasonsHolder.tvReasons.setText(this.dataString.get(position - 1));
        reasonsHolder.rlLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ReportReasonsAdapter.this.onItemClickListener != null) {
                    int itemPosition = reasonsHolder.getAdapterPosition() - 1;
                    ReportReasonsAdapter.this.onItemClickListener
                            .onItemClick(ReportReasonsAdapter.this.dataString.get(itemPosition), itemPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.dataString.size() + 1;
    }

    class ReasonsViewHolder extends RecyclerView.ViewHolder {

        private TextView tvReasons;
        private AppRelativeLayout rlLayout;

        ReasonsViewHolder(View itemView) {
            super(itemView);
            this.tvReasons = (TextView) itemView.findViewById(R.id.tv_report_reasons_item);
            this.rlLayout = (AppRelativeLayout) itemView.findViewById(R.id.rl_report_reasons);
        }
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {

        HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    private class FooterViewHolder extends RecyclerView.ViewHolder {

        private LongSolidButton btnSuccess;

        FooterViewHolder(View itemView) {
            super(itemView);
            this.btnSuccess = (LongSolidButton) this.itemView.findViewById(R.id.btn_report_seasons);
            this.btnSuccess.getTv().setText(R.string.sure);
        }
    }
}