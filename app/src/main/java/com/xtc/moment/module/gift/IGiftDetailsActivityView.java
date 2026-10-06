package com.xtc.moment.module.gift;

import com.xtc.architecture.mvp.core.MvpView;
import com.xtc.moment.net.bean.GiftDataBean;

import java.util.List;

/**
 * 礼物详情页视图接口。
 */
interface IGiftDetailsActivityView extends MvpView {
    void getGiftDataSuccess(List<GiftDataBean> giftData);

    void getGiftDataFail(List<GiftDataBean> giftData);
}