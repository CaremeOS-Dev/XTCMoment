package com.xtc.nsfw.aidl

import android.os.Parcel
import android.os.Parcelable

/**
 * 鉴黄上传结果：文件名称以及 NSFW / 色情分级两类结果列表。
 */
class NSFWUpload() : Parcelable {

    /** 文件名。 */
    lateinit var filename: String

    /** 色情分级结果列表。 */
    var pornResults: List<PornResult>? = null

    /** 鉴黄结果列表。 */
    var nsfwResults: List<NsfwResult>? = null

    constructor(parcel: Parcel) : this() {
        filename = parcel.readString().toString()
        pornResults = parcel.createTypedArrayList(PornResult.CREATOR)
        nsfwResults = parcel.createTypedArrayList(NsfwResult.CREATOR)
    }

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(filename)
        dest.writeTypedList(pornResults)
        dest.writeTypedList(nsfwResults)
    }

    override fun toString(): String =
        "NSFWUpload(filename='$filename', pornResults=$pornResults, nsfwResults=$nsfwResults)"

    companion object CREATOR : Parcelable.Creator<NSFWUpload> {
        override fun createFromParcel(parcel: Parcel): NSFWUpload = NSFWUpload(parcel)

        override fun newArray(size: Int): Array<NSFWUpload?> = arrayOfNulls(size)
    }
}