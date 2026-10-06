package com.xtc.shareapi.share.sharescene;

import android.os.Bundle;
import android.util.Log;

import com.xtc.shareapi.share.communication.BaseResponse;
import com.xtc.shareapi.share.communication.SendMessageToXTC;
import com.xtc.shareapi.share.constant.OpenApiConstant;
import com.xtc.shareapi.share.interfaces.IBundleSerialize;
import com.xtc.shareapi.share.interfaces.Scene;

import java.util.ArrayList;
import java.util.List;

/**
 * 微聊会话分享场景，可指定会话、好友类型与选择模式等过滤条件。
 */
public class Chat implements Scene {

    /** 好友类型：普通好友。 */
    public static final int FRIEND = 1;
    /** 好友类型：好友分组。 */
    public static final int FRIEND_GROUP = 16;
    /** 好友类型：管理员。 */
    public static final int MANAGER = 256;
    /** 好友类型：家庭群。 */
    public static final int FAMILY_GROUP = 4096;
    /** 好友类型：老师。 */
    public static final int TEACHER = 65536;
    /** 好友类型：老师群。 */
    public static final int TEACHER_GROUP = 1048576;
    /** 好友类型：班级群。 */
    public static final int CLASS_GROUP = 16777216;

    /** 选择模式：不可选。 */
    public static final int NONE = 1;
    /** 选择模式：单选。 */
    public static final int SINGLE = 2;
    /** 选择模式：多选。 */
    public static final int MULTIPLE = 3;

    private int selectActionMode;
    private List<String> openIdList;
    private int friendType;
    private List<String> filterConversationList;
    private List<String> filterModeList;
    private String filterTip;

    public Chat() {
        setSelectActionMode(SINGLE);
        setFriendType(FRIEND | FRIEND_GROUP);
    }

    @Override
    public void toBundle(Bundle bundle) {
        bundle.putInt(OpenApiConstant.SceneConstant.BUNDLE_CHAT_SELECTION_MODE, selectActionMode);
        bundle.putStringArrayList(OpenApiConstant.SceneConstant.BUNDLE_CHAT_OPENID_LIST, (ArrayList<String>) openIdList);
        bundle.putInt(OpenApiConstant.SceneConstant.BUNDLE_CHAT_FRIEND_TYPE, friendType);
        bundle.putStringArrayList(OpenApiConstant.SceneConstant.BUNDLE_CHAT_CONVERSATION_LIST, (ArrayList<String>) filterConversationList);
        bundle.putStringArrayList(OpenApiConstant.SceneConstant.BUNDLE_CHAT_MODE_LIST, (ArrayList<String>) filterModeList);
        bundle.putString(OpenApiConstant.SceneConstant.BUNDLE_CHAT_FILTER_TIP, filterTip);
        bundle.putInt(OpenApiConstant.SceneConstant.BUNDLE_SCENE_SHARE_TYPE, getType());
        Log.d(OpenApiConstant.TAG, "come to chat toBundle");
    }

    @Override
    public IBundleSerialize fromBundle(Bundle bundle) {
        setSelectActionMode(bundle.getInt(OpenApiConstant.SceneConstant.BUNDLE_CHAT_SELECTION_MODE));
        setOpenIdList(bundle.getStringArrayList(OpenApiConstant.SceneConstant.BUNDLE_CHAT_OPENID_LIST));
        setFriendType(bundle.getInt(OpenApiConstant.SceneConstant.BUNDLE_CHAT_FRIEND_TYPE));
        setFilterConversationList(bundle.getStringArrayList(OpenApiConstant.SceneConstant.BUNDLE_CHAT_CONVERSATION_LIST));
        setFilterModeList(bundle.getStringArrayList(OpenApiConstant.SceneConstant.BUNDLE_CHAT_MODE_LIST));
        setFilterTip(bundle.getString(OpenApiConstant.SceneConstant.BUNDLE_CHAT_FILTER_TIP));
        Log.d(OpenApiConstant.TAG, "come to chat fromBundle");
        return this;
    }

    @Override
    public BaseResponse checkArgs() {
        SendMessageToXTC.Response response = new SendMessageToXTC.Response();
        response.setCode(1);
        return response;
    }

    @Override
    public String getAppName() {
        return OpenApiConstant.XTCShareAppName.XTC_CHAT_APP_NAME;
    }

    @Override
    public String getPackageName() {
        return OpenApiConstant.App.CHAT_PACKAGE_NAME;
    }

    @Override
    public String getTargetClassName() {
        return OpenApiConstant.App.LAUNCHER_CHAT_ACTIVITY;
    }

    @Override
    public int getType() {
        return TYPE_CHAT;
    }

    public List<String> getOpenIdList() {
        return openIdList;
    }

    public void setOpenIdList(List<String> openIdList) {
        this.openIdList = openIdList;
    }

    public int getSelectActionMode() {
        return selectActionMode;
    }

    public void setSelectActionMode(int selectActionMode) {
        this.selectActionMode = selectActionMode;
    }

    public int getFriendType() {
        return friendType;
    }

    public void setFriendType(int friendType) {
        this.friendType = friendType;
    }

    public List<String> getFilterConversationList() {
        return filterConversationList;
    }

    public void setFilterConversationList(List<String> filterConversationList) {
        this.filterConversationList = filterConversationList;
    }

    public String getFilterTip() {
        return filterTip;
    }

    public void setFilterTip(String filterTip) {
        this.filterTip = filterTip;
    }

    public List<String> getFilterModeList() {
        return filterModeList;
    }

    public void setFilterModeList(List<String> filterModeList) {
        this.filterModeList = filterModeList;
    }

    @Override
    public String toString() {
        return "Chat{selectActionMode=" + selectActionMode + ", openIdList=" + openIdList + '}';
    }
}