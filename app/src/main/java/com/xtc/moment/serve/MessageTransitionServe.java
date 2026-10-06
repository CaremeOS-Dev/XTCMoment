package com.xtc.moment.serve;

import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.serve.bean.MomentMessageData;
import com.xtc.utils.common.CollectionUtil;
import com.xtc.utils.encode.JSONUtil;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 消息类型转换：当一条消息带多个类型时，按 typeList 选出优先级最高的类型。
 */
public class MessageTransitionServe {

    public interface OnTransitionCompleteListener {
        void transitionComplete(MomentMessageData messageData);

        void transitionError(MomentMessageData messageData);
    }

    public static MomentMessageData dealMsgTypeTransition(MomentMessageData messageData) {
        if (messageData == null) {
            return null;
        }
        List<Integer> typeList = JSONUtil.fromJSON(messageData.getTypeList(), List.class, Integer.class);
        if (!CollectionUtil.isEmpty(typeList)) {
            Collections.sort(typeList, new Comparator<Integer>() {
                @Override
                public int compare(Integer left, Integer right) {
                    return right.intValue() - left.intValue();
                }
            });
            for (int i = 0; i < typeList.size(); i++) {
                Integer type = typeList.get(i);
                if (type.intValue() < 30) {
                    if (messageData.getType() == type.intValue()) {
                        break;
                    }
                    messageData.setType(type.intValue());
                    break;
                }
            }
        }
        return messageData;
    }

    public static DbMoment dealMsgTypeTransition(DbMoment moment) {
        MomentMessageData messageData = new MomentMessageData();
        String typeListJson = moment.getTypeList();
        messageData.setTypeList(typeListJson);
        List<Integer> typeList = JSONUtil.fromJSON(typeListJson, List.class, Integer.class);
        if (!CollectionUtil.isEmpty(typeList)) {
            Collections.sort(typeList, new Comparator<Integer>() {
                @Override
                public int compare(Integer left, Integer right) {
                    return right.intValue() - left.intValue();
                }
            });
            for (int i = 0; i < typeList.size(); i++) {
                Integer type = typeList.get(i);
                if (type.intValue() < 30) {
                    if (moment.getType().intValue() == type.intValue()) {
                        break;
                    }
                    moment.setType(type);
                    break;
                }
            }
        }
        return moment;
    }
}