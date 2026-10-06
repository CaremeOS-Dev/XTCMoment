package com.xtc.moment.share.presenter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.xtc.log.LogUtil;
import com.xtc.moment.event.ShareEventEntity;
import com.xtc.moment.module.main.MomentActivity;
import com.xtc.moment.share.callback.ICheckBundleCallback;
import com.xtc.moment.share.model.DbAppShareImpl;
import com.xtc.moment.share.model.NetManager;
import com.xtc.moment.share.model.bean.DbAppShare;
import com.xtc.moment.share.model.bean.NetAppShare;
import com.xtc.moment.share.other.BundleParseException;
import com.xtc.moment.share.other.ShareUtils;
import com.xtc.moment.third.behavior.MomentBehavior;
import com.xtc.shareapi.share.communication.SendMessageToXTC;
import com.xtc.shareapi.share.constant.OpenApiConstant;
import com.xtc.shareapi.share.interfaces.Scene;
import com.xtc.shareapi.share.manager.ShareSupportManager;
import com.xtc.shareapi.share.shareobject.XTCShareMessage;
import com.xtc.shareapi.share.shareobject.XTCWebObject;
import com.xtc.utils.encode.AESUtil;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 分享参数校验与响应管理：解析 Bundle、校验应用权限并回传结果。
 */
class ShareBundleManager {

    private static final String TAG = ShareUtils.LOG + ShareBundleManager.class.getSimpleName();
    /** 分享支持校验通过。 */
    private static final int RESPONSE_CODE_SUCCESS = 1;
    private static final int RESPONSE_CODE_CANCEL = 2;
    private static final int RESPONSE_CODE_APP_ERROR = 3;
    private static final int RESPONSE_CODE_NOT_SUPPORT = 5;
    private static final int RESPONSE_CODE_PARSE_ERROR = 6;
    private static final int RESPONSE_CODE_SCENE_FORBID = 10;
    private static final int RESPONSE_CODE_NO_FRIEND = 11;
    private static final int RESPONSE_CODE_OTHER = 100;
    private static final int JUMP_FLAG_MOMENT = 1;

    private String appKey;
    private final ICheckBundleCallback checkBundleCallback;
    private String className;
    private String conversionId;
    private final DbAppShareImpl dbAppShareImpl;
    byte[] icon;
    private int jumpFlag;
    private DbAppShare localApp;
    private final Context mContext;
    String name;
    private final NetManager netManager;
    String packageName;
    Scene scene;
    private String token;
    String transaction;
    XTCShareMessage xtcShareMessage;

    ShareBundleManager(Context context, ICheckBundleCallback checkBundleCallback) {
        this.dbAppShareImpl = new DbAppShareImpl(context);
        this.netManager = new NetManager(context);
        this.checkBundleCallback = checkBundleCallback;
        this.mContext = context;
    }

    private boolean checkShareType(int type) {
        return type == 1 || type == 2 || type == 3 || type == 6 || type == 7 || type == 4;
    }

    private boolean checkSum() {
        return true;
    }

