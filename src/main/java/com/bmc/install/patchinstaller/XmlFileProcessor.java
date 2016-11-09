/**
 * 
 */
package com.bmc.install.patchinstaller;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.bmc.util.database.impl.DatabasHelperImpl;
import com.bmc.util.database.impl.SQLScriptRunner;
import com.bmc.util.io.GenericFileHelper;
import com.bmc.util.misc.EnvironmentHelper;
import com.bmc.util.os.OperatingSystem;
import com.bmc.util.os.OperatingSystemHelper;
import com.bmc.util.properties.PropertiesHelper;
import com.bmc.util.xml.XMLParser;

/**
 * @author kmoranka
 *
 */
public class XmlFileProcessor {

	private File configXML;
	private String patchZipExtractionLocation;
	private String productBasePath;
	private String backupDirName;
	private String lastBackupDir;
	private List<String> supportedKeywords = new LinkedList<String>();
	static Logger logger = Logger.getLogger(XmlFileProcessor.class);
	private OperatingSystem osHelper = OperatingSystemHelper.getInstance();

	public XmlFileProcessor(File xmlFile, String zipFileExtractedLocation, String prodBasePath, String backupDirName,
			String lastBackupDir) {
		configXML = xmlFile;
		patchZipExtractionLocation = zipFileExtractedLocation;
		productBasePath = prodBasePath;
		this.backupDirName = backupDirName;
		this.lastBackupDir = lastBackupDir;

		supportedKeywords.add("$HOSTNAME");
		supportedKeywords.add("$FQDN");
	}

	public String toString() {
		return "configXML=" + configXML.getAbsolutePath() + " patchZipExtractionLocation=" + patchZipExtractionLocation
				+ " prodBasePath=" + productBasePath + " backupDirName=" + backupDirName + " lastBackupDir="
				+ lastBackupDir + " supportedKeywords=" + supportedKeywords;
	}

	public boolean process() throws Exception {
		logger.debug("Started processing the " + configXML.getCanonicalPath() + " " + this);
		XMLParser parser = new XMLParser(configXML);
		boolean success = false;
		List<Node> nodeList = parser.getNodeList("configurations");
		NodeList configurations = nodeList.get(0).getChildNodes();
		for (int i = 0; i < configurations.getLength(); i++) {
			if ("configuration".equals(configurations.item(i).getNodeName())) {
				Node config = configurations.item(i);
				success = processConfig(config, i);
			}
		}
		return success;
	}

