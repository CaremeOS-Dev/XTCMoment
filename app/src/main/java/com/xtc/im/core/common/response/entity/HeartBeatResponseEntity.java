package com.xtc.im.core.common.response.entity;

import com.xtc.im.core.common.anotation.CommandValue;
import com.xtc.im.core.common.anotation.TagValue;

@CommandValue(8)
public class HeartBeatResponseEntity extends ResponseEntity {
    public static final int SERVER_ERROR = 1;
    public static final int SERVER_OK = 0;
    public static int currentStatus;


    @TagValue(10)
    private int serverStatus;

    public int getServerStatus() {
        return this.serverStatus;
    }

    public void setServerStatus(int serverStatus) {
        this.serverStatus = serverStatus;
    }

    @Override
    public String toString() {
        return "HeartBeatResponseEntity{serverStatus=" + this.serverStatus + "}";
    }
}
