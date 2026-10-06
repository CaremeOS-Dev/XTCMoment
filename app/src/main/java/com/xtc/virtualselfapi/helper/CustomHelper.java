package com.xtc.virtualselfapi.helper;

import com.xtc.database.ormlite.CollectionUtil;
import com.xtc.log.LogUtil;
import com.xtc.virtualselfapi.bean.db.DbCostume;
import com.xtc.virtualselfapi.bean.db.DbDecorate;
import com.xtc.virtualselfapi.bean.net.req.RespCardInfo;
import com.xtc.virtualselfapi.manager.HttpManager;
import com.xtc.virtualselfapi.manager.VirtualSelfDBServeImpl;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 自定义装扮数据同步工具。
 */
public class CustomHelper {

    private static final String TAG = "Virtual_Self_Api_CustomHelper";

    private final HttpManager httpManager;

    public CustomHelper(HttpManager httpManager) {
        this.httpManager = httpManager;
    }

    public void updateCustom() {
        getCustomDecorateList();
        getCardInfo();
    }

    private void getCustomDecorateList() {
        this.httpManager.getCustomDecorateList()
                .map(new Func1<List<DbDecorate>, Boolean>() {
                    @Override
                    public Boolean call(List<DbDecorate> decorateList) {
                        return VirtualSelfDBServeImpl.getInstance().insertDecorateList(decorateList);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean result) {
                        LogUtil.i(TAG, "getCustomDecorateList result = " + result);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        LogUtil.d(TAG, "getCustomDecorateList error = " + throwable);
                    }
                });
    }

    private void getCardInfo() {
        this.httpManager.getCardInfo()
                .map(new Func1<RespCardInfo, Boolean>() {
                    @Override
                    public Boolean call(RespCardInfo cardInfo) {
                        if (cardInfo != null && !CollectionUtil.isEmpty(cardInfo.getCostumes())) {
                            return VirtualSelfDBServeImpl.getInstance()
                                    .insertOrUpdateCostumeForBatch(convertDbCostume(cardInfo.getCostumes()));
                        }
                        return false;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean result) {
                        LogUtil.i(TAG, "getCardInfo result = " + result);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        LogUtil.d(TAG, "getCardInfo error = " + throwable);
                    }
                });
    }

    private List<DbCostume> convertDbCostume(List<DbCostume> costumes) {
        for (DbCostume costume : costumes) {
            costume.setCostumeId(costume.getId());
            costume.setCostumeType(1);
            LogUtil.i(TAG, "完善集卡对应的装扮信息 cardCostume:" + costume);
        }
        return costumes;
    }
}