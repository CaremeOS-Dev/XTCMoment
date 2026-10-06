package com.xtc.log.util;

import android.os.Looper;
import android.os.Process;

import com.xtc.log.IStackLogger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.UnknownHostException;

/**
 * Builds the call-site information that {@code StandardOutLogger} prints when
 * the app runs off-device (there is no logcat to fall back on).
 */
public final class StackUtils {

    /** Separator placed between the method name and the line number. */
    private static final String POSITION_SEPARATOR = "-";

    private StackUtils() {
        throw new AssertionError();
    }

    public static IStackLogger.StackInfo makeStackInfo(int depth, int pid, long mainTid) {
        StackTraceElement element = getStackTraceOfPosition(depth);
        return new IStackLogger.StackInfo(
                element.getFileName(),
                element.getClassName(),
                element.getMethodName(),
                element.getLineNumber(),
                pid,
                Thread.currentThread().getId(),
                mainTid);
    }

    /** Returns the frame {@code depth} levels up, or the oldest frame if too shallow. */
    private static StackTraceElement getStackTraceOfPosition(int depth) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        int length = stackTrace.length;
        if (length > depth) {
            return stackTrace[depth];
        }
        return stackTrace[length - 1];
    }

    public static IStackLogger.StackInfo makeAndroidStackInfo(int depth) {
        return makeStackInfo(depth, Process.myPid(), Looper.getMainLooper().getThread().getId());
    }

    public static String generateConsoleMessage(IStackLogger.StackInfo stackInfo, String message) {
        String className = stackInfo._className;
        return "{"
                + className.substring(className.lastIndexOf('.') + 1)
                + "." + stackInfo._methodName
                + POSITION_SEPARATOR + stackInfo._lineNumber
                + "} " + message;
    }

    public static String getStackTraceString(Throwable throwable) {
        if (throwable == null) {
            return "";
        }
        for (Throwable cause = throwable; cause != null; cause = cause.getCause()) {
            if (cause instanceof UnknownHostException) {
                return "";
            }
        }
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        throwable.printStackTrace(printWriter);
        printWriter.flush();
        return stringWriter.toString();
    }
}
