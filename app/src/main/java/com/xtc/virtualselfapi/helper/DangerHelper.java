package com.xtc.virtualselfapi.helper;

import com.xtc.log.LogUtil;
import com.xtc.utils.encode.JSONUtil;
import com.xtc.virtualselfapi.bean.db.DbCostume;
import com.xtc.virtualselfapi.bean.db.DbDanger;
import com.xtc.virtualselfapi.manager.VirtualSelfDBServeImpl;

import java.util.HashMap;
import java.util.List;

/**
 * 危险状态与装扮关联处理工具。
 */
public class DangerHelper {

    private static final String TAG = "Virtual_Self_Api_DangerHelper";

    public List<Integer> convertDangerToUsingDecoration(int dangerId, List<Integer> decorationList) {
        DbDanger danger = VirtualSelfDBServeImpl.getInstance().getDanger(Integer.valueOf(dangerId));
        LogUtil.d(TAG, "current danger = " + danger);
        DbCostume currentSuit = getCurrentSuit(decorationList);
        List<Integer> decorationIds = (List) JSONUtil.fromJSON(danger.getInfo(), List.class, Integer.class);
        decorationIds.add(Integer.valueOf(currentSuit.getCostumeId()));
        return decorationIds;
    }

    private DbCostume getCurrentSuit(List<Integer> decorationList) {
        HashMap<Integer, DbCostume> costumeMap = new HashMap<>();
        List<DbCostume> costumes = VirtualSelfDBServeImpl.getInstance().getCostumeSuit();
        for (int index = 0; index < costumes.size(); index++) {
            DbCostume costume = costumes.get(index);
            costumeMap.put(Integer.valueOf(costume.getCostumeId()), costume);
        }
        for (int index = 0; index < decorationList.size(); index++) {
            int decorationId = decorationList.get(index).intValue();
            if (costumeMap.containsKey(Integer.valueOf(decorationId))) {
                return costumeMap.get(Integer.valueOf(decorationId));
            }
        }
        return null;
    }
}