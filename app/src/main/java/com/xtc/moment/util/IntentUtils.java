package com.xtc.moment.util;

import android.app.Activity;
import android.content.Intent;

import com.xtc.funlist.util.FuncUtil;
import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.module.Constants;
import com.xtc.moment.module.bean.ShareVideoMoment;
import com.xtc.moment.module.publish.PublishActivity;
import com.xtc.utils.encode.JSONUtil;
import com.xtc.web.core.CoreConstants;

public class IntentUtils {

    private static final String TAG = "IntentUtils";

    public static void startFunVideoActivity(final Activity activity, ShareVideoMoment.FunVideoParam funVideoParam) {
        final Intent intent = new Intent();
        if (1 == funVideoParam.getModelType()) {
            intent.setAction(Constants.FunPhoto.ACTION_NAME);
        } else {
            intent.setAction(Constants.FunPhoto.ACTION_VIDEO_NAME);
        }
        intent.putExtra(Constants.ShareVideoKey.FUN_VIDEO_PARAM, JSONUtil.toJSON(funVideoParam));
        Intent publishIntent = new Intent(activity, (Class<?>) PublishActivity.class);
        publishIntent.putExtra(CoreConstants.TakePhotoConstant.TARGET_APP, PublishActivity.class.getSimpleName());
        publishIntent.putExtra(CoreConstants.TakePhotoConstant.REQUEST, 4);
        intent.putExtra(CoreConstants.TakePhotoConstant.TARGET_INTENT, publishIntent.toURI());
        HandlerUtil.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                activity.startActivityForResult(intent, 4);
            }
        });
        LogUtil.d(TAG, "startFunVideoActivity: activity = " + activity + " ;modelType = " + funVideoParam);
    }

    public static void go2VideoTarget(final Activity activity) {
        if (activity == null) {
            return;
        }
        boolean isAvailable = FunPhotoUtils.isAvailable(activity);
        if (FuncUtil.supportFunShortVideo() && isAvailable) {
            ShareVideoMoment.FunVideoParam funVideoParam = new ShareVideoMoment.FunVideoParam();
            funVideoParam.setModelType(2);
            startFunVideoActivity(activity, funVideoParam);
            return;
        }
        final Intent intent = new Intent();
        intent.setAction("android.media.action.VIDEO_CAPTURE");
        intent.putExtra(CoreConstants.TakePhotoConstant.LEFT_BUTTON_TEXT, activity.getString(R.string.cancel));
        intent.putExtra(CoreConstants.TakePhotoConstant.RIGHT_BUTTON_TEXT, activity.getString(R.string.complete));
        Intent publishIntent = new Intent(activity, (Class<?>) PublishActivity.class);
        publishIntent.putExtra(CoreConstants.TakePhotoConstant.TARGET_APP, PublishActivity.class.getSimpleName());
        publishIntent.putExtra(CoreConstants.TakePhotoConstant.REQUEST, 3);
        intent.putExtra(CoreConstants.TakePhotoConstant.TARGET_INTENT, publishIntent.toURI());
        HandlerUtil.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                activity.startActivityForResult(intent, 3);
            }
        });
    }
}