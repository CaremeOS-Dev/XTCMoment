package com.xtc.dataservice.api.request

import android.os.Parcelable
import com.xtc.ipc.client.ProtoParcelable
import java.util.concurrent.TimeUnit

/** Reads the data points of the given types inside a time range. */
class ReadDataRequest private constructor(proto: RequestProto.ReadDataRequest) :
    ProtoParcelable<RequestProto.ReadDataRequest>(proto) {

    fun getStartTimeAtMils(): Long = proto.startTimeAtMils

    fun getEndTimeAtMils(): Long = proto.endTimeAtMils

    fun getDataTypeList(): List<Int> = proto.dataTypeList

    class Builder {

        private var startTimeAtMils = 0L
        private var endTimeAtMils = 0L
        private val dataTypes = ArrayList<Int>()

        fun addDataType(dataType: Int) = apply { dataTypes.add(dataType) }

        fun setTimeRange(start: Long, end: Long, unit: TimeUnit) = apply {
            this.startTimeAtMils = unit.toMillis(start)
            this.endTimeAtMils = unit.toMillis(end)
        }

        fun build(): ReadDataRequest = ReadDataRequest(
            RequestProto.ReadDataRequest.newBuilder()
                .setStartTimeAtMils(startTimeAtMils)
                .setEndTimeAtMils(endTimeAtMils)
                .addAllDataType(dataTypes)
                .build()
        )
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<ReadDataRequest> =
            newCreator({ ReadDataRequest(it) }, RequestProto.ReadDataRequest.parser())
    }
}