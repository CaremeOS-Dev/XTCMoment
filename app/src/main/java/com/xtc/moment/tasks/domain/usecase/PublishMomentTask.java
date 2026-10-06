package com.xtc.moment.tasks.domain.usecase;

import android.content.Context;
import android.os.SystemClock;
import android.text.TextUtils;

import com.bumptech.glide.util.Preconditions;
import com.xtc.dispatch.task.AbsTask;
import com.xtc.log.LogUtil;
import com.xtc.moment.behavior.DigitalBigDateSender;
import com.xtc.moment.behavior.DigitalManager;
import com.xtc.moment.data.IMomentsDataSource;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.db.bean.prerogative.DbMomentPrerogativeBackground;
import com.xtc.moment.net.bean.Moment;
import com.xtc.moment.prerogative.MomentPrerogativeServeImpl;
import com.xtc.moment.util.BeanConverterUtil;
import com.xtc.utils.encode.JSONUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import rx.Subscriber;

/**
 * 发布动态任务：调用数据仓库发布动态，成功后写入本地库并做埋点统计。
 */
public class PublishMomentTask extends AbsTask<PublishMomentTask.RequestValues, AbsTask.ResponseValue> {

    private Context context;
    private final IMomentsDataSource mMomentsRepository;

    public PublishMomentTask(IMomentsDataSource momentsRepository, Context context) {
        this.context = context;
        this.mMomentsRepository = Preconditions.checkNotNull(momentsRepository, "tasksRepository cannot be null!");
    }

    @Override
    protected void executeTask(final RequestValues requestValues) {
        final long startTime = SystemClock.elapsedRealtime();
        this.mMomentsRepository.publishMoment(requestValues).subscribe(new Subscriber<Moment>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable throwable) {
                LogUtil.e("AbsTask", "publishMoment ErrorMessage = ", throwable);
                TaskCallback<AbsTask.ResponseValue> callback = PublishMomentTask.this.getTaskCallback();
                if (callback != null) {
                    callback.uiError(new ErrorResponseValue(throwable.getMessage()));
                }
                DigitalBigDateSender.onPublicResult(PublishMomentTask.this.context, DigitalManager.getInstance().getDigitalEntity(), false, "9005", throwable);
            }

            @Override
            public void onNext(Moment moment) {
                long transformStart = SystemClock.elapsedRealtime();
                DigitalManager.getInstance().getDigitalEntity().publicTime = String.valueOf(transformStart - startTime);
                DbMoment dbMoment = BeanConverterUtil.convertToDbMoment(moment);
                if (5 == requestValues.getType()) {
                    dbMoment.setContent(JSONUtil.toJSON(requestValues.getContent()));
                }
                if (dbMoment.getEmotionId() != 0 && TextUtils.isEmpty(dbMoment.getMomentBgPath())) {
                    DbMomentPrerogativeBackground background = MomentPrerogativeServeImpl.getInstance(PublishMomentTask.this.context).getPrerogativeBackgroundByEmotionId(dbMoment.getEmotionId());
                    if (background != null && !TextUtils.isEmpty(background.getLocalEmotionPath())) {
                        dbMoment.setMomentBgPath(background.getLocalEmotionPath());
                    }
                    LogUtil.i("AbsTask", "updateLocalMoment" + background.getLocalEmotionPath());
                }
                TaskCallback<AbsTask.ResponseValue> callback = PublishMomentTask.this.getTaskCallback();
                if (callback != null) {
                    LogUtil.d("AbsTask", "onNext: dbMoment = " + (dbMoment == null ? null : dbMoment.getMomentId()));
                    callback.uiSuccess(new ResponseValue(dbMoment));
                }
                PublishMomentTask.this.mMomentsRepository.insertMomentByMomentId(dbMoment);
                DigitalManager.getInstance().getDigitalEntity().transformTime = String.valueOf(SystemClock.elapsedRealtime() - transformStart);
                DigitalManager.getInstance().getDigitalEntity().momentContent = requestValues.content;
                DigitalManager.getInstance().getDigitalEntity().momentType = String.valueOf(3);
                DigitalBigDateSender.onPublicResult(PublishMomentTask.this.context, DigitalManager.getInstance().getDigitalEntity(), true, "", null);
                EventBus.getDefault().post(dbMoment);
            }
        });
    }

    public static class RequestValues implements AbsTask.RequestValues {

        private String content;
        private int emotionId;
        private double latitude;
        private String location;
        private int locationType;
        private double longitude;
        private List<String> lookupIds;
        private String packageName;
        private int permissionType;
        private String resource;
        private int resourceId;
        private int type;
        private String watchId;

        public RequestValues(String watchId, int resourceId, String resource, String content, int type, String packageName, int emotionId) {
            this.watchId = watchId;
            this.resourceId = resourceId;
            this.resource = resource;
            this.content = content;
            this.type = type;
            this.packageName = packageName;
            this.emotionId = emotionId;
        }

        public String getWatchId() {
            return this.watchId;
        }

        public int getResourceId() {
            return this.resourceId;
        }

        public String getResource() {
            return this.resource;
        }

        public String getContent() {
            return this.content;
        }

        public int getType() {
            return this.type;
        }

        public String getPackageName() {
            return this.packageName;
        }

        public String getLocation() {
            return this.location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public int getLocationType() {
            return this.locationType;
        }

        public void setLocationType(int locationType) {
            this.locationType = locationType;
        }

        public double getLongitude() {
            return this.longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public double getLatitude() {
            return this.latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public int getPermissionType() {
            return this.permissionType;
        }

        public void setPermissionType(int permissionType) {
            this.permissionType = permissionType;
        }

        public List<String> getLookupIds() {
            return this.lookupIds;
        }

        public void setLookupIds(List<String> lookupIds) {
            this.lookupIds = lookupIds;
        }

        @Override
        public String toString() {
            return "RequestValues{watchId='" + this.watchId + "', resourceId=" + this.resourceId + ", resource='" + this.resource + "', content='" + this.content + "', type=" + this.type + ", packageName='" + this.packageName + "', permissionType=" + this.permissionType + "', lookupIds='" + this.lookupIds + "', emotionId=" + this.emotionId + '}';
        }
    }

    public static class ResponseValue implements AbsTask.ResponseValue {

        private DbMoment dbMoment;

        public ResponseValue(DbMoment dbMoment) {
            this.dbMoment = dbMoment;
        }

        public DbMoment getDbMoment() {
            return this.dbMoment;
        }

        public void setDbMoment(DbMoment dbMoment) {
            this.dbMoment = dbMoment;
        }

        @Override
        public String toString() {
            return "ResponseValue{dbMoment=" + this.dbMoment + '}';
        }
    }

    public static class ErrorResponseValue implements AbsTask.ResponseValue {

        private String errorCode;

        public ErrorResponseValue(String errorCode) {
            this.errorCode = errorCode;
        }

        public String getErrorCode() {
            return this.errorCode;
        }

        public void setErrorCode(String errorCode) {
            this.errorCode = errorCode;
        }

        @Override
        public String toString() {
            return "ErrorResponseValue{errorCode='" + this.errorCode + "'}";
        }
    }
}