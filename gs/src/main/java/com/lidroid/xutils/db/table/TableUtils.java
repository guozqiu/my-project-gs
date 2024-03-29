/*
 * Copyright (c) 2013. wyouflf (wyouflf@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lidroid.xutils.db.table;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import com.gs.common.util.StringUtils;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;
import com.lidroid.xutils.util.LogUtils;

public class TableUtils {

    private TableUtils() {
    }

    public static String getTableName(Class<?> entityType) {
        Table table = entityType.getAnnotation(Table.class);
        if (table == null || StringUtils.isEmpty(table.name())) {
            //当没有注解的时候默认用类的名称作为表名,并把点（.）替换为下划线(_)
            return entityType.getSimpleName().replace('.', '_');
        }
        return table.name();
    }

    /**
     * key: entityType.canonicalName
     */
    private static ConcurrentHashMap<String, HashMap<String, Column>> entityColumnsMap = new ConcurrentHashMap<String, HashMap<String, Column>>();

    /**
     * @param entityType
     * @return key: columnName
     */
    public static synchronized HashMap<String, Column> getColumnMap(Class<?> entityType) {

        if (entityColumnsMap.containsKey(entityType.getCanonicalName())) {
            return entityColumnsMap.get(entityType.getCanonicalName());
        }

        HashMap<String, Column> columnMap = new HashMap<String, Column>();
        addColumns2Map(entityType, columnMap);
        entityColumnsMap.put(entityType.getCanonicalName(), columnMap);

        return columnMap;
    }

    private static void addColumns2Map(Class<?> entityType, HashMap<String, Column> columnMap) {
        if (Object.class.equals(entityType)) return;
        try {
            Field[] fields = entityType.getDeclaredFields();
            String primaryKeyFieldName = getPrimaryKeyFieldName(entityType);
            for (Field field : fields) {
                if (ColumnUtils.isTransient(field) || Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                if (ColumnUtils.isSimpleColumnType(field)) {
                    if (!field.getName().equals(primaryKeyFieldName)) {
                        Column column = new Column(entityType, field);
                        if (!columnMap.containsKey(column.getColumnName())) {
                            columnMap.put(column.getColumnName(), column);
                        }
                    }
                } else if (ColumnUtils.isForeign(field)) {
                    Foreign column = new Foreign(entityType, field);
                    if (!columnMap.containsKey(column.getColumnName())) {
                        columnMap.put(column.getColumnName(), column);
                    }
                }
            }

            if (!Object.class.equals(entityType.getSuperclass())) {
                addColumns2Map(entityType.getSuperclass(), columnMap);
            }
        } catch (Exception e) {
            LogUtils.e(e.getMessage(), e);
        }
    }

    public static Column getColumnOrId(Class<?> entityType, String columnName) {
        if (getPrimaryKeyColumnName(entityType).equals(columnName)) {
            return com.lidroid.xutils.db.table.Table.get(entityType).getId();
        }
        return getColumnMap(entityType).get(columnName);
    }

    public static Column getColumnOrId(Class<?> entityType, Field columnField) {
        String columnName = ColumnUtils.getColumnNameByField(columnField);
        if (getPrimaryKeyColumnName(entityType).equals(columnName)) {
            return com.lidroid.xutils.db.table.Table.get(entityType).getId();
        }
        return getColumnMap(entityType).get(columnName);
    }

    /**
     * key: entityType.canonicalName
     */
    private static ConcurrentHashMap<String, com.lidroid.xutils.db.table.Id> entityIdMap = new ConcurrentHashMap<String, com.lidroid.xutils.db.table.Id>();

    public static synchronized com.lidroid.xutils.db.table.Id getId(Class<?> entityType) {
        if (Object.class.equals(entityType)) {
            throw new RuntimeException("this model[" + entityType + "] has no any field");
        }

        if (entityIdMap.containsKey(entityType.getCanonicalName())) {
            return entityIdMap.get(entityType.getCanonicalName());
        }

        Field primaryKeyField = null;
        Field[] fields = entityType.getDeclaredFields();
        if (fields != null) {

            for (Field field : fields) {
                if (field.getAnnotation(Id.class) != null) {
                    primaryKeyField = field;
                    break;
                }
            }

            if (primaryKeyField == null) {
                for (Field field : fields) {
                    if ("id".equals(field.getName()) || "_id".equals(field.getName())) {
                        primaryKeyField = field;
                        break;
                    }
                }
            }
        }

        if (primaryKeyField == null) {
            return getId(entityType.getSuperclass());
        }

        com.lidroid.xutils.db.table.Id id = new com.lidroid.xutils.db.table.Id(entityType, primaryKeyField);
        entityIdMap.put(entityType.getCanonicalName(), id);
        return id;
    }

    private static String getPrimaryKeyFieldName(Class<?> entityType) {
        com.lidroid.xutils.db.table.Id id = getId(entityType);
        return id == null ? null : id.getColumnField().getName();
    }

    private static String getPrimaryKeyColumnName(Class<?> entityType) {
        com.lidroid.xutils.db.table.Id id = getId(entityType);
        return id == null ? null : id.getColumnName();
    }

    public static Object getIdValue(Object entity) {
        if (entity == null) return null;

        try {
            com.lidroid.xutils.db.table.Id id = getId(entity.getClass());
            if (id == null) return null;
            Object idValue = id.getColumnValue(entity);
            if (idValue != null && !idValue.equals(0) && idValue.toString().length() > 0) {
                return idValue;
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }
}
