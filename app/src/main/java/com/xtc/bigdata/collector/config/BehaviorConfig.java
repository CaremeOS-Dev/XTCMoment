package com.xtc.bigdata.collector.config;

import com.xtc.bigdata.common.constants.Constants;

/** Behaviour-collection configuration. */
public class BehaviorConfig {
    public boolean usable = true;
    public boolean openActivityDurationTrack = false;
    public boolean isAutoCollectEvent = false;
    public boolean isToastAutoCollectEvent = false;
    public long sessionTimeout = Constants.NOTIFY_INTERVAL;
    public boolean crashUsable = false;
    public boolean crashToastUsable = false;
    public final CollectFilterConfig collectFilterConfig = new CollectFilterConfig();
    public boolean isUploadSysLog = true;
    public String sdcardRootPath = "";
}