package com.xtc.moment.module.personalinfo.constant;

/**
 * 个性装扮服务常量。
 */
public interface DressConstants {

    String DRESS_SERVICE_ACTION = "com.xtc.personalitydress.service.DressService";
    String DRESS_SERVICE_PACKAGE = "com.xtc.theme";

    interface DressMovementType {
        int STATIC_DRESS = 1;
        int DYNAMIC_DRESS = 2;
    }
}