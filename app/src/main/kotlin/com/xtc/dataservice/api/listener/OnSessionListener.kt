package com.xtc.dataservice.api.listener

import com.xtc.dataservice.api.domain.Session

/** Receives session start/end notifications. */
interface OnSessionListener {
    fun onSessionStart(session: Session)

    fun onSessionEnd(session: Session)
}