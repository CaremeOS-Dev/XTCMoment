package com.xtc.watch.net

import android.content.Context

/** Base of the remote-config business services, holding the application context. */
abstract class ConfigBusinessService(context: Context) {

    protected val appContext: Context = context.applicationContext ?: context
}