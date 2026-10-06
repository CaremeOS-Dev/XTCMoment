package com.xtc.utils.common;

import android.text.TextUtils;

import com.xtc.moment.module.Constants;

import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/** String formatting, validation and width helpers. */
public class StringUtil {

    protected static final String TAG = StringUtil.class.getSimpleName();

    /** Pads a number up to {@code length} with the {@code "s"} specifier. */
    public static String getBlank(int length) {
        return String.format("%" + length + Constants.ENGLISH_PLURAL, "");
    }

    /** @return true when {@code ip} is a dotted-quad IPv4 address. */
    public static boolean isIp(String ip) {
        String[] parts;
        int value;
        if (ip == null || (parts = ip.split("\\.")) == null) {
            return false;
        }
        try {
            for (String part : parts) {
                if (TextUtils.isEmpty(part.trim()) || (value = Integer.parseInt(part.trim())) < 0 || value > 255) {
                    return false;
                }
            }
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    /** @return true when {@code value} only contains digits. */
    public static boolean isDigits(String value) {
        return !TextUtils.isEmpty(value) && value.matches("[0-9]*");
    }

    /** @return true when {@code value} starts with a digit. */
    public static boolean isStartWithDigit(String value) {
        if (TextUtils.isEmpty(value)) {
            return false;
        }
        return Pattern.compile("^\\d+").matcher(value.charAt(0) + "").matches();
    }

    /** @return true when {@code value} is a digit. */
    public static boolean isDigit(char value) {
        return Pattern.compile("^\\d+").matcher(value + "").matches();
    }

    /** @return true when {@code url} matches the http/ftp pattern. */
    public static boolean isUrl(String url) {
        return !TextUtils.isEmpty(url) && url.matches("^((https?)|(ftp))://(?:(\\s+?)(?::(\\s+?))?@)?([a-zA-Z0-9\\-.]+)(?::(\\d+))?((?:/[a-zA-Z0-9\\-._?,'+\\&%$=~*!():@\\\\]*)+)?$");
    }

    /** Converts each character of {@code text} into its code point. */
    public static String stringToUnicode(String text) {
        if (TextUtils.isEmpty(text)) {
            return null;
        }
        char[] chars = text.toCharArray();
        StringBuffer buffer = new StringBuffer();
        for (char value : chars) {
            buffer.append((int) value);
        }
        return buffer.toString();
    }

    /** Decodes a five-digit-per-character code string back into text. */
    public static String unicodeToString(String text) {
        if (TextUtils.isEmpty(text)) {
            return null;
        }
        String trimmed = text.trim();
        int length = trimmed.length() / 5;
        StringBuffer buffer = new StringBuffer();
        int end = 0;
        for (int index = 0; index < length; index++) {
            end += 5;
            buffer.append((char) Integer.valueOf(trimmed.substring(index * 5, end)).intValue());
        }
        return buffer.toString();
    }

    /** Normalizes full-width spaces and characters into their half-width form. */
    public static String toHalfWidth(String text) {
        char[] chars = text.toCharArray();
        for (int index = 0; index < chars.length; index++) {
            if (chars[index] == 12288) {
                chars[index] = ' ';
            } else if (chars[index] > 65280 && chars[index] < 65375) {
                chars[index] = (char) (chars[index] - 65248);
            }
        }
        return new String(chars);
    }

    /** Removes characters outside the allowed whitespace/punctuation set. */
    public static String filterChars(String text) throws PatternSyntaxException {
        return Pattern.compile("[^0-9a-zA-Z一-龥`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？[-]•－／｜＼／～＠《》〈〉〔〕［］<>-_ˇ｛｝ˉ¨＝＜％＄＃＋︿＿＆＊＂｀．〃‖々「」『』〖〗∶＇＂＊ ＆]+").matcher(text).replaceAll("");
    }

    /** Normalizes punctuation and strips surrounding brackets. */
    public static String filterBrackets(String text) {
        return Pattern.compile("[『』]").matcher(text.replaceAll("【", "[").replaceAll("】", "]").replaceAll("！", "!").replaceAll("：", ":")).replaceAll("").trim();
    }

    /** @return true when {@code value} belongs to a CJK-ish unicode block. */
    public static boolean isChineseChar(char value) {
        Character.UnicodeBlock unicodeBlock = Character.UnicodeBlock.of(value);
        return unicodeBlock == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || unicodeBlock == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS || unicodeBlock == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || unicodeBlock == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B || unicodeBlock == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || unicodeBlock == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS || unicodeBlock == Character.UnicodeBlock.GENERAL_PUNCTUATION;
    }

    /** @return true when {@code text} contains any Chinese character. */
    public static boolean containsChinese(String text) {
        for (char value : text.toCharArray()) {
            if (isChineseChar(value)) {
                return true;
            }
        }
        return false;
    }

    /** Length of {@code text} where non-ASCII characters count as two. */
    public static int getWidth(String text) {
        if (text == null) {
            return 0;
        }
        int width = 0;
        for (char value : text.toCharArray()) {
            width++;
            if (!isAscii(value)) {
                width++;
            }
        }
        return width;
    }

    /** @return true when {@code value} is a single-byte ASCII character. */
    public static boolean isAscii(char value) {
        return value / 128 == 0;
    }

    /** Truncates {@code text} to a display width of at most 8. */
    public static String limitWidth(String text) {
        if (text == null) {
            return "";
        }
        char[] chars = text.toCharArray();
        for (int end = chars.length - 1; end >= 0; end--) {
            String candidate = substring(chars, 0, end);
            if (getWidth(candidate) <= 8) {
                return candidate;
            }
        }
        return text;
    }

    /** Concatenates {@code chars[start..end]}. */
    private static String substring(char[] chars, int start, int end) {
        if (start > end) {
            return "";
        }
        StringBuffer buffer = new StringBuffer();
        while (start <= end) {
            buffer.append(chars[start]);
            start++;
        }
        return buffer.toString();
    }

    /** @return true when {@code text} only contains Chinese letters, digits or underscore. */
    public static boolean isChineseId(String text) {
        return Pattern.compile("^[一-龥_a-zA-Z0-9]+$").matcher(text).matches();
    }

    /** @return true when the list is null or empty. */
    public static <T> boolean isEmpty(List<T> list) {
        return list == null || list.isEmpty();
    }

    /** Generates a random lowercase-alphanumeric string of {@code length} characters. */
    public static String randomString(int length) {
        StringBuffer alphabet = new StringBuffer("0123456789abcdefghijklmnopqrstuvwxyz");
        StringBuffer result = new StringBuffer();
        Random random = new Random();
        int alphabetLength = alphabet.length();
        for (int index = 0; index < length; index++) {
            result.append(alphabet.charAt(random.nextInt(alphabetLength)));
        }
        return result.toString();
    }

    /** Formats a two-digit number, saturating at 100 and clamping negatives to {@code "00"}. */
    public static String formatTwoDigits(int value) {
        if (value >= 100) {
            return "" + value;
        }
        if (value >= 0) {
            return String.format("%02d", Integer.valueOf(value));
        }
        return "" + Constants.Share.VIDEO_DURATION_PATTERN;
    }
}