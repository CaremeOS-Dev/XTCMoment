package com.xtc.web.client.data.response;

/** 应用安装状态查询结果。 */
public class RespAppInstallState {

    /** 安装状态码。 */
    public interface State {
        int ATSTORE = 2;
        int INSTALLED = 1;
        int NOEXIST = 3;
    }

    private int state;

    public int getState() {
        return this.state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "RespAppInstallState{state=" + this.state + '}';
    }
}