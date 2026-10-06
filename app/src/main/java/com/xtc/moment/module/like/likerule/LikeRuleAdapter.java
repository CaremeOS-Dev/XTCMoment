package com.xtc.moment.module.like.likerule;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.module.personalinfo.net.bean.LikeRule;

import java.util.ArrayList;
import java.util.List;

/**
 * 点赞规则列表适配器：按等级区间展示每日可点赞次数。
 */
public class LikeRuleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "LikeRuleAdapter";

    private Context mContext;
    private List<LikeRule> mLikeRules = new ArrayList<>();

    public LikeRuleAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(this.mContext)
                .inflate(R.layout.item_like_rule, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MyViewHolder ruleHolder = (MyViewHolder) holder;
        LikeRule likeRule = this.mLikeRules.get(position);
        if (likeRule == null) {
            return;
        }
        if (likeRule.getMaxLevel() == 0) {
            ruleHolder.mTvLevel.setText(this.mContext.getString(R.string.above_grade_format,
                    likeRule.getMinLevel() + ""));
        } else {
            ruleHolder.mTvLevel.setText(this.mContext.getString(R.string.level_section_format,
                    likeRule.getMinLevel() + "", likeRule.getMaxLevel() + ""));
        }
        ruleHolder.mTvLevelCount.setText(this.mContext.getString(R.string.praises_time_format,
                likeRule.getTimes() + ""));
        ruleHolder.mTvLevel.setTextColor(ContextCompat.getColor(this.mContext, getTextColorId(position)));
        ruleHolder.mTvLevelCount.setTextColor(ContextCompat.getColor(this.mContext, getTextColorId(position)));
    }

    public void addData(List<LikeRule> likeRules) {
        LogUtil.d(TAG, "list size = " + likeRules.size());
        this.mLikeRules.addAll(likeRules);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return this.mLikeRules.size();
    }

    /** 按行号循环取色，让相邻等级颜色不同。 */
    private int getTextColorId(int position) {
        int colorIndex = position % 6;
        if (colorIndex == 0) {
            return R.color.color_cffcff;
        }
        if (colorIndex == 1) {
            return R.color.color_59eaff;
        }
        if (colorIndex == 2) {
            return R.color.color_fec002;
        }
        if (colorIndex == 3) {
            return R.color.color_fe7f02;
        }
        return colorIndex == 4 ? R.color.color_fe4302 : R.color.color_ff27ca;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mTvLevel;
        TextView mTvLevelCount;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.mTvLevel = (TextView) itemView.findViewById(R.id.tv_level);
            this.mTvLevelCount = (TextView) itemView.findViewById(R.id.tv_level_count);
        }
    }
}