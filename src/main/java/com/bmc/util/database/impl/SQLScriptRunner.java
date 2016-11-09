package com.bmc.util.database.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.bmc.util.database.DatabaseHelper;
import com.bmc.util.database.DatabaseHelper.DATABASE_TYPE;
import com.bmc.util.io.GenericFileHelper;

public class SQLScriptRunner {

	public static void executeSQLFile(DatabaseHelper dbHelper, String sqlfileName,String sqlStatementsDelimeter) 
			throws SQLException, IOException {
		executeSQLFile(dbHelper, sqlfileName, sqlStatementsDelimeter,true);
	}

	public static void executeSQLFile(DatabaseHelper dbHelper, String sqlfileName, String sqlStatementsDelimeter,
			boolean abortIfStatementFails)
 throws SQLException, IOException {

		List<String> skipLineDelimeter = new LinkedList<String>();
		Connection connection = dbHelper.getConnection();

		String comment = "--";
		String[] skipLines = null;

		if (dbHelper.getDatabaseType() == DATABASE_TYPE.ORACLE) {

			try {

				URL url = new SQLScriptRunner().getClass().getResource("/conf/database.properties");
				Properties prop = new Properties();
				prop.load(url.openStream());

				// get the property value and print it out
				skipLines = prop.getProperty("database.oracle.skip.execution.lines").split(",");
				logger.info("database.oracle.skip.execution.lines for oracle DB are " + Arrays.toString(skipLines));

			} catch (Exception ae) {
				ae.printStackTrace();
			}

			for (int i = 0; i < skipLines.length; i++) {
				skipLineDelimeter.add(skipLines[i]);
			}
			comment = "--";
		}
		
		if (dbHelper.getDatabaseType() == DATABASE_TYPE.SQLSERVER) {

			try {

				URL url = new SQLScriptRunner().getClass().getResource("/conf/database.properties");
				Properties prop = new Properties();
				prop.load(url.openStream());

				// get the property value and print it out
				skipLines = prop.getProperty("database.sqlserver.skip.execution.lines").split(",");
				logger.info("database.sqlserver.skip.execution.lines for SQL Server DB are " + Arrays.toString(skipLines));

			} catch (Exception ae) {
				ae.printStackTrace();
			}

			for (int i = 0; i < skipLines.length; i++) {
				skipLineDelimeter.add(skipLines[i]);
			}
			comment = "--";
		}
		
		if (dbHelper.getDatabaseType() == DATABASE_TYPE.SYBASE) {

			try {

				URL url = new SQLScriptRunner().getClass().getResource("/conf/database.properties");
				Properties prop = new Properties();
				prop.load(url.openStream());

				// get the property value and print it out
				skipLines = prop.getProperty("database.sybase.skip.execution.lines").split(",");
				logger.info("database.sybase.skip.execution.lines for Sybase DB are " + Arrays.toString(skipLines));

			} catch (Exception ae) {
				ae.printStackTrace();
			}

			for (int i = 0; i < skipLines.length; i++) {
				skipLineDelimeter.add(skipLines[i]);
			}
			comment = "--";
		}
		
		logger.info("skipLineDelimeter for database are " + skipLineDelimeter);
		logger.info("Executing SQL file " + sqlfileName);
		File sqlFile=new File(sqlfileName);
		if(!sqlFile.exists())
		{
			IOException e=new FileNotFoundException("SQL file "+sqlfileName+" not found.");
			logger.fatal("SQL file does not exist ",e);
			throw e;
		}
		else
		{
			logger.info("Found SQL file "+sqlFile.getCanonicalPath());
		}
		List<String> sqls = GenericFileHelper.parseSQLFile(
				// "C:\\e_drive\\share\\portal\\portal\\SDD\\Patch-installer\\build\\testing\\Oracle_Typical_SQL.sql"),
				sqlFile, true, sqlStatementsDelimeter, 0, skipLineDelimeter, comment);
		for (String sql : sqls) {
			logger.info("SQL script execution started " );
			logger.debug("SQL script execution started \n" + sql);

			SQLException sqlException = null;
			Statement statement = connection.createStatement();
			int affected = 0;
			try {

				// if all the lines are commented in the SQL, do not execute it.
				if (sql != null && !sql.equals(""))
					affected = statement.executeUpdate(sql);
				logger.debug("SQL script execution completed successfully");
			} catch (SQLException sqe) {
				sqlException = sqe;
				logger.fatal("SQL script execution completed abnormally "+sql +"\n"+ sqe,sqe);
			} finally {
				statement.close();
			}
			if (sqlException != null) {
				if (abortIfStatementFails == true)
					throw sqlException;
				else
					logger.info(
							"\nSQL script execution completed abnormally. Since abortIfStatementFails="
									+ abortIfStatementFails + ". Continuing...");
			}

		}

	}

	static Logger logger = Logger.getLogger(SQLScriptRunner.class);
}
