package com.xtc.moment.net.bean;

/** Request body for the ban-state query. */
public class BanStateBody {
    private String bindNumber;

    public String getBindNumber() {
        return this.bindNumber;
    }

    public void setBindNumber(String bindNumber) {
        this.bindNumber = bindNumber;
    }

    @Override
    public String toString() {
        return "BanStateBody{bindNumber='" + this.bindNumber + "'}";
    }
}
