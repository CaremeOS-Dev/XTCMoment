package com.xtc.bigdata.collector;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.xtc.bigdata.collector.config.BehaviorConfig;
import com.xtc.bigdata.collector.config.ConfigAgent;
import com.xtc.bigdata.collector.config.DeviceInfo;
import com.xtc.bigdata.collector.encapsulation.entity.BaseAttr;
import com.xtc.bigdata.collector.encapsulation.entity.attr.UserAttr;
import com.xtc.bigdata.collector.encapsulation.entity.event.ClickEvent;
import com.xtc.bigdata.collector.encapsulation.entity.event.CountEvent;
import com.xtc.bigdata.collector.encapsulation.entity.event.CustomEvent;
import com.xtc.bigdata.collector.encapsulation.entity.event.ExceptionEvent;
import com.xtc.bigdata.collector.encapsulation.entity.event.MonitorURLEvent;
import com.xtc.bigdata.collector.encapsulation.entity.event.SearchEvent;
import com.xtc.bigdata.collector.encapsulation.entity.event.SystemInfoEvent;
import com.xtc.bigdata.collector.encapsulation.interfaces.IEvent;
import com.xtc.bigdata.collector.interfaces.ICollector;
import com.xtc.bigdata.collector.utils.MD5Coder;
import com.xtc.bigdata.collector.utils.QueueUtils;
import com.xtc.bigdata.common.constants.Constants;
import com.xtc.bigdata.common.error.ErrorCode;
import com.xtc.bigdata.common.utils.ContextUtils;
import com.xtc.bigdata.common.utils.SharedPrefUtils;
import com.xtc.bigdata.common.utils.UriUtils;
import com.xtc.log.LogUtil;
import com.xtc.utils.storage.SharedManager;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Map;

/** Entry point of the behaviour collector. */
public final class BehaviorCollector implements ICollector {

    private static final String TAG = BehaviorCollector.class.getName();

    private Uri contentUri;
    private MyView downView;
    private boolean isCollectInit;

    private static class InstanceHolder {
        private static final BehaviorCollector INSTANCE = new BehaviorCollector();

        private InstanceHolder() {
        }
    }

    public static BehaviorCollector getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private BehaviorCollector() {
        this.contentUri = null;
        this.isCollectInit = false;
    }

    /** Builder used to configure the collector. */
    public static final class Builder {
        private final BehaviorConfig buildConfig;

        public Builder(Application application) {
            ContextUtils.setContext(application);
            Constants.setPackageName(application.getPackageName());
            initMMKV(application);
            this.buildConfig = ConfigAgent.getBehaviorConfig();
        }

        private void initMMKV(Application application) {
            LogUtil.i(BehaviorCollector.TAG, "mmkv, initMMKV...");
            String dir = application.getFilesDir().getAbsolutePath() + "/mmkv";
            ArrayList<String> names = new ArrayList<>();
            names.add(SharedPrefUtils.PREF_NAME);
            SharedManager.getInstance(application).init(application, names, dir);
            LogUtil.i(BehaviorCollector.TAG, "init mmkv finish...");
        }

        public Builder enable(boolean enabled) {
            this.buildConfig.usable = enabled;
            return this;
        }

        public Builder setDeviceType(String deviceType) {
            Constants.deviceType = deviceType;
            return this;
        }

        public Builder setHostAppId(String hostAppId) {
            Constants.setHostAppId(hostAppId);
            return this;
        }

        public Builder sessionTimeout(long sessionTimeout) {
            this.buildConfig.sessionTimeout = sessionTimeout;
            return this;
        }

        public Builder setDebugMode(boolean debug) {
            Constants.setDebug(debug);
            return this;
        }

        public Builder openActivityDurationTrack(boolean open) {
            this.buildConfig.openActivityDurationTrack = open;
            return this;
        }

        public Builder setAutoCollectEvent(boolean autoCollect) {
            this.buildConfig.isAutoCollectEvent = autoCollect;
            return this;
        }

        public Builder setToastAutoCollectEvent(boolean toast) {
            this.buildConfig.isToastAutoCollectEvent = toast;
            return this;
        }

        public Builder enableCrash(boolean enable) {
            this.buildConfig.crashUsable = enable;
            return this;
        }

