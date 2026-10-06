package com.xtc.dataservice.api.request

import android.os.Parcelable
import com.xtc.ipc.client.ProtoParcelable

/** Requests the last known value of each data type. */
class FindLastKnownDataRequest private constructor(proto: RequestProto.FindLastKnownDataRequest) :
    ProtoParcelable<RequestProto.FindLastKnownDataRequest>(proto) {

    fun getDataTypeList(): List<Int> = proto.dataTypeList

    class Builder {

        private val dataTypes = ArrayList<Int>()

        fun addDataType(dataType: Int) = apply { dataTypes.add(dataType) }

        fun build(): FindLastKnownDataRequest =
            FindLastKnownDataRequest(RequestProto.FindLastKnownDataRequest.newBuilder().addAllDataType(dataTypes).build())
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<FindLastKnownDataRequest> =
            newCreator({ FindLastKnownDataRequest(it) }, RequestProto.FindLastKnownDataRequest.parser())
    }
}