package com.xtc.bigdata.common.error;

import java.util.HashMap;
import java.util.Map;

/** Error codes reported by the big-data collector. */
public class ErrorCode extends ErrorCodeStandard {
    public static final String CACHE_EVENT_NULL_POINTER = "02020601";
    public static final String CONTROL_ILLEGAL_ARGUMENT_RECORD_FUNCTION = "02010301";
    public static final String CONTROL_INIT_CRASH = "02010102";
    public static final String CONTROL_INIT_DOREPORT = "02040102";
    public static final String CONTROL_INIT_INSERT = "02010101";
    public static final String CONTROL_INIT_PROVIDER = "02030101";
    public static final String CONTROL_INIT_REPORT = "02040101";
    public static final String CONTROL_NULL_POINTER_APPLICATION = "02010606";
    public static final String CONTROL_NULL_POINTER_BEHAVIOR_CONFIG = "02010605";
    public static final String CONTROL_NULL_POINTER_CACHE_CONFIG = "02010601";
    public static final String CONTROL_NULL_POINTER_EVENT_ATTR = "02010602";
    public static final String CONTROL_NULL_POINTER_IEVENT = "02010603";
    public static final String CONTROL_NULL_POINTER_PAGE_FUNCTIONNAME = "02010604";
    public static final String CONTROL_PAGE_FUNCTIONNAME_MULTIDEX = "02010203";
    public static final String CONTROL_PAGE_ORDER = "02010201";
    public static final String CONTROL_RECORD_FUNCTION_ORDER = "02010202";
    public static final String REPORT_BFC_UPLOAD_INIT_EXCEPTION = "02040105";
    public static final String REPORT_CREATE_FILE_ERROR = "0204ff05";
    public static final String REPORT_CREATE_ZIP_ERROR = "0204ff06";
    public static final String REPORT_HOME_KEY_REG = "02040103";
    public static final String REPORT_HOME_KEY_UNREG = "02040104";
    public static final String REPORT_ILLEGAL_MODE_START_FIXED_TIME = "0204ff01";
    public static final String REPORT_ILLEGAL_MODE_START_PUSH = "0204ff02";
    public static final String REPORT_INSUFFICIENT_REMAINING_SPACE = "0204ff08";
    public static final String REPORT_NO_NET = "02040401";
    public static final String REPORT_ONLY_WIFI = "02040402";
    public static final String REPORT_TASK_CONTEXT_NULL = "02040601";
    public static final String REPORT_THE_MODE_ALREADY_CLOSED = "0204ff03";
    public static final String REPORT_UNKNOW_FILE_TYPE = "0204ff07";
    public static final String REPORT_ZIP_NOT_EXISTS = "02040603";
    public static final String REPORT_ZIP_NULL = "02040602";

    private static Map<String, String> errorCodes;

    public static String logErrorCode() {
        return null;
    }

    public static synchronized Map<String, String> getErrorCodes() {
        if (errorCodes != null) {
            return errorCodes;
        }
        errorCodes = new HashMap<>();
        errorCodes.put(CONTROL_INIT_INSERT, "数据入库失败，请先调用BehaviorCollector.getInstance().init()进行初始化");
        errorCodes.put(CONTROL_INIT_CRASH, "异常捕获模块初始化失败");
        errorCodes.put(CONTROL_PAGE_ORDER, "在调用 pawo qgeEnd() 前必须要先调用 pageBegin()");
        errorCodes.put(CONTROL_RECORD_FUNCTION_ORDER, "在调用 recordFunctionEnd() 前必须要先调用 recordFunctionBegin()");
        errorCodes.put(CONTROL_PAGE_FUNCTIONNAME_MULTIDEX, "数据入库失败，该functionName已经存在");
        errorCodes.put(CONTROL_ILLEGAL_ARGUMENT_RECORD_FUNCTION, "记录的时间有问题，计算得到使用时长小于0");
        errorCodes.put(CONTROL_NULL_POINTER_CACHE_CONFIG, "缓存配置设置失败，cacheConfig不能为空！");
        errorCodes.put(CONTROL_NULL_POINTER_EVENT_ATTR, "EventAttr参数不能为空");
        errorCodes.put(CONTROL_NULL_POINTER_IEVENT, "数据入库失败，数据为空");
        errorCodes.put(CONTROL_NULL_POINTER_PAGE_FUNCTIONNAME, "functionName不能为空");
        errorCodes.put(CONTROL_NULL_POINTER_BEHAVIOR_CONFIG, "配置信息设置失败，behaviorConfig不能为空");
        errorCodes.put(CONTROL_NULL_POINTER_APPLICATION, "Application context不能设置为空");
        errorCodes.put(CACHE_EVENT_NULL_POINTER, "数据数据为空，无法缓存上报");
        errorCodes.put(CONTROL_INIT_PROVIDER, "数据库provider初始化异常");
        errorCodes.put(CONTROL_INIT_REPORT, "上报模块初始化失败");
        errorCodes.put(CONTROL_INIT_DOREPORT, "初始化异常，无法执行上报操作");
        errorCodes.put(REPORT_HOME_KEY_REG, "注册监听home键失败");
        errorCodes.put(REPORT_HOME_KEY_UNREG, "注销home键监听失败");
        errorCodes.put(REPORT_BFC_UPLOAD_INIT_EXCEPTION, "数据上报失败,bfc-uploadsdk异常,数据可能丢失");
        errorCodes.put(REPORT_NO_NET, "没有网络，无法执行上报操作");
        errorCodes.put(REPORT_ONLY_WIFI, "当前非wifi网络状态，仅在wifi下上报");
        errorCodes.put(REPORT_TASK_CONTEXT_NULL, "上传参数有问题!! context = null");
        errorCodes.put(REPORT_ZIP_NULL, "上传参数有问题!! zipFile = null");
        errorCodes.put(REPORT_ZIP_NOT_EXISTS, "上传参数有问题!! zipFile文件不存在:");
        errorCodes.put(REPORT_ILLEGAL_MODE_START_FIXED_TIME, "收到定时上报消息，但当前非定时上报模式，不做处理");
        errorCodes.put(REPORT_ILLEGAL_MODE_START_PUSH, "收到定时上报消息，但当前非推送上报模式，不做处理");
        errorCodes.put(REPORT_THE_MODE_ALREADY_CLOSED, "此上报模式尚未开启");
        errorCodes.put(REPORT_CREATE_FILE_ERROR, "创建上传文件失败");
        errorCodes.put(REPORT_CREATE_ZIP_ERROR, "创建上传文件压缩失败");
        errorCodes.put(REPORT_UNKNOW_FILE_TYPE, "上报行为采集数据不支持类型,\u3000taskType:");
        errorCodes.put(REPORT_INSUFFICIENT_REMAINING_SPACE, "磁盘剩余空间不足,上报失败");
        return errorCodes;
    }

    private ErrorCode() {
    }
}