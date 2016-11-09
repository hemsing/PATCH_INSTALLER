package com.bmc.util.misc;
import static org.junit.Assert.*;
import com.bmc.util.database.DatabaseHelper;
import com.bmc.util.database.impl.DatabasHelperImpl;
import com.bmc.util.io.GenericFileHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
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

public class EnvironmentHelperJunit extends TestCase {

	@BeforeClass
	public static void setUpBeforeClass() {
		

	}

	@AfterClass
	public static void tearDownAfterClass() {		
		
	}

	@Before
	public void setUp() throws SQLException {
		LoggerHelper.initLogger();	
	}

	@Test
	public void testGetLocalHost() throws Exception{
		
		String localHost=EnvironmentHelper.getEnvironmentValue("$HOSTNAME");
		
		if(localHost==null||localHost.isEmpty())
		{
		logger.error("Not able to get the localHost= "+localHost);
		throw new UnknownHostException();
		}
		else
		{
			logger.info("Received localHost = "+localHost);
		}
		
	}

	@Test
	public void testGetFQDN() throws Exception{
		
		String localHost=EnvironmentHelper.getEnvironmentValue("$FQDN");
		
		if(localHost==null||localHost.isEmpty())
		{
		logger.error("Not able to get the FQDN= "+localHost);
		throw new UnknownHostException();
		}
		else
		{
			logger.info("Received FQDN= "+localHost);
		}
		
	}
	
	static Logger logger = Logger.getLogger(EnvironmentHelperJunit.class);

}