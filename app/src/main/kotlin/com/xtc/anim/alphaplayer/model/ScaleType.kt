package com.xtc.anim.alphaplayer.model

/**
 * 视频缩放模式，取值与外部下发的 index 一一对应。
 */
enum class ScaleType(private val index: Int) {
    ScaleToFill(0),
    ScaleAspectFitCenter(1),
    ScaleAspectFill(2),
    TopFill(3),
    BottomFill(4),
    LeftFill(5),
    RightFill(6),
    TopFit(7),
    BottomFit(8),
    LeftFit(9),
    RightFit(10);

    companion object {
        /** 根据外部 index 转换缩放模式，未知取值回退为拉伸填满。 */
        fun convertFrom(index: Int): ScaleType = when (index) {
            1 -> ScaleAspectFitCenter
            2 -> ScaleAspectFill
            3 -> TopFill
            4 -> BottomFill
            5 -> LeftFill
            6 -> RightFill
            7 -> TopFit
            8 -> BottomFit
            9 -> LeftFit
            10 -> RightFit
            else -> ScaleToFill
        }
    }
}