package com.xtc.moment.util;

import android.text.TextUtils;

import com.xtc.log.LogUtil;
import com.xtc.moment.MomentApp;
import com.xtc.moment.constants.ModuleSwitchConstant;
import com.xtc.moment.db.bean.DbLikeMessage;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.db.bean.DbMomentComment;
import com.xtc.moment.module.bean.LivePhotoMsg;
import com.xtc.moment.module.bean.MomentNewMsgBean;
import com.xtc.moment.module.bean.PhotoMsg;
import com.xtc.moment.module.bean.PoiBean;
import com.xtc.moment.module.bean.VideoMsg;
import com.xtc.moment.net.bean.CommentBean;
import com.xtc.moment.net.bean.Moment;
import com.xtc.moment.net.bean.MomentLbs;
import com.xtc.moment.net.bean.MomentLikeVo;
import com.xtc.moment.serve.bean.CommentDeleteBean;
import com.xtc.moment.serve.bean.MomentDeleteBean;
import com.xtc.moment.serve.bean.MomentMessageData;
import com.xtc.moment.util.switchs.ModuleSwitchUtil;
import com.xtc.utils.common.CollectionUtil;
import com.xtc.utils_screenshot_carry_data.ScreenshotUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 网络/数据库实体转换工具。
 */
public class BeanConverterUtil {

    private static final String TAG = "BeanConverterUtil";

    public static DbMoment convertToDbMoment(MomentMessageData messageData) {
        if (messageData == null) {
            return null;
        }
        DbMoment moment = new DbMoment();
        moment.setContent(messageData.getContent());
        moment.setMomentId(messageData.getMomentId());
        moment.setType(Integer.valueOf(messageData.getType()));
        moment.setResource(messageData.getResource());
        moment.setResourceId(Integer.valueOf(messageData.getResourceId()));
        moment.setLikeTotal(0);
        moment.setChecked(false);
        moment.setEnableLike(true);
        moment.setWatchId(messageData.getWatchId());
        moment.setCreateTime(Long.valueOf(messageData.getCreateTime()));
        moment.setEmotionId(messageData.getEmotionId());
        moment.setLocation(messageData.getLocation());
        moment.setMomentLbs(new MomentLbs(0, 0, 0, 0));
        moment.setLbsSwitch(ModuleSwitchUtil.queryModuleSwitchByBoolean(MomentApp.getAppContext(), ModuleSwitchConstant.MODULE_SWITCH_LBS_PUBLISH, false));
        return moment;
    }

    public static DbMoment convertToDbMoment(MomentDeleteBean deleteBean) {
        if (deleteBean == null) {
            return null;
        }
        DbMoment moment = new DbMoment();
        moment.setMomentId(1 == deleteBean.getSelf() ? deleteBean.getMomentId() : deleteBean.getParentId());
        moment.setLikeTotal(0);
        moment.setChecked(false);
        moment.setEnableLike(true);
        moment.setWatchId(deleteBean.getWatchId());
        moment.setLbsSwitch(ModuleSwitchUtil.queryModuleSwitchByBoolean(MomentApp.getAppContext(), ModuleSwitchConstant.MODULE_SWITCH_LBS_PUBLISH, false));
        return moment;
    }

    public static DbMoment convertToDbMoment(Moment moment) {
        if (moment == null) {
            return null;
        }
        DbMoment dbMoment = new DbMoment();
        dbMoment.setChecked(true);
        dbMoment.setContent(moment.getContent());
        dbMoment.setCreateTime(Long.valueOf(moment.getCreateTime()));
        dbMoment.setMomentId(moment.getMomentId());
        dbMoment.setResourceId(Integer.valueOf(moment.getResourceId()));
        dbMoment.setResource(String.valueOf(moment.getResource()));
        dbMoment.setType(Integer.valueOf(moment.getType()));
        dbMoment.setLikeTotal(0);
        dbMoment.setWatchId(moment.getWatchId());
        dbMoment.setEmotionId(moment.getEmotionId());
        dbMoment.setMomentLbs(new MomentLbs(0, 0, 0, 0));
        dbMoment.setLocation(moment.getLocation());
        dbMoment.setLbsSwitch(ModuleSwitchUtil.queryModuleSwitchByBoolean(MomentApp.getAppContext(), ModuleSwitchConstant.MODULE_SWITCH_LBS_PUBLISH, false));
        return dbMoment;
    }

