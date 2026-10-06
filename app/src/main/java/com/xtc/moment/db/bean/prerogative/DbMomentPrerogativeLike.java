package com.xtc.moment.db.bean.prerogative;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.xtc.moment.db.Constants;

/** Like prerogative with its own praise icon resources. */
@DatabaseTable(tableName = Constants.TableName.MOMENT_PREROGATIVE_LIKE)
public class DbMomentPrerogativeLike extends AbsPrerogativeBean {

    @DatabaseField
    private String praisedDisablePic;

    @DatabaseField
    private String praisedPic;

    public void setPraisedPic(String praisedPic) {
        this.praisedPic = praisedPic;
    }

    public String getPraisedPic() {
        return this.praisedPic;
    }

    public String getPraisedDisablePic() {
        return this.praisedDisablePic;
    }

    public void setPraisedDisablePic(String praisedDisablePic) {
        this.praisedDisablePic = praisedDisablePic;
    }

    @Override
    public String toString() {
        return "DbMomentPrerogativeLike{id=" + this.id + ", emotionCode='" + this.emotionCode + "', praisedPic='"
                + this.praisedPic + "', praisedDisablePic='" + this.praisedDisablePic + "', emotionType="
                + this.emotionType + ", prerogativeId=" + this.prerogativeId + ", localEmotionPath='"
                + this.localEmotionPath + "', emotionTypeDesc='" + this.emotionTypeDesc + "', emotionName='"
                + this.emotionName + "', netDynamicName='" + this.netDynamicName + "', netDynamicUrl='"
                + this.netDynamicUrl + "', netDynamicVersion='" + this.netDynamicVersion + "', useStatus="
                + this.useStatus + ", useTime=" + this.useTime + ", expireMins=" + this.expireMins + ", expireTime="
                + this.expireTime + '}';
    }
}