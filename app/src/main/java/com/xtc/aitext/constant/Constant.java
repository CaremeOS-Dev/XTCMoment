package com.xtc.aitext.constant;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * AI 文案模块常量。
 */
public interface Constant {

    /** 日志前缀。 */
    String LOG_PREFIX = "ai_text_";
    /** 模块号。 */
    int MODULE_ID = 3438;
    /** IM 消息类型。 */
    int IM_MSG_TYPE = 49002;
    /** 视频资源目录。 */
    String ASSET_VIDEO_DIR = "video";
    /** 画笔动画视频名。 */
    String ASSET_PAINT_VIDEO = "play_paint.mp4";
    /** 刷新数据广播。 */
    String ACTION_REFRESH_DATA = "action_refresh_data";
    /** IM 接收广播。 */
    String ACTION_AI_TEXT_IM_RECEIVER = "action_aitext_im_receiver";
    /** 创建服务 Action。 */
    String ACTION_AI_TEXT_SERVICE = "com.xtc.action_aitext.service";
    /** IM 序列化实体 Extra。 */
    String EXTRA_SERIALIZE_IM_BEAN = "extra_serialize_im_bean";
    /** 应用内容 Extra。 */
    String EXTRA_APP_CONTENT = "extra_app_content";

    /** 点击行为状态。 */
    @interface ActionStatus {
        int ACTION_OVER_NUMBER = 1;
        int ACTION_MIDDLE_SCREEN = 2;
    }

    /** 按钮调用状态。 */
    @interface CallStatus {
        int CALL_CLICK = 0;
        int CALL_DEFAULT = 1;
    }

    /** 弹窗按钮点击状态。 */
    @interface ClickStatus {
        int CLICK_REQUEST_NETWORK = 1;
        int CLICK_JUMP = 2;
    }

    /** Intent 传参键。 */
    interface IntentExtras {
        String EXTRA_SELECT_STYLE = "extra_select_style";
        String EXTRA_SELECT_STYLE_LIST = "extra_select_style_list";
        String EXTRA_REFRESH_DATA = "extra_refresh_data";
    }

    /** 领取结果。 */
    @interface ObtainResult {
        int OBTAIN_FAIL = 0;
        int OBTAIN_SUCCEED = 1;
        int OBTAIN_DIALOG_TEXT = 2;
    }

    /** 领取状态。 */
    @interface ObtainStatus {
        int TYPE_IN_OBTAIN = 0;
        int TYPE_CAN_OBTAIN = 1;
        int TYPE_NO_OBTAIN = 2;
    }

    /** 绘画弹窗展示类型。 */
    @interface PainShowType {
        int TYPE_CHECK_INPUT_FAIL = 0;
        int TYPE_CHECK_INPUT_SUCCESS = 1;
        int TYPE_OUT_SUCCESS = 2;
        int TYPE_OUT_FAIL = 3;
        int TYPE_START_CREATE = 4;
        int TYPE_CHECK_INPUT_NET_FAIL = 5;
    }

    /** 系统属性键。 */
    @interface Property {
        String HAS_AGREE_PERMISSION = "persist.sys.aitext.agree";
    }

    /** 本地存储键。 */
    @interface SharedConstants {
        String SHARE_AI_FIRST = "share_ai_first";
        String SHARE_HAS_AGREE_PERMISSION = "share_has_agree_permission";
        String SHARE_LAST_STYLE = "share_last_style";
    }
}