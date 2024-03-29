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
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Date;
import java.util.List;

import com.gs.common.util.DateUtils;
import com.gs.common.util.StringUtils;
import com.lidroid.xutils.db.annotation.Check;
import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Foreign;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NotNull;
import com.lidroid.xutils.db.annotation.Transient;
import com.lidroid.xutils.db.annotation.Unique;
import com.lidroid.xutils.db.sqlite.SQLiteLazyLoader;
import com.lidroid.xutils.util.LogUtils;

public class ColumnUtils {

    private ColumnUtils() {
    }

    public static Method getColumnGetMethod(Class<?> entityType, Field field) {
        String fieldName = field.getName();
        Method getMethod = null;
        if (field.getType() == boolean.class) {
            getMethod = getBooleanColumnGetMethod(entityType, fieldName);
        }
        if (getMethod == null) {
            String methodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            try {
                getMethod = entityType.getDeclaredMethod(methodName);
            } catch (NoSuchMethodException e) {
                LogUtils.d(methodName + " not exist");
            }
        }

        if (getMethod == null && !Object.class.equals(entityType.getSuperclass())) {
            return getColumnGetMethod(entityType.getSuperclass(), field);
        }
        return getMethod;
    }

    public static Method getColumnSetMethod(Class<?> entityType, Field field) {
        String fieldName = field.getName();
        Method setMethod = null;
        if (field.getType() == boolean.class) {
            setMethod = getBooleanColumnSetMethod(entityType, field);
        }
        if (setMethod == null) {
            String methodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            try {
                setMethod = entityType.getDeclaredMethod(methodName, field.getType());
            } catch (NoSuchMethodException e) {
                LogUtils.d(methodName + " not exist");
            }
        }

        if (setMethod == null && !Object.class.equals(entityType.getSuperclass())) {
            return getColumnSetMethod(entityType.getSuperclass(), field);
        }
        return setMethod;
    }


    public static String getColumnNameByField(Field field) {
        Column column = field.getAnnotation(Column.class);
        if (column != null && !StringUtils.isEmpty(column.column())) {
            return column.column();
        }

        Id id = field.getAnnotation(Id.class);
        if (id != null && !StringUtils.isEmpty(id.column())) {
            return id.column();
        }

        Foreign foreign = field.getAnnotation(Foreign.class);
        if (foreign != null && !StringUtils.isEmpty(foreign.column())) {
            return foreign.column();
        }

        return field.getName();
    }

    public static String getForeignColumnNameByField(Field field) {

        Foreign foreign = field.getAnnotation(Foreign.class);
        if (foreign != null) {
            return foreign.foreign();
        }

        return field.getName();
    }

    public static Object getColumnDefaultValue(Field field) {
        Column column = field.getAnnotation(Column.class);
        if (column != null && !StringUtils.isEmpty(column.defaultValue())) {
            return valueStr2SimpleTypeFieldValue(field.getType(), column.defaultValue());
        }
        return null;
    }

    public static boolean isTransient(Field field) {
        return field.getAnnotation(Transient.class) != null;
    }

    public static boolean isForeign(Field field) {
        return field.getAnnotation(Foreign.class) != null;
    }

    public static boolean isSimpleColumnType(Field field) {
        Class<?> clazz = field.getType();
        return isSimpleColumnType(clazz);
    }

    public static boolean isSimpleColumnType(Class<?> columnType) {
        return columnType.isPrimitive() ||
                columnType.equals(String.class) ||
                columnType.equals(Integer.class) ||
                columnType.equals(Long.class) ||
                columnType.equals(Date.class) ||
                columnType.equals(java.sql.Date.class) ||
                columnType.equals(Boolean.class) ||
                columnType.equals(Float.class) ||
                columnType.equals(Double.class) ||
                columnType.equals(Byte.class) ||
                columnType.equals(Short.class) ||
                columnType.equals(CharSequence.class) ||
                columnType.equals(Character.class);
    }

    public static boolean isUnique(Field field) {
        return field.getAnnotation(Unique.class) != null;
    }

    public static boolean isNotNull(Field field) {
        return field.getAnnotation(NotNull.class) != null;
    }

    /**
     * @param field
     * @return check.value or null
     */
    public static String getCheck(Field field) {
        Check check = field.getAnnotation(Check.class);
        if (check != null) {
            return check.value();
        } else {
            return null;
        }
    }

