package com.xtc.im.core.app;

/** IM 服务使用的广播 action。 */
public interface IntentAction {

    String ACTION_IM_DATA_SUFFIX = ".im.data";
    String CRASH_ON_PUSH = "com.xtc.im.core.crash.on_push";
    String DEVICE_SHUTDOWN_ACTION = "com.xtc.im.core.device.shutdown";
    String HEARTBEAT_REQUEST = "com.xtc.im.core.heartbeat";
    String KILL_PUSH_PROCESS = "com.xtc.im.core.kill.push_process";
    String NETWORK_CONNECTED_ACTION = "com.xtc.im.core.network.connected";
    String PUSH_HOST_SERVICE_CHECK = "com.xtc.im.core.push.host_service.check";
    String PUSH_INFO_CHANGED = "com.xtc.im.core.push_info_changed";
    String PUSH_INFO_COLLECTION = "com.xtc.im.core.push.info.collection";
    String READ_DATA_ACTION = "com.xtc.im.core.read_data";
    String RECONNECT_ACTION = "com.xtc.im.core.reconnect";
    String SERVER_ERROR = "com.xtc.im.core.server_error";
    String START_ACTION = "com.xtc.im.core.start";
    String STOP_CONN_SERVICE_ACTION = "com.xtc.im.core.stop_conn_service";
    String SYNC_CONNECTED_ACTION = "com.xtc.im.core.connected";
    String SYNC_CONNECTING_ACTION = "com.xtc.im.core.connecting";
    String SYNC_CONNECT_FAIL_ACTION = "com.xtc.im.core.connect_fail";
    String SYNC_DISCONNECTED_ACTION = "com.xtc.im.core.disconnected";
    String SYNC_RESPONSE_ACTION = "com.xtc.im.core.sync_response";
    String TAG = "com.xtc.im.core";
    String UDP_INIT = "udp_init";
    String UDP_INIT_RESP = "udp_init_resp";
}