package com.xtc.utils_screenshot_carry_data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.util.Log;

/**
 * 截图携带数据 Provider 基类。
 */
public abstract class BaseScreenshotProvide extends ContentProvider {

    public static final int CODE_SCREENSHOT = 1;
    public static final String GET_SCREENSHOT_DATA = "getScreenshotData";

    private static final String TAG = "BaseScreenshotProvide";
    private static final String SCREENSHOT_PACKAGE = "%s.ScreenshotProvide";
    private static final String SCREENSHOT_RESULT = "ScreenshotResult";

    protected static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    @Override
    public boolean onCreate() {
        Log.d(TAG, "onCreate: getContext().getPackageName()=" + getContext().getPackageName());
        uriMatcher.addURI(String.format(SCREENSHOT_PACKAGE, getContext().getPackageName()),
                GET_SCREENSHOT_DATA, CODE_SCREENSHOT);
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (uriMatcher.match(uri) != CODE_SCREENSHOT || projection == null || projection.length == 0) {
            return null;
        }
        MatrixCursor cursor = new MatrixCursor(new String[]{SCREENSHOT_RESULT});
        MatrixCursor.RowBuilder row = cursor.newRow();
        String carryData = ScreenshotUtils.getScreenshotCarryData(projection[0]);
        Log.d(TAG, "query: getScreenshotData carryData=" + carryData);
        row.add(carryData);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}