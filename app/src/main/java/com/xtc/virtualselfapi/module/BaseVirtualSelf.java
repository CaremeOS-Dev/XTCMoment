package com.xtc.virtualselfapi.module;

import android.content.Context;

import com.xtc.virtualselfapi.bean.ViewInfo;
import com.xtc.virtualselfapi.bean.net.resp.RespUserFormat;
import com.xtc.virtualselfapi.manager.HttpManager;
import com.xtc.virtualselfapi.view.BaseVirtualView;
import com.xtc.virtualselfapi.view.ModelView;

import java.util.List;

import rx.Observable;

/**
 * 虚拟形象模块基类，负责组装数据模型与网络管理器。
 */
public abstract class BaseVirtualSelf<T extends BaseVirtualView> {

    protected HttpManager httpManager;
    protected Context mContext;
    protected ModelView mModelView;

    public abstract Observable<T> getVirtualSelfView(String openId);

    public BaseVirtualSelf(Context context) {
        this.mContext = context;
        this.httpManager = new HttpManager(context);
        this.mModelView = new ModelView(this.httpManager);
    }

    protected Observable<List<ViewInfo>> getViewInfoList(RespUserFormat userFormat) {
        return this.mModelView.getViewInfoFromUserInfo(userFormat);
    }
}