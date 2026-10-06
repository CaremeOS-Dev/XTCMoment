package com.xtc.dataservice.api.request

import android.os.Parcelable
import com.xtc.ipc.client.ProtoParcelable

/** Registers a listener for the sessions of the given types. */
class RegisterSessionListenerRequest private constructor(proto: RequestProto.RegisterSessionListenerRequest) :
    ProtoParcelable<RequestProto.RegisterSessionListenerRequest>(proto) {

    fun getSessionTypeList(): List<Int> = proto.sessionTypeList

    class Builder {

        private val sessionTypes = ArrayList<Int>()

        fun addSessionType(sessionType: Int) = apply { sessionTypes.add(sessionType) }

        fun build(): RegisterSessionListenerRequest = RegisterSessionListenerRequest(
            RequestProto.RegisterSessionListenerRequest.newBuilder().addAllSessionType(sessionTypes).build()
        )
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<RegisterSessionListenerRequest> =
            newCreator({ RegisterSessionListenerRequest(it) },
                RequestProto.RegisterSessionListenerRequest.parser())
    }
}