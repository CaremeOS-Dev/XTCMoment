package com.xtc.utils.system;

import java.util.ArrayList;
import java.util.List;

/** Names of the {@code persist.*} / {@code ro.*} system properties used by the app. */
public class SystemProperty {

    public static final String WATCH_LOSS_STATUS = "persist.sys.watchlossstatus";
    public static final String CURRENT_CLOCK_FILE = "persist.sys.currentclockfile";
    public static final String CHARGE_USABLE = "persist.sys.charge.usable";
    public static final String UNLOCK_HIDDEN = "persist.sys.unlock.hidden";
    public static final String BT_REJECT_CALL = "persist.sys.bt.rejectcall";
    public static final String MOTION_SWITCH_STATE = "persist.sys.motion.switch.state";
    public static final String OCCUR_DUMP = "persist.sys.occurdump";
    public static final String APP_CRASH = "persist.sys.appcrash";
    public static final String SWIPE_DISMISS = "persist.sys.SwipeDismiss";
    public static final String CALL_END = "persist.sys.call.end";
    public static final String DURE_LIMITS = "persist.sys.dure.limits";
    public static final String STANDBY_DISPLAY = "persist.sys.standby.display";
    public static final String STATUS_BAR = "persist.sys.status_bar";
    public static final String POWER_SAVE_ENABLE = "persist.sys.powersave.enable";
    public static final String CLOSE_WIFI = "persist.sys.close_wifi";
    public static final String CLOSE_GPS = "persist.sys.close_gps";
    public static final String GPS_COLLECT_WIFI = "persist.sys.gps_collect_wifi";
    public static final String AUTO_ANSWER = "persist.sys.autoanswer";
    public static final String REFUSE_STRANGER_CALL = "persist.sys.refusestrangercall";
    public static final String REPORT_CALL_POSITION = "persist.sys.reportcallposition";
    public static final String BIND_STATUS = "persist.sys.bindstatus";
    /** @deprecated superseded by the os-type property. */
    @Deprecated
    public static final String PRESCHOOL_STATUS = "persist.sys.preschoolstatus";
    public static final String OS_TYPE = "persist.sys.ostype";
    public static final String LONG_BATTERY_LIFE = "persist.sys.longbatterylife";
    public static final String CLASS_MODE_STATUS = "persist.sys.classmodestatus";
    public static final String SLEEP_MODE_STATUS = "persist.sys.sleepmodestatus";
    public static final String OFFICIAL_STAT = "persist.sys.officialstat";
    public static final String QUIETNESS_MODE = "persist.sys.quietnessmode";
    public static final String CLOSE_BLE = "persist.sys.close_ble";
    public static final String BT_SHUTDOWN = "persist.sys.bt.shutdown";
    public static final String BT_WARN = "persist.sys.bt.warn";
    public static final String WIFI_VISIBLE_STATUS = "persist.sys.wifi_visible_status";
    public static final String WIFI_USER_OPERATE = "persist.sys.wifi_user_operate";
    public static final String POWER_ON_OFF = "persist.sys.power_on_off";
    public static final String SETTING_LOCATION = "persist.sys.setting_location";
    public static final String SETTING_SAVE_POWER = "persist.sys.setting_savepower";
    public static final String MOBILE_DATA_CONTROL = "persist.sys.mobliedata.control";
    public static final String MOBILE_DATA_TIME = "persist.sys.mobliedata.time";
    public static final String UNKNOWN_SOURCES = "persist.sys.unknownsources";
    public static final String WIFI_CONNECT_ENABLE = "persist.sys.wificonnect.enable";
    public static final String CELLULAR_NETEASE = "persist.sys.cellular.netease";
    public static final String SHAKE_MUSIC = "persist.sys.shakemusic";
    public static final String IM_STATUS = "persist.sys.im.status";
    public static final String CELLULAR_THRESHOLD = "persist.sys.cellular.threshold";
    public static final String WEICHAT_MORE_EMOJI = "persist.sys.weichat.more.emoji";
    public static final String SPORT_PK_TCT = "persist.sys.sport.pk.tct";
    public static final String SERVER_INNER = "persist.sys.serverinner";
    public static final String SETTING_KEY_ID = "persist.sys.setting.keyid";
    public static final String FORBID_RANK = "persist.sys.forbid.rank";
    public static final String LAUNCHER_THEME = "persist.sys.launcher.theme";
    public static final String MODE_STATUS = "persist.sys.modestatus";
    public static final String NW_MODE = "persist.sys.nwmode";
    public static final String HALL_OPENED = "persist.sys.hall.opened";
    public static final String APP_CONFIG = "persist.sys.appconfig";
    public static final String CTA_PERMISSION = "persist.sys.cta.permission";
    public static final String CTA_VERSION = "ro.xtc.ctaversion";
    public static final String CPU_MONITOR_STATUS = "persist.sys.cpu.monitor.status";
    public static final String LOCATION_STATUS = "persist.sys.path.lcation.status";
    public static final String POWER_FUNC_TYPE = "persist.sys.power.func.type";
    public static final String QUICK_DIAL_STATUS = "persist.sys.quickdialstatus";
    public static final String SETTING_RING = "persist.sys.setting.ring";
    public static final String RING_SWITCH = "persist.sys.ring.switch";
    public static final String LOCATION_HIGH_AC = "persist.sys.location.high.ac";
    public static final String ALLOW_POWER_OFF = "persist.sys.allowpoweroff";
    public static final String PROPERTY_B = "persist.sys.b";
    public static final String HEADSET = "persist.sys.headset";
    public static final String LAST_DEVICE = "persist.sys.last.device";
    public static final String BT_CALL_ONCE = "persist.sys.bt.callonce";
    public static final String VC_CALLING = "persist.sys.vc.calling";
    public static final String TAKE_PHOTO_MODE = "persist.sys.takephoto.mode";
    public static final String DIAL_120_STATUS = "persist.sys.dial120status";
    public static final String DIAL_110_STATUS = "persist.sys.dial110status";
    public static final String WATCH_ID = "persist.sys.watch.id";
    public static final String LOG_TAG_MODEM = "log.tag.modem";
    public static final String LOG_TAG_FACTORY_RESET = "log.tag.factoryreset";
    public static final String LOG_TAG_APP_DATA = "log.tag.appdata";
    public static final String LOG_TAG_BT_WIFI = "log.tag.btwifi";
    public static final String LOG_TAG_XTC_BUGREPORT = "log.tag.xtc_bugreport";
    public static final String CURRENT_SOFT_VERSION = "ro.product.current.softversion";
    public static final String PRODUCT_MODEL = "ro.product.model";
    public static final String PRODUCT_PRIMARY_MODEL = "ro.product.pri.model";
    public static final String PRODUCT_INNER_MODEL = "ro.product.innermodel";
    public static final String CARE_ME_VERSION = "ro.product.careme.version";
    public static final String PRODUCT_INNER_MODEL_EX = "ro.product.innermodel.ex";
    public static final String LOCALE_REGION = "ro.product.locale.region";
    public static final String LOCALE = "ro.product.locale";
    public static final String BUILD_DATE_UTC = "ro.build.date.utc";
    public static final String BUILD_TYPE = "ro.build.type";
    public static final String BUILD_VERSION_AUTH = "ro.build.version.auth";
    public static final String BOOT_BIND_NUMBER = "ro.boot.bindnumber";
    public static final String DEFAULT_NETWORK = "ro.telephony.default_network";
    public static final String HARDWARE = "ro.hardware";
    public static final String CTA_ENABLED = "ro.cta_enabled";
    public static final String MOBILE_DATA_DEFAULT = "ro.com.android.mobiledata";
    public static final String WATCH_COLOR = "ro.xtcwatch.color";
    public static final String WIFI_AUTOCONNECT_DISABLE = "wifi.autoconnect.disable";
    public static final String WIFI_IS_SLEEP = "wifi.is.sleep";
    public static final String IM_INIT_ENABLE = "sys.im.init.enable";
    public static final String XTC_HACKER = "persist.service.xtc_hacker";
    public static final String GSM_SIGNAL_STRENGTH = "gsm.signal.strength";
    public static final String GSM_CT_VOLTE = "gsm.ct.volte";
    public static final String GSM_BASEBAND = "gsm.version.baseband";

