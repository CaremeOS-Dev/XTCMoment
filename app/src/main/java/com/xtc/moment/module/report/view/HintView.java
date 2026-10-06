package com.xtc.moment.module.report.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.xtc.moment.R;
import com.xtc.moment.module.report.interfaces.IJumpView;
import com.xtc.ui.widget.button.DoubleFlatButton;
import com.xtc.ui.widget.button.LongSolidButton;

/**
 * 举报流程中的提示页：可展示双按钮（取消/确定）或单个确定按钮。
 */
public class HintView extends AbsBaseJumpView {

    private static final int FINISH_TIME = 3000;

    private TextView tvCenterHintView;
    private DoubleFlatButton hintDoubleButton;
    private LongSolidButton btnInformContent;
    private boolean autoDis;
    private boolean cancelBackView;

    @Override
    protected int getLayoutId() {
        return R.layout.layout_hint_report;
    }

    public HintView(Context context, IJumpView iJumpView) {
        super(context, iJumpView);
    }

    public HintView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    protected void findViewId() {
        this.hintDoubleButton = (DoubleFlatButton) findId(R.id.hintDoubleButton);
        this.tvCenterHintView = (TextView) findId(R.id.centerHintView);
    }

    public void setCenterHintText(String hintText) {
        this.tvCenterHintView.setText(hintText);
    }

    @Override
    protected void initData() {
        this.hintDoubleButton.setLeftBgColorIdArray(new int[]{R.color.color_gray_start, R.color.color_gray_end});
        this.hintDoubleButton.setRightBgColorIdArray(new int[]{R.color.color_2bdbff, R.color.color_1794fa});
        TextView leftButton = this.hintDoubleButton.getLeftButton();
        leftButton.setText(R.string.cancel);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (HintView.this.cancelBackView) {
                    HintView.this.showPreviousView();
                } else {
                    HintView.this.finishView();
                }
            }
        });
        TextView rightButton = this.hintDoubleButton.getRightButton();
        rightButton.setText(R.string.sure);
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HintView.this.showNextView();
            }
        });
        this.btnInformContent = (LongSolidButton) findId(R.id.btn_hint_sure);
        this.btnInformContent.getTv().setText(this.mContext.getResources().getString(R.string.sure));
        this.btnInformContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HintView.this.showNextView();
            }
        });
    }

    /** 切换成单个确定按钮的样式。 */
    public void setHintSure() {
        this.hintDoubleButton.setVisibility(View.GONE);
        this.btnInformContent.setVisibility(View.VISIBLE);
    }

    /** 展示后自动关闭。 */
    public void setAutoDis() {
        this.autoDis = true;
    }

    /** 取消按钮回退到上一步而不是结束页面。 */
    public void setCancelBack() {
        this.cancelBackView = true;
    }

    @Override
    public void viewShow(int informSource) {
        super.viewShow(informSource);
        if (this.autoDis) {
            this.btnInformContent.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (HintView.this.cancelBackView) {
                        HintView.this.showPreviousView();
                    } else {
                        HintView.this.finishView();
                    }
                }
            }, FINISH_TIME);
        }
    }
}