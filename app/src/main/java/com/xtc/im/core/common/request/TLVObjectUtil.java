package com.xtc.im.core.common.request;

import com.xtc.im.core.common.LogTag;
import com.xtc.im.core.common.request.entity.RequestEntity;
import com.xtc.im.core.common.response.entity.ResponseEntity;
import com.xtc.im.core.common.tlv.TLVCache;
import com.xtc.im.core.common.tlv.TLVDecodeResult;
import com.xtc.im.core.common.tlv.TLVDecoder;
import com.xtc.im.core.common.tlv.TLVObject;
import com.xtc.log.LogUtil;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;

/** 实体 <-> TLV 互转工具，基于 {@link com.xtc.im.core.common.anotation.TagValue} 反射读写字段。 */
public class TLVObjectUtil {

    private static final String TAG = LogTag.tag("TLVObjectUtil");

    /** 心跳包没有字段，仅写入命令字。 */
    public static byte[] createHeartBeatByteArray(int command) throws IOException {
        TLVObject tlvObject = new TLVObject();
        tlvObject.put(command, (byte[]) null);
        return tlvObject.toByteArray();
    }

    public static TLVObject parseTLVObject(Entity entity) {
        TLVObject tlvObject = new TLVObject();
        try {
            tlvObject.put(entity.getCommand(), parseTLVObjectImpl(entity));
        } catch (Exception e) {
            LogUtil.e(TAG, e);
        }
        return tlvObject;
    }

    public static byte[] parseByteArray(Entity entity) {
        return parseTLVObject(entity).toByteArray();
    }

    private static TLVObject parseTLVObjectImpl(Entity entity) throws Exception {
        TLVObject tlvObject = new TLVObject();
        for (Field field : entity.getDeclaredFields()) {
            putValueToTLVObject(entity.getTagValue(field.getName()), field, entity, tlvObject);
        }
        return tlvObject;
    }

    private static void putValueToTLVObject(int tagValue, Field field, Entity entity, TLVObject tlvObject)
            throws Exception {
        field.setAccessible(true);
        Class<?> fieldType = field.getType();
        Object value = field.get(entity);
        if (value == null) {
            LogUtil.w(TAG, "formatdata error(value is null),name:" + field.getName() + ",type:" + field.getType());
            return;
        }
        if (fieldType == Integer.TYPE) {
            int intValue = ((Integer) value).intValue();
            if (intValue != 0) {
                tlvObject.put(tagValue, intValue);
            } else {
                LogUtil.v(TAG, "formatdata error(" + value.getClass().getSimpleName() + " value is 0):"
                        + field.getName());
            }
            return;
        }
        if (fieldType == Long.TYPE) {
            long longValue = ((Long) value).longValue();
            if (longValue != 0) {
                tlvObject.put(tagValue, longValue);
            } else {
                LogUtil.v(TAG, "formatdata error(" + value.getClass().getSimpleName() + " value is 0):"
                        + field.getName());
            }
            return;
        }
        if (fieldType == String.class) {
            tlvObject.put(tagValue, value.toString());
            return;
        }
        if (fieldType == byte[].class) {
            tlvObject.put(tagValue, (byte[]) value);
            return;
        }
        if (value instanceof Entity) {
            tlvObject.put(tagValue, parseTLVObject((Entity) value));
            return;
        }
        LogUtil.v(TAG, "formatdata error(unsupport type " + value.getClass().getName() + "):" + field.getName());
    }

    public static ResponseEntity parseResponseEntity(byte[] data) throws Throwable {
        return (ResponseEntity) parseEntity(data);
    }

    public static RequestEntity parseRequestEntity(byte[] data) throws Throwable {
        return (RequestEntity) parseEntity(data);
    }

    /** 按命令字从 {@link ReqRespRelationship} 找到实体类并反序列化。 */
    public static Entity parseEntity(byte[] data) throws Throwable {
        TLVDecodeResult decodeResult = TLVDecoder.decode(data);
        return (Entity) parseEntity(decodeResult,
                ReqRespRelationship.COMMAND_CLASS_MAP.get(Integer.valueOf(decodeResult.getTagValue())));
    }

