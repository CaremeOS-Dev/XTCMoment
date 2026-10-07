package com.ss.ugc.android.alpha_player.utils;

import android.content.res.Resources;
import android.opengl.GLES20;
import com.badlogic.gdx.graphics.GL20;
import com.bumptech.glide.load.Key;
import com.xtc.log.LogUtil;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/* JADX INFO: loaded from: classes2.dex */
public class ShaderUtil {
    public static String a(String str, Resources resources) {
        String str2 = null;
        try {
            InputStream inputStreamOpen = resources.getAssets().open(str);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            while (true) {
                int i = inputStreamOpen.read();
                if (i != -1) {
                    byteArrayOutputStream.write(i);
                } else {
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    byteArrayOutputStream.close();
                    inputStreamOpen.close();
                    String str3 = new String(byteArray, Key.a);
                    try {
                        return str3.replaceAll("\\r\\n", "\n");
                    } catch (Exception e) {
                        str2 = str3;
                        e = e;
                    }
                }
                e.printStackTrace();
                return str2;
            }
        } catch (Exception e2) {
            e = e2;
        }
    }

    public static int a(String str, String str2) {
        int iA;
        int iA2 = a(GL20.bL, str);
        if (iA2 == 0 || (iA = a(GL20.bK, str2)) == 0) {
            return 0;
        }
        int iGlCreateProgram = GLES20.glCreateProgram();
        if (iGlCreateProgram != 0) {
            GLES20.glAttachShader(iGlCreateProgram, iA2);
            a("glAttachShader");
            GLES20.glAttachShader(iGlCreateProgram, iA);
            a("glAttachShader");
            GLES20.glLinkProgram(iGlCreateProgram);
            int[] iArr = new int[1];
            GLES20.glGetProgramiv(iGlCreateProgram, GL20.bV, iArr, 0);
            if (iArr[0] != 1) {
                LogUtil.e("ES20_ERROR", "Could not link program: ");
                LogUtil.e("ES20_ERROR", GLES20.glGetProgramInfoLog(iGlCreateProgram));
                GLES20.glDeleteProgram(iGlCreateProgram);
                return 0;
            }
        }
        return iGlCreateProgram;
    }

    private static int a(int i, String str) {
        int iGlCreateShader = GLES20.glCreateShader(i);
        if (iGlCreateShader == 0) {
            return iGlCreateShader;
        }
        GLES20.glShaderSource(iGlCreateShader, str);
        GLES20.glCompileShader(iGlCreateShader);
        int[] iArr = new int[1];
        GLES20.glGetShaderiv(iGlCreateShader, GL20.dZ, iArr, 0);
        if (iArr[0] != 0) {
            return iGlCreateShader;
        }
        LogUtil.e("ES20_ERROR", "Could not compile shader " + i + ":");
        LogUtil.e("ES20_ERROR", GLES20.glGetShaderInfoLog(iGlCreateShader));
        GLES20.glDeleteShader(iGlCreateShader);
        return 0;
    }

    public static void a(String str) {
        int iGlGetError = GLES20.glGetError();
        if (iGlGetError == 0) {
            return;
        }
        LogUtil.e("ES20_ERROR", str + ": glError " + iGlGetError);
        throw new RuntimeException(str + ": glError " + iGlGetError);
    }
}
