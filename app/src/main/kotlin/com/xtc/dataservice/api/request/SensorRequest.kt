package com.xtc.dataservice.api.request

import android.os.Parcelable
import com.xtc.ipc.client.ProtoParcelable

/** Subscribes to the sensor data points of the given types. */
class SensorRequest private constructor(proto: RequestProto.SensorRequest) :
    ProtoParcelable<RequestProto.SensorRequest>(proto) {

    fun getDataTypeList(): List<Int> = proto.dataTypeList

    fun getIncludeLastKnown(): Boolean = proto.includeLastKnown

    class Builder {

        private val dataTypes = ArrayList<Int>()
        private var includeLastKnown = false

        fun addDataType(dataType: Int) = apply { dataTypes.add(dataType) }

        fun setIncludeLastKnown(includeLastKnown: Boolean) = apply { this.includeLastKnown = includeLastKnown }

        fun build(): SensorRequest = SensorRequest(
            RequestProto.SensorRequest.newBuilder()
                .addAllDataType(dataTypes)
                .setIncludeLastKnown(includeLastKnown)
                .build()
        )
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<SensorRequest> =
            newCreator({ SensorRequest(it) }, RequestProto.SensorRequest.parser())
    }
}