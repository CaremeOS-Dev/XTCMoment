package com.xtc.moment.module.bean;

import java.util.List;

/**
 * 多图动态的正文内容：文字、MD5 追踪列表、云文件资源以及本地路径与视频文案。
 */
public class MultiPhotoContent {

    private String content;
    private List<PhotoMD5Value> trackMd5Values;
    private CloudFileResource resource;
    private String localPaths;
    private String videoMsgContent;

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<PhotoMD5Value> getTrackMd5Values() {
        return this.trackMd5Values;
    }

    public void setTrackMd5Values(List<PhotoMD5Value> trackMd5Values) {
        this.trackMd5Values = trackMd5Values;
    }

    public CloudFileResource getResource() {
        return this.resource;
    }

    public void setResource(CloudFileResource resource) {
        this.resource = resource;
    }

    public String getLocalPaths() {
        return this.localPaths;
    }

    public void setLocalPaths(String localPaths) {
        this.localPaths = localPaths;
    }

    public String getVideoMsgContent() {
        return this.videoMsgContent;
    }

    public void setVideoMsgContent(String videoMsgContent) {
        this.videoMsgContent = videoMsgContent;
    }

    @Override
    public String toString() {
        return "MultiPhotoContent{content='" + this.content + "', trackMd5Values=" + this.trackMd5Values + ", resource="
                + this.resource + ", LocalPaths='" + this.localPaths + "', videoMsgContent='" + this.videoMsgContent + "'}";
    }
}