package com.xtc.bigdata.common.utils;

import com.xtc.log.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.regex.Pattern;

/**
 * 字符串与格式校验工具。
 */
public final class StringUtils {

    private static final String DEFAULT_URL_ENCODE = "utf-8";
    private static final String REGEX_DATE = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)$";
    private static final String REGEX_EMAIL = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
    private static final String REGEX_IDCARD18 = "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9Xx])$";
    private static final String REGEX_IP = "((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)";
    private static final String REGEX_IPV6 = "^\\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:)))(%.+)?\\s*$";
    private static final String REGEX_MOBILE_EXACT = "^((13[0-9])|(14[5,7])|(15[0-3,5-9])|(17[0,3,5-8])|(18[0-9])|(147))\\d{8}$";
    private static final String TAG = "BfcCommon_StringUtils";
    private static final char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    private StringUtils() {
        throw new UnsupportedOperationException("Are u ok ?");
    }

    public static boolean isEmpty(String text) {
        return text == null || text.length() == 0;
    }

    public static boolean isSpace(String text) {
        return text == null || text.trim().length() == 0;
    }

    public static String encodeUrl(String url) {
        try {
            return URLEncoder.encode(url, DEFAULT_URL_ENCODE);
        } catch (Exception e) {
            LogUtil.w(TAG, e + ":url encode异常, 直接返回url");
            return url;
        }
    }

    public static String decodeUrl(String url) {
        try {
            return URLDecoder.decode(url, DEFAULT_URL_ENCODE);
        } catch (Exception e) {
            LogUtil.w(TAG, e + ":url decode解码异常, 直接返回url");
            return url;
        }
    }

    public static String bytes2HexString(byte[] bytes) {
        char[] result = new char[bytes.length << 1];
        int index = 0;
        for (int i = 0; i < bytes.length; i++) {
            int high = index + 1;
            result[index] = hexDigits[(bytes[i] >>> 4) & 15];
            index = high + 1;
            result[high] = hexDigits[bytes[i] & 15];
        }
        return new String(result);
    }

    private static boolean isJson(String text) {
        try {
            String trimmed = text.trim();
            if (trimmed.startsWith("{")) {
                new JSONObject(trimmed);
                return true;
            }
            if (trimmed.startsWith("[")) {
                new JSONArray(trimmed);
                return true;
            }
            return false;
        } catch (JSONException e) {
            return false;
        }
    }

    public static boolean isMatch(String regex, String input) {
        return !isEmpty(input) && Pattern.matches(regex, input);
    }

    public static boolean isEmail(String text) {
        return isMatch(REGEX_EMAIL, text);
    }

    public static boolean isMobileNum(String text) {
        return isMatch(REGEX_MOBILE_EXACT, text);
    }

    public static boolean isIdCard(String text) {
        return isMatch(REGEX_IDCARD18, text);
    }

    public static boolean isIp(String text) {
        return isMatch(REGEX_IP, text) || isMatch(REGEX_IPV6, text);
    }
}