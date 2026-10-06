package com.xtc.moment.module.publish.location;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.module.bean.PoiBean;
import com.xtc.moment.module.widget.MomentContentView;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter of the location list: a header row followed by the currently resolved POI.
 */
public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationAdapterHolder> {

    private static final String TAG = LocationAdapter.class.getSimpleName();

    /** View type of the header row. */
    private static final int TYPE_HEADER = 3;
    /** View type of a location row. */
    private static final int TYPE_ITEM = 1;

    private Context mContext;
    private List<PoiBean> dataList;
    private OnItemClickListener onItemClickListener;

    /** Notified when a location row is tapped. */
    public interface OnItemClickListener {
        void onItemClick(String address);
    }

    public LocationAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? TYPE_HEADER : TYPE_ITEM;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @Override
    public LocationAdapterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_HEADER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_recycle_share, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycle_mood_state, parent, false);
        }
        return new LocationAdapterHolder(viewType, view);
    }

    @Override
    public void onBindViewHolder(LocationAdapterHolder holder, int position) {
        if (getItemViewType(position) == TYPE_HEADER) {
            holder.setHeadText(R.string.publish_location);
            return;
        }
        if (this.dataList == null || this.dataList.isEmpty()) {
            return;
        }
        final PoiBean poiBean = this.dataList.get(holder.getAdapterPosition() - 1);
        if (poiBean == null) {
            return;
        }
        holder.setAddress(poiBean.getAddressDesc());
        holder.momentContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(poiBean.getAddressDesc());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (this.dataList == null) {
            return 1;
        }
        return 1 + this.dataList.size();
    }

    /** Adds the resolved POI; only the first one is kept. */
    public void addDataItem(PoiBean poiBean) {
        if (poiBean == null) {
            return;
        }
        if (this.dataList == null) {
            this.dataList = new ArrayList<PoiBean>();
        }
        if (this.dataList.size() > 0) {
            return;
        }
        int position = this.dataList.size();
        this.dataList.add(poiBean);
        LogUtil.d(TAG, "dataList:" + this.dataList.toString());
        notifyItemRangeInserted(position + 1, 1);
    }

    public PoiBean getPublishContent() {
        if (this.dataList == null || this.dataList.isEmpty()) {
            return null;
        }
        return this.dataList.get(0);
    }

    /** View holder for both the header and the location rows. */
    class LocationAdapterHolder extends RecyclerView.ViewHolder {

        final int viewType;
        TextView headerTv;
        MomentContentView momentContentView;

        LocationAdapterHolder(int viewType, View itemView) {
            super(itemView);
            this.viewType = viewType;
            if (viewType == TYPE_HEADER) {
                this.headerTv = (TextView) itemView.findViewById(R.id.tv_header);
            } else {
                this.momentContentView = (MomentContentView) itemView.findViewById(R.id.tv_moment_content);
            }
        }

        void setHeadText(int resId) {
            if (this.headerTv != null) {
                this.headerTv.setText(resId);
            }
        }

        void setAddress(String address) {
            if (this.momentContentView != null) {
                this.momentContentView.setContext(mContext);
                this.momentContentView.setRichText(R.drawable.location, address);
            }
        }
    }
}