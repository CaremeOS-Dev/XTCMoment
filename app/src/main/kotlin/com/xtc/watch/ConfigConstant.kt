package com.xtc.watch

/**
 * Static names used by the remote-config store.
 */
object ConfigConstant {

    /** Identifies where a config entry applies. */
    object Field {
        const val ANDROID_WATCH = "android-watch"
        const val RTOS_WATCH = " rtos-watch"
        const val APP = "app"
        const val H5 = "h5"
    }

    /** File names and directories of the config store. */
    object Config {
        const val DEFAULT_CONFIG_NAME = "RemoteConfig.json"
        const val DEFAULT_SYSTEM_CONFIG_NAME = "/vendor/res/remoteconfig/"
    }

    /** Version comparison modes. */
    object VersionEqualType {
        const val EQUAL = 1
        const val LESS = 2
        const val LESS_AND_EQUAL = 3
        const val MORE = 4
        const val MORE_AND_EQUAL = 5
    }
}