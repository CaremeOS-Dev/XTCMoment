package com.xtc.shareapi.share.interfaces;

import com.xtc.shareapi.share.communication.BaseResponse;

/**
 * 跳转到好友圈发动态的入参对象，不同来源（相册/相机/外部）实现各自的参数校验。
 */
public interface IJumpToMomentObject {

    /** 来源：相机。 */
    int FROM_CAMARA = 0;
    /** 来源：相册。 */
    int FROM_ALBUM = 1;
    /** 来源：外部应用。 */
    int FROM_OUTSIDE = 2;

    /** 来源类型。 */
    int type();

    /** 校验参数是否合法。 */
    BaseResponse checkArgs();
}