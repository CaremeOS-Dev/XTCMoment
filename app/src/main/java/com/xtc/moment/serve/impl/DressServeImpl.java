package com.xtc.moment.serve.impl;

import android.content.Context;
import android.os.IBinder;
import android.text.TextUtils;

import com.xtc.database.ormlite.CollectionUtil;
import com.xtc.database.ormlite.RxDao;
import com.xtc.log.LogUtil;
import com.xtc.moment.db.Constants;
import com.xtc.moment.db.bean.DbHead;
import com.xtc.moment.db.bean.DbNickname;
import com.xtc.moment.serve.DressProxy;
import com.xtc.moment.serve.interfaces.IDressServe;
import com.xtc.moment.util.DressUtil;
import com.xtc.personalitydress.aidl.IDressCallback;
import com.xtc.personalitydress.aidl.IDressIdCallback;
import com.xtc.personalitydress.aidl.RemoteDress;
import com.xtc.personalitydress.aidl.RemoteHead;
import com.xtc.personalitydress.aidl.RemoteNickname;

import java.lang.ref.WeakReference;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 装扮服务实现：本地缓存头像框/昵称，缺失时向远程服务拉取。
 */
public class DressServeImpl implements IDressServe {

    private static final String HEAD_ID = "headId";
    private static final String NICKNAME_ID = "nicknameId";
    private static final String TAG = DressServeImpl.class.getSimpleName();

    private RxDao<DbHead> headRxDao;
    private Context mContext;
    private DressProxy mProxy;
    private RxDao<DbNickname> nicknameRxDao;

    public interface ICallback<T> {
        void callback(T value);
    }

    @Override
    public void bindService() {
    }

    public DressServeImpl(Context context) {
        LogUtil.d(TAG, "new DressServeImpl");
        this.mContext = context;
        this.nicknameRxDao = new RxDao<>(this.mContext, DbNickname.class, Constants.DATABASE_NAME);
        this.headRxDao = new RxDao<>(this.mContext, DbHead.class, Constants.DATABASE_NAME);
    }

    @Override
    public void releaseDeathRecipient(IBinder.DeathRecipient deathRecipient) {
        LogUtil.i(TAG, "releaseDeathRecipient = " + deathRecipient);
        DressProxy proxy = this.mProxy;
        if (deathRecipient == null || proxy == null) {
            return;
        }
        try {
            proxy.getBinder().asBinder().unlinkToDeath(deathRecipient, 0);
        } catch (Exception e) {
            LogUtil.e(TAG, "unlinkToDeath error = " + e);
            e.printStackTrace();
        }
    }

    @Override
    public DressProxy getProxy() {
        return this.mProxy;
    }

    @Override
    public void setProxy(DressProxy proxy) {
        this.mProxy = proxy;
    }

    @Override
    public void getNicknameIdByWatchId(List<String> watchIds, ICallback<HashMap> callback) {
        if (this.mProxy == null) {
            LogUtil.d(TAG, "mProxy == null");
            bindService();
            return;
        }
        LogUtil.d(TAG, "getDressIdByWatchId" + watchIds);
        this.mProxy.getDressIdByWatchId(watchIds, 1, new NicknameIdCallbackStub(callback));
    }

    @Override
    public List<DbHead> getHeadById(List<String> headIds, ICallback<Boolean> callback) {
        ArrayList<DbHead> result = new ArrayList<>();
        ArrayList<String> needRemoteHeads = new ArrayList<>();
        if (CollectionUtil.isEmpty(headIds)) {
            return result;
        }
        for (int i = 0; i < headIds.size(); i++) {
            String headId = headIds.get(i);
            if (headId != null && !TextUtils.isEmpty(headId.trim())) {
                DbHead head = this.headRxDao.queryForFirst(HEAD_ID, headId);
                if (head == null || !DressUtil.isSourceExist(head.getSourcePath(), 1)) {
                    if (!needRemoteHeads.contains(headId)) {
                        needRemoteHeads.add(headId);
                    }
                } else {
                    result.add(head);
                }
            }
        }
        LogUtil.i(TAG, "needRemoteHeads: " + needRemoteHeads);
        if (!needRemoteHeads.isEmpty()) {
            if (this.mProxy == null) {
                bindService();
            } else {
                getNextHead(needRemoteHeads, callback);
            }
        }
        LogUtil.i(TAG, "getHeadById: " + result);
        return result;
    }

