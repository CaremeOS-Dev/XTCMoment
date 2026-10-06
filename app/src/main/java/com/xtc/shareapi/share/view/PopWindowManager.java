package com.xtc.shareapi.share.view;

import android.content.Context;
import android.util.Log;
import android.view.WindowManager;

import com.xtc.shareapi.share.communication.BaseResponse;
import com.xtc.shareapi.share.communication.SendMessageToXTC;
import com.xtc.shareapi.share.constant.OpenApiConstant;
import com.xtc.shareapi.share.interfaces.IChooseSceneCallback;
import com.xtc.shareapi.share.interfaces.Scene;
import com.xtc.shareapi.share.manager.ShareStrategy;
import com.xtc.shareapi.share.sharescene.Chat;
import com.xtc.shareapi.share.sharescene.Moment;
import com.xtc.shareapi.share.utils.BitmapUtil;
import com.xtc.shareapi.share.utils.ShareUtil;

/**
 * 分享场景选择弹窗管理，负责悬浮窗的显示、隐藏与场景回调分发。
 */
public class PopWindowManager {

    private static final String TAG = OpenApiConstant.TAG + PopWindowManager.class.getSimpleName();

    private final Context context;
    private final WindowManager windowManager;
    private final WindowManager.LayoutParams windowLayoutParams = new WindowManager.LayoutParams();

    private ChooseSceneView chooseSceneView;
    private SendMessageToXTC.Request chatRequest;
    private SendMessageToXTC.Request momentRequest;

    public static PopWindowManager getInstance(Context context) {
        return new PopWindowManager(context);
    }

    private PopWindowManager(Context context) {
        this.context = context;
        this.windowManager = (WindowManager) this.context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams layoutParams = this.windowLayoutParams;
        layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        layoutParams.format = android.graphics.PixelFormat.OPAQUE;
        layoutParams.gravity = android.view.Gravity.TOP | android.view.Gravity.START;
        layoutParams.width = BitmapUtil.getScreenWidth(context);
        layoutParams.height = BitmapUtil.getScreenHeight(context);
        layoutParams.x = 0;
        layoutParams.y = 0;
        layoutParams.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
    }

    public void showChooseSceneWindow(String appKey, SendMessageToXTC.Request request, ShareStrategy shareStrategy) {
        this.chatRequest = request;
        showChooseSceneWindow(appKey, shareStrategy);
    }

    public void showChooseSceneWindow(String appKey, SendMessageToXTC.Request chatRequest,
                                      SendMessageToXTC.Request momentRequest, ShareStrategy shareStrategy) {
        this.chatRequest = chatRequest;
        this.momentRequest = momentRequest;
        showChooseSceneWindow(appKey, shareStrategy);
    }

    private void showChooseSceneWindow(final String appKey, final ShareStrategy shareStrategy) {
        if (chooseSceneView == null) {
            chooseSceneView = new ChooseSceneView(context);
        }
        chooseSceneView.setCallback(new IChooseSceneCallback() {
            @Override
            public void setOnClickCallback(int sceneType) {
                dealSceneType(sceneType, appKey, shareStrategy);
            }
        });
        if (chooseSceneView.getParent() == null) {
            windowManager.addView(chooseSceneView, windowLayoutParams);
            chooseSceneView.setPopWindow(this);
            Log.d(TAG, "come to showChooseSceneWindow !");
            return;
        }
        Log.d(TAG, "come to add window activity !");
    }

    private void dealSceneType(int sceneType, String appKey, ShareStrategy shareStrategy) {
        if (sceneType == ChooseSceneView.CANCEL_TYPE) {
            Log.d(TAG, "cancel choose scene");
            ShareUtil.startTargetActivity(context, BaseResponse.Code.CANCEL, BaseResponse.Desc.CANCEL);
        } else if (sceneType == ChooseSceneView.CHAT_TYPE) {
            shareStrategy.share(getChatScene(), appKey);
        } else if (sceneType == ChooseSceneView.MOMENT_TYPE) {
            shareStrategy.share(getMomentScene(), appKey);
        }
    }

    private SendMessageToXTC.Request getChatScene() {
        Scene firstScene = chatRequest.getScene();
        Scene secondScene = momentRequest != null ? momentRequest.getScene() : null;
        Log.d(TAG, "scene chat = " + firstScene + " " + secondScene);
        if (firstScene != null && firstScene.getType() == Scene.TYPE_CHAT) {
            return chatRequest;
        }
        if (secondScene != null && secondScene.getType() == Scene.TYPE_CHAT) {
            return momentRequest;
        }
        Chat chat = new Chat();
        chat.setFriendType(Chat.FRIEND | Chat.FRIEND_GROUP);
        chat.setSelectActionMode(Chat.SINGLE);
        chatRequest.setScene(chat);
        return chatRequest;
    }

    private SendMessageToXTC.Request getMomentScene() {
        Scene firstScene = chatRequest.getScene();
        Scene secondScene = momentRequest != null ? momentRequest.getScene() : null;
        Log.d(TAG, "scene moment = " + firstScene + " " + secondScene);
        if (firstScene != null && firstScene.getType() == Scene.TYPE_MOMENT) {
            return chatRequest;
        }
        if (secondScene != null && secondScene.getType() == Scene.TYPE_MOMENT) {
            return momentRequest;
        }
        chatRequest.setScene(new Moment());
        return chatRequest;
    }

    public void hideChooseSceneWindow() {
        ChooseSceneView view = this.chooseSceneView;
        if (view != null) {
            if (view.getParent() != null) {
                Log.d(TAG, "hideChooseSceneWindow");
                windowManager.removeViewImmediate(view);
            }
            this.chooseSceneView = null;
        }
    }
}