    public static DbMoment convertToDbMoment(Moment moment, int type) {
        if (moment == null) {
            return null;
        }
        DbMoment dbMoment = new DbMoment();
        dbMoment.setChecked(true);
        dbMoment.setContent(moment.getContent());
        dbMoment.setCreateTime(Long.valueOf(moment.getCreateTime()));
        dbMoment.setMomentId(moment.getMomentId());
        dbMoment.setResourceId(Integer.valueOf(moment.getResourceId()));
        dbMoment.setResource(String.valueOf(moment.getResource()));
        dbMoment.setType(Integer.valueOf(type));
        dbMoment.setLikeTotal(0);
        dbMoment.setWatchId(moment.getWatchId());
        dbMoment.setEmotionId(moment.getEmotionId());
        dbMoment.setMomentLbs(new MomentLbs(0, 0, 0, 0));
        dbMoment.setLocation(moment.getLocation());
        dbMoment.setLbsSwitch(ModuleSwitchUtil.queryModuleSwitchByBoolean(MomentApp.getAppContext(), ModuleSwitchConstant.MODULE_SWITCH_LBS_PUBLISH, false));
        return dbMoment;
    }

    public static List<DbLikeMessage> convertToDbLikeMessage(List<MomentLikeVo> likeVos) {
        if (likeVos == null || likeVos.isEmpty()) {
            return null;
        }
        ArrayList<DbLikeMessage> result = new ArrayList<>();
        Iterator<MomentLikeVo> iterator = likeVos.iterator();
        while (iterator.hasNext()) {
            DbLikeMessage likeMessage = convertToDbLikeMessage(iterator.next());
            if (likeMessage != null) {
                result.add(likeMessage);
            }
        }
        return result;
    }

    public static DbLikeMessage convertToDbLikeMessage(MomentLikeVo likeVo) {
        if (likeVo == null) {
            return null;
        }
        DbLikeMessage likeMessage = new DbLikeMessage();
        likeMessage.setCreateTime(Long.valueOf(likeVo.getCreateTime().getTime()));
        likeMessage.setMomentWatchId(likeVo.getMomentWatchId());
        likeMessage.setMomentId(likeVo.getMomentId());
        likeMessage.setWatchId(likeVo.getWatchId());
        likeMessage.setWatchName(likeVo.getWatchName());
        likeMessage.setChecked(true);
        likeMessage.setEmotionId(likeVo.getEmotionId());
        return likeMessage;
    }

    public static PhotoMsg toPhotoMsg(String localPath) {
        if (TextUtils.isEmpty(localPath)) {
            return null;
        }
        PhotoMsg photoMsg = new PhotoMsg();
        photoMsg.setDialogType(5);
        photoMsg.setLocalPath(localPath);
        photoMsg.setPhotoHasDownload(true);
        photoMsg.setTrackMd5Value(ScreenshotUtils.getLocalPathMd5(localPath));
        return photoMsg;
    }

    public static PhotoMsg toPhotoMsg(String localPath, PoiBean poiBean) {
        if (TextUtils.isEmpty(localPath)) {
            return null;
        }
        PhotoMsg photoMsg = new PhotoMsg();
        photoMsg.setDialogType(5);
        photoMsg.setLocalPath(localPath);
        photoMsg.setPhotoHasDownload(true);
        if (poiBean != null) {
            photoMsg.setPoiBean(poiBean);
        }
        photoMsg.setTrackMd5Value(ScreenshotUtils.getLocalPathMd5(localPath));
        return photoMsg;
    }

    public static ArrayList<PhotoMsg> toPhotosMsg(List<String> paths, String content, PoiBean poiBean, ConcurrentHashMap<String, String> compressedPaths) {
        if (CollectionUtil.isEmpty(paths)) {
            LogUtil.i(TAG, "toPhotosMsg error, list is null");
            return null;
        }
        ArrayList<PhotoMsg> result = new ArrayList<>();
        if (paths.size() == 1 && TextUtils.isEmpty(content)) {
            PhotoMsg photoMsg = new PhotoMsg();
            photoMsg.setDialogType(5);
            String path = paths.get(0);
            String compressedPath = compressedPaths.get(path);
            if (!TextUtils.isEmpty(compressedPath)) {
                path = compressedPath;
            }
            photoMsg.setLocalPath(path);
            photoMsg.setPhotoHasDownload(true);
            photoMsg.setContent(content);
            result.add(photoMsg);
            return result;
        }
        for (int i = 0; i < paths.size(); i++) {
            PhotoMsg photoMsg = new PhotoMsg();
            photoMsg.setDialogType(26);
            String path = paths.get(i);
            String compressedPath = compressedPaths.get(path);
            if (!TextUtils.isEmpty(compressedPath)) {
                path = compressedPath;
            }
            photoMsg.setLocalPath(path);
            photoMsg.setPhotoHasDownload(true);
            photoMsg.setContent(content);
            if (poiBean != null) {
                photoMsg.setPoiBean(poiBean);
            }
            result.add(photoMsg);
        }
        return result;
    }

