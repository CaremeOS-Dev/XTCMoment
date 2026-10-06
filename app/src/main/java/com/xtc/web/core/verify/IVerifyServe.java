package com.xtc.web.core.verify;

/** 白名单本地存储读写接口。 */
public interface IVerifyServe {

    DbVerify queryWhiteDatas();

    DbVerify updateWhiteDatas(DbVerify dbVerify);
}