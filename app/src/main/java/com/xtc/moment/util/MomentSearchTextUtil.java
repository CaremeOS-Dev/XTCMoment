package com.xtc.moment.util;

import android.text.TextUtils;

import com.xtc.moment.db.bean.DbMoment;
import com.xtc.utils.encode.JSONUtil;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 「搜索动态」的关键词匹配工具。
 *
 * <p>动态的文本分散在多个字段里：纯文字动态看 {@code content}，图文/视频/分享类动态的文案则
 * 藏在 JSON 里（如 {@code MultiPhotoContent.content}、{@code ShareTextPublish.content}、
 * {@code ShareAppMoment.desc}）。这里把一条动态摊平成可检索文本，再对关键词做包含匹配。
 *
 * <p>JSON 字段只取白名单键的<b>值</b>，避免把 {@code content}、{@code resource} 这类键名
 * 本身当成内容，导致搜 "content" 时满屏假命中。
 */
public final class MomentSearchTextUtil {

    /** 关键词长度上限，与搜索框的 maxLength 保持一致。 */
    public static final int MAX_KEYWORD_LENGTH = 30;

    /** JSON 里允许参与检索的键（键名小写比较）。 */
    private static final String[] SEARCHABLE_JSON_KEYS = {
            "content", "desc", "textmsg", "appname", "title", "name",
            "location", "address", "addressdesc", "poiname", "description"
    };

    /** 递归解析 JSON 的最大深度，防御异常嵌套。 */
    private static final int MAX_JSON_DEPTH = 6;

    private MomentSearchTextUtil() {
    }

    /** 关键词归一化：去首尾空白 + 小写（大小写不敏感匹配）。 */
    public static String normalize(String keyword) {
        if (TextUtils.isEmpty(keyword)) {
            return "";
        }
        return keyword.trim().toLowerCase(Locale.ROOT);
    }

    /** 关键词是否可用于检索。 */
    public static boolean isValidKeyword(String keyword) {
        String normalized = normalize(keyword);
        return !TextUtils.isEmpty(normalized) && normalized.length() <= MAX_KEYWORD_LENGTH;
    }

    /** 该动态是否命中关键词（关键词需已 {@link #normalize(String)}）。 */
    public static boolean matches(DbMoment moment, String normalizedKeyword) {
        if (moment == null || TextUtils.isEmpty(normalizedKeyword)) {
            return false;
        }
        String text = extractSearchableText(moment);
        return !TextUtils.isEmpty(text) && text.contains(normalizedKeyword);
    }

    /**
     * 摊平一条动态里所有可检索文本，结果已小写。
     *
     * <p>字段以 <code>{</code>/<code>[</code> 开头时按 JSON 解析并只取白名单键的值，
     * 否则按纯文本原样计入。
     */
    public static String extractSearchableText(DbMoment moment) {
        if (moment == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        appendText(builder, moment.getContent());
        appendText(builder, moment.getPublishContent());
        appendText(builder, moment.getDescription());
        appendText(builder, moment.getLocation());
        appendText(builder, moment.getName());
        return builder.toString().toLowerCase(Locale.ROOT);
    }

    /** 追加一个字段：JSON 走白名单取值，纯文本直接追加。 */
    private static void appendText(StringBuilder builder, String raw) {
        if (TextUtils.isEmpty(raw)) {
            return;
        }
        String trimmed = raw.trim();
        if (isJsonLike(trimmed)) {
            Object parsed = JSONUtil.fromJSON(trimmed, Object.class);
            if (parsed != null) {
                appendJsonValue(builder, parsed, false, 0);
                return;
            }
            // 解析失败（例如用户正文本身就以 "{" 开头）时按纯文本处理，避免漏搜。
        }
        builder.append(' ').append(trimmed);
    }

    private static boolean isJsonLike(String text) {
        return text.startsWith("{") || text.startsWith("[");
    }

    /**
     * 递归收集 JSON 里白名单键对应的文本。
     *
     * <p>非白名单键的整棵子树直接跳过：URL、本地路径、MD5 之类的元数据不该参与检索，
     * 否则搜 "http"、".jpg" 这类词会出现莫名其妙的命中。
     */
    private static void appendJsonValue(StringBuilder builder, Object node, boolean inSearchableBranch, int depth) {
        if (node == null || depth > MAX_JSON_DEPTH) {
            return;
        }
        if (node instanceof Map) {
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) node).entrySet()) {
                Object key = entry.getKey();
                if (key == null || !isSearchableKey(String.valueOf(key).toLowerCase(Locale.ROOT))) {
                    continue;
                }
                appendJsonValue(builder, entry.getValue(), true, depth + 1);
            }
            return;
        }
        if (node instanceof List) {
            for (Object item : (List<?>) node) {
                appendJsonValue(builder, item, inSearchableBranch, depth + 1);
            }
            return;
        }
        if (inSearchableBranch && node instanceof String) {
            builder.append(' ').append((String) node);
        }
    }

    private static boolean isSearchableKey(String keyName) {
        for (String searchable : SEARCHABLE_JSON_KEYS) {
            if (searchable.equals(keyName)) {
                return true;
            }
        }
        return false;
    }
}
