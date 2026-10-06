package com.xtc.moment.dress;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.opensource.svgaplayer.SVGACallback;
import com.opensource.svgaplayer.SVGADrawable;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;
import com.xtc.log.LogUtil;
import com.xtc.moment.MomentApp;
import com.xtc.moment.R;
import com.xtc.moment.db.bean.DbHead;
import com.xtc.moment.module.bean.DressBean;
import com.xtc.moment.util.DressUtil;
import com.xtc.moment.util.HandlerUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

/**
 * 头像挂件装扮管理器，支持 PNG 与 SVGA 动态头像。
 */
public class HeadDressManager extends BaseDressManager {

    private static final String TAG = HeadDressManager.class.getSimpleName();
    private static final int DEFAULT_LOOPS = 3;

    private static volatile HeadDressManager mInstance;
    private static HashMap<String, DressBean> dressHeadHashMap;
    private static HashMap<String, SVGAVideoEntity> svgHeadHashMap;

    private final SVGAParser parser;

    private HeadDressManager() {
        dressHeadHashMap = new HashMap<>();
        svgHeadHashMap = new HashMap<>();
        this.parser = new SVGAParser(MomentApp.getAppContext());
    }

    public static HeadDressManager getInstance() {
        if (mInstance == null) {
            synchronized (HeadDressManager.class) {
                if (mInstance == null) {
                    mInstance = new HeadDressManager();
                }
            }
        }
        return mInstance;
    }

    public synchronized void updateHeadDressData(Context context, HashMap<String, DbHead> headMap) {
        if (headMap == null || context == null) {
            return;
        }
        dressHeadHashMap.clear();
        for (Map.Entry<String, DbHead> entry : headMap.entrySet()) {
            DbHead head = headMap.get(entry.getKey());
            if (head == null) {
                continue;
            }
            boolean isSvga = head.getMovementType() == 2;
            dressHeadHashMap.put(entry.getKey(), new DressBean(entry.getKey(), head.getHeadId(),
                    head.getSourcePath(), isSvga, head.getCarouseNum()));
            LogUtil.d(TAG, "设置头像装扮数据  watchId: " + entry.getKey() + "  头像路径:" + head.getSourcePath()
                    + "  是否为动态头像：" + isSvga);
        }
    }

    public synchronized void setDressHead(Context context, String watchId, SVGAImageView imageView) {
        setDressHead(context, watchId, imageView, 0);
    }

    public synchronized void setDressHead(Context context, String watchId, SVGAImageView imageView, int loopCount) {
        if (imageView == null) {
            return;
        }
        DressBean dressBean = dressHeadHashMap.get(watchId);
        if (context == null || dressBean == null) {
            imageView.setImageDrawable(null);
            return;
        }
        if (dressBean.isSvga()) {
            if (loopCount > 0) {
                dressBean.setLoopCount(loopCount);
            }
            setDressHeadWithSvg(context, dressBean, imageView);
        } else {
            setDressHeadWithPng(context, dressBean, imageView);
        }
    }

    private void setDressHeadWithSvg(Context context, final DressBean dressBean, final SVGAImageView imageView) {
        if (imageView == null) {
            return;
        }
        final String svgaPath = dressBean.getDressPath() + DressUtil.Head.FILE_NAME_HEAD_SVGA;
        imageView.setTag(R.id.head_key, svgaPath);
        SVGAVideoEntity entity = svgHeadHashMap.get(svgaPath);
        if (entity != null) {
            LogUtil.i(TAG, "setDressHeadWithSvg hit entity");
            imageView.setTag(R.id.head_key, svgaPath);
            playDynamicHead(entity, imageView, dressBean.getLoopCount());
            return;
        }
        imageView.setTag(R.id.head_key, svgaPath);
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                asyncLoadHeadDress(dressBean, imageView, svgaPath);
            }
        });
    }

    private void asyncLoadHeadDress(final DressBean dressBean, final SVGAImageView imageView, final String svgaPath) {
        File file = new File(svgaPath);
        LogUtil.i(TAG, "setDressHeadWithSvg dressPath:" + svgaPath);
        try {
            FileInputStream inputStream = new FileInputStream(file);
            if (!file.exists()) {
                LogUtil.w(TAG, "setDressHeadWithSvg file is not exists");
                return;
            }
            this.parser.decodeFromInputStream(inputStream, file.getName(), new SVGAParser.ParseCompletion() {
                @Override
                public void onComplete(SVGAVideoEntity videoItem) {
                    Object tag = imageView.getTag(R.id.head_key);
                    if (!svgaPath.equals(tag)) {
                        LogUtil.w(TAG, "当前SVGAImageView持有的TAG值与需加载的值不一致，不做加载  loadFlag value：" + tag
                                + "  load value:" + svgaPath);
                        return;
                    }
                    playDynamicHead(videoItem, imageView, dressBean.getLoopCount());
                    svgHeadHashMap.put(svgaPath, videoItem);
                }

                @Override
                public void onError() {
                    LogUtil.i(TAG, "SVGAParser Error");
                }
            }, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            LogUtil.i(TAG, "SVGAParser Error FileNotFoundException");
        }
    }

    private void setDressHeadWithPng(Context context, DressBean dressBean, SVGAImageView imageView) {
        String pngPath = dressBean.getDressPath() + DressUtil.Head.FILE_NAME_HEAD_PNG;
        LogUtil.i(TAG, "setDressHeadWithPng dressPath:" + pngPath);
        Glide.with(context).asDrawable().load(pngPath)
                .apply(new RequestOptions().signature(new ObjectKey(dressBean.getDressId())))
                .into((ImageView) imageView);
    }

    private void playDynamicHead(SVGAVideoEntity entity, SVGAImageView imageView, int loopCount) {
        imageView.setImageDrawable(new SVGADrawable(entity));
        if (loopCount <= 0) {
            loopCount = DEFAULT_LOOPS;
        }
        imageView.setLoops(loopCount);
        imageView.setClearsAfterStop(false);
        imageView.startAnimation();
        addVisibleImageView(imageView);
        if (imageView.getCallback() != null) {
            return;
        }
        imageView.setCallback(new SVGACallback() {
            @Override
            public void onStep(int frame, double percentage) {
            }

            @Override
            public void onPause() {
                LogUtil.i(TAG, "动态头像停止播放");
            }

            @Override
            public void onFinished() {
                LogUtil.i(TAG, "动态头像完成播放");
            }

            @Override
            public void onRepeat() {
                LogUtil.i(TAG, "动态头像循环播放");
            }
        });
    }

    public void clearDynamicHeadData() {
        svgHeadHashMap.clear();
    }
}