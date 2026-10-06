package com.xtc.dataservice.api.request

import android.os.Parcelable
import com.xtc.ipc.client.ProtoParcelable

/** Finds the sessions of the given types that have not ended yet. */
class FindStartingSessionRequest private constructor(proto: RequestProto.FindStartingSessionRequest) :
    ProtoParcelable<RequestProto.FindStartingSessionRequest>(proto) {

    fun getSessionTypeList(): List<Int> = proto.sessionTypeList

    class Builder {

        private val sessionTypes = ArrayList<Int>()

        fun addSessionType(sessionType: Int) = apply { sessionTypes.add(sessionType) }

        fun build(): FindStartingSessionRequest = FindStartingSessionRequest(
            RequestProto.FindStartingSessionRequest.newBuilder().addAllSessionType(sessionTypes).build()
        )
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<FindStartingSessionRequest> =
            newCreator({ FindStartingSessionRequest(it) }, RequestProto.FindStartingSessionRequest.parser())
    }
}