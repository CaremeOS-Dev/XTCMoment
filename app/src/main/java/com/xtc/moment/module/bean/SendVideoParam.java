package com.xtc.moment.module.bean;

import com.xtc.moment.net.bean.VideoTokenVoResponse;

/**
 * 视频发布参数：缩略图、视频名、来源、文案、地点、分享数据与 token 返回体。
 */
public class SendVideoParam {

    private String thumbnailPath;
    private String videoName;
    private boolean fromAlbum;
    private String videoText;
    private PoiBean poiBean;
    private ShareVideoMoment shareVideoMoment;
    private VideoTokenVoResponse videoTokenVoResponse;
    private int fromType;
    private FriendsVisibleBean friendsVisibleBean;

    public SendVideoParam(String thumbnailPath, String videoName, boolean fromAlbum, String videoText, PoiBean poiBean,
            ShareVideoMoment shareVideoMoment) {
        this.thumbnailPath = thumbnailPath;
        this.videoName = videoName;
        this.fromAlbum = fromAlbum;
        this.videoText = videoText;
        this.poiBean = poiBean;
        this.shareVideoMoment = shareVideoMoment;
    }

    public SendVideoParam(String thumbnailPath, String videoName, boolean fromAlbum, String videoText, PoiBean poiBean,
            ShareVideoMoment shareVideoMoment, VideoTokenVoResponse videoTokenVoResponse,
            FriendsVisibleBean friendsVisibleBean) {
        this.thumbnailPath = thumbnailPath;
        this.videoName = videoName;
        this.fromAlbum = fromAlbum;
        this.videoText = videoText;
        this.poiBean = poiBean;
        this.shareVideoMoment = shareVideoMoment;
        this.videoTokenVoResponse = videoTokenVoResponse;
        this.friendsVisibleBean = friendsVisibleBean;
    }

    public String getThumbnailPath() {
        return this.thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public String getVideoName() {
        return this.videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public boolean isFromAlbum() {
        return this.fromAlbum;
    }

    public void setFromAlbum(boolean fromAlbum) {
        this.fromAlbum = fromAlbum;
    }

    public String getVideoText() {
        return this.videoText;
    }

    public void setVideoText(String videoText) {
        this.videoText = videoText;
    }

    public PoiBean getPoiBean() {
        return this.poiBean;
    }

    public void setPoiBean(PoiBean poiBean) {
        this.poiBean = poiBean;
    }

    public ShareVideoMoment getShareVideoMoment() {
        return this.shareVideoMoment;
    }

    public void setShareVideoMoment(ShareVideoMoment shareVideoMoment) {
        this.shareVideoMoment = shareVideoMoment;
    }

    public VideoTokenVoResponse getVideoTokenVoResponse() {
        return this.videoTokenVoResponse;
    }

    public void setVideoTokenVoResponse(VideoTokenVoResponse videoTokenVoResponse) {
        this.videoTokenVoResponse = videoTokenVoResponse;
    }

    public int getFromType() {
        return this.fromType;
    }

    public void setFromType(int fromType) {
        this.fromType = fromType;
    }

    public FriendsVisibleBean getFriendsVisibleBean() {
        return this.friendsVisibleBean;
    }

    public void setFriendsVisibleBean(FriendsVisibleBean friendsVisibleBean) {
        this.friendsVisibleBean = friendsVisibleBean;
    }

    @Override
    public String toString() {
        return "SendVideoParam{thumbnailPath='" + this.thumbnailPath + "', videoName='" + this.videoName
                + "', fromAlbum=" + this.fromAlbum + ", videoText='" + this.videoText + "', poiBean=" + this.poiBean
                + ", shareVideoMoment=" + this.shareVideoMoment + ", videoTokenVoResponse=" + this.videoTokenVoResponse
                + ", fromType=" + this.fromType + ", friendsVisibleBean=" + this.friendsVisibleBean + '}';
    }
}