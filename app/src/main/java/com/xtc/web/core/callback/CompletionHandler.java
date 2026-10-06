package com.xtc.web.core.callback;

/** JS 异步调用 native 时的结果回调，progressData 用于多次回调（进度）。 */
public interface CompletionHandler<T> {

    /** 无数据完成。 */
    void complete();

    /** 携带最终数据完成。 */
    void complete(T data);

    /** 上报中间进度数据，不会结束本次调用。 */
    void setProgressData(T data);
}