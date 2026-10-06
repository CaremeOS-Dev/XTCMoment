package com.xtc.moment.module.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.xtc.log.LogUtil;
import com.xtc.log.util.TextUtils;
import com.xtc.moment.R;

/**
 * 动态正文视图，支持表情图标 + 文本。
 */
public class MomentContentView extends RelativeLayout {

    private static final String TAG = "MomentContentView";
    private static final String EMPTY_TEXT = "null";

    private final Context context;
    private ExpandTextView expandTextView;
    private ImageView ivMoodIcon;
    private TextView tvMoodContent;
    private ExpandTextView.ClickCheckAllListener clickCheckAllListener;

    public MomentContentView(Context context) {
        this(context, null);
    }

    public MomentContentView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MomentContentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
    }

    private void initView() {
        View content = LayoutInflater.from(getContext()).inflate(R.layout.layout_mood_and_status_view, this);
        this.expandTextView = (ExpandTextView) content.findViewById(R.id.ev_text);
        this.ivMoodIcon = (ImageView) content.findViewById(R.id.iv_mood_icon);
    }

    public void setRichText(String resource, CharSequence text) {
        if (text == null || TextUtils.isEmpty(text)) {
            text = " ";
        }
        setIconAndText(resource, text);
    }

    private void setIconAndText(final Object resource, final CharSequence text) {
        LogUtil.i(TAG, " setIconAndText resource:" + resource + ";text:" + ((Object) text));
        if (this.context == null) {
            return;
        }
        try {
            this.expandTextView.setText(text, this.clickCheckAllListener);
            if (isResourceInValid(resource)) {
                this.ivMoodIcon.setVisibility(GONE);
            } else {
                Glide.with(this.context).load(resource).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(GlideException exception, Object model, Target<Drawable> target,
                            boolean isFirstResource) {
                        LogUtil.i(TAG, "resource instanceof onLoadFailed" + resource + text);
                        MomentContentView.this.ivMoodIcon.setVisibility(GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable drawable, Object model, Target<Drawable> target,
                            DataSource dataSource, boolean isFirstResource) {
                        if (MomentContentView.this.ivMoodIcon != null) {
                            MomentContentView.this.ivMoodIcon.setVisibility(VISIBLE);
                            MomentContentView.this.ivMoodIcon.setImageDrawable(drawable);
                        }
                        return false;
                    }
                }).into(this.ivMoodIcon);
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "setIconAndText: ", e);
        }
    }

    private boolean isResourceInValid(Object resource) {
        if (resource == null) {
            return true;
        }
        if (resource instanceof String) {
            return TextUtils.isEmpty((String) resource) || EMPTY_TEXT.equals(resource);
        }
        return false;
    }

    public void setTextColor(int color) {
        this.expandTextView.setTextColor(color);
    }

    public void setTextSize(long textSize) {
        this.expandTextView.setTextSize(textSize);
    }

    public void setText(String text) {
        setRichText(null, text);
    }

    public void setTextMaxLines(int maxLines) {
        this.expandTextView.setMaxLines(maxLines);
    }

    public void setTextSupportExpand(boolean supportExpand) {
        this.expandTextView.setSupportExpand(supportExpand);
    }

    public void setRichText(int resId, CharSequence text) {
        if (text == null || TextUtils.isEmpty(text)) {
            text = " ";
        }
        setIconAndText(Integer.valueOf(resId), text);
    }

    public void setClickCheckAllListener(ExpandTextView.ClickCheckAllListener listener) {
        this.clickCheckAllListener = listener;
    }
}