package com.xtc.moment.serve.bean;

import com.xtc.moment.module.bean.CloudFileResource;

import java.util.List;

/**
 * 文件下载地址返回体。
 */
public class DownloadUrlVo {

    private static final long serialVersionUID = 8290673691110828962L;

    private List<CloudFileResource> urls;

    public List<CloudFileResource> getUrls() {
        return this.urls;
    }

    public void setUrls(List<CloudFileResource> urls) {
        this.urls = urls;
    }
}