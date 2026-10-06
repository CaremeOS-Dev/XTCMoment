package com.xtc.contactapi.base;

/**
 * 联系人 API 请求基类，携带请求码与泛型参数。
 */
public class BaseRequest<T> {

    private int requestCode;
    private T parameter;

    public int getRequestCode() {
        return requestCode;
    }

    public T getParameter() {
        return parameter;
    }

    public void setParameter(T parameter) {
        this.parameter = parameter;
    }

    @Override
    public String toString() {
        return "BaseRequest{requestCode=" + requestCode + ", parameter=" + parameter + '}';
    }
}