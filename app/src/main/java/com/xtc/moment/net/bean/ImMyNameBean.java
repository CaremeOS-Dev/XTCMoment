package com.xtc.moment.net.bean;

/** An IM name-change record. */
public class ImMyNameBean {
    private DataBean data;
    private String id;
    private String mobileId;
    private int tableType;
    private String watchId;

    public int getTableType() {
        return this.tableType;
    }

    public void setTableType(int tableType) {
        this.tableType = tableType;
    }

    public DataBean getData() {
        return this.data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getMobileId() {
        return this.mobileId;
    }

    public void setMobileId(String mobileId) {
        this.mobileId = mobileId;
    }

    public String getWatchId() {
        return this.watchId;
    }

    public void setWatchId(String watchId) {
        this.watchId = watchId;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /** The changed value and its type. */
    public static class DataBean {
        private String data;
        private int type;

        public int getType() {
            return this.type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getData() {
            return this.data;
        }

        public void setData(String data) {
            this.data = data;
        }

        @Override
        public String toString() {
            return "DataBean{type=" + this.type + ", data='" + this.data + "'}";
        }
    }

    @Override
    public String toString() {
        return "ImMyNameBean{tableType=" + this.tableType + ", data=" + this.data + ", mobileId='" + this.mobileId + "', watchId='" + this.watchId + "', id='" + this.id + "'}";
    }
}
