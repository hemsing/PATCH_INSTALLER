package com.bmc.util.database.impl;

import static org.junit.Assert.*;
import com.bmc.util.database.DatabaseHelper;
import com.bmc.util.database.impl.DatabasHelperImpl;
import com.bmc.util.io.GenericFileHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import junit.framework.TestCase;

public class DatabasHelperImplJunitTest extends TestCase {

	@BeforeClass
	public static void setUpBeforeClass() {

	}

	@AfterClass
	public static void tearDownAfterClass() {
		System.out.println("tearDownAfterClass*************************************************************");
	}

	@Before
	public void setUp() throws SQLException {
		System.out.println("setUp*************************************************************");
		File log = new File("log4j-config.xml");
		if (log.exists()) {
			DOMConfigurator.configure("log4j-config.xml");
		} else {
			DOMConfigurator.configure(DatabasHelperImpl.class.getResource("/conf/log4j-config.xml"));
		}

		Properties prop = new Properties();
		InputStream input = null;
		try {
			input = new FileInputStream("conf/junit.properties");

			// load a properties file
			prop.load(input);

			// get the property value and print it out
			db_oracle_url = prop.getProperty("DB.ORACLE.URL");
			db_oracle_login = prop.getProperty("DB.ORACLE.LOGIN");
			db_oracle_password = prop.getProperty("DB.ORACLE.PASSWORD");

			db_sqlserver_url = prop.getProperty("DB.SQLSERVER.URL");
			db_sqlserver_login = prop.getProperty("DB.SQLSERVER.LOGIN");
			db_sqlserver_password = prop.getProperty("DB.SQLSERVER.PASSWORD");

			db_sybase_url = prop.getProperty("DB.SYBASE.URL");
			db_sybase_login = prop.getProperty("DB.SYBASE.LOGIN");
			db_sybase_password = prop.getProperty("DB.SYBASE.PASSWORD");

			sql_oracle_files_to_be_executed = prop.getProperty("DB.ORACLE.SQL_FILES").split(",");
			sql_oracle_statements_to_be_executed_file = prop.getProperty("DB.ORACLE.SQL_STATEMENTS_FILE");

			sql_sqlserver_files_to_be_executed = prop.getProperty("DB.SQLSERVER.SQL_FILES").split(",");
			sql_sqlserver_statements_to_be_executed_file = prop.getProperty("DB.SQLSERVER.SQL_STATEMENTS_FILE");

			sql_sybase_files_to_be_executed = prop.getProperty("DB.SYBASE.SQL_FILES").split(",");
			sql_sybase_statements_to_be_executed_file = prop.getProperty("DB.SYBASE.SQL_STATEMENTS_FILE");

			logger.info("db_oracle_url=" + db_oracle_url);
			logger.info("db_oracle_login=" + db_oracle_login);
			logger.info("db_oracle_password=" + db_oracle_password);

			logger.info("db_sqlserver_url=" + db_sqlserver_url);
			logger.info("db_sqlserver_login=" + db_sqlserver_login);
			logger.info("db_sqlserver_password=" + db_sqlserver_password);

			logger.info("db_sybase_url=" + db_sybase_url);
			logger.info("db_sybase_login=" + db_sybase_login);
			logger.info("db_sybase_password=" + db_sybase_password);

			logger.info("sql_sybase_files_to_be_executed= " + sql_sybase_files_to_be_executed);

		} catch (IOException ae) {
			ae.printStackTrace();

		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		db_oracle.createConnection(db_oracle_url, db_oracle_login, db_oracle_password);
		db_sql_server.createConnection(db_sqlserver_url, db_sqlserver_login, db_sqlserver_password);
		db_sybase_server.createConnection(db_sybase_url, db_sybase_login, db_sybase_password);
	}

	@After
	public void tearDown() {
		logger.info("tearDown setUpBeforeClass**************************************************************");
	}

	@Test
	public void testOracleSQLStatement() throws Exception {
		List<String> sql_oracle_statements = GenericFileHelper
				.readLinesFromFile("tests/" + sql_oracle_statements_to_be_executed_file);
		for (String s : sql_oracle_statements) {
			logger.info("Executing " + s);
			db_oracle.executeStatement(s);
		}
	}

	@Test
	public void testOracleSQLFile() throws Exception {
		if (db_oracle.isAvailable() == false) {
			throw new Exception("Database not alive");
		}
		for (int i = 0; i < sql_oracle_files_to_be_executed.length; i++) {
			SQLScriptRunner.executeSQLFile(db_oracle, "tests/" + sql_oracle_files_to_be_executed[i], "/", true);
		}
	}

	@Test
	public void testSQLServerSQLFile() throws Exception {
		if (db_oracle.isAvailable() == false) {
			throw new Exception("Database not alive");
		}
		for (int i = 0; i < sql_sqlserver_files_to_be_executed.length; i++) {
			SQLScriptRunner.executeSQLFile(db_oracle, "tests\\" + sql_sqlserver_files_to_be_executed[i], "/", true);
		}
	}

	@Test
	public void testSQLServerSQLStatement() throws Exception {
		// try
		{

			List<String> sql_sqlserver_statements = GenericFileHelper
					.readLinesFromFile("tests\\" + sql_sqlserver_statements_to_be_executed_file);
			for (String s : sql_sqlserver_statements) {
				logger.info("Executing " + s);
				db_sql_server.executeStatement(s);
			}
		}
	}

	@Test
	public void testSybaseSQLFile() throws Exception {
		{
			if (db_sybase_server.isAvailable() == false) {
				throw new Exception("Database not alive");
			}
			logger.info("Executing " + sql_sybase_files_to_be_executed);
			for (int i = 0; i < sql_sybase_files_to_be_executed.length; i++) {
				SQLScriptRunner.executeSQLFile(db_sybase_server, "tests\\" + sql_sybase_files_to_be_executed[i], "GO",
						true);
			}

		}

	}

	@Test
	public void testSybaseSQLStatement() throws Exception {
		{

			List<String> sql_sybase_statements = GenericFileHelper
					.readLinesFromFile("tests\\" + sql_sybase_statements_to_be_executed_file);
			for (String s : sql_sybase_statements) {
				logger.info("Executing " + s);
				db_sybase_server.executeStatement(s);
			}
		}
	}

	@Test
	public void testSybaseConnection() throws Exception {

		if (!db_sybase_server.isAvailable()) {
			throw new Exception("Sybase DB connection can not be created");
		}
	}

	private DatabasHelperImpl db_oracle = new DatabasHelperImpl();
	private DatabasHelperImpl db_sql_server = new DatabasHelperImpl();
	private DatabasHelperImpl db_sybase_server = new DatabasHelperImpl();

	private static String db_oracle_url;
	private static String db_oracle_login;
	private static String db_oracle_password;

	private static String db_sqlserver_url;
	private static String db_sqlserver_login;
	private static String db_sqlserver_password;

	private static String db_sybase_url;
	private static String db_sybase_login;
	private static String db_sybase_password;

	private static String[] sql_oracle_files_to_be_executed;
	private static String sql_oracle_statements_to_be_executed_file;

	private static String[] sql_sqlserver_files_to_be_executed;
	private static String sql_sqlserver_statements_to_be_executed_file;

	private static String[] sql_sybase_files_to_be_executed;
	private static String sql_sybase_statements_to_be_executed_file;

	static Logger logger = Logger.getLogger(DatabasHelperImplJunitTest.class);

}