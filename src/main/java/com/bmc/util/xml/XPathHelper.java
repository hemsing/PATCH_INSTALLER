package com.bmc.util.xml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class XPathHelper {
	
	/**
	   * <link rel="stylesheet" type="text/css" href="../../../../../../javadoc.css"/>
	   * <p>
	   * Get a collection of nodes matching the given XPath applied to the DOM.
	   * The XPath is applied to the DOM node and the resulting nodes collected
	   * and returned.
	   * </p>
	   * <p>
	   * Given an XPath of <code class="styledInline">'/doc/name[@first='David']'</code>
	   * </p>
	   * <p>
	   * and a doc
	   * </p>
	   * <pre><code class="styled">&lt;doc&gt;
	   *  &lt;name first='David' last='Smith'/&gt;
	   *  &lt;name first='Joe' last='Jones'/&gt;
	   *  &lt;name first='David' last='Frost'/&gt;
	   *&lt;/doc&gt;</code></pre>
	   * <p>
	   * Would return the nodes representing
	   * </p>
	   * <pre><code class="styled">  &lt;name first='David' last='Smith'/&gt;
	   *  &lt;name first='David' last='Frost'/&gt;</code></pre>
	   * <p>
	   * Note that this API can also be used to retrieve Attr or Text Nodes.
	   * If you want Elements use elementFilter()
	   * </p>
	   *
	   * @param rootNode    Node we want to apply the xpath to
	   * @param xpathString XPath
	   *
	   * @return array of matching nodes
	   */
	public static List<Node> getNodes( Node rootNode, String xpathString )
	  {
	    List<Node> nodes = new ArrayList<Node>();
	    if ( ( rootNode != null ) && ( xpathString != null ) &&
	      ( !xpathString.equals( "" ) ) )
	    {
	      XPath xpath = XPathFactory.newInstance().newXPath();
	      try
	      {
	        NodeList nodeList = ( NodeList )xpath.evaluate( xpathString,
	          rootNode, XPathConstants.NODESET );
	        for ( int i = 0; i < nodeList.getLength(); i++ )
	        {
	          nodes.add( nodeList.item( i ) );
	        }
	      }
	      catch ( XPathExpressionException xpee )
	      {
	    	   
		        /*LOGGER.logp(
		                     Level.FINE, XPathHelper.class.getName(),
		                     "XPathHelper",
		                     " Error while getting the nodelist using xpath "+xpathString
		            );*/
		       
	      }
	    }
	    return nodes;
	  }

	  /**
	   * Gets a node matching the given XPath applied to the DOM. Same as calling
	   * {@link #getNodes(Node,String)} and extracting the first element. If no
	   * such node exists, null is returned.
	   *
	   * @param rootNode    Node we want to apply the xpath to
	   * @param xpathString XPath
	   *
	   * @return matching node or null if no such node exists
	   */
	  public static Node getNode( Node rootNode, String xpathString )
	  {
	    Node result = null;
	    List<Node> nodes = getNodes( rootNode, xpathString );
	    if ( nodes.size() != 0 )
	    {
	      result = nodes.iterator().next();
	    }
	    return result;
	  }

	  /**
	   * <link rel="stylesheet" type="text/css" href="../../../../../../javadoc.css"/>
	   * <p>
	   * Get the string value of the node matching the given XPath applied
	   * to the DOM node. The XPath is applied to the DOM node and the FIRST
	   * match is returned. Given an XPath of
	   * <code class="styledInline">'/doc/name[@first='David']/@last'</code>
	   * and a doc
	   * </p>
	   * <pre><code class="styled">&lt;doc&gt;
	   *  &lt;name first='David' last='Smith'/&gt;
	   *  &lt;name first='Joe' last='Jones'/&gt;
	   *  &lt;name first='David' last='Frost'/&gt;
	   *&lt;/doc&gt;</code></pre>
	   * <p>
	   * Would return the String <code class="styledInline">'Smith'</code>
	   * </p>
	   *
	   * @param rootNode     Node we want to apply the xpath to
	   * @param xpathString  XPath
	   * @param defaultValue default value
	   *
	   * @return String value of the match or defaultValue if no match was found
	   */
	  public static String getStringValue( Node rootNode, String xpathString,
	    String defaultValue )
	  {
	    String returnString = defaultValue;
	    List<String> strings = getStringValues( rootNode, xpathString );
	    if ( strings.size() > 0 )
	    {
	      returnString = strings.iterator().next();
	    }
	    return returnString;
	  }

	  /**
	   * <link rel="stylesheet" type="text/css" href="../../../../../../javadoc.css"/>
	   * <p>
	   * Get the string value of the node matching the given XPath applied to
	   * the DOM node.
	   * </p>
	   * <p>
	   * Equivalent of calling
	   * </p>
	   * <pre><code class="styled">getStringValue( rootNode, xpathString, "" )</code></pre>
	   *
	   * @param rootNode    Node we want to apply the xpath to
	   * @param xpathString XPath
	   *
	   * @return String value of the match or "" if no match was found
	   */
	  public static String getStringValue( Node rootNode, String xpathString )
	  {
	    return getStringValue( rootNode, xpathString, null );
	  }

	  /**
	   * <link rel="stylesheet" type="text/css" href="../../../../../../javadoc.css"/>
	   * <p>
	   * Get the collection of string values of the matching the given XPath
	   * applied to the DOM rootNode.
	   * </p>
	   * <p>
	   * The XPath is applied to the DOM rootNode and the results returned as
	   * a collection of Strings.
	   * </p>
	   * <p>
	   * Given an xPathString of <code class="styledInline">'/doc/name[@first='David']/@last'</code>
	   * and a rootNode pointing to the document
	   * </p>
	   * <pre><code class="styled">&lt;doc&gt;
	   *  &lt;name first='David' last='Smith'/&gt;
	   *  &lt;name first='Joe' last='Jones'/&gt;
	   *  &lt;name first='David' last='Frost'/&gt;
	   *&lt;/doc&gt;</code></pre>
	   * <p>
	   * Would return a collection containing
	   * <code class="styledInline">'Smith'</code>,
	   * <code class="styledInline">'Frost'</code>
	   * </p>
	   *
	   * @param rootNode    Node we want to apply the xpath to
	   * @param xpathString XPath
	   *
	   * @return String array of matches
	   */
	  public static List<String> getStringValues( Node rootNode,
	    String xpathString )
	  {
	    List<Node> nodes = getNodes( rootNode, xpathString );
	    List<String> strings = new ArrayList<String>();
	    for ( Node node : nodes )
	    {
	      if ( node instanceof Attr )
	      {
	        Attr attr = ( Attr )node;
	        strings.add( attr.getValue() );
	      }
	      else if ( node instanceof Text )
	      {
	        Text text = ( Text )node;
	        strings.add( text.getNodeValue() );
	      }
	    }
	    return strings;
	  }


	  /**
	   * Extract only the Nodes which are implementations of Attr
	   * from the given collection.
	   *
	   * @param nodes Collection of Nodes
	   *
	   * @return subset of Element implementions
	   */
	  public static List<Attr> attrFilter( Collection<Node> nodes )
	  {
	    List<Attr> attrNodes = new ArrayList<Attr>();
	    for ( Node node : nodes )
	    {
	      if ( node instanceof Attr )
	      {
	        attrNodes.add( ( Attr )node );
	      }
	    }
	    return attrNodes;
	  }

	  /**
	   * Extract only the Nodes which are implementations of Attr
	   * from the given collection and that has a given name.
	   *
	   * @param nodes         Collection of Nodes
	   * @param attributeName attribute name
	   *
	   * @return subset of Element implementions
	   */
	  public static List<Attr> attrFilter( Collection<Node> nodes,
	    String attributeName )
	  {
	    List<Attr> attrNodes = new ArrayList<Attr>();
	    Attr attr;
	    for ( Node node : nodes )
	    {
	      if ( node instanceof Attr )
	      {
	        attr = ( Attr )node;
	        if ( nullableEquals( attributeName, attr.getName() ) )
	        {
	          attrNodes.add( attr );
	        }
	      }
	    }
	    return attrNodes;
	  }
	  
	  /**
	   * Does a nullable equals of 2 Objects
	   *
	   * @param a Object a
	   * @param b Object b
	   *
	   * @return true if both Objects are null or equal, false otherwise
	   */
	  public static boolean nullableEquals( Object a, Object b )
	  {
	    boolean result = true;
	    if ( a == null )
	    {
	      if ( b != null )
	      {
	        result = false;
	      }
	    }
	    else
	    {
	      if ( b == null )
	      {
	        result = false;
	      }
	      else
	      {
	        result = a.equals( b );
	      }
	    }
	    return result;
	  }
}
