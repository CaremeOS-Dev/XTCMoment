package com.xtc.moment.db;

import android.net.Uri;

/** Constants of the app-data sync provider. */
public interface SyncDataConstant {

    /** Broadcast sent when the sync finished. */
    interface BroadCastConstant {
        String ACTION_SYNC_DATA_STATUS = "com.xtc.i3launcher.action.SYNC_DATA_STATUS";
        String KEY_APP_NAME = "app_name";
        String KEY_SYNC_STATUS = "sync_status";
    }

    /** Contract of the {@code com.xtc.appdata} provider. */
    interface ProviderConstant {
        Uri DATA_PROVIDER_URI = Uri.parse("content://com.xtc.appdata");
        String GET_DB_METHOD = "getDb";
        String GET_SP_METHOD = "getSp";
        String KEY_FILE_PATH = "file_path";
        String KEY_FILE_STREAM = "file_byte";
        String TRANSFER_TYPE = "transfer_type";
        int TYPE_FILE_NONE = 3;
        int TYPE_FILE_PATH = 2;
        int TYPE_FILE_STREAM = 1;
    }

    /** Names of the temporary files used during a sync. */
    interface TempConstant {
        String DB_ALIASES = "moment_db";
        String DB_TEMP = "temp.db";
        String SP_TEMP = "temp.xml";
    }
}