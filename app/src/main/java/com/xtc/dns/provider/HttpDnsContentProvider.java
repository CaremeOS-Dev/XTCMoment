package com.xtc.dns.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;

import com.xtc.dns.LogTag;
import com.xtc.dns.dnsp.DnsConfig;
import com.xtc.dns.dnsp.DnsServiceImpl;
import com.xtc.dns.dnsp.IDnsService;
import com.xtc.dns.storage.db.DatabaseManager;
import com.xtc.dns.storage.db.DomainModel;
import com.xtc.httplib.constant.Const;
import com.xtc.log.LogUtil;

import java.util.List;
import java.util.Locale;

/**
 * HTTP DNS 内容提供者，对外暴露域名解析查询与维护操作。
 */
public class HttpDnsContentProvider extends ContentProvider {

    private static final String TAG = LogTag.tag("HttpDnsContentProvider");
    private static final int MATCH_DOMAIN_MODEL = 1;

    private final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    IDnsService dnsService;

    @Override
    public boolean onCreate() {
        LogUtil.i(TAG, "onCreate");
        Context context = getContext();
        DnsConfig.loadAsync();
        DatabaseManager.getInstance(context).init();
        this.dnsService = new DnsServiceImpl(context);
        this.uriMatcher.addURI(
                String.format(Locale.ROOT, CourseProviders.AUTHORITY_FORMAT, context.getPackageName()),
                CourseProviders.PATH_DOMAIN_MODEL,
                MATCH_DOMAIN_MODEL);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String callingPackage = getCallingPackage();
        LogUtil.d(TAG, "uri: " + uri.toString() + "; caller:" + callingPackage);
        int match = this.uriMatcher.match(uri);
        LogUtil.d(TAG, "query type:" + match);
        MatrixCursor cursor = null;
        if (match == MATCH_DOMAIN_MODEL) {
            if (!DnsConfig.httpDnsEnabled) {
                LogUtil.i(TAG, "httpdns disable");
                return null;
            }
            cursor = new MatrixCursor(new String[]{
                    CourseProviders.DomainColumn.ID,
                    CourseProviders.DomainColumn.CREATE_TIME,
                    CourseProviders.DomainColumn.DOMAIN,
                    CourseProviders.DomainColumn.DOMAIN_TTL,
                    CourseProviders.DomainColumn.IP,
                    CourseProviders.DomainColumn.IP_TTL,
                    CourseProviders.DomainColumn.PRIORITY,
                    CourseProviders.DomainColumn.SP});
            List<DomainModel> domainList = this.dnsService.resolveDomain(uri.getLastPathSegment());
            if (domainList != null) {
                for (DomainModel domainModel : domainList) {
                    cursor.addRow(new Object[]{
                            domainModel.getId(),
                            domainModel.getCreateTime(),
                            domainModel.getDomain(),
                            domainModel.getDomainTtl(),
                            domainModel.getIp(),
                            domainModel.getIpTtl(),
                            domainModel.getPriority(),
                            domainModel.getSp()});
                }
            }
        }
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        List<String> pathSegments = uri.getPathSegments();
        LogUtil.i(TAG, "getType: args: " + pathSegments);
        if (pathSegments != null && !pathSegments.isEmpty()) {
            String method = pathSegments.get(0);
            int hash = method.hashCode();
            int methodCode = -1;
            if (hash == 94746189 && method.equals(CourseProviders.METHOD_CLEAR)) {
                methodCode = 3;
            } else if (hash == -862978482 && method.equals(CourseProviders.METHOD_CHECK_IP_EXPIRE)) {
                methodCode = 1;
            } else if (hash == 2067065648 && method.equals(CourseProviders.METHOD_CHECK_IP_OVER_TIME)) {
                methodCode = 2;
            } else if (hash == -812545239 && method.equals(CourseProviders.METHOD_DECREASE_IP_PRIORITY)) {
                methodCode = 0;
            }
            switch (methodCode) {
                case 0:
                    if (pathSegments.size() == 2) {
                        this.dnsService.removeDomain(pathSegments.get(1));
                    }
                    break;
                case 1:
                    if (pathSegments.size() == 3) {
                        this.dnsService.saveDomain(pathSegments.get(1), pathSegments.get(2));
                    }
                    break;
                case 2:
                    if (pathSegments.size() == 1) {
                        this.dnsService.init();
                    } else if (pathSegments.size() == 2) {
                        this.dnsService.removeDomain(pathSegments.get(1));
                    }
                    break;
                case 3:
                    this.dnsService.release();
                    break;
                default:
                    break;
            }
        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        LogUtil.d(TAG, Const.INSERT);
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        LogUtil.d(TAG, "delete");
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        LogUtil.d(TAG, "update");
        return 0;
    }
}