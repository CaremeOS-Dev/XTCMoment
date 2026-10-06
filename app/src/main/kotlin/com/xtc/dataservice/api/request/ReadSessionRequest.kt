package com.xtc.dataservice.api.request

import android.os.Parcelable
import com.xtc.ipc.client.ProtoParcelable
import java.util.concurrent.TimeUnit

/** Reads the sessions of the given types inside a time range. */
class ReadSessionRequest private constructor(proto: RequestProto.ReadSessionRequest) :
    ProtoParcelable<RequestProto.ReadSessionRequest>(proto) {

    fun getStartTimeAtMils(): Long = proto.startTimeAtMils

    fun getEndTimeAtMils(): Long = proto.endTimeAtMils

    fun getSessionTypeList(): List<Int> = proto.sessionTypeList

    class Builder {

        private var startTimeAtMils = 0L
        private var endTimeAtMils = 0L
        private val sessionTypes = ArrayList<Int>()

        fun addSessionType(sessionType: Int) = apply { sessionTypes.add(sessionType) }

        fun setTimeRange(start: Long, end: Long, unit: TimeUnit) = apply {
            this.startTimeAtMils = unit.toMillis(start)
            this.endTimeAtMils = unit.toMillis(end)
        }

        fun build(): ReadSessionRequest = ReadSessionRequest(
            RequestProto.ReadSessionRequest.newBuilder()
                .setStartTimeAtMils(startTimeAtMils)
                .setEndTimeAtMils(endTimeAtMils)
                .addAllSessionType(sessionTypes)
                .build()
        )
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<ReadSessionRequest> =
            newCreator({ ReadSessionRequest(it) }, RequestProto.ReadSessionRequest.parser())
    }
}