package com.xtc.aitext.weight;

import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;

import com.xtc.aitext.R;
import com.xtc.ui.widget.toast.view.ToastUtil;

import java.util.regex.Pattern;

/**
 * 输入过滤器，拦截表情字符并限制长度。
 */
public class EmojiInputFilter extends InputFilter.LengthFilter {

    private static final String TAG = "EmojiInputFilter";

    private final Context context;
    protected Pattern emoji;

    public EmojiInputFilter(int maxLength, Context context) {
        super(maxLength);
        this.emoji = Pattern.compile("[🀀-🏿]|[🐀-\u1f7ff]|[☀-⟿]", Pattern.CASE_INSENSITIVE);
        this.context = context;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        if (this.emoji.matcher(source).find()) {
            ToastUtil.showShortCover(context, context.getString(R.string.string_emoji_input_word_toast));
            return "";
        }
        return super.filter(source, start, end, dest, dstart, dend);
    }
}