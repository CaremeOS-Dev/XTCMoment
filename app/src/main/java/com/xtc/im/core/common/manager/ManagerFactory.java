package com.xtc.im.core.common.manager;

import com.xtc.im.core.common.LogTag;
import com.xtc.log.LogUtil;

import java.util.WeakHashMap;

/** 管理器工厂：按类名缓存并复用 Manager 单例。 */
public class ManagerFactory {

    private static final String TAG = LogTag.tag("ManagerFactory");
    private static volatile ManagerFactory managerFactory;
    private WeakHashMap<String, Manager> managerCache = new WeakHashMap<>();

    public static ManagerFactory getInstance() {
        if (managerFactory == null) {
            synchronized (ManagerFactory.class) {
                if (managerFactory == null) {
                    managerFactory = new ManagerFactory();
                }
            }
        }
        return managerFactory;
    }

    /** 取管理器实例，缓存未命中时反射创建。 */
    @SuppressWarnings("unchecked")
    public <T> T getManager(Class<? extends Manager> managerClass) {
        T manager;
        synchronized (this.managerCache) {
            manager = (T) this.managerCache.get(managerClass.getName());
        }
        if (manager == null) {
            try {
                return (T) managerClass.newInstance();
            } catch (IllegalAccessException e) {
                LogUtil.e(TAG, e);
            } catch (InstantiationException e) {
                LogUtil.e(TAG, e);
            }
        }
        return manager;
    }

    protected void putManager(Manager manager) {
        String name = manager.getClass().getName();
        synchronized (this.managerCache) {
            if (!this.managerCache.containsKey(name)) {
                this.managerCache.put(name, manager);
                LogUtil.i(TAG, "put manager:" + name);
            }
        }
    }
}