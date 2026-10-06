package com.xtc.web.core.verify;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/** H5 域名白名单缓存，whiteDatas 为白名单 JSON 数组字符串。 */
@DatabaseTable(tableName = Constants.TableName.SHARE_WHITE_TABLE_NAME)
public class DbVerify {

    public static final String WHITE_DATAS_FIELD_NAME = "whiteDatas";

    @DatabaseField(columnName = WHITE_DATAS_FIELD_NAME)
    private String whiteDatas;

    public String getWhiteDatas() {
        return this.whiteDatas;
    }

    public void setWhiteDatas(String whiteDatas) {
        this.whiteDatas = whiteDatas;
    }

    @Override
    public String toString() {
        return "VerifyBean{whiteDatas=" + this.whiteDatas + '}';
    }
}