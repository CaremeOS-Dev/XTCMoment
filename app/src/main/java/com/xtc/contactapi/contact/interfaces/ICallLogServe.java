package com.xtc.contactapi.contact.interfaces;

import com.xtc.contactapi.contact.bean.CallLogBean;

import java.util.List;

/**
 * 通话记录服务接口。
 */
public interface ICallLogServe {

    /** 查询全部通话记录。 */
    List<CallLogBean> getAllCallLog();

    /** 按时间区间查询通话记录。 */
    List<CallLogBean> getCallLogLimit(long startTime, long endTime);

    /** 更新通话记录已读状态。 */
    void updateReadState(int readState);

    /** 批量删除通话记录。 */
    boolean deleteCallLogForBatch(String selection);

    /** 清除未读数量。 */
    void clearUnreadNumber();

    /** 清除通话记录。 */
    void clearCallLog();

    /** 是否支持删除。 */
    boolean isSupportDelete();

    /** 删除全部通话记录。 */
    boolean deleteAllCallLog();
}