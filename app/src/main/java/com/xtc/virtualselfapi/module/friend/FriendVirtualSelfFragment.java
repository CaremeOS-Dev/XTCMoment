package com.xtc.virtualselfapi.module.friend;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.xtc.log.LogUtil;
import com.xtc.virtualselfapi.R;
import com.xtc.virtualselfapi.interfaces.ViewLoadCallBack;
import com.xtc.virtualselfapi.load.BaseViewLoader;
import com.xtc.virtualselfapi.load.FriendVirtualViewLoader;
import com.xtc.virtualselfapi.view.FriendVirtualView;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 好友虚拟形象展示 Fragment。
 */
public class FriendVirtualSelfFragment extends Fragment implements ViewLoadCallBack {

    public static final String OPEN_ID = "open_id";

    private static final String TAG = "Virtual_Self_Api_FriendVirtualSelfFragment";

    private ImageView ivForeground;
    private FriendVirtualSelf friendVirtualSelf;
    private BaseViewLoader friendVirtualViewLoader;
    private ViewLoadCallBack viewLoadCallBack;
    private RelativeLayout rlContent;

    public static FriendVirtualSelfFragment newInstance(String openId) {
        FriendVirtualSelfFragment fragment = new FriendVirtualSelfFragment();
        if (!TextUtils.isEmpty(openId)) {
            Bundle bundle = new Bundle();
            bundle.putString(OPEN_ID, openId);
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_virtual_self, container, false);
        initView(view);
        initData();
        return view;
    }

    private void initView(View view) {
        this.ivForeground = (ImageView) view.findViewById(R.id.iv_foreground);
        this.rlContent = (RelativeLayout) view.findViewById(R.id.rl_content);
    }

    private void initData() {
        this.friendVirtualViewLoader = new FriendVirtualViewLoader(getActivity())
                .setIvForeground(this.ivForeground)
                .setVgGenerateRender(this.rlContent)
                .setDynamicsViewId(R.id.rl_content_dynamics)
                .setViewLoadCallBack(this);
        if (getArguments() != null) {
            String openId = getArguments().getString(OPEN_ID);
            if (TextUtils.isEmpty(openId)) {
                LogUtil.d(TAG, "openId is empty");
            } else {
                this.friendVirtualSelf = new FriendVirtualSelf(getContext());
                showFriendView(openId);
            }
        }
    }

    public void showFriendView(String openId) {
        this.friendVirtualSelf.getVirtualSelfView(openId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<FriendVirtualView>() {
                    @Override
                    public void call(FriendVirtualView friendVirtualView) {
                        if (friendVirtualView != null) {
                            friendVirtualViewLoader.loadVirtualView(friendVirtualView);
                        } else {
                            onLoadFail();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        LogUtil.e(TAG, "getVirtualSelfView  error：" + throwable.getMessage());
                        onLoadFail();
                    }
                });
    }

    @Override
    public void onLoadSuccess() {
        if (this.viewLoadCallBack != null) {
            this.viewLoadCallBack.onLoadSuccess();
        }
    }

    @Override
    public void onLoadFail() {
        if (this.viewLoadCallBack != null) {
            this.viewLoadCallBack.onLoadFail();
        }
    }

    public void setViewLoadCallBack(ViewLoadCallBack viewLoadCallBack) {
        this.viewLoadCallBack = viewLoadCallBack;
    }

    public void sayHi(long delayMillis) {
        if (this.friendVirtualViewLoader != null) {
            this.friendVirtualViewLoader.sayHi(delayMillis);
        }
    }
}