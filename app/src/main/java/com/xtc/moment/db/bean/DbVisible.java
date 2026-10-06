package com.xtc.moment.db.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import com.xtc.moment.db.Constants;

/** Per-moment visibility scope for one watch id. */
@DatabaseTable(tableName = Constants.TableName.MOMENT_VISIBLE)
public class DbVisible {

    public static final String MOMENT_ID_MOMENT_VISIBLE = "momentId";
    public static final String PERMISSION_TYPE_MOMENT_VISIBLE = "permissionType";
    public static final String WATCH_ID_MOMENT_VISIBLE = "watchId";

    @DatabaseField
    private String momentId;

    @DatabaseField
    private Integer permissionType;

    @DatabaseField
    private String watchId;

    public String getMomentId() {
        return this.momentId;
    }

    public void setMomentId(String momentId) {
        this.momentId = momentId;
    }

    public Integer getPermissionType() {
        return this.permissionType;
    }

    public void setPermissionType(Integer permissionType) {
        this.permissionType = permissionType;
    }

    public String getWatchId() {
        return this.watchId;
    }

    public void setWatchId(String watchId) {
        this.watchId = watchId;
    }

    @Override
    public String toString() {
        return "DbVisible{momentId='" + this.momentId + "', permissionType=" + this.permissionType + ", watchId='" + this.watchId + "'}";
    }
}
