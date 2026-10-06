package com.xtc.moment.monitor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 对外暴露主 IO 线程池的接口。
 */
public interface IThreadPool {
    ThreadPoolExecutor getMainIOThreadPoolExecutor();
}