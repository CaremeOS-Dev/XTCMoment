package com.xtc.anim.alphaplayer.utils;

import android.content.res.Resources;
import android.opengl.GLES20;

import com.bumptech.glide.load.Key;
import com.xtc.log.LogUtil;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * OpenGL 着色器工具：负责加载 asset 中的着色器源码、编译与链接。
 */
public class ShaderUtil {

    private static final String TAG = "ES20_ERROR";

    /** 从 assets 读取着色器源码，并统一换行符。 */
    public static String loadShaderSource(String fileName, Resources resources) {
        String result = null;
        try {
            InputStream inputStream = resources.getAssets().open(fileName);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            int value;
            while ((value = inputStream.read()) != -1) {
                outputStream.write(value);
            }
            byte[] bytes = outputStream.toByteArray();
            outputStream.close();
            inputStream.close();
            String content = new String(bytes, Key.STRING_CHARSET_NAME);
            try {
                return content.replaceAll("\\r\\n", "\n");
            } catch (Exception e) {
                result = content;
                e.printStackTrace();
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
    }

    /** 编译顶点与片元着色器并链接为程序对象。 */
    public static int createProgram(String vertexSource, String fragmentSource) {
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        if (vertexShader == 0) {
            return 0;
        }
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        if (fragmentShader == 0) {
            return 0;
        }
        int program = GLES20.glCreateProgram();
        if (program != 0) {
            GLES20.glAttachShader(program, vertexShader);
            checkGlError("glAttachShader");
            GLES20.glAttachShader(program, fragmentShader);
            checkGlError("glAttachShader");
            GLES20.glLinkProgram(program);
            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] != 1) {
                LogUtil.e(TAG, "Could not link program: ");
                LogUtil.e(TAG, GLES20.glGetProgramInfoLog(program));
                GLES20.glDeleteProgram(program);
                return 0;
            }
        }
        return program;
    }

    private static int loadShader(int type, String source) {
        int shader = GLES20.glCreateShader(type);
        if (shader == 0) {
            return shader;
        }
        GLES20.glShaderSource(shader, source);
        GLES20.glCompileShader(shader);
        int[] compiled = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] != 0) {
            return shader;
        }
        LogUtil.e(TAG, "Could not compile shader " + type + ":");
        LogUtil.e(TAG, GLES20.glGetShaderInfoLog(shader));
        GLES20.glDeleteShader(shader);
        return 0;
    }

    /** 检查 GL 错误，出错时抛出运行时异常。 */
    public static void checkGlError(String operation) {
        int error = GLES20.glGetError();
        if (error == 0) {
            return;
        }
        LogUtil.e(TAG, operation + ": glError " + error);
        throw new RuntimeException(operation + ": glError " + error);
    }
}