package com.xtc.moment.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.xtc.log.LogUtil;

public class VirtualSelfReleaseUtil {

    private static final String TAG = "VirtualSelfReleaseUtil";

    private static int chatActivityCount;

    public static void onCreate() {
        HandlerUtil.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                chatActivityCount++;
                LogUtil.d(TAG, "onCreate() called : chatActivityCount = " + chatActivityCount);
            }
        });
    }

    public static void onDestroy() {
        HandlerUtil.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                chatActivityCount--;
                LogUtil.d(TAG, "onDestroy() called : chatActivityCount = " + chatActivityCount);
                if (chatActivityCount == 0) {
                    releaseGdx();
                }
            }
        });
    }

    private static void releaseGdx() {
        LogUtil.d(TAG, "releaseGdx");
        if (Gdx.app == null) {
            return;
        }
        ShaderProgram.clearAllShaderPrograms(Gdx.app);
        Gdx.app = null;
        Gdx.graphics = null;
        Gdx.audio = null;
        Gdx.input = null;
        Gdx.files = null;
        Gdx.net = null;
        Gdx.gl = null;
        Gdx.gl20 = null;
        Gdx.gl30 = null;
    }
}