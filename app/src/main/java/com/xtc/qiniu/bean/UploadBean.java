package com.xtc.qiniu.bean;

import android.content.Context;

import com.xtc.qiniu.ICloudManager;

import java.io.File;

/** One queued upload request. */
public class UploadBean {

    private Context context;
    private File file;
    private String filePath;
    private String key;
    private ICloudManager.OnUpLoadListener onUpLoadListener;
    private int spaceType = -1;
    private String tag;
    private String token;
    private int uploadMethod;

    public UploadBean(String tag, int uploadMethod, Context context, int spaceType, String key, File file,
            ICloudManager.OnUpLoadListener onUpLoadListener) {
        this.tag = tag;
        this.uploadMethod = uploadMethod;
        this.context = context.getApplicationContext();
        this.spaceType = spaceType;
        this.key = key;
        this.file = file;
        this.onUpLoadListener = onUpLoadListener;
    }

    public UploadBean(String tag, int uploadMethod, Context context, String key, String filePath, String token,
            ICloudManager.OnUpLoadListener onUpLoadListener) {
        this.tag = tag;
        this.uploadMethod = uploadMethod;
        this.context = context.getApplicationContext();
        this.key = key;
        this.filePath = filePath;
        this.token = token;
        this.onUpLoadListener = onUpLoadListener;
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getUploadMethod() {
        return this.uploadMethod;
    }

    public void setUploadMethod(int uploadMethod) {
        this.uploadMethod = uploadMethod;
    }

    public Context getContext() {
        return this.context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public int getSpaceType() {
        return this.spaceType;
    }

    public void setSpaceType(int spaceType) {
        this.spaceType = spaceType;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public File getFile() {
        return this.file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public ICloudManager.OnUpLoadListener getOnUpLoadListener() {
        return this.onUpLoadListener;
    }

    public void setOnUpLoadListener(ICloudManager.OnUpLoadListener onUpLoadListener) {
        this.onUpLoadListener = onUpLoadListener;
    }

    @Override
    public String toString() {
        return "UploadBean{tag='" + this.tag + "', uploadMethod=" + this.uploadMethod + ", key='" + this.key + "'}";
    }
}