    private void getNextHead(List<String> headIds, ICallback<Boolean> callback) {
        if (CollectionUtil.isEmpty(headIds)) {
            return;
        }
        HeadCallbackStub stub = new HeadCallbackStub(headIds, this, callback);
        DressProxy proxy = this.mProxy;
        if (proxy != null) {
            proxy.getRemoteDress(headIds.get(0), 2, stub);
        }
    }

    @Override
    public boolean addOrUpdateHead(DbHead head) {
        DbHead local = this.headRxDao.queryForFirst(HEAD_ID, head.getHeadId());
        if (local != null) {
            head.setId(local.getId());
            return local.equals(head) || updateHead(head);
        }
        return this.headRxDao.insert(head);
    }

    @Override
    public void getHeadIdByWatchId(List<String> watchIds, ICallback<HashMap> callback) {
        if (this.mProxy == null) {
            LogUtil.d(TAG, "mProxy == null");
            bindService();
            return;
        }
        this.mProxy.getDressIdByWatchId(watchIds, 2, new HeadIdCallbackStub(callback));
    }

    private boolean updateHead(DbHead head) {
        try {
            return this.headRxDao.getDao().update(head) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            LogUtil.e(TAG, "updateHead error: ", e);
            return false;
        }
    }

    @Override
    public DbNickname getNicknameById(String nicknameId, ICallback<Boolean> callback) {
        if (nicknameId == null || nicknameId.trim().isEmpty()) {
            return null;
        }
        DbNickname local = this.nicknameRxDao.queryForFirst(NICKNAME_ID, nicknameId);
        if (local == null || !DressUtil.isSourceExist(local.getSourcePath(), 1)) {
            if (this.mProxy == null) {
                LogUtil.d(TAG, "mProxy == null");
                bindService();
            } else {
                LogUtil.d(TAG, "getRemoteDress nicknameId :" + nicknameId);
                this.mProxy.getRemoteDress(nicknameId, 1, new NicknameCallbackStubById(this, callback));
            }
        }
        return local;
    }

    @Override
    public List<DbNickname> getNicknameById(List<String> nicknameIds, ICallback<Boolean> callback) {
        ArrayList<DbNickname> result = new ArrayList<>();
        ArrayList<String> needRemoteNicknames = new ArrayList<>();
        for (int i = 0; i < nicknameIds.size(); i++) {
            String nicknameId = nicknameIds.get(i);
            if (nicknameId != null && !nicknameId.trim().isEmpty()) {
                DbNickname local = this.nicknameRxDao.queryForFirst(NICKNAME_ID, nicknameId);
                if (local == null || !DressUtil.isSourceExist(local.getSourcePath(), 1)) {
                    if (!needRemoteNicknames.contains(nicknameId)) {
                        needRemoteNicknames.add(nicknameId);
                    }
                } else {
                    result.add(local);
                }
            }
        }
        LogUtil.i(TAG, "needRemoteNicknames: " + needRemoteNicknames);
        if (!needRemoteNicknames.isEmpty()) {
            if (this.mProxy == null) {
                bindService();
            } else {
                getNextNickname(needRemoteNicknames, callback);
            }
        }
        LogUtil.i(TAG, "getNicknameById: " + result);
        return result;
    }

    private void getNextNickname(List<String> nicknameIds, ICallback<Boolean> callback) {
        if (nicknameIds == null || nicknameIds.size() == 0 || this.mProxy == null) {
            return;
        }
        this.mProxy.getRemoteDress(nicknameIds.get(0), 1, new NextNickNameCallbackStub(nicknameIds, this, callback));
    }

    @Override
    public boolean addOrUpdateNickname(DbNickname nickname) {
        DbNickname local = this.nicknameRxDao.queryForFirst(NICKNAME_ID, nickname.getNicknameId());
        if (local != null) {
            nickname.setId(local.getId());
            return local.equals(nickname) || updateNickname(nickname);
        }
        return this.nicknameRxDao.insert(nickname);
    }

