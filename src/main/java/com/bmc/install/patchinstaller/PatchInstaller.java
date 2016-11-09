/**
 * 
 */
package com.bmc.install.patchinstaller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import com.bmc.util.io.GenericFileHelper;
import com.bmc.util.os.OperatingSystem;
import com.bmc.util.os.OperatingSystemHelper;

/**
 * @author kmoranka
 *
 */

/*
 * it will create the proper insta
 * 
 */
public class PatchInstaller {

	/**
	 * Topic for logger.
	 */
	public static Properties initiationParams = null;
	static {
	}

	public PatchInstaller(Properties initiationProps) {
		if (initiationProps != null) {
			initiationParams = initiationProps;
			initiateInstallation();
		}
		initLogger();
	}

	public static void initLogger() {
		File log4jFile = new File("log4j-config.xml");
		String filePath = "";
		if (log4jFile.exists()) {
			try {
				filePath = log4jFile.getCanonicalPath();
			} catch (IOException e) {
				e.printStackTrace();
			}
			;
			System.out.println("Using the log4j configuration from " + filePath);
			DOMConfigurator.configure("log4j-config.xml");
		} else {

			log4jFile = new File("conf/log4j-config.xml");
			if (log4jFile.exists()) {
				try {
					filePath = log4jFile.getCanonicalPath();
				} catch (IOException e) {
					e.printStackTrace();
				}
				;
				System.out.println("Using the log4j configuration from " + filePath);
				DOMConfigurator.configure("conf/log4j-config.xml");
			} else {
				URL url = PatchInstaller.class.getResource("/conf/log4j-config.xml");
				System.out.println("Using the log4j configuration from " + url.toExternalForm());
				DOMConfigurator.configure(url);
			}
		}

		logger.debug("Log4j appender configuration is successful !!");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		initLogger();
		File propfile = null;
		String propertyFilePath;
		if (args == null || args.length < 1) {
			System.out.print(
					"Argument for product specific properties not provided. Please provide location for the product environment details properties file : ");
			propertyFilePath = new Scanner(System.in).next();
		} else {
			propertyFilePath = args[0];
		}
		FileInputStream setupProp = null;
		try {
			propfile = new File(propertyFilePath);
			if (propfile.isFile() && propfile.canRead()) {
				setupProp = new FileInputStream(propfile);
			} else {
				System.out.println("The properties file " + propertyFilePath
						+ " does not exist please verify. Exiting Installation.");
				logger.error("The properties file " + propertyFilePath
						+ " does not exist please verify. Exiting Installation.");
				System.exit(0);
			}
			initiationParams = new Properties();
			initiationParams.load(setupProp);
		} catch (IOException e) {
			e.printStackTrace();
		}
		initiateInstallation();
	}

	private static void initiateInstallation() {
		String zipFileExtractedLocation = null;
		FileInputStream input = null;
		try {
			URL url = PatchInstaller.class.getResource("/conf/patchinstaller.properties");
			installerprops = new Properties();
			installerprops.load(url.openStream());

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		if (initiationParams != null) {

			String installActionProp = initiationParams.getProperty(PatchInstallerConstants.PATCH_INSTALLER_ACTION);
			logger.info("Detected installAction =" + installActionProp);
			installAction = installActionProp;
			String zipFilePath = initiationParams.getProperty(PatchInstallerConstants.PATCH_FILE_PATH);
			if (zipFilePath == null || zipFilePath.isEmpty()) {
				zipFilePath = System.getProperty(PatchInstallerConstants.PATCH_FILE_PATH);
				if (zipFilePath == null || zipFilePath.isEmpty()) {
					System.out.print(
							"Value for the property \"patch.file.path\" not found. Please provide location for the patch file : ");
					zipFilePath = new Scanner(System.in).next();
				}
			}
			if (zipFilePath.startsWith("%")) {
				zipFilePath = osHelper.resolveEnvVars(zipFilePath);
			} else if (zipFilePath.startsWith("$")) {
				// zipFilePath =
				// UNIXOperatingSystemHelper.resolveEnvVars(zipFilePath);
			}
			String prodBasePath = initiationParams.getProperty(PatchInstallerConstants.PRODUCT_INSTALLATION_BASE_PATH);
			if (prodBasePath == null || prodBasePath.isEmpty()) {
				prodBasePath = System.getProperty(PatchInstallerConstants.PRODUCT_INSTALLATION_BASE_PATH);
				if (prodBasePath == null || prodBasePath.isEmpty()) {
					System.out.print(
							"Value for the property \"product.installation.base.path\" not found. Please provide the Product's installation directory path: ");
					prodBasePath = new Scanner(System.in).next();
				}
			}
			if (prodBasePath.startsWith("%")) {
				prodBasePath = osHelper.resolveEnvVars(prodBasePath);
			} else if (prodBasePath.startsWith("$")) {
				// prodBasePath =
				// UNIXOperatingSystemHelper.resolveEnvVars(prodBasePath);
			}
			try {
				String backupDirName = initiationParams.getProperty(PatchInstallerConstants.BACKUP_DIR_NAME,
						"PatchInstallerBackup");
				if (!GenericFileHelper.checkFileExistance(zipFilePath)) {
					logger.error("Zip File " + zipFilePath + " not available. Exiting installation.");
					System.exit(0);
				}
				File zipFile = new File(zipFilePath);
				String zipFileNameWOExtenssion = zipFile.getName();
				int pos = zipFileNameWOExtenssion.lastIndexOf(".");
				if (pos > 0) {
					zipFileNameWOExtenssion = zipFileNameWOExtenssion.substring(0, pos);
				}
				zipFileExtractedLocation = prodBasePath + "/" + backupDirName + "/" + zipFileNameWOExtenssion;
				String xmlFileName = initiationParams.getProperty(PatchInstallerConstants.PATCH_CONFIG_XML_FILE_NAME);
				if (xmlFileName == null) {
					xmlFileName = System.getProperty(PatchInstallerConstants.PATCH_CONFIG_XML_FILE_NAME);
					if (xmlFileName == null || xmlFileName.isEmpty()) {
						System.out.print(
								"Value for the property \"patch.config.xml.file.name\" not found. Please provide the patch configuration XML file name: ");
						xmlFileName = new Scanner(System.in).next();
					}
				}
				File backupDir = null;
				File lastBackupDir = null;
				File xmlFile = null;
				backupDir = createBackupDir(prodBasePath, backupDirName, zipFileNameWOExtenssion);
				lastBackupDir = getLatestBackupDir(prodBasePath, backupDirName, zipFileNameWOExtenssion);
				if (installAction.equals("install")) {
					GenericFileHelper.createDirectory(backupDir);
				}
				GenericFileHelper.extractFolder(zipFilePath, zipFileExtractedLocation);
				if (xmlFileName != null) {
					xmlFile = new File(xmlFileName);
					if (!GenericFileHelper.checkFileExistance(xmlFile.getCanonicalPath())) {
						xmlFile = new File(zipFileExtractedLocation + "/" + xmlFileName);
						if (!GenericFileHelper.checkFileExistance(xmlFile.getCanonicalPath())) {
							xmlFile = new File("conf/" + xmlFileName);
							if (!GenericFileHelper.checkFileExistance(xmlFile.getCanonicalPath())) {
								logger.error("Patch Configuration XML file was not found. Exiting the installation.");
								System.exit(0);
							} else {
								logger.info("Picked up Patch Configuration XML file from location "
										+ xmlFile.getCanonicalPath());
							}
						} else {
							logger.info("Picked up Patch Configuration XML file from location "
									+ xmlFile.getCanonicalPath());
						}
					} else {
						logger.info(
								"Picked up Patch Configuration XML file from location " + xmlFile.getCanonicalPath());
					}
				} else {
					logger.error("Patch Configuration XML file name not set. Exiting the installation.");
					System.exit(0);
				}
				XmlFileProcessor processor = new XmlFileProcessor(xmlFile, zipFileExtractedLocation, prodBasePath,
						backupDir.getCanonicalPath(),
						(lastBackupDir != null ? lastBackupDir.getCanonicalPath() : null));
				boolean result = processor.process();
				try {
					if (GenericFileHelper.checkFileExistance(zipFileExtractedLocation)) {
						GenericFileHelper.deleteFile(new File(zipFileExtractedLocation));
					}
				} catch (IOException e) {
					e.printStackTrace();
					logger.error("Error while deleting the patch extraction folder.");
					logger.error(e);
				}
				if (result) {
					logger.info("\n\n\nPatch " + installAction + "ed Successfully.");
				} else {
					logger.info("\n\n\nPatch was not " + installAction + "ed successfully.");
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.fatal(e);
				try {
					if (GenericFileHelper.checkFileExistance(zipFileExtractedLocation)) {
						GenericFileHelper.deleteFile(new File(zipFileExtractedLocation));
					}
				} catch (IOException e1) {
					e1.printStackTrace();
					logger.error("Error while deleting the patch extraction folder.");
					logger.error(e1);
				}
			}
		}
	}

	private static File createBackupDir(String prodBasePath, String backupDirName, String zipFileNameWOExtenssion)
			throws IOException {
		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
		String time = dateFormat.format(now);
		String backuplocation = prodBasePath + "/" + backupDirName + "/backup_" + zipFileNameWOExtenssion + "/" + time;
		logger.info("Backup folder to be created is " + prodBasePath + "/" + backupDirName + "/backup_"
				+ zipFileNameWOExtenssion + "/" + time);
		return new File(backuplocation);

	}

	private static File getLatestBackupDir(String prodBasePath, String backupDirName, String zipFileNameWOExtenssion)
			throws IOException {

		String dateTime = "0000-00-00 00:00:00";
		Entry<String, String> lastfile;
		String lastBackupFolder = "";
		TreeMap<String, String> map = new TreeMap<String, String>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
		String backuplocation = prodBasePath + "/" + backupDirName + "/backup_" + zipFileNameWOExtenssion;
		File backupDir = new File(backuplocation);
		SortedSet<String> set = new TreeSet<String>();
		if (backupDir.exists() && backupDir.isDirectory()) {
			File[] files = backupDir.listFiles();
			for (File file : files) {
				if (file.isDirectory()) {
					// Following logic is for date modified sort. But I did not
					// do intentionally because when the folder is
					// moved, the date modified changes. So better I stay with
					// sorted order of dateandtime. ***
					// dateTime = sdf.format(new Date(file.lastModified()));
					// map.put(dateTime, file.toString());
					// fileName=file.getName();
					set.add(file.getName());
				}
			}
			// See *** above
			// lastfile = map.lastEntry();
			// logger.info("Retreived latest folder "+lastfile.getValue()+"
			// prodBasePath=" + prodBasePath + "/" + backupDirName + "/backup_"
			// + zipFileNameWOExtenssion + "/");
			// return new File(lastfile.getValue());
			lastBackupFolder = set.last();
			logger.info("Retreived latest folder " + lastBackupFolder + "  prodBasePath=" + prodBasePath + "/"
					+ backupDirName + "/backup_" + zipFileNameWOExtenssion + "/");
			return new File(backuplocation + "/" + lastBackupFolder);
		}
		return null;

	}

	static Properties installerprops = null;
	static String installAction = "install";
	static Logger logger = Logger.getLogger(PatchInstaller.class);
	private static OperatingSystem osHelper = OperatingSystemHelper.getInstance();
}
