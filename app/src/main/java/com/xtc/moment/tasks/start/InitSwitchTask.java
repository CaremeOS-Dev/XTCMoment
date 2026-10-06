package com.xtc.moment.tasks.start;

import android.content.Context;
import android.util.Pair;

import com.xtc.dispatch.task.AbsTask;
import com.xtc.moment.constants.FunSwitchConstant;
import com.xtc.moment.constants.ModuleSwitchConstant;
import com.xtc.moment.manager.StartupManage;
import com.xtc.moment.receiver.ModuleSwitchReceiver;
import com.xtc.moment.util.switchs.ModuleSwitchUtil;
import com.xtc.moment.util.switchs.WeiChatFunSwitchUtil;
import com.xtc.system.wearswitch.function.FunSwitchUtil;

import java.util.Arrays;
import java.util.Iterator;

/**
 * 启动时初始化功能开关与模块开关：预热缓存、注册开关变化监听，并把需要的开关读到缓存里。
 */
public class InitSwitchTask extends AbsTask {

    private Context mContext;

    public InitSwitchTask(Context context) {
        this.mContext = context;
        setThreadType(2);
    }

    @Override
    protected void executeTask(AbsTask.RequestValues requestValues) {
        run();
    }

    @Override
    public void run() {
        StartupManage.startMethodStartupTime("InitSwitchTask");
        FunSwitchUtil.setCacheEnabled(true);
        ModuleSwitchUtil.setIsOpenCache(true);
        initFunSwitchChange();
        loadFunSwitch();
        loadModuleSwitch();
        ModuleSwitchReceiver.register(this.mContext);
        StartupManage.endMethodStartupTime("InitSwitchTask");
    }

    public void initFunSwitchChange() {
        WeiChatFunSwitchUtil.registerContentObserver(this.mContext.getApplicationContext(), FunSwitchConstant.FUN_SWITCH_URI);
    }

    /**
     * 预读模块开关，使后续查询直接命中缓存。
     */
    public void loadModuleSwitch() {
        for (Pair pair : Arrays.asList(
                new Pair(102, true),
                new Pair(112, false),
                new Pair(113, true),
                new Pair(ModuleSwitchConstant.MODULE_SWITCH_MOMENT_COMMENT_DELETE, true),
                new Pair(ModuleSwitchConstant.MODULE_SWITCH_MOMENT_REPORT_FUNCTION, false),
                new Pair(ModuleSwitchConstant.MODULE_SWITCH_MOMENT_SEND_VIDEO_FUNCTION, false),
                new Pair(ModuleSwitchConstant.MODULE_SWITCH_MOMENT_FUN_PHOTO_IS_AVAILABLE, false),
                new Pair(ModuleSwitchConstant.MODULE_SWITCH_MOMENT_CONTENT_SUPERVISION, false),
                new Pair(ModuleSwitchConstant.MODULE_SWITCH_DRESS_HEAD, false),
                new Pair(ModuleSwitchConstant.MODULE_SWITCH_MOMENT_PUBLISH_LOCATION, false),
                new Pair(ModuleSwitchConstant.MODULE_SWITCH_MOMENT_CANCEL_PRAISE, false),
                new Pair(ModuleSwitchConstant.MODULE_ILLEGAL_PUNISH, false),
                new Pair(ModuleSwitchConstant.MODULE_REPORT_SUPPORT, true),
                new Pair(ModuleSwitchConstant.MODULE_SWITCH_REAL_NAME, false),
                new Pair(116, true),
                new Pair(115, true),
                new Pair(ModuleSwitchConstant.MODULE_SWITCH_COMMUNITY_CONVERSATION, false),
                new Pair(ModuleSwitchConstant.MULTI_TYPE_COMBINED_DYNAMIC, false),
                new Pair(ModuleSwitchConstant.MODULE_SWITCH_PERSONAL_CENTER, false),
                new Pair(ModuleSwitchConstant.MODULE_SWITCH_SET_STATE, false),
                new Pair(ModuleSwitchConstant.MODULE_VISIBLE_RANGE, false),
                new Pair(ModuleSwitchConstant.MODULE_PERSONAL_BADGE_SUPPORT, false))) {
            ModuleSwitchUtil.queryModuleSwitchByInt(this.mContext, ((Integer) pair.first).intValue(), ((Boolean) pair.second).booleanValue());
        }
    }

    public void loadFunSwitch() {
        Iterator iterator = Arrays.asList(FunSwitchConstant.FUN_PHOTO).iterator();
        while (iterator.hasNext()) {
            FunSwitchUtil.queryFunSwitchByInt(this.mContext, (String) iterator.next(), true);
        }
    }
}