package com.xtc.dataservice.api.request

import android.os.Parcelable
import com.xtc.ipc.client.ProtoParcelable

/** Registers a listener for newly inserted data points of the given types. */
class RegisterDataInsertListenerRequest private constructor(proto: RequestProto.RegisterDataInsertListenerRequest) :
    ProtoParcelable<RequestProto.RegisterDataInsertListenerRequest>(proto) {

    fun getDataTypeList(): List<Int> = proto.dataTypeList

    class Builder {

        private val dataTypes = ArrayList<Int>()

        fun addDataType(dataType: Int) = apply { dataTypes.add(dataType) }

        fun build(): RegisterDataInsertListenerRequest = RegisterDataInsertListenerRequest(
            RequestProto.RegisterDataInsertListenerRequest.newBuilder().addAllDataType(dataTypes).build()
        )
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<RegisterDataInsertListenerRequest> =
            newCreator({ RegisterDataInsertListenerRequest(it) },
                RequestProto.RegisterDataInsertListenerRequest.parser())
    }
}