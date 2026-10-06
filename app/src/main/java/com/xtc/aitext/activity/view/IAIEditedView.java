package com.xtc.aitext.activity.view;

import com.xtc.aitext.bean.AIDataBean;
import com.xtc.aitext.bean.UserAccessBean;
import com.xtc.aitext.bean.WatchBoxContent;
import com.xtc.architecture.mvp.core.MvpView;

/**
 * AI 编辑页视图接口。
 */
public interface IAIEditedView extends MvpView {

    /** 展示剩余次数。 */
    void showRemainTimes(int remainTimes);

    /** 展示主页数据。 */
    void showHomeData(AIDataBean dataBean);

    /** 展示权益项。 */
    void showUserAccess(UserAccessBean userAccessBean);

    /** 展示手表弹窗。 */
    void showWatchBoxContent(WatchBoxContent content, UserAccessBean userAccessBean);

    /** 展示提示文案。 */
    void showToast(String message);

    /** 展示空数据。 */
    void showEmpty();

    /** 处理权益领取状态。 */
    void onUserAccessObtained(UserAccessBean userAccessBean);

    /** 次数单位后缀。 */
    int getTimesSuffix();
}