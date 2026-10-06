package com.xtc.moment;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.multidex.MultiDex;

import com.bumptech.glide.Glide;
import com.xtc.bigdata.collector.BehaviorCollector;
import com.xtc.common.bigdata.BehaviorUtil;
import com.xtc.contactapi.contact.manager.ContactApi;
import com.xtc.contactapi.contact.manager.ContactManager;
import com.xtc.httplib.HttpManager;
import com.xtc.httplib.bean.AppInfo;
import com.xtc.httplib.okhttp.OnGetAppInfoListener;
import com.xtc.log.ILogger;
import com.xtc.log.LogConfig;
import com.xtc.log.LogUtil;
import com.xtc.log.crash.CrashHandler;
import com.xtc.log.crash.CrashListener;
import com.xtc.moment.asynclayout.AsyncLayoutUtil;
import com.xtc.moment.data.local.MomentsLocalDataSource;
import com.xtc.moment.data.remote.MomentsRemoteDataSource;
import com.xtc.moment.db.MomentDbManager;
import com.xtc.moment.module.illegal.handler.IllegalMessageHandler;
import com.xtc.moment.monitor.IOMonitorManager;
import com.xtc.moment.monitor.LimitCoreThreadPool;
import com.xtc.moment.prerogative.MomentPrerogativeServeImpl;
import com.xtc.moment.receiver.BindWatchReceiver;
import com.xtc.moment.receiver.IConChangeReceiver;
import com.xtc.moment.serve.AccountInfoServerImpl;
import com.xtc.moment.serve.CommentServeImpl;
import com.xtc.moment.serve.MomentServeImpl;
import com.xtc.moment.serve.MomentTemplateServeImpl;
import com.xtc.moment.tasks.start.InitSwitchTask;
import com.xtc.moment.util.FileManager;
import com.xtc.moment.util.HandlerUtil;
import com.xtc.moment.util.SharedTool;
import com.xtc.moment.util.Utils;
import com.xtc.system.account.AppInfoImpl;
import com.xtc.ui.widget.util.TypedValueCompat;
import com.xtc.utils.storage.FolderManager;
import com.xtc.utils.system.ProcessUtils;
import com.xtc.xtcoco.JacocoUtils;

import java.util.ArrayList;
import java.util.List;

public class MomentApplication extends Application {

    private static final String TAG = "MomentApplication";

    public static long startApplicationTimeMillis;
    public static long sAppCreateCostTimeMillis;

    private String curProcessName;

    private void initContactApi(Context context) {
    }

    private void startBlockCanaryMonitor() {
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (SharedTool.getIsHintPermission(this)) {
            Utils.initWatchConfigManager(this);
        }
        startApplicationTimeMillis = SystemClock.elapsedRealtime();
        cacheProcessName();
        MomentApp.init(this);
        startBlockCanaryMonitor();
        initRxJavaSchedulerManager();
        initLog();
        AsyncLayoutUtil.asyncLayout();
        initDb();
        initNet();
        runAsyncBusiness();
        sAppCreateCostTimeMillis = SystemClock.elapsedRealtime() - startApplicationTimeMillis;
        LogUtil.i(TAG, "onCreate: 耗时 = " + sAppCreateCostTimeMillis);
        initCover();
        TypedValueCompat.init(this);
    }

    private void initCover() {
        JacocoUtils.getInstance().init("17c225e", "34d2ece");
    }

    private void initRxJavaSchedulerManager() {
        int max = Math.max(2, Math.min(Runtime.getRuntime().availableProcessors() - 1, 4));
        int min = Math.min(max * 2, 8);
        ArrayList<String> targetNameList = new ArrayList<>(2);
        targetNameList.add("com.xtc");
        IOMonitorManager.getInstance().setTargetNameList(targetNameList);
        IOMonitorManager.getInstance().setReplaceIOScheduler(true)
                .setIOThreadPool(LimitCoreThreadPool.getInstance().build(max, min, 30, 1000, false));
    }

