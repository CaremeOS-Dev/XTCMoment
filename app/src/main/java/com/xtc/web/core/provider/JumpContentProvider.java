package com.xtc.web.core.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Bundle;

import com.xtc.web.core.jump.JumpManager;
import com.xtc.web.core.utils.JSONUtil;

import java.util.HashMap;

/** 把 JumpManager 收到的 Activity 结果以 ContentProvider 形式回传给 Web 组件。 */
public class JumpContentProvider extends ContentProvider {

    private static final int MATCH_CODE = 101;
    private static final String PATH_JUMP_RESULT = "jumpActivityResult";
    private static UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private Context mContext;
    private HashMap<String, Object> map;

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
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
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        if (context == null) {
            return false;
        }
        this.mContext = context.getApplicationContext();
        if (this.mContext == null) {
            this.mContext = context;
        }
        uriMatcher.addURI(getFileProviderName(this.mContext), PATH_JUMP_RESULT, MATCH_CODE);
        this.map = new HashMap<>();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (uriMatcher.match(uri) != MATCH_CODE) {
            return null;
        }
        Bundle data = JumpManager.getData();
        for (String key : data.keySet()) {
            this.map.put(key, data.get(key));
        }
        String json = JSONUtil.toJSON(this.map);
        MatrixCursor cursor = new MatrixCursor(new String[]{"result"});
        cursor.addRow(new Object[]{json});
        return cursor;
    }

    public static String getFileProviderName(Context context) {
        return context.getPackageName() + ".JumpContentProvider";
    }
}