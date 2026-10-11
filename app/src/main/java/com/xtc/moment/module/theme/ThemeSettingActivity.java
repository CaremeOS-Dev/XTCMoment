package com.xtc.moment.module.theme;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.util.SystemUtil;
import com.xtc.moment.util.ToastUtil;
import com.xtc.system.account.constant.NotificationFlag;
import com.xtc.ui.widget.item.clickScale.RightSwitchItemWithExtraInfo;
import com.xtc.ui.widget.privacy.setting.AbstractSettingActivity;
import com.xtc.ui.widget.toggleswitch.SwitchButton;
import com.xtc.web.core.CoreConstants;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 * 主题设置页：支持启用自定义背景并从小天才相册选择背景图片。
 */
public class ThemeSettingActivity extends AbstractSettingActivity {

    private static final String TAG = ThemeSettingActivity.class.getSimpleName();
    private static final String TARGET_APP = "targetApp";
    private static final String SELECT_PHOTO_NUM = "com.xtc.camera.SELECT_PHOTO_NUM";
    private static final String EXTRA_PHOTO_TYP = "com.xtc.camera.EXTRA_PHOTO_TYPE";
    private static final String SELECT_PHOTO_LIST = "com.xtc.camera.SELECT_PHOTO_LIST";
    private static final String EXTRA_OUTPUT = "output";
    private static final String EXTRA_RESULT = "RESULT";
    private static final int ALBUM_MAX_COUNT = 1;
    private static final int REQUEST_PICK_BACKGROUND = 2;
    private static final int PHOTO_TYPE_LIVE = 2;

    private RightSwitchItemWithExtraInfo mEnableSwitchItem;
    private SwitchButton mSwitchButton;
    private boolean mInitializing;

    public static void start(Context context) {
        context.startActivity(new Intent(context, ThemeSettingActivity.class));
    }