    /** Known inner model codes. */
    public interface Model {

        interface Inner {
            String IDI13 = "IDI13";
            String IDI6C = "IDI6C";
            String Y01 = "Y01";
            String Y02 = "Y02";
            String Y1A = "Y1A";
            String I25 = "I25";
            String I26 = "I26";
            String I26A = "I26A";
            String I28 = "I28";
            String I25C = "I25C";
            String I32 = "I32";
            String I25D = "I25D";
            String ND01 = "ND01";
            String UNKNOWN = "";
            String DI01 = "DI01";
            String DI02 = "DI02";
            String F1 = "F1";
            String F2 = "F2";
            String F3 = "F3";
            String F4 = "F4";
            String GLI17 = "GLI17";
            String HKI17 = "HKI17";
            String PHI17 = "PHI17";
            String THI17 = "THI17";
            String IA = "IA";
            String IB = "IB";
            String I12 = "I12";
            String I13 = "I13";
            String I13C = "I13C";
            String I16 = "I16";
            String I17 = "I17";
            String I17D = "I17D";
            String I17E = "I17E";
            String I18 = "I18";
            String I19 = "I19";
            String I20 = "I20";
            String I2C = "I2C";
            String I3 = "I3";
            String I6 = "I6";
            String I8 = "I8";
        }

