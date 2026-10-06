package com.xtc.bigdata.collector;

import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.net.Uri;

import com.xtc.bigdata.common.error.ErrorCode;
import com.xtc.bigdata.common.utils.ContextUtils;
import com.xtc.bigdata.common.utils.UriUtils;
import com.xtc.log.LogUtil;

/** Writes the collected events into the behaviour provider. */
public class ShareHelper {

    private static final String TAG = "ShareHelper";
    private static ShareHelper instance;

    private ShareHelper() {
    }

    public static ShareHelper getInstance() {
        ShareHelper helper = instance;
        if (helper != null) {
            return helper;
        }
        synchronized (ShareHelper.class) {
            if (instance == null) {
                instance = new ShareHelper();
            }
        }
        return instance;
    }

    private ContentProviderClient acquireContentProviderClient(Uri uri) {
        return ContextUtils.getContext().getContentResolver().acquireUnstableContentProviderClient(uri);
    }

    /** Inserts a single row. */
    public void insert(ContentValues contentValues) {
        if (contentValues == null || ContextUtils.isEmpty()) {
            return;
        }
        Uri contentUri = UriUtils.getContentUri(ContextUtils.getContext());
        ContentProviderClient client = null;
        try {
            client = acquireContentProviderClient(contentUri);
            client.insert(contentUri, contentValues);
        } catch (Exception e) {
            LogUtil.e(TAG, ErrorCode.CONTROL_INIT_PROVIDER);
            e.printStackTrace();
        } finally {
            if (client != null) {
                client.release();
            }
        }
    }

    /** Inserts multiple rows. */
    public void insertBulk(ContentValues[] contentValues) {
        if (contentValues == null || contentValues.length == 0 || ContextUtils.isEmpty()) {
            return;
        }
        Uri contentUri = UriUtils.getContentUri(ContextUtils.getContext());
        ContentProviderClient client = null;
        try {
            client = acquireContentProviderClient(contentUri);
            client.bulkInsert(contentUri, contentValues);
        } catch (Exception e) {
            LogUtil.e(TAG, ErrorCode.CONTROL_INIT_PROVIDER);
            e.printStackTrace();
        } finally {
            if (client != null) {
                client.release();
            }
        }
    }

    /** Deletes all collected rows. */
    public boolean deleteAll() throws Exception {
        Uri contentUri = UriUtils.getContentUri(ContextUtils.getContext());
        ContentProviderClient client = null;
        try {
            client = acquireContentProviderClient(contentUri);
            return client.delete(contentUri, null, null) > 0;
        } catch (Exception e) {
            LogUtil.e(TAG, ErrorCode.CONTROL_INIT_PROVIDER);
            e.printStackTrace();
            return false;
        } finally {
            if (client != null) {
                client.release();
            }
        }
    }

    /** Notifies the provider that the home key was pressed. */
    public void pressHomeKeyNotify() {
        if (ContextUtils.isEmpty()) {
            return;
        }
        Uri uri = UriUtils.getPressHomeKeyContentUri(ContextUtils.getContext());
        ContentProviderClient client = null;
        try {
            client = acquireContentProviderClient(uri);
            client.getType(uri);
        } catch (Exception e) {
            LogUtil.e(TAG, ErrorCode.CONTROL_INIT_PROVIDER);
            e.printStackTrace();
        } finally {
            if (client != null) {
                client.release();
            }
        }
    }

    /** Notifies the provider that real-time upload is requested. */
    public void realTimeNotify() {
        if (ContextUtils.isEmpty()) {
            return;
        }
        Uri uri = UriUtils.getRealTimeContentUri(ContextUtils.getContext());
        ContentProviderClient client = null;
        try {
            client = acquireContentProviderClient(uri);
            client.getType(uri);
        } catch (Exception e) {
            LogUtil.e(TAG, ErrorCode.CONTROL_INIT_PROVIDER);
            e.printStackTrace();
        } finally {
            if (client != null) {
                client.release();
            }
        }
    }

    /** Queries collected data of the given package. */
    public void queryData(String packageName) {
        if (ContextUtils.isEmpty()) {
            return;
        }
        Uri uri = UriUtils.getQueryDataContentUri(packageName);
        ContentProviderClient client = null;
        try {
            client = acquireContentProviderClient(uri);
            client.getType(uri);
        } catch (Throwable throwable) {
            LogUtil.e(TAG, ErrorCode.CONTROL_INIT_PROVIDER);
            throwable.printStackTrace();
        } finally {
            if (client != null) {
                client.release();
            }
        }
    }

    /** Notifies the provider to report data of the given package. */
    public void reportData(String packageName) {
        if (ContextUtils.isEmpty()) {
            return;
        }
        Uri uri = UriUtils.getReportDataContentUri(packageName);
        ContentProviderClient client = null;
        try {
            client = acquireContentProviderClient(uri);
            client.getType(uri);
        } catch (Exception e) {
            LogUtil.e(TAG, ErrorCode.CONTROL_INIT_PROVIDER);
            e.printStackTrace();
        } finally {
            if (client != null) {
                client.release();
            }
        }
    }
}