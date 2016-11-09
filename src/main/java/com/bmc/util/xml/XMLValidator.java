/**
 * @author kmoranka
 *
 */
package com.bmc.util.xml;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Schema;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import com.bmc.install.patchinstaller.PatchInstaller;

import javax.xml.validation.Validator;
import javax.xml.transform.dom.DOMSource;
import java.io.File;
import java.net.URL;
import java.util.Properties;

public class XMLValidator {

	public static boolean validate(File xmlFile) {
		if (xmlFile == null ) {
			logger.info("java XsdSchemaValidator schema_file_name "
				+ "xml_file_name");
		} else {
			URL url = new XMLValidator().getClass().getResource("/conf/configXml.xsd");
			Properties installerprops = new Properties();
			//installerprops.load(url.openStream());
			logger.info("Loading xsd from "+url.toExternalForm());
			Schema schema = loadSchema(url);
			logger.info("Loaded xsd from "+url.toExternalForm());
			//logger.info("LoadSchema : "+url.toExternalForm());
			Document document = parseXmlDom(xmlFile);
			return validateXml(schema, document);
		}
		return false;
	}
	
	public static boolean validateXml(Schema schema, Document document) {
		try {
		// creating a Validator instance
		Validator validator = schema.newValidator();
		
		logger.info("Validator Class: "
			+ validator.getClass().getName());

		// validating the document against the schema
		validator.validate(new DOMSource(document));
		
		logger.info("Validation passed.");
		return true;
		} catch (Exception e) {
			logger.fatal("Error "+e.toString(),e);
			return false;
		}
	}
	
	public static Schema loadSchema(URL url) {
		Schema schema = null;
		try {
		String language = XMLConstants.W3C_XML_SCHEMA_NS_URI;
		SchemaFactory factory = SchemaFactory.newInstance(language);
		//URL url=new XMLValidator().getClass().getClassLoader().getResource(name);
		logger.info("Using URL ="+url.toExternalForm());
		schema = factory.newSchema(url);
		} catch (Exception e) {
			logger.fatal("Error in loadSchema  "+url+" "+e.toString(),e);
		}
		return schema;
	}
	
	public static Document parseXmlDom(File xml) {
		Document document = null;
		try {
		DocumentBuilderFactory factory 
			 = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		document = builder.parse(xml);
		} catch (Exception e) {
		logger.fatal("Error in parseXMLDOm "+e.toString(),e);
		}
		return document;
	}
	static Logger logger = Logger.getLogger(XMLValidator.class);
}