        public Builder enableToastCrash(boolean enable) {
            this.buildConfig.crashToastUsable = enable;
            return this;
        }

        public Builder enableUploadSysCrash(boolean enable) {
            this.buildConfig.isUploadSysLog = enable;
            return this;
        }

        public BehaviorConfig build() {
            return this.buildConfig;
        }
    }

    public void init(BehaviorConfig behaviorConfig) {
        ConfigAgent.setBehaviorConfig(behaviorConfig);
        init(ContextUtils.getContext());
    }

    private void init(Application application) {
        if (application == null) {
            LogUtil.e(TAG, "BFC DA init fail , Application can not be null !!!");
            return;
        }
        this.contentUri = UriUtils.getContentUri(application);
        this.isCollectInit = true;
    }

    public boolean isCollectInit() {
        return this.isCollectInit;
    }

    public void saveWatchConfig(final String config) {
        QueueUtils.getInstance().post(new Runnable() {
            @Override
            public void run() {
                BaseAttr.saveConfig(DeviceInfo.WATCH_CONFIG_PATH, config);
            }
        });
    }

    @Override
    public void appLaunch() {
        if (this.isCollectInit) {
            CollectionManager.getInstance().appLaunch();
        }
    }

    @Override
    public void clickEvent(ClickEvent clickEvent) {
        if (this.isCollectInit) {
            CollectionManager.getInstance().clickEvent(clickEvent);
        }
    }

    @Override
    public void customEvent(CustomEvent customEvent) {
        if (this.isCollectInit) {
            CollectionManager.getInstance().customEvent(customEvent);
        }
    }

    @Override
    public void countEvent(CountEvent countEvent) {
        if (this.isCollectInit) {
            CollectionManager.getInstance().countEvent(countEvent);
        }
    }

    @Override
    public void searchEvent(SearchEvent searchEvent) {
        if (this.isCollectInit) {
            CollectionManager.getInstance().searchEvent(searchEvent);
        }
    }

    @Override
    public void pageBegin(String pageName) {
        if (this.isCollectInit) {
            CollectionManager.getInstance().pageBegin(pageName);
        }
    }

    @Override
    public boolean pageEnd(String pageName) {
        if (this.isCollectInit) {
            return CollectionManager.getInstance().pageEnd(pageName, null, null, "B", "C", null);
        }
        return false;
    }

    @Override
    public boolean pageEnd(String pageName, String functionName, String moduleDetail) {
        if (this.isCollectInit) {
            return CollectionManager.getInstance().pageEnd(pageName, functionName, moduleDetail, "B", "C", null);
        }
        return false;
    }

    @Override
    public boolean pageEnd(String pageName, String functionName, String moduleDetail, String dataCollectLevel,
                           String dataSecurityLevel, Map<String, String> extend) {
        if (this.isCollectInit) {
            return CollectionManager.getInstance().pageEnd(pageName, functionName, moduleDetail, dataCollectLevel,
                    dataSecurityLevel, extend);
        }
        return false;
    }

    @Override
    public void exceptionEvent(ExceptionEvent exceptionEvent) {
        if (this.isCollectInit) {
            CollectionManager.getInstance().exceptionEvent(exceptionEvent);
        }
    }

    @Override
    public void monitorURLEvent(MonitorURLEvent monitorURLEvent) {
        if (this.isCollectInit) {
            CollectionManager.getInstance().monitorURLEvent(monitorURLEvent);
        }
    }

    @Override
    public void systemInfoEvent(SystemInfoEvent systemInfoEvent) {
        if (this.isCollectInit) {
            CollectionManager.getInstance().systemInfoEvent(systemInfoEvent);
        }
    }

    @Override
    public void uploadSystemCrash() {
        CollectionManager.getInstance().uploadSystemCrash();
    }

    @Override
    public void initUserInfo(UserAttr userAttr) {
        if (this.isCollectInit) {
            CollectionManager.getInstance().initUserInfo(userAttr);
        }
    }

    @Override
    public void realTime2Upload() {
        CollectionManager.getInstance().realTime2Upload();
    }

    public UserAttr getUserInfo() {
        return CollectionManager.getInstance().getUserInfo();
    }

