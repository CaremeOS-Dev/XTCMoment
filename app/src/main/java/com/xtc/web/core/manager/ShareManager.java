package com.xtc.web.core.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Base64;

import com.xtc.log.LogUtil;
import com.xtc.shareapi.share.bean.JumpToMomentRequest;
import com.xtc.shareapi.share.bean.PoiBean;
import com.xtc.shareapi.share.bean.SerializableMap;
import com.xtc.shareapi.share.communication.BaseResponse;
import com.xtc.shareapi.share.communication.SendMessageToXTC;
import com.xtc.shareapi.share.communication.ShowMessageFromXTC;
import com.xtc.shareapi.share.interfaces.IResponseCallback;
import com.xtc.shareapi.share.jumpmoment.JumpToMomentBase;
import com.xtc.shareapi.share.jumpmoment.JumpToMomentFromAddress;
import com.xtc.shareapi.share.manager.IShareCallback;
import com.xtc.shareapi.share.manager.ShareMessageManager;
import com.xtc.shareapi.share.manager.XTCCallbackImpl;
import com.xtc.shareapi.share.shareobject.XTCAppExtendObject;
import com.xtc.shareapi.share.shareobject.XTCImageObject;
import com.xtc.shareapi.share.shareobject.XTCShareMessage;
import com.xtc.shareapi.share.shareobject.XTCTextObject;
import com.xtc.shareapi.share.shareobject.XTCWebObject;
import com.xtc.shareapi.share.sharescene.Chat;
import com.xtc.shareapi.share.sharescene.Moment;
import com.xtc.web.core.CoreConstants;
import com.xtc.web.core.callback.CompletionHandler;
import com.xtc.web.core.callback.LifecycleCallbacks;
import com.xtc.web.core.data.req.ReqShare;
import com.xtc.web.core.data.req.ReqShareAccount;
import com.xtc.web.core.data.req.ReqShareApp;
import com.xtc.web.core.data.req.ReqShareJumpMoment;
import com.xtc.web.core.data.req.ReqShareScene;

import java.util.HashMap;

/** H5 分享管理器：把 H5 的分享参数转换成 shareapi 的请求并等待分享结果。 */
public class ShareManager extends LifecycleCallbacks implements IResponseCallback {

    private static final String TAG = CoreConstants.TAG + ShareManager.class.getSimpleName();
    private static final String DEFAULT_START_ACTIVITY = "com.xtc.shakeandshake.module.ShakeActivity";
    private static final int SHARE_TYPE_TEXT = 1;
    private static final int SHARE_TYPE_IMAGE = 2;
    private static final int SHARE_TYPE_WEB = 3;

    private Activity activity;
    private CompletionHandler<BaseResponse> handler;

    @Override
    public void onReq(ShowMessageFromXTC.Request request) {
    }

    /** 通用分享入口，按 type 分发。 */
    public void share(Context context, ReqShare reqShare, CompletionHandler<BaseResponse> completionHandler) {
        this.activity = (Activity) context;
        this.handler = completionHandler;
        int type = reqShare.getType();
        if (type == SHARE_TYPE_TEXT) {
            shareText(context, reqShare, completionHandler);
        } else if (type == SHARE_TYPE_IMAGE) {
            shareImage(context, reqShare, completionHandler);
        } else if (type == SHARE_TYPE_WEB) {
            shareWeb(context, reqShare, completionHandler);
        }
    }

