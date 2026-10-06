package com.xtc.web.core.callback;

/** native 调用 JS 后接收 JS 返回值。 */
public interface OnReturnValue<T> {

    void onValue(T value);
}