    @SuppressWarnings("unchecked")
    public static <T> T parseEntity(byte[] data, Class<?> entityClass) throws Throwable {
        return (T) parseEntity(TLVDecoder.decode(data), entityClass);
    }

    @SuppressWarnings("unchecked")
    private static <T> T parseEntity(TLVDecodeResult decodeResult, Class<?> entityClass) throws Throwable {
        T entity = (T) ((Entity) entityClass.newInstance());
        parseEntityImpl(decodeResult, (Entity) entity);
        return entity;
    }

    private static void parseEntityImpl(TLVDecodeResult decodeResult, Entity entity)
            throws IllegalAccessException, NoSuchFieldException {
        if (decodeResult.getDataType() == 32) {
            initEntityFieldValue((List<TLVDecodeResult>) decodeResult.getValue(), entity);
        } else {
            initEntityFieldValueImpl(decodeResult, entity);
        }
    }

    private static void initEntityFieldValue(List<TLVDecodeResult> results, Entity entity)
            throws IllegalAccessException, NoSuchFieldException {
        if (results == null) {
            return;
        }
        Iterator<TLVDecodeResult> iterator = results.iterator();
        while (iterator.hasNext()) {
            initEntityFieldValueImpl(iterator.next(), entity);
        }
    }

    private static void initEntityFieldValueImpl(TLVDecodeResult decodeResult, Entity entity)
            throws IllegalAccessException, NoSuchFieldException {
        Field field = getFieldByTagValueFromEntity(decodeResult, entity);
        if (field != null) {
            setEntityFieldValue(decodeResult, entity, field);
        }
    }

    private static Field getFieldByTagValueFromEntity(TLVDecodeResult decodeResult, Entity entity)
            throws NoSuchFieldException {
        Field field = TLVCache.getField(entity.getClass().getName(), decodeResult.getTagValue());
        if (field != null) {
            return field;
        }
        for (Field declaredField : entity.getDeclaredFields()) {
            if (decodeResult.getTagValue() == entity.getTagValue(declaredField.getName())) {
                TLVCache.addTlvEntityCache(entity.getClass().getName(), decodeResult.getTagValue(), declaredField);
                return declaredField;
            }
        }
        return field;
    }

    @SuppressWarnings("unchecked")
    private static void setEntityFieldValue(TLVDecodeResult decodeResult, Entity entity, Field field)
            throws IllegalAccessException, NoSuchFieldException {
        field.setAccessible(true);
        Class<?> fieldType = field.getType();
        if (fieldType == Integer.TYPE) {
            field.setInt(entity, decodeResult.getIntValue());
            return;
        }
        if (fieldType == Long.TYPE) {
            field.setLong(entity, decodeResult.getLongValue());
            return;
        }
        if (fieldType == String.class) {
            field.set(entity, decodeResult.getStringValue());
            return;
        }
        if (fieldType == byte[].class) {
            field.set(entity, decodeResult.getValue());
            return;
        }
        if (fieldType == Entity.class || fieldType.getSuperclass() == Entity.class) {
            if (decodeResult.getDataType() == 32) {
                List<TLVDecodeResult> results = (List<TLVDecodeResult>) decodeResult.getValue();
                if (results == null) {
                    return;
                }
                for (TLVDecodeResult childResult : results) {
                    Class<?> childClass = ReqRespRelationship.COMMAND_CLASS_MAP
                            .get(Integer.valueOf(childResult.getTagValue()));
                    if (childClass != null) {
                        try {
                            Entity childEntity = (Entity) childClass.newInstance();
                            parseEntityImpl(childResult, childEntity);
                            field.set(entity, childEntity);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InstantiationException e) {
                            e.printStackTrace();
                        }
                    } else {
                        LogUtil.w(TAG, "未找到该类型:" + field.getName() + " " + fieldType);
                    }
                }
            } else {
                LogUtil.w(TAG, "TLV数据类型错误!");
            }
            return;
        }
        LogUtil.w(TAG, "不支持值类型:" + field.getName() + " " + fieldType);
    }
}