package com.xtc.qiniu;

import android.content.Context;
import android.text.TextUtils;

import com.xtc.log.LogUtil;
import com.xtc.qiniu.bean.UploadBean;
import com.xtc.utils.encode.UUIDUtil;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/** Queues the uploads so no more than {@code mMaxUploadLimit} run at a time. */
public class UploadLimitAgent {

    public static final int UPLOAD_LIMIT_SIX = 6;

    private static final String TAG = "qiniu_upload_UploadLimitAgent";

    private static volatile UploadLimitAgent instance;

    public int mMaxUploadLimit = UPLOAD_LIMIT_SIX;

    private final ConcurrentHashMap<String, Character> mUploadingMap = new ConcurrentHashMap<>(this.mMaxUploadLimit);
    private final ConcurrentLinkedQueue<UploadBean> mNeedUploadQueue = new ConcurrentLinkedQueue<>();

    private UploadLimitAgent() {
    }

    public static UploadLimitAgent getInstance() {
        if (instance == null) {
            synchronized (UploadLimitAgent.class) {
                if (instance == null) {
                    instance = new UploadLimitAgent();
                }
            }
        }
        return instance;
    }

    public void setMaxUploadLimit(int maxUploadLimit) {
        this.mMaxUploadLimit = maxUploadLimit;
    }

    private ICloudManager getCloudManagerInstance(Context context) {
        return QiNiuManager.getInstance(context.getApplicationContext(), ICloudService.qiNiuDomain);
    }

    public String upLoadFileByCover(Context context, int spaceType, String key, File file,
            ICloudManager.OnUpLoadListener listener) {
        String tag = UUIDUtil.randomUUID();
        if (isLimit()) {
            this.mNeedUploadQueue.offer(new UploadBean(tag, Constants.UploadMethod.UPLOAD_COVER, context, spaceType,
                    key, file, listener));
            return tag;
        }
        this.mUploadingMap.put(key, '1');
        return getCloudManagerInstance(context).uploadFileByCover(tag, spaceType, key, file,
                new CommonOnUpLoadListener(listener));
    }

    public String upLoadFile(Context context, int spaceType, String key, File file,
            ICloudManager.OnUpLoadListener listener) {
        String tag = UUIDUtil.randomUUID();
        if (isLimit()) {
            this.mNeedUploadQueue.offer(new UploadBean(tag, Constants.UploadMethod.UPLOAD_SPACE, context, spaceType,
                    key, file, listener));
            return tag;
        }
        this.mUploadingMap.put(key, '1');
        return getCloudManagerInstance(context).uploadFile(tag, spaceType, key, file,
                new CommonOnUpLoadListener(listener));
    }

    public String uploadFile(Context context, String filePath, String key, String token,
            ICloudManager.OnUpLoadListener listener) {
        String tag = UUIDUtil.randomUUID();
        if (isLimit()) {
            this.mNeedUploadQueue.offer(new UploadBean(tag, Constants.UploadMethod.UPLOAD_NO_SPACE, context, key,
                    filePath, token, listener));
            return tag;
        }
        this.mUploadingMap.put(key, '1');
        return getCloudManagerInstance(context).uploadFile(tag, filePath, key, token,
                new CommonOnUpLoadListener(listener));
    }

    private boolean isLimit() {
        return this.mUploadingMap.size() >= this.mMaxUploadLimit;
    }

    public void cancel(String key) {
        LogUtil.d(TAG, "cancel: key = [" + key + "]");
        if (TextUtils.isEmpty(key)) {
            return;
        }
        this.mUploadingMap.remove(key);
    }

    private void checkNeedUploadNext() {
        if (isLimit()) {
            LogUtil.d(TAG, "checkNeedUploadNext: still limit");
            return;
        }
        if (this.mNeedUploadQueue.isEmpty()) {
            return;
        }
        UploadBean bean = this.mNeedUploadQueue.poll();
        if (bean == null) {
            checkNeedUploadNext();
            return;
        }
        LogUtil.d(TAG, "checkNeedUploadNext: nextUploadBean = [" + bean + "]");
        ICloudManager manager = getCloudManagerInstance(bean.getContext());
        this.mUploadingMap.put(bean.getKey(), '1');
        if (bean.getUploadMethod() == Constants.UploadMethod.UPLOAD_COVER) {
            manager.uploadFileByCover(bean.getTag(), bean.getSpaceType(), bean.getKey(), bean.getFile(),
                    new CommonOnUpLoadListener(bean.getOnUpLoadListener()));
            return;
        }
        if (bean.getUploadMethod() == Constants.UploadMethod.UPLOAD_SPACE) {
            manager.uploadFile(bean.getTag(), bean.getSpaceType(), bean.getKey(), bean.getFile(),
                    new CommonOnUpLoadListener(bean.getOnUpLoadListener()));
        } else if (bean.getUploadMethod() == Constants.UploadMethod.UPLOAD_NO_SPACE) {
            manager.uploadFile(bean.getTag(), bean.getFilePath(), bean.getKey(), bean.getToken(),
                    new CommonOnUpLoadListener(bean.getOnUpLoadListener()));
        } else {
            LogUtil.d(TAG, "checkNeedUploadNext: no uploadMethod");
        }
    }

    /** Forwards the callback and starts the next queued upload. */
    class CommonOnUpLoadListener implements ICloudManager.OnUpLoadListener {

        private final ICloudManager.OnUpLoadListener listener;

        public CommonOnUpLoadListener(ICloudManager.OnUpLoadListener listener) {
            this.listener = listener;
        }

        @Override
        public void onProgress(String key, double percent) {
            if (this.listener != null) {
                this.listener.onProgress(key, percent);
            }
        }

        @Override
        public void onSuccess(String key) {
            if (!TextUtils.isEmpty(key)) {
                UploadLimitAgent.this.mUploadingMap.remove(key);
            }
            if (this.listener != null) {
                this.listener.onSuccess(key);
            }
            UploadLimitAgent.this.checkNeedUploadNext();
        }

        @Override
        public void onFailure(String key, int code, String message) {
            if (!TextUtils.isEmpty(key)) {
                UploadLimitAgent.this.mUploadingMap.remove(key);
            }
            if (this.listener != null) {
                this.listener.onFailure(key, code, message);
            }
            UploadLimitAgent.this.checkNeedUploadNext();
        }
    }
}