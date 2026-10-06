package com.xtc.aitext.weight;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.xtc.aitext.R;
import com.xtc.log.LogUtil;

/**
 * 绘画退出确认弹窗。
 */
public class PaintExitDialog extends Dialog {

    private static final String TAG = "ai_text_PaintExitDialog";

    private Context mContext;
    private TextView tvContinue;
    private TextView tvExit;
    private MoveCallback moveCallback;

    /** 继续/退出回调。 */
    public interface MoveCallback {
        void clickContinue();

        void clickExit();
    }

    public PaintExitDialog(Context context) {
        this(context, R.style.dialog_default_style);
    }

    public PaintExitDialog(Context context, int themeResId) {
        super(context, themeResId);
        setContentView(R.layout.dialog_move_tip);
        this.mContext = context;
        initView();
    }

    private void initView() {
        this.tvContinue = findViewById(R.id.tv_continue);
        this.tvExit = findViewById(R.id.tv_exit);
        this.tvContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MoveCallback callback = moveCallback;
                if (callback != null) {
                    callback.clickContinue();
                }
            }
        });
        this.tvExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MoveCallback callback = moveCallback;
                if (callback != null) {
                    callback.clickExit();
                }
            }
        });
    }

    @Override
    public void show() {
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams attributes = window.getAttributes();
            attributes.width = ViewGroup.LayoutParams.MATCH_PARENT;
            attributes.height = ViewGroup.LayoutParams.MATCH_PARENT;
            window.getDecorView().setPadding(0, 0, 0, 0);
            window.setAttributes(attributes);
            window.setBackgroundDrawable(null);
        }
        super.show();
    }

    public void setMoveCallback(MoveCallback moveCallback) {
        this.moveCallback = moveCallback;
    }

    @Override
    public void dismiss() {
        super.dismiss();
        LogUtil.d(TAG, "dismiss: ");
    }
}