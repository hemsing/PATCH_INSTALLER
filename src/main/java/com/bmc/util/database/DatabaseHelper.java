package com.bmc.util.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface DatabaseHelper {
	public Connection createConnection(String url, String login, String password) throws SQLException;

	public Connection getConnection() throws SQLException;

	public void closeConnection(Connection connection) throws SQLException;

	public int executeStatement(String sql) throws SQLException;

	public ResultSet executeStatementWithResultSet(String sql) throws SQLException;

	public int executePreparedStatement(String sql, String[] parameters) throws SQLException;

	public DATABASE_TYPE getDatabaseType();
	
	public void setDatabaseType(DATABASE_TYPE type);

	public static enum DATABASE_TYPE {
		ORACLE, SQLSERVER, SYBASE,UNKNOWN
	};
}
