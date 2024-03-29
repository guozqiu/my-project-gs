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

package com.lidroid.xutils.db.sqlite;

import java.util.List;

import com.lidroid.xutils.db.table.Foreign;
import com.lidroid.xutils.db.table.TableUtils;
import com.lidroid.xutils.exception.DbException;

public class SQLiteLazyLoader<T> {
    private Foreign foreignColumn;
    private Object columnValue;

    public SQLiteLazyLoader(Class<?> entityType, String columnName, Object columnValue) {
        this.foreignColumn = (Foreign) TableUtils.getColumnOrId(entityType, columnName);
        this.columnValue = columnValue;
    }

    public SQLiteLazyLoader(Foreign foreignColumn, Object columnValue) {
        this.foreignColumn = foreignColumn;
        this.columnValue = columnValue;
    }

    public List<T> getAllFromDb() throws DbException {
        List<T> entities = null;
        if (foreignColumn != null && foreignColumn.db != null) {
            entities = foreignColumn.db.findAll(
                    Selector.from(foreignColumn.getForeignEntityType()).
                            where(WhereBuilder.b(foreignColumn.getForeignColumnName(), "=", columnValue)));
        }
        return entities;
    }

    public T getFirstFromDb() throws DbException {
        T entity = null;
        if (foreignColumn != null && foreignColumn.db != null) {
            entity = foreignColumn.db.findFirst(
                    Selector.from(foreignColumn.getForeignEntityType()).
                            where(WhereBuilder.b(foreignColumn.getForeignColumnName(), "=", columnValue)));
        }
        return entity;
    }

    public Object getColumnValue() {
        return columnValue;
    }
}
