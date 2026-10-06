package com.xtc.ui.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.ui.widget.button.DoubleFlatButton;
import com.xtc.ui.widget.dialog.bean.noIcon.DoubleFlatBtnBean;
import com.xtc.ui.widget.util.UiCommonUtil;
import com.xtc.utils.ui.WatchBlurUtil;

/** Two-button flat dialog whose content is an editable text field. */
public class EditableDoubleFlatBtnDialog extends Dialog {

    private static final String TAG = "EditableDoubleFlatBtnDialog";

    /** The content area never grows past this many lines before it scrolls. */
    private static final int MAX_LINE = 4;

    private Context mContext;
    private RelativeLayout rlRoot;
    private ScrollView svRoot;
    private EditText etEditableContent;
    private DoubleFlatButton tvBottom;
    private View coverSpace;
    private InputMethodManager inputManager;
    private OnEditListener listener;

    /** Notified when the user confirms the edited text. */
    public interface OnEditListener {
        void onEdit(String text);
    }

    public EditableDoubleFlatBtnDialog(Context context, boolean forbidSwipeToDismiss) {
        this(context, forbidSwipeToDismiss ? R.style.dialog_default_style_forbidSwipe : R.style.dialog_default_style);
    }

    public EditableDoubleFlatBtnDialog(Context context, int themeResId) {
        super(context, themeResId);
        super.setContentView(R.layout.layout_editable_double_flat_button_dialog);
        this.mContext = context;
        initView();
    }

    private void initView() {
        this.rlRoot = (RelativeLayout) findViewById(R.id.rl_root_normal_dialog);
        this.svRoot = (ScrollView) findViewById(R.id.sv_root);
        this.etEditableContent = (EditText) findViewById(R.id.et_editable_content_normal_dialog);
        this.etEditableContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (EditableDoubleFlatBtnDialog.this.etEditableContent.getLineCount() > MAX_LINE) {
                    EditableDoubleFlatBtnDialog.this.svRoot.setLayoutParams(new RelativeLayout.LayoutParams(-1, EditableDoubleFlatBtnDialog.this.etEditableContent.getLineHeight() * MAX_LINE));
                } else {
                    EditableDoubleFlatBtnDialog.this.svRoot.setLayoutParams(new RelativeLayout.LayoutParams(-1, EditableDoubleFlatBtnDialog.this.etEditableContent.getHeight()));
                }
            }
        });
        this.etEditableContent.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if (keyCode != KeyEvent.KEYCODE_ENTER) {
                    return false;
                }
                String text = EditableDoubleFlatBtnDialog.this.etEditableContent.getText().toString();
                if (EditableDoubleFlatBtnDialog.this.listener == null) {
                    return true;
                }
                EditableDoubleFlatBtnDialog.this.listener.onEdit(text);
                return true;
            }
        });
        this.tvBottom = (DoubleFlatButton) findViewById(R.id.btn_bottom);
        this.coverSpace = findViewById(R.id.cover_space);
        this.coverSpace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (EditableDoubleFlatBtnDialog.this.inputManager == null) {
                    EditableDoubleFlatBtnDialog dialog = EditableDoubleFlatBtnDialog.this;
                    dialog.inputManager = (InputMethodManager) dialog.etEditableContent.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                }
                if (EditableDoubleFlatBtnDialog.this.inputManager != null) {
                    EditableDoubleFlatBtnDialog.this.inputManager.showSoftInput(EditableDoubleFlatBtnDialog.this.etEditableContent, 0);
                    if (TextUtils.isEmpty(EditableDoubleFlatBtnDialog.this.etEditableContent.getText())) {
                        return;
                    }
                    EditableDoubleFlatBtnDialog.this.etEditableContent.setSelection(EditableDoubleFlatBtnDialog.this.etEditableContent.getText().length());
                }
            }
        });
        this.svRoot.scrollTo(0, 0);
    }

    public void initData(DoubleFlatBtnBean bean) {
        setContent(bean.getTitleString());
        this.tvBottom.getLeftButton().setText(bean.getLeftStringId());
        this.tvBottom.getRightButton().setText(bean.getRightStringId());
    }

    public void setContent(CharSequence content) {
        LogUtil.d(TAG, "setContent = " + ((Object) content));
        if (TextUtils.isEmpty(content)) {
            this.etEditableContent.setVisibility(View.GONE);
        } else {
            this.etEditableContent.setVisibility(View.VISIBLE);
            this.etEditableContent.setText(content);
        }
    }

    public DoubleFlatButton getBottomBtn() {
        return this.tvBottom;
    }

    public String getContent() {
        return this.etEditableContent.getVisibility() == View.VISIBLE ? this.etEditableContent.getText().toString() : "";
    }

    public void setTransparentBackgroud() {
        RelativeLayout relativeLayout = this.rlRoot;
        if (relativeLayout != null) {
            relativeLayout.setBackground(null);
        }
    }

    @Deprecated
    public void setWholeBackgroud(View view) {
        if (this.rlRoot == null) {
            LogUtil.e(TAG, "rlRoot = null , return !");
            return;
        }
        Resources resources = this.mContext.getResources();
        Bitmap bitmap = WatchBlurUtil.blur(view, this.mContext);
        if (bitmap != null) {
            this.rlRoot.setBackground(new BitmapDrawable(resources, bitmap));
        } else {
            LogUtil.w(TAG, "dialog 高斯模糊失败 !");
        }
    }

    public void setWholeBackgroud(int colorRes) {
        RelativeLayout relativeLayout = this.rlRoot;
        if (relativeLayout != null) {
            relativeLayout.setBackgroundColor(UiCommonUtil.getColor(this.mContext, colorRes));
        }
    }

    public void setWholeBackgroud(Drawable drawable) {
        RelativeLayout relativeLayout = this.rlRoot;
        if (relativeLayout != null) {
            relativeLayout.setBackground(drawable);
        }
    }

    @Override
    public void show() {
        LogUtil.i(TAG, "show()--");
        super.show();
        Window window = getWindow();
        if (window == null) {
            LogUtil.e(TAG, "error , window = null !");
            return;
        }
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = -1;
        attributes.height = -1;
        window.getDecorView().setPadding(0, 0, 0, 0);
        window.setAttributes(attributes);
    }

    @Override
    public void dismiss() {
        LogUtil.i(TAG, "dismiss()");
        InputMethodManager inputMethodManager = this.inputManager;
        EditText editText = this.etEditableContent;
        if (inputMethodManager != null && editText != null) {
            inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
        super.dismiss();
    }

    public void setOnEditListener(OnEditListener listener) {
        this.listener = listener;
    }

    public void setImeOptions(int imeOptions) {
        this.etEditableContent.setImeOptions(imeOptions);
    }

    public EditText getEtEditableContent() {
        return this.etEditableContent;
    }
}
