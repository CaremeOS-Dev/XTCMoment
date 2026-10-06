package com.xtc.database.ormlite;

import android.content.Context;
import android.text.TextUtils;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;
import com.xtc.log.LogUtil;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/** Thin wrapper over the ormlite {@link Dao} with the operations used by the app. */
public class OrmLiteDao<T> {

    private static final String TAG = "OrmLiteDao";

    protected final DatabaseHelper helper;
    protected final Dao<T, Integer> ormLiteDao;

    public OrmLiteDao(Context context, Class<T> clazz, String databaseName) {
        this.helper = DatabaseHelper.getInstance(context.getApplicationContext(), databaseName);
        this.ormLiteDao = this.helper.getDao(clazz);
    }

    public Dao<T, Integer> getDao() {
        return this.ormLiteDao;
    }

    private boolean doBatchInTransaction(final List<T> list, final int operation) {
        try {
            return new TransactionManager(this.ormLiteDao.getConnectionSource())
                    .callInTransaction(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return doBatch(list, operation);
                        }
                    });
        } catch (SQLException e) {
            LogUtil.e(TAG, e);
            return false;
        }
    }

    private boolean doBatch(List<T> list, int operation) {
        int affected = 0;
        for (T item : list) {
            try {
                int count;
                if (operation == DaoOperation.INSERT) {
                    count = this.ormLiteDao.create(item);
                } else if (operation == DaoOperation.DELETE) {
                    count = this.ormLiteDao.delete(item);
                } else if (operation == DaoOperation.UPDATE) {
                    count = updateIfValueNotNull(item);
                } else {
                    LogUtil.w(TAG, "no this type.");
                    count = 0;
                }
                affected += count;
            } catch (SQLException e) {
                LogUtil.e(TAG, e);
            }
        }
        return affected == list.size();
    }

    /** Inserts [item]. */
    public boolean insert(T item) {
        int count;
        try {
            count = this.ormLiteDao.create(item);
        } catch (SQLException e) {
            LogUtil.e(TAG, e);
            count = 0;
        }
        return count > 0;
    }

    /** Inserts every item of [list] in a single transaction. */
    public boolean insertForBatch(List<T> list) {
        return doBatchInTransaction(list, DaoOperation.INSERT);
    }

    /** Deletes every row of the table. */
    public boolean clearTableData() {
        long count;
        try {
            count = this.ormLiteDao.countOf();
        } catch (SQLException e) {
            LogUtil.e(TAG, e);
            count = 0;
        }
        if (count == 0) {
            return true;
        }
        try {
            return this.ormLiteDao.deleteBuilder().delete() > 0;
        } catch (SQLException e) {
            LogUtil.e(TAG, e);
            return false;
        }
    }

    /** Deletes every row whose [columnName] equals [value]. */
    public boolean deleteByColumnName(String columnName, Object value) {
        DeleteBuilder<T, Integer> builder = this.ormLiteDao.deleteBuilder();
        try {
            builder.where().eq(columnName, value);
            return builder.delete() > 0;
        } catch (SQLException e) {
            LogUtil.e("delete error, columnName: " + columnName + ", value: " + value + ", result: 0",
                    "error = " + e.getMessage());
            return false;
        }
    }

    /** Deletes every row matching every entry of [conditions]. */
    public boolean deleteByColumnName(Map<String, Object> conditions) {
        DeleteBuilder<T, Integer> builder = this.ormLiteDao.deleteBuilder();
        Where<T, Integer> where = builder.where();
        try {
            boolean first = true;
            for (Map.Entry<String, Object> entry : conditions.entrySet()) {
                if (first) {
                    where.eq(entry.getKey(), entry.getValue());
                    first = false;
                } else {
                    where.and().eq(entry.getKey(), entry.getValue());
                }
            }
            return builder.delete() > 0;
        } catch (SQLException e) {
            LogUtil.e("delete error,delete line:0", e);
            return false;
        }
    }

    /** Deletes every row whose [columnName] is lower than [value]. */
    public int deleteLtValue(String columnName, Object value) {
        DeleteBuilder<T, Integer> builder = this.ormLiteDao.deleteBuilder();
        try {
            builder.where().lt(columnName, value);
            return builder.delete();
        } catch (SQLException e) {
            LogUtil.e("delete error, columnName: " + columnName + ", value: " + value + ", result: 0", e);
            return 0;
        }
    }

    /** Deletes every row of the table and returns the deleted count. */
    public int deleteAll() {
        try {
            return this.ormLiteDao.deleteBuilder().delete();
        } catch (SQLException e) {
            LogUtil.e("clear table data error ", e);
            return 0;
        }
    }

    /** Deletes every item of [list] in a single transaction. */
    public boolean deleteForBatch(List<T> list) {
        return doBatchInTransaction(list, DaoOperation.DELETE);
    }

    /** @return the number of rows matching every entry of [conditions]. */
    public long getCount(Map<String, Object> conditions) {
        QueryBuilder<T, Integer> builder = this.ormLiteDao.queryBuilder();
        builder.setCountOf(true);
        Where<T, Integer> where = builder.where();
        try {
            boolean first = true;
            for (Map.Entry<String, Object> entry : conditions.entrySet()) {
                if (first) {
                    where.eq(entry.getKey(), entry.getValue());
                    first = false;
                } else {
                    where.and().eq(entry.getKey(), entry.getValue());
                }
            }
            return this.ormLiteDao.countOf(builder.prepare());
        } catch (SQLException e) {
            LogUtil.e(TAG, e);
            return 0L;
        }
    }

    /** @return the number of rows of the table. */
    public long getCount() {
        QueryBuilder<T, Integer> builder = this.ormLiteDao.queryBuilder();
        builder.setCountOf(true);
        try {
            return this.ormLiteDao.countOf(builder.prepare());
        } catch (SQLException e) {
            LogUtil.e(TAG, e);
            return 0L;
        }
    }

    public List<T> queryForAll() {
        try {
            return this.ormLiteDao.queryForAll();
        } catch (SQLException e) {
            LogUtil.e(TAG, e);
            return null;
        }
    }

    public List<T> queryByColumnName(Map<String, Object> conditions) {
        try {
            return this.ormLiteDao.queryForFieldValuesArgs(conditions);
        } catch (SQLException e) {
            LogUtil.e(TAG, e);
            return null;
        }
    }

    public List<T> queryByColumnName(String columnName, Object value) {
        QueryBuilder<T, Integer> builder = this.ormLiteDao.queryBuilder();
        try {
            builder.where().eq(columnName, value);
            return builder.query();
        } catch (SQLException e) {
            LogUtil.e(TAG, e);
            return null;
        }
    }

    public List<T> queryAllBySelectColumns(String[] columns) {
        QueryBuilder<T, Integer> builder = this.ormLiteDao.queryBuilder();
        try {
            builder.selectColumns(columns);
            return builder.query();
        } catch (SQLException e) {
            LogUtil.e(TAG, e);
            return null;
        }
    }

    public List<T> queryAllByGt(String columnName, Object value) {
        QueryBuilder<T, Integer> builder = this.ormLiteDao.queryBuilder();
        try {
            builder.where().gt(columnName, value);
            return builder.query();
        } catch (SQLException e) {
            LogUtil.e(TAG, e);
            return null;
        }
    }

    public List<T> queryAllByOrder(String columnName, boolean ascending) {
        QueryBuilder<T, Integer> builder = this.ormLiteDao.queryBuilder();
        try {
            builder.where().isNotNull(columnName);
            builder.orderBy(columnName, ascending);
            return builder.query();
        } catch (SQLException e) {
            LogUtil.e(TAG, e);
            return null;
        }
    }

    public List<T> queryByOrder(String orderColumn, String columnName, Object value, boolean ascending) {
        QueryBuilder<T, Integer> builder = this.ormLiteDao.queryBuilder();
        try {
            builder.where().eq(columnName, value);
            builder.orderBy(orderColumn, ascending);
            return builder.query();
        } catch (SQLException e) {
            LogUtil.e(TAG, e);
            return null;
        }
    }

    public List<T> queryGeByOrder(String columnName, Object value, boolean ascending) {
        QueryBuilder<T, Integer> builder = this.ormLiteDao.queryBuilder();
        try {
            builder.where().ge(columnName, value);
            builder.orderBy(columnName, ascending);
            return builder.query();
        } catch (SQLException e) {
            LogUtil.e(TAG, e);
            return null;
        }
    }

    public List<T> queryLeByOrder(String columnName, Object value, boolean ascending) {
        QueryBuilder<T, Integer> builder = this.ormLiteDao.queryBuilder();
        try {
            builder.where().le(columnName, value);
            builder.orderBy(columnName, ascending);
            return builder.query();
        } catch (SQLException e) {
            LogUtil.e(TAG, e);
            return null;
        }
    }

    public List<T> queryForPagesByOrder(String columnName, boolean ascending, Long offset, Long limit) {
        QueryBuilder<T, Integer> builder = this.ormLiteDao.queryBuilder();
        try {
            builder.where().isNotNull(columnName);
            builder.orderBy(columnName, ascending);
            builder.offset(offset);
            builder.limit(limit);
            return builder.query();
        } catch (SQLException e) {
            LogUtil.e(TAG, e);
            return null;
        }
    }

    public List<T> queryForPagesByOrder(String columnName, Object value, String orderColumn, boolean ascending,
            Long offset, Long limit) {
        QueryBuilder<T, Integer> builder = this.ormLiteDao.queryBuilder();
        try {
            builder.where().eq(columnName, value);
            builder.orderBy(orderColumn, ascending);
            builder.offset(offset);
            builder.limit(limit);
            return builder.query();
        } catch (SQLException e) {
            LogUtil.e(TAG, e);
            return null;
        }
    }

    public List<T> queryForPagesByOrder(Map<String, Object> conditions, String orderColumn, boolean ascending,
            Long offset, Long limit) {
        QueryBuilder<T, Integer> builder = this.ormLiteDao.queryBuilder();
        Where<T, Integer> where = builder.where();
        try {
            builder.orderBy(orderColumn, ascending);
            builder.offset(offset);
            builder.limit(limit);
            boolean first = true;
            for (Map.Entry<String, Object> entry : conditions.entrySet()) {
                if (first) {
                    where.eq(entry.getKey(), entry.getValue());
                    first = false;
                } else {
                    where.and().eq(entry.getKey(), entry.getValue());
                }
            }
            return builder.query();
        } catch (SQLException e) {
            LogUtil.e(TAG, e);
            return null;
        }
    }

    public T queryForFirst(String columnName, Object value) {
        QueryBuilder<T, Integer> builder = this.ormLiteDao.queryBuilder();
        try {
            builder.where().eq(columnName, value);
            return builder.queryForFirst();
        } catch (Exception e) {
            LogUtil.e(TAG, e);
            return null;
        }
    }

    public T queryForFirst(Map<String, Object> conditions) {
        QueryBuilder<T, Integer> builder = this.ormLiteDao.queryBuilder();
        Where<T, Integer> where = builder.where();
        try {
            boolean first = true;
            for (Map.Entry<String, Object> entry : conditions.entrySet()) {
                if (first) {
                    where.eq(entry.getKey(), entry.getValue());
                    first = false;
                } else {
                    where.and().eq(entry.getKey(), entry.getValue());
                }
            }
            return builder.queryForFirst();
        } catch (Exception e) {
            LogUtil.e(TAG, e);
            return null;
        }
    }

    public T queryForFirstColumnNotNull(String columnName) {
        QueryBuilder<T, Integer> builder = this.ormLiteDao.queryBuilder();
        try {
            builder.where().isNotNull(columnName);
            return builder.queryForFirst();
        } catch (SQLException e) {
            LogUtil.e(TAG, e);
            return null;
        }
    }

    public List<T> queryAllColumnNotNull(String columnName) {
        QueryBuilder<T, Integer> builder = this.ormLiteDao.queryBuilder();
        try {
            builder.where().isNotNull(columnName);
            return builder.query();
        } catch (SQLException e) {
            LogUtil.e(TAG, e);
            return null;
        }
    }

    public T queryForFirstByOrder(Map<String, Object> conditions, String orderColumn, boolean ascending) {
        QueryBuilder<T, Integer> builder = this.ormLiteDao.queryBuilder();
        Where<T, Integer> where = builder.where();
        try {
            builder.orderBy(orderColumn, ascending);
            boolean first = true;
            for (Map.Entry<String, Object> entry : conditions.entrySet()) {
                if (first) {
                    where.eq(entry.getKey(), entry.getValue());
                    first = false;
                } else {
                    where.and().eq(entry.getKey(), entry.getValue());
                }
            }
            return builder.queryForFirst();
        } catch (SQLException e) {
            LogUtil.e(TAG, e);
            return null;
        }
    }

    public T queryForFirstByOrder(String columnName, Object value, String orderColumn, boolean ascending) {
        QueryBuilder<T, Integer> builder = this.ormLiteDao.queryBuilder();
        try {
            builder.orderBy(orderColumn, ascending);
            builder.where().eq(columnName, value);
            return builder.queryForFirst();
        } catch (SQLException e) {
            LogUtil.e(TAG, e);
            return null;
        }
    }

    public List<T> queryNotEqualsByColumnName(String columnName, Object value) {
        QueryBuilder<T, Integer> builder = this.ormLiteDao.queryBuilder();
        Where<T, Integer> where = builder.where();
        try {
            where.or(where.gt(columnName, value), where.lt(columnName, value), new Where[0]);
            return builder.query();
        } catch (SQLException e) {
            LogUtil.e(TAG, e);
            return null;
        }
    }

    /** Updates [item] using its non null fields. */
    public boolean update(T item) {
        return updateIfValueNotNull(item) > 0;
    }

    /** Updates the row matching [columnName]/[value] with the non null fields of [item]. */
    public boolean updateBy(T item, String columnName, Object value) {
        int count;
        T existing = queryForFirst(columnName, value);
        if (existing == null) {
            LogUtil.e(TAG, "no find this data in database:" + item);
            return false;
        }
        try {
            copyNotNullValues(existing, item);
            count = this.ormLiteDao.update(existing);
        } catch (IllegalAccessException e) {
            LogUtil.e(TAG, e);
            count = 0;
        } catch (NoSuchFieldException e) {
            LogUtil.e(TAG, e);
            count = 0;
        } catch (SQLException e) {
            LogUtil.e(TAG, e);
            count = 0;
        }
        return count > 0;
    }

    /** Updates the row matching [conditions] with the non null fields of [item]. */
    public boolean updateBy(T item, Map<String, Object> conditions) {
        int count;
        T existing = queryForFirst(conditions);
        if (existing == null) {
            LogUtil.e(TAG, "no find this data in database:" + item);
            return false;
        }
        try {
            copyNotNullValues(existing, item);
            count = this.ormLiteDao.update(existing);
        } catch (IllegalAccessException e) {
            LogUtil.e("update error,update line:0", e);
            count = 0;
        } catch (NoSuchFieldException e) {
            LogUtil.e("update error,update line:0", e);
            count = 0;
        } catch (SQLException e) {
            LogUtil.e("update error,update line:0", e);
            count = 0;
        }
        return count > 0;
    }

    /** Updates every item of [list] in a single transaction. */
    public boolean updateForBatch(List<T> list) {
        return doBatchInTransaction(list, DaoOperation.UPDATE);
    }

    private int updateIfValueNotNull(T item) {
        UpdateBuilder<T, Integer> builder = this.ormLiteDao.updateBuilder();
        Map<String, Object> fields = getFieldsIfValueNotNull(item);
        if (fields.isEmpty()) {
            LogUtil.w(TAG, "all field value is null.");
            return 0;
        }
        String primaryKey = getDbPrimarykey(item);
        if (fields.get(primaryKey) == null) {
            LogUtil.w(TAG, primaryKey + " is null.");
            return 0;
        }
        try {
            builder.where().idEq((Integer) fields.get(primaryKey));
            for (Map.Entry<String, Object> entry : fields.entrySet()) {
                if (!entry.getKey().equals(primaryKey)) {
                    builder.updateColumnValue(entry.getKey(), new SelectArg(entry.getValue()));
                }
            }
            return builder.update();
        } catch (SQLException e) {
            LogUtil.e(TAG, e);
            return 0;
        }
    }

    private void copyNotNullValues(Object target, Object source) throws IllegalAccessException, NoSuchFieldException {
        copySuperValues(target, target.getClass(), source, source.getClass(), getDbPrimarykey(source));
    }

    private void copySuperValues(Object target, Class targetClass, Object source, Class sourceClass,
            String primaryKey) throws IllegalAccessException, NoSuchFieldException {
        for (Field field : sourceClass.getDeclaredFields()) {
            if (field.getName().equals(primaryKey)) {
                continue;
            }
            field.setAccessible(true);
            Object value = field.get(source);
            if (value != null) {
                Field targetField = targetClass.getDeclaredField(field.getName());
                targetField.setAccessible(true);
                targetField.set(target, value);
            }
        }
        if (targetClass.getSuperclass() == null || sourceClass.getSuperclass() == null) {
            return;
        }
        copySuperValues(target, targetClass.getSuperclass(), source, sourceClass.getSuperclass(), primaryKey);
    }

    private Map<String, Object> getFieldsIfValueNotNull(Object item) {
        HashMap<String, Object> fields = new HashMap<>();
        ArrayList<Field> allFields = new ArrayList<>();
        for (Class<?> clazz = item.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
            allFields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        }
        for (Field field : allFields) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(DatabaseField.class)) {
                Object value = null;
                try {
                    value = field.get(item);
                } catch (IllegalAccessException e) {
                    LogUtil.e(TAG, e);
                }
                if (value != null) {
                    fields.put(field.getName(), value);
                }
            }
        }
        return fields;
    }

    private boolean checkIdIsNull(Integer id) {
        if (id != null) {
            return false;
        }
        LogUtil.w(TAG, "id is null.");
        return true;
    }

    private String getDbPrimarykey(Object item) {
        String name = "";
        ArrayList<Field> allFields = new ArrayList<>();
        for (Class<?> clazz = item.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
            allFields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        }
        Iterator<Field> iterator = allFields.iterator();
        while (iterator.hasNext()) {
            Field field = iterator.next();
            name = field.getName();
            DatabaseField databaseField = field.getAnnotation(DatabaseField.class);
            if (databaseField != null && (databaseField.generatedId() || databaseField.id()
                    || !TextUtils.isEmpty(databaseField.generatedIdSequence()))) {
                break;
            }
        }
        if (!TextUtils.isEmpty(name)) {
            return name;
        }
        LogUtil.e(TAG, "这里获取的主键信息居然为空,那我就匹配主键为id来容错");
        return "id";
    }

    /** @return the name of the last field annotated with {@link DatabaseField}. */
    public String getDbAnnotationField(Object item) {
        String name = "";
        for (Field field : item.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(DatabaseField.class)) {
                name = field.getName();
            }
        }
        return name;
    }
}