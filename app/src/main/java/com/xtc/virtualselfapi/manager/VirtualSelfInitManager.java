package com.xtc.virtualselfapi.manager;

import android.content.Context;
import android.text.TextUtils;

import com.xtc.database.ormlite.DatabaseHelper;
import com.xtc.log.LogUtil;
import com.xtc.virtualselfapi.constants.Constants;
import com.xtc.virtualselfapi.helper.ResourceHelper;
import com.xtc.virtualselfapi.helper.VirtualSelfDbHelper;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 虚拟形象初始化管理器。
 */
public class VirtualSelfInitManager {

    private static final String TAG = "Virtual_Self_Api_VirtualSelfManager";

    private static volatile VirtualSelfInitManager instance;

    private Context appContext;
    private String databaseName;

    public static VirtualSelfInitManager getInstance() {
        VirtualSelfInitManager manager = instance;
        if (manager == null) {
            synchronized (VirtualSelfInitManager.class) {
                manager = instance;
                if (manager == null) {
                    manager = new VirtualSelfInitManager();
                    instance = manager;
                }
            }
        }
        return manager;
    }

    public void initData(Context context, String databaseName) {
        initData(context, databaseName, Constants.DEFAULT_INIT_DELAY_TIME, true);
    }

    public void initData(Context context, String databaseName, boolean registerTable) {
        initData(context, databaseName, Constants.DEFAULT_INIT_DELAY_TIME, registerTable);
    }

    public void initData(Context context, String databaseName, long delayMillis, boolean registerTable) {
        if (context == null) {
            throw new NullPointerException("context can not be null");
        }
        if (TextUtils.isEmpty(databaseName)) {
            throw new NullPointerException("databaseName can not be null");
        }
        this.appContext = context.getApplicationContext();
        this.databaseName = databaseName;
        LogUtil.d(TAG, "start init data!");
        if (registerTable) {
            VirtualSelfDbHelper.registerTable(DatabaseHelper.getInstance(context, databaseName));
        }
        Observable.just(this.appContext).delay(delayMillis, TimeUnit.MILLISECONDS)
                .map(new Func1<Context, Boolean>() {
                    @Override
                    public Boolean call(Context context) {
                        new ResourceHelper(context, new HttpManager(context)).checkUpdateVersion();
                        return true;
                    }
                })
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean result) {
                        LogUtil.i(TAG, "initData result = " + result);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        LogUtil.d(TAG, "initData error = " + throwable);
                    }
                });
    }

    public Context getAppContext() {
        return this.appContext;
    }

    public String getDatabaseName() {
        return this.databaseName;
    }
}