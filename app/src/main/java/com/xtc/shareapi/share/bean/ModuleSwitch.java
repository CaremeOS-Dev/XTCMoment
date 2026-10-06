package com.xtc.shareapi.share.bean;

/**
 * 分享模块开关状态及提示文案。
 */
public class ModuleSwitch {

    private boolean module;
    private String tip;

    public ModuleSwitch() {
    }

    public ModuleSwitch(boolean module, String tip) {
        this.module = module;
        this.tip = tip;
    }

    public boolean isModule() {
        return module;
    }

    public void setModule(boolean module) {
        this.module = module;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    @Override
    public String toString() {
        return "ModuleSwitch{module=" + module + ", tip='" + tip + "'}";
    }
}