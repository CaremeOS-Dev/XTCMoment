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
import com.xtc.log.LogUtil;
import com.xtc.moment.MomentApp;
import com.xtc.moment.R;
import com.xtc.moment.db.MomentDbManager;
import com.xtc.moment.db.bean.DbLikeMessage;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.module.Constants;
import com.xtc.moment.net.bean.DefaultResponse;
import com.xtc.moment.serve.AccountInfoServerImpl;
import com.xtc.moment.serve.IMomentServe;
import com.xtc.moment.serve.MomentServeImpl;
import com.xtc.moment.util.EventData;
import com.xtc.moment.util.ToastUtil;
import com.xtc.utils.encode.JSONUtil;
import com.xtc.utils.system.NetworkUtils;

import org.greenrobot.eventbus.EventBus;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class LikeMessageProvider extends ContentProvider {

    private static final String TAG = "LikeMessageProvider";
    private static final String KEY_MOMENT_ID = "momentId";

    private static final UriMatcher uriMatcher = new UriMatcher(-1);

    private Context mContext;
    private ContentResolver mContentResolver;

    static {
        uriMatcher.addURI(Constants.ProviderConstants.LIKE_MESSAGE_AUTHORITY, Constants.ProviderConstants.LIKE_MESSAGE_PATH, 301);
        uriMatcher.addURI(Constants.ProviderConstants.LIKE_MESSAGE_AUTHORITY, Constants.ProviderConstants.ALL_LIKE_MESSAGE_UNREAD, 302);
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
        queryBuilder.setTables(com.xtc.moment.db.Constants.TableName.LIKE_MESSAGE_INFO);
        int match = uriMatcher.match(uri);
        if (match == 301) {
            Cursor cursor = queryBuilder.query(readableDatabase, projection, selection, selectionArgs, null, null, sortOrder);
            cursor.setNotificationUri(mContentResolver, uri);
            return cursor;
        }
        if (match == 302) {
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
        if (uriMatcher.match(uri) != 301) {
            LogUtil.e(TAG, "error uri: " + uri);
            return null;
        }
        if (values == null) {
            LogUtil.e(TAG, "insert: ContentValues为空");
            return null;
        }
        String momentId = values.getAsString(KEY_MOMENT_ID);
        if (TextUtils.isEmpty(momentId)) {
            LogUtil.e(TAG, "insert: momentId为空");
            return null;
        }
        praiseMoment(momentId);
        return Uri.parse("content://com.xtc.moment.likeMessageProvider/likeMessage");
    }

    private void praiseMoment(final String momentId) {
        final IMomentServe momentServe = MomentServeImpl.getInstance(mContext);
        Observable.just(Boolean.FALSE).map(new Func1<Boolean, DbMoment>() {
            @Override
            public DbMoment call(Boolean aBoolean) {
                return momentServe.getMomentByMomentIdWithoutComment(momentId);
            }
        }).filter(new Func1<DbMoment, Boolean>() {
            @Override
            public Boolean call(DbMoment moment) {
                boolean exists = moment != null;
                LogUtil.i(TAG, "praiseMoment, isMomentExisted: " + exists + ", momentId: " + momentId);
                return Boolean.valueOf(exists);
            }
        }).flatMap(new Func1<DbMoment, Observable<DefaultResponse>>() {
            @Override
            public Observable<DefaultResponse> call(DbMoment moment) {
                return momentServe.praiseMoment(moment.getMomentId(), moment.getWatchId());
            }
        }).map(new Func1<DefaultResponse, DbLikeMessage>() {
            @Override
            public DbLikeMessage call(DefaultResponse response) {
                DbLikeMessage likeMessage = new DbLikeMessage();
                likeMessage.setChecked(true);
                likeMessage.setWatchName(AccountInfoServerImpl.getInstance(mContext).getWatchAccountInfo().getName(mContext));
                likeMessage.setMomentId(momentId);
                likeMessage.setCreateTime(Long.valueOf(System.currentTimeMillis()));
                likeMessage.setWatchId(AccountInfoServerImpl.getInstance(mContext).getWatchAccountInfo().getWatchId(mContext));
                likeMessage.setMomentWatchId(response.getMomentWatchId());
                if (momentServe.addLikeMessage(likeMessage)) {
                    momentServe.updateMomentByMomentId(momentId);
                }
                return likeMessage;
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<DbLikeMessage>() {
            @Override
            public void call(DbLikeMessage likeMessage) {
                Uri.Builder builder = Uri.parse("content://com.xtc.moment.likeMessageProvider/likeMessage").buildUpon();
                builder.appendQueryParameter("type", Constants.QueryParameter.NEW_LIKE_MESSAGE_BY_CONTENT_PROVIDER);
                builder.appendQueryParameter("data", JSONUtil.toJSON(likeMessage));
                mContentResolver.notifyChange(builder.build(), null);
                EventBus.getDefault().post(new EventData(4, likeMessage));
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LogUtil.e(TAG, "praiseMoment#error", throwable);
                if (throwable == null) {
                    ToastUtil.showShortCover(mContext, mContext.getString(R.string.like_error));
                    return;
                }
                String message = throwable.getMessage();
                if (TextUtils.isEmpty(message) || (!message.contains("1003") && !message.contains("1002"))) {
                    boolean networkAvailable = NetworkUtils.isNetworkAvailable(mContext);
                    LogUtil.d("moment", "点赞失败回调 —— 网络是否可用: " + networkAvailable);
                    if (!networkAvailable) {
                        ToastUtil.showNoConnected(mContext);
                    } else {
                        ToastUtil.showShortCover(mContext, mContext.getString(R.string.like_error));
                    }
                    return;
                }
                ToastUtil.showShortCover(mContext, mContext.getString(R.string.frequent_request));
            }
        });
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