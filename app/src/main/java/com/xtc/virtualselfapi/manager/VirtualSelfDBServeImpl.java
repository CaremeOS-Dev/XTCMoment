package com.xtc.virtualselfapi.manager;

import android.content.Context;
import android.util.ArrayMap;

import com.xtc.database.ormlite.CollectionUtil;
import com.xtc.database.ormlite.RxDao;
import com.xtc.log.LogUtil;
import com.xtc.virtualselfapi.bean.db.DbCostume;
import com.xtc.virtualselfapi.bean.db.DbDanger;
import com.xtc.virtualselfapi.bean.db.DbDecorate;
import com.xtc.virtualselfapi.bean.db.DbPosition;

import java.util.List;

/**
 * 虚拟形象数据库服务实现。
 */
public class VirtualSelfDBServeImpl {

    private static final String TAG = "Virtual_Self_Api_VirtualSelfDBServeImpl";

    private static VirtualSelfDBServeImpl instance;

    private final RxDao<DbCostume> costumeDao;
    private final RxDao<DbDanger> dangerDao;
    private final RxDao<DbDecorate> decorateDao;
    private final RxDao<DbPosition> positionDao;

    public static VirtualSelfDBServeImpl getInstance() {
        if (instance == null) {
            synchronized (VirtualSelfDBServeImpl.class) {
                if (instance == null) {
                    instance = new VirtualSelfDBServeImpl(VirtualSelfInitManager.getInstance().getAppContext());
                }
            }
        }
        return instance;
    }

    private VirtualSelfDBServeImpl(Context context) {
        String databaseName = VirtualSelfInitManager.getInstance().getDatabaseName();
        this.decorateDao = new RxDao<>(context, DbDecorate.class, databaseName);
        this.dangerDao = new RxDao<>(context, DbDanger.class, databaseName);
        this.costumeDao = new RxDao<>(context, DbCostume.class, databaseName);
        this.positionDao = new RxDao<>(context, DbPosition.class, databaseName);
    }

    public boolean clearCostumeData() {
        return this.costumeDao.clearTableData();
    }

    public boolean insertCostumeForBatch(List<DbCostume> list) {
        return this.costumeDao.insertForBatch(list);
    }

    public boolean insertCostume(DbCostume costume) {
        return this.costumeDao.insert(costume);
    }

    public boolean insertOrUpdateCostumeForBatch(List<DbCostume> list) {
        if (CollectionUtil.isEmpty(list)) {
            return false;
        }
        for (DbCostume costume : list) {
            if (getCostume(costume.getCostumeId()) != null) {
                LogUtil.i("DaoCostume", "updateForBatch dbCostume:" + costume + " update:" + this.costumeDao.update(costume));
            } else {
                LogUtil.i("DaoCostume", "insertBatch dbCostume:" + costume + " insert:" + this.costumeDao.insert(costume));
            }
        }
        return true;
    }

    public DbCostume getCostume(int costumeId) {
        return this.costumeDao.queryForFirst("costumeId", Integer.valueOf(costumeId));
    }

    public List<DbCostume> getCostumeSuit() {
        return this.costumeDao.queryByColumnName("type", 0);
    }

    public List<DbCostume> getAllCostume() {
        return this.costumeDao.queryAllByOrder("type", true);
    }

    public boolean clearDangerData() {
        return this.dangerDao.clearTableData();
    }

    public boolean insertDangerForBatch(List<DbDanger> list) {
        return this.dangerDao.insertForBatch(list);
    }

    public DbDanger getDanger(Integer id) {
        return this.dangerDao.queryForFirst("id", id);
    }

    public boolean insertDecorateList(List<DbDecorate> list) {
        this.decorateDao.deleteAll();
        boolean success = true;
        for (int index = 0; index < list.size(); index++) {
            try {
                boolean inserted = this.decorateDao.insert(list.get(index));
                LogUtil.d(TAG, "insertIdList: " + inserted);
                success &= inserted;
            } catch (Exception e) {
                LogUtil.d(TAG, "");
            }
        }
        return success;
    }

    public List<DbDecorate> queryDecorateList() {
        return this.decorateDao.queryForAll();
    }

    public DbDecorate queryDecorateByDecorateId(String costumeId) {
        return this.decorateDao.queryForFirst("costumeId", costumeId);
    }

    public boolean clearPositionData() {
        return this.positionDao.clearTableData();
    }

    public boolean insertPositionForBatch(List<DbPosition> list) {
        return this.positionDao.insertForBatch(list);
    }

    public DbPosition getPosition(int suitId, int ornamentId) {
        ArrayMap<String, Object> conditions = new ArrayMap<>();
        conditions.put(DbPosition.Key.SUIT_ID, Integer.valueOf(suitId));
        conditions.put(DbPosition.Key.ORNAMENT_ID, Integer.valueOf(ornamentId));
        return this.positionDao.queryForFirst(conditions);
    }
}