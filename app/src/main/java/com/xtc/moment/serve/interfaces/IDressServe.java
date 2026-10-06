package com.xtc.moment.serve.interfaces;

import android.os.IBinder;

import com.xtc.moment.db.bean.DbHead;
import com.xtc.moment.db.bean.DbNickname;
import com.xtc.moment.serve.DressProxy;
import com.xtc.moment.serve.impl.DressServeImpl;

import java.util.HashMap;
import java.util.List;

/**
 * 装扮服务接口。
 */
public interface IDressServe {

    interface DressType {
        int BUBBLE = 0;
        int NICKNAME = 1;
        int HEADFRAME = 2;
    }

    void bindService();

    DressProxy getProxy();

    void setProxy(DressProxy proxy);

    void releaseDeathRecipient(IBinder.DeathRecipient deathRecipient);

    boolean addOrUpdateHead(DbHead head);

    boolean addOrUpdateNickname(DbNickname nickname);

    List<DbHead> getHeadById(List<String> headIds, DressServeImpl.ICallback<Boolean> callback);

    DbNickname getNicknameById(String nicknameId, DressServeImpl.ICallback<Boolean> callback);

    List<DbNickname> getNicknameById(List<String> nicknameIds, DressServeImpl.ICallback<Boolean> callback);

    void getHeadIdByWatchId(List<String> watchIds, DressServeImpl.ICallback<HashMap> callback);

    void getNicknameIdByWatchId(List<String> watchIds, DressServeImpl.ICallback<HashMap> callback);

    void notifyDressExpired(String dressId, int dressType);
}