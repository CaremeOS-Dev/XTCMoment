package com.xtc.dataservice.api.domain

import android.os.Parcelable
import com.google.protobuf.InvalidProtocolBufferException
import com.xtc.dataservice.api.domain.DomainProto.Value.ValueCase
import com.xtc.ipc.client.ProtoParcelable
import java.nio.ByteBuffer

/**
 * One session record. Unlike [DataPoint] the end time is optional and reports
 * null while the session is still running.
 */
class Session private constructor(proto: DomainProto.Session) : ProtoParcelable<DomainProto.Session>(proto) {

    constructor(bytes: ByteArray) : this(DomainProto.Session.parseFrom(bytes))

    fun getId(): String = proto.id

    fun getSessionType(): Int = proto.sessionType

    fun getSessionSource(): String = proto.sessionSource

    fun getStartTimeAtMillis(): Long = proto.startTimeAtMillis

    fun getEndTimeAtMillis(): Long? = proto.endTimeAtMillis

    fun getIntField(fieldId: Int): Int? =
        proto.getValueOrDefault(fieldId, null)?.takeIf { it.valueCase == ValueCase.INTVAL }?.intVal

    fun getLongField(fieldId: Int): Long? =
        proto.getValueOrDefault(fieldId, null)?.takeIf { it.valueCase == ValueCase.LONGVAL }?.longVal

    fun getFloatField(fieldId: Int): Float? =
        proto.getValueOrDefault(fieldId, null)?.takeIf { it.valueCase == ValueCase.FLOATVAL }?.floatVal

    fun getDoubleField(fieldId: Int): Double? =
        proto.getValueOrDefault(fieldId, null)?.takeIf { it.valueCase == ValueCase.DOUBLEVAL }?.doubleVal

    fun getStringField(fieldId: Int): String? =
        proto.getValueOrDefault(fieldId, null)?.takeIf { it.valueCase == ValueCase.STRINGVAL }?.stringVal

    fun getByteArrayField(fieldId: Int): ByteBuffer? =
        proto.getValueOrDefault(fieldId, null)?.takeIf { it.valueCase == ValueCase.BYTEARRAYVAL }
            ?.byteArrayVal?.asReadOnlyByteBuffer()

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Session> =
            newCreator({ Session(it) }, DomainProto.Session.parser())

        @JvmStatic
        @Throws(InvalidProtocolBufferException::class)
        fun parseFrom(bytes: ByteArray): Session = Session(bytes)
    }
}