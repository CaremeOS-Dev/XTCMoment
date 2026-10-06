package com.xtc.nsfw.aidl

import android.os.Parcel
import android.os.Parcelable

/**
 * 鉴黄结果：某个文件属于 NSFW / SFW 的置信度及版本号。
 */
class NsfwResult() : Parcelable {

    /** 结果版本号。 */
    lateinit var version: String

    /** NSFW 置信度。 */
    var nsfw: Float = 0f

    /** SFW 置信度。 */
    var sfw: Float = 0f

    constructor(parcel: Parcel) : this() {
        version = parcel.readString().toString()
        sfw = parcel.readFloat()
        nsfw = parcel.readFloat()
    }

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(version)
        dest.writeFloat(sfw)
        dest.writeFloat(nsfw)
    }

    override fun toString(): String = "NsfwResult(version='$version', sfw=$sfw, nsfw=$nsfw)"

    companion object CREATOR : Parcelable.Creator<NsfwResult> {
        override fun createFromParcel(parcel: Parcel): NsfwResult = NsfwResult(parcel)

        override fun newArray(size: Int): Array<NsfwResult?> = arrayOfNulls(size)
    }
}