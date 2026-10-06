package com.xtc.utils.system.model;

/** Chipset identifiers reported by the system. */
public interface Platform {
    String QUALCOMM = "Qualcomm";
    /** Lower case hardware name reported by ro.hardware on Qualcomm devices. */
    String QCOM = "qcom";
    String UNKNOWN = "";
    String MEDIATEK = "MediaTek";
    String UNISOC = "Unisoc";
}