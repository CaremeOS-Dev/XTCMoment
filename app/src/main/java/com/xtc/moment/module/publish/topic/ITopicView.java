package com.xtc.moment.module.publish.topic;

import com.xtc.architecture.mvp.core.MvpView;
import com.xtc.moment.net.bean.RespTopicConfig;

/** 「小括号」（话题）选择页视图接口。 */
public interface ITopicView extends MvpView {

    /** 推荐话题拉取成功。 */
    void showTopicList(RespTopicConfig config);

    /** 推荐话题拉取失败。 */
    void showTopicError();
}
