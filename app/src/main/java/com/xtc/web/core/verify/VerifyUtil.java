package com.xtc.web.core.verify;

import com.xtc.database.ormlite.CollectionUtil;
import com.xtc.web.core.utils.JSONUtil;

import java.util.List;

/** 白名单数据转换工具。 */
public class VerifyUtil {

    /** 把服务端返回的域名列表转换为数据库实体。 */
    public static DbVerify trans2DbVerify(List<String> whiteDatas) {
        if (CollectionUtil.isEmpty(whiteDatas)) {
            return null;
        }
        DbVerify dbVerify = new DbVerify();
        dbVerify.setWhiteDatas(JSONUtil.toJSON(whiteDatas));
        return dbVerify;
    }
}