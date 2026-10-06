package com.xtc.utils.system;

/** Model of the {@code persist.sys.appconfig} property payload. */
final class SystemPropertyAppConfig {

    private String group;

    SystemPropertyAppConfig() {
    }

    public String getGroup() {
        return this.group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    @Override
    public String toString() {
        return "SystemPropertyAppConfig{group=\'" + this.group + "\'}";
    }
}