    public static LivePhotoMsg toLivePhotoMsg(String photoPath, String videoPath) {
        if (TextUtils.isEmpty(photoPath)) {
            return null;
        }
        LivePhotoMsg livePhotoMsg = new LivePhotoMsg();
        VideoMsg videoMsg = toVideoMsg(photoPath, videoPath);
        livePhotoMsg.setDialogType(5);
        livePhotoMsg.setLocalPath(photoPath);
        livePhotoMsg.setPhotoHasDownload(true);
        livePhotoMsg.setVideoMsg(videoMsg);
        return livePhotoMsg;
    }

    public static VideoMsg toVideoMsg(String thumbnailPath, String videoPath) {
        if (TextUtils.isEmpty(videoPath) || TextUtils.isEmpty(thumbnailPath)) {
            return null;
        }
        VideoMsg videoMsg = new VideoMsg();
        videoMsg.setLocalVideoPath(videoPath);
        videoMsg.setLocalThumbnailPath(thumbnailPath);
        videoMsg.setThumnailHasDownload(true);
        videoMsg.setVideoHasDownload(true);
        videoMsg.setFromAlbum(true);
        return videoMsg;
    }

    public static MomentNewMsgBean<DbMoment> toMomentNewMsgBean(DbMomentComment comment, DbMoment moment) {
        MomentNewMsgBean<DbMoment> bean = new MomentNewMsgBean<>();
        bean.setBean(moment);
        bean.setType(2);
        bean.setCreateTime(comment.getCreateTime());
        bean.setCommentName(comment.getWatchName());
        bean.setCommentWatchId(comment.getWatchId());
        bean.setMediaType(comment.getMediaType());
        bean.setContent(comment.getComment());
        bean.setResourceId(comment.getResourceId());
        bean.setMomentId(comment.getMomentId());
        return bean;
    }

    public static MomentNewMsgBean<DbMoment> toMomentNewMsgBean(DbLikeMessage likeMessage, DbMoment moment) {
        MomentNewMsgBean<DbMoment> bean = new MomentNewMsgBean<>();
        bean.setBean(moment);
        bean.setType(1);
        bean.setCreateTime(likeMessage.getCreateTime());
        bean.setCommentName(likeMessage.getWatchName());
        bean.setCommentWatchId(likeMessage.getWatchId());
        bean.setMomentId(likeMessage.getMomentId());
        bean.setEmotionId(likeMessage.getEmotionId());
        return bean;
    }
    public static DbMomentComment convertToDbMomentComment(CommentBean commentBean, DbMomentComment source) {
        DbMomentComment comment = new DbMomentComment();
        comment.setCommentId(commentBean.getCommentId());
        comment.setMomentId(commentBean.getMomentId());
        comment.setCreateTime(Long.valueOf(commentBean.getCreateTime()));
        comment.setWatchId(commentBean.getWatchId());
        comment.setWatchName(commentBean.getWatchName());
        comment.setReplyId(commentBean.getReplyId());
        comment.setReplyName(commentBean.getReplyName());
        comment.setComment(commentBean.getComment());
        comment.setChecked(true);
        comment.setType(source.getType());
        comment.setMomentWatchId(source.getMomentWatchId());
        comment.setMediaType(source.getMediaType());
        comment.setParentWatchId(commentBean.getParentWatchId());
        if (comment.getMomentWatchId().length() < 40) {
            comment.setReplyCommentId(commentBean.getReplyCommentId());
            comment.setReplyId(commentBean.getReplyWatchId());
            comment.setReplyName(commentBean.getReplyWatchName());
            comment.setMomentId(commentBean.getAdvertId());
        }
        return comment;
    }

