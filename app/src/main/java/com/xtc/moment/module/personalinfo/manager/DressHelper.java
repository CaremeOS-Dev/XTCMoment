package com.xtc.moment.module.personalinfo.manager;

import android.content.Context;

import com.xtc.funlist.util.FuncUtil;
import com.xtc.log.LogUtil;
import com.xtc.moment.constants.ModuleSwitchConstant;
import com.xtc.moment.db.bean.DbHead;
import com.xtc.moment.db.bean.DbNickname;
import com.xtc.moment.serve.impl.DressServeImpl;
import com.xtc.moment.serve.interfaces.IDressServe;
import com.xtc.moment.util.DressUtil;
import com.xtc.moment.util.HandlerUtil;
import com.xtc.moment.util.switchs.ModuleSwitchUtil;
import com.xtc.utils.common.CollectionUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import rx.Single;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

public class DressHelper {

    private static final String TAG = "DressHelper";
    private static final String DEAFLUT_ID = "0";

    private Context mContext;
    private DressManage mDressManage;
    private OnLoadDataListener mOnLoadDataListener;
    private List<String> mWatchIdList;
    private int retryLoadNameCount = 0;

    private DressServeImpl.ICallback<HashMap> loadNickNameCallBack = new DressServeImpl.ICallback<HashMap>() {
        @Override
        public void callback(HashMap map) {
            LogUtil.i(TAG, "loadNicknameFromFriendList: " + map);
            if (map == null || map.size() <= 0) {
                return;
            }
            getRemoteNicknames(map);
        }
    };

    private DressServeImpl.ICallback<Boolean> remoteNickNameCallBack = new DressServeImpl.ICallback<Boolean>() {
        @Override
        public void callback(Boolean result) {
            LogUtil.i(TAG, "getNickNameById callback result = " + result);
            if (retryLoadNameCount <= 3) {
                retryLoadNameCount++;
                loadNicknameFromFriendList();
            }
        }
    };

    public interface OnLoadDataListener {
        void loadHeadDressSuccess();

        void loadNickNameSuccess();
    }

    public DressHelper(Context context, DressManage dressManage) {
        mContext = context.getApplicationContext();
        mDressManage = dressManage;
    }

    public void loadHeadAndNickNameFromDress(List<String> watchIdList, OnLoadDataListener listener) {
        mOnLoadDataListener = listener;
        if (!FuncUtil.supportPersonalityDress() || mDressManage == null || mContext == null) {
            return;
        }
        if (CollectionUtil.isEmpty(watchIdList)) {
            LogUtil.d(TAG, "watchIdList is empty");
            return;
        }
        if (!mDressManage.isBindDressResult()) {
            LogUtil.d(TAG, "isBindDressResult false");
            return;
        }
        mWatchIdList = watchIdList;
        IDressServe dressServe = mDressManage.getDressServe();
        if (dressServe == null) {
            LogUtil.d(TAG, "dressServe is empty");
            return;
        }
        LogUtil.d(TAG, "loadHeadAndNickNameFromDress() called with: watchIdList = [" + watchIdList + "]");
        dressServe.getHeadIdByWatchId(mWatchIdList, new DressServeImpl.ICallback<HashMap>() {
            @Override
            public void callback(HashMap map) {
                LogUtil.i(TAG, "loadHeadFromFriendList: " + map + "   thread:" + Thread.currentThread());
                getRemoteHeads(map);
            }
        });
        loadNicknameFromFriendList();
    }

    private void getRemoteHeads(HashMap<String, String> headIdMap) {
        boolean isDisplayDressHead = ModuleSwitchUtil.queryModuleSwitchByBoolean(mContext, ModuleSwitchConstant.MODULE_SWITCH_DRESS_HEAD, false);
        LogUtil.i(TAG, "isDisplayDressHead: " + isDisplayDressHead);
        if (!FuncUtil.supportPersonalityDress() || !isDisplayDressHead || mDressManage == null
                || headIdMap == null || headIdMap.size() == 0) {
            return;
        }
        IDressServe dressServe = mDressManage.getDressServe();
        if (dressServe == null) {
            return;
        }
        ArrayList<String> headIds = new ArrayList<>();
        Iterator<Map.Entry<String, String>> iterator = headIdMap.entrySet().iterator();
        while (iterator.hasNext()) {
            String headId = iterator.next().getValue();
            if (!headIds.contains(headId) && !DEAFLUT_ID.equals(headId)) {
                headIds.add(headId);
            }
        }
        List<DbHead> heads = dressServe.getHeadById(headIds, new DressServeImpl.ICallback<Boolean>() {
            @Override
            public void callback(Boolean result) {
            }
        });
        LogUtil.i(TAG, "mDressServe: " + heads);
        HashMap<String, DbHead> headMap = new HashMap<>();
        if (heads != null) {
            for (Map.Entry<String, String> entry : headIdMap.entrySet()) {
                String headId = entry.getValue();
                for (int i = 0; i < heads.size(); i++) {
                    if (heads.get(i).getHeadId().equals(headId)) {
                        headMap.put(entry.getKey(), heads.get(i));
                        break;
                    }
                }
            }
        }
        LogUtil.i(TAG, "getRemoteHeads: " + headMap);
        DressUtil.updateDbHead(mContext, headMap);
        Single.fromCallable(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                if (mOnLoadDataListener == null) {
                    return null;
                }
                mOnLoadDataListener.loadHeadDressSuccess();
                return null;
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Void>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onNext(Void value) {
            }

            @Override
            public void onError(Throwable throwable) {
                LogUtil.i(TAG, "setMyHeadDress onError:", throwable);
            }
        });
    }

    private void loadNicknameFromFriendList() {
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                if (FuncUtil.supportPersonalityDress() && mDressManage != null) {
                    IDressServe dressServe = mDressManage.getDressServe();
                    if (dressServe != null) {
                        dressServe.getNicknameIdByWatchId(mWatchIdList, loadNickNameCallBack);
                    }
                }
            }
        });
    }

    private void getRemoteNicknames(HashMap<String, String> nicknameIdMap) {
        ArrayList<String> nicknameIds = new ArrayList<>();
        Iterator<Map.Entry<String, String>> iterator = nicknameIdMap.entrySet().iterator();
        while (iterator.hasNext()) {
            String nicknameId = iterator.next().getValue();
            if (!nicknameIds.contains(nicknameId) && !DEAFLUT_ID.equals(nicknameId)) {
                nicknameIds.add(nicknameId);
            }
        }
        LogUtil.i(TAG, "getRemoteNicknames: " + nicknameIds);
        List<DbNickname> nicknames = mDressManage.getDressServe().getNicknameById(nicknameIds, remoteNickNameCallBack);
        final HashMap<String, DbNickname> nicknameMap = new HashMap<>();
        if (nicknames != null) {
            for (Map.Entry<String, String> entry : nicknameIdMap.entrySet()) {
                String nicknameId = entry.getValue();
                for (int i = 0; i < nicknames.size(); i++) {
                    if (nicknames.get(i).getNicknameId().equals(nicknameId)) {
                        nicknameMap.put(entry.getKey(), nicknames.get(i));
                        break;
                    }
                }
            }
        }
        HandlerUtil.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                DressUtil.updateDbNickName(nicknameMap);
                if (mOnLoadDataListener != null) {
                    mOnLoadDataListener.loadNickNameSuccess();
                }
            }
        });
    }
}