package com.xtc.web.core.verify;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.xtc.bigdata.common.utils.ContextUtils;
import com.xtc.common.bigdata.BehaviorUtil;
import com.xtc.database.ormlite.DatabaseHelper;
import com.xtc.httplib.util.HandlerUtil;
import com.xtc.log.LogUtil;
import com.xtc.system.account.WatchAccountBase;
import com.xtc.utils.storage.SharedManager;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/** H5 白名单校验管理器：本地缓存 + 定时从服务端刷新，并拦截非白名单域名请求。 */
public class VerifyManager {

    private static final String HTTP_TAG = "http";
    private static final int MODULE_INTERCEPTER_H5 = 3389;
    private static final String TAG = "VerifyManager";
    private static final String VERIFY_LAST_UPDATE_TIME = "verify_last_update_time";
    private static VerifyManager instance = null;
    /** 刷新间隔（毫秒），约 4 小时。 */
    private static final float intervalTime = 1.44E7f;

    private Context context;
    private boolean isIntercepterSwitch = false;
    private DbVerify verifyCache;

    public static VerifyManager getInstance(Context context) {
        if (instance == null) {
            instance = new VerifyManager(context);
        }
        return instance;
    }

    public VerifyManager(Context context) {
        this.context = context;
        registerTable();
        initOrUpdateData(context);
        initIntercepterSwitch();
    }

    /** 异步读取 H5 拦截开关（模块 3389）。 */
    private void initIntercepterSwitch() {
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                isIntercepterSwitch = WatchAccountBase.queryModuleSwitchByBoolean(context,
                        MODULE_INTERCEPTER_H5, false);
                LogUtil.i(TAG, "initIntercepterSwitch result = " + isIntercepterSwitch);
            }
        });
    }

    private void registerTable() {
        DatabaseHelper.getInstance(this.context, Constants.TableName.SHARE_WHITE_TABLE_NAME)
                .registerTable(DbVerify.class);
    }

    /** 距上次刷新未超过间隔时读本地缓存，否则请求网络。 */
    private void initOrUpdateData(Context context) {
        long lastUpdateTime = SharedManager.getInstance(context).getLong(VERIFY_LAST_UPDATE_TIME, 0L);
        if (System.currentTimeMillis() - lastUpdateTime <= intervalTime) {
            this.verifyCache = VerifyServeImpl.getInstance(context).queryWhiteDatas();
        } else {
            getVerifyDatasFromNet(context);
        }
    }

    private void getVerifyDatasFromNet(final Context context) {
        Log.i(TAG, "getVerifyDatasFromNet");
        new VerifyServeHttpProxy(context).getVerifyDatas(context)
                .map(new Func1<List<String>, DbVerify>() {
                    @Override
                    public DbVerify call(List<String> whiteDatas) {
                        Log.i(TAG, "verifyBean = " + whiteDatas);
                        return VerifyUtil.trans2DbVerify(whiteDatas);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Action1<DbVerify>() {
                    @Override
                    public void call(DbVerify dbVerify) {
                        verifyCache = VerifyServeImpl.getInstance(context).updateWhiteDatas(dbVerify);
                        Log.i(TAG, "verifyCache = " + verifyCache);
                        SharedManager.getInstance(context).putLong(VERIFY_LAST_UPDATE_TIME,
                                System.currentTimeMillis());
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e(TAG, "getVerifyDatasFromNet error", throwable);
                    }
                });
    }

    /** 校验 H5 请求的 host 是否在白名单内，非 http 请求直接放行。 */
    public boolean checkUrlIsValid(String url, String host) {
        initOrUpdateData(this.context);
        if ((!TextUtils.isEmpty(url) && !url.startsWith("http")) || TextUtils.isEmpty(host)) {
            behaviorVerifyResult(Constants.Intercepter.LOCAL_VERIFY, url);
            return true;
        }
        DbVerify dbVerify = this.verifyCache;
        if (dbVerify == null || TextUtils.isEmpty(dbVerify.getWhiteDatas())) {
            Log.i(TAG, "verifyCache is empty, release this request");
            behaviorVerifyResult(Constants.Intercepter.CACHE_EMPTY, url);
            return true;
        }
        if (this.verifyCache.getWhiteDatas().contains(host)) {
            Log.i(TAG, "verify url success, url = " + url);
            behaviorVerifyResult(Constants.Intercepter.VERIFY_SUCCESS, url);
            return true;
        }
        Log.i(TAG, "verify url failed, url = " + url);
        behaviorVerifyResult(Constants.Intercepter.VERIFY_FAILED, url);
        return false;
    }

    public boolean getIntercepterSwitchValue() {
        return this.isIntercepterSwitch;
    }

    /** 异步上报白名单校验结果。 */
    private void behaviorVerifyResult(final int result, final String url) {
        LogUtil.d(TAG, "behaviorVerifyResult, url = " + url + " result = " + result);
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> extend = new HashMap<>();
                extend.put(Constants.Intercepter.INTERCEPTER_RESULT, String.valueOf(result));
                if (!TextUtils.isEmpty(url) && Objects.equals(Integer.valueOf(result),
                        Constants.Intercepter.VERIFY_FAILED)) {
                    extend.put(Constants.Intercepter.INTERCEPTER_URL_VALUE, url);
                }
                BehaviorUtil.customEvent(ContextUtils.getContext(),
                        Constants.Intercepter.FUNCTION_NAME_INTERCEPTE_H5, extend);
            }
        });
    }
}