        interface InnerEx {
            String QCH = "QCH";
        }

        interface Outer {
            String XTC_Z3 = "XTC_Z3";
            String XTC_Z2Y = "XTC_Z2y";
            String XTC_Z5 = "XTC_Z5";
            String XTC_V1 = "XTC V1";
        }
    }

    /** Values of the {@code persist.sys.ostype} property. */
    public interface OsType {
        String PRESCHOOL = "preschool";
        String PRIMARY = "primary";
        String JUNIOR = "junior";
        String SENIOR = "senior";
        /** Alias of {@link #PRIMARY}. */
        String PRIMARY_ALIAS = "primary";
    }

    /** Values of the {@code persist.sys.modestatus} property. */
    public interface PropertyStatusValue {
        String NORMAL = "normal";
        String OFFICIAL_DISABLE = "official_disable";
        String SLEEP = "sleep";
        String POWER_SAVE = "power_save";
        String LONG_BATTERY = "long_battery";
        String CLASS = "class";
        String HIGH_TEMP = "high_temp";
        String WATCH_LOSS = "watch_loss";
    }

    /** Properties that are cleared when the watch is reset. */
    public static List<String> getResettableProperties() {
        ArrayList<String> properties = new ArrayList<>();
        properties.add(CLOSE_WIFI);
        properties.add(CLOSE_BLE);
        properties.add(QUICK_DIAL_STATUS);
        properties.add(LOCATION_STATUS);
        properties.add(SETTING_RING);
        properties.add(LOCATION_HIGH_AC);
        properties.add(HEADSET);
        properties.add(LAST_DEVICE);
        properties.add(BT_CALL_ONCE);
        properties.add(BT_SHUTDOWN);
        properties.add(BT_WARN);
        properties.add(MODE_STATUS);
        properties.add(LONG_BATTERY_LIFE);
        properties.add(CLASS_MODE_STATUS);
        properties.add(SLEEP_MODE_STATUS);
        properties.add(OFFICIAL_STAT);
        properties.add(SETTING_SAVE_POWER);
        properties.add(WATCH_LOSS_STATUS);
        properties.add(BT_REJECT_CALL);
        properties.add(UNLOCK_HIDDEN);
        properties.add(PRESCHOOL_STATUS);
        properties.add(VC_CALLING);
        properties.add(QUICK_DIAL_STATUS);
        properties.add(DIAL_110_STATUS);
        properties.add(DIAL_120_STATUS);
        properties.add(RING_SWITCH);
        properties.add(SETTING_RING);
        properties.add(WIFI_VISIBLE_STATUS);
        properties.add(WIFI_USER_OPERATE);
        properties.add(TAKE_PHOTO_MODE);
        properties.add(CPU_MONITOR_STATUS);
        return properties;
    }
}