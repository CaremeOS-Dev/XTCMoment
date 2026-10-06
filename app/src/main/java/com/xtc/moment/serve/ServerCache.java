package com.xtc.moment.serve;

import android.content.Context;

import com.xtc.architecture.mvp.BaseServe;
import com.xtc.database.ormlite.OrmLiteDao;
import com.xtc.httplib.net.HttpServiceProxy;
import com.xtc.log.LogUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.WeakHashMap;

/**
 * 业务服务、网络代理与 DAO 的弱引用缓存，统一通过反射创建实例。
 */
public class ServerCache {

    private static final String TAG = "XTC_MOMENT_ServerCache";

    private static final WeakHashMap<String, BaseServe> businessServerCache = new WeakHashMap<>();
    private static final WeakHashMap<String, HttpServiceProxy> httpServiceCache = new WeakHashMap<>();
    private static final WeakHashMap<String, OrmLiteDao> daoCache = new WeakHashMap<>();

    @SuppressWarnings("unchecked")
    public static <T> T getBusinessServer(Context context, Class<? extends BaseServe> serverClass) {
        String name = serverClass.getName();
        Object server;
        synchronized (businessServerCache) {
            server = businessServerCache.get(name);
        }
        if (server == null) {
            server = createServer(context, serverClass);
            synchronized (businessServerCache) {
                if (!businessServerCache.containsKey(name) && server != null) {
                    businessServerCache.put(name, (BaseServe) server);
                } else {
                    LogUtil.i(TAG, "getBusinessServer: server class is exist: " + name);
                }
            }
        }
        return (T) server;
    }

    public static void putBusinessServer(BaseServe server) {
        synchronized (businessServerCache) {
            if (!businessServerCache.containsKey(server.getClass().getName())) {
                businessServerCache.put(server.getClass().getName(), server);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getHttpService(Context context, Class<? extends HttpServiceProxy> serviceClass) {
        String name = serviceClass.getName();
        Object service;
        synchronized (httpServiceCache) {
            service = httpServiceCache.get(name);
        }
        if (service == null) {
            service = createServer(context, serviceClass);
            synchronized (httpServiceCache) {
                if (!httpServiceCache.containsKey(name) && service != null) {
                    httpServiceCache.put(name, (HttpServiceProxy) service);
                }
            }
        }
        return (T) service;
    }

    public static void putHttpService(HttpServiceProxy service) {
        synchronized (httpServiceCache) {
            if (!httpServiceCache.containsKey(service.getClass().getName())) {
                httpServiceCache.put(service.getClass().getName(), service);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getDao(Context context, Class<? extends OrmLiteDao> daoClass) {
        String name = daoClass.getName();
        Object dao;
        synchronized (daoCache) {
            dao = daoCache.get(name);
        }
        if (dao == null) {
            dao = createServer(context, daoClass);
            synchronized (daoCache) {
                if (!daoCache.containsKey(name) && dao != null) {
                    daoCache.put(name, (OrmLiteDao) dao);
                }
            }
        }
        return (T) dao;
    }

    public static void putDao(OrmLiteDao dao) {
        synchronized (daoCache) {
            if (!daoCache.containsKey(dao.getClass().getName())) {
                daoCache.put(dao.getClass().getName(), dao);
            }
        }
    }

    private static <S> S createServer(Context context, Class<? extends S> clazz) {
        if (context == null) {
            throw new IllegalArgumentException("server argument context is null");
        }
        S instance = null;
        String name = clazz.getName();
        try {
            try {
                Constructor<? extends S> constructor = clazz.getDeclaredConstructor(Context.class);
                constructor.setAccessible(true);
                instance = constructor.newInstance(context.getApplicationContext());
                LogUtil.i("ServerCache", "create server,name:" + name);
                if (instance == null) {
                    throw new NullPointerException("create server failed,name:" + name);
                }
            } catch (NoSuchMethodException e) {
                LogUtil.e("ServerCache", e);
                if (instance == null) {
                    throw new NullPointerException("create server failed,name:" + name);
                }
            } catch (InstantiationException e) {
                LogUtil.e("ServerCache", e);
                if (instance == null) {
                    throw new NullPointerException("create server failed,name:" + name);
                }
            } catch (IllegalAccessException e) {
                LogUtil.e("ServerCache", e);
                if (instance == null) {
                    throw new NullPointerException("create server failed,name:" + name);
                }
            } catch (InvocationTargetException e) {
                LogUtil.e("ServerCache InvocationTargetException target=", e.getTargetException());
                if (instance == null) {
                    throw new NullPointerException("create server failed,name:" + name);
                }
            }
            return instance;
        } catch (Throwable t) {
            if (instance != null) {
                throw t;
            }
            throw new NullPointerException("create server failed,name:" + name);
        }
    }
}