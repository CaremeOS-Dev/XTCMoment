package com.xtc.moment.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Shader;
import android.widget.TextView;

import com.opensource.svgaplayer.SVGAImageView;
import com.xtc.log.LogUtil;
import com.xtc.moment.db.bean.DbHead;
import com.xtc.moment.db.bean.DbNickname;
import com.xtc.moment.dress.HeadDressManager;

import java.io.File;
import java.util.HashMap;

/**
 * 昵称底色与头像挂件装扮工具。
 */
public class DressUtil {

    private static final String TAG = "DressUtil";

    private static final HashMap<String, Bitmap> nicknameBitmapMap = new HashMap<>();
    private static final HashMap<String, DbNickname> dbNicknameHashMap = new HashMap<>();
    private static final HeadDressManager headDressManager = HeadDressManager.getInstance();

    public interface Head {
        String FILE_NAME_HEAD_PNG = "bg_head_pendant.png";
        String FILE_NAME_HEAD_SVGA = "bg_head_pendant.svga";
    }

    public interface NicknameName {
        String FILE_NAME = "bg_nickname.png";
    }

    public static synchronized void setNicknameSource(final String watchId, final TextView textView) {
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                DbNickname nickname = dbNicknameHashMap.get(watchId);
                if (nickname == null) {
                    return;
                }
                Bitmap bitmap;
                if (nicknameBitmapMap.containsKey(nickname.getNicknameId())) {
                    bitmap = nicknameBitmapMap.get(nickname.getNicknameId());
                } else {
                    bitmap = BitmapFactory.decodeFile(nickname.getSourcePath() + NicknameName.FILE_NAME);
                    nicknameBitmapMap.put(nickname.getNicknameId(), bitmap);
                }
                LogUtil.i(TAG, "setNicknameSource bitmap: " + bitmap);
                if (bitmap == null) {
                    return;
                }
                final BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
                LogUtil.i(TAG, "setNicknameSource Shader: " + shader);
                HandlerUtil.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.getPaint().setShader(shader);
                        textView.invalidate();
                    }
                });
            }
        });
    }

    public static boolean isSourceExist(String dirPath, int expectedCount) {
        File dir = new File(dirPath);
        String[] children;
        if (dir.isDirectory() && (children = dir.list()) != null && children.length > 0
                && children.length == expectedCount) {
            return true;
        }
        dir.delete();
        return false;
    }

    public static synchronized void setDressHead(Context context, String watchId, SVGAImageView imageView) {
        if (imageView == null) {
            return;
        }
        headDressManager.setDressHead(context, watchId, imageView);
    }

    public static synchronized void removeDressHead(SVGAImageView imageView) {
        if (imageView == null) {
            return;
        }
        headDressManager.viewDetachedFromWindow(imageView);
    }

    public static void updateDbNickName(HashMap<String, DbNickname> nicknameMap) {
        if (nicknameMap != null) {
            dbNicknameHashMap.putAll(nicknameMap);
        }
    }

    public static synchronized void updateDbHead(Context context, HashMap<String, DbHead> headMap) {
        headDressManager.updateHeadDressData(context, headMap);
    }
}