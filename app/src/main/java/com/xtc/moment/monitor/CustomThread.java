package com.xtc.moment.monitor;

/**
 * 供监控模块使用的线程实现，便于统一识别线程来源。
 */
public class CustomThread extends Thread {

    public CustomThread() {
    }

    public CustomThread(Runnable runnable) {
        super(runnable);
    }

    public CustomThread(ThreadGroup group, Runnable runnable) {
        super(group, runnable);
    }

    public CustomThread(String name) {
        super(name);
    }

    public CustomThread(ThreadGroup group, String name) {
        super(group, name);
    }

    public CustomThread(Runnable runnable, String name) {
        super(runnable, name);
    }

    public CustomThread(ThreadGroup group, Runnable runnable, String name) {
        super(group, runnable, name);
    }

    public CustomThread(ThreadGroup group, Runnable runnable, String name, long stackSize) {
        super(group, runnable, name, stackSize);
    }

    @Override
    public void run() {
        super.run();
    }
}