    public static List<DbMomentComment> convertToDbMomentCommentList(List<CommentBean> commentBeans, String momentWatchId) {
        ArrayList<DbMomentComment> result = new ArrayList<>();
        for (int i = 0; i < commentBeans.size(); i++) {
            DbMomentComment comment = new DbMomentComment();
            CommentBean commentBean = commentBeans.get(i);
            comment.setCommentId(commentBean.getCommentId());
            comment.setMomentId(commentBean.getMomentId());
            comment.setCreateTime(Long.valueOf(commentBean.getCreateTime()));
            comment.setWatchId(commentBean.getWatchId());
            comment.setWatchName(commentBean.getWatchName());
            comment.setReplyId(commentBean.getReplyId());
            comment.setReplyName(commentBean.getReplyName());
            comment.setComment(commentBean.getComment());
            comment.setChecked(true);
            comment.setType(1);
            comment.setMomentWatchId(momentWatchId);
            comment.setParentWatchId(commentBean.getParentWatchId());
            comment.setMediaType(1);
            if (comment.getMomentWatchId().length() < 40) {
                comment.setReplyCommentId(commentBean.getReplyCommentId());
                comment.setReplyId(commentBean.getReplyWatchId());
                comment.setReplyName(commentBean.getReplyWatchName());
                comment.setMomentId(commentBean.getAdvertId());
                comment.setParentWatchId(commentBean.getParentWatchId());
            }
            result.add(comment);
        }
        return result;
    }

    public static DbMomentComment convertToDbMomentCommentBean(CommentBean commentBean) {
        LogUtil.i("convertToDbMomentCommentBean", "" + commentBean);
        DbMomentComment comment = new DbMomentComment();
        comment.setCommentId(commentBean.getCommentId());
        comment.setCreateTime(Long.valueOf(commentBean.getCreateTime()));
        comment.setWatchId(commentBean.getWatchId());
        comment.setWatchName(commentBean.getWatchName());
        comment.setReplyCommentId(commentBean.getReplyCommentId());
        comment.setReplyId(commentBean.getReplyWatchId());
        comment.setReplyName(commentBean.getReplyWatchName());
        comment.setMomentId(commentBean.getAdvertId());
        comment.setComment(commentBean.getComment());
        comment.setChecked(false);
        comment.setParentWatchId(commentBean.getParentWatchId());
        comment.setType(1);
        comment.setMomentWatchId(commentBean.getReplyWatchId());
        comment.setMediaType(1);
        return comment;
    }

    public static List<DbMomentComment> mapCommentList(List<DbMoment> moments) {
        if (moments != null && moments.size() > 0) {
            ArrayList<DbMomentComment> result = new ArrayList<>();
            for (DbMoment moment : moments) {
                List<DbMomentComment> comments = moment.getComments();
                if (comments != null && comments.size() > 0) {
                    for (DbMomentComment comment : comments) {
                        comment.setMomentWatchId(moment.getWatchId());
                        int type = 1;
                        comment.setChecked(true);
                        comment.setMediaType(1);
                        if (!TextUtils.isEmpty(comment.getReplyId()) && !TextUtils.isEmpty(comment.getReplyName())) {
                            type = 2;
                        }
                        comment.setType(type);
                        result.add(comment);
                    }
                }
            }
            Utils.logSize(TAG, "mapCommentList", result);
            return result;
        }
        return new ArrayList<>();
    }

    public static List<DbMomentComment> mapComment(DbMoment moment) {
        ArrayList<DbMomentComment> result = new ArrayList<>();
        List<DbMomentComment> comments = moment.getComments();
        if (comments != null && comments.size() > 0) {
            for (DbMomentComment comment : comments) {
                comment.setMomentWatchId(moment.getWatchId());
                int type = 1;
                comment.setChecked(true);
                comment.setMediaType(1);
                if (!TextUtils.isEmpty(comment.getReplyId()) && !TextUtils.isEmpty(comment.getReplyName())) {
                    type = 2;
                }
                comment.setType(type);
                result.add(comment);
            }
            LogUtil.i("mapComment", "" + result);
        }
        return result;
    }

    public static List<DbMomentComment> mapMomentToCommentList(DbMoment moment, List<DbMomentComment> comments) {
        ArrayList<DbMomentComment> result = new ArrayList<>();
        if (comments != null && comments.size() > 0) {
            for (DbMomentComment comment : comments) {
                comment.setMomentWatchId(moment.getWatchId());
                int type = 1;
                comment.setChecked(true);
                comment.setMediaType(1);
                if (!TextUtils.isEmpty(comment.getReplyId()) && !TextUtils.isEmpty(comment.getReplyName())) {
                    type = 2;
                }
                comment.setType(type);
                result.add(comment);
            }
            Utils.logSize(TAG, "mapMomentToCommentList ", result);
        }
        return result;
    }

    public static DbMomentComment convertToDbMomentComment(CommentDeleteBean deleteBean) {
        if (deleteBean == null) {
            return null;
        }
        DbMomentComment comment = new DbMomentComment();
        comment.setWatchId(deleteBean.getWatchId());
        comment.setWatchName(deleteBean.getWatchName());
        comment.setMomentId(deleteBean.getMomentId());
        comment.setCommentId(deleteBean.getCommentId());
        return comment;
    }
}