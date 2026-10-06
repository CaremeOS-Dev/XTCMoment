package com.xtc.shareapi.share.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.xtc.shareapi.R;
import com.xtc.shareapi.share.interfaces.IChooseSceneCallback;

/**
 * 分享场景选择视图，提供微聊、好友圈与取消三个入口。
 */
public class ChooseSceneView extends RelativeLayout implements View.OnClickListener {

    /** 取消。 */
    public static final int CANCEL_TYPE = 0;
    /** 微聊。 */
    public static final int CHAT_TYPE = 1;
    /** 好友圈。 */
    public static final int MOMENT_TYPE = 2;

    private IChooseSceneCallback callback;
    private PopWindowManager popWindowManager;

    public ChooseSceneView(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        View content = LayoutInflater.from(getContext()).inflate(R.layout.window_choose_scene_layout, this);
        AppLinearLayout chatScene = content.findViewById(R.id.iv_chat_scene);
        AppLinearLayout momentScene = content.findViewById(R.id.iv_moment_scene);
        Button cancelButton = content.findViewById(R.id.bt_cancel_share);
        cancelButton.setGravity(android.view.Gravity.CENTER);
        chatScene.setOnClickListener(this);
        momentScene.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
    }

    public void setPopWindow(PopWindowManager popWindowManager) {
        this.popWindowManager = popWindowManager;
    }

    private void hideChooseSceneWindow() {
        PopWindowManager popWindowManager = this.popWindowManager;
        if (popWindowManager != null) {
            popWindowManager.hideChooseSceneWindow();
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_chat_scene) {
            dealCallback(CHAT_TYPE);
            hideChooseSceneWindow();
        } else if (id == R.id.iv_moment_scene) {
            dealCallback(MOMENT_TYPE);
            hideChooseSceneWindow();
        } else if (id == R.id.bt_cancel_share) {
            dealCallback(CANCEL_TYPE);
            hideChooseSceneWindow();
        } else {
            dealCallback(CANCEL_TYPE);
            hideChooseSceneWindow();
        }
    }

    private void dealCallback(int sceneType) {
        IChooseSceneCallback callback = this.callback;
        if (callback == null) {
            return;
        }
        callback.setOnClickCallback(sceneType);
    }

    public void setCallback(IChooseSceneCallback callback) {
        this.callback = callback;
    }
}