package com.xtc.moment.module.widget;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Looper;
import android.os.MessageQueue;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.db.bean.DbMomentComment;
import com.xtc.moment.util.HandlerUtil;
import com.xtc.moment.util.SystemUtil;
import com.xtc.utils.ui.DimenUtil;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

/**
 * Legacy comment list view that inflates one text view per comment.
 *
 * @deprecated kept for call sites that still use the old layout; new code uses
 * {@link MainMomentCommentView}.
 */
@Deprecated
public class MomentCommentView extends AbsMomentCommentView implements View.OnClickListener,
        View.OnLongClickListener {

    private static final String TAG = "MomentCommentView";

    /** Recycled comment text views, shared by every instance. */
    public static Queue<TextView> cachedTextViewQueue = new ArrayDeque<TextView>();

    private LinearLayout.LayoutParams layoutParams;;
    private ForegroundColorSpan colorSpan;
    private ForegroundColorSpan colorSpan2;
    private String unknownName;
    private String replyString;
    private String separatorString;

    private final List<DbMomentComment> dbMomentComments = new ArrayList<DbMomentComment>();

    private int childViewBackground = R.drawable.bg_comment_textview;
    private int childViewTextColor = R.color.color_d9d9d9;
    private final String richTextColorString = "#888888";

    private OnCommentDeleteListener deleteListener;
    private OnMomentCommentListener listener;

    /** Notifies that a comment was long pressed for deletion. */
    public interface OnCommentDeleteListener {
        void onCommentDeleteClick(int position, DbMomentComment comment);
    }

    /** Notifies that a comment was tapped. */
    public interface OnMomentCommentListener {
        void onMomentCommentClick(int position, DbMomentComment comment);
    }

    public MomentCommentView(Context context) {
        super(context);
        init();
    }

    public MomentCommentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MomentCommentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        this.layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        this.colorSpan = new ForegroundColorSpan(Color.parseColor(this.richTextColorString));
        this.colorSpan2 = new ForegroundColorSpan(Color.parseColor(this.richTextColorString));
        this.unknownName = getContext().getString(R.string.unknown_watch);
        this.replyString = getContext().getString(R.string.reply);
        this.separatorString = getContext().getString(R.string.separator);
    }

    @Override
    public void initView() {
        if (this.dbMomentComments == null || this.dbMomentComments.size() <= 0) {
            setVisibility(GONE);
        } else if (getVisibility() != VISIBLE) {
            setVisibility(VISIBLE);
        }
        Collections.sort(this.dbMomentComments, new Comparator<DbMomentComment>() {
            @Override
            public int compare(DbMomentComment left, DbMomentComment right) {
                if (right.getCreateTime() < left.getCreateTime()) {
                    return 1;
                }
                return right.getCreateTime() > left.getCreateTime() ? -1 : 0;
            }
        });
        if (this.dbMomentComments.isEmpty()) {
            return;
        }
        Iterator<DbMomentComment> iterator = this.dbMomentComments.iterator();
        while (iterator.hasNext()) {
            addMomentCommentView(iterator.next());
        }
    }

    private void addDatas(final List<DbMomentComment> comments, final String selfWatchId) {
        Collections.sort(comments, new Comparator<DbMomentComment>() {
            @Override
            public int compare(DbMomentComment left, DbMomentComment right) {
                if (right.getCreateTime() < left.getCreateTime()) {
                    return 1;
                }
                return right.getCreateTime() > left.getCreateTime() ? -1 : 0;
            }
        });
        LogUtil.d(TAG, "addViewsToWindow: start");
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                if (comments == null || comments.isEmpty()) {
                    return;
                }
                final ArrayList<View> pendingViews = new ArrayList<View>();
                for (DbMomentComment comment : comments) {
                    if (!TextUtils.isEmpty(comment.getWatchId()) && selfWatchId.equals(comment.getWatchId())) {
                        comment.setWatchName(getContext().getString(R.string.me));
                    }
                    if (!TextUtils.isEmpty(comment.getReplyId()) && selfWatchId.equals(comment.getReplyId())) {
                        comment.setReplyName(getContext().getString(R.string.me));
                    }
                    pendingViews.add(addData(comment));
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Looper.getMainLooper().getQueue().addIdleHandler(new MessageQueue.IdleHandler() {
                        @Override
                        public boolean queueIdle() {
                            addViewsToWindow(pendingViews);
                            return false;
                        }
                    });
                } else {
                    addViewsToWindow(pendingViews);
                }
            }
        });
    }

    private View addData(DbMomentComment comment) {
        this.dbMomentComments.add(comment);
        return addMomentCommentView(comment);
    }

    public void setDatas(List<DbMomentComment> comments, String selfWatchId) {
        setVisibility(GONE);
        this.dbMomentComments.clear();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            cachedTextViewQueue.offer((TextView) getChildAt(i));
        }
        removeAllViews();
        if (comments == null || comments.size() <= 0) {
            return;
        }
        setVisibility(VISIBLE);
        addDatas(comments, selfWatchId);
    }

    public void setChildViewBackgroundResource(int resId) {
        this.childViewBackground = resId;
    }

    private View addMomentCommentView(DbMomentComment comment) {
        return realRefresh(comment);
    }

    /** Builds (or recycles) the text view for a single comment. */
    private View realRefresh(DbMomentComment comment) {
        int index = this.dbMomentComments.indexOf(comment);
        if (index == 0) {
            this.layoutParams.topMargin = DimenUtil.dp2px(getContext(), 0.5f);
        }
        if (index == this.dbMomentComments.size() - 1) {
            this.layoutParams.bottomMargin = DimenUtil.dp2px(getContext(), 2.0f);
        }
        this.layoutParams.leftMargin = 0;
        this.layoutParams.rightMargin = 0;

        Queue<TextView> queue = cachedTextViewQueue;
        TextView textView = queue != null ? queue.poll() : null;
        if (textView == null) {
            textView = new TextView(getContext());
            textView.setPadding(DimenUtil.dp2px(getContext(), 1.0f),
                    (int) getContext().getResources().getDimension(R.dimen.comment_padding_top_and_bottom),
                    DimenUtil.dp2px(getContext(), 1.0f),
                    (int) getContext().getResources().getDimension(R.dimen.comment_padding_top_and_bottom));
            textView.setBackgroundResource(this.childViewBackground);
            textView.setLayoutParams(this.layoutParams);
            textView.setTextSize(14.0f);
            textView.setTextColor(getResources().getColor(this.childViewTextColor));
        } else {
            textView.setText("");
        }
        textView.setClickable(true);
        textView.setOnClickListener(this);
        textView.setOnLongClickListener(this);

        String watchName = TextUtils.isEmpty(comment.getWatchName()) ? this.unknownName : comment.getWatchName();
        SpannableStringBuilder nameSpan = new SpannableStringBuilder(watchName);
        nameSpan.setSpan(this.colorSpan, 0, watchName.length(), 17);
        textView.append(nameSpan);
        if (!TextUtils.isEmpty(comment.getReplyName())) {
            textView.append(this.replyString);
            String replyName = TextUtils.isEmpty(comment.getReplyName()) ? this.unknownName : comment.getReplyName();
            SpannableStringBuilder replySpan = new SpannableStringBuilder(replyName);
            replySpan.setSpan(this.colorSpan2, 0, replyName.length(), 17);
            textView.append(replySpan);
        }
        textView.append(this.separatorString);
        textView.append(comment.getComment());
        return textView;
    }

    /** Adds the prepared comment views on the next main thread frame. */
    public void addViewsToWindow(final List<View> views) {
        HandlerUtil.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                LogUtil.d(TAG, "addViewsToWindow: " + views.size());
                for (View view : views) {
                    addViewInLayout(view, -1, view.getLayoutParams(), true);
                }
                requestLayout();
                invalidate();
                if (getVisibility() != VISIBLE) {
                    setVisibility(VISIBLE);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (SystemUtil.isFastDoubleClick()) {
            LogUtil.i(TAG, "onClick: click too fast.");
            return;
        }
        for (int i = 0; i < getChildCount(); i++) {
            if (view == getChildAt(i) && this.listener != null && !this.dbMomentComments.isEmpty()) {
                this.listener.onMomentCommentClick(i, this.dbMomentComments.get(i));
            }
        }
    }

    @Override
    public boolean onLongClick(View view) {
        for (int i = 0; i < getChildCount(); i++) {
            if (view == getChildAt(i) && this.deleteListener != null) {
                this.deleteListener.onCommentDeleteClick(i, this.dbMomentComments.get(i));
                return true;
            }
        }
        return false;
    }

    public void setOnMomentCommentListener(OnMomentCommentListener listener) {
        this.listener = listener;
    }

    public void setOnCommentDeleteListener(OnCommentDeleteListener listener) {
        this.deleteListener = listener;
    }
}