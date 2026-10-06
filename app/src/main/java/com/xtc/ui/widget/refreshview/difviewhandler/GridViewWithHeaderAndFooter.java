package com.xtc.ui.widget.refreshview.difviewhandler;

import android.content.Context;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.WrapperListAdapter;
import com.xtc.log.LogUtil;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;

/** 支持页眉/页脚的 GridView，内部通过包装适配器实现整行占位。 */
public class GridViewWithHeaderAndFooter extends GridView {
    public static boolean DEBUG = false;
    private static final String LOG_TAG = "GridViewHeaderAndFooter";
    private ArrayList<FixedViewInfo> mFooterViewInfos;
    private ArrayList<FixedViewInfo> mHeaderViewInfos;
    private ItemClickHandler mItemClickHandler;
    private int mNumColumns;
    private AdapterView.OnItemClickListener mOnItemClickListener;
    private AdapterView.OnItemLongClickListener mOnItemLongClickListener;
    private ListAdapter mOriginalAdapter;
    private int mRowHeight;
    private View mViewForMeasureRowHeight;

    private void initHeaderGridView() {
    }

    @Override
    public void setClipChildren(boolean clipChildren) {
    }

    /** 页眉/页脚的信息载体。 */
    private static class FixedViewInfo {
        public Object data;
        public boolean isSelectable;
        public View view;
        public ViewGroup viewContainer;

        private FixedViewInfo() {
        }
    }

    public GridViewWithHeaderAndFooter(Context context) {
        super(context);
        this.mNumColumns = -1;
        this.mViewForMeasureRowHeight = null;
        this.mRowHeight = -1;
        this.mHeaderViewInfos = new ArrayList<>();
        this.mFooterViewInfos = new ArrayList<>();
        initHeaderGridView();
    }

    public GridViewWithHeaderAndFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mNumColumns = -1;
        this.mViewForMeasureRowHeight = null;
        this.mRowHeight = -1;
        this.mHeaderViewInfos = new ArrayList<>();
        this.mFooterViewInfos = new ArrayList<>();
        initHeaderGridView();
    }

    public GridViewWithHeaderAndFooter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mNumColumns = -1;
        this.mViewForMeasureRowHeight = null;
        this.mRowHeight = -1;
        this.mHeaderViewInfos = new ArrayList<>();
        this.mFooterViewInfos = new ArrayList<>();
        initHeaderGridView();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        ListAdapter adapter = getAdapter();
        if (adapter == null || !(adapter instanceof HeaderViewGridAdapter)) {
            return;
        }
        HeaderViewGridAdapter headerViewGridAdapter = (HeaderViewGridAdapter) adapter;
        headerViewGridAdapter.setNumColumns(getNumColumnsCompatible());
        headerViewGridAdapter.setRowHeight(getRowHeight());
    }

    public void setClipChildrenSupper(boolean clipChildren) {
        super.setClipChildren(false);
    }

    public void addHeaderView(View view) {
        addHeaderView(view, null, true);
    }

    public void addHeaderView(View view, Object data, boolean isSelectable) {
        ListAdapter adapter = getAdapter();
        if (adapter != null && !(adapter instanceof HeaderViewGridAdapter)) {
            throw new IllegalStateException("Cannot add header view to grid -- setAdapter has already been called.");
        }
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        FixedViewInfo info = new FixedViewInfo();
        FullWidthFixedViewLayout fullWidthLayout = new FullWidthFixedViewLayout(getContext());
        if (layoutParams != null) {
            view.setLayoutParams(new FrameLayout.LayoutParams(layoutParams.width, layoutParams.height));
            fullWidthLayout.setLayoutParams(new AbsListView.LayoutParams(layoutParams.width, layoutParams.height));
        }
        fullWidthLayout.addView(view);
        info.view = view;
        info.viewContainer = fullWidthLayout;
        info.data = data;
        info.isSelectable = isSelectable;
        this.mHeaderViewInfos.add(info);
        if (adapter != null) {
            ((HeaderViewGridAdapter) adapter).notifyDataSetChanged();
        }
    }

    public void addFooterView(View view) {
        addFooterView(view, null, true);
    }

    public void addFooterView(View view, Object data, boolean isSelectable) {
        ListAdapter adapter = getAdapter();
        if (adapter != null) {
            boolean isHeaderViewGridAdapter = adapter instanceof HeaderViewGridAdapter;
        }
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        FixedViewInfo info = new FixedViewInfo();
        FullWidthFixedViewLayout fullWidthLayout = new FullWidthFixedViewLayout(getContext());
        if (layoutParams != null) {
            view.setLayoutParams(new FrameLayout.LayoutParams(layoutParams.width, layoutParams.height));
            fullWidthLayout.setLayoutParams(new AbsListView.LayoutParams(layoutParams.width, layoutParams.height));
        }
        if (view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
        fullWidthLayout.addView(view);
        info.view = view;
        info.viewContainer = fullWidthLayout;
        info.data = data;
        info.isSelectable = isSelectable;
        this.mFooterViewInfos.add(info);
    }

    public int getHeaderViewCount() {
        return this.mHeaderViewInfos.size();
    }

    public int getFooterViewCount() {
        return this.mFooterViewInfos.size();
    }

    public boolean removeHeaderView(View view) {
        boolean removed = false;
        if (this.mHeaderViewInfos.size() > 0) {
            ListAdapter adapter = getAdapter();
            if (adapter != null && ((HeaderViewGridAdapter) adapter).removeHeader(view)) {
                removed = true;
            }
            removeFixedViewInfo(view, this.mHeaderViewInfos);
        }
        return removed;
    }

    public boolean removeFooterView(View view) {
        ListAdapter adapter;
        return this.mFooterViewInfos.size() > 0 && (adapter = getAdapter()) != null
                && ((HeaderViewGridAdapter) adapter).removeFooter(view);
    }

    private void removeFixedViewInfo(View view, ArrayList<FixedViewInfo> viewInfos) {
        int size = viewInfos.size();
        for (int index = 0; index < size; index++) {
            if (viewInfos.get(index).view == view) {
                viewInfos.remove(index);
                return;
            }
        }
    }

    private int getNumColumnsCompatible() {
        if (Build.VERSION.SDK_INT >= 11) {
            return super.getNumColumns();
        }
        try {
            Field field = GridView.class.getDeclaredField("mNumColumns");
            field.setAccessible(true);
            return field.getInt(this);
        } catch (Exception unused) {
            int numColumns = this.mNumColumns;
            if (numColumns != -1) {
                return numColumns;
            }
            throw new RuntimeException("Can not determine the mNumColumns for this API platform, please call setNumColumns to set it.");
        }
    }

    private int getColumnWidthCompatible() {
        if (Build.VERSION.SDK_INT >= 16) {
            return super.getColumnWidth();
        }
        try {
            Field field = GridView.class.getDeclaredField("mColumnWidth");
            field.setAccessible(true);
            return field.getInt(this);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mViewForMeasureRowHeight = null;
    }

    public void invalidateRowHeight() {
        this.mRowHeight = -1;
    }

    public int getHeaderHeight(int index) {
        if (index >= 0) {
            return this.mHeaderViewInfos.get(index).view.getMeasuredHeight();
        }
        return 0;
    }

    @Override
    public int getVerticalSpacing() {
        int verticalSpacing;
        try {
            if (Build.VERSION.SDK_INT < 16) {
                Field field = GridView.class.getDeclaredField("mVerticalSpacing");
                field.setAccessible(true);
                verticalSpacing = field.getInt(this);
            } else {
                verticalSpacing = super.getVerticalSpacing();
            }
            return verticalSpacing;
        } catch (Exception unused) {
            return 0;
        }
    }

    @Override
    public int getHorizontalSpacing() {
        int horizontalSpacing;
        try {
            if (Build.VERSION.SDK_INT < 16) {
                Field field = GridView.class.getDeclaredField("mHorizontalSpacing");
                field.setAccessible(true);
                horizontalSpacing = field.getInt(this);
            } else {
                horizontalSpacing = super.getHorizontalSpacing();
            }
            return horizontalSpacing;
        } catch (Exception unused) {
            return 0;
        }
    }

    public int getRowHeight() {
        int rowHeight = this.mRowHeight;
        if (rowHeight > 0) {
            return rowHeight;
        }
        ListAdapter adapter = getAdapter();
        int numColumns = getNumColumnsCompatible();
        if (adapter == null || adapter.getCount() <= (this.mHeaderViewInfos.size() + this.mFooterViewInfos.size()) * numColumns) {
            return -1;
        }
        int columnWidth = getColumnWidthCompatible();
        View view = getAdapter().getView(numColumns * this.mHeaderViewInfos.size(), this.mViewForMeasureRowHeight, this);
        AbsListView.LayoutParams layoutParams = (AbsListView.LayoutParams) view.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new AbsListView.LayoutParams(-1, -2, 0);
            view.setLayoutParams(layoutParams);
        }
        view.measure(getChildMeasureSpec(View.MeasureSpec.makeMeasureSpec(columnWidth, 1073741824), 0, layoutParams.width),
                getChildMeasureSpec(View.MeasureSpec.makeMeasureSpec(0, 0), 0, layoutParams.height));
        this.mViewForMeasureRowHeight = view;
        this.mRowHeight = view.getMeasuredHeight();
        return this.mRowHeight;
    }

    public void tryToScrollToBottomSmoothly() {
        int count = getAdapter().getCount() - 1;
        if (Build.VERSION.SDK_INT >= 11) {
            smoothScrollToPositionFromTop(count, 0);
        } else {
            setSelection(count);
        }
    }

    public void tryToScrollToBottomSmoothly(int duration) {
        int count = getAdapter().getCount() - 1;
        if (Build.VERSION.SDK_INT >= 11) {
            smoothScrollToPositionFromTop(count, 0, duration);
        } else {
            setSelection(count);
        }
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        this.mOriginalAdapter = adapter;
        if (this.mHeaderViewInfos.size() > 0 || this.mFooterViewInfos.size() > 0) {
            HeaderViewGridAdapter headerViewGridAdapter = new HeaderViewGridAdapter(this.mHeaderViewInfos, this.mFooterViewInfos, adapter);
            int numColumns = getNumColumnsCompatible();
            if (numColumns > 1) {
                headerViewGridAdapter.setNumColumns(numColumns);
            }
            headerViewGridAdapter.setRowHeight(getRowHeight());
            super.setAdapter((ListAdapter) headerViewGridAdapter);
            return;
        }
        super.setAdapter(adapter);
    }

    public ListAdapter getOriginalAdapter() {
        return this.mOriginalAdapter;
    }

    /** 让页眉页脚占满整行的容器。 */
    private class FullWidthFixedViewLayout extends FrameLayout {
        public FullWidthFixedViewLayout(Context context) {
            super(context);
        }

        @Override
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            int targetLeft = GridViewWithHeaderAndFooter.this.getPaddingLeft() + getPaddingLeft();
            if (targetLeft != left) {
                offsetLeftAndRight(targetLeft - left);
            }
            super.onLayout(changed, left, top, right, bottom);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(
                    (GridViewWithHeaderAndFooter.this.getMeasuredWidth() - GridViewWithHeaderAndFooter.this.getPaddingLeft())
                            - GridViewWithHeaderAndFooter.this.getPaddingRight(), View.MeasureSpec.getMode(widthMeasureSpec)),
                    heightMeasureSpec);
        }
    }

    @Override
    public void setNumColumns(int numColumns) {
        super.setNumColumns(numColumns);
        this.mNumColumns = numColumns;
        ListAdapter adapter = getAdapter();
        if (adapter == null || !(adapter instanceof HeaderViewGridAdapter)) {
            return;
        }
        ((HeaderViewGridAdapter) adapter).setNumColumns(numColumns);
    }
    /** 包装原始适配器并插入页眉/页脚占位项的适配器。 */
    private static class HeaderViewGridAdapter implements Filterable, WrapperListAdapter {
        static final ArrayList<FixedViewInfo> EMPTY_INFO_LIST = new ArrayList<>();
        private final ListAdapter mAdapter;
        boolean mAreAllFixedViewsSelectable;
        ArrayList<FixedViewInfo> mFooterViewInfos;
        ArrayList<FixedViewInfo> mHeaderViewInfos;
        private final boolean mIsFilterable;
        private final DataSetObservable mDataSetObservable = new DataSetObservable();
        private int mNumColumns = 1;
        private int mRowHeight = -1;
        private boolean mCachePlaceHoldView = true;
        private boolean mCacheFirstHeaderView = false;

        public HeaderViewGridAdapter(ArrayList<FixedViewInfo> headerViewInfos, ArrayList<FixedViewInfo> footerViewInfos, ListAdapter adapter) {
            this.mAdapter = adapter;
            this.mIsFilterable = adapter instanceof Filterable;
            if (headerViewInfos == null) {
                this.mHeaderViewInfos = EMPTY_INFO_LIST;
            } else {
                this.mHeaderViewInfos = headerViewInfos;
            }
            if (footerViewInfos == null) {
                this.mFooterViewInfos = EMPTY_INFO_LIST;
            } else {
                this.mFooterViewInfos = footerViewInfos;
            }
            this.mAreAllFixedViewsSelectable = areAllListInfosSelectable(this.mHeaderViewInfos) && areAllListInfosSelectable(this.mFooterViewInfos);
        }

        public void setNumColumns(int numColumns) {
            if (numColumns >= 1 && this.mNumColumns != numColumns) {
                this.mNumColumns = numColumns;
                notifyDataSetChanged();
            }
        }

        public void setRowHeight(int rowHeight) {
            this.mRowHeight = rowHeight;
        }

        public int getHeadersCount() {
            return this.mHeaderViewInfos.size();
        }

        public int getFootersCount() {
            return this.mFooterViewInfos.size();
        }

        @Override
        public boolean isEmpty() {
            ListAdapter adapter = this.mAdapter;
            return adapter == null || adapter.isEmpty();
        }

        private boolean areAllListInfosSelectable(ArrayList<FixedViewInfo> viewInfos) {
            if (viewInfos == null) {
                return true;
            }
            Iterator<FixedViewInfo> iterator = viewInfos.iterator();
            while (iterator.hasNext()) {
                if (!iterator.next().isSelectable) {
                    return false;
                }
            }
            return true;
        }

        public boolean removeHeader(View view) {
            boolean selectable = false;
            for (int index = 0; index < this.mHeaderViewInfos.size(); index++) {
                if (this.mHeaderViewInfos.get(index).view == view) {
                    this.mHeaderViewInfos.remove(index);
                    if (areAllListInfosSelectable(this.mHeaderViewInfos) && areAllListInfosSelectable(this.mFooterViewInfos)) {
                        selectable = true;
                    }
                    this.mAreAllFixedViewsSelectable = selectable;
                    this.mDataSetObservable.notifyChanged();
                    return true;
                }
            }
            return false;
        }

        public boolean removeFooter(View view) {
            boolean selectable = false;
            for (int index = 0; index < this.mFooterViewInfos.size(); index++) {
                if (this.mFooterViewInfos.get(index).view == view) {
                    this.mFooterViewInfos.remove(index);
                    if (areAllListInfosSelectable(this.mHeaderViewInfos) && areAllListInfosSelectable(this.mFooterViewInfos)) {
                        selectable = true;
                    }
                    this.mAreAllFixedViewsSelectable = selectable;
                    this.mDataSetObservable.notifyChanged();
                    return true;
                }
            }
            return false;
        }

        @Override
        public int getCount() {
            if (this.mAdapter != null) {
                return ((getFootersCount() + getHeadersCount()) * this.mNumColumns) + getAdapterAndPlaceHolderCount();
            }
            return (getFootersCount() + getHeadersCount()) * this.mNumColumns;
        }

        @Override
        public boolean areAllItemsEnabled() {
            ListAdapter adapter = this.mAdapter;
            return adapter == null || (this.mAreAllFixedViewsSelectable && adapter.areAllItemsEnabled());
        }

        private int getAdapterAndPlaceHolderCount() {
            double rows = Math.ceil((this.mAdapter.getCount() * 1.0f) / this.mNumColumns);
            double numColumns = this.mNumColumns;
            return (int) (rows * numColumns);
        }

        @Override
        public boolean isEnabled(int position) {
            int headerCount = getHeadersCount();
            int numColumns = this.mNumColumns;
            int headerItems = headerCount * numColumns;
            if (position < headerItems) {
                return position % numColumns == 0 && this.mHeaderViewInfos.get(position / numColumns).isSelectable;
            }
            int contentPosition = position - headerItems;
            int adapterAndPlaceHolderCount = 0;
            if (this.mAdapter != null) {
                adapterAndPlaceHolderCount = getAdapterAndPlaceHolderCount();
                if (contentPosition < adapterAndPlaceHolderCount) {
                    return contentPosition < this.mAdapter.getCount() && this.mAdapter.isEnabled(contentPosition);
                }
            }
            int footerPosition = contentPosition - adapterAndPlaceHolderCount;
            return footerPosition % numColumns == 0 && this.mFooterViewInfos.get(footerPosition / numColumns).isSelectable;
        }

        @Override
        public Object getItem(int position) {
            int headerCount = getHeadersCount();
            int numColumns = this.mNumColumns;
            int headerItems = headerCount * numColumns;
            if (position < headerItems) {
                if (position % numColumns == 0) {
                    return this.mHeaderViewInfos.get(position / numColumns).data;
                }
                return null;
            }
            int contentPosition = position - headerItems;
            int adapterAndPlaceHolderCount = 0;
            if (this.mAdapter != null && contentPosition < (adapterAndPlaceHolderCount = getAdapterAndPlaceHolderCount())) {
                if (contentPosition < this.mAdapter.getCount()) {
                    return this.mAdapter.getItem(contentPosition);
                }
                return null;
            }
            int footerPosition = contentPosition - adapterAndPlaceHolderCount;
            if (footerPosition % this.mNumColumns == 0) {
                return this.mFooterViewInfos.get(footerPosition).data;
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            int headerItems = getHeadersCount() * this.mNumColumns;
            ListAdapter adapter = this.mAdapter;
            if (adapter == null || position < headerItems || (position - headerItems) >= adapter.getCount()) {
                return -1L;
            }
            return adapter.getItemId(position - headerItems);
        }

        @Override
        public boolean hasStableIds() {
            ListAdapter adapter = this.mAdapter;
            return adapter != null && adapter.hasStableIds();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int adapterAndPlaceHolderCount = 0;
            if (GridViewWithHeaderAndFooter.DEBUG) {
                LogUtil.d(GridViewWithHeaderAndFooter.LOG_TAG,
                        String.format("getView: %s, reused: %s", Integer.valueOf(position), Boolean.valueOf(convertView == null)));
            }
            int headerCount = getHeadersCount();
            int numColumns = this.mNumColumns;
            int headerItems = headerCount * numColumns;
            if (position < headerItems) {
                ViewGroup headerContainer = this.mHeaderViewInfos.get(position / numColumns).viewContainer;
                if (position % this.mNumColumns == 0) {
                    return headerContainer;
                }
                if (convertView == null) {
                    convertView = new View(parent.getContext());
                }
                convertView.setVisibility(4);
                convertView.setMinimumHeight(headerContainer.getHeight());
                return convertView;
            }
            int contentPosition = position - headerItems;
            if (this.mAdapter != null && contentPosition < (adapterAndPlaceHolderCount = getAdapterAndPlaceHolderCount())) {
                if (contentPosition < this.mAdapter.getCount()) {
                    return this.mAdapter.getView(contentPosition, convertView, parent);
                }
                if (convertView == null) {
                    convertView = new View(parent.getContext());
                }
                convertView.setVisibility(4);
                convertView.setMinimumHeight(this.mRowHeight);
                return convertView;
            }
            int footerPosition = contentPosition - adapterAndPlaceHolderCount;
            if (footerPosition < getCount()) {
                ViewGroup footerContainer = this.mFooterViewInfos.get(footerPosition / this.mNumColumns).viewContainer;
                if (position % this.mNumColumns == 0) {
                    return footerContainer;
                }
                if (convertView == null) {
                    convertView = new View(parent.getContext());
                }
                convertView.setVisibility(4);
                convertView.setMinimumHeight(footerContainer.getHeight());
                return convertView;
            }
            throw new ArrayIndexOutOfBoundsException(position);
        }

        @Override
        public int getItemViewType(int position) {
            int adapterAndPlaceHolderCount;
            int headerItems = getHeadersCount() * this.mNumColumns;
            ListAdapter adapter = this.mAdapter;
            int lastViewType = adapter == null ? 0 : adapter.getViewTypeCount() - 1;
            int viewType = -2;
            if (this.mCachePlaceHoldView && position < headerItems) {
                if (position == 0 && this.mCacheFirstHeaderView) {
                    viewType = this.mHeaderViewInfos.size() + lastViewType + this.mFooterViewInfos.size() + 1 + 1;
                }
                int numColumns = this.mNumColumns;
                if (position % numColumns != 0) {
                    viewType = (position / numColumns) + 1 + lastViewType;
                }
            }
            int contentPosition = position - headerItems;
            if (this.mAdapter != null) {
                adapterAndPlaceHolderCount = getAdapterAndPlaceHolderCount();
                if (contentPosition >= 0 && contentPosition < adapterAndPlaceHolderCount) {
                    if (contentPosition < this.mAdapter.getCount()) {
                        viewType = this.mAdapter.getItemViewType(contentPosition);
                    } else if (this.mCachePlaceHoldView) {
                        viewType = this.mHeaderViewInfos.size() + lastViewType + 1;
                    }
                }
            } else {
                adapterAndPlaceHolderCount = 0;
            }
            int footerPosition = contentPosition - adapterAndPlaceHolderCount;
            if (this.mCachePlaceHoldView && footerPosition >= 0 && footerPosition < getCount() && footerPosition % this.mNumColumns != 0) {
                viewType = lastViewType + this.mHeaderViewInfos.size() + 1 + (footerPosition / this.mNumColumns) + 1;
            }
            if (GridViewWithHeaderAndFooter.DEBUG) {
                LogUtil.d(GridViewWithHeaderAndFooter.LOG_TAG, String.format("getItemViewType: pos: %s, result: %s",
                        Integer.valueOf(position), Integer.valueOf(viewType), Boolean.valueOf(this.mCachePlaceHoldView),
                        Boolean.valueOf(this.mCacheFirstHeaderView)));
            }
            return viewType;
        }

        @Override
        public int getViewTypeCount() {
            ListAdapter adapter = this.mAdapter;
            int viewTypeCount = adapter == null ? 1 : adapter.getViewTypeCount();
            if (this.mCachePlaceHoldView) {
                int extra = this.mHeaderViewInfos.size() + 1 + this.mFooterViewInfos.size();
                if (this.mCacheFirstHeaderView) {
                    extra++;
                }
                viewTypeCount += extra;
            }
            if (GridViewWithHeaderAndFooter.DEBUG) {
                LogUtil.d(GridViewWithHeaderAndFooter.LOG_TAG, String.format("getViewTypeCount: %s", Integer.valueOf(viewTypeCount)));
            }
            return viewTypeCount;
        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {
            this.mDataSetObservable.registerObserver(observer);
            ListAdapter adapter = this.mAdapter;
            if (adapter != null) {
                adapter.registerDataSetObserver(observer);
            }
        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {
            this.mDataSetObservable.unregisterObserver(observer);
            ListAdapter adapter = this.mAdapter;
            if (adapter != null) {
                adapter.unregisterDataSetObserver(observer);
            }
        }

        @Override
        public Filter getFilter() {
            if (this.mIsFilterable) {
                return ((Filterable) this.mAdapter).getFilter();
            }
            return null;
        }

        @Override
        public ListAdapter getWrappedAdapter() {
            return this.mAdapter;
        }

        public void notifyDataSetChanged() {
            this.mDataSetObservable.notifyChanged();
        }
    }

    @Override
    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
        super.setOnItemClickListener(getItemClickHandler());
    }

    @Override
    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener listener) {
        this.mOnItemLongClickListener = listener;
        super.setOnItemLongClickListener(getItemClickHandler());
    }

    private ItemClickHandler getItemClickHandler() {
        if (this.mItemClickHandler == null) {
            this.mItemClickHandler = new ItemClickHandler();
        }
        return this.mItemClickHandler;
    }

    /** 修正点击位置以排除页眉占位的监听器。 */
    private class ItemClickHandler implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
        private ItemClickHandler() {
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (GridViewWithHeaderAndFooter.this.mOnItemClickListener == null) {
                return;
            }
            int adjustedPosition = position - (GridViewWithHeaderAndFooter.this.getHeaderViewCount() * GridViewWithHeaderAndFooter.this.getNumColumnsCompatible());
            if (adjustedPosition < 0) {
                return;
            }
            GridViewWithHeaderAndFooter.this.mOnItemClickListener.onItemClick(parent, view, adjustedPosition, id);
        }

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            if (GridViewWithHeaderAndFooter.this.mOnItemLongClickListener == null) {
                return true;
            }
            int adjustedPosition = position - (GridViewWithHeaderAndFooter.this.getHeaderViewCount() * GridViewWithHeaderAndFooter.this.getNumColumnsCompatible());
            if (adjustedPosition < 0) {
                return true;
            }
            GridViewWithHeaderAndFooter.this.mOnItemLongClickListener.onItemLongClick(parent, view, adjustedPosition, id);
            return true;
        }
    }
}