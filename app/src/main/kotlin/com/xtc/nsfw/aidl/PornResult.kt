package com.xtc.nsfw.aidl

import android.os.Parcel
import android.os.Parcelable

/**
 * 色情分级结果：各类别的置信度及版本号。
 */
class PornResult() : Parcelable {

    /** 结果版本号。 */
    lateinit var version: String

    /** 二次元色情置信度。 */
    var hentai: Float = 0f

    /** 中性置信度。 */
    var neutral: Float = 0f

    /** 色情置信度。 */
    var porn: Float = 0f

    /** 性感置信度。 */
    var sexy: Float = 0f

    /** 绘图置信度。 */
    var drawings: Float = 0f

    constructor(parcel: Parcel) : this() {
        version = parcel.readString().toString()
        drawings = parcel.readFloat()
        hentai = parcel.readFloat()
        neutral = parcel.readFloat()
        porn = parcel.readFloat()
        sexy = parcel.readFloat()
    }

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(version)
        dest.writeFloat(drawings)
        dest.writeFloat(hentai)
        dest.writeFloat(neutral)
        dest.writeFloat(porn)
        dest.writeFloat(sexy)
    }

    override fun toString(): String =
        "PornResult(version='$version', drawings=$drawings, hentai=$hentai, neutral=$neutral, porn=$porn, sexy=$sexy)"

    companion object CREATOR : Parcelable.Creator<PornResult> {
        override fun createFromParcel(parcel: Parcel): PornResult = PornResult(parcel)

        override fun newArray(size: Int): Array<PornResult?> = arrayOfNulls(size)
    }
}