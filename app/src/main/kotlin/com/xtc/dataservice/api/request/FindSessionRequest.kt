package com.xtc.dataservice.api.request

import android.os.Parcelable
import com.xtc.ipc.client.ProtoParcelable

/** Looks a session up by id. */
class FindSessionRequest private constructor(proto: RequestProto.FindSessionRequest) :
    ProtoParcelable<RequestProto.FindSessionRequest>(proto) {

    fun getSessionId(): String = proto.sessionId

    class Builder {

        private var sessionId = ""

        fun setSessionId(sessionId: String) = apply { this.sessionId = sessionId }

        fun build(): FindSessionRequest =
            FindSessionRequest(RequestProto.FindSessionRequest.newBuilder().setSessionId(sessionId).build())
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<FindSessionRequest> =
            newCreator({ FindSessionRequest(it) }, RequestProto.FindSessionRequest.parser())
    }
}