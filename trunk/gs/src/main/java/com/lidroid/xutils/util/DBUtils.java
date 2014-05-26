package com.lidroid.xutils.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;

import com.lidroid.xutils.db.sqlite.Cursor;
import com.lidroid.xutils.db.sqlite.CursorUtils;
import com.lidroid.xutils.db.sqlite.DbModelSelector;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.SqlInfo;
import com.lidroid.xutils.db.sqlite.SqlInfoBuilder;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.db.table.DbModel;
import com.lidroid.xutils.db.table.Id;
import com.lidroid.xutils.db.table.KeyValue;
import com.lidroid.xutils.db.table.Table;
import com.lidroid.xutils.db.table.TableUtils;
import com.lidroid.xutils.exception.DbException;

public class DBUtils {

    //*************************************** create instance ****************************************************

    /**
     * key: dbName
     */
    private static HashMap<String, DBUtils> daoMap = new HashMap<String, DBUtils>();

    private DaoConfig config;
    private boolean debug = false;
    private boolean allowTransaction = false;
    private BasicDataSource dataSource;

    private DBUtils(DaoConfig config) throws DbException {
        if (config == null) {
            throw new RuntimeException("daoConfig is null");
        }
        try {
			dataSource = new BasicDataSource();
	        dataSource.setDriverClassName("org.sqlite.JDBC");
	        dataSource.setUrl("jdbc:sqlite:"+config.getDbName());
	        dataSource.setValidationQuery("select 1");
			
		} catch (Exception e) {
			throw new DbException(e);
		}
        this.config = config;
    }


    public synchronized static DBUtils getInstance(String dbName) throws DbException {
        DBUtils dao = daoMap.get(dbName);
        if (dao == null) {
        	DaoConfig daoConfig = new DaoConfig();
        	daoConfig.setDbName(dbName);
            dao = new DBUtils(daoConfig);
            daoMap.put(daoConfig.getDbName(), dao);
        }
        return dao;
    }
    


    public DBUtils configDebug(boolean debug) {
        this.debug = debug;
        return this;
    }

    public DBUtils configAllowTransaction(boolean allowTransaction) {
        this.allowTransaction = allowTransaction;
        return this;
    }


    //*********************************************** operations ********************************************************

