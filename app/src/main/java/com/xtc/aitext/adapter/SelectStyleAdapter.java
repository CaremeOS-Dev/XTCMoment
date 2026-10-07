package com.xtc.aitext.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.xtc.moment.R;
import com.xtc.aitext.bean.AIStyleTextBean;
import com.xtc.database.ormlite.CollectionUtil;
import com.xtc.ui.widget.util.TypedValueCompat;

import java.util.List;
import java.util.Objects;

/**
 * AI 文案风格列表适配器。
 */
public class SelectStyleAdapter extends RecyclerView.Adapter<SelectStyleAdapter.StyleViewHolder> {

    private static final String TAG = "ai_text_SelectStyleAdapter";

    private static final RequestOptions STYLE_IMAGE_OPTIONS = new RequestOptions()
            .transform((Transformation<Bitmap>) new RoundedCorners((int) TypedValueCompat.applyDimensionDip(10.0f)))
            .diskCacheStrategy(DiskCacheStrategy.ALL);

    private final Context context;
    private final List<AIStyleTextBean> styleList;
    private AIStyleTextBean selectedStyle;
    private ClickStyleBack clickStyleBack;

    /** 风格点击回调。 */
    public interface ClickStyleBack {
        void onStyleChanged();
    }

    public SelectStyleAdapter(Context context, List<AIStyleTextBean> styleList) {
        this.context = context;
        this.styleList = styleList;
    }

    @Override
    public StyleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new StyleViewHolder(LayoutInflater.from(context).inflate(R.layout.item_select_style, parent, false));
    }

    @Override
    public void onBindViewHolder(StyleViewHolder holder, int position) {
        holder.bind(styleList.get(position));
    }

    @Override
    public int getItemCount() {
        return styleList.size();
    }

    /** 更新风格列表。 */
    public void setStyleList(List<AIStyleTextBean> list) {
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        this.styleList.clear();
        this.styleList.addAll(list);
        notifyDataSetChanged();
    }

    /** 选中指定风格。 */
    public void selectStyle(AIStyleTextBean styleBean) {
        if (styleBean == null || CollectionUtil.isEmpty(this.styleList)) {
            return;
        }
        for (AIStyleTextBean item : this.styleList) {
            if (Objects.equals(item.getAiStyleName(), styleBean.getAiStyleName())) {
                notifyItemChanged(this.styleList.indexOf(item));
                notifyItemChanged(this.styleList.indexOf(styleBean));
                this.selectedStyle = styleBean;
            }
        }
    }

    public AIStyleTextBean getSelectedStyle() {
        return selectedStyle;
    }

    public void setClickStyleBack(ClickStyleBack clickStyleBack) {
        this.clickStyleBack = clickStyleBack;
    }

    /**
     * 风格条目 ViewHolder。
     */
    class StyleViewHolder extends RecyclerView.ViewHolder {

        private final ImageView ivBg;
        private final ImageView ivCheck;
        private AIStyleTextBean styleBean;

        StyleViewHolder(View itemView) {
            super(itemView);
            this.ivBg = itemView.findViewById(R.id.iv_bg);
            this.ivCheck = itemView.findViewById(R.id.iv_check);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SelectStyleAdapter.this.selectStyle(styleBean);
                    if (SelectStyleAdapter.this.clickStyleBack != null) {
                        SelectStyleAdapter.this.clickStyleBack.onStyleChanged();
                    }
                }
            });
        }

        void bind(AIStyleTextBean styleBean) {
            this.styleBean = styleBean;
            updateCheckState();
            loadStyleImage();
        }

        private void updateCheckState() {
            if (SelectStyleAdapter.this.selectedStyle == null) {
                return;
            }
            if (Objects.equals(styleBean.getAiStyleName(), SelectStyleAdapter.this.selectedStyle.getAiStyleName())) {
                ivCheck.setVisibility(View.VISIBLE);
            } else {
                ivCheck.setVisibility(View.GONE);
            }
        }

        private void loadStyleImage() {
            Glide.with(SelectStyleAdapter.this.context).load(styleBean.getAiStyleUrl())
                    .apply(STYLE_IMAGE_OPTIONS).into(ivBg);
        }
    }
}