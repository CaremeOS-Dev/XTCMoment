package com.xtc.im.core.common.manager;

/** 管理器基类，实例化时自动注册到 {@link ManagerFactory}。 */
public abstract class Manager {

    public Manager() {
        ManagerFactory.getInstance().putManager(this);
    }
}