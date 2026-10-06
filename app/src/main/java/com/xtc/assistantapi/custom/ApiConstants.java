package com.xtc.assistantapi.custom;

/**
 * 自定义用户交互能力的指令命名空间常量。
 */
public class ApiConstants {

    public static final String NAMESPACE_CUSTOM_USER_INTERACTION = "ai.dueros.device_interface.extensions.custom_user_interaction";
    public static final String NAME_CUSTOM_USER_INTERACTION = "CustomUserInteraction";

    /**
     * 指令名常量。
     */
    public static final class Directives {

        /**
         * 点击链接指令。
         */
        public static final class ClickLink {
            public static final String NAME = "ClickLink";
        }
    }
}