    private void runAsyncBusiness() {
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                Context applicationContext = getApplicationContext();
                if (applicationContext == null) {
                    return;
                }
                MomentServeImpl.getInstance(applicationContext);
                IllegalMessageHandler.getInstance(applicationContext);
                Glide.with(applicationContext);
                prepareDataCache();
                new InitSwitchTask(applicationContext).run();
                LogUtil.d(TAG, "runAsyncBusiness1 end");
            }
        });
        com.xtc.httplib.util.HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                Context applicationContext = getApplicationContext();
                if (applicationContext == null) {
                    return;
                }
                MomentApp.getWatchId();
                AccountInfoServerImpl.getInstance(applicationContext).initWatchAccountInfo();
                Glide.get(applicationContext);
                initFolder();
                initBigData();
                ContactApi.init(applicationContext);
                MomentsLocalDataSource.getInstance(applicationContext);
                CommentServeImpl.getInstance(applicationContext);
                MomentPrerogativeServeImpl.getInstance(applicationContext);
                MomentTemplateServeImpl.getInstance(applicationContext);
                MomentsRemoteDataSource.getInstance(applicationContext);
                ContactManager.getInstance(applicationContext);
                LogUtil.d(TAG, "runAsyncBusiness2 end");
            }
        });
    }

    private void cacheProcessName() {
        curProcessName = ProcessUtils.getCurrentProcessName();
        LogUtil.d(TAG, "onCreate curProcessName:" + curProcessName);
    }

    public boolean isMainProcess(Context context) {
        return curProcessName != null && curProcessName.equals(context.getPackageName());
    }

    private void initFolder() {
        FolderManager.getInstance().configure("ibwatch", "moment");
        FileManager.init(this);
    }

    private void initLog() {
        LogConfig.builder()
                .isPrintConsole(true)
                .saveLog(true)
                .setSaveLevel(ILogger.Level.Debug)
                .isDebugVersion(false)
                .module("ibwatch")
                .appName("com.xtc.moment")
                .build(LogConfig.Builder.BuildMode.Android, new Object[0]);
    }

    private void initDb() {
        MomentDbManager.getInstance(this);
    }

    private void initBigData() {
        CrashHandler.setCrashListener(new CrashListener() {
            @Override
            public void onUncaughtException(Thread thread, Throwable throwable) {
                BehaviorCollector.getInstance().uploadSystemCrash();
            }
        });
        BehaviorUtil.init(new BehaviorCollector.Builder(this)
                .setHostAppId("com.xtc.i3launcher")
                .setDeviceType("watch")
                .openActivityDurationTrack(true)
                .setDebugMode(false)
                .build());
    }

    private void initNet() {
        HttpManager.getInstance(this).setNoNeedMonitor(getFilterNet());
        HttpManager.getInstance(this).setOnGetAppInfoListener(new OnGetAppInfoListener() {
            @Override
            public AppInfo getAppInfo() {
                AppInfoImpl defaultInstance = AppInfoImpl.getDefaultInstance(getApplicationContext());
                AppInfo appInfo = new AppInfo();
                appInfo.setEncSwitch(defaultInstance.getEncSwitch());
                appInfo.setGrey(defaultInstance.getGrey());
                appInfo.setRsaPublicKey(defaultInstance.getRsaPublicKey());
                appInfo.setVersion(defaultInstance.getVersion());
                return appInfo;
            }
        });
    }

    private List<String> getFilterNet() {
        ArrayList<String> urls = new ArrayList<>();
        urls.add("/moment/public");
        urls.add("/moment/like");
        urls.add("/moment/like/cancel");
        urls.add("/moment/delete");
        urls.add("/moment/comment");
        urls.add("/moment/deleteComment");
        urls.add("/moment/search");
        urls.add("/moment/like/search");
        urls.add("/moment/file/download");
        urls.add("/moment/file/batchDownload");
        urls.add("/moment/advertComment/getAllAdvert");
        urls.add("/moment/advertComment/deleteComment");
        urls.add("/moment/advertComment/comment");
        urls.add("/moment/vlog/gift/sendgift");
        urls.add("/moment/vlog/gift/search");
        urls.add("/moment/getMomentComment");
        urls.add("/social-service/personalInfo/like");
        urls.add("/social-service/prerogative/getpersonal");
        urls.add("/social-service/violation/initViolationInfo");
        urls.add("/social-service/violation/searchMomentRiskRecord");
        urls.add("/ai-operation-service/aiText/home");
        urls.add("/ai-operation-service/userAiText/creation");
        urls.add("/ai-operation-service/aiTextObtain/obtainTime/{accessType}");
        urls.add("/ai-operation-service/userAiText/getUserRecord");
        return urls;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        LogUtil.i(TAG, "moment onLowMemory");
        HandlerUtil.executeWhenMainThreadIdle(new Runnable() {
            @Override
            public void run() {
                Glide.get(MomentApplication.this).onLowMemory();
            }
        });
    }

    @Override
    public void onTrimMemory(final int level) {
        super.onTrimMemory(level);
        LogUtil.i(TAG, "moment onTrimMemory level: " + level);
        HandlerUtil.executeWhenMainThreadIdle(new Runnable() {
            @Override
            public void run() {
                Glide.get(MomentApplication.this).onTrimMemory(level);
            }
        });
    }

    private void prepareDataCache() {
        LogUtil.d(TAG, "prepareDataCache");
        registerGlobalReceiver(getApplicationContext());
    }

    private void registerGlobalReceiver(Context context) {
        BindWatchReceiver.register(context);
        new IConChangeReceiver() {
            @Override
            public void onReceive(final Context context, final Intent intent) {
                LogUtil.d(TAG, "refresh my icon");
                HandlerUtil.runOnBackground(new Runnable() {
                    @Override
                    public void run() {
                        if (IConChangeReceiver.ICON_CHANGE_ACTION.equals(intent.getAction())) {
                            FileManager.setMyIconPath(context);
                        }
                    }
                });
            }
        }.register(getApplicationContext());
    }
}