    @Override
    protected int setContentViewId() {
        return R.layout.activity_theme_setting;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        this.mEnableSwitchItem = (RightSwitchItemWithExtraInfo) findViewById(R.id.theme_enable_switch_item);
        // 本项不需要左侧图标，隐藏预留的图标位，避免标题被挤窄换行、左侧留白。
        this.mEnableSwitchItem.setLeftIconVisible(false, false);
        this.mEnableSwitchItem.getTvTitle().setText(R.string.enable_custom_background);
        this.mSwitchButton = this.mEnableSwitchItem.getSwitchButton();
        this.mSwitchButton.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton switchButton, boolean isChecked) {
                if (ThemeSettingActivity.this.mInitializing) {
                    return;
                }
                ThemeSettingActivity.this.onSwitchChanged(isChecked);
            }
        });
        // 整行可点，避免只有开关小区域响应。
        this.mEnableSwitchItem.getRootView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ThemeSettingActivity.this.mSwitchButton.toggle();
            }
        });
        setSwitchChecked(ThemeBackgroundHelper.isEnabled(this));
        setItemViewStateByViewId(R.id.theme_custom_bg_rii, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemUtil.isFastDoubleClick()) {
                    LogUtil.i(TAG, "onClick: click too fast.");
                    return;
                }
                openAlbum();
            }
        });
        refreshSummary();
    }

    /** 同步开关显示状态，期间屏蔽状态变化回调，避免初始化触发落盘。 */
    private void setSwitchChecked(boolean checked) {
        this.mInitializing = true;
        this.mSwitchButton.setChecked(checked);
        this.mInitializing = false;
    }

    /** 开关状态变化：打开时若无壁纸则先引导选图，关闭时恢复默认背景。 */
    private void onSwitchChanged(boolean isChecked) {
        if (isChecked && !ThemeBackgroundHelper.hasWallpaper(this)) {
            openAlbum();
            return;
        }
        applyEnableState(isChecked);
    }

    /** 落盘开关状态并立即刷新当前页面与其它已打开页面的背景。 */
    private void applyEnableState(boolean enabled) {
        ThemeBackgroundHelper.setEnabled(this, enabled);
        ThemeBackgroundHelper.refresh(this);
        ThemeBackgroundHelper.apply(this);
        EventBus.getDefault().post(new ThemeChangedEvent());
        refreshSummary();
    }

    /** 相册取消选择时把开关回弹为关闭。 */
    private void revertSwitchIfNoWallpaper() {
        if (ThemeBackgroundHelper.hasWallpaper(this)) {
            return;
        }
        ThemeBackgroundHelper.setEnabled(this, false);
        setSwitchChecked(false);
        refreshSummary();
    }

    private void refreshSummary() {
        this.mEnableSwitchItem.getTvExtra().setText(ThemeBackgroundHelper.hasWallpaper(this)
                ? R.string.custom_background_set
                : R.string.custom_background_not_set);
    }

    /** 打开小天才相册选择背景图片。 */
    private void openAlbum() {
        SystemUtil.setEnterFuncFlag();
        Intent pickIntent = new Intent();
        pickIntent.setAction("android.intent.action.GET_CONTENT");
        pickIntent.setType(CoreConstants.TakePhotoConstant.TYPE_IMAGE);
        pickIntent.putExtra(SELECT_PHOTO_NUM, ALBUM_MAX_COUNT);
        pickIntent.putExtra(CoreConstants.TakePhotoConstant.RIGHT_BUTTON_TEXT, getString(R.string.complete));
        Intent targetIntent = new Intent(this, ThemeSettingActivity.class);
        targetIntent.putExtra(TARGET_APP, ThemeSettingActivity.class.getSimpleName());
        targetIntent.putExtra(CoreConstants.TakePhotoConstant.REQUEST, REQUEST_PICK_BACKGROUND);
        targetIntent.setFlags(NotificationFlag.NOTIFICATION_FLAG_BOOT_USE);
        pickIntent.putExtra(CoreConstants.TakePhotoConstant.TARGET_INTENT, targetIntent.toURI());
        startActivityForResult(pickIntent, REQUEST_PICK_BACKGROUND);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQUEST_PICK_BACKGROUND) {
            return;
        }
        if (resultCode != RESULT_OK || data == null) {
            revertSwitchIfNoWallpaper();
            return;
        }
        handlePickedImage(data.getExtras());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (intent == null) {
            return;
        }
        String targetApp = intent.getStringExtra(TARGET_APP);
        if (TextUtils.isEmpty(targetApp) || !ThemeSettingActivity.class.getSimpleName().equals(targetApp)) {
            return;
        }
        if (intent.getIntExtra(CoreConstants.TakePhotoConstant.REQUEST, -1) != REQUEST_PICK_BACKGROUND) {
            return;
        }
        if (intent.getIntExtra(EXTRA_RESULT, -1) != RESULT_OK) {
            revertSwitchIfNoWallpaper();
            return;
        }
        handlePickedImage(intent.getExtras());
    }

    /** 解析相册返回值并保存为壁纸。 */
    private void handlePickedImage(Bundle extras) {
        if (extras == null) {
            LogUtil.i(TAG, "handlePickedImage: extras is null");
            revertSwitchIfNoWallpaper();
            return;
        }
        if (extras.getInt(EXTRA_PHOTO_TYP) == PHOTO_TYPE_LIVE) {
            ToastUtil.showLong(this, getString(R.string.custom_background_live_not_support));
            revertSwitchIfNoWallpaper();
            return;
        }
        String photoPath = null;
        ArrayList<String> photoPaths = extras.getStringArrayList(SELECT_PHOTO_LIST);
        if (photoPaths != null && !photoPaths.isEmpty()) {
            photoPath = photoPaths.get(0);
        }
        if (TextUtils.isEmpty(photoPath)) {
            photoPath = extras.getString(EXTRA_OUTPUT, null);
        }
        LogUtil.i(TAG, "handlePickedImage: photoPath = " + photoPath);
        if (TextUtils.isEmpty(photoPath) || !ThemeBackgroundHelper.saveWallpaper(this, photoPath)) {
            ToastUtil.showShort(this, getString(R.string.custom_background_failed));
            revertSwitchIfNoWallpaper();
            return;
        }
        applyEnableState(true);
        ToastUtil.showShort(this, getString(R.string.custom_background_set));
    }

    @Override
    protected void onResume() {
        super.onResume();
        ThemeBackgroundHelper.refresh(this);
        ThemeBackgroundHelper.applyIfEnabled(this);
        if (this.mEnableSwitchItem != null) {
            // 从相册返回或页面重建后，以落盘状态为准同步开关显示。
            setSwitchChecked(ThemeBackgroundHelper.isEnabled(this));
            refreshSummary();
        }
    }
}
