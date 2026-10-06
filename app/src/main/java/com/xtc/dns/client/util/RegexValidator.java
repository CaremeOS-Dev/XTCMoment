package com.xtc.dns.client.util;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则校验器：按一组正则表达式依次匹配输入串，支持校验、分组匹配与拼接。
 */
public class RegexValidator implements Serializable {

    private static final long serialVersionUID = -8832409930574867162L;

    private final Pattern[] patterns;

    public RegexValidator(String regex) {
        this(regex, true);
    }

    public RegexValidator(String regex, boolean caseSensitive) {
        this(new String[]{regex}, caseSensitive);
    }

    public RegexValidator(String[] regexes) {
        this(regexes, true);
    }

    public RegexValidator(String[] regexes, boolean caseSensitive) {
        if (regexes == null || regexes.length == 0) {
            throw new IllegalArgumentException("Regular expressions are missing");
        }
        this.patterns = new Pattern[regexes.length];
        int flags = caseSensitive ? 0 : Pattern.CASE_INSENSITIVE;
        for (int index = 0; index < regexes.length; index++) {
            if (regexes[index] == null || regexes[index].length() == 0) {
                throw new IllegalArgumentException("Regular expression[" + index + "] is missing");
            }
            this.patterns[index] = Pattern.compile(regexes[index], flags);
        }
    }

    public boolean isValid(String value) {
        if (value == null) {
            return false;
        }
        for (Pattern pattern : this.patterns) {
            if (pattern.matcher(value).matches()) {
                return true;
            }
        }
        return false;
    }

    public String[] match(String value) {
        if (value == null) {
            return null;
        }
        for (Pattern pattern : this.patterns) {
            Matcher matcher = pattern.matcher(value);
            if (matcher.matches()) {
                int groupCount = matcher.groupCount();
                String[] groups = new String[groupCount];
                for (int group = 0; group < groupCount; group++) {
                    groups[group] = matcher.group(group + 1);
                }
                return groups;
            }
        }
        return null;
    }

    public String validate(String value) {
        if (value == null) {
            return null;
        }
        for (Pattern pattern : this.patterns) {
            Matcher matcher = pattern.matcher(value);
            if (matcher.matches()) {
                int groupCount = matcher.groupCount();
                if (groupCount == 1) {
                    return matcher.group(1);
                }
                StringBuilder builder = new StringBuilder();
                for (int group = 1; group <= groupCount; group++) {
                    String part = matcher.group(group);
                    if (part != null) {
                        builder.append(part);
                    }
                }
                return builder.toString();
            }
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("RegexValidator{");
        for (int index = 0; index < this.patterns.length; index++) {
            if (index > 0) {
                builder.append(",");
            }
            builder.append(this.patterns[index].pattern());
        }
        builder.append("}");
        return builder.toString();
    }
}