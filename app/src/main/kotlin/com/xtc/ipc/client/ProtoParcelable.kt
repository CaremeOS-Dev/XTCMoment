package com.xtc.ipc.client

import android.os.Parcel
import android.os.Parcelable
import com.google.protobuf.MessageLite
import com.google.protobuf.Parser

/**
 * Wraps a protobuf lite message so it can be sent over a [Parcel].
 */
abstract class ProtoParcelable<T : MessageLite>(protected val proto: T) : Parcelable {

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeByteArray(proto.toByteArray())
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (javaClass != other?.javaClass) {
            return false
        }
        return proto == (other as ProtoParcelable<*>).proto
    }

    override fun hashCode(): Int = proto.hashCode()

    override fun toString(): String = proto.toString()

    companion object {

        /** Builds a [Parcelable.Creator] that parses the proto with [parser] and wraps it via [ctor]. */
        @JvmStatic
        fun <T : ProtoParcelable<PROTO>, PROTO : MessageLite> newCreator(
            ctor: (PROTO) -> T,
            parser: Parser<PROTO>
        ): Parcelable.Creator<T> = object : Parcelable.Creator<T> {
            override fun createFromParcel(source: Parcel): T = ctor(parser.parseFrom(source.createByteArray()))

            @Suppress("UNCHECKED_CAST")
            override fun newArray(size: Int): Array<T?> = arrayOfNulls<ProtoParcelable<*>>(size) as Array<T?>
        }
    }
}