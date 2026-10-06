package com.xtc.moment.module.publish.multi.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.xtc.funlist.util.FuncUtil;
import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.constants.ModuleSwitchConstant;
import com.xtc.moment.module.Constants;
import com.xtc.moment.module.bean.ShareVideoMoment;
import com.xtc.moment.module.publish.multi.PushPictureActivity;
import com.xtc.moment.third.behavior.MomentBehavior;
import com.xtc.moment.util.FunPhotoUtils;
import com.xtc.moment.util.SystemUtil;
import com.xtc.moment.util.switchs.ModuleSwitchUtil;
import com.xtc.system.account.constant.NotificationFlag;
import com.xtc.utils.encode.JSONUtil;
import com.xtc.web.core.CoreConstants;

/**
 * Dialog offering the three publish entries: album, camera and video.
 */
public class ThreeIconDialog extends Dialog {

    private static final String TAG = "ThreeIconDialog";

    /** Request code of the album flow. */
    private static final int REQUEST_ALBUM = 2;
    /** Request code of the camera flow. */
    private static final int REQUEST_CAMERA = 1;
    /** Request code of the video flow. */
    private static final int REQUEST_VIDEO = 3;
    /** Request code of the "fun video" flow. */
    private static final int REQUEST_FUN_VIDEO = 4;
    /** Model type reported to the fun video app. */
    private static final int FUN_VIDEO_MODEL_TYPE = 2;
    /** Max pictures the album picker allows. */
    private final int maxCount;

    private final Activity context;

    private RelativeLayout albumView;
    private RelativeLayout PhotoView;
    private RelativeLayout videoView;

    public ThreeIconDialog(Activity activity, int maxCount) {
        super(activity, R.style.three_dialog_style);
        this.context = activity;
        this.maxCount = maxCount;
        initView();
    }

    private void initView() {
        setContentView(R.layout.dialog_three_icon_view);
        this.albumView = (RelativeLayout) findViewById(R.id.rl_dialog_album);
        this.PhotoView = (RelativeLayout) findViewById(R.id.rl_dialog_photo);
        this.videoView = (RelativeLayout) findViewById(R.id.rl_dialog_video);
        this.albumView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAlbum();
                MomentBehavior.addDynamicType(getContext(), 2);
                dismiss();
            }
        });
        this.PhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCamera();
                MomentBehavior.addDynamicType(getContext(), 1);
                dismiss();
            }
        });
        this.videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openVideo();
                MomentBehavior.addDynamicType(getContext(), 3);
                dismiss();
            }
        });
    }

    /** Opens the fun video app when available, otherwise the system video recorder. */
    private void openVideo() {
        LogUtil.i(TAG, "趣味视频");
        boolean funPhotoAvailable = FunPhotoUtils.isAvailable(this.context);
        if (FuncUtil.supportFunShortVideo() && funPhotoAvailable) {
            ShareVideoMoment.FunVideoParam funVideoParam = new ShareVideoMoment.FunVideoParam();
            funVideoParam.setModelType(FUN_VIDEO_MODEL_TYPE);
            Intent intent = new Intent();
            intent.setAction(Constants.FunPhoto.ACTION_VIDEO_NAME);
            intent.putExtra(Constants.ShareVideoKey.FUN_VIDEO_PARAM, JSONUtil.toJSON(funVideoParam));
            Intent target = new Intent(this.context, PushPictureActivity.class);
            target.putExtra(CoreConstants.TakePhotoConstant.TARGET_APP, PushPictureActivity.class.getSimpleName());
            target.putExtra(CoreConstants.TakePhotoConstant.REQUEST, REQUEST_FUN_VIDEO);
            intent.putExtra(CoreConstants.TakePhotoConstant.TARGET_INTENT, target.toURI());
            this.context.startActivityForResult(intent, REQUEST_FUN_VIDEO);
            return;
        }
        Intent intent = new Intent();
        intent.setAction("android.media.action.VIDEO_CAPTURE");
        intent.putExtra(CoreConstants.TakePhotoConstant.LEFT_BUTTON_TEXT, this.context.getString(R.string.cancel));
        intent.putExtra(CoreConstants.TakePhotoConstant.RIGHT_BUTTON_TEXT, this.context.getString(R.string.complete));
        Intent target = new Intent(this.context, PushPictureActivity.class);
        target.putExtra(CoreConstants.TakePhotoConstant.TARGET_APP, PushPictureActivity.class.getSimpleName());
        target.putExtra(CoreConstants.TakePhotoConstant.REQUEST, REQUEST_VIDEO);
        intent.putExtra(CoreConstants.TakePhotoConstant.TARGET_INTENT, target.toURI());
        this.context.startActivityForResult(intent, REQUEST_VIDEO);
    }

    public void openCamera() {
        SystemUtil.setEnterFuncFlag();
        Intent intent = new Intent();
        intent.setAction("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(CoreConstants.TakePhotoConstant.LEFT_BUTTON_TEXT, this.context.getString(R.string.cancel));
        intent.putExtra(CoreConstants.TakePhotoConstant.RIGHT_BUTTON_TEXT, this.context.getString(R.string.complete));
        Intent target = new Intent(this.context, PushPictureActivity.class);
        target.putExtra(CoreConstants.TakePhotoConstant.TARGET_APP, PushPictureActivity.class.getSimpleName());
        target.putExtra(CoreConstants.TakePhotoConstant.REQUEST, REQUEST_CAMERA);
        target.setFlags(NotificationFlag.NOTIFICATION_FLAG_BOOT_USE);
        intent.putExtra(CoreConstants.TakePhotoConstant.TARGET_INTENT, target.toURI());
        this.context.startActivityForResult(intent, REQUEST_CAMERA);
    }

    public void openAlbum() {
        SystemUtil.setEnterFuncFlag();
        Intent intent = new Intent();
        intent.setAction("android.intent.action.GET_CONTENT");
        if (ModuleSwitchUtil.queryModuleSwitchByBoolean(this.context,
                ModuleSwitchConstant.MODULE_SWITCH_MOMENT_SEND_VIDEO_FUNCTION, false)) {
            intent.setType("file/*");
        } else {
            intent.setType(CoreConstants.TakePhotoConstant.TYPE_IMAGE);
        }
        intent.putExtra("com.xtc.camera.SELECT_PHOTO_NUM", this.maxCount);
        intent.putExtra(CoreConstants.TakePhotoConstant.RIGHT_BUTTON_TEXT, this.context.getString(R.string.complete));
        Intent target = new Intent(this.context, PushPictureActivity.class);
        target.putExtra(CoreConstants.TakePhotoConstant.TARGET_APP, PushPictureActivity.class.getSimpleName());
        target.putExtra(CoreConstants.TakePhotoConstant.REQUEST, REQUEST_ALBUM);
        target.setFlags(NotificationFlag.NOTIFICATION_FLAG_BOOT_USE);
        intent.putExtra(CoreConstants.TakePhotoConstant.TARGET_INTENT, target.toURI());
        this.context.startActivityForResult(intent, REQUEST_ALBUM);
    }

    @Override
    public void show() {
        super.show();
        Window window = getWindow();
        if (window == null) {
            return;
        }
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = WindowManager.LayoutParams.MATCH_PARENT;
        attributes.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.getDecorView().setPadding(0, 0, 0, 0);
        window.setAttributes(attributes);
    }
}