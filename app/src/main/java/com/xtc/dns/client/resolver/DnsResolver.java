package com.xtc.dns.client.resolver;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import com.xtc.dns.client.DomainInfo;
import com.xtc.dns.client.LogTag;
import com.xtc.dns.client.util.InetAddressValidator;
import com.xtc.log.LogUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Queries the HTTP-DNS content provider. */
public class DnsResolver {

    private static final String TAG = LogTag.tag("DnsResolver");
    private static final String SCHEME_CONTENT = "content";

    private final Uri baseUri;
    private final ContentResolver contentResolver;
    private final InetAddressValidator addressValidator = InetAddressValidator.getInstance();

    public DnsResolver(Context context, String authority) {
        this.contentResolver = context.getContentResolver();
        this.baseUri = new Uri.Builder().scheme(SCHEME_CONTENT).authority(authority).build();
    }

    public void checkIpExpire(String host, String ip) {
        this.contentResolver.getType(this.baseUri.buildUpon().appendPath(CourseProviders.CHECK_IP_EXPIRE)
                .appendPath(host).appendPath(ip).build());
    }

    public void checkIpOverTime() {
        this.contentResolver.getType(this.baseUri.buildUpon().appendPath(CourseProviders.CHECK_IP_OVER_TIME).build());
    }

    public void decreaseIpPriority(String ip) {
        this.contentResolver.getType(this.baseUri.buildUpon().appendPath(CourseProviders.DECREASE_IP_PRIORITY)
                .appendPath(ip).build());
    }

    public void clear() {
        this.contentResolver.getType(this.baseUri.buildUpon().appendPath(CourseProviders.CLEAR).build());
    }

    /** Returns the cached IPs for the host, or null when there is no hint. */
    public List<DomainInfo> queryIp(String host) {
        if (TextUtils.isEmpty(host)) {
            return null;
        }
        if (this.addressValidator.isValid(host)) {
            LogUtil.i(TAG, "已经是ip请求，返回原url");
            return Collections.singletonList(new DomainInfo(host, host));
        }
        Uri uri = this.baseUri.buildUpon().appendPath(CourseProviders.QUERY_IP).appendPath(host).build();
        ArrayList<DomainInfo> result = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = this.contentResolver.query(uri, null, null, null, null);
            if (cursor == null) {
                LogUtil.d(TAG, "cursor == null");
                return null;
            }
            if (cursor.moveToNext()) {
                String ip = cursor.getString(cursor.getColumnIndex("ip"));
                LogUtil.d(TAG, "ip: " + ip);
                result.add(new DomainInfo(host, ip));
            }
        } catch (Exception e) {
            LogUtil.e(TAG, e);
            return result.isEmpty() ? null : result;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        if (result.isEmpty()) {
            LogUtil.i(TAG, "没有HttpDns缓存");
            return null;
        }
        return result;
    }
}