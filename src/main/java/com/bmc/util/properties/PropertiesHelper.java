/**
 * 
 */
package com.bmc.util.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

/**
 * @author kmoranka
 *
 */
public class PropertiesHelper {

	public static boolean addProperty (String fileName, String propName, String propValue) throws Exception {
		FileInputStream readerStream = null;
		FileOutputStream writerStream = null;
		File propertiesFile = null;
		Properties props = new Properties();
		if (fileName !=null) {
			readerStream = new FileInputStream (fileName);
			propertiesFile = new File(fileName);
            if(propertiesFile.exists()){
                props.load(readerStream);
            }
            readerStream.close();
		}
		props.setProperty(propName, propValue);
		if (writerStream == null) {
			writerStream = new FileOutputStream(fileName);
		}
		props.store(writerStream, null);
		writerStream.close();
		props = new Properties();
		if (fileName !=null) {
			readerStream = new FileInputStream (fileName);
			propertiesFile = new File(fileName);
            if(propertiesFile.exists()){
                props.load(readerStream);
            }
            readerStream.close();
		}
		if(props.getProperty(propName) != null && propValue.equals(props.getProperty(propName))) {
			return true;
		} else {
			return false;
		}
		
	}
	
	public static boolean removeProperty (String fileName, String propName) throws Exception {
		FileInputStream readerStream = null;
		FileOutputStream writerStream = null;
		File propertiesFile = null;
		Properties props = new Properties();
		if (fileName !=null) {
			readerStream = new FileInputStream (fileName);
			propertiesFile = new File(fileName);
            if(propertiesFile.exists()){
                props.load(readerStream);
            }
            readerStream.close();
		}
		props.remove(propName);
		if (writerStream == null) {
			writerStream = new FileOutputStream(fileName);
		}
		props.store(writerStream, null);
		writerStream.close();
		props = new Properties();
		if (fileName !=null) {
			readerStream = new FileInputStream (fileName);
			propertiesFile = new File(fileName);
            if(propertiesFile.exists()){
                props.load(readerStream);
            }
            readerStream.close();
		}
		if(props.getProperty(propName) == null) {
			return true;
		} else {
			return false;
		}
		
	}
	
	public static boolean renameProperty (String fileName, String propName, String newPropName) throws Exception {
		FileInputStream readerStream = null;
		File propertiesFile = null;
		Properties props = new Properties();
		if (fileName !=null) {
			readerStream = new FileInputStream (fileName);
			propertiesFile = new File(fileName);
            if(propertiesFile.exists()){
                props.load(readerStream);
            }
            readerStream.close();
		}
		String propValue = props.getProperty(propName);
		removeProperty(fileName, propName);
		addProperty(fileName, newPropName, propValue);
		props = new Properties();
		if (fileName !=null) {
			readerStream = new FileInputStream (fileName);
			propertiesFile = new File(fileName);
            if(propertiesFile.exists()){
                props.load(readerStream);
            }
            readerStream.close();
		}
		if(props.getProperty(newPropName) == null || !props.getProperty(newPropName).equals(propValue)) {
			return false;
		} else {
			return true;
		}
	}	
}
