package com.xtc.bigdata.collector.encapsulation.entity.attr;

import android.content.ContentValues;

import com.xtc.bigdata.collector.encapsulation.interfaces.IAttr;
import com.xtc.bigdata.common.db.constant.Columns;

/** User attributes attached to every event. */
public class UserAttr implements IAttr {
    private String userExtend;
    private String userId;
    private String userName;

    @Override
    public void insert(ContentValues contentValues) {
        contentValues.put(Columns.COLUMN_UA_USERID, this.userId);
        contentValues.put(Columns.COLUMN_UA_USERNAME, this.userName);
        contentValues.put(Columns.COLUMN_UA_USEREXTEND, this.userExtend);
    }

    @Override
    public IAttr clone() {
        UserAttr userAttr = new UserAttr();
        userAttr.userExtend = this.userExtend;
        userAttr.userId = this.userId;
        userAttr.userName = this.userName;
        return userAttr;
    }

    public String getUserId() {
        return this.userId;
    }

    public UserAttr setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getUserName() {
        return this.userName;
    }

    public UserAttr setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getUserExtend() {
        return this.userExtend;
    }

    public UserAttr setUserExtend(String userExtend) {
        this.userExtend = userExtend;
        return this;
    }
}