	private boolean processConfig(Node config, int index) throws Exception {
		boolean result = true;
		if ((config.getNodeType() == Node.ELEMENT_NODE)) {
			NodeList nl = config.getChildNodes();
			PatchConfiguration pconfig = new PatchConfiguration();
			pconfig.setIndex_(index);
			for (int i = 0; i < nl.getLength(); i++) {
				Node n = nl.item(i);
				if ((n.getNodeType() == Node.ELEMENT_NODE)) {
					Text nodeText = (Text) n.getFirstChild();
					if ("installerversion".equals(((Element) n).getNodeName())) {
						if (!PatchInstaller.installerprops.getProperty(PatchInstallerConstants.PATCH_INSTALLER_VERSION)
								.equals(nodeText.getNodeValue())) {
							logger.error("PatchInstaller Version mismatch. Expecting "
									+ PatchInstaller.installerprops
											.getProperty(PatchInstallerConstants.PATCH_INSTALLER_VERSION)
									+ " found " + nodeText.getNodeValue());
							return false;
						}
					} else if ("productname".equals(((Element) n).getNodeName())) {
						pconfig.setProductname_(nodeText.getNodeValue());
					} else if ("productversion".equals(((Element) n).getNodeName())) {
						pconfig.setProductversion_(nodeText.getNodeValue());
					} else if ("mincompatibleversion".equals(((Element) n).getNodeName())) {

						System.out.println("mincompatibleversion".equals(((Element) n).getNodeName()));

						System.out.println((nodeText.getNodeValue()));

						pconfig.setMincompatibleversion_(nodeText.getNodeValue());

						logger.info(" chechikn the values for the doing the proper work in the respectetive field");

						System.out.println((nodeText.getNodeValue()));

						if (!PatchInstaller.installerprops.getProperty(PatchInstallerConstants.MIN_COMPITABLE_VERSIONS)
								.equals(nodeText.getNodeValue())) {

							logger.error("min compitable veresion mismatch");

							return false;
						}

						// pconfig.setMincompatibleversions_();

						// TODO check the minimum compatible version here and
						// throw error if not matching

					} else if ("installsequence".equals(((Element) n).getNodeName())) {
						logger.info("PATCH_INSTALLER_ACTION=" + PatchInstaller.installAction);
						if (PatchInstaller.installAction.equals("install")) {
							pconfig.setInstallSequence_(n);
							result = processInstallSequence(pconfig, n);
						}
					} else if ("uninstallsequence".equals(((Element) n).getNodeName())) {
						if (PatchInstaller.installAction.equals("uninstall")) {
							pconfig.setUnInstallSequence_(n);
							result = processUnInstallSequence(pconfig, n);
						}

					}
				}
			}
		}
		return result;

	}
	/*
	 * private boolean mincompitableversioncheck(PatchConfiguration pconfig,
	 * Node mincompitableversions) {
	 * 
	 * if (mincompitableversions.getNodeType() == Node.ELEMENT_NODE) { NodeList
	 * nl = mincompitableversions.getChildNodes();
	 * 
	 * for (int i = 0; i < nl.getLength(); i++) { Node n = nl.item(i);
	 * 
	 * } }
	 * 
	 * return false;
	 * 
	 * }
	 */

	private boolean processInstallSequence(PatchConfiguration pconfig, Node installsequence) throws Exception {
		boolean result = true;
		if ((installsequence.getNodeType() == Node.ELEMENT_NODE)) {
			NodeList nl = installsequence.getChildNodes();
			for (int i = 0; i < nl.getLength(); i++) {
				Node n = nl.item(i);
				if ((n.getNodeType() == Node.ELEMENT_NODE)) {
					if ("preinstall".equals(((Element) n).getNodeName())) {
						logger.info("PRE-INSTALL Operations");
						logger.info("**********************************************************");
						logger.info("Processing PRE-INSTALL section");
						result = performSequence(pconfig, n, productBasePath, backupDirName, "install");
					} else if ("install".equals(((Element) n).getNodeName())) {
						logger.info("INSTALL Operations");
						logger.info("**********************************************************");
						logger.info("Processing INSTALL section");
						result = performSequence(pconfig, n, productBasePath, backupDirName, "install");
					} else if ("postinstall".equals(((Element) n).getNodeName())) {
						logger.info("POST-INSTALL Operations");
						logger.info("**********************************************************");
						logger.info("Processing POST-INSTALL section");
						result = performSequence(pconfig, n, productBasePath, backupDirName, "install");
					}
				}
			}
		}
		return result;
	}

	private boolean processUnInstallSequence(PatchConfiguration pconfig, Node installsequence) throws Exception {
		logger.info("processUnInstallSequence");

		boolean result = true;
		if ((installsequence.getNodeType() == Node.ELEMENT_NODE)) {
			NodeList nl = installsequence.getChildNodes();
			for (int i = 0; i < nl.getLength(); i++) {
				Node n = nl.item(i);
				if ((n.getNodeType() == Node.ELEMENT_NODE)) {
					if ("preuninstall".equals(((Element) n).getNodeName())) {
						logger.info("PRE-UNINSTALL Operations");
						logger.info("**********************************************************");
						logger.info("Processing PRE-UNINSTALL section");
						result = performSequence(pconfig, n, lastBackupDir, productBasePath, "uninstall");
					} else if ("uninstall".equals(((Element) n).getNodeName())) {
						logger.info("UNINSTALL Operations");
						logger.info("**********************************************************");
						logger.info("Processing UNINSTALL section");
						result = performSequence(pconfig, n, lastBackupDir, productBasePath, "uninstall");
					} else if ("postuninstall".equals(((Element) n).getNodeName())) {
						logger.info("POST-UNINSTALL Operations");
						logger.info("**********************************************************");
						logger.info("Processing POST-UNINSTALL section");
						result = performSequence(pconfig, n, lastBackupDir, productBasePath, "uninstall");
					}
				}
			}
		}
		return result;
	}

