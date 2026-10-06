package com.xtc.moment.module.bean;

import com.xtc.moment.service.PublishService;

import java.util.List;

/**
 * 图片发布参数：图片路径列表、正文、地点与发布服务 Binder。
 */
public class SendPhotosParam {

    List<String> photoMsgs;
    String content;
    PoiBean poiBean;
    PublishService.PublishBinder publishBinder;

    public SendPhotosParam(List<String> photoMsgs, String content, PoiBean poiBean, PublishService.PublishBinder publishBinder) {
        this.photoMsgs = photoMsgs;
        this.content = content;
        this.poiBean = poiBean;
        this.publishBinder = publishBinder;
    }

    public List<String> getPhotoMsgs() {
        return this.photoMsgs;
    }

    public void setPhotoMsgs(List<String> photoMsgs) {
        this.photoMsgs = photoMsgs;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public PoiBean getPoiBean() {
        return this.poiBean;
    }

    public void setPoiBean(PoiBean poiBean) {
        this.poiBean = poiBean;
    }

    public PublishService.PublishBinder getPublishBinder() {
        return this.publishBinder;
    }

    public void setPublishBinder(PublishService.PublishBinder publishBinder) {
        this.publishBinder = publishBinder;
    }
}