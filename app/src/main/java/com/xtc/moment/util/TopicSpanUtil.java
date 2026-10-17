package com.xtc.moment.util;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.constants.ModuleSwitchConstant;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.module.Constants;
import com.xtc.moment.net.bean.TopicListBean;
import com.xtc.moment.third.behavior.MomentBehavior;
import com.xtc.moment.util.switchs.ModuleSwitchUtil;
import com.xtc.utils.common.CollectionUtil;
import com.xtc.utils.encode.JSONUtil;

import java.util.List;

/**
 * 动态列表/详情中「小括号」富文本渲染。
 *
 * <p>把动态持久化的 {@code topicListJson} 渲染成可点击的彩色话题文本，点击后按
 * 话题类型跳转 H5 或仅上报埋点。</p>
 */
public class TopicSpanUtil {

    private static final String TAG = "TopicSpanUtil";
    private static final String DEFAULT_TOPIC_COLOR = "#FFFFFFCC";
    private static final int SPAN_FLAG = 33;

    /** 渲染「小括号」文本；无数据或开关关闭时隐藏。 */
    public static void setTopicSpanText(Context context, TextView tvTopic, final DbMoment moment,
                                        final boolean isSelf) {
        if (tvTopic == null) {
            return;
        }
        if (!ModuleSwitchUtil.queryModuleSwitchByBoolean(context,
                ModuleSwitchConstant.MODULE_SWITCH_TOPIC, true)) {
            tvTopic.setVisibility(View.GONE);
            LogUtil.d(TAG, "topic switch is close");
            return;
        }
        if (moment == null) {
            tvTopic.setVisibility(View.GONE);
            return;
        }
        String topicListJson = moment.getTopicListJson();
        if (TextUtils.isEmpty(topicListJson)) {
            LogUtil.d(TAG, "setTopicSpanText, jsonString is null");
            tvTopic.setVisibility(View.GONE);
            return;
        }
        List<TopicListBean> topicList = (List<TopicListBean>) JSONUtil.fromJSON(
                topicListJson, List.class, TopicListBean.class);
        if (CollectionUtil.isEmpty(topicList)) {
            tvTopic.setVisibility(View.GONE);
            return;
        }
        final Context appContext = context.getApplicationContext();
        SpannableStringBuilder builder = new SpannableStringBuilder();
        for (TopicListBean topicListBean : topicList) {
            if (builder.length() > 0) {
                builder.append("\n");
            }
            int start = builder.length();
            if (!topicListBean.getTopicName().contains(Constants.TOPIC_PREFIX)) {
                builder.append(Constants.TOPIC_PREFIX)
                        .append(topicListBean.getTopicName())
                        .append(Constants.TOPIC_SUFFIX);
            } else {
                builder.append(topicListBean.getTopicName());
            }
            int end = builder.length();
            try {
                builder.setSpan(new ForegroundColorSpan(Color.parseColor(topicListBean.getColor())),
                        start, end, SPAN_FLAG);
            } catch (Exception e) {
                builder.setSpan(new ForegroundColorSpan(Color.parseColor(DEFAULT_TOPIC_COLOR)),
                        start, end, SPAN_FLAG);
            }
            final String topicName = topicListBean.getTopicName();
            builder.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    LogUtil.d(TAG, "onClick#topicText:" + topicName);
                    for (TopicListBean bean : topicList) {
                        if (bean.getTopicName().equals(topicName)) {
                            LogUtil.d(TAG, "onClick#listener:" + bean);
                            MomentBehavior.clickTopic(isSelf, bean.getTopicType());
                            if (bean.getTopicType() == 1) {
                                String source = MomentTypeUtil.isOfficialType(moment.getType().intValue())
                                        ? Constants.TopicSource.OFFICIAL
                                        : Constants.TopicSource.SHARE;
                                String jumpUrl = StartWebUtils.appendQueryParameter(
                                        bean.getJumpUrl(), Constants.Strings.STRING_SOURCE, source);
                                StartWebUtils.startH5Activity(appContext, jumpUrl);
                            }
                            return;
                        }
                    }
                }

                @Override
                public void updateDrawState(TextPaint textPaint) {
                    textPaint.setUnderlineText(false);
                }
            }, start, end, SPAN_FLAG);
        }
        tvTopic.setText(builder);
        tvTopic.setMovementMethod(LinkMovementMethod.getInstance());
        tvTopic.setHighlightColor(Color.TRANSPARENT);
        tvTopic.setVisibility(View.VISIBLE);
    }
}

