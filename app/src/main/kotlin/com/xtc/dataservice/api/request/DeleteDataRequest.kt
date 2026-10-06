package com.xtc.dataservice.api.request

import android.os.Parcelable
import com.xtc.ipc.client.ProtoParcelable
import java.util.concurrent.TimeUnit

/** Deletes the data points of the given types inside a time range. */
class DeleteDataRequest private constructor(proto: RequestProto.DeleteDataRequest) :
    ProtoParcelable<RequestProto.DeleteDataRequest>(proto) {

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

        fun build(): DeleteDataRequest = DeleteDataRequest(
            RequestProto.DeleteDataRequest.newBuilder()
                .setStartTimeAtMils(startTimeAtMils)
                .setEndTimeAtMils(endTimeAtMils)
                .addAllDataType(dataTypes)
                .build()
        )
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<DeleteDataRequest> =
            newCreator({ DeleteDataRequest(it) }, RequestProto.DeleteDataRequest.parser())
    }
}