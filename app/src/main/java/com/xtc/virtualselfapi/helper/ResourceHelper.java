package com.xtc.virtualselfapi.helper;

import android.content.Context;
import android.text.TextUtils;

import com.xtc.log.LogUtil;
import com.xtc.utils.storage.SharedManager;
import com.xtc.virtualselfapi.bean.db.DbCostume;
import com.xtc.virtualselfapi.bean.net.req.RespResource;
import com.xtc.virtualselfapi.bean.net.req.RespVersion;
import com.xtc.virtualselfapi.manager.DynamicsShowCache;
import com.xtc.virtualselfapi.manager.HttpManager;
import com.xtc.virtualselfapi.manager.VirtualSelfDBServeImpl;
import com.xtc.virtualselfapi.utils.SpUtils;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 虚拟形象资源版本检查与更新工具。
 */
public class ResourceHelper {

    private static final String TAG = "Virtual_Self_Api_ResourceHelper";
    private static final long INTERVAL = TimeUnit.HOURS.toMillis(6);

    private final CustomHelper customHelper;
    private final HttpManager httpManager;
    private final SpUtils spUtils;

    private int remoteVersion;

    public ResourceHelper(Context context, HttpManager httpManager) {
        this.httpManager = httpManager;
        this.spUtils = new SpUtils(SharedManager.getInstance(context));
        this.customHelper = new CustomHelper(this.httpManager);
    }

    public void checkUpdateVersion() {
        float dynamicsScale = this.spUtils.getDynamicsScale();
        DynamicsShowCache.setScale(dynamicsScale);
        if (System.currentTimeMillis() - this.spUtils.getCustomUpdateTime() <= INTERVAL && dynamicsScale != 0.0f) {
            LogUtil.i(TAG, "checkUpdateVersion: is in updateIntervalTime");
            return;
        }
        this.httpManager.getVersion()
                .flatMap(new Func1<RespVersion, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> call(RespVersion version) {
                        return dealDynamicsScale(version);
                    }
                })
                .flatMap(new Func1<Integer, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(Integer version) {
                        return judgeVersionUpdate(version);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean result) {
                        LogUtil.i(TAG, "checkUpdateVersion result = " + result);
                        customHelper.updateCustom();
                        spUtils.saveCustomUpdateTime(System.currentTimeMillis());
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        LogUtil.e(TAG, "checkUpdateVersion error = ", throwable);
                    }
                });
    }

    private Observable<Integer> dealDynamicsScale(RespVersion version) {
        String scale = version.getScale();
        if (!TextUtils.isEmpty(scale)) {
            try {
                float scaleValue = Float.parseFloat(scale);
                DynamicsShowCache.setScale(scaleValue);
                this.spUtils.saveDynamicsScale(scaleValue);
            } catch (Exception e) {
                LogUtil.e(TAG, "服务器下发天才秀放大参数异常：", e);
            }
        }
        return Observable.just(Integer.valueOf(version.getVersionNumber()));
    }

    private Observable<Boolean> judgeVersionUpdate(Integer version) {
        this.remoteVersion = version.intValue();
        int localVersion = this.spUtils.getVersion();
        LogUtil.d(TAG, "remote = " + version + " local = " + localVersion);
        if (version.intValue() > localVersion) {
            this.spUtils.saveVersion(0);
            return this.httpManager.getResource().map(new Func1<RespResource, Boolean>() {
                @Override
                public Boolean call(RespResource resource) {
                    return updateLocalData(resource);
                }
            });
        }
        return Observable.just(true);
    }

    private boolean updateLocalData(RespResource resource) {
        LogUtil.d(TAG, "start update local data !");
        if (checkResource(resource) && updateDatabase(resource)) {
            LogUtil.d(TAG, "update local data success!");
            this.spUtils.saveVersion(this.remoteVersion);
            return true;
        }
        LogUtil.e(TAG, "insert to database error!");
        return false;
    }

    private boolean checkResource(RespResource resource) {
        boolean valid = resource != null
                && resource.getOrnamentList() != null && resource.getOrnamentList().size() > 0
                && resource.getDangerList() != null && resource.getDangerList().size() > 0;
        LogUtil.d(TAG, "check resource result = " + valid);
        return valid;
    }

    private boolean updateDatabase(RespResource resource) {
        boolean costumeCleared = VirtualSelfDBServeImpl.getInstance().clearCostumeData();
        for (DbCostume costume : resource.getOrnamentList()) {
            LogUtil.i(TAG, "updateDatabase dbCostume id:" + costume.getId() + " title:" + costume.getName());
        }
        boolean costumeInserted = VirtualSelfDBServeImpl.getInstance().insertCostumeForBatch(resource.getOrnamentList());
        boolean dangerCleared = VirtualSelfDBServeImpl.getInstance().clearDangerData();
        boolean dangerInserted = VirtualSelfDBServeImpl.getInstance().insertDangerForBatch(resource.getDangerList());
        LogUtil.d(TAG, " clean costume = " + costumeCleared + " insert costume = " + costumeInserted
                + " clean danger = " + dangerCleared + " insert danger = " + dangerInserted);
        return costumeCleared && costumeInserted && dangerCleared && dangerInserted;
    }
}