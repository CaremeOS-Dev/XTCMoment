package com.xtc.ui.widget.textview;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.xtc.moment.R;
import com.xtc.ui.widget.util.TypedValueCompat;

/** 右侧带图标的文本项，用于设置列表中的入口行。 */
public class RightIconTextView extends FrameLayout {
    private AttributeSet attrs;
    private ImageView imageView;
    private TextView textView;

    public RightIconTextView(Context context) {
        super(context);
        init();
    }

    public RightIconTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.attrs = attrs;
        init();
    }

    public RightIconTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.attrs = attrs;
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.item_right_icon_textview, (ViewGroup) this, true);
        this.textView = (TextView) findViewById(R.id.right_icon_tv);
        this.imageView = (ImageView) findViewById(R.id.right_icon_iv);
        TypedArray attributes = getContext().obtainStyledAttributes(this.attrs, R.styleable.RightIconTextView);
        if (attributes != null) {
            String title = attributes.getString(R.styleable.RightIconTextView_ritv_title);
            if (!TextUtils.isEmpty(title)) {
                this.textView.setText(title);
            } else {
                this.textView.setText(String.format(getResources().getString(R.string.about_settings), getAppName()));
            }
            this.textView.setTextColor(attributes.getColor(R.styleable.RightIconTextView_ritv_title_color,
                    getResources().getColor(R.color.color_4dd865)));
            this.textView.setTextSize(0, attributes.getDimensionPixelSize(R.styleable.RightIconTextView_ritv_title_size,
                    getResources().getDimensionPixelSize(R.dimen.sp_12)));
            Drawable icon = attributes.getDrawable(R.styleable.RightIconTextView_ritv_icon);
            if (icon != null) {
                this.imageView.setImageDrawable(icon);
            }
            this.imageView.getLayoutParams().height = attributes.getDimensionPixelSize(R.styleable.RightIconTextView_ritv_icon_height,
                    getResources().getDimensionPixelSize(R.dimen.dp_11));
            this.imageView.getLayoutParams().width = attributes.getDimensionPixelSize(R.styleable.RightIconTextView_ritv_icon_width,
                    getResources().getDimensionPixelSize(R.dimen.dp_11));
            int iconVisibility = attributes.getInt(R.styleable.RightIconTextView_ritv_icon_visibility, 0);
            this.imageView.setVisibility(iconVisibility);
            int backgroundColor = attributes.getColor(R.styleable.RightIconTextView_ritv_bg_color, 0);
            if (backgroundColor != 0) {
                LinearLayout.LayoutParams titleParams = (LinearLayout.LayoutParams) this.textView.getLayoutParams();
                titleParams.setMarginStart((int) TypedValueCompat.applyDimensionDip(6.0f));
                if (iconVisibility == 0) {
                    LinearLayout.LayoutParams iconParams = (LinearLayout.LayoutParams) this.imageView.getLayoutParams();
                    iconParams.setMarginEnd((int) TypedValueCompat.applyDimensionDip(4.0f));
                    this.imageView.setLayoutParams(iconParams);
                } else {
                    titleParams.setMarginStart((int) TypedValueCompat.applyDimensionDip(6.0f));
                }
                this.textView.setLayoutParams(titleParams);
            }
            ((GradientDrawable) findViewById(R.id.right_icon_ll).getBackground()).setColor(backgroundColor);
            attributes.recycle();
        }
    }

    public void setTextView(String text) {
        this.textView.setText(text);
    }

    public void setIconResource(int resId) {
        if (resId != 0) {
            this.imageView.setImageResource(resId);
        }
    }

    private String getAppName() {
        try {
            PackageManager packageManager = getContext().getPackageManager();
            return (String) packageManager.getApplicationInfo(getContext().getPackageName(), 0).loadLabel(packageManager);
        } catch (PackageManager.NameNotFoundException unused) {
            return "";
        }
    }
}