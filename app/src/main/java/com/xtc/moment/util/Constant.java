package com.xtc.moment.util;

/** Shared-preferences keys of the moment app. */
public class Constant {

    private Constant() {
    }

    /** Keys of the moment supervision state. */
    interface MomentSupervise {
        String BAN_PUBLISH_STATUS = "ban_publish_status";
        String FIRST_USE_MOMENT_CAMERA = "first_use_moment_camera";
        String FIRST_USE_MOMENT_PHOTO = "first_use_moment_photo";
        String FIRST_USE_MOMENT_TEXT = "first_use_moment_text";
        String FIRST_USE_MOMENT_VIDEO = "first_use_moment_video";
        String IS_COPY_ASSETS_FILE = "is_copy_assets_file";
    }
}