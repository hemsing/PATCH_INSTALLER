package com.bmc.util.io.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.bmc.util.io.GenericFileHelper;

import junit.framework.TestCase;

public class GenericFileHelperImplJunitTest extends TestCase {
	static Logger logger = Logger.getLogger(GenericFileHelperImplJunitTest.class);
	// private static File sourceDirectory, destinationDirectory, deleteFile,
	// extractFolder = null;
	private static String directory = "";
	private static String file = "";
	private static String sourceFile = "";
	private static String destinationFile = "";
	private static String sourceDirectory = "";
	private static String destinationDirectory = "";
	private static String deleteFile = "";
	private static String extractFolder = "";
	private static String zipFile = "TestZip.zip";

	@BeforeClass
	public static void setUpBeforeClass() throws IOException {
	}

	@AfterClass
	public static void tearDownAfterClass() {
		System.out.println("tearDownAfterClass Hellooowww");
	}

	@Before
	public void setUp() throws IOException {
		logger.info("setUp Hellooowww");

		Properties prop = new Properties();
		InputStream input = null;

		input = new FileInputStream("conf\\Junit.properties");

		// load a properties file
		prop.load(input);

		sourceDirectory = (prop.getProperty("IO.SOURCE_DIRECTORY"));
		GenericFileHelper.deleteFile(sourceDirectory);
		GenericFileHelper.createDirectory(sourceDirectory);

		logger.info("IO.SOURCE_DIRECTORY=" + sourceDirectory);

		destinationDirectory = (prop.getProperty("IO.DESTINATION_DIRECTORY"));
		GenericFileHelper.deleteFile(destinationDirectory);
		GenericFileHelper.createDirectory(destinationDirectory);
		logger.info("IO.DESTINATION_DIRECTOR=" + destinationDirectory);

		sourceFile = (prop.getProperty("IO.SOURCE_FILE"));
		GenericFileHelper.createFile(sourceFile);
		logger.info("sourceFile=" + sourceFile);

		destinationFile = (prop.getProperty("IO.DESTINATION_FILE"));

		logger.info("destinationFile=" + destinationFile);

		file = (prop.getProperty("IO.FILE"));

		logger.info("file=" + file);

		directory = (prop.getProperty("IO.DIRECTORY"));

		logger.info("directory=" + directory);

		deleteFile = (prop.getProperty("IO.DELETE_FILE"));
		GenericFileHelper.createFile(deleteFile);

		extractFolder = (prop.getProperty("IO.EXTRACTFOLDER"));
		GenericFileHelper.deleteFile(extractFolder);
		GenericFileHelper.createDirectory(extractFolder);

		logger.info("setUpBeforeClass setUpBeforeClass");

	}

	@After
	public void tearDown() {
		logger.info("tearDown setUpBeforeClass");
	}

	@Test
	public void testCreateDirectory() throws Exception {
		logger.info("Method to creating the folder");
		GenericFileHelper.createDirectory(directory);
		File f = new File(directory);
		if (f.exists() && f.isDirectory()) {
			logger.info("Created the folder");
		} else {

			logger.info("checcking the error");
			throw new Exception(directory + " not created");
		}
	}

	@Test
	public void testCreateFile() throws Exception {
		logger.info("Method to creating the file");
		GenericFileHelper.createFile(file);
		File f = new File(file);
		if (f.exists() && f.isFile()) {
			logger.info("Created the file");
		} else {
			throw new Exception(file + " not created");
		}

	}

	@Test
	public void testCopyFile() throws Exception {
		logger.info(" Method to copy the file is callling ");

		GenericFileHelper.copyFile(sourceFile, destinationFile, true);

		File f = new File(destinationFile);
		if (f.exists() && f.isFile()) {
			logger.info(file + "is copied Successfully");
		}

		else {
			throw new Exception("File is not successfully copied");
		}
	}

	@Test
	public static void testCopyDirectory() throws Exception {

		logger.info("Method to copy the folder is calling ");

		GenericFileHelper.copyDirectory(sourceDirectory, destinationDirectory, true, true);

		File f = new File(destinationDirectory);

		if (f.exists() && f.isDirectory()) {
			logger.info("Directory  is successfully copied");
		}

		else {
			throw new Exception("directory" + destinationDirectory + "does not copied successfully");
		}
	}

	@Test
	public static void testDeleteFile() throws Exception {

		logger.info("Method to delete the file " + deleteFile);

		GenericFileHelper.deleteFile(deleteFile);

		if (!(new File(deleteFile).exists())) {
			logger.info("file is " + deleteFile + " deleted  successfully ");
		} else {
			throw new Exception(file + "is not delete successfully");
		}
	}

	@Test
	public static void testExtractFolder() throws Exception {
		logger.info("Method to extracting the the zip files into targeted folder");

		File f = new File(extractFolder);

		logger.info("Extracting the zip file: " + "tests/ " + zipFile + " into extractFolder " + f.getAbsolutePath());
		GenericFileHelper.extractFolder("tests/" + zipFile, f.getAbsolutePath());

		if (f.exists() && f.isDirectory()) {
			logger.info("Zip file is succesfully extracted in to targeted folder" + f);
		}

		else {

			throw new Exception("Zip file is not extracted in to the targated folder");

		}
	}

	@Test
	public static void testGetParentDirectory() throws Exception

	{
		File f = new File(sourceFile);

		logger.info(GenericFileHelper.getParentDirectory(f));

	}

	@Test
	public static void testRegexPattern() throws Exception {
		logger.info("Calling the method to copy the files using the regex.pattern");

		GenericFileHelper.copyFile("tests/regex/.*txt", destinationDirectory, true, true);
		GenericFileHelper.copyFile("tests/regex/patsdk*.jar", destinationDirectory, true, true);
		GenericFileHelper.copyFile("tests/regex/patsdk.*.jar", destinationDirectory, true, true);
		GenericFileHelper.copyFile("tests/regex/patsdk-impl.*jar", destinationDirectory, true, true);
	}

}