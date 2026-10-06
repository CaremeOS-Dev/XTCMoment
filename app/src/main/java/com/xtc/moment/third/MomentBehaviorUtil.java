package com.xtc.moment.third;

import com.xtc.httplib.netstate.NetStateDataManager;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.third.bean.PushContentBean;

/**
 * 推送行为内容构造工具。
 */
public class MomentBehaviorUtil {

    public static PushContentBean createDeleteContentBean(DbMoment moment) {
        PushContentBean contentBean = new PushContentBean();
        int type = moment.getType().intValue();
        if (type == 8 || type == 9 || type == 22 || type == 23) {
            contentBean.setType(PushContentBean.PHOTO);
            contentBean.setContent(moment.getResource());
            return contentBean;
        }
        switch (type) {
            case 0:
                contentBean.setType(PushContentBean.MOOD);
                contentBean.setContent(moment.getContent());
                break;
            case 1:
                contentBean.setType(PushContentBean.STATE);
                contentBean.setContent(moment.getContent());
                break;
            case 2:
                contentBean.setType(PushContentBean.LOCATION);
                contentBean.setContent(moment.getContent());
                break;
            case 3:
                contentBean.setType(PushContentBean.TEXT);
                contentBean.setContent(moment.getContent());
                break;
            case 4:
            case 5:
                contentBean.setType(PushContentBean.PHOTO);
                contentBean.setContent(moment.getResource());
                break;
            case 6:
                contentBean.setType(PushContentBean.VIDEO);
                contentBean.setContent(moment.getResource());
                break;
            default:
                contentBean.setType(PushContentBean.UNKNOWN);
                contentBean.setContent(NetStateDataManager.UNKNOWN);
                break;
        }
        return contentBean;
    }
}