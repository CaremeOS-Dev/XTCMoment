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
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.db.bean.DbTemplate;
import com.xtc.moment.module.Constants;
import com.xtc.moment.module.bean.FriendsVisibleBean;
import com.xtc.moment.net.bean.Moment;
import com.xtc.moment.net.bean.TemplateResponseBean;
import com.xtc.moment.serve.AccountInfoServerImpl;
import com.xtc.moment.serve.MomentServeImpl;
import com.xtc.moment.serve.MomentTemplateServeImpl;
import com.xtc.moment.util.BeanConverterUtil;
import com.xtc.moment.util.TimeUtils;
import com.xtc.moment.util.ToastUtil;
import com.xtc.utils.encode.JSONUtil;
import com.xtc.utils.system.NetworkUtils;

import org.greenrobot.eventbus.EventBus;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MomentProvider extends ContentProvider {

    private static final String TAG = "MomentProvider";
    private static final String KEY_CONTENT = "content";
    private static final String KEY_MOMENT_ID = "momentId";
    private static final String QUERY_KEY_TYPE = "type=";

    private static final UriMatcher uriMatcher = new UriMatcher(-1);

    private Context mContext;

    static {
        uriMatcher.addURI(Constants.ProviderConstants.MOMENT_PROVIDER_AUTHORITY, Constants.ProviderConstants.MOMENT_ITEM_MOOD_PATH, 0);
        uriMatcher.addURI(Constants.ProviderConstants.MOMENT_PROVIDER_AUTHORITY, Constants.ProviderConstants.MOMENT_ITEM_STATE_PATH, 1);
        uriMatcher.addURI(Constants.ProviderConstants.MOMENT_PROVIDER_AUTHORITY, Constants.ProviderConstants.MOMENT_ITEM_LOCATION_PATH, 2);
        uriMatcher.addURI(Constants.ProviderConstants.MOMENT_PROVIDER_AUTHORITY, Constants.ProviderConstants.MOMENT_ITEM_WORD_PATH, 3);
        uriMatcher.addURI(Constants.ProviderConstants.MOMENT_PROVIDER_AUTHORITY, Constants.ProviderConstants.MOMENT_ITEM_PHOTO_PATH, 5);
        uriMatcher.addURI(Constants.ProviderConstants.MOMENT_PROVIDER_AUTHORITY, "video", 6);
        uriMatcher.addURI(Constants.ProviderConstants.MOMENT_PROVIDER_AUTHORITY, Constants.ProviderConstants.TEST_PATH, 100);
        uriMatcher.addURI(Constants.ProviderConstants.MOMENT_PROVIDER_AUTHORITY, "item", 101);
        uriMatcher.addURI(Constants.ProviderConstants.MOMENT_PROVIDER_AUTHORITY, Constants.ProviderConstants.MOMENT_UNREAD_PATH, 106);
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        if (context == null) {
            LogUtil.w(TAG, "onCreate: context is null!");
            return false;
        }
        mContext = context.getApplicationContext();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        DatabaseHelper databaseHelper = MomentDbManager.getInstance(mContext).getDatabaseHelper();
        ContentResolver contentResolver = mContext.getContentResolver();
        if (databaseHelper == null || contentResolver == null) {
            LogUtil.w(TAG, "query: DatabaseHelper or ContentResolver is null");
            return null;
        }
        SQLiteDatabase readableDatabase = databaseHelper.getReadableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables("moment");
        int match = uriMatcher.match(uri);
        if (match == 0) {
            queryBuilder.appendWhere("type=0");
        } else if (match == 1) {
            queryBuilder.appendWhere("type=1");
        } else if (match == 2) {
            queryBuilder.appendWhere("type=2");
        } else if (match == 3) {
            queryBuilder.appendWhere("type=3");
        } else if (match == 5) {
            queryBuilder.appendWhere("type=5");
        } else if (match == 6) {
            queryBuilder.appendWhere("type=6");
        } else if (match == 100) {
            MatrixCursor matrixCursor = new MatrixCursor(new String[]{"result"});
            matrixCursor.newRow().add(1);
            return matrixCursor;
        } else if (match == 106) {
            String watchId = MomentApp.getWatchId();
            if (TextUtils.isEmpty(watchId)) {
                watchId = "";
            }
            long uncheckedCount = MomentServeImpl.getInstance(mContext).getOthersUncheckedMomentCount(watchId);
            LogUtil.i(TAG, "dealUnreadPoint: uncheckedCount = " + uncheckedCount);
            MatrixCursor matrixCursor = new MatrixCursor(new String[]{"result"});
            matrixCursor.newRow().add(Long.valueOf(uncheckedCount));
            return matrixCursor;
        } else {
            LogUtil.e(TAG, "error uri: " + uri);
            return null;
        }
        Cursor cursor = queryBuilder.query(readableDatabase, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(contentResolver, uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (uriMatcher.match(uri) != 0) {
            LogUtil.i(TAG, "insert: 非心情类动态");
            return null;
        }
        if (values == null) {
            LogUtil.e(TAG, "insert: ContentValues为空");
            return null;
        }
        return publishMoodMomentOrComment(values);
    }

    private Uri publishMoodMomentOrComment(final ContentValues values) {
        Observable.just(Boolean.FALSE).map(new Func1<Boolean, Boolean>() {
            @Override
            public Boolean call(Boolean aBoolean) {
                String momentId = values.getAsString(KEY_MOMENT_ID);
                if (!TextUtils.isEmpty(momentId)) {
                    DbMoment moment = MomentServeImpl.getInstance(mContext).getMomentByMomentIdWithoutComment(momentId);
                    if (moment == null) {
                        LogUtil.i(TAG, "publishMoodMomentOrComment: the moment is not in database");
                        return Boolean.FALSE;
                    }
                    return Boolean.valueOf(TimeUtils.isSameDay(moment.getCreateTime().longValue()));
                }
                LogUtil.i(TAG, "publishMoodMomentOrComment: momentId is empty");
                return Boolean.FALSE;
            }
        }).subscribeOn(Schedulers.io()).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean isSameDay) {
                if (!isSameDay.booleanValue()) {
                    checkMomentTemplate(values.getAsString(KEY_CONTENT));
                } else {
                    mContext.getContentResolver().insert(Uri.parse("content://com.xtc.moment.commentProvider/comment"), values);
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LogUtil.e(TAG, "publishMoodMomentOrComment#error", throwable);
            }
        });
        return Uri.parse("content://com.xtc.moment.momentProvider/mood");
    }
    private void checkMomentTemplate(final String content) {
        Observable.just(Boolean.FALSE).map(new Func1<Boolean, Long>() {
            @Override
            public Long call(Boolean aBoolean) {
                return Long.valueOf(MomentTemplateServeImpl.getInstance(mContext).getTemplatesCount());
            }
        }).subscribeOn(Schedulers.io()).subscribe(new Action1<Long>() {
            @Override
            public void call(Long count) {
                if (count.longValue() <= 0) {
                    getTemplatesAndPublishMoment(content);
                } else {
                    publishMoment(content);
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LogUtil.e(TAG, "checkMomentTemplate#error", throwable);
            }
        });
    }

    private void getTemplatesAndPublishMoment(final String content) {
        MomentTemplateServeImpl.getInstance(mContext).getMomentTemplateFromNet(0L, 100L, 0L).map(new Func1<TemplateResponseBean, Boolean>() {
            @Override
            public Boolean call(TemplateResponseBean responseBean) {
                MomentTemplateServeImpl.getInstance(mContext).updateTemplates(responseBean.getResources());
                return Boolean.TRUE;
            }
        }).map(new Func1<Boolean, DbTemplate>() {
            @Override
            public DbTemplate call(Boolean aBoolean) {
                return MomentTemplateServeImpl.getInstance(mContext).getTemplateByContent(content);
            }
        }).filter(new Func1<DbTemplate, Boolean>() {
            @Override
            public Boolean call(DbTemplate template) {
                return Boolean.valueOf(template != null);
            }
        }).flatMap(new Func1<DbTemplate, Observable<Moment>>() {
            @Override
            public Observable<Moment> call(DbTemplate template) {
                return MomentServeImpl.getInstance(mContext).publishMoment(0, template.getResource(), template.getResourceId(), template.getContent(), (FriendsVisibleBean) null);
            }
        }).map(new Func1<Moment, DbMoment>() {
            @Override
            public DbMoment call(Moment moment) {
                DbMoment dbMoment = BeanConverterUtil.convertToDbMoment(moment);
                dbMoment.setEnableLike(!MomentServeImpl.getInstance(mContext).isLiked(dbMoment));
                String name = AccountInfoServerImpl.getInstance(mContext).getWatchAccountInfo().getName(mContext);
                if (TextUtils.isEmpty(name)) {
                    name = mContext.getString(R.string.unknown_watch);
                }
                dbMoment.setName(name);
                MomentServeImpl.getInstance(mContext).insertMomentByMomentId(dbMoment);
                return dbMoment;
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<DbMoment>() {
            @Override
            public void call(DbMoment dbMoment) {
                LogUtil.i(TAG, "getTemplatesAndPublishMoment: 发布心情成功");
                Uri.Builder builder = Uri.parse("content://com.xtc.moment.momentProvider/mood").buildUpon();
                builder.appendQueryParameter("type", Constants.QueryParameter.NEW_MOMENT_BY_CONTENT_PROVIDER);
                builder.appendQueryParameter("data", JSONUtil.toJSON(dbMoment));
                mContext.getContentResolver().notifyChange(builder.build(), null);
                EventBus.getDefault().post(dbMoment);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LogUtil.e(TAG, "getTemplatesAndPublishMoment#error", throwable);
                if (throwable == null) {
                    ToastUtil.showShortCover(mContext, mContext.getString(R.string.publish_fail));
                    return;
                }
                String message = throwable.getMessage();
                if ("000060".equals(message)) {
                    ToastUtil.showShortCover(mContext, mContext.getString(R.string.publish_limit));
                } else if ("000061".equals(message)) {
                    ToastUtil.showShortCover(mContext, mContext.getString(R.string.publish_sensitive));
                } else if (!NetworkUtils.isNetworkAvailable(mContext)) {
                    ToastUtil.showShortCover(mContext, mContext.getString(R.string.net_work_exception));
                } else if (TextUtils.isEmpty(message) || (!message.contains("1003") && !message.contains("1002"))) {
                    ToastUtil.showShortCover(mContext, mContext.getString(R.string.publish_fail));
                } else {
                    ToastUtil.showShortCover(mContext, mContext.getString(R.string.frequent_request));
                }
            }
        });
    }

    private void publishMoment(final String content) {
        Observable.just(Boolean.FALSE).map(new Func1<Boolean, DbTemplate>() {
            @Override
            public DbTemplate call(Boolean aBoolean) {
                return MomentTemplateServeImpl.getInstance(mContext).getTemplateByContent(content);
            }
        }).filter(new Func1<DbTemplate, Boolean>() {
            @Override
            public Boolean call(DbTemplate template) {
                return Boolean.valueOf(template != null);
            }
        }).flatMap(new Func1<DbTemplate, Observable<Moment>>() {
            @Override
            public Observable<Moment> call(DbTemplate template) {
                return MomentServeImpl.getInstance(mContext).publishMoment(0, template.getResource(), template.getResourceId(), template.getContent(), (FriendsVisibleBean) null);
            }
        }).map(new Func1<Moment, DbMoment>() {
            @Override
            public DbMoment call(Moment moment) {
                DbMoment dbMoment = BeanConverterUtil.convertToDbMoment(moment);
                String name = AccountInfoServerImpl.getInstance(mContext).getWatchAccountInfo().getName(mContext);
                if (TextUtils.isEmpty(name)) {
                    name = mContext.getString(R.string.unknown_watch);
                }
                dbMoment.setName(name);
                MomentServeImpl.getInstance(mContext).insertMomentByMomentId(dbMoment);
                return dbMoment;
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<DbMoment>() {
            @Override
            public void call(DbMoment dbMoment) {
                LogUtil.i(TAG, "checkMomentTemplate: 发布表情成功");
                Uri.Builder builder = Uri.parse("content://com.xtc.moment.momentProvider/mood").buildUpon();
                builder.appendQueryParameter("type", Constants.QueryParameter.NEW_MOMENT_BY_CONTENT_PROVIDER);
                builder.appendQueryParameter("data", JSONUtil.toJSON(dbMoment));
                mContext.getContentResolver().notifyChange(builder.build(), null);
                EventBus.getDefault().post(dbMoment);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LogUtil.e(TAG, "checkMomentTemplate#error", throwable);
                if (throwable == null) {
                    ToastUtil.showShortCover(mContext, mContext.getString(R.string.publish_fail));
                    return;
                }
                String message = throwable.getMessage();
                if ("4".equals(message)) {
                    ToastUtil.showShortCover(mContext, mContext.getString(R.string.publish_comment_limit));
                } else if ("2".equals(message)) {
                    ToastUtil.showShortCover(mContext, mContext.getString(R.string.publish_invalidate));
                } else if (!NetworkUtils.isNetworkAvailable(mContext)) {
                    ToastUtil.showShortCover(mContext, mContext.getString(R.string.net_work_exception));
                } else if (TextUtils.isEmpty(message) || (!message.contains("1003") && !message.contains("1002"))) {
                    ToastUtil.showShortCover(mContext, mContext.getString(R.string.publish_fail));
                } else {
                    ToastUtil.showShortCover(mContext, mContext.getString(R.string.frequent_request));
                }
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