package com.xtc.moment.module.publish;

import android.content.Context;
import android.content.Intent;

import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.constants.ModuleSwitchConstant;
import com.xtc.moment.module.publish.multi.PushPictureActivity;
import com.xtc.moment.util.SystemUtil;
import com.xtc.moment.util.switchs.ModuleSwitchUtil;
import com.xtc.system.account.constant.NotificationFlag;
import com.xtc.web.core.CoreConstants;

/**
 * Builds the intents that hand control to the camera/album apps and route their results back to
 * the moment publish flow.
 */
public class StartCamera {

    private static final String TAG = "StartCamera";
    private static final String TARGET_APP = "targetApp";
    private static final String SELECT_PHOTO_NUM = "com.xtc.camera.SELECT_PHOTO_NUM";

    /** Intent that opens the system camera in single photo mode. */
    public static Intent openCamera(Context context) {
        LogUtil.d(TAG, "左键 点击--");
        SystemUtil.setEnterFuncFlag();
        Intent intent = new Intent();
        intent.setAction("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(CoreConstants.TakePhotoConstant.LEFT_BUTTON_TEXT, context.getString(R.string.cancel));
        intent.putExtra(CoreConstants.TakePhotoConstant.RIGHT_BUTTON_TEXT, context.getString(R.string.complete));
        Intent target = new Intent(context, PublishActivity.class);
        target.putExtra(TARGET_APP, PublishActivity.class.getSimpleName());
        target.putExtra(CoreConstants.TakePhotoConstant.REQUEST, 1);
        target.setFlags(NotificationFlag.NOTIFICATION_FLAG_BOOT_USE);
        intent.putExtra(CoreConstants.TakePhotoConstant.TARGET_INTENT, target.toURI());
        return intent;
    }

    /** Intent that opens the album picker; {@code request} -1 keeps the default selection count. */
    public static Intent openAlbum(Context context, int request, int selectCount) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.GET_CONTENT");
        if (ModuleSwitchUtil.queryModuleSwitchByBoolean(context,
                ModuleSwitchConstant.MODULE_SWITCH_MOMENT_SEND_VIDEO_FUNCTION, false)) {
            intent.setType("file/*");
        } else {
            intent.setType(CoreConstants.TakePhotoConstant.TYPE_IMAGE);
        }
        LogUtil.i(TAG, "getCount number" + selectCount);
        if (request != -1) {
            intent.putExtra(SELECT_PHOTO_NUM, selectCount);
        }
        intent.putExtra(CoreConstants.TakePhotoConstant.RIGHT_BUTTON_TEXT, context.getString(R.string.complete));
        Intent target = new Intent(context, PushPictureActivity.class);
        target.putExtra(TARGET_APP, PushPictureActivity.class.getSimpleName());
        target.putExtra(CoreConstants.TakePhotoConstant.REQUEST, 2);
        target.setFlags(NotificationFlag.NOTIFICATION_FLAG_BOOT_USE);
        intent.putExtra(CoreConstants.TakePhotoConstant.TARGET_INTENT, target.toURI());
        return intent;
    }

    /** Intent that opens the system video recorder. */
    public static Intent videoResult(Context context) {
        Intent intent = new Intent();
        intent.setAction("android.media.action.VIDEO_CAPTURE");
        intent.putExtra(CoreConstants.TakePhotoConstant.LEFT_BUTTON_TEXT, context.getString(R.string.cancel));
        intent.putExtra(CoreConstants.TakePhotoConstant.RIGHT_BUTTON_TEXT, context.getString(R.string.publish));
        Intent target = new Intent(context, PublishActivity.class);
        target.putExtra(TARGET_APP, PublishActivity.class.getSimpleName());
        target.putExtra(CoreConstants.TakePhotoConstant.REQUEST, 3);
        intent.putExtra(CoreConstants.TakePhotoConstant.TARGET_INTENT, target.toURI());
        return intent;
    }
}