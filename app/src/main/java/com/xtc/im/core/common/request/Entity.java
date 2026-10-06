package com.xtc.im.core.common.request;

import com.xtc.im.core.common.LogTag;
import com.xtc.im.core.common.anotation.CommandValue;
import com.xtc.im.core.common.anotation.TagValue;
import com.xtc.im.core.common.tlv.TLVCache;
import com.xtc.log.LogUtil;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** TLV 实体基类：通过注解描述字段与 tag 的映射，并提供序列化入口。 */
@CommandValue(0)
public abstract class Entity {

    private static final String TAG = LogTag.tag("Entity");
    protected static transient Map<String, Field[]> fieldMap = new ConcurrentHashMap<>();
    protected transient int command;

    /** 命令字，取自类的 {@link CommandValue} 注解。 */
    public int getCommand() {
        int cachedCommand = this.command;
        if (cachedCommand != 0) {
            return cachedCommand;
        }
        CommandValue commandValue = getClass().getAnnotation(CommandValue.class);
        if (commandValue == null) {
            return 0;
        }
        this.command = commandValue.value();
        return this.command;
    }

    /** 取字段对应的 tag 值，优先查缓存。 */
    public int getTagValue(String fieldName) {
        String className = getClass().getName();
        int cachedTag = TLVCache.getTagValue(className, fieldName);
        if (cachedTag != 0) {
            return cachedTag;
        }
        try {
            Field field = getClass().getDeclaredField(fieldName);
            TagValue tagValue = field.getAnnotation(TagValue.class);
            if (tagValue != null) {
                TLVCache.addTlvEntityCache(className, tagValue.value(), field);
                return tagValue.value();
            }
            return 0;
        } catch (NoSuchFieldException e) {
            LogUtil.e(TAG, e);
            return 0;
        } catch (SecurityException e) {
            LogUtil.e(TAG, e);
            return 0;
        }
    }

    /** 带缓存的声明字段列表。 */
    public Field[] getDeclaredFields() {
        String className = getClass().getName();
        Field[] fields = fieldMap.get(className);
        if (fields != null) {
            return fields;
        }
        Field[] declaredFields = getClass().getDeclaredFields();
        fieldMap.put(className, declaredFields);
        return declaredFields;
    }

    /** 序列化为 TLV 字节数组，失败时返回 null。 */
    public byte[] toByteArray() {
        int command = getCommand();
        try {
            if (command == Command.HEART_BEAT_REQUEST) {
                return TLVObjectUtil.createHeartBeatByteArray(command);
            }
            return TLVObjectUtil.parseByteArray(this);
        } catch (Exception e) {
            LogUtil.e(TAG, e);
            return null;
        }
    }
}