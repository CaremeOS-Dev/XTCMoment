package com.xtc.moment.net.bean;

import android.text.TextUtils;

import com.xtc.bigdata.common.utils.ContextUtils;
import com.xtc.log.LogUtil;
import com.xtc.moment.data.local.MomentsLocalDataSource;
import com.xtc.moment.db.bean.DbAdvertCloseRecord;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.module.bean.CloudFileResource;
import com.xtc.moment.module.bean.MomentAdvertise;
import com.xtc.moment.module.bean.VideoMsg;
import com.xtc.utils.encode.JSONUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 动态列表响应体：包含好友动态与广告动态。
 */
public class Moments {

    private static final String TAG = "Moments";
    private static final long NEVER_EXPIRE = Long.MAX_VALUE;

    private List<MomentAdvertise> adverts;
    private Object likeNotify;
    private List<DbMoment> watchMoments;

    public Object getLikeNotify() {
        return this.likeNotify;
    }

    public void setLikeNotify(Object likeNotify) {
        this.likeNotify = likeNotify;
    }

    public List<DbMoment> getWatchMoments() {
        List<DbMoment> advertMoments = toDbMoment(this.adverts);
        if (advertMoments != null && advertMoments.size() > 0) {
            List<DbMoment> localMoments = this.watchMoments;
            if (localMoments == null || localMoments.size() <= 0) {
                return advertMoments;
            }
            this.watchMoments.addAll(advertMoments);
        }
        return this.watchMoments;
    }

    /**
     * 把广告数据转换成动态实体，同时过滤掉用户已关闭过的广告。
     */
    private List<DbMoment> toDbMoment(List<MomentAdvertise> adverts) {
        if (adverts == null || adverts.size() <= 0) {
            return null;
        }
        ArrayList<DbAdvertCloseRecord> closedRecords = new ArrayList<>();
        ArrayList<DbMoment> result = new ArrayList<>();
        for (int i = 0; i < adverts.size(); i++) {
            MomentAdvertise advert = adverts.get(i);
            if (!TextUtils.isEmpty(advert.getAdvertId())) {
                DbAdvertCloseRecord closeRecord = MomentsLocalDataSource.getInstance(ContextUtils.getContext()).getByAdvertId(advert.getAdvertId());
                if (closeRecord != null) {
                    closedRecords.add(closeRecord);
                    LogUtil.d(TAG, "拦截被关闭过的广告，并添加到关闭记录中——> " + closeRecord);
                } else {
                    DbMoment moment = new DbMoment();
                    moment.setMomentId(advert.getAdvertId());
                    moment.setWatchId(advert.getPublisherId());
                    moment.setChecked(true);
                    moment.setSkiped(false);
                    moment.setPreviewed(false);
                    moment.setName(advert.getName());
                    moment.setDescription(advert.getContent());
                    moment.setIconPath(advert.getIcon());
                    moment.setCreateTime(Long.valueOf(advert.getPublishTime()));
                    if (advert.getType() == 0) {
                        moment.setType(10);
                    } else if (1 == advert.getType()) {
                        moment.setType(11);
                    } else if (2 == advert.getType()) {
                        moment.setType(12);
                    } else if (3 == advert.getType()) {
                        moment.setType(13);
                        moment.setContent(createVideoMsg(advert));
                        moment.setDataUrl(advert.getDataUrl());
                    } else if (4 == advert.getType()) {
                        moment.setType(14);
                        moment.setDataUrl(advert.getDataUrl());
                    } else {
                        LogUtil.d(TAG, "当前广告类型不支持显示");
                    }
                    moment.setResource(advert.getResource());
                    moment.setScaleType(advert.getScaleType());
                    moment.setLikeTotal(Integer.valueOf(advert.getLikeTotal()));
                    moment.setEnableLike(true);
                    moment.setTop(advert.getTop());
                    moment.setTopExpireTime(advert.getTopExpireTime());
                    moment.setOfficial(advert.getOfficial());
                    moment.setComments(advert.getComments());
                    result.add(moment);
                }
            }
        }
        MomentsLocalDataSource.getInstance(ContextUtils.getContext()).deleteAll();
        if (closedRecords.size() > 0) {
            Iterator<DbAdvertCloseRecord> iterator = closedRecords.iterator();
            while (iterator.hasNext()) {
                MomentsLocalDataSource.getInstance(ContextUtils.getContext()).addAdvertCloseRecord(iterator.next());
            }
        }
        return result;
    }

    private String createVideoMsg(MomentAdvertise advert) {
        VideoMsg videoMsg = new VideoMsg();
        CloudFileResource source = new CloudFileResource("", advert.getResource(), NEVER_EXPIRE);
        videoMsg.setSource(source);
        videoMsg.setIcon(new CloudFileResource("", advert.getDataUrl(), NEVER_EXPIRE));
        videoMsg.setTransfer(source);
        videoMsg.setLocalThumbnailPath(advert.getDataUrl());
        videoMsg.setLocalVideoPath(advert.getResource());
        return JSONUtil.toJSON(videoMsg);
    }

    public void setWatchMoments(List<DbMoment> watchMoments) {
        this.watchMoments = watchMoments;
    }

    public List<MomentAdvertise> getAdverts() {
        return this.adverts;
    }

    public void setAdverts(List<MomentAdvertise> adverts) {
        this.adverts = adverts;
    }

    @Override
    public String toString() {
        return "Moments{likeNotify=" + this.likeNotify + ", watchMoments=" + this.watchMoments + ", adverts=" + this.adverts + '}';
    }
}