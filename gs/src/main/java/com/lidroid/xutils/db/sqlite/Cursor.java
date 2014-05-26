package com.lidroid.xutils.db.sqlite;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.dbutils.DbUtils;

public class Cursor {

	private ResultSet resultSet;
	private Statement statement;
	private Connection connection;

	public Cursor(Connection connection, Statement statement, ResultSet rs) {
		this.connection = connection;
		this.statement = statement;
		this.resultSet = rs;
	}

	public int getColumnCount() throws SQLException {
		return resultSet.getMetaData().getColumnCount();
	}

	public String getColumnName(int i) throws SQLException {
		return resultSet.getMetaData().getColumnName(i+1);
	}

	public String getString(int i) throws SQLException {
		return resultSet.getString(i+1);
	}

	public boolean moveToNext() throws SQLException {
		return resultSet.next();
	}

	public void close() {
		DbUtils.closeQuietly(resultSet);
		DbUtils.closeQuietly(statement);
		DbUtils.closeQuietly(connection);
	}

	public int getInt(int i) throws SQLException {
		return resultSet.getInt(i+1);
	}

}
