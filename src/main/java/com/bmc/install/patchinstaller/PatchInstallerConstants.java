/**
 * 
 */
package com.bmc.install.patchinstaller;

/**
 * @author kmoranka
 *
 */
public interface PatchInstallerConstants {

	/**
	 * Field for property patch file name
	 */
	public static final String PATCH_FILE_PATH = "patch.file.path";

	/**
	 * Field for property patch file name
	 */
	public static final String PATCH_CONFIG_XML_FILE_NAME = "patch.config.xml.file.name";

	/**
	 * Field for property product name
	 */
	public static final String PRODUCT_NAME = "product.name";

	/**
	 * Field for property product version
	 */
	public static final String PRODUCT_VERSION = "product.version";

	/**
	 * Field for property product version
	 */
	public static final String PRODUCT_INSTALLATION_BASE_PATH = "product.installation.base.path";

	/**
	 * Field for property product version
	 */
	public static final String BACKUP_DIR_NAME = "backup.dir.name";

	/**
	 * Field for property db1 user name
	 */
	public static final String DB1_URL = "db1.url";
	/**
	 * Field for property db1 SID
	 */
	public static final String DB1_INSTANCENAME = "db1.instancename";

	/**
	 * Field for property db2 user name
	 */
	public static final String DB2_USER = "db2.user";

	/**
	 * Field for property db2 user password
	 */
	public static final String DB2_PASSWORD = "db2.password";

	/**
	 * Field for property db2 SID
	 */
	public static final String DB2_SID = "db2.sid";

	/**
	 * Field for property db2 host name
	 */
	public static final String DB2_HOSTNAME = "db2.hostname";

	/**
	 * Field for property db2 port number
	 */
	public static final String DB2_PORT = "db2.port";

	/**
	 * Field for property db2 SID
	 */
	public static final String DB2_SERVICENAME = "db2.servicename";

	/**
	 * Field for property patch installer versions
	 */
	public static final String PATCH_INSTALLER_VERSION = "patch.installer.version";
	/****
	 * 
	 */

	public static final String MIN_COMPITABLE_VERSIONS = "min.compitable.version";

	/**
	 * Field for property patch installer action
	 */
	public static final String PATCH_INSTALLER_ACTION = "patch.installer.action";

	/**
	 * Field for property for location of the product specific installer
	 * properties
	 */
	public static final String PRODUCT_INSTALLER_PROPERTIES_LOCATION = "product.installer.propeties.location";
	/**
	 * Field for property for location of the product specific installer
	 * properties
	 */
	public static final String PRODUCT_INSTALLER_PROPERTIES_FILE_NAME = "product.installer.propeties.file.name";

	/**
	 * Field for minCompitableversion
	 */
	public static final String PRODUCT_MINCOMPITABLE_VERSIONS = "product.mincompitable_versions";
}
