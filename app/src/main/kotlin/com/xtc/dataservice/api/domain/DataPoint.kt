package com.xtc.dataservice.api.domain

import android.os.Parcelable
import com.google.protobuf.InvalidProtocolBufferException
import com.xtc.dataservice.api.domain.DomainProto.Value.ValueCase
import com.xtc.ipc.client.ProtoParcelable
import java.nio.ByteBuffer

/**
 * One sensor data record. Values are stored in a sparse map keyed by field id and
 * exposed through the typed accessors below.
 */
class DataPoint private constructor(proto: DomainProto.DataPoint) : ProtoParcelable<DomainProto.DataPoint>(proto) {

    constructor(bytes: ByteArray) : this(DomainProto.DataPoint.parseFrom(bytes))

    fun getDataType(): Int = proto.dataType

    fun getDataSource(): String = proto.dataSource

    fun getStartTimeAtMillis(): Long = proto.startTimeAtMillis

    fun getEndTimeAtMillis(): Long = proto.endTimeAtMillis

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

    /** Copies this record into a builder. */
    fun toBuilder(): Builder = Builder(this)

    /** Mutable builder of [DataPoint]. */
    class Builder {

        private var dataType = -1
        private var dataSource = ""
        private var startTimeAtMillis = 0L
        private var endTimeAtMillis = 0L
        private val values = HashMap<Int, DomainProto.Value>()

        constructor() {
            setTime(System.currentTimeMillis())
        }

        constructor(dataPoint: DataPoint) {
            this.dataType = dataPoint.getDataType()
            this.dataSource = dataPoint.getDataSource()
            this.startTimeAtMillis = dataPoint.getStartTimeAtMillis()
            this.endTimeAtMillis = dataPoint.getEndTimeAtMillis()
            this.values.putAll(dataPoint.proto.valueMap)
        }

        fun setDataType(dataType: Int) = apply { this.dataType = dataType }

        fun setDataSource(dataSource: String) = apply { this.dataSource = dataSource }

        /** Sets both the start and end time. */
        fun setTime(timeMillis: Long) = apply {
            this.startTimeAtMillis = timeMillis
            this.endTimeAtMillis = timeMillis
        }

        fun setStartTime(startTimeAtMillis: Long) = apply { this.startTimeAtMillis = startTimeAtMillis }

        fun setEndTime(endTimeAtMillis: Long) = apply { this.endTimeAtMillis = endTimeAtMillis }

        fun setIntField(fieldId: Int, value: Int?) = apply {
            if (value == null) values.remove(fieldId) else values[fieldId] =
                DomainProto.Value.newBuilder().setIntVal(value).build()
        }

        fun setLongField(fieldId: Int, value: Long?) = apply {
            if (value == null) values.remove(fieldId) else values[fieldId] =
                DomainProto.Value.newBuilder().setLongVal(value).build()
        }

        fun setFloatField(fieldId: Int, value: Float?) = apply {
            if (value == null) values.remove(fieldId) else values[fieldId] =
                DomainProto.Value.newBuilder().setFloatVal(value).build()
        }

        fun setDoubleField(fieldId: Int, value: Double?) = apply {
            if (value == null) values.remove(fieldId) else values[fieldId] =
                DomainProto.Value.newBuilder().setDoubleVal(value).build()
        }

        fun setStringField(fieldId: Int, value: String?) = apply {
            if (value == null) values.remove(fieldId) else values[fieldId] =
                DomainProto.Value.newBuilder().setStringVal(value).build()
        }

        fun setByteArrayField(fieldId: Int, value: ByteBuffer?) = apply {
            if (value == null) {
                values.remove(fieldId)
            } else {
                values[fieldId] = DomainProto.Value.newBuilder()
                    .setByteArrayVal(com.google.protobuf.ByteString.copyFrom(value))
                    .build()
            }
        }

        fun build(): DataPoint = DataPoint(
            DomainProto.DataPoint.newBuilder()
                .setStartTimeAtMillis(startTimeAtMillis)
                .setEndTimeAtMillis(endTimeAtMillis)
                .setDataType(dataType)
                .setDataSource(dataSource)
                .putAllValue(values)
                .build()
        )
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<DataPoint> =
            newCreator({ DataPoint(it) }, DomainProto.DataPoint.parser())

        @JvmStatic
        @Throws(InvalidProtocolBufferException::class)
        fun parseFrom(bytes: ByteArray): DataPoint = DataPoint(bytes)
    }
}