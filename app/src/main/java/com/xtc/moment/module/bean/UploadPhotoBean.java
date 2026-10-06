package com.xtc.moment.module.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 图片上传过程中的临时数据：本地临时路径与对应的上传 token 参数。
 */
public class UploadPhotoBean {

    private String tmpPath;
    private PhotoTokenParam photoTokenParam;
    private List<PhotoTokenParam> photoTokenParams;
    private ArrayList<String> tmpPaths;

    public List<PhotoTokenParam> getmPhotoTokenParams() {
        return this.photoTokenParams;
    }

    public void setmPhotoTokenParams(List<PhotoTokenParam> photoTokenParams) {
        this.photoTokenParams = photoTokenParams;
    }

    public ArrayList<String> getmTmpPaths() {
        return this.tmpPaths;
    }

    public void setmTmpPaths(ArrayList<String> tmpPaths) {
        this.tmpPaths = tmpPaths;
    }

    public PhotoTokenParam getPhotoTokenParam() {
        return this.photoTokenParam;
    }

    public void setPhotoTokenParam(PhotoTokenParam photoTokenParam) {
        this.photoTokenParam = photoTokenParam;
    }

    public String getTmpPath() {
        return this.tmpPath;
    }

    public void setTmpPath(String tmpPath) {
        this.tmpPath = tmpPath;
    }

    @Override
    public String toString() {
        return "UploadPhotoBean{mTmpPath='" + this.tmpPath + "', mPhotoTokenParam=" + this.photoTokenParam + '}';
    }
}