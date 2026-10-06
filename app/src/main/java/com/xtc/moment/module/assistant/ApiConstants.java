package com.xtc.moment.module.assistant;

import java.io.Serializable;

/**
 * 语音助手接口常量。
 */
public class ApiConstants implements Serializable {

    public static final String NAME = "MomentInterface";
    public static final String NAMESPACE = "ai.dueros.device_interface.thirdparty.watch.assistant";

    public static final class Directives {

        public static final class PostStatus {
            public static final String NAME = "PostStatus";
        }
    }

    public static final class Events {
    }
}