    @Override
    public void notifyDressExpired(String dressId, int dressType) {
        DressProxy proxy = this.mProxy;
        if (proxy == null) {
            bindService();
        } else {
            proxy.notifyDressExpired(dressId, dressType);
        }
    }

    private boolean updateNickname(DbNickname nickname) {
        try {
            return this.nicknameRxDao.getDao().update(nickname) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            LogUtil.e(TAG, "updateNickname error: " + e.getMessage());
            return false;
        }
    }

    private DbNickname convertToDbNickname(RemoteNickname remoteNickname) {
        if (remoteNickname == null) {
            return null;
        }
        DbNickname nickname = new DbNickname();
        nickname.setNicknameId(remoteNickname.getNicknameId());
        nickname.setSourcePath(remoteNickname.getSourcePath());
        return nickname;
    }

    private DbHead convertToDbHead(RemoteHead remoteHead) {
        if (remoteHead == null) {
            return null;
        }
        DbHead head = new DbHead();
        head.setHeadId(remoteHead.getHeadId());
        head.setSourcePath(remoteHead.getSourcePath());
        head.setMovementType(remoteHead.getMovementType());
        head.setCarouseNum(remoteHead.getCarouseNum());
        return head;
    }

    static class NicknameIdCallbackStub extends IDressIdCallback.Stub {
        WeakReference<ICallback<HashMap>> callbackWeakReference;

        NicknameIdCallbackStub(ICallback<HashMap> callback) {
            this.callbackWeakReference = new WeakReference<>(callback);
        }

        @Override
        public void onSuccess(Map map) {
            ICallback<HashMap> callback = this.callbackWeakReference.get();
            if (callback != null) {
                callback.callback((HashMap) map);
            }
        }

        @Override
        public void onError(String message) {
            LogUtil.e(DressServeImpl.TAG, "getNicknameIdByWatchId: " + message);
        }
    }

    static class NicknameCallbackStubById extends IDressCallback.Stub {
        WeakReference<ICallback<Boolean>> callbackWeakReference;
        WeakReference<DressServeImpl> dressServeWeakReference;

        NicknameCallbackStubById(DressServeImpl dressServe, ICallback<Boolean> callback) {
            this.dressServeWeakReference = new WeakReference<>(dressServe);
            this.callbackWeakReference = new WeakReference<>(callback);
        }

        @Override
        public void onSuccess(RemoteDress remoteDress) {
            DressServeImpl dressServe = this.dressServeWeakReference.get();
            ICallback<Boolean> callback = this.callbackWeakReference.get();
            if (dressServe == null) {
                return;
            }
            LogUtil.d(DressServeImpl.TAG, "dressCallback onSuccess :" + remoteDress);
            if (remoteDress == null) {
                LogUtil.i(DressServeImpl.TAG, "getNicknameById is null");
                if (callback != null) {
                    callback.callback(false);
                }
                return;
            }
            dressServe.addOrUpdateNickname(dressServe.convertToDbNickname((RemoteNickname) remoteDress));
            if (callback != null) {
                callback.callback(true);
            }
            LogUtil.i(DressServeImpl.TAG, "getNicknameById: " + remoteDress);
        }

        @Override
        public void onError(String message) {
            LogUtil.e(DressServeImpl.TAG, "getRemoteNickname error: " + message);
            if (message.contains("1003") || message.contains("1005")) {
                LogUtil.i(DressServeImpl.TAG, "exception request " + message);
                return;
            }
            ICallback<Boolean> callback = this.callbackWeakReference.get();
            if (callback != null) {
                callback.callback(false);
            }
        }
    }

    static class NextNickNameCallbackStub extends IDressCallback.Stub {
        WeakReference<ICallback<Boolean>> callbackWeakReference;
        WeakReference<DressServeImpl> dressServeWeakReference;
        List<String> needRemoteNicknames;

        NextNickNameCallbackStub(List<String> nicknameIds, DressServeImpl dressServe, ICallback<Boolean> callback) {
            this.needRemoteNicknames = nicknameIds;
            this.callbackWeakReference = new WeakReference<>(callback);
            this.dressServeWeakReference = new WeakReference<>(dressServe);
        }