    /** 分享图片。 */
    public void shareImage(Context context, ReqShare reqShare, CompletionHandler<BaseResponse> completionHandler) {
        LifecycleDispatcher.getInstance().registerCallback(this);
        XTCImageObject imageObject = new XTCImageObject();
        byte[] imageBytes = Base64.decode(reqShare.getImage(), Base64.NO_WRAP);
        imageObject.setBitmap(BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length));
        XTCShareMessage shareMessage = new XTCShareMessage();
        shareMessage.setShareObject(imageObject);
        SendMessageToXTC.Request chatRequest = new SendMessageToXTC.Request();
        SendMessageToXTC.Request momentRequest = new SendMessageToXTC.Request();
        chatRequest.setMessage(shareMessage);
        momentRequest.setMessage(shareMessage);
        setScene(reqShare.getScene(), chatRequest, momentRequest);
        send(context, reqShare.getScene().getType(), chatRequest, momentRequest, reqShare.getAccount(),
                reqShare.getAppName(), reqShare.getIcon(), reqShare.getKey());
    }

    /** 分享文本。 */
    public void shareText(Context context, ReqShare reqShare, CompletionHandler<BaseResponse> completionHandler) {
        LifecycleDispatcher.getInstance().registerCallback(this);
        XTCTextObject textObject = new XTCTextObject();
        textObject.setText(reqShare.getContent());
        XTCShareMessage shareMessage = new XTCShareMessage();
        shareMessage.setShareObject(textObject);
        SendMessageToXTC.Request chatRequest = new SendMessageToXTC.Request();
        SendMessageToXTC.Request momentRequest = new SendMessageToXTC.Request();
        chatRequest.setMessage(shareMessage);
        momentRequest.setMessage(shareMessage);
        setScene(reqShare.getScene(), chatRequest, momentRequest);
        send(context, reqShare.getScene().getType(), chatRequest, momentRequest, reqShare.getAccount(),
                reqShare.getAppName(), reqShare.getIcon(), reqShare.getKey());
    }

    /** 分享应用（扩展信息）。 */
    public void shareApp(Context context, ReqShareApp reqShareApp, CompletionHandler<BaseResponse> completionHandler) {
        this.activity = (Activity) context;
        this.handler = completionHandler;
        XTCAppExtendObject appExtendObject = new XTCAppExtendObject();
        appExtendObject.setExtInfo(reqShareApp.getExtInfo());
        if (TextUtils.isEmpty(reqShareApp.getStartActivity())) {
            appExtendObject.setStartActivity(DEFAULT_START_ACTIVITY);
        } else {
            appExtendObject.setStartActivity(reqShareApp.getStartActivity());
        }
        XTCShareMessage shareMessage = new XTCShareMessage();
        shareMessage.setShareObject(appExtendObject);
        byte[] imageBytes = Base64.decode(reqShareApp.getImage(), Base64.NO_WRAP);
        shareMessage.setThumbImage(BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length));
        shareMessage.setDescription(reqShareApp.getDesc());
        SendMessageToXTC.Request chatRequest = new SendMessageToXTC.Request();
        SendMessageToXTC.Request momentRequest = new SendMessageToXTC.Request();
        chatRequest.setMessage(shareMessage);
        momentRequest.setMessage(shareMessage);
        setScene(reqShareApp.getScene(), chatRequest, momentRequest);
        send(context, reqShareApp.getScene().getType(), chatRequest, momentRequest, reqShareApp.getAccount(),
                reqShareApp.getAppName(), reqShareApp.getIcon(), reqShareApp.getKey());
    }

    /** 分享网页链接。 */
    public void shareWeb(Context context, ReqShare reqShare, CompletionHandler<BaseResponse> completionHandler) {
        XTCWebObject webObject = new XTCWebObject();
        SerializableMap extMap = new SerializableMap();
        extMap.setMap(new HashMap());
        webObject.setExtMap(extMap);
        webObject.setExtInfo(reqShare.getExtInfo());
        webObject.setUrl(reqShare.getLink());
        webObject.setRtosSupport(reqShare.getRtosSupport());
        XTCShareMessage shareMessage = new XTCShareMessage();
        shareMessage.setShareObject(webObject);
        byte[] imageBytes = Base64.decode(reqShare.getImage(), Base64.NO_WRAP);
        shareMessage.setThumbImage(BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length));
        shareMessage.setDescription(reqShare.getDesc());
        SendMessageToXTC.Request chatRequest = new SendMessageToXTC.Request();
        SendMessageToXTC.Request momentRequest = new SendMessageToXTC.Request();
        setScene(reqShare.getScene(), chatRequest, momentRequest);
        chatRequest.setMessage(shareMessage);
        momentRequest.setMessage(shareMessage);
        send(context, reqShare.getScene().getType(), chatRequest, momentRequest, reqShare.getAccount(),
                reqShare.getAppName(), reqShare.getIcon(), reqShare.getKey());
    }

    /** 分享地理位置并跳转到动态发布页。 */
    public void shareJumpToMoment(Context context, ReqShareJumpMoment reqShareJumpMoment,
            CompletionHandler<BaseResponse> completionHandler) {
        PoiBean poiBean = new PoiBean();
        poiBean.setName(reqShareJumpMoment.getPoiName());
        poiBean.setCity(reqShareJumpMoment.getCity());
        poiBean.setLocation(new PoiBean.Location(reqShareJumpMoment.getLat(), reqShareJumpMoment.getLng()));
        JumpToMomentBase jumpToMomentBase = new JumpToMomentBase();
        jumpToMomentBase.setPoiBean(poiBean);
        JumpToMomentFromAddress jumpToMomentFromAddress = new JumpToMomentFromAddress();
        jumpToMomentFromAddress.setAddressId(reqShareJumpMoment.getAddressId());
        jumpToMomentFromAddress.setBase(jumpToMomentBase);
        JumpToMomentRequest jumpToMomentRequest = new JumpToMomentRequest();
        jumpToMomentRequest.setJumpToMomentObject(jumpToMomentFromAddress);
        new ShareMessageManager(context).shareToMomentPicture(jumpToMomentRequest);
    }

    /** 按场景位掩码设置聊天/动态场景。 */
    private void setScene(ReqShareScene reqShareScene, SendMessageToXTC.Request chatRequest,
            SendMessageToXTC.Request momentRequest) {
        if (reqShareScene.getType() != 0) {
            if ((reqShareScene.getType() & ReqShareScene.SceneType.CHAT) == ReqShareScene.SceneType.CHAT) {
                Chat chat = reqShareScene.getChat();
                if (chat == null) {
                    chat = new Chat();
                }
                chatRequest.setScene(chat);
            } else if ((reqShareScene.getType() & ReqShareScene.SceneType.MOMOENT) == ReqShareScene.SceneType.MOMOENT) {
                Moment moment = reqShareScene.getMoment();
                if (moment == null) {
                    moment = new Moment();
                }
                chatRequest.setScene(moment);
            }
            return;
        }
        Chat chat = reqShareScene.getChat();
        if (chat == null) {
            chat = new Chat();
        }
        chatRequest.setScene(chat);
        Moment moment = reqShareScene.getMoment();
        if (moment == null) {
            moment = new Moment();
        }
        momentRequest.setScene(moment);
    }

    /** 按展示类型（静默/普通/全部）发送分享请求。 */
    private void send(Context context, int sceneType, SendMessageToXTC.Request chatRequest,
            SendMessageToXTC.Request momentRequest, ReqShareAccount reqShareAccount, String appName,
            String appIcon, String appKey) {
        ShareMessageManager shareMessageManager = new ShareMessageManager(context);
        setAppInfo(appName, appIcon, shareMessageManager);
        if ((sceneType & ReqShareScene.ShowType.SILENT) == ReqShareScene.ShowType.SILENT) {
            LogUtil.i(TAG, "silentShare  ");
            silentShare(chatRequest, reqShareAccount, appKey, shareMessageManager);
            return;
        }
        if (sceneType != 0) {
            LogUtil.i(TAG, "normal Share ");
            LifecycleDispatcher.getInstance().registerCallback(this);
            shareMessageManager.sendRequestToXTC(chatRequest, appKey);
            return;
        }
        LifecycleDispatcher.getInstance().registerCallback(this);
        LogUtil.i(TAG, "normal Share  all request:" + chatRequest.getMessage().getShareObject());
        shareMessageManager.sendRequestToXTC(chatRequest, momentRequest, appKey);
    }

    private void silentShare(SendMessageToXTC.Request request, ReqShareAccount reqShareAccount, String appKey,
            ShareMessageManager shareMessageManager) {
        try {
            shareMessageManager.sendRequestToXTC(request, appKey, reqShareAccount.getAccountType(),
                    reqShareAccount.getAccount(), new IShareCallback.Stub() {
                        @Override
                        public void onResult(int code, String errorDesc) throws RemoteException {
                            SendMessageToXTC.Response response = new SendMessageToXTC.Response();
                            response.setCode(code);
                            response.setErrorDesc(errorDesc);
                            handler.complete(response);
                        }
                    });
        } catch (RemoteException e) {
            e.printStackTrace();
            SendMessageToXTC.Response response = new SendMessageToXTC.Response();
            response.setCode(100);
            response.setErrorDesc(e.toString());
            this.handler.complete(response);
        }
    }

    private void setAppInfo(String appName, String appIcon, ShareMessageManager shareMessageManager) {
        if (!TextUtils.isEmpty(appName)) {
            shareMessageManager.setAppName(appName);
        }
        if (TextUtils.isEmpty(appIcon)) {
            return;
        }
        byte[] iconBytes = Base64.decode(appIcon, Base64.NO_WRAP);
        shareMessageManager.setAppIcon(BitmapFactory.decodeByteArray(iconBytes, 0, iconBytes.length));
    }

    @Override
    public void disPatchOnCreate(Bundle savedInstanceState) {
        super.disPatchOnCreate(savedInstanceState);
        new XTCCallbackImpl().handleIntent(this.activity.getIntent(), this);
        LifecycleDispatcher.getInstance().unRegisterCallback(this);
    }

    @Override
    public void dispatchOnNewIntent(Intent intent) {
        super.dispatchOnNewIntent(intent);
        new XTCCallbackImpl().handleIntent(intent, this);
        LifecycleDispatcher.getInstance().unRegisterCallback(this);
    }

    @Override
    public void onResp(boolean handled, BaseResponse baseResponse) {
        this.handler.complete(baseResponse);
    }
}