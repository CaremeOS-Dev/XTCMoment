package com.xtc.moment.db.bean.prerogative;

import com.j256.ormlite.table.DatabaseTable;
import com.xtc.moment.db.Constants;

/** Background prerogative owned by the watch account. */
@DatabaseTable(tableName = Constants.TableName.MOMENT_PREROGATIVE_BACKGROUND)
public class DbMomentPrerogativeBackground extends AbsPrerogativeBean {

    @Override
    public String toString() {
        return "DbMomentPrerogativeBackground{id=" + this.id + ", emotionCode='" + this.emotionCode
                + "', prerogativeId=" + this.prerogativeId + ", emotionType=" + this.emotionType
                + ", localEmotionPath='" + this.localEmotionPath + "', emotionTypeDesc='" + this.emotionTypeDesc
                + "', emotionName='" + this.emotionName + "', netDynamicName='" + this.netDynamicName
                + "', netDynamicUrl='" + this.netDynamicUrl + "', netDynamicVersion='" + this.netDynamicVersion
                + "', useStatus=" + this.useStatus + ", useTime=" + this.useTime + ", expireMins=" + this.expireMins
                + ", expireTime=" + this.expireTime + '}';
    }
}