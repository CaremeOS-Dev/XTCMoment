package com.xtc.ui.widget.refreshview.difviewhandler;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.xtc.log.LogUtil;
import java.util.ArrayList;
import java.util.List;

/** 为 RecyclerView 适配器补充页眉/页脚能力的包装适配器。 */
public class RecyclerAdapterWithHF extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int TYPE_FOOTER = 7899;
    public static final int TYPE_HEADER = 7898;
    public static final int TYPE_MANAGER_GRID = 2;
    public static final int TYPE_MANAGER_LINEAR = 1;
    public static final int TYPE_MANAGER_OTHER = 0;
    public static final int TYPE_MANAGER_STAGGERED_GRID = 3;
    private RecyclerView.Adapter<RecyclerView.ViewHolder> mAdapter;
    private int mManagerType;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    private List<View> mHeaders = new ArrayList<>();
    private List<View> mFooters = new ArrayList<>();
    private RecyclerView.AdapterDataObserver adapterDataObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            RecyclerAdapterWithHF.this.notifyDataSetChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            RecyclerAdapterWithHF.this.notifyItemRangeChanged(positionStart + RecyclerAdapterWithHF.this.getHeadSize(), itemCount);
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            RecyclerAdapterWithHF.this.notifyItemRangeInserted(positionStart + RecyclerAdapterWithHF.this.getHeadSize(), itemCount);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            RecyclerAdapterWithHF.this.notifyItemRangeRemoved(positionStart + RecyclerAdapterWithHF.this.getHeadSize(), itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            RecyclerAdapterWithHF.this.notifyItemMoved(fromPosition + RecyclerAdapterWithHF.this.getHeadSize(),
                    toPosition + RecyclerAdapterWithHF.this.getHeadSize());
        }
    };

    /** 条目点击回调。 */
    public interface OnItemClickListener {
        void onItemClick(RecyclerAdapterWithHF adapter, RecyclerView.ViewHolder holder, int position);
    }

    /** 条目长按回调。 */
    public interface OnItemLongClickListener {
        void onItemLongClick(RecyclerAdapterWithHF adapter, RecyclerView.ViewHolder holder, int position);
    }

    protected void onItemClick(RecyclerView.ViewHolder holder, int position) {
    }

    protected void onItemLongClick(RecyclerView.ViewHolder holder, int position) {
    }

    public int getHeadSize() {
        return this.mHeaders.size();
    }

    public int getFootSize() {
        return this.mFooters.size();
    }

    public int getManagerType() {
        return this.mManagerType;
    }

    public void notifyDataSetChangedHF() {
        notifyDataSetChanged();
    }

    public void notifyItemChangedHF(int position) {
        notifyItemChanged(getRealPosition(position));
    }

    public void notifyItemMovedHF(int fromPosition, int toPosition) {
        notifyItemMovedHF(getRealPosition(fromPosition), getRealPosition(toPosition));
    }

    public void notifyItemRangeChangedHF(int positionStart, int itemCount) {
        notifyItemRangeChanged(getRealPosition(positionStart), itemCount);
    }

    public void notifyItemRangeRemovedHF(int positionStart, int itemCount) {
        notifyItemRangeRemoved(getRealPosition(positionStart), itemCount);
    }

    public void notifyItemRemovedHF(int position) {
        notifyItemRemoved(getRealPosition(position));
    }

    public void notifyItemInsertedHF(int position) {
        notifyItemInserted(getRealPosition(position));
    }

    public void notifyItemRangeInsertedHF(int positionStart, int itemCount) {
        notifyItemRangeInserted(getRealPosition(positionStart), itemCount);
    }

    @Override
    public final long getItemId(int position) {
        return getItemIdHF(getRealPosition(position));
    }

    public long getItemIdHF(int position) {
        return this.mAdapter.getItemId(position);
    }

    public RecyclerView.ViewHolder onCreateViewHolderHF(ViewGroup parent, int viewType) {
        return this.mAdapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType != 7898 && viewType != 7899) {
            return onCreateViewHolderHF(parent, viewType);
        }
        FrameLayout frameLayout = new FrameLayout(parent.getContext());
        frameLayout.setLayoutParams(new ViewGroup.LayoutParams(-1, -2));
        return new HeaderFooterViewHolder(frameLayout);
    }

    @Override
    public final void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (isHeader(position)) {
            prepareHeaderFooter((HeaderFooterViewHolder) holder, this.mHeaders.get(position));
        } else if (isFooter(position)) {
            prepareHeaderFooter((HeaderFooterViewHolder) holder, this.mFooters.get((position - getItemCountHF()) - this.mHeaders.size()));
        } else {
            holder.itemView.setOnClickListener(new MyOnClickListener(holder));
            holder.itemView.setOnLongClickListener(new MyOnLongClickListener(holder));
            onBindViewHolderHF(holder, getRealPosition(position));
        }
    }

    public int getRealPosition(int position) {
        return position - this.mHeaders.size();
    }

    public void onBindViewHolderHF(RecyclerView.ViewHolder holder, int position) {
        this.mAdapter.onBindViewHolder(holder, position);
    }

    private void prepareHeaderFooter(HeaderFooterViewHolder holder, View view) {
        if (this.mManagerType == 3) {
            StaggeredGridLayoutManager.LayoutParams layoutParams = new StaggeredGridLayoutManager.LayoutParams(-1, -1);
            layoutParams.setFullSpan(true);
            holder.itemView.setLayoutParams(layoutParams);
        }
        if (view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
        holder.base.removeAllViews();
        holder.base.addView(view);
    }

    private boolean isHeader(int position) {
        return position < this.mHeaders.size();
    }

    private boolean isFooter(int position) {
        return position >= this.mHeaders.size() + getItemCountHF();
    }

    @Override
    public final int getItemCount() {
        return this.mHeaders.size() + getItemCountHF() + this.mFooters.size();
    }

    public int getItemCountHF() {
        return this.mAdapter.getItemCount();
    }

    @Override
    public final int getItemViewType(int position) {
        if (isHeader(position)) {
            return 7898;
        }
        if (isFooter(position)) {
            return 7899;
        }
        int itemViewTypeHF = getItemViewTypeHF(getRealPosition(position));
        if (itemViewTypeHF == 7898 || itemViewTypeHF == 7899) {
            throw new IllegalArgumentException("Item type cannot equal 7898 or 7899");
        }
        return itemViewTypeHF;
    }

    public int getItemViewTypeHF(int position) {
        return this.mAdapter.getItemViewType(position);
    }

    public void addHeader(View view) {
        if (this.mHeaders.contains(view)) {
            return;
        }
        this.mHeaders.add(view);
        notifyItemInserted(this.mHeaders.size() - 1);
    }

    public void removeHeader(View view) {
        if (this.mHeaders.contains(view)) {
            notifyItemRemoved(this.mHeaders.indexOf(view));
            this.mHeaders.remove(view);
        }
    }

    public void addFooter(View view) {
        if (this.mFooters.contains(view)) {
            return;
        }
        this.mFooters.add(view);
        StringBuilder builder = new StringBuilder();
        builder.append("mHeaders.size()---->>");
        builder.append(this.mHeaders.size());
        builder.append(",");
        builder.append(getItemCountHF());
        builder.append(",");
        builder.append(this.mFooters.size() - 1);
        LogUtil.d("test", builder.toString());
        notifyItemInserted(((this.mHeaders.size() + getItemCountHF()) + this.mFooters.size()) - 1);
    }

    public void removeFooter(View view) {
        if (this.mFooters.contains(view)) {
            notifyItemRemoved(this.mHeaders.size() + getItemCountHF() + this.mFooters.indexOf(view));
            this.mFooters.remove(view);
        }
    }

    /** 页眉/页脚占位 ViewHolder。 */
    public static class HeaderFooterViewHolder extends RecyclerView.ViewHolder {
        FrameLayout base;

        public HeaderFooterViewHolder(View view) {
            super(view);
            this.base = (FrameLayout) view;
        }
    }

    public OnItemClickListener getOnItemClickListener() {
        return this.onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
        LogUtil.d("eeee", "setOnItemClickListener " + this.onItemClickListener);
    }

    public OnItemLongClickListener getOnItemLongClickListener() {
        return this.onItemLongClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.onItemLongClickListener = listener;
    }

    /** 条目点击监听。 */
    private class MyOnClickListener implements View.OnClickListener {
        private RecyclerView.ViewHolder vh;

        public MyOnClickListener(RecyclerView.ViewHolder holder) {
            this.vh = holder;
        }

        @Override
        public void onClick(View view) {
            int realPosition = RecyclerAdapterWithHF.this.getRealPosition(this.vh.getLayoutPosition());
            if (RecyclerAdapterWithHF.this.onItemClickListener != null) {
                RecyclerAdapterWithHF.this.onItemClickListener.onItemClick(RecyclerAdapterWithHF.this, this.vh, realPosition);
            }
            RecyclerAdapterWithHF.this.onItemClick(this.vh, realPosition);
        }
    }

    /** 条目长按监听。 */
    private class MyOnLongClickListener implements View.OnLongClickListener {
        private RecyclerView.ViewHolder vh;

        public MyOnLongClickListener(RecyclerView.ViewHolder holder) {
            this.vh = holder;
        }

        @Override
        public boolean onLongClick(View view) {
            int realPosition = RecyclerAdapterWithHF.this.getRealPosition(this.vh.getLayoutPosition());
            if (RecyclerAdapterWithHF.this.onItemLongClickListener != null) {
                RecyclerAdapterWithHF.this.onItemLongClickListener.onItemLongClick(RecyclerAdapterWithHF.this, this.vh, realPosition);
            }
            RecyclerAdapterWithHF.this.onItemLongClick(this.vh, realPosition);
            return true;
        }
    }

    public RecyclerAdapterWithHF(RecyclerView.Adapter<RecyclerView.ViewHolder> adapter) {
        this.mAdapter = adapter;
        adapter.registerAdapterDataObserver(this.adapterDataObserver);
    }
}