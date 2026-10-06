package com.xtc.ui.widget.indicator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.xtc.ui.widget.R;

/** 页头视图：标题 + 新增按钮 + 未读红点。 */
public class HeadView extends RelativeLayout implements View.OnClickListener {
    private ImageView ivBtnAdd;
    private ImageView ivPoint;
    private OnItemClickListener mListener;
    private RelativeLayout rlBtnAdd;
    private RelativeLayout rootView;
    private TextView tvTitle;

    /** 条目点击回调。 */
    public interface OnItemClickListener {
        void onClick();
    }

    public HeadView(Context context) {
        super(context);
        initView(context);
    }

    public void setListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_os_head_bottom, this);
        this.rootView = (RelativeLayout) findViewById(R.id.rl_head);
        this.tvTitle = (TextView) this.rootView.findViewById(R.id.tv_head_title);
        this.ivBtnAdd = (ImageView) this.rootView.findViewById(R.id.iv_head_btn_add);
        this.ivPoint = (ImageView) this.rootView.findViewById(R.id.iv_friend_new_point);
        this.rlBtnAdd = (RelativeLayout) this.rootView.findViewById(R.id.rl_head_btn_add);
        this.rlBtnAdd.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        OnItemClickListener listener;
        if (view.getId() != R.id.rl_head_btn_add || (listener = this.mListener) == null) {
            return;
        }
        listener.onClick();
    }

    public void setTitleText(String title) {
        this.tvTitle.setText(title);
    }

    public void setTitleColor(int color) {
        this.tvTitle.setTextColor(color);
    }

    public void setShowUnReadPoint() {
        this.ivPoint.setVisibility(0);
    }

    public void setHideUnReadPoint() {
        this.ivPoint.setVisibility(8);
    }

    public ImageView getIvBtnAdd() {
        return this.ivBtnAdd;
    }

    public void hideAddBtn() {
        this.rlBtnAdd.setVisibility(8);
    }

    public void showAddBtn() {
        this.rlBtnAdd.setVisibility(0);
    }
}