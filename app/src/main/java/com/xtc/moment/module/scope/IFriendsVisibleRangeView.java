package com.xtc.moment.module.scope;

import com.xtc.architecture.mvp.core.MvpView;
import com.xtc.moment.module.bean.FriendsVisibleBean;

/**
 * 好友可见范围页面视图接口。
 */
public interface IFriendsVisibleRangeView extends MvpView {
    void showPermissons(FriendsVisibleBean visibleBean, String extra);
}