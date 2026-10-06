package com.xtc.ui.widget.util;

import com.xtc.ui.widget.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/** Maps {@link com.xtc.ui.widget.UiConstants.Icon} ids to drawable resources. */
public class UiIconUtil {

    /** Marks parameters that must be one of the {@code UiConstants.Icon} ids. */
    @Retention(RetentionPolicy.SOURCE)
    public @interface IconType {
    }

    public static int getIconResourceId(int iconConstant) {
        switch (iconConstant) {
            case 0:
                return R.drawable.cancel;
            case 1:
                return R.drawable.sure;
            case 2:
                return R.drawable.delete;
            case 3:
                return R.drawable.phone_take;
            case 4:
                return R.drawable.phone_refuse;
            case 5:
                return R.drawable.single_not_select;
            case 6:
                return R.drawable.single_select;
            case 7:
                return R.drawable.multiple_not_select;
            case 8:
                return R.drawable.multiple_select;
            case 9:
                return R.drawable.check_select;
            case 10:
                return R.drawable.right_arrow;
            case 11:
                return R.drawable.play;
            case 12:
                return R.drawable.video_communication;
            case 13:
                return R.drawable.small_video;
            case 14:
                return R.drawable.shut_down;
            case 15:
                return R.drawable.restart;
            case 16:
                return R.drawable.collection;
            case 17:
                return R.drawable.uncollected;
            case 18:
                return R.drawable.remark;
            case 19:
                return R.drawable.voice_communication;
            case 20:
                return R.drawable.phone_take_green;
            case 21:
                return R.drawable.ic_continue;
            case 22:
                return R.drawable.ic_netflow;
            case 23:
                return R.drawable.ic_wifidownload;
            case 24:
                return R.drawable.ic_disconnect;
            case 25:
                return R.drawable.ic_cancel_focus;
            case 26:
                return R.drawable.ic_unbind;
            default:
                return R.drawable.cancel;
        }
    }
}
