package com.xtc.dns.storage.db;

import android.content.Context;

import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.xtc.database.ormlite.OrmLiteDao;
import com.xtc.dns.LogTag;
import com.xtc.log.LogUtil;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * DNS 域名记录 DAO。
 */
public class DomainModelDao extends OrmLiteDao<DomainModel> {

    private static final String TAG = LogTag.tag("DomainModelDao");

    private static final String COLUMN_DOMAIN = "domain";
    private static final String COLUMN_IP = "ip";
    private static final String COLUMN_PRIORITY = "priority";
    private static final String COLUMN_CREATE_TIME = "create_time";

    public DomainModelDao(Context context) {
        super(context, DomainModel.class, DatabaseManager.DB_NAME);
    }

    /** 按域名查询，按优先级排序。 */
    public List<DomainModel> queryByDomain(String domain) {
        return queryByOrder(COLUMN_PRIORITY, COLUMN_DOMAIN, domain, false);
    }

    /** 按域名与 IP 查询单条记录。 */
    public DomainModel queryByDomainAndIp(String domain, String ip) {
        HashMap<String, Object> conditions = new HashMap<>();
        conditions.put(COLUMN_DOMAIN, domain);
        conditions.put(COLUMN_IP, ip);
        return queryForFirst(conditions);
    }

    /** 按 IP 查询。 */
    public List<DomainModel> queryByIp(String ip) {
        return queryByColumnName(COLUMN_IP, ip);
    }

    /** 覆盖式保存某域名的解析记录。 */
    public void saveDomainList(String domain, List<DomainModel> domainList) {
        deleteByDomain(domain);
        insertForBatch(domainList);
    }

    /** 批量更新 IP 记录。 */
    public boolean updateBatch(final List<DomainModel> domainList) {
        if (domainList != null && !domainList.isEmpty()) {
            try {
                return new TransactionManager(getDao().getConnectionSource()).callInTransaction(new Callable<Boolean>() {
                    @Override
                    public Boolean call() {
                        for (DomainModel domainModel : domainList) {
                            DomainModelDao.this.updateBy(domainModel, COLUMN_IP, domainModel.getIp());
                        }
                        return true;
                    }
                });
            } catch (SQLException e) {
                LogUtil.e(TAG, "updateBatch error: ", e);
            }
        }
        return false;
    }

    /** 按域名删除。 */
    public boolean deleteByDomain(String domain) {
        return deleteByColumnName(COLUMN_DOMAIN, domain);
    }

    /** 删除创建时间早于指定时间的记录。 */
    public int deleteBeforeTime(long time) {
        try {
            DeleteBuilder<DomainModel, Integer> deleteBuilder = getDao().deleteBuilder();
            deleteBuilder.where().lt(COLUMN_CREATE_TIME, time);
            return deleteBuilder.delete();
        } catch (SQLException e) {
            LogUtil.e(TAG, "deleteReportByTrigTime: ", e);
            return 0;
        }
    }
}