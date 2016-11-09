/**
 * 
 */
package com.bmc.util.xml;



import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.LogRecord;
import java.util.logging.XMLFormatter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
/**
 * @author kmoranka
 *
 */
public class XMLParser {
	/**
	   * Constructor for XMLParser
	   * Use this constructor only for read actions (not for write)
	   *
	   * @param file XML file to parse
	   *
	   * @throws Exception for any errors
	   */
	  public XMLParser( File xmlFile )
	    throws Exception
	  {
	    this( xmlFile, null );
	  }

	  /**
	   * Constructor for XMLParser
	   *
	   * @param fileName        XML file to parse
	   * @param documentBuilder document builder (may be null)
	   *
	   * @throws Exception for any errors
	   */
	  public XMLParser( String fileName, DocumentBuilder documentBuilder )
	    throws Exception
	  {
	    fileName_ = fileName;
	    DocumentBuilder builder = documentBuilder;
	    if ( documentBuilder == null )
	    {
	      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	      builder = factory.newDocumentBuilder();
	    }
	    document_ = builder.parse( new File( fileName ) );
	    xmlFormatter_ = new XMLFormatter();
	  }
	  
	  /**
	   * Constructor for XMLParser
	   * Use this constructor only for read actions (not for write)
	   *
	   * @param file            XML file to parse
	   * @param documentBuilder DocumentBuilder (may be null)
	   *
	   * @throws Exception for any errors
	   */
	  public XMLParser( File file, DocumentBuilder documentBuilder )
	    throws Exception
	  {
	    fileName_ = null;
	    DocumentBuilder builder = documentBuilder;
	    if ( documentBuilder == null )
	    {
	      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	      builder = factory.newDocumentBuilder();
	    }
	    document_ = builder.parse( file );
	   /* if ( LOGGER.isLoggable( Level.FINE ) )
        {
        LOGGER.logp(
                     Level.FINE, XMLParser.class.getName(),
                     "XMLParser",
                     " Document is welformed, processing for XSD validation.")
            );
        }*/
	    boolean xsdcompliant = XMLValidator.validate(file);
	    
	    if (xsdcompliant) {
	    	logger.info(" XML File"+ file.getCanonicalPath()+" Validated successfully with XSD");
	    }
	    else
	    {
	    	logger.info(" XML File"+ file.getCanonicalPath()+" Validation failed with XSD. Exiting...");
	    	System.exit(1);
	    }
	    document_.getDocumentElement().normalize();
	    xmlFormatter_ = new XMLFormatter();
	    System.out.println("Root element :" + document_.getDocumentElement().getNodeName());
	/*    NodeList nList = document_.getElementsByTagName("*");

	    System.out.println("----------------------------");

	    Node n=null;
	    Element eElement=null;

	    for (int i = 0; i < nList.getLength(); i++) {           
	      System.out.println(nList.getLength());     
	      n= nList.item(i);                            
	      System.out.println("\nCurrent Element :" + n.getNodeName());


	      if (n.getNodeType() == Node.ELEMENT_NODE) {
	        eElement = (Element) n.getChildNodes();
	        System.out.println("\nCurrent Element :" + n.getNodeName());
	      }
	      n.getNextSibling();
	    }*/
	  }
	  

	  /**
	   * Gets the node list
	   *
	   * @param xpathString XPath expression
	   *
	   * @return node list
	   *
	   * @throws Exception for any errors
	   */
	  public List<Node> getNodeList( String xpathString )
	    throws Exception
	  {
	    List<Node> nodeList = XPathHelper.getNodes( document_, xpathString );
	    if ( nodeList == null )
	    {
	      throw new Exception(
	        "The XPath string was written incorect - please verify...["
	          + xpathString + "]" );
	    }
	    return nodeList;
	  }

	  /**
	   * Gets the value of a node from an XPath
	   *
	   * @param xpathString XPath expression
	   *
	   * @return value of node pointed to by xpathString
	   *
	   * @throws Exception for any errors
	   */
	  public List<String> getNodesValue( String xpathString )
	    throws Exception
	  {
	    List<String> entities = new ArrayList<String>();
	    List<Node> nodeList = getNodeList( xpathString );
	    Iterator<Node> iter = nodeList.iterator();
	    while ( iter.hasNext() )
	    {
	      Node node = iter.next();
	      NodeList childsNodes = node.getChildNodes();
	      Node child = childsNodes.item( 0 );
	      String tagValue = "";
	      if ( child != null )
	      {
	        tagValue = child.getNodeValue();
	      }
	      entities.add( tagValue );
	    }
	    return entities;
	  }

	  /**
	   * Gets attributes matched under xpathString and having name attribute name
	   *
	   * @param xpathString   XPath expression
	   * @param attributeName attribute name
	   *
	   * @return attributes matched under xpathString and having attribute name
	   *
	   * @throws Exception for any errors
	   */
	  public List<String> getNodesAttributes( String xpathString,
	    String attributeName )
	    throws Exception
	  {
	    List<String> attributes = new ArrayList<String>();
	    List<Node> nodeList = getNodeList( xpathString );
	    Iterator<Node> iter = nodeList.iterator();
	    while ( iter.hasNext() )
	    {
	      Node node = iter.next();
	      String attributeValue =
	        node.getAttributes().getNamedItem( attributeName ).getNodeValue();
	      attributes.add( attributeValue );
	    }
	    return attributes;
	  }

	  /**
	   * Get a node value
	   *
	   * @param xpathString XPath expression
	   *
	   * @return value of the node
	   *
	   * @throws Exception for any errors
	   */
	  public String getNodeValue( String xpathString )
	    throws Exception
	  {
	    Node child = getChild( xpathString );
	    String initalValue = new String( "" );
	    if ( child != null )
	    {
	      initalValue = child.getNodeValue();
	    }
	    else
	    {
	      Element parent = ( Element )XPathHelper.getNode( document_,
	        xpathString );

	      // child=Parent.getFirstChild();
	      initalValue = parent.getNodeValue();
	    }
	    return initalValue;
	  }

	  /**
	   * Gets the sub nodes
	   *
	   * @param xpathString XPath expression
	   *
	   * @return sub nodes
	   *
	   * @throws Exception for any errors
	   */
	  public String getSubNodes( String xpathString )
	    throws Exception
	  {
	    StringBuilder result = new StringBuilder();
	    List<Node> nodeList = getNodeList( xpathString );
	    Iterator<Node> iter = nodeList.iterator();
	    while ( iter.hasNext() )
	    {
	      Node node = iter.next();
	      result.append( xmlFormatter_.format( (LogRecord) node) );
	    }
	    return result.toString();
	  }
	  
	  /**
	   * Gets the child Node
	   *
	   * @param xpathString XPath expression
	   *
	   * @return child Node
	   *
	   * @throws Exception for any errors
	   */
	  private Node getChild( String xpathString )
	    throws Exception
	  {
	    Node child = null;
	    Node node = XPathHelper.getNode( document_, xpathString );
	    if ( node == null )
	    {
	      throw new Exception(
	        "The XPath string was written incorect - please verify...["
	          + xpathString + "]" );
	    }
	    child = node.getFirstChild();
	    return child;
	  }
	  
	  /**
	   * file name
	   */
	  private String fileName_;

	  /**
	   * root document
	   */
	  private Document document_;

	  /**
	   * XML formatter
	   */
	  private XMLFormatter xmlFormatter_;

	  /**
	   * character set
	   */
	  private String charset_;
		static Logger logger = Logger.getLogger(XMLParser.class);

}


