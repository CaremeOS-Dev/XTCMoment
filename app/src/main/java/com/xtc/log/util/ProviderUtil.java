package com.xtc.log.util;

import android.content.Context;
import android.net.Uri;

/**
 * Reads the device-wide sensitive-keyword list from the data-center provider.
 *
 * <p>The provider exposes the list through the MIME type of a well-known path
 * rather than through a cursor, which is why this uses {@code getType}.
 */
public class ProviderUtil {

    private static final String AUTHORITY = "com.xtc.datacenter.SensitiveProvider";
    private static final Uri CONTENT_URI = Uri.parse("content://com.xtc.datacenter.SensitiveProvider/");
    private static final String URI_SENSITIVE_DATA = "data/sensitiveData";

    public static String[] getSensitiveList(Context context) throws IllegalArgumentException, NullPointerException {
        String type = context.getContentResolver().getType(Uri.parse(CONTENT_URI + URI_SENSITIVE_DATA));
        if (TextUtils.isEmpty(type)) {
            return null;
        }
        return type.split("、");
    }
}