        @Override
        public void onSuccess(RemoteDress remoteDress) {
            DressServeImpl dressServe = this.dressServeWeakReference.get();
            ICallback<Boolean> callback = this.callbackWeakReference.get();
            if (dressServe == null) {
                return;
            }
            LogUtil.d(DressServeImpl.TAG, "dressCallback onSuccess : " + remoteDress);
            if (remoteDress != null) {
                dressServe.addOrUpdateNickname(dressServe.convertToDbNickname((RemoteNickname) remoteDress));
            }
            this.needRemoteNicknames.remove(0);
            if (this.needRemoteNicknames.size() != 0) {
                dressServe.getNextNickname(this.needRemoteNicknames, callback);
            } else if (callback != null) {
                callback.callback(true);
            }
        }

        @Override
        public void onError(String message) {
            if (message.contains("1003") || message.contains("1005")) {
                LogUtil.i(DressServeImpl.TAG, "exception request " + message);
                return;
            }
            DressServeImpl dressServe = this.dressServeWeakReference.get();
            ICallback<Boolean> callback = this.callbackWeakReference.get();
            if (dressServe == null) {
                return;
            }
            LogUtil.e(DressServeImpl.TAG, "getRemoteNickname error: " + message);
            this.needRemoteNicknames.remove(0);
            if (this.needRemoteNicknames.size() != 0) {
                dressServe.getNextNickname(this.needRemoteNicknames, callback);
            } else if (callback != null) {
                callback.callback(true);
            }
        }
    }

    static class HeadCallbackStub extends IDressCallback.Stub {
        WeakReference<ICallback<Boolean>> callbackWeakReference;
        WeakReference<DressServeImpl> dressServeWeakReference;
        List<String> needRemoteHeads;

        HeadCallbackStub(List<String> headIds, DressServeImpl dressServe, ICallback<Boolean> callback) {
            this.needRemoteHeads = headIds;
            this.callbackWeakReference = new WeakReference<>(callback);
            this.dressServeWeakReference = new WeakReference<>(dressServe);
        }

        @Override
        public void onSuccess(RemoteDress remoteDress) {
            DressServeImpl dressServe = this.dressServeWeakReference.get();
            ICallback<Boolean> callback = this.callbackWeakReference.get();
            if (dressServe == null) {
                return;
            }
            LogUtil.d(DressServeImpl.TAG, "getRemoteNickname: " + remoteDress);
            if (remoteDress != null) {
                dressServe.addOrUpdateHead(dressServe.convertToDbHead((RemoteHead) remoteDress));
            }
            this.needRemoteHeads.remove(0);
            if (this.needRemoteHeads.size() != 0) {
                dressServe.getNextHead(this.needRemoteHeads, callback);
            } else if (callback != null) {
                callback.callback(true);
            }
        }

        @Override
        public void onError(String message) {
            if (message.contains("1003") || message.contains("1005")) {
                LogUtil.i(DressServeImpl.TAG, "exception request " + message);
                return;
            }
            DressServeImpl dressServe = this.dressServeWeakReference.get();
            ICallback<Boolean> callback = this.callbackWeakReference.get();
            LogUtil.e(DressServeImpl.TAG, "getRemoteNickname error: " + message);
            this.needRemoteHeads.remove(0);
            if (this.needRemoteHeads.size() == 0) {
                if (callback != null) {
                    callback.callback(true);
                }
            } else if (dressServe != null) {
                dressServe.getNextHead(this.needRemoteHeads, callback);
            }
        }
    }

    static class HeadIdCallbackStub extends IDressIdCallback.Stub {
        WeakReference<ICallback<HashMap>> callbackWeakReference;

        HeadIdCallbackStub(ICallback<HashMap> callback) {
            this.callbackWeakReference = new WeakReference<>(callback);
        }

        @Override
        public void onSuccess(Map map) {
            ICallback<HashMap> callback = this.callbackWeakReference.get();
            if (callback == null) {
                LogUtil.e(DressServeImpl.TAG, "getHeadIdByWatchId but HeadIdCallback is null");
            } else {
                callback.callback((HashMap) map);
            }
        }

        @Override
        public void onError(String message) {
            LogUtil.e(DressServeImpl.TAG, "getHeadIdByWatchId: " + message);
        }
    }
}