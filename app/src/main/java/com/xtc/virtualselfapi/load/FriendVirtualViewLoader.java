package com.xtc.virtualselfapi.load;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.xtc.database.ormlite.CollectionUtil;
import com.xtc.game.engine.bean.SpineCodeLoadBean;
import com.xtc.game.engine.render.BaseSpineAdapter;
import com.xtc.game.engine.support.SpineFunctionDelegate;
import com.xtc.log.LogUtil;
import com.xtc.virtualselfapi.bean.DynamicsVirtualSelfBean;
import com.xtc.virtualselfapi.bean.ViewInfo;
import com.xtc.virtualselfapi.fragment.VirtualDynamicsSelfFragment;
import com.xtc.virtualselfapi.generate.DynamicsVisualStrategy;
import com.xtc.virtualselfapi.generate.StaticVisualStrategy;
import com.xtc.virtualselfapi.generate.bean.GenerateVirtualBean;
import com.xtc.virtualselfapi.view.FriendVirtualView;

import java.util.List;

/**
 * 好友虚拟形象视图加载器。
 */
public class FriendVirtualViewLoader extends BaseViewLoader<FriendVirtualView> {

    private static final String TAG = "Virtual_Self_Api_FriendVirtualViewLoader";

    @Override
    public String getTag() {
        return TAG;
    }

    public FriendVirtualViewLoader(FragmentActivity activity) {
        super(activity);
    }

    @Override
    public void loadVirtualView(FriendVirtualView friendVirtualView) {
        if (!checkParams()) {
            return;
        }
        LogUtil.d(TAG, "friend view info = " + friendVirtualView);
        final List<ViewInfo> viewInfoList = friendVirtualView.getViewInfoList();
        DynamicsVirtualSelfBean talentCustomDress = friendVirtualView.getTalentCustomDress();
        final GenerateVirtualBean generateVirtualBean = new GenerateVirtualBean();
        boolean haveDanger = friendVirtualView.getStatus() == 1;
        if (talentCustomDress != null) {
            if (this.virtualDynamicsSelfFragment == null) {
                this.virtualDynamicsSelfFragment = VirtualDynamicsSelfFragment.getInstance();
            }
            generateVirtualBean.setDynamicsRender(this.virtualDynamicsSelfFragment);
            generateVirtualBean.setDynamicsVirtualSelfBean(talentCustomDress);
            generateVirtualBean.setFragmentManager(this.mActivity.getSupportFragmentManager());
            generateVirtualBean.setHaveDanger(haveDanger);
            generateVirtualBean.setIvForeground(this.mIvForeground);
            generateVirtualBean.setStatus(friendVirtualView.isSkill() ? 10 : friendVirtualView.getStatus());
            generateVirtualBean.setLoadDataListener(new BaseSpineAdapter.LoadDataListener() {
                @Override
                public void onLoadDataFinish(SpineCodeLoadBean loadBean) {
                    LogUtil.i(TAG, "onLoadDataFinish");
                    spineCodeLoadBean = loadBean;
                    spineFunctionDelegate = new SpineFunctionDelegate(generateVirtualBean.getDynamicsRender().getSpineRender());
                    notifyLoadSuccess();
                }

                @Override
                public void onLoadDataDestroy() {
                    LogUtil.i(TAG, "onLoadDataDestroy");
                    virtualDynamicsSelfFragment = null;
                    spineCodeLoadBean = null;
                    spineFunctionDelegate = null;
                }

                @Override
                public void onLoadDataError(int code, String message) {
                    LogUtil.i(TAG, "onLoadDataError");
                    if (virtualDynamicsSelfFragment == null || isActivityFinish()) {
                        return;
                    }
                    LogUtil.i(TAG, "onLoadDataError code:" + code + " msg:" + message);
                    if (code == 1) {
                        FragmentTransaction transaction = mActivity.getSupportFragmentManager().beginTransaction();
                        transaction.remove(virtualDynamicsSelfFragment);
                        transaction.commitAllowingStateLoss();
                    } else if (code == 2) {
                        loadDefaultVisual(generateVirtualBean, viewInfoList);
                        return;
                    }
                    notifyLoadFail();
                }
            });
            generateVirtualBean.setVgGenerateRender(this.mVgGenerateRender);
            generateVirtualBean.setViewInfoList(viewInfoList);
            new DynamicsVisualStrategy(this.mActivity).generateVisual(generateVirtualBean, this.mDynamicsViewId);
            return;
        }
        if (CollectionUtil.isEmpty(viewInfoList)) {
            notifyLoadFail();
            return;
        }
        generateVirtualBean.setDynamicsRender(this.virtualDynamicsSelfFragment);
        generateVirtualBean.setFragmentManager(this.mActivity.getSupportFragmentManager());
        generateVirtualBean.setHaveDanger(haveDanger);
        generateVirtualBean.setStatus(friendVirtualView.isSkill() ? 10 : friendVirtualView.getStatus());
        generateVirtualBean.setVgGenerateRender(this.mVgGenerateRender);
        generateVirtualBean.setViewInfoList(viewInfoList);
        new StaticVisualStrategy(this.mActivity).generateVisual(generateVirtualBean, this.mDynamicsViewId);
        notifyLoadSuccess();
    }
}