package com.xtc.moment.module.publish.location;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.module.bean.PoiBean;
import com.xtc.utils.ui.DimenUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter of the POI picker: a header, the "do not show location" row, the resolved POIs and a
 * trailing spacer.
 */
public class PoiAdapter extends RecyclerView.Adapter<PoiAdapter.LocationAdapterHolder> {

    private static final String TAG = PoiAdapter.class.getSimpleName();

    /** View type of the header row. */
    private static final int TYPE_HEADER = 3;
    /** View type of the trailing spacer. */
    private static final int TYPE_SPACER = 4;
    /** View type of a POI row. */
    private static final int TYPE_POI = 1;
    /** Height of the trailing spacer, in dp. */
    private static final float SPACER_HEIGHT_DP = 42.0f;

    private final Context mContext;
    private final PoiBean pendingPoi;
    private List<ItemPoi> dataList;

    public PoiAdapter(Context context, PoiBean poiBean) {
        this.mContext = context;
        this.pendingPoi = poiBean;
    }

    @Override
    public LocationAdapterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_HEADER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_recycle_share, parent, false);
        } else if (viewType == TYPE_SPACER) {
            view = new View(parent.getContext());
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    DimenUtil.dp2px(mContext, SPACER_HEIGHT_DP)));
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_publish_poi, parent, false);
        }
        return new LocationAdapterHolder(viewType, view);
    }

    @Override
    public void onBindViewHolder(final LocationAdapterHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == TYPE_HEADER) {
            holder.setHeadText(R.string.publish_lbs);
            return;
        }
        if (viewType != TYPE_POI || this.dataList == null || this.dataList.isEmpty()) {
            return;
        }
        final ItemPoi itemPoi = this.dataList.get(position - 1);
        if (itemPoi == null) {
            return;
        }
        holder.setAddress(itemPoi.text);
        holder.setIvChecked(itemPoi.checked);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemPoi.checked) {
                    return;
                }
                for (int i = 0; i < dataList.size(); i++) {
                    ItemPoi other = dataList.get(i);
                    if (other.checked) {
                        other.checked = false;
                        notifyItemChanged(i + 1);
                        break;
                    }
                }
                itemPoi.checked = true;
                notifyItemChanged(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        if (this.dataList == null) {
            return 1;
        }
        return this.dataList.size() + 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        }
        return position == this.dataList.size() + 1 ? TYPE_SPACER : TYPE_POI;
    }

    /** Replaces the POI list; only the first call has an effect. */
    public void setDataItem(List<PoiBean> poiList) {
        if (poiList == null) {
            return;
        }
        if (this.dataList == null) {
            this.dataList = new ArrayList<ItemPoi>();
        }
        if (this.dataList.size() > 0) {
            return;
        }
        String pendingAddress = this.pendingPoi != null ? this.pendingPoi.getAddressDesc() : null;
        boolean pendingMatched = false;
        for (PoiBean poiBean : poiList) {
            String address = poiBean.getAddressDesc();
            if (TextUtils.isEmpty(address)) {
                address = "";
            }
            if (address.equals(pendingAddress)) {
                pendingMatched = true;
            }
            this.dataList.add(new ItemPoi(address, ItemPoi.TYPE_POI, poiBean, address.equals(pendingAddress)));
        }
        this.dataList.add(0, new ItemPoi(this.mContext.getString(R.string.dont_show_location),
                ItemPoi.TYPE_NONE, null, !pendingMatched));
        LogUtil.d(TAG, "dataList:" + this.dataList.toString());
        notifyDataSetChanged();
    }

    public PoiBean getCheckedPoi() {
        if (this.dataList == null) {
            return null;
        }
        for (ItemPoi itemPoi : this.dataList) {
            if (itemPoi.checked) {
                return itemPoi.poiBean;
            }
        }
        return null;
    }

    /** View holder for the header, POI rows and the spacer. */
    static class LocationAdapterHolder extends RecyclerView.ViewHolder {

        final int viewType;
        TextView headerTv;
        ImageView ivChecked;
        TextView tvPoi;

        LocationAdapterHolder(int viewType, View itemView) {
            super(itemView);
            this.viewType = viewType;
            if (viewType == TYPE_HEADER) {
                this.headerTv = (TextView) itemView.findViewById(R.id.tv_header);
            } else if (viewType == TYPE_POI) {
                this.tvPoi = (TextView) itemView.findViewById(R.id.tv_poi);
                this.ivChecked = (ImageView) itemView.findViewById(R.id.iv_checked);
            }
        }

        void setHeadText(int resId) {
            if (this.headerTv != null) {
                this.headerTv.setText(resId);
            }
        }

        void setIvChecked(boolean checked) {
            this.ivChecked.setVisibility(checked ? View.VISIBLE : View.GONE);
        }

        void setAddress(String address) {
            if (this.tvPoi != null) {
                this.tvPoi.setText(address);
            }
        }
    }

    /** One row of the POI list. */
    private static class ItemPoi {

        static final int TYPE_NONE = 0;
        static final int TYPE_POI = 1;

        String text;
        int type;
        PoiBean poiBean;
        boolean checked;

        ItemPoi(String text, int type, PoiBean poiBean, boolean checked) {
            this.text = text;
            this.type = type;
            this.poiBean = poiBean;
            this.checked = checked;
        }
    }
}