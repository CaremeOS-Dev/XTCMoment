package com.xtc.moment.module.bean;

/** One entry of the function grid. */
public class FunctionBean {

    private int icon;
    private String name;
    private int type;

    public FunctionBean(int icon, String name, int type) {
        this.icon = icon;
        this.name = name;
        this.type = type;
    }

    public int getIcon() {
        return this.icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "FunctionBean{icon=" + this.icon + ", name='" + this.name + "', type=" + this.type + '}';
    }
}