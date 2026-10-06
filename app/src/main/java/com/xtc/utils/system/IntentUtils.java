package com.xtc.utils.system;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.webkit.MimeTypeMap;

import java.io.File;

/** Factory methods for the intents used throughout the app. */
public class IntentUtils {

    /** {@code Intent.FLAG_ACTIVITY_NEW_TASK}. */
    private static final int FLAG_NEW_TASK = 0x10000000;
    /** {@code Intent.FLAG_GRANT_READ_URI_PERMISSION}. */
    private static final int FLAG_GRANT_READ = 0x1;
    private static final String APK_MIME_TYPE = "application/vnd.android.package-archive";

    private IntentUtils() {
        throw new UnsupportedOperationException("u can't fuck me...");
    }

    /** Opens an installed package file. */
    public static Intent openFile(String path) {
        return openFile(PrivateUtils.toFile(path));
    }

    /** Opens an installed package file. */
    public static Intent openFile(File file) {
        if (file == null) {
            return null;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String mimeType;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mimeType = APK_MIME_TYPE;
        } else {
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(PrivateUtils.getExtension(file.getPath()));
        }
        intent.setDataAndType(Uri.fromFile(file), mimeType);
        return intent.addFlags(FLAG_NEW_TASK);
    }

    /** Prompts the user to uninstall the given package. */
    public static Intent uninstall(String packageName) {
        Intent intent = new Intent(Intent.ACTION_DELETE);
        intent.setData(Uri.parse("package:" + packageName));
        return intent.addFlags(FLAG_NEW_TASK);
    }

    /** Launcher intent for the given package, or null. */
    public static Intent getLaunchIntent(Context context, String packageName) {
        return context.getPackageManager().getLaunchIntentForPackage(packageName);
    }

    /** Opens the app-details settings page for the given package. */
    public static Intent appDetailsSettings(String packageName) {
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.parse("package:" + packageName));
        return intent.addFlags(FLAG_NEW_TASK);
    }

    /** Shares plain text. */
    public static Intent shareText(String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        return intent.setFlags(FLAG_NEW_TASK);
    }

    /** Shares text together with a file attachment. */
    public static Intent shareTextWithFile(String text, String filePath) {
        return shareTextWithFile(text, PrivateUtils.toFile(filePath));
    }

    /** Shares text together with a file attachment. */
    public static Intent shareTextWithFile(String text, File file) {
        if (PrivateUtils.exists(file)) {
            return shareTextWithUri(text, Uri.fromFile(file));
        }
        return null;
    }

    /** Shares text together with a content URI. */
    public static Intent shareTextWithUri(String text, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setType("image/*");
        return intent.setFlags(FLAG_NEW_TASK);
    }

    /** Explicit view intent for {@code packageName/className}. */
    public static Intent viewComponent(String packageName, String className) {
        return viewComponent(packageName, className, null);
    }

    /** Explicit view intent carrying {@code extras}. */
    public static Intent viewComponent(String packageName, String className, Bundle extras) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (extras != null) {
            intent.putExtras(extras);
        }
        intent.setComponent(new ComponentName(packageName, className));
        return intent.addFlags(FLAG_NEW_TASK);
    }

    /** Shutdown broadcast intent. */
    public static Intent shutdown() {
        return new Intent(Intent.ACTION_SHUTDOWN).addFlags(FLAG_NEW_TASK);
    }

    /** Camera capture intent writing to {@code outputUri}. */
    public static Intent captureImage(Uri outputUri) {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra("output", outputUri);
        return intent.addFlags(FLAG_NEW_TASK | FLAG_GRANT_READ);
    }
}