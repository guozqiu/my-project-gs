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
import java.util.List;

import com.lidroid.xutils.db.sqlite.SQLiteLazyLoader;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.util.DBUtils;
import com.lidroid.xutils.util.LogUtils;

public class Foreign extends Column {

    /**
     * 被 CursorUtils.getEntity 或 SqlInfoBuilder.entity2KeyValueList 赋值
     */
    public DBUtils db;

    private String foreignColumnName;

    protected Foreign(Class<?> entityType, Field field) {
        super(entityType, field);
        foreignColumnName = ColumnUtils.getForeignColumnNameByField(field);
    }

    public String getForeignColumnName() {
        return foreignColumnName;
    }

    public Class<?> getForeignEntityType() {
        return ColumnUtils.getForeignEntityType(this);
    }

    public Class<?> getForeignColumnType() {
        return TableUtils.getColumnOrId(getForeignEntityType(), foreignColumnName).columnField.getType();
    }

    @Override
    public void setValue2Entity(Object entity, String valueStr) {
        Object value = null;
        if (valueStr != null) {
            Class<?> columnType = columnField.getType();
            Object columnValue = ColumnUtils.valueStr2SimpleTypeFieldValue(getForeignColumnType(), valueStr);
            if (ColumnUtils.isSimpleColumnType(columnField)) {
                value = columnValue;
            } else if (columnType.equals(SQLiteLazyLoader.class)) {
                value = new SQLiteLazyLoader<Object>(this, columnValue);
            } else if (columnType.equals(List.class)) {
                try {
                    value = new SQLiteLazyLoader<Object>(this, columnValue).getAllFromDb();
                } catch (DbException e) {
                    LogUtils.e(e.getMessage(), e);
                }
            } else {
                try {
                    value = new SQLiteLazyLoader<Object>(this, columnValue).getFirstFromDb();
                } catch (DbException e) {
                    LogUtils.e(e.getMessage(), e);
                }
            }
        }

        if (setMethod != null) {
            try {
                setMethod.invoke(entity, value);
            } catch (Exception e) {
                LogUtils.e(e.getMessage(), e);
            }
        } else {
            try {
                this.columnField.setAccessible(true);
                this.columnField.set(entity, value);
            } catch (Exception e) {
                LogUtils.e(e.getMessage(), e);
            }
        }
    }

    @Override
    public Object getColumnValue(Object entity) {
        Object resultObj = null;
        if (entity != null) {
            if (getMethod != null) {
                try {
                    resultObj = getMethod.invoke(entity);
                } catch (Exception e) {
                    LogUtils.e(e.getMessage(), e);
                }
            } else {
                try {
                    this.columnField.setAccessible(true);
                    resultObj = this.columnField.get(entity);
                } catch (Exception e) {
                    LogUtils.e(e.getMessage(), e);
                }
            }
        }

        if (resultObj != null) {
            Class<?> columnType = columnField.getType();
            if (columnType.equals(SQLiteLazyLoader.class)) {
                resultObj = ((SQLiteLazyLoader<?>) resultObj).getColumnValue();
            } else if (columnType.equals(List.class)) {
                try {
                    List<?> foreignValues = (List<?>) resultObj;
                    if (foreignValues.size() > 0) {

                        if (this.db != null) {
                            for (Object item : foreignValues) {
                                try {
                                    this.db.saveOrUpdate(item);
                                } catch (DbException e) {
                                    LogUtils.e(e.getMessage(), e);
                                }
                            }
                        }

                        Class<?> foreignEntityType = ColumnUtils.getForeignEntityType(this);
                        Column column = TableUtils.getColumnOrId(foreignEntityType, foreignColumnName);
                        resultObj = column.getColumnValue(foreignValues.get(0));
                    }
                } catch (Exception e) {
                    resultObj = null;
                    LogUtils.e(e.getMessage(), e);
                }
            } else {
                try {
                    if (this.db != null) {
                        try {
                            this.db.saveOrUpdate(resultObj);
                        } catch (DbException e) {
                            LogUtils.e(e.getMessage(), e);
                        }
                    }
                    Column column = TableUtils.getColumnOrId(columnType, foreignColumnName);
                    resultObj = column.getColumnValue(resultObj);
                } catch (Exception e) {
                    resultObj = null;
                    LogUtils.e(e.getMessage(), e);
                }
            }
        }

        return ColumnUtils.convert2DbColumnValueIfNeeded(resultObj);
    }

    @Override
    public String getColumnDbType() {
        return ColumnUtils.fieldType2DbType(getForeignColumnType());
    }

    /**
     * 外键没有默认值，返回null
     *
     * @return null
     */
    @Override
    public Object getDefaultValue() {
        return null;
    }
}
