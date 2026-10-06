package com.xtc.bigdata.collector.exception;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;

import com.bumptech.glide.load.Key;
import com.xtc.bigdata.collector.ShareHelper;
import com.xtc.bigdata.collector.encapsulation.entity.event.ExceptionEvent;
import com.xtc.bigdata.collector.utils.MD5Coder;
import com.xtc.bigdata.common.constants.Constants;
import com.xtc.bigdata.common.constants.EType;
import com.xtc.bigdata.common.utils.ContextUtils;
import com.xtc.bigdata.common.utils.StoreUtils;
import com.xtc.log.LogUtil;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Date;

/** Intent service that records a crash forwarded from another process. */
public class CrashIntentService extends IntentService {

    private static final String STACK_TRACE = "STACK_TRACE";
    private static final String STACK_TRACE_SEPARATOR = "=";
    private static final String TAG = "CrashIntentService";
    private static final String EXTRA_CRASH_INFO = "CRASH_INFO";

    private Object lock = new Object();

    public CrashIntentService() {
        super(TAG);
        this.lock = new Object();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String stack;
        String stackMd5;
        boolean collect;
        if (intent != null) {
            Throwable throwable = (Throwable) intent.getSerializableExtra(EXTRA_CRASH_INFO);
            synchronized (this.lock) {
                StringWriter stringWriter = new StringWriter();
                PrintWriter printWriter = new PrintWriter(stringWriter);
                throwable.printStackTrace(printWriter);
                for (Throwable cause = throwable.getCause(); cause != null; cause = cause.getCause()) {
                    cause.printStackTrace(printWriter);
                }
                stack = stringWriter.toString();
                printWriter.close();
                LogUtil.d(TAG, "logTxt:" + stack);
                try {
                    stackMd5 = new MD5Coder().encode(stack.getBytes(Key.STRING_CHARSET_NAME));
                } catch (Exception e) {
                    if (Constants.isDebug) {
                        LogUtil.e(TAG, e);
                    }
                    stackMd5 = "";
                }
                collect = CrashHandler.isCollect(throwable);
            }
            if (!collect || TextUtils.isEmpty(stackMd5) || TextUtils.isEmpty(stack)) {
                return;
            }
            String stackTrace = formatStackTrace(stack);
            if (Constants.isDebug) {
                LogUtil.e(TAG, stackTrace);
            }
            ExceptionEvent exceptionEvent = new ExceptionEvent();
            exceptionEvent.reason = throwable.toString() + stackMd5;
            exceptionEvent.stack = stackTrace;
            long internalStoreTotalSize = StoreUtils.getInternalStoreTotalSize();
            long internalStoreAvailableSize = StoreUtils.getInternalStoreAvailableSize();
            exceptionEvent.diskTotal = StoreUtils.convertSizeUnit(internalStoreTotalSize);
            exceptionEvent.diskUsage = StoreUtils.availablepercent(internalStoreAvailableSize, internalStoreTotalSize);
            long externalStoreTotalSize = StoreUtils.getExternalStoreTotalSize();
            long externalStoreAvailableSize = StoreUtils.getExternalStoreAvailableSize();
            exceptionEvent.sdTotal = StoreUtils.convertSizeUnit(externalStoreTotalSize);
            exceptionEvent.sdUsage = StoreUtils.availablepercent(externalStoreAvailableSize, externalStoreTotalSize);
            long memoryTotalSize = StoreUtils.getMemoryTotalSize();
            long memoryAvailable = StoreUtils.getMemoryAvailable(ContextUtils.getContext());
            exceptionEvent.memTotal = StoreUtils.convertSizeUnit(StoreUtils.getMemoryTotalSize());
            exceptionEvent.memUsage = StoreUtils.availablepercent(memoryAvailable, memoryTotalSize);
            exceptionEvent.functionName = EType.NAME_APP_EXCEPTION;
            exceptionEvent.dataCollectLevel = "B";
            exceptionEvent.dataSecurityLevel = "C";
            exceptionEvent.makeData();
            ShareHelper.getInstance().insert(exceptionEvent.getContentValues());
        }
    }

    private String formatStackTrace(String stack) {
        return TextUtils.concat("#", "\n", "#", new Date().toString(), "\n", STACK_TRACE, STACK_TRACE_SEPARATOR, stack).toString();
    }
}