    public static Object valueStr2SimpleTypeFieldValue(Class<?> columnFieldType, final String valueStr) {
        Object value = null;
        if (isSimpleColumnType(columnFieldType) && valueStr != null) {
            if (columnFieldType.equals(String.class) || columnFieldType.equals(CharSequence.class)) {
                value = valueStr;
            } else if (columnFieldType.equals(int.class) || columnFieldType.equals(Integer.class)) {
                value = Integer.valueOf(valueStr);
            } else if (columnFieldType.equals(long.class) || columnFieldType.equals(Long.class)) {
                value = Long.valueOf(valueStr);
            } else if (columnFieldType.equals(java.sql.Date.class)) {
                value = new java.sql.Date(Long.valueOf(valueStr));
            } else if (columnFieldType.equals(Date.class)) {
            	if(valueStr.contains("-")){
            		value = DateUtils.parse(valueStr);//new Date(Long.valueOf(valueStr));
            	}else{
            		value = new Date(Long.valueOf(valueStr));
            	}
            } else if (columnFieldType.equals(boolean.class) || columnFieldType.equals(Boolean.class)) {
                value = ColumnUtils.convert2Boolean(valueStr);
            } else if (columnFieldType.equals(float.class) || columnFieldType.equals(Float.class)) {
                value = Float.valueOf(valueStr);
            } else if (columnFieldType.equals(double.class) || columnFieldType.equals(Double.class)) {
                value = Double.valueOf(valueStr);
            } else if (columnFieldType.equals(byte.class) || columnFieldType.equals(Byte.class)) {
                value = Byte.valueOf(valueStr);
            } else if (columnFieldType.equals(short.class) || columnFieldType.equals(Short.class)) {
                value = Short.valueOf(valueStr);
            } else if (columnFieldType.equals(char.class) || columnFieldType.equals(Character.class)) {
                value = valueStr.charAt(0);
            }
        }
        return value;
    }

    public static Class<?> getForeignEntityType(com.lidroid.xutils.db.table.Foreign foreignColumn) {
        Class<?> result = (Class<?>) foreignColumn.getColumnField().getType();
        if (result.equals(SQLiteLazyLoader.class) || result.equals(List.class)) {
            result = (Class<?>) ((ParameterizedType) foreignColumn.getColumnField().getGenericType()).getActualTypeArguments()[0];
        }
        return result;
    }

    public static Boolean convert2Boolean(final Object value) {
        if (value != null) {
            String valueStr = value.toString();
            return valueStr.length() == 1 ? "1".equals(valueStr) : Boolean.valueOf(valueStr);
        }
        return false;
    }

    public static Object convert2DbColumnValueIfNeeded(final Object value) {
        if (value != null) {
            if (value instanceof Boolean) {
                return ((Boolean) value) ? 1 : 0;
            } else if (value instanceof java.sql.Date) {
                return ((java.sql.Date) value).getTime();
            } else if (value instanceof Date) {
                return DateUtils.formatDateTime((Date) value);//((Date) value).getTime();
            }
        }
        return value;
    }

    public static String fieldType2DbType(Class<?> fieldType) {
        if (fieldType.equals(int.class) ||
                fieldType.equals(Integer.class) ||
                fieldType.equals(boolean.class) ||
                fieldType.equals(Boolean.class) ||
                fieldType.equals(java.sql.Date.class) ||
                fieldType.equals(long.class) ||
                fieldType.equals(Long.class) ||
                fieldType.equals(byte.class) ||
                fieldType.equals(Byte.class) ||
                fieldType.equals(short.class) ||
                fieldType.equals(Short.class)) {
            return "INTEGER";
        } else if (fieldType.equals(float.class) ||
                fieldType.equals(Float.class)) {
            return "Float";
        } else if (fieldType.equals(double.class) ||
                fieldType.equals(Double.class)) {
            return "Double";
        } else if (fieldType.equals(Date.class)) {
            return "Varchar(19)";
        }
        return "Varchar(100)";
    }

    private static boolean isStartWithIs(final String fieldName) {
        return fieldName != null && fieldName.startsWith("is");
    }

    private static Method getBooleanColumnGetMethod(Class<?> entityType, final String fieldName) {
        String methodName = "is" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        if (isStartWithIs(fieldName)) {
            methodName = fieldName;
        }
        try {
            return entityType.getDeclaredMethod(methodName);
        } catch (NoSuchMethodException e) {
            LogUtils.d(methodName + " not exist");
        }
        return null;
    }

    private static Method getBooleanColumnSetMethod(Class<?> entityType, Field field) {
        String fieldName = field.getName();
        String methodName = null;
        if (isStartWithIs(field.getName())) {
            methodName = "set" + fieldName.substring(2, 3).toUpperCase() + fieldName.substring(3);
        } else {
            methodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        }
        try {
            return entityType.getDeclaredMethod(methodName, field.getType());
        } catch (NoSuchMethodException e) {
            LogUtils.d(methodName + " not exist");
        }
        return null;
    }

}