    /** 校验分享参数并回传结果。 */
    void checkBundle(Bundle bundle) {
        if (bundle != null && bundle.getClassLoader() == null) {
            bundle.setClassLoader(getClass().getClassLoader());
        }
        Observable.just(bundle)
                .map(new Func1<Bundle, Boolean>() {
                    @Override
                    public Boolean call(Bundle targetBundle) {
                        return Boolean.valueOf(parseExtra(targetBundle));
                    }
                })
                .map(new Func1<Boolean, DbAppShare>() {
                    @Override
                    public DbAppShare call(Boolean valid) {
                        return queryAppFromDb(valid.booleanValue());
                    }
                })
                .flatMap(new Func1<DbAppShare, Observable<?>>() {
                    @Override
                    public Observable<?> call(DbAppShare dbAppShare) {
                        return queryAppFromNet(dbAppShare);
                    }
                })
                .map(new Func1<Object, DbAppShare>() {
                    @Override
                    public DbAppShare call(Object result) {
                        return updateAppToDatabase(result);
                    }
                })
                .map(new Func1<DbAppShare, SendMessageToXTC.Response>() {
                    @Override
                    public SendMessageToXTC.Response call(DbAppShare dbAppShare) {
                        return checkArgAndCompatible(dbAppShare);
                    }
                })
                .doOnNext(new Action1<SendMessageToXTC.Response>() {
                    @Override
                    public void call(SendMessageToXTC.Response response) {
                        dbAppShareImpl.updateToken(packageName, ShareUtils.createToken());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<SendMessageToXTC.Response>() {
                    @Override
                    public void call(SendMessageToXTC.Response response) {
                        checkBundleFinish(response);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        checkBundleError(throwable);
                    }
                });
    }

    /** 仅解析包名与类名，用于拒绝分享时的回传。 */
    public void onlyParseClassName(Bundle bundle) {
        LogUtil.d(TAG, "onlyParseClassName");
        if (bundle == null) {
            return;
        }
        if (bundle.getClassLoader() == null) {
            bundle.setClassLoader(getClass().getClassLoader());
        }
        this.className = bundle.getString(OpenApiConstant.IntentConstant.INTENT_CLASSNAME);
        this.packageName = bundle.getString(OpenApiConstant.IntentConstant.INTENT_PACKAGE);
        this.jumpFlag = bundle.getInt(OpenApiConstant.IntentConstant.INTENT_APP_JUMP_FLAG);
    }

    void sendNotSupportResponse() {
        SendMessageToXTC.Response response = new SendMessageToXTC.Response();
        response.setCode(RESPONSE_CODE_NOT_SUPPORT);
        response.setErrorDesc("non support type !");
        sendResponse(response);
    }

    void sendSuccessResponse() {
        SendMessageToXTC.Response response = new SendMessageToXTC.Response();
        response.setCode(RESPONSE_CODE_SUCCESS);
        response.setErrorDesc("send share success !");
        sendResponse(response);
    }

    void sendFailResponse() {
        SendMessageToXTC.Response response = new SendMessageToXTC.Response();
        response.setCode(RESPONSE_CODE_OTHER);
        response.setErrorDesc("other error happen : send fail! ");
        sendResponse(response);
    }

    void sendCancelResponse() {
        SendMessageToXTC.Response response = new SendMessageToXTC.Response();
        response.setCode(RESPONSE_CODE_CANCEL);
        response.setErrorDesc("cancel share !");
        sendResponse(response);
    }

    void sendSceneForbidResponse() {
        LogUtil.d(TAG, "this scene have bean forbid!");
        SendMessageToXTC.Response response = new SendMessageToXTC.Response();
        response.setCode(RESPONSE_CODE_SCENE_FORBID);
        response.setErrorDesc("this scene have bean forbid!");
        sendResponse(response);
    }

    void sendNoFriendResponse() {
        LogUtil.d(TAG, "no share friend!");
        SendMessageToXTC.Response response = new SendMessageToXTC.Response();
        response.setCode(RESPONSE_CODE_NO_FRIEND);
        response.setErrorDesc("other error happen : no share friend");
        sendResponse(response);
    }

    void sendOtherResponse(Throwable throwable) {
        throwable.printStackTrace();
        LogUtil.d(TAG, "other error happen : " + throwable.toString());
        SendMessageToXTC.Response response = new SendMessageToXTC.Response();
        response.setCode(RESPONSE_CODE_OTHER);
        response.setErrorDesc("other error happen : " + throwable.toString());
        sendResponse(response);
    }

    void setConversionId(long conversionId) {
        if (conversionId == -1) {
            this.conversionId = String.valueOf(conversionId);
        } else {
            this.conversionId = AESUtil.encryptToBase64(String.valueOf(conversionId), ShareUtils.PASSWORD);
        }
        LogUtil.d(TAG, "conversion id = " + conversionId + " aes result = " + this.conversionId);
    }

    private boolean parseExtra(Bundle bundle) {
        LogUtil.d(TAG, "start parse extra!");
        if (bundle == null) {
            Observable.just(new BundleParseException("argument exception: bundle is null !"));
            return false;
        }
        this.appKey = bundle.getString(OpenApiConstant.IntentConstant.INTENT_APP_KEY);
        this.token = bundle.getString(OpenApiConstant.IntentConstant.INTENT_APP_TOKEN);
        this.icon = bundle.getByteArray(OpenApiConstant.IntentConstant.INTENT_APP_ICON);
        this.name = bundle.getString(OpenApiConstant.IntentConstant.INTENT_APP_NAME);
        this.className = bundle.getString(OpenApiConstant.IntentConstant.INTENT_CLASSNAME);
        this.packageName = bundle.getString(OpenApiConstant.IntentConstant.INTENT_PACKAGE);
        this.jumpFlag = bundle.getInt(OpenApiConstant.IntentConstant.INTENT_APP_JUMP_FLAG);
        SendMessageToXTC.Request request = new SendMessageToXTC.Request().fromBundle(bundle);
        this.transaction = request.getTransaction();
        this.xtcShareMessage = request.getMessage();
        this.scene = request.getScene();
        LogUtil.d(TAG, "share transaction = " + this.transaction);
        if (TextUtils.isEmpty(this.transaction)) {
            LogUtil.e(TAG, "transaction is null!");
            Observable.just(new BundleParseException("argument exception: transaction is empty !"));
            return false;
        }
        if (TextUtils.isEmpty(this.appKey)) {
            LogUtil.e(TAG, "app key is null!");
            Observable.just(new BundleParseException("argument exception: localApp key is empty !"));
            return false;
        }
        if (TextUtils.isEmpty(this.className)) {
            LogUtil.e(TAG, "class name is null!");
            Observable.just(new BundleParseException("argument exception: class name is empty!"));
            return false;
        }
        if (TextUtils.isEmpty(this.packageName)) {
            LogUtil.e(TAG, "package name is null!");
            Observable.just(new BundleParseException("argument exception: package name is empty!"));
            return false;
        }
        if (TextUtils.isEmpty(this.name)) {
            LogUtil.e(TAG, "app name is null!");
            Observable.just(new BundleParseException("argument exception: name is empty!"));
            return false;
        }
        byte[] iconData = this.icon;
        if (iconData == null || iconData.length == 0) {
            LogUtil.e(TAG, "icon is null!");
            Observable.just(new BundleParseException("argument exception: icon is null!"));
            return false;
        }
        if (this.xtcShareMessage == null) {
            LogUtil.e(TAG, "xtc share message is null!");
            Observable.just(new BundleParseException("argument exception：share message is null !"));
            return false;
        }
        if (this.scene != null) {
            return true;
        }
        LogUtil.e(TAG, "xtc scene is null!");
        Observable.just(new BundleParseException("argument exception: scene is null !"));
        return false;
    }

    private DbAppShare queryAppFromDb(boolean valid) {
        LogUtil.d(TAG, "start query app from database!");
        if (!valid) {
            return null;
        }
        this.localApp = this.dbAppShareImpl.queryApp(this.packageName);
        return this.localApp;
    }

    private Observable<?> queryAppFromNet(DbAppShare dbAppShare) {
        LogUtil.d(TAG, "start query app from net!");
        if (dbAppShare == null || System.currentTimeMillis() > dbAppShare.getDeadline()) {
            return this.netManager.queryAppInfo(this.packageName);
        }
        return Observable.just(dbAppShare);
    }

    private DbAppShare updateAppToDatabase(Object result) {
        if (result == null) {
            return null;
        }
        if (result instanceof DbAppShare) {
            return (DbAppShare) result;
        }
        if (result instanceof NetAppShare) {
            NetAppShare netAppShare = (NetAppShare) result;
            DbAppShare dbAppShare = new DbAppShare();
            dbAppShare.setPackageName(netAppShare.getPackageName());
            dbAppShare.setAppKey(netAppShare.getAppKey());
            dbAppShare.setMaxTimes(netAppShare.getMaxTimes());
            dbAppShare.setDeadline(netAppShare.getDeadline());
            dbAppShare.setAllow(netAppShare.getAllow());
            DbAppShare existing = this.localApp;
            if (existing != null) {
                dbAppShare.setMaxTimes(existing.getTimes());
                dbAppShare.setToken(this.localApp.getToken());
                this.dbAppShareImpl.updateApp(dbAppShare);
            } else {
                this.dbAppShareImpl.insertApp(dbAppShare);
            }
            return dbAppShare;
        }
        return null;
    }

    private SendMessageToXTC.Response checkArgAndCompatible(DbAppShare dbAppShare) {
        SendMessageToXTC.Response response = new SendMessageToXTC.Response();
        if (dbAppShare == null) {
            response.setCode(RESPONSE_CODE_APP_ERROR);
            response.setErrorDesc("query remote localApp error !");
            LogUtil.e(TAG, "query remote localApp is error !");
            return response;
        }
        int supportResult = ShareSupportManager.getInstance(this.mContext).isSupportShareFunction(this.packageName);
        MomentBehavior.shareSupportResult(this.mContext, this.packageName, this.xtcShareMessage.getType(),
                supportResult, 6 == this.xtcShareMessage.getType()
                        ? ((XTCWebObject) this.xtcShareMessage.getShareObject()).getUrl() : null);
        if (supportResult == 2 || supportResult == 3) {
            response.setCode(RESPONSE_CODE_APP_ERROR);
            response.setErrorDesc("the current app does not support sharing");
            LogUtil.e(TAG, "the current app does not support sharing");
            return response;
        }
        if (dbAppShare.getTimes() > dbAppShare.getMaxTimes()) {
            response.setCode(RESPONSE_CODE_APP_ERROR);
            response.setErrorDesc("today share times exceed max times!");
            LogUtil.e(TAG, "today share times exceed max times!");
            return response;
        }
        if (!checkShareType(this.xtcShareMessage.getType())) {
            response.setCode(RESPONSE_CODE_NOT_SUPPORT);
            response.setErrorDesc("share type not support!");
            LogUtil.e(TAG, "share type not support!");
            return response;
        }
        if (!checkSum()) {
            response.setCode(RESPONSE_CODE_APP_ERROR);
            response.setErrorDesc("localApp key not equals!");
            LogUtil.e(TAG, "localApp key not equals!");
            return response;
        }
        response.setCode(RESPONSE_CODE_SUCCESS);
        return response;
    }

    private void checkBundleFinish(SendMessageToXTC.Response response) {
        if (response.getCode() == RESPONSE_CODE_SUCCESS) {
            this.checkBundleCallback.checkSuccess();
        } else {
            sendResponse(response);
        }
    }

    private void checkBundleError(Throwable throwable) {
        throwable.printStackTrace();
        SendMessageToXTC.Response response = new SendMessageToXTC.Response();
        if (throwable instanceof BundleParseException) {
            response.setCode(RESPONSE_CODE_PARSE_ERROR);
            response.setErrorDesc(throwable.toString());
        } else {
            response.setCode(RESPONSE_CODE_OTHER);
            response.setErrorDesc("other error happen : " + throwable.toString());
        }
        LogUtil.d(TAG, response.getErrorDesc());
        sendResponse(response);
    }

    private void sendResponse(SendMessageToXTC.Response response) {
        if (response == null) {
            LogUtil.d(TAG, "response is null! ");
            return;
        }
        if (this.checkBundleCallback == null) {
            LogUtil.d(TAG, "checkBundleCallback is null! ");
            return;
        }
        response.setTransaction(this.transaction);
        response.setConversationId(this.conversionId);
        Bundle bundle = new Bundle();
        response.toBundle(bundle);
        LogUtil.d(TAG, "jump flag =  " + this.jumpFlag);
        if (this.jumpFlag == JUMP_FLAG_MOMENT) {
            if (response.getCode() == RESPONSE_CODE_SUCCESS) {
                Intent intent = new Intent(this.mContext, MomentActivity.class);
                intent.addFlags(131072);
                intent.putExtra("share", "share");
                this.checkBundleCallback.sendResponse(intent);
                return;
            }
            if (TextUtils.isEmpty(this.packageName) || TextUtils.isEmpty(this.className)) {
                LogUtil.d(TAG, "packageName or className is null! ");
                this.checkBundleCallback.sendResponse(null);
                return;
            }
            Intent intent = new Intent();
            intent.setClassName(this.packageName, this.className);
            intent.putExtras(bundle);
            MomentBehavior.shareFail(this.mContext, this.name, ShareEventEntity.FAIL, String.valueOf(0));
            this.checkBundleCallback.sendResponse(intent);
            return;
        }
        if (TextUtils.isEmpty(this.packageName) || TextUtils.isEmpty(this.className)) {
            LogUtil.d(TAG, "packageName or className is null! ");
            this.checkBundleCallback.sendResponse(null);
            return;
        }
        if (response.getCode() != RESPONSE_CODE_SUCCESS) {
            MomentBehavior.shareFail(this.mContext, this.name, ShareEventEntity.FAIL, String.valueOf(0));
        }
        Intent intent = new Intent();
        intent.setClassName(this.packageName, this.className);
        bundle.putString(OpenApiConstant.ResponseConstant.BUNDLE_SCENE_FROM_TYPE,
                OpenApiConstant.ResponseConstant.BUNDLE_SCENE_FROM_MOMENT);
        intent.putExtras(bundle);
        this.checkBundleCallback.sendResponse(intent);
    }
}