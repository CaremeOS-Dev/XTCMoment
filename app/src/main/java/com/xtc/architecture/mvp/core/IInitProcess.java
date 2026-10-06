package com.xtc.architecture.mvp.core;

/** Two-phase initialisation contract for MVP activities. */
public interface IInitProcess {
    void initData();

    void initView();
}
