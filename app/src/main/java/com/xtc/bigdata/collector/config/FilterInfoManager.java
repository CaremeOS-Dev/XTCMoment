package com.xtc.bigdata.collector.config;

import android.text.TextUtils;

import com.xtc.bigdata.collector.utils.CollectionUtil;
import com.xtc.bigdata.common.utils.BloomFilter;
import com.xtc.bigdata.common.utils.FileUtils;
import com.xtc.log.LogUtil;
import com.xtc.utils.encode.JSONUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 过滤埋点配置读写：从本地文件加载 GDPR 名单，并同步到内存配置。
 */
public class FilterInfoManager {

    public static final String ADD_MASK = "+";
    private static final String FILE_GDPR_NAME = "/filterFunctions_GDPR.txt";
    private static final String FILE_OLD_NAME = "/filterFunctions.txt";
    private static final String TAG = "FilterInfoManager";
    private static final double FALSE_POSITIVE_RATE = 0.001d;

    public static synchronized BloomFilter readFilterFunctions() {
        LogUtil.i(TAG, "readFilterFunctions -->");
        String bigDataDirPath = FileUtils.getBigDataDirPath();
        if (!new File(bigDataDirPath).exists()) {
            return null;
        }
        String json = null;
        try {
            json = FileUtils.readFromFile(bigDataDirPath + FILE_GDPR_NAME);
        } catch (Exception e) {
            LogUtil.e(TAG, e);
        }
        LogUtil.d(TAG, "磁盘 读取出来的json字符串为 = " + json);
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        List<NewFilterFunctionItem> items = JSONUtil.fromJSON(json, List.class, NewFilterFunctionItem.class);
        BloomFilter bloomFilter = new BloomFilter(items.size(), FALSE_POSITIVE_RATE);
        ArrayList<String> listGradeA = new ArrayList<>();
        ArrayList<String> listGradeC = new ArrayList<>();
        LogUtil.d(TAG, "从本地获取到的的 list.size() 为 = " + items.size() + ", list : " + items.toString());
        for (NewFilterFunctionItem item : items) {
            bloomFilter.add(item.getF());
            if (TextUtils.equals(item.getC(), "A")) {
                listGradeA.add(item.getF());
            } else if (TextUtils.equals(item.getC(), "C")) {
                listGradeC.add(item.getF());
            }
        }
        ConfigAgent.getBehaviorConfig().collectFilterConfig.setBloomFilter(bloomFilter);
        ConfigAgent.getBehaviorConfig().collectFilterConfig.setListGradeA(listGradeA);
        ConfigAgent.getBehaviorConfig().collectFilterConfig.setListGradeC(listGradeC);
        LogUtil.d(TAG, "读取服务器配置A或C名单为 ListA ： " + listGradeA + ", ListC : " + listGradeC);
        return bloomFilter;
    }

    public static synchronized void saveFilterFunctions(String json) {
        LogUtil.i(TAG, "saveFilterFunctions -- 将要保存的配置string为 --> " + json);
        String bigDataDirPath = FileUtils.getBigDataDirPath();
        File dir = new File(bigDataDirPath);
        if (!dir.exists()) {
            dir.mkdir();
        }
        deleteOldConfig(bigDataDirPath + FILE_OLD_NAME);
        String path = bigDataDirPath + FILE_GDPR_NAME;
        LogUtil.d(TAG, "保存的路径为 --> " + path);
        if (!TextUtils.isEmpty(json) && !TextUtils.isEmpty(json.trim())) {
            List<NewFilterFunctionItem> items = JSONUtil.fromJSON(json, List.class, NewFilterFunctionItem.class);
            LogUtil.w(TAG, "本机 list = " + items);
            if (CollectionUtil.isEmpty(items)) {
                tryReloadFilterConfig();
                return;
            }
            FileUtils.saveToFile(JSONUtil.toJSON(items), path);
            BloomFilter bloomFilter = new BloomFilter(items.size(), FALSE_POSITIVE_RATE);
            ArrayList<String> listGradeA = new ArrayList<>();
            ArrayList<String> listGradeC = new ArrayList<>();
            LogUtil.d(TAG, "获取到的的 list.size() 为 = " + items.size());
            for (NewFilterFunctionItem item : items) {
                bloomFilter.add(item.getF());
                if (TextUtils.equals(item.getC(), "A")) {
                    listGradeA.add(item.getF());
                } else if (TextUtils.equals(item.getC(), "C")) {
                    listGradeC.add(item.getF());
                }
            }
            LogUtil.d(TAG, "保存服务器配置A或C名单为 ListA ： " + listGradeA + ", ListC : " + listGradeC);
            ConfigAgent.getBehaviorConfig().collectFilterConfig.setBloomFilter(bloomFilter);
            ConfigAgent.getBehaviorConfig().collectFilterConfig.setListGradeA(listGradeA);
            ConfigAgent.getBehaviorConfig().collectFilterConfig.setListGradeC(listGradeC);
            return;
        }
        tryReloadFilterConfig();
    }

    private static void tryReloadFilterConfig() {
        if (ConfigAgent.getBehaviorConfig().collectFilterConfig.getBloomFilter() == null) {
            LogUtil.d(TAG, "tryloadFilterConfig: empty, try reload");
            readFilterFunctions();
        }
    }

    public static synchronized String getFilterFunctionItemJson() {
        String bigDataDirPath = FileUtils.getBigDataDirPath();
        if (!new File(bigDataDirPath).exists()) {
            return null;
        }
        String path = bigDataDirPath + FILE_GDPR_NAME;
        LogUtil.d(TAG, "保存的路径为 --> " + path);
        try {
            return FileUtils.readFromFile(path);
        } catch (Exception e) {
            LogUtil.e(e);
            return null;
        }
    }

    private static void deleteOldConfig(String path) {
        LogUtil.d(TAG, "删除 旧版本 本地过滤埋点缓存数据: 路径: " + path + " -> isDelete: " + FileUtils.deleteFile(path));
    }

    private static List<FilterFunctionItem> selectFilterFunctionItemList(List<FilterFunctionItem> items, String innerModel) {
        ArrayList<FilterFunctionItem> result = new ArrayList<>();
        if (CollectionUtil.isEmpty(items)) {
            return result;
        }
        for (FilterFunctionItem item : items) {
            if (item != null && (TextUtils.isEmpty(item.getI()) || item.getI().equals(innerModel))) {
                result.add(item);
            }
        }
        LogUtil.d(TAG, "FilterFunctionItem newList:" + result);
        return result;
    }
}