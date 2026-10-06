package com.xtc.moment.db.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import com.xtc.moment.db.Constants;

/** Reminder configuration for one label (send/receive copy, colours, H5 urls). */
@DatabaseTable(tableName = Constants.TableName.MOMENT_REMINDER_CONFIG)
public class DbReminder {

    public static final String REMINDER_LABEL = "label";

    @DatabaseField(columnName = REMINDER_LABEL, id = true)
    private String label;

    @DatabaseField
    private String receiveColor;

    @DatabaseField
    private String receiveContent;

    @DatabaseField
    private String receiveH5Url;

    @DatabaseField
    private int receiveNotification;

    @DatabaseField
    private String sendColor;

    @DatabaseField
    private String sendContent;

    @DatabaseField
    private String sendH5Url;

    @DatabaseField
    private int sendNotification;

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getSendContent() {
        return this.sendContent;
    }

    public void setSendContent(String sendContent) {
        this.sendContent = sendContent;
    }

    public String getSendColor() {
        return this.sendColor;
    }

    public void setSendColor(String sendColor) {
        this.sendColor = sendColor;
    }

    public String getReceiveContent() {
        return this.receiveContent;
    }

    public void setReceiveContent(String receiveContent) {
        this.receiveContent = receiveContent;
    }

    public String getReceiveColor() {
        return this.receiveColor;
    }

    public void setReceiveColor(String receiveColor) {
        this.receiveColor = receiveColor;
    }

    public int getSendNotification() {
        return this.sendNotification;
    }

    public void setSendNotification(int sendNotification) {
        this.sendNotification = sendNotification;
    }

    public int getReceiveNotification() {
        return this.receiveNotification;
    }

    public void setReceiveNotification(int receiveNotification) {
        this.receiveNotification = receiveNotification;
    }

    public String getSendH5Url() {
        return this.sendH5Url;
    }

    public void setSendH5Url(String sendH5Url) {
        this.sendH5Url = sendH5Url;
    }

    public String getReceiveH5Url() {
        return this.receiveH5Url;
    }

    public void setReceiveH5Url(String receiveH5Url) {
        this.receiveH5Url = receiveH5Url;
    }

    @Override
    public String toString() {
        return "DbReminder{label='" + this.label + "', sendContent='" + this.sendContent + "', sendColor='" + this.sendColor + "', sendNotification=" + this.sendNotification + ", sendH5Url='" + this.sendH5Url + "', receiveContent='" + this.receiveContent + "', receiveColor='" + this.receiveColor + "', receiveNotification=" + this.receiveNotification + ", receiveH5Url='" + this.receiveH5Url + "'}";
    }
}
