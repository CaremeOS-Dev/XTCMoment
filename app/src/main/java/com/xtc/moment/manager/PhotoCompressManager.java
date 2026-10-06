package com.xtc.moment.manager;

import android.text.TextUtils;

import com.xtc.log.LogUtil;
import com.xtc.moment.module.Constants;
import com.xtc.moment.module.bean.PhotoMsg;
import com.xtc.moment.module.bean.PhotoTokenParam;
import com.xtc.moment.util.ImageUtil;
import com.xtc.utils.common.CollectionUtil;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 图片压缩任务构造器。
 */
public class PhotoCompressManager {

    private static final String TAG = "PushPictureActivity";

    public static Observable<PhotoMsg>[] createCompressObservables(ArrayList<PhotoMsg> photoMsgs,
            final ArrayList<String> compressedPaths) {
        if (CollectionUtil.isEmpty(photoMsgs)) {
            LogUtil.i(TAG, "createMsgObservables error");
            return null;
        }
        Observable<PhotoMsg>[] observables = new Observable[photoMsgs.size()];
        int size = photoMsgs.size();
        for (int index = 0; index < size; index++) {
            observables[index] = Observable.just(photoMsgs.get(index)).map(new Func1<PhotoMsg, PhotoMsg>() {
                @Override
                public PhotoMsg call(PhotoMsg photoMsg) {
                    String compressedPath = null;
                    try {
                        compressedPath = ImageUtil.compressByScale(photoMsg.getLocalPath(), PhotoTokenParam.WEBP_FORMAT,
                                Constants.Camera.PhotoSize.WIDTH, Constants.Camera.PhotoSize.HEIGHT);
                    } catch (Throwable throwable) {
                        LogUtil.e(TAG, "compress photo error", throwable);
                    }
                    if (TextUtils.isEmpty(compressedPath)) {
                        compressedPath = photoMsg.getLocalPath();
                    }
                    photoMsg.setLocalPath(compressedPath);
                    compressedPaths.add(compressedPath);
                    return photoMsg;
                }
            }).subscribeOn(Schedulers.io());
        }
        return observables;
    }

    public static Observable<String>[] createCompressObservables(List<String> paths) {
        if (CollectionUtil.isEmpty(paths)) {
            LogUtil.i(TAG, "createMsgObservables error");
            return null;
        }
        Observable<String>[] observables = new Observable[paths.size()];
        int size = paths.size();
        for (int index = 0; index < size; index++) {
            observables[index] = Observable.just(paths.get(index)).map(new Func1<String, String>() {
                @Override
                public String call(String path) {
                    String compressedPath = null;
                    try {
                        compressedPath = ImageUtil.compressByScale(path, PhotoTokenParam.WEBP_FORMAT,
                                Constants.Camera.PhotoSize.WIDTH, Constants.Camera.PhotoSize.HEIGHT);
                    } catch (Throwable throwable) {
                        LogUtil.e(TAG, "compress photo error", throwable);
                    }
                    return TextUtils.isEmpty(compressedPath) ? path : compressedPath;
                }
            }).subscribeOn(Schedulers.io());
        }
        return observables;
    }
}