    public void event(IEvent event) {
        if (this.isCollectInit) {
            CollectionManager.getInstance().event(event);
        }
    }

    public void onKillProcess() {
        CollectionManager.getInstance().exit();
    }

    public void setConfig(BehaviorConfig behaviorConfig) {
        ConfigAgent.setBehaviorConfig(behaviorConfig);
    }

    public String logErrorCode() {
        return ErrorCode.logErrorCode();
    }

    public Map<String, String> getErrorCodes() {
        return ErrorCode.getErrorCodes();
    }

    public BehaviorConfig getConfig() {
        return ConfigAgent.getBehaviorConfig();
    }

    public Uri getContentUri() {
        return this.contentUri;
    }

    @Override
    public String getBehaviorVersion() {
        return "";
    }

    /** Collects a click when auto-collection is enabled. */
    public void dealAutoCollect(MotionEvent motionEvent, Activity activity) {
        if (ContextUtils.isEmpty() || !ConfigAgent.getBehaviorConfig().isAutoCollectEvent) {
            return;
        }
        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            if (this.downView != null) {
                MyView clickView = findClickView(motionEvent, activity);
                if (clickView != null && this.downView.view == clickView.view) {
                    ClickEvent clickEvent = new ClickEvent();
                    try {
                        clickEvent.activity = activity.getClass().getName();
                        clickEvent.functionName = new MD5Coder(32).encode(clickView.viewTree.getBytes(Charset.forName("UTF-8")));
                        clickEvent.moduleDetail = Constants.AUTO_COLLECT;
                    } catch (Exception e) {
                        if (Constants.isDebug) {
                            LogUtil.i(TAG, "获取方法名失败");
                        }
                        clickEvent.functionName = clickView.viewTree;
                    }
                    clickEvent(clickEvent);
                    if (ConfigAgent.getBehaviorConfig().isToastAutoCollectEvent) {
                        Toast.makeText(ContextUtils.getContext(), clickEvent.functionName, Toast.LENGTH_SHORT).show();
                    }
                }
                this.downView = null;
            }
            return;
        }
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            this.downView = findClickView(motionEvent, activity);
        }
    }

    private MyView findClickView(MotionEvent motionEvent, Activity activity) {
        if (activity != null) {
            return searchClickView(new MyView(activity.getWindow().getDecorView(), activity.getClass().getName()), motionEvent, 0);
        }
        return null;
    }

    private MyView searchClickView(MyView myView, MotionEvent motionEvent, int index) {
        View view = myView.view;
        MyView found = null;
        if (view == null || !isInView(view, motionEvent) || view.getVisibility() != View.VISIBLE) {
            return null;
        }
        myView.level++;
        if (myView.level == 2 && !"LinearLayout".equals(view.getClass().getSimpleName())) {
            myView.filterLevelCount++;
        }
        if (myView.level > myView.filterLevelCount) {
            myView.viewTree += "." + view.getClass().getSimpleName() + "[" + index + "]";
        }
        if (view.getTag() != null) {
            String tag = view.getTag().toString();
            if (!tag.startsWith("bigdata_") || "bigdata_ignore".equals(tag)) {
                return null;
            }
            return myView;
        }
        if (!(view instanceof ViewGroup)) {
            return myView;
        }
        ViewGroup viewGroup = (ViewGroup) view;
        for (int childIndex = viewGroup.getChildCount() - 1; childIndex >= 0; childIndex--) {
            myView.view = viewGroup.getChildAt(childIndex);
            found = searchClickView(myView, motionEvent, childIndex);
            if (found != null) {
                return found;
            }
        }
        return found;
    }

    private boolean isInView(View view, MotionEvent motionEvent) {
        if (view.getVisibility() == View.INVISIBLE || view.getVisibility() == View.GONE) {
            return false;
        }
        int rawX = (int) motionEvent.getRawX();
        int rawY = (int) motionEvent.getRawY();
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        return rawX > x && rawX < x + view.getWidth() && rawY > y && rawY < y + view.getHeight();
    }

    private static class MyView {
        public View view;
        public String viewTree;
        public int level = 0;
        public int filterLevelCount = 3;

        public MyView(View view, String viewTree) {
            this.view = view;
            this.viewTree = viewTree;
        }
    }
}