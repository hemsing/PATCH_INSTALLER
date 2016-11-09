package com.bmc.util.database.impl;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import com.microsoft.sqlserver.jdbc.*;
import sun.security.x509.IssuerAlternativeNameExtension;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import com.sybase.jdbc3.jdbc.SybDriver;
import com.bmc.util.database.DatabaseHelper;

public class DatabasHelperImpl extends AbstractDatabaseHelperImpl {

	Connection conn = null;

	
	@Override
	public Connection createConnection(String url, String login, String password) throws SQLException {

		if (conn != null) {
			return conn;
		}
		// If this is Oracle DB connection
		if (url.toLowerCase().contains(":oracle:")) {
			DatabaseType = DatabaseHelper.DATABASE_TYPE.ORACLE;
			logger.info("Detected type of connection as Oracle " + url);

			try {
				DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
				Class.forName("oracle.jdbc.OracleDriver");
				logger.info("Using the connection URL " + url);
				conn = DriverManager.getConnection(url, login, password);
				setDatabaseType(DATABASE_TYPE.ORACLE);
				return conn;
			} catch (ClassNotFoundException ae) {
				ae.printStackTrace();
			}
		}
		
		if (url.toLowerCase().contains(":sqlserver:")) {
			//jdbc:sqlserver://[serverName[\instanceName][:portNumber]
			DatabaseType = DatabaseHelper.DATABASE_TYPE.SQLSERVER;
			logger.info("Detected type of connection as SQL Server " + url);

			try {
				DriverManager.registerDriver(new com.microsoft.sqlserver.jdbc.SQLServerDriver());
				Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
				setDatabaseType(DATABASE_TYPE.SQLSERVER);
				logger.info("Using the connection URL " + url);
				conn = DriverManager.getConnection(url, login, password);
				return conn;
			} catch (ClassNotFoundException ae) {
				ae.printStackTrace();
			}
		}
		
		if (url.toLowerCase().contains(":sybase:")) {
			// jdbc:sybase:Tds:host:port?ServiceName=database
			// Connection con = DriverManager.getConnection(
			// "jdbc:sybase:Tds:localhost:2638?ServiceName=demo", "DBA", "sql");
			DatabaseType = DatabaseHelper.DATABASE_TYPE.SYBASE;
			logger.info("Detected type of connection as Sybase Server " + url);

			try {
				DriverManager.registerDriver(new com.sybase.jdbc3.jdbc.SybDriver());
				Class.forName("com.sybase.jdbc3.jdbc.SybDriver");
				// DriverManager.setLoginTimeout( 900 );
				setDatabaseType(DATABASE_TYPE.SYBASE);
				logger.info("Using the connection URL " + url);
				conn = DriverManager.getConnection(url, login, password);
				return conn;
			} catch (ClassNotFoundException ae) {
				ae.printStackTrace();
			}
		}

		if (conn == null) {
			throw new SQLException("Not able to create the connection");
		}
		return conn;

	}

	@Override
	public String removeComments(String sql) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSafeSQLString(String sql) {
		// TODO Auto-generated method stub
		return sql;
	}

	@Override
	public void closeConnection(Connection connection) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void initLogger() {
		DOMConfigurator.configure(DatabasHelperImpl.class.getResource("/conf/log4j-config.xml"));
		// Log in console in and log file
		logger.debug("Log4j appender configuration is successful !!");
		logger.info("hello info");
	}

	static Logger logger = Logger.getLogger(DatabasHelperImpl.class);
	DatabaseHelper.DATABASE_TYPE DatabaseType = DatabaseHelper.DATABASE_TYPE.UNKNOWN;

	@Override
	public DATABASE_TYPE getDatabaseType() {
		// TODO Auto-generated method stub
		return DatabaseType;

	}

	@Override
	public Connection getConnection() throws SQLException {
		if (conn != null) {
			return conn;
		}
		return null;
	}

	@Override
	public void setDatabaseType(DATABASE_TYPE type) {
		DatabaseType=type;
		
	}
}
