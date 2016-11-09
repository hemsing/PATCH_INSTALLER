package com.bmc.util.database.impl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import com.bmc.util.database.DatabaseHelper;


public abstract class AbstractDatabaseHelperImpl implements DatabaseHelper {

	public abstract Connection createConnection(String url, String login, String password) throws SQLException;

	public String getDatabaseVendorVersion() throws SQLException {
		Connection connection = getConnection();
		String result = "";
		try {
			DatabaseMetaData databaseMetaData = connection.getMetaData();
			result = databaseMetaData.getDatabaseProductVersion();
		} catch (Throwable t) {
			result = "";
		}
		return result;
	}

	public boolean isAvailable() throws SQLException {
		Connection connection = getConnection();
		boolean result = true;
		try {

			// NOTE: isClosed is NOT guaranteed to give a reliable indication of
			// the validity of the connection. This method is provided as a
			// default.
			// It is highly recommended that implementations override this
			// method
			// with something more reliable
			result = !connection.isClosed();
		} catch (SQLException sqle) {
			try {
				connection.close();
			} catch (Throwable t) {

				// ignore
			}
			result = false;
		}
		return result;
	}

	public abstract String removeComments(String sql);

	public abstract String getSafeSQLString(String sql);

	public int executePreparedStatement(String sql, String[] parameters) throws SQLException {
		Connection connection = getConnection();
		SQLException sqlException = null;
		int affected = 0;
		PreparedStatement ps = connection.prepareStatement(getSafeSQLString(sql));
		try {
			if (parameters != null) {
				for (int i = 0; i < parameters.length; i++) {
					ps.setString(i + 1, parameters[i]);
				}
			}
			ps.execute();
			affected = ps.getUpdateCount();
			if (affected < 0) {
				ResultSet rs = ps.getResultSet();
				affected = 0;
				while (rs.next()) {
					affected++;
				}
				rs.close();
			}
		} catch (SQLException sqe) {
			sqlException = sqe;
		} finally {
			ps.close();
		}
		if (sqlException != null) {
			throw sqlException;
		}
		return affected;
	}

	public int executeStatement(String sql) throws SQLException {
		SQLException sqlException = null;
		Statement statement = getConnection().createStatement();
		int affected = 0;
		
		String safeSql = getSafeSQLString(sql);
		logger.info("executing SQL " + safeSql);
		try {
			affected = statement.executeUpdate(safeSql);
		} catch (SQLException sqe) {
			sqlException = sqe;
		} finally {
			statement.close();
		}
		if (sqlException != null) {
			throw sqlException;
		}
		return affected;
	}

	public ResultSet executeStatementWithResultSet(String sql) throws SQLException {
		Connection connection = getConnection();
		Statement statement = connection.createStatement();
		return statement.executeQuery(sql);
	}


	public static final String SQL_DELIMITER_SLASH_REGEX = "\r\\s*/\\s*\r|\r\\s*/\\s*\n|\n\\s*/\\s*\r|\n\\s*/\\s*\n|\r\\s*/\\s*$|\n\\s*/\\s*$";
	public static final String SQL_DELIMITER_SEMICOLON_REGEX = ";";

	public static final String SQL_DELIMITER_GO_REGEX = "\r[Gg][Oo]\\s*\r" + "|" + "\r[Gg][Oo]\\s*\n" + "|"
			+ "\n[Gg][Oo]\\s*\r" + "|" + "\n[Gg][Oo]\\s*\n" + "|" + "\r[Gg][Oo]$" + "|" + "\n[Gg][Oo]$";
	static Logger logger = Logger.getLogger(AbstractDatabaseHelperImpl.class);

}
