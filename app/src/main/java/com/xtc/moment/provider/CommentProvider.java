package com.xtc.moment.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.xtc.database.ormlite.DatabaseHelper;
import com.xtc.dispatch.TaskDispatcher;
import com.xtc.dispatch.task.AbsTask;
import com.xtc.log.LogUtil;
import com.xtc.moment.MomentApp;
import com.xtc.moment.R;
import com.xtc.moment.data.MomentsRepository;
import com.xtc.moment.data.local.MomentsLocalDataSource;
import com.xtc.moment.data.remote.MomentsRemoteDataSource;
import com.xtc.moment.db.MomentDbManager;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.db.bean.DbMomentComment;
import com.xtc.moment.module.Constants;
import com.xtc.moment.module.bean.CommentEvent;
import com.xtc.moment.provider.bean.MoodComment;
import com.xtc.moment.serve.AccountInfoServerImpl;
import com.xtc.moment.serve.MomentServeImpl;
import com.xtc.moment.tasks.domain.usecase.PublishCommentTask;
import com.xtc.moment.util.AssetFileUtil;
import com.xtc.moment.util.PublishErrorUtil;
import com.xtc.moment.util.ToastUtil;
import com.xtc.utils.common.CollectionUtil;
import com.xtc.utils.encode.JSONUtil;
import com.xtc.utils.system.NetworkUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class CommentProvider extends ContentProvider {

    private static final String TAG = "CommentProvider";
    private static final String KEY_CONTENT = "content";
    private static final String KEY_MOMENT_ID = "momentId";

    private static final UriMatcher uriMatcher = new UriMatcher(-1);

    private Context mContext;
    private ContentResolver mContentResolver;

    static {
        uriMatcher.addURI(Constants.ProviderConstants.COMMENT_PROVIDER_AUTHORITY, Constants.ProviderConstants.COMMENT_PATH, 201);
        uriMatcher.addURI(Constants.ProviderConstants.COMMENT_PROVIDER_AUTHORITY, Constants.ProviderConstants.COMMENT_UNREAD_PATH, 204);
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        if (context == null) {
            LogUtil.w(TAG, "onCreate: context is null!");
            return false;
        }
        mContext = context.getApplicationContext();
        mContentResolver = mContext.getContentResolver();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        DatabaseHelper databaseHelper = MomentDbManager.getInstance(mContext).getDatabaseHelper();
        if (databaseHelper == null || mContentResolver == null) {
            LogUtil.w(TAG, "query: DatabaseHelper or ContentResolver is null");
            return null;
        }
        SQLiteDatabase readableDatabase = databaseHelper.getReadableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(com.xtc.moment.db.Constants.TableName.MOMENT_COMMENT);
        int match = uriMatcher.match(uri);
        if (match == 201) {
            Cursor cursor = queryBuilder.query(readableDatabase, projection, selection, selectionArgs, null, null, sortOrder);
            cursor.setNotificationUri(mContentResolver, uri);
            return cursor;
        }
        if (match == 204) {
            String watchId = MomentApp.getWatchId();
            if (TextUtils.isEmpty(watchId)) {
                watchId = "";
            }
            int unreadSum = (int) (MomentServeImpl.getInstance(mContext).getUncheckedLikeMessageCountByWatchId(watchId)
                    + MomentServeImpl.getInstance(mContext).getUncheckedCommentCountByWatchId(watchId));
            LogUtil.d(TAG, "unreadSum=" + unreadSum);
            MatrixCursor matrixCursor = new MatrixCursor(new String[]{"result"});
            matrixCursor.newRow().add(Integer.valueOf(unreadSum));
            return matrixCursor;
        }
        LogUtil.e(TAG, "error uri: " + uri);
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (uriMatcher.match(uri) != 201) {
            LogUtil.e(TAG, "error uri: " + uri);
            return null;
        }
        if (values == null) {
            LogUtil.e(TAG, "insert: ContentValues为空");
            return null;
        }
        String momentId = values.getAsString(KEY_MOMENT_ID);
        String content = values.getAsString(KEY_CONTENT);
        if (TextUtils.isEmpty(momentId) || TextUtils.isEmpty(content)) {
            LogUtil.e(TAG, "insert: momentId 或 content 为空");
            return null;
        }
        publishComment(momentId, content);
        return Uri.parse("content://com.xtc.moment.commentProvider/comment");
    }

    private void publishComment(final String momentId, final String content) {
        Observable.just(Boolean.FALSE).map(new Func1<Boolean, String>() {
            @Override
            public String call(Boolean aBoolean) {
                String config = null;
                try {
                    config = AssetFileUtil.decodeConfigFile(mContext.getResources(), "MoodCommentConfig.json");
                } catch (Throwable throwable) {
                    LogUtil.e(TAG, "decodeConfigFile error", throwable);
                }
                if (TextUtils.isEmpty(config)) {
                    return null;
                }
                List<MoodComment> comments = (List<MoodComment>) JSONUtil.fromJSON(config, List.class, MoodComment.class);
                if (TextUtils.isEmpty(content) || CollectionUtil.isEmpty(comments)) {
                    return null;
                }
                for (MoodComment moodComment : comments) {
                    if (content.equals(moodComment.getCommentContent())) {
                        return moodComment.getMoodCommentContent();
                    }
                }
                return null;
            }
        }).filter(new Func1<String, Boolean>() {
            @Override
            public Boolean call(String moodCommentContent) {
                boolean isEmpty = TextUtils.isEmpty(moodCommentContent);
                if (isEmpty) {
                    LogUtil.i(TAG, "filter, content empty");
                }
                return Boolean.valueOf(!isEmpty);
            }
        }).map(new Func1<String, DbMomentComment>() {
            @Override
            public DbMomentComment call(String moodCommentContent) {
                DbMoment moment = MomentServeImpl.getInstance(mContext).getMomentByMomentIdWithoutComment(momentId);
                if (moment != null) {
                    String watchId = AccountInfoServerImpl.getInstance(mContext).getWatchAccountInfo().getWatchId(mContext);
                    DbMomentComment comment = new DbMomentComment();
                    comment.setWatchId(watchId);
                    comment.setComment(moodCommentContent);
                    comment.setChecked(true);
                    comment.setType(1);
                    comment.setMomentId(moment.getMomentId());
                    comment.setMomentWatchId(moment.getWatchId());
                    comment.setMediaType(1);
                    return comment;
                }
                LogUtil.w(TAG, "没有此动态，momentId: " + momentId);
                return null;
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<DbMomentComment>() {
            @Override
            public void call(DbMomentComment comment) {
                if (comment != null) {
                    commentMoment(comment);
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LogUtil.e(TAG, "publishComment#error", throwable);
            }
        });
    }

    private void commentMoment(DbMomentComment comment) {
        final String watchId = AccountInfoServerImpl.getInstance(mContext).getWatchAccountInfo().getWatchId(mContext);
        PublishCommentTask publishCommentTask = new PublishCommentTask(MomentsRepository.getInstance(
                MomentsRemoteDataSource.getInstance(mContext), MomentsLocalDataSource.getInstance(mContext)));
        publishCommentTask.setRequestValues(new PublishCommentTask.RequestValues(comment));
        publishCommentTask.setTaskCallback(new AbsTask.TaskCallback<AbsTask.ResponseValue>() {
            @Override
            protected void onSuccess(AbsTask.ResponseValue responseValue) {
                LogUtil.i(TAG, "commentMoment success");
                if (responseValue instanceof PublishCommentTask.ResponseValue) {
                    PublishCommentTask.ResponseValue value = (PublishCommentTask.ResponseValue) responseValue;
                    DbMomentComment momentComment = value.getMomentComment();
                    momentComment.setWatchName(mContext.getResources().getString(R.string.me));
                    if (!TextUtils.isEmpty(momentComment.getReplyId()) && watchId.equals(momentComment.getReplyId())) {
                        momentComment.setReplyName(mContext.getResources().getString(R.string.me));
                    }
                    Uri.Builder builder = Uri.parse("content://com.xtc.moment.commentProvider/comment").buildUpon();
                    builder.appendQueryParameter("type", "200");
                    builder.appendQueryParameter("data", JSONUtil.toJSON(momentComment));
                    mContentResolver.notifyChange(builder.build(), null);
                    EventBus.getDefault().post(new CommentEvent(value.getResult(), momentComment));
                }
            }

            @Override
            protected void onError(AbsTask.ResponseValue responseValue) {
                LogUtil.e(TAG, "commentMoment#error: " + responseValue);
                if (responseValue instanceof PublishCommentTask.ErrorResponseValue) {
                    String errorCode = ((PublishCommentTask.ErrorResponseValue) responseValue).getErrorCode();
                    if ("4".equals(errorCode)) {
                        ToastUtil.showShortCover(mContext, mContext.getString(R.string.publish_comment_limit));
                    } else if ("2".equals(errorCode)) {
                        ToastUtil.showShortCover(mContext, mContext.getString(R.string.publish_invalidate));
                    } else if (!NetworkUtils.isNetworkAvailable(mContext)) {
                        ToastUtil.showShortCover(mContext, mContext.getString(R.string.net_work_exception));
                    } else if (TextUtils.isEmpty(errorCode) || (!errorCode.contains("1003") && !errorCode.contains("1002"))) {
                        // 复用统一映射：000007 账号异常，其余未识别码仍提示发布失败。
                        ToastUtil.showShortCover(mContext,
                                mContext.getString(PublishErrorUtil.getFailMessageRes(errorCode)));
                    } else {
                        ToastUtil.showShortCover(mContext, mContext.getString(R.string.frequent_request));
                    }
                }
            }

            @Override
            protected void onError() {
                LogUtil.e(TAG, "commentMoment#error");
                if (!NetworkUtils.isNetworkAvailable(mContext)) {
                    ToastUtil.showShortCover(mContext, mContext.getString(R.string.net_work_exception));
                } else {
                    ToastUtil.showShortCover(mContext, mContext.getString(R.string.publish_fail));
                }
            }
        });
        TaskDispatcher.dispatchImmediately(publishCommentTask);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