    public void saveOrUpdate(Object entity) throws DbException {
        try {
            beginTransaction();

            saveOrUpdateWithoutTransaction(entity);

            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
    }

    public void saveOrUpdate(List<Object> entities) throws DbException {
        try {
            beginTransaction();

            for (Object entity : entities) {
                saveOrUpdateWithoutTransaction(entity);
            }

            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
    }

    public void replace(Object entity) throws DbException {
        try {
            beginTransaction();

            replaceWithoutTransaction(entity);

            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
    }

    public void replace(List<Object> entities) throws DbException {
        try {
            beginTransaction();

            for (Object entity : entities) {
                replaceWithoutTransaction(entity);
            }

            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
    }

    public void save(Object entity) throws DbException {
        try {
            beginTransaction();

            saveWithoutTransaction(entity);

            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
    }

    public void save(List<Object> entities) throws DbException {
        try {
            beginTransaction();

            for (Object entity : entities) {
                saveWithoutTransaction(entity);
            }

            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
    }

    public boolean saveBindingId(Object entity) throws DbException {
        boolean result = false;
        try {
            beginTransaction();

            result = saveBindingIdWithoutTransaction(entity);

            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
        return result;
    }

    public void saveBindingId(List<Object> entities) throws DbException {
        try {
            beginTransaction();

            for (Object entity : entities) {
                if (!saveBindingIdWithoutTransaction(entity)) {
                    throw new DbException("saveBindingId error, transaction will not commit!");
                }
            }

            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
    }


    public void delete(Object entity) throws DbException {
        try {
            beginTransaction();

            deleteWithoutTransaction(entity);

            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
    }

    public void delete(List<Object> entities) throws DbException {
        try {
            beginTransaction();

            for (Object entity : entities) {
                deleteWithoutTransaction(entity);
            }

            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
    }

    public void deleteById(Class<?> entityType, Object idValue) throws DbException {
        try {
            beginTransaction();

            execNonQuery(SqlInfoBuilder.buildDeleteSqlInfo(entityType, idValue));

            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
    }

    public void delete(Class<?> entityType, WhereBuilder whereBuilder) throws DbException {
        try {
            beginTransaction();

            SqlInfo sql = SqlInfoBuilder.buildDeleteSqlInfo(entityType, whereBuilder);
            execNonQuery(sql);

            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
    }

    public void update(Object entity) throws DbException {
        try {
            beginTransaction();

            updateWithoutTransaction(entity);

            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
    }

    public void update(List<Object> entities) throws DbException {
        try {
            beginTransaction();

            for (Object entity : entities) {
                updateWithoutTransaction(entity);
            }

            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
    }

    public void update(Object entity, WhereBuilder whereBuilder) throws DbException {
        try {
            beginTransaction();

            execNonQuery(SqlInfoBuilder.buildUpdateSqlInfo(this, entity, whereBuilder));

            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T findById(Class<T> entityType, Object idValue) throws DbException {
        Id id = Table.get(entityType).getId();
        Selector selector = Selector.from(entityType).where(WhereBuilder.b(id.getColumnName(), "=", idValue));
        Cursor cursor = execQuery(selector.limit(1).toString());
        try {
            if (cursor.moveToNext()) {
                return (T) CursorUtils.getEntity(this, cursor, selector.getEntityType());
            }
        } catch (Exception e) {
            throw new DbException(e);
        } finally {
            IOUtils.closeQuietly(cursor);
        }
        return null;
    }

    public <T> T findFirst(Object entity) throws DbException {
        Selector selector = Selector.from(entity.getClass());
        List<KeyValue> entityKvList = SqlInfoBuilder.entity2KeyValueList(this, entity);
        if (entityKvList != null) {
            WhereBuilder wb = WhereBuilder.b();
            for (KeyValue keyValue : entityKvList) {
                wb.append(keyValue.getKey(), "=", keyValue.getValue());
            }
            selector.where(wb);
        }
        return findFirst(selector);
    }

    public <T> List<T> findAll(Object entity) throws DbException {
        Selector selector = Selector.from(entity.getClass());
        List<KeyValue> entityKvList = SqlInfoBuilder.entity2KeyValueList(this, entity);
        if (entityKvList != null) {
            WhereBuilder wb = WhereBuilder.b();
            for (KeyValue keyValue : entityKvList) {
                wb.append(keyValue.getKey(), "=", keyValue.getValue());
            }
            selector.where(wb);
        }
        return findAll(selector);
    }

    @SuppressWarnings("unchecked")
    public <T> T findFirst(Selector selector) throws DbException {
        Cursor cursor = execQuery(selector.limit(1).toString());
        try {
            if (cursor.moveToNext()) {
                return (T) CursorUtils.getEntity(this, cursor, selector.getEntityType());
            }
        } catch (Exception e) {
            throw new DbException(e);
        } finally {
            IOUtils.closeQuietly(cursor);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> findAll(Selector selector) throws DbException {
        Cursor cursor = execQuery(selector.toString());
        List<T> result = new ArrayList<T>();
        try {
            while (cursor.moveToNext()) {
                result.add((T) CursorUtils.getEntity(this, cursor, selector.getEntityType()));
            }
        } catch (Exception e) {
            throw new DbException(e);
        } finally {
            IOUtils.closeQuietly(cursor);
        }
        return result;
    }

    public DbModel findDbModelFirst(String sql) throws DbException {
        Cursor cursor = execQuery(sql);
        try {
            if (cursor.moveToNext()) {
                return CursorUtils.getDbModel(cursor);
            }
        } catch (Exception e) {
            throw new DbException(e);
        } finally {
            IOUtils.closeQuietly(cursor);
        }
        return null;
    }

    public DbModel findDbModelFirst(DbModelSelector selector) throws DbException {
        Cursor cursor = execQuery(selector.limit(1).toString());
        try {
            if (cursor.moveToNext()) {
                return CursorUtils.getDbModel(cursor);
            }
        } catch (Exception e) {
            throw new DbException(e);
        } finally {
            IOUtils.closeQuietly(cursor);
        }
        return null;
    }

    public List<DbModel> findDbModelAll(String sql) throws DbException {
        Cursor cursor = execQuery(sql);
        List<DbModel> dbModelList = new ArrayList<DbModel>();
        try {
            while (cursor.moveToNext()) {
                dbModelList.add(CursorUtils.getDbModel(cursor));
            }
        } catch (Exception e) {
            throw new DbException(e);
        } finally {
            IOUtils.closeQuietly(cursor);
        }
        return dbModelList;
    }

    public List<DbModel> findDbModelAll(DbModelSelector selector) throws DbException {
        Cursor cursor = execQuery(selector.toString());
        List<DbModel> dbModelList = new ArrayList<DbModel>();
        try {
            while (cursor.moveToNext()) {
                dbModelList.add(CursorUtils.getDbModel(cursor));
            }
        } catch (Exception e) {
            throw new DbException(e);
        } finally {
            IOUtils.closeQuietly(cursor);
        }
        return dbModelList;
    }

    //******************************************** config ******************************************************

    public static class DaoConfig {
        private String dbName = "lgd_gs.db"; 
        private int dbVersion = 1;


        public String getDbName() {
            return dbName;
        }

        public void setDbName(String dbName) {
            this.dbName = dbName;
        }

        public int getDbVersion() {
            return dbVersion;
        }

        public void setDbVersion(int dbVersion) {
            this.dbVersion = dbVersion;
        }

    }


    //***************************** private operations with out transaction *****************************
    private void saveOrUpdateWithoutTransaction(Object entity) throws DbException {
        if (TableUtils.getIdValue(entity) != null) {
            update(entity);
        } else {
            saveBindingId(entity);
        }
    }

    private void replaceWithoutTransaction(Object entity) throws DbException {
        createTableIfNotExist(entity.getClass());
        execNonQuery(SqlInfoBuilder.buildReplaceSqlInfo(this, entity));
    }

    private void saveWithoutTransaction(Object entity) throws DbException {
        createTableIfNotExist(entity.getClass());
        execNonQuery(SqlInfoBuilder.buildInsertSqlInfo(this, entity));
    }

    private boolean saveBindingIdWithoutTransaction(Object entity) throws DbException {
        createTableIfNotExist(entity.getClass());
        List<KeyValue> entityKvList = SqlInfoBuilder.entity2KeyValueList(this, entity);
        if (entityKvList != null && entityKvList.size() > 0) {
            Table table = Table.get(entity.getClass());
//            ContentValues<String,Object> cv = new ContentValues<String,Object>();
//            LidroidDbUtils.fillContentValues(cv, entityKvList);
            
            SqlInfo insertSqlInfo = SqlInfoBuilder.buildInsertSqlInfo(this, entityKvList);
            
            Integer id;
			try {
				id = getQueryRunner().update(insertSqlInfo.getSql(), insertSqlInfo.getBindingArgs());
			} catch (SQLException e) {
				throw new DbException(e);
			}
            
            if (id == -1) {
                return false;
            }
            table.getId().setValue2Entity(entity, id.toString());
            return true;
        }
        return false;
    }
    
    private QueryRunner getQueryRunner(){
    	return new QueryRunner(dataSource);
    }

    private void deleteWithoutTransaction(Object entity) throws DbException {
        execNonQuery(SqlInfoBuilder.buildDeleteSqlInfo(entity));
    }

    private void updateWithoutTransaction(Object entity) throws DbException {
        execNonQuery(SqlInfoBuilder.buildUpdateSqlInfo(this, entity));
    }

    //************************************************ tools ***********************************


    public void createTableIfNotExist(Class<?> entityType) throws DbException {
        if (!tableIsExist(entityType)) {
            SqlInfo sqlInfo = SqlInfoBuilder.buildCreateTableSqlInfo(entityType);
            execNonQuery(sqlInfo);
        }
    }

    public boolean tableIsExist(Class<?> entityType) throws DbException {
        Table table = Table.get(entityType);
        if (table.isCheckDatabase()) {
            return true;
        }

        Cursor cursor = null;
        try {
            cursor = execQuery("SELECT COUNT(*) AS c FROM sqlite_master WHERE type ='table' AND name ='" + table.getTableName() + "'");
            if (cursor != null && cursor.moveToNext()) {
                int count = cursor.getInt(0);
                if (count > 0) {
                    table.setCheckDatabase(true);
                    return true;
                }
            }

        } catch (Exception e) {
            throw new DbException(e);
        } finally {
            IOUtils.closeQuietly(cursor);
        }

        return false;
    }

    public void dropDb() throws DbException {
        Cursor cursor = null;
        try {
            cursor = execQuery("SELECT name FROM sqlite_master WHERE type ='table'");
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    try {
                        execNonQuery("DROP TABLE " + cursor.getString(0));
                    } catch (Exception e) {
                        LogUtils.e(e.getMessage(), e);
                    }
                }
            }
        } catch (Exception e) {
            throw new DbException(e);
        } finally {
            IOUtils.closeQuietly(cursor);
        }
    }

    public void dropTable(Class<?> entityType) throws DbException {
        try {
            Table table = Table.get(entityType);
            execNonQuery("DROP TABLE " + table.getTableName());
        } catch (Exception e) {
            throw new DbException(e);
        }
    }

    ///////////////////////////////////// exec sql /////////////////////////////////////////////////////
    private void debugSql(String sql) {
        if (config != null && debug) {
            LogUtils.d(sql);
        }
    }

    private void beginTransaction() {
        if (allowTransaction) {
            //database.beginTransaction();
        }
    }

    private void setTransactionSuccessful() {
        if (allowTransaction) {
            //database.setTransactionSuccessful();
        }
    }

    private void endTransaction() {
        if (allowTransaction) {
            //database.endTransaction();
        }
    }


    public void execNonQuery(SqlInfo sqlInfo) throws DbException {
        debugSql(sqlInfo.getSql());
        try {
            if (sqlInfo.getBindingArgs() != null) {
				getQueryRunner().update(sqlInfo.getSql(), sqlInfo.getBindingArgsAsArray());
            } else {
				getQueryRunner().update(sqlInfo.getSql());
            }
        } catch (Exception e) {
            throw new DbException(e);
        }
    }

    public void execNonQuery(String sql) throws DbException {
        debugSql(sql);
        try {
        	getQueryRunner().update(sql);
        } catch (Exception e) {
            throw new DbException(e);
        }
    }

    public Cursor execQuery(SqlInfo sqlInfo) throws DbException {
        debugSql(sqlInfo.getSql());
        try {
        	Connection connection = dataSource.getConnection();
        	PreparedStatement prepareStatement = connection.prepareStatement(sqlInfo.getSql());
        	String[] bindingArgsAsStringArray = sqlInfo.getBindingArgsAsStringArray();
        	
        	QueryRunner queryRunner = new QueryRunner();
        	queryRunner.fillStatement(prepareStatement, bindingArgsAsStringArray);
        	ResultSet resultSet = prepareStatement.executeQuery();
        	
        	return new Cursor(connection, prepareStatement, resultSet);
        } catch (Exception e) {
            throw new DbException(e);
        }
    }

    public Cursor execQuery(String sql) throws DbException {
        debugSql(sql);
        try {
        	Connection connection = dataSource.getConnection();
        	PreparedStatement prepareStatement = connection.prepareStatement(sql);
        	
        	ResultSet resultSet = prepareStatement.executeQuery();
        	
        	return new Cursor(connection, prepareStatement, resultSet);
        } catch (Exception e) {
            throw new DbException(e);
        }
    }

}
