package com.xtc.im.core.common.tlv;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** 单个 TLV 实体的字段缓存：tag 值与 Field 的双向查找。 */
public class TLVEntityCache {

    private String entityName;
    private Map<Integer, Field> fieldMap = new ConcurrentHashMap<>();

    public TLVEntityCache(String entityName) {
        this.entityName = entityName;
    }

    public String getEntityName() {
        return this.entityName;
    }

    public void putField(int tagValue, Field field) {
        this.fieldMap.put(Integer.valueOf(tagValue), field);
    }

    public Field getField(int tagValue) {
        return this.fieldMap.get(Integer.valueOf(tagValue));
    }

    /** 按字段名反查 tag 值，未命中返回 0。 */
    public int getTagValue(String fieldName) {
        for (Map.Entry<Integer, Field> entry : this.fieldMap.entrySet()) {
            Field field = entry.getValue();
            if (field != null && field.getName().equals(fieldName)) {
                return entry.getKey().intValue();
            }
        }
        return 0;
    }
}