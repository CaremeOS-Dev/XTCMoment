package com.xtc.moment.prerogative.handler;

import android.content.Context;
import android.text.TextUtils;

import com.xtc.database.ormlite.CollectionUtil;
import com.xtc.log.LogUtil;
import com.xtc.moment.db.bean.prerogative.AbsPrerogativeBean;
import com.xtc.moment.db.dao.prerogative.IPrerogativeDao;
import com.xtc.moment.module.Constants;
import com.xtc.moment.module.prerogative.bean.EmotionsEntity;
import com.xtc.moment.module.prerogative.bean.ResourceNetResponse;
import com.xtc.moment.prerogative.MomentPrerogativeServeImpl;
import com.xtc.moment.serve.DownloadServe;
import com.xtc.moment.util.ZipUtil;
import com.xtc.utils.encode.JSONUtil;
import com.xtc.utils.storage.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * 特权资源处理基类：下载 zip、解压、读取 desc.json 并写入数据库。
 */
abstract class BasePrerogativeHandler<T extends AbsPrerogativeBean> implements IPrerogativeHandler<T> {

    private final DownloadServe downloadServe = DownloadServe.getInstance();
    protected final Context mContext;
    protected int mCurrentUseEmotionId;
    protected IPrerogativeDao mPrerogativeDao;
    protected List<T> mPrerogativeList;

    protected abstract String getPrerogativeResourceSpData();

    protected abstract String getPrerogativeRootPath();

    protected abstract String getTag();

    protected abstract List<T> readDescInfo(File file);

    protected abstract void savePrerogativeResourceData(ResourceNetResponse resourceNetResponse);

    public BasePrerogativeHandler(Context context) {
        this.mContext = context;
    }

    @Override
    public Boolean initPrerogativeResource(ResourceNetResponse resourceNetResponse) {
        LogUtil.d(getTag(), "initPrerogativeResource");
        String rootPath = getPrerogativeRootPath();
        ResourceNetResponse localResource = JSONUtil.fromJSON(getPrerogativeResourceSpData(), ResourceNetResponse.class);
        if (localResource != null) {
            if (checkRes(rootPath + localResource.getDynamicName(), this.mPrerogativeList) && Objects.equals(localResource.getDynamicVersion(), resourceNetResponse.getDynamicVersion())) {
                LogUtil.e(getTag(), "本地动效文件和数据库条数一致且版本号一致 不做更新下载操作。");
                return true;
            }
        }
        FileUtils.deleteDirectory(rootPath);
        boolean downloadResult = dealDownload(resourceNetResponse, rootPath);
        LogUtil.d(getTag(), "initLocalNetResourceData call: downResult = " + downloadResult);
        if (!downloadResult) {
            return false;
        }
        List<File> files = FileUtils.listFiles(rootPath + resourceNetResponse.getDynamicName(), false);
        File descFile = new File(rootPath + "desc.json");
        if (!descFile.exists()) {
            LogUtil.e(getTag(), "descFile not exit");
            return false;
        }
        List<T> descInfo = readDescInfo(descFile);
        if (CollectionUtil.isEmpty(descInfo)) {
            LogUtil.e(getTag(), "animations isEmpty");
            return false;
        }
        ArrayList<T> result = new ArrayList<>();
        HashMap<String, String> fileMap = new HashMap<>();
        for (File file : files) {
            LogUtil.e(getTag(), "目录存在的文件：file:" + file.getPath());
            if (FileUtils.isFile(file)) {
                fileMap.put(file.getName(), file.getAbsolutePath());
            }
        }
        for (int i = 0; i < descInfo.size(); i++) {
            T item = descInfo.get(i);
            String localPath = fileMap.get(item.getEmotionCode());
            if (!TextUtils.isEmpty(localPath)) {
                item.setLocalEmotionPath(localPath);
                item.setNetDynamicName(resourceNetResponse.getDynamicName());
                item.setNetDynamicUrl(resourceNetResponse.getDynamicUrl());
                item.setNetDynamicVersion(resourceNetResponse.getDynamicVersion());
                LogUtil.d(getTag(), "initPrerogativeResource: " + item);
                result.add(item);
            } else {
                LogUtil.d(getTag(), "目录文件不存在：file: " + localPath);
            }
        }
        int deleteCount = this.mPrerogativeDao.deleteDataForAll();
        LogUtil.d(getTag(), "call: deleteAll size = " + deleteCount);
        boolean inserted = this.mPrerogativeDao.insertList(result);
        LogUtil.d(getTag(), "initLocalNetResourceData call: insertOrUpdate = " + inserted);
        savePrerogativeResourceData(resourceNetResponse);
        this.mPrerogativeList = result;
        MomentPrerogativeServeImpl.isNeedRefreshPersonalPrerogative = true;
        return true;
    }

    @Override
    public int getCurrentUseEmotionId() {
        return this.mCurrentUseEmotionId;
    }

    @Override
    public T getPrerogativeByEmotionId(int emotionId) {
        if (emotionId <= 0) {
            return null;
        }
        if (!CollectionUtil.isEmpty(this.mPrerogativeList)) {
            for (int i = 0; i < this.mPrerogativeList.size(); i++) {
                T item = this.mPrerogativeList.get(i);
                if (item != null && item.getPrerogativeId() == emotionId) {
                    return item;
                }
            }
        }
        return (T) this.mPrerogativeDao.queryDbForPrerogativeId(emotionId);
    }

    @Override
    public List<T> getAllLocalData(boolean refreshCurrentUse) {
        this.mPrerogativeList = this.mPrerogativeDao.queryDataForAll();
        if (refreshCurrentUse) {
            refreshCurrentUseEmotionId();
        }
        return this.mPrerogativeList;
    }

    private void refreshCurrentUseEmotionId() {
        this.mCurrentUseEmotionId = 0;
        if (this.mPrerogativeList == null) {
            return;
        }
        for (int i = 0; i < this.mPrerogativeList.size(); i++) {
            T item = this.mPrerogativeList.get(i);
            if (item != null && item.isUsing() && !item.isOverdue()) {
                this.mCurrentUseEmotionId = item.getPrerogativeId();
                LogUtil.d(getTag(), "mCurrentUseEmotionId = " + this.mCurrentUseEmotionId);
                return;
            }
        }
    }

    @Override
    public void refreshLocalPrerogativeData(HashMap<Integer, EmotionsEntity> emotions) {
        if (CollectionUtil.isEmpty(this.mPrerogativeList)) {
            getAllLocalData(false);
        }
        this.mCurrentUseEmotionId = 0;
        if (this.mPrerogativeList == null || emotions == null) {
            LogUtil.d(getTag(), "refreshLocalPrerogativeData: data invalid");
            return;
        }
        for (int i = 0; i < this.mPrerogativeList.size(); i++) {
            T item = this.mPrerogativeList.get(i);
            EmotionsEntity emotion = emotions.get(Integer.valueOf(item.getPrerogativeId()));
            if (emotion == null) {
                if (!item.isNotHave()) {
                    item.setUseStatus(0);
                    item.setUseTime(0L);
                    item.setExpireMins(0L);
                    item.setExpireTime(0L);
                    this.mPrerogativeDao.updateLocalPrerogativeById(item);
                }
            } else {
                item.setUseStatus(emotion.getStatus());
                item.setUseTime(emotion.getUseTime());
                item.setExpireMins(emotion.getExpireMins());
                item.setExpireTime(emotion.getExpireTime());
                this.mPrerogativeDao.updateLocalPrerogativeById(item);
                if (item.isUsing() && !item.isOverdue()) {
                    this.mCurrentUseEmotionId = item.getPrerogativeId();
                    LogUtil.d(getTag(), "mCurrentUseEmotionId = " + this.mCurrentUseEmotionId);
                }
            }
        }
    }

    @Override
    public boolean hasOverdueData() {
        if (CollectionUtil.isEmpty(this.mPrerogativeList)) {
            return false;
        }
        for (int i = 0; i < this.mPrerogativeList.size(); i++) {
            T item = this.mPrerogativeList.get(i);
            if (item != null && item.isOverdue()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 校验本地动效文件数量与数据库记录数量是否一致。
     */
    protected boolean checkRes(String path, List<?> list) {
        List<File> files = FileUtils.listFiles(path, false);
        int dbSize = CollectionUtil.isEmpty(list) ? 0 : list.size();
        int fileSize = CollectionUtil.isEmpty(files) ? 0 : files.size();
        LogUtil.d(getTag(), "checkRes() called with: name = [" + path + "], dbSize = " + dbSize + ", fileSize = " + fileSize);
        return !(dbSize == 0 || fileSize == 0 || dbSize != fileSize);
    }

    protected boolean dealDownload(ResourceNetResponse resourceNetResponse, String rootPath) {
        return startDownLoadResource(resourceNetResponse, rootPath);
    }

    private boolean startDownLoadResource(ResourceNetResponse resourceNetResponse, String rootPath) {
        String dynamicUrl = resourceNetResponse.getDynamicUrl();
        String zipPath = rootPath + resourceNetResponse.getDynamicName() + "_" + resourceNetResponse.getDynamicVersion() + Constants.Suffix.SUFFIX_ZIP;
        LogUtil.i(getTag(), "startDownLoadResource :" + resourceNetResponse + " resourceName:" + zipPath);
        if (!this.downloadServe.startDownload(dynamicUrl, zipPath, Constants.Suffix.SUFFIX_ZIP)) {
            LogUtil.w(getTag(), "startDownLoadResource fail");
            return false;
        }
        try {
            ZipUtil.unZipFolder(rootPath, zipPath, rootPath);
            List<File> fileList = ZipUtil.getFileList(zipPath, true, false);
            if (!CollectionUtil.isEmpty(fileList)) {
                String firstName = fileList.get(0).getName();
                renameResourceFolder(rootPath, firstName, new File(rootPath + firstName), resourceNetResponse.getDynamicName());
            }
            FileUtils.deleteFile(zipPath);
            LogUtil.w(getTag(), "startDownLoadResource end");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(getTag(), "解压资源出错:", e);
            return false;
        }
    }

    private void renameResourceFolder(String rootPath, String oldName, File sourceFile, String newName) {
        if (sourceFile == null || TextUtils.isEmpty(oldName) || TextUtils.isEmpty(newName)) {
            return;
        }
        LogUtil.e(getTag(), "renameResourceFolder  zipFolderName：" + oldName + "  targetFileFolder:" + sourceFile.getPath() + " newFolderName:" + newName);
        File targetFile = new File(rootPath + newName);
        if (oldName.equals(newName)) {
            return;
        }
        LogUtil.e(getTag(), "renameResourceFolder rename:" + targetFile.getPath());
        if (targetFile.exists()) {
            LogUtil.i(getTag(), "重命名文件夹已存在，删除后重命名  file:" + targetFile.getAbsolutePath());
            FileUtils.deleteFile(targetFile);
        }
        sourceFile.renameTo(targetFile);
    }
}