	private boolean performSequence(PatchConfiguration pconfig, Node installCommands, String sourcePath,
			String targetPath, String installAction) throws Exception {
		boolean result = true;
		NodeList nl = installCommands.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			if ((n.getNodeType() == Node.ELEMENT_NODE)) {
				if ("servicecommands".equals(((Element) n).getNodeName())) {
					result = performServiceOperations(pconfig, n);
				} else if ("oscommands".equals(((Element) n).getNodeName())) {
					result = performOSOperations(pconfig, n);
				} else if ("fileoperations".equals(((Element) n).getNodeName())) {
					result = performFileOperations(pconfig, n, sourcePath, targetPath, installAction);
				} else if ("sqls".equals(((Element) n).getNodeName())) {
					result = performSQLOperations(pconfig, n);
				} else if ("properties".equals(((Element) n).getNodeName())) {
					result = performPropertiesOperations(pconfig, n);
				}
			}
		}
		return result;
	}

	private boolean performSQLOperations(PatchConfiguration pconfig, Node sqls) throws SQLException, IOException {
		boolean result = true;
		logger.info("Performing SQL operations");
		DatabasHelperImpl dbimpl = new DatabasHelperImpl();
		NodeList nl = sqls.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node sqlNode = nl.item(i);
			if ((sqlNode.getNodeType() == Node.ELEMENT_NODE)) {
				if ("sql".equals(((Element) sqlNode).getNodeName())) {
					NodeList sqldetails = sqlNode.getChildNodes();
					String instancename = null;
					String filename = null;
					String sqlDelimiter = null;
					String sqlStatement = null;
					for (int j = 0; j < sqldetails.getLength(); j++) {
						Node sqlDetailNode = sqldetails.item(j);
						if ((sqlDetailNode.getNodeType() == Node.ELEMENT_NODE)) {
							Text elText = (Text) sqlDetailNode.getFirstChild();
							if ("instancename".equals(sqlDetailNode.getNodeName())) {
								instancename = elText.getNodeValue();
							} else if ("filename".equals(sqlDetailNode.getNodeName())) {
								filename = sqlDetailNode.getFirstChild().getNodeValue();
								sqlDelimiter = sqlDetailNode.getAttributes().getNamedItem("sqldelimiter")
										.getNodeValue();
							} else if ("sqlstatement".equals(sqlDetailNode.getNodeName())) {
								sqlStatement = elText.getNodeValue();
							}
						}
					}
					String url = PatchInstaller.initiationParams.getProperty("db." + instancename + ".url");
					String login = PatchInstaller.initiationParams.getProperty("db." + instancename + ".user");
					String password = PatchInstaller.initiationParams.getProperty("db." + instancename + ".password");
					if (password == null || password.isEmpty()) {
						System.out.print("Value for the property db." + instancename
								+ ".password not found.\nPlease provide the database password for instance name "
								+ instancename + " and user " + login + " : ");
						char[] pwd = System.console().readPassword();
						password = pwd.toString();
					}
					dbimpl.createConnection(url, login, password);
					if (sqlStatement != null) {
						dbimpl.executeStatement(sqlStatement);
						result = true;
					} else if (filename != null) {
						SQLScriptRunner.executeSQLFile(dbimpl,
								replaceKeyword(patchZipExtractionLocation + "/" + filename), sqlDelimiter, true);
						result = true;
					}

				} else {
					// TODO throw error
				}
			}
		}
		return result;
	}

	private boolean performOSOperations(PatchConfiguration pconfig, Node osCommands) throws IOException {
		boolean result = true;
		logger.info("Performing OS operations");
		String output = null;
		String currentEnv = System.getProperty("os.name");
		if ((osCommands.getNodeType() == Node.ELEMENT_NODE)) {
			NodeList nl = osCommands.getChildNodes();
			if (currentEnv != null && currentEnv.startsWith("Windows")) {
				for (int i = 0; i < nl.getLength(); i++) {
					Node osCommandNode = nl.item(i);
					if ((osCommandNode.getNodeType() == Node.ELEMENT_NODE)) {
						String osname = osCommandNode.getAttributes().getNamedItem("osfamily").getNodeValue();
						if (!"WINDOWS".equals(osname)) {
							continue;
						}
						Text osCommandNodeText = (Text) osCommandNode.getFirstChild();
						String osCommand = osCommandNodeText.getNodeValue();
						if (osCommand != null && (osCommand.contains(":") || osCommand.startsWith("%"))) {

						} else {
							osCommand = patchZipExtractionLocation + "/" + osCommand;
						}
						output = osHelper.executeCommand(osCommand);
						logger.debug("Output of the command " + osCommand + " is :\n" + output);
					}
				}
			}
			// Unix part
			else if (currentEnv != null && !currentEnv.startsWith("Windows")) {
				for (int i = 0; i < nl.getLength(); i++) {
					Node osCommandNode = nl.item(i);
					if ((osCommandNode.getNodeType() == Node.ELEMENT_NODE)) {
						String osname = osCommandNode.getAttributes().getNamedItem("osfamily").getNodeValue();
						if (!"WINDOWS".equals(osname)) {
							Text osCommandNodeText = (Text) osCommandNode.getFirstChild();
							String osCommand = osCommandNodeText.getNodeValue();
							if (osCommand != null && osCommand.startsWith("$")) {
								// Environment variable handling TODO
							} else {
								osCommand = patchZipExtractionLocation + "/" + osCommand;
							}
							output = osHelper.executeCommand(osCommand);
							logger.debug("Output of the command " + osCommand + " is :\n" + output);
						}
					}
				}

			}
		}
		return result;
	}

	private String replaceKeyword(String input) throws UnknownHostException {

		if (input != null && !input.isEmpty()) {
			for (int i = 0; i < supportedKeywords.size(); i++) {
				logger.trace("Replacing " + supportedKeywords.get(i) + " with "
						+ EnvironmentHelper.getEnvironmentValue(supportedKeywords.get(i)));
				input = input.replace(supportedKeywords.get(i),
						EnvironmentHelper.getEnvironmentValue(supportedKeywords.get(i)));
			}
		}

		Map<String, String> env = System.getenv();
		for (int i = 0; i < env.size(); i++) {
			for (String envName : env.keySet()) {
				if (input.contains("$" + envName.toUpperCase())) {
					logger.info("Found the env variable " + "$" + envName.toUpperCase());
					input = input.replace("$" + envName.toUpperCase(),
							EnvironmentHelper.getEnvironmentValue("$" + envName.toUpperCase()));
				}
			}

		}
		return input;
	}

	private boolean performFileOperations(PatchConfiguration pconfig, Node fileOperations, String source, String target,
			String installAction) throws Exception {
		boolean result = true;
		logger.info("Performing File operations");

		NodeList nl = fileOperations.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			if ((n.getNodeType() == Node.ELEMENT_NODE)) {
				if ("restore".equals(((Element) n).getNodeName())) {
					logger.info("Performing restore operations");
					NodeList files = n.getChildNodes();
					for (int j = 0; j < files.getLength(); j++) {
						Node fileNode = files.item(j);
						if ((fileNode.getNodeType() == Node.ELEMENT_NODE)) {
							Text fileNodeText = (Text) fileNode.getFirstChild();
							if (fileNode != null) {
								GenericFileHelper.copyFile(
										replaceKeyword(lastBackupDir + "/" + fileNodeText.getNodeValue()),
										replaceKeyword(productBasePath + "/" + fileNodeText.getNodeValue()), true);
							}
						}
					}
				}
				if ("backup".equals(((Element) n).getNodeName())) {
					logger.info("Performing backup operations");
					NodeList files = n.getChildNodes();
					for (int j = 0; j < files.getLength(); j++) {
						Node fileNode = files.item(j);
						if ((fileNode.getNodeType() == Node.ELEMENT_NODE)) {
							Text fileNodeText = (Text) fileNode.getFirstChild();
							if (fileNode != null) {
								GenericFileHelper.copyFile(
										replaceKeyword(productBasePath + "/" + fileNodeText.getNodeValue()),
										replaceKeyword(backupDirName + "/" + fileNodeText.getNodeValue()), true);
							}
						}
					}
				} else if ("delete".equals(((Element) n).getNodeName())) {
					logger.info("Performing delete operations");
					NodeList files = n.getChildNodes();
					for (int j = 0; j < files.getLength(); j++) {
						Node fileNode = files.item(j);
						if ((fileNode.getNodeType() == Node.ELEMENT_NODE)) {
							Text fileNodeText = (Text) fileNode.getFirstChild();
							if (fileNode != null) {
								if (!GenericFileHelper
										.checkFileExistance(backupDirName + "/" + fileNodeText.getNodeValue())) {

									if (installAction.equals("install"))
										GenericFileHelper.copyFile(
												replaceKeyword(productBasePath + "/" + fileNodeText.getNodeValue()),
												replaceKeyword(backupDirName + "/" + fileNodeText.getNodeValue()),
												true);
								}
								GenericFileHelper.deleteFile(
										replaceKeyword(productBasePath + "/" + fileNodeText.getNodeValue()));
							}
						}
					}
				} else if ("copy".equals(((Element) n).getNodeName())) {
					logger.info("Performing copy operations");
					NodeList files = n.getChildNodes();
					for (int j = 0; j < files.getLength(); j++) {
						Node fileNode = files.item(j);
						if ((fileNode.getNodeType() == Node.ELEMENT_NODE)) {
							Text fileNodeText = (Text) fileNode.getFirstChild();
							String fromPath = fileNode.getAttributes().getNamedItem("copyFrom").getNodeValue();
							String toPath = fileNode.getAttributes().getNamedItem("copyTo").getNodeValue();
							if (fromPath != null) {
								if (!(fromPath.contains(":") || fromPath.startsWith("%"))) {
									fromPath = patchZipExtractionLocation + "/" + fromPath;
								}
							} else {
								logger.error("copyFrom attribute not found for file node having value " + fileNodeText);
								continue;
							}
							if (toPath != null) {
								if (!(toPath.contains(":") || toPath.startsWith("%"))) {
									toPath = productBasePath + "/" + toPath;
								}
							} else {
								logger.error("copyTo attribute not found for file node having value " + fileNodeText);
								continue;
							}
							if ("file".equals(((Element) fileNode).getNodeName())) {
								String fileName = fileNodeText.getNodeValue();
								result = GenericFileHelper.copyFile(replaceKeyword(fromPath + "/" + fileName),
										replaceKeyword(toPath + "/" + fileName), true);
							} else if ("folder".equals(((Element) fileNode).getNodeName())) {
								logger.info("Copy folder from  " + replaceKeyword(fromPath) + " to "
										+ replaceKeyword(toPath));
								result = GenericFileHelper.copyDirectory(replaceKeyword(fromPath),
										replaceKeyword(toPath), true, false);
							}
						}
					}

				} else if ("rename".equals(((Element) n).getNodeName())) {
					logger.info("Performing rename operations");
					NodeList files = n.getChildNodes();
					for (int j = 0; j < files.getLength(); j++) {
						Node fileNode = files.item(j);
						if ((fileNode.getNodeType() == Node.ELEMENT_NODE)) {
							if (fileNode != null) {
								String fileName = fileNode.getAttributes().getNamedItem("name").getNodeValue();
								String newName = fileNode.getAttributes().getNamedItem("newname").getNodeValue();
								if (fileName != null && !(fileName.contains(":") || fileName.startsWith("%"))) {
									fileName = productBasePath + "/" + fileName;
								}
								GenericFileHelper.renameFile(replaceKeyword(fileName), replaceKeyword(newName));
							}
						}
					}
				} else if ("move".equals(((Element) n).getNodeName())) {
					logger.info("Performing move operations");
					NodeList files = n.getChildNodes();
					for (int j = 0; j < files.getLength(); j++) {
						Node fileNode = files.item(j);
						if ((fileNode.getNodeType() == Node.ELEMENT_NODE)) {
							Text fileNodeText = (Text) fileNode.getFirstChild();
							String fromPath = fileNode.getAttributes().getNamedItem("moveFrom").getNodeValue();
							String toPath = fileNode.getAttributes().getNamedItem("moveTo").getNodeValue();
							if (fromPath != null && !(fromPath.contains(":") || fromPath.startsWith("%"))) {
								fromPath = patchZipExtractionLocation + "/" + fromPath;
							}
							if (toPath != null && !(toPath.contains(":") || toPath.startsWith("%"))) {
								toPath = productBasePath + "/" + toPath;
							}
							if ("file".equals(((Element) fileNode).getNodeName())) {
								String fileName = fileNodeText.getNodeValue();
								result = GenericFileHelper.moveFile(replaceKeyword(fromPath + "/" + fileName),
										replaceKeyword(toPath + "/" + fileName));
							} else if ("folder".equals(((Element) fileNode).getNodeName())) {
								result = GenericFileHelper.moveDirectory(replaceKeyword(fromPath),
										replaceKeyword(toPath));
							}

						}
					}
				}
			}
		}

		return result;
	}

	private boolean performPropertiesOperations(PatchConfiguration pconfig, Node properties) throws Exception {
		boolean result = true;
		logger.info("Performing Properties Operations");
		if ((properties.getNodeType() == Node.ELEMENT_NODE)) {
			NodeList nl = properties.getChildNodes();
			for (int i = 0; i < nl.getLength(); i++) {
				Node n = nl.item(i);
				if ((n.getNodeType() == Node.ELEMENT_NODE)) {
					String operation = n.getAttributes().getNamedItem("operation").getNodeValue();
					String fileName = n.getAttributes().getNamedItem("file").getNodeValue();
					NodeList children = n.getChildNodes();
					String propName = null;
					String propValue = null;
					for (int j = 0; j < children.getLength(); j++) {
						Node nameValue = children.item(j);
						if ((nameValue.getNodeType() == Node.ELEMENT_NODE)) {
							if ("name".equals(nameValue.getNodeName())) {
								Text nameNodeText = (Text) nameValue.getFirstChild();
								propName = nameNodeText.getNodeValue();
							} else if ("value".equals(nameValue.getNodeName())) {
								Text valueNodeText = (Text) nameValue.getFirstChild();
								if (valueNodeText != null) {
									propValue = valueNodeText.getNodeValue();
								}
							}
						}
					}
					if (fileName != null && !(fileName.contains(":") || fileName.startsWith("%"))) {
						fileName = productBasePath + "/" + fileName;
					} else {

					}
					if ("add".equals(operation)) {
						result = PropertiesHelper.addProperty(fileName, propName, propValue);
					} else if ("delete".equals(operation)) {
						result = PropertiesHelper.removeProperty(fileName, propName);
					} else if ("modify".equals(operation)) {
						result = PropertiesHelper.addProperty(fileName, propName, propValue);
					} else if ("rename".equals(operation)) {
						result = PropertiesHelper.renameProperty(fileName, propName, propValue);
					}
				}
			}
		}

		return result;
	}

	private boolean performServiceOperations(PatchConfiguration pconfig, Node serviceOperations) throws Exception {
		boolean result = true;
		logger.info("Performing Service Operations");
		String currentEnv = System.getProperty("os.name");
		if ((serviceOperations.getNodeType() == Node.ELEMENT_NODE)) {
			NodeList nl = serviceOperations.getChildNodes();
			if (currentEnv != null && currentEnv.startsWith("Windows")) {
				for (int i = 0; i < nl.getLength(); i++) {
					Node n = nl.item(i);
					if ((n.getNodeType() == Node.ELEMENT_NODE)) {
						String osname = n.getAttributes().getNamedItem("osfamily").getNodeValue();
						if (!"WINDOWS".equals(osname)) {
							continue;
						}
						Text nodeText = (Text) n.getFirstChild();
						if ("stopservice".equals(((Element) n).getNodeName())) {
							if (nodeText != null) {
								osHelper.stopService(nodeText.getNodeValue());
							}

						} else if ("stopprocess".equals(((Element) n).getNodeName())) {

							// TODO result =
							// WindowsOSHandlerImpl.stopProcess(n.getNodeValue());
							if (nodeText != null) {
								osHelper.killProcess(nodeText.getNodeValue());
							}

						} else if ("startservice".equals(((Element) n).getNodeName())) {

							if (nodeText != null) {
								osHelper.startService(nodeText.getNodeValue());
							}
						} else if ("startprocess".equals(((Element) n).getNodeName())) {

							// TODO result =
							// WindowsOperatingSystemHelper.startProcess(n.getNodeValue());
						}
					}
				}
			} else
			// Unix part
			{
				for (int i = 0; i < nl.getLength(); i++) {
					Node n = nl.item(i);
					if ((n.getNodeType() == Node.ELEMENT_NODE)) {
						String osname = n.getAttributes().getNamedItem("osfamily").getNodeValue();
						String validateGrepPattern = "";
						Node node = n.getAttributes().getNamedItem("validateProcessText");
						// TODO why to handle NPE? I tried with setting this as
						// optional attribute but places where attribute value
						// was not specified, I received the error.
						if (node != null) {
							validateGrepPattern = node.getNodeValue();
						}

						if (!"UNIX".equals(osname)) {
							continue;
						}
						Text nodeText = (Text) n.getFirstChild();
						if ("stopservice".equals(((Element) n).getNodeName())) {
							if (nodeText != null) {
								if (!osHelper.stopService(nodeText.getNodeValue(), validateGrepPattern)) {
									logger.error("Not able to stop the service " + nodeText.getNodeValue()
											+ " with validateProcessText=" + validateGrepPattern);
								}
							}
						} else if ("stopprocess".equals(((Element) n).getNodeName())) {
							if (nodeText != null) {
								osHelper.killProcess(nodeText.getNodeValue());
							}

						} else if ("startservice".equals(((Element) n).getNodeName())) {

							if (nodeText != null) {
								if (!osHelper.startService(nodeText.getNodeValue(), validateGrepPattern)) {
									logger.error("Not able to start the service " + nodeText.getNodeValue()
											+ " with validateProcessText=" + validateGrepPattern);
								}
							}
						} else if ("startprocess".equals(((Element) n).getNodeName())) {

							// TODO result =
							// WindowsOperatingSystemHelper.startProcess(n.getNodeValue());
						}
					}
				}
			}
		}
		return result;
	}
}
