/*
 * Copyright 2003, 2004  The Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.

 */
package org.apache.ws.jaxme.xs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.ws.jaxme.xs.impl.XSLogicalParser;
import org.apache.ws.jaxme.xs.parser.XSContext;
import org.apache.ws.jaxme.xs.parser.XsSAXParser;
import org.apache.ws.jaxme.xs.parser.impl.XSContextImpl;
import org.apache.ws.jaxme.xs.util.LoggingContentHandler;
import org.apache.ws.jaxme.xs.xml.XsESchema;
import org.apache.ws.jaxme.xs.xml.XsObjectFactory;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;


/** <p>The XML schema parser.</p>
 * 
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 * @version $Id: XSParser.java 437259 2006-08-27 00:25:00Z jochen $
 */
public class XSParser {
  /** <p>The XML Schema URI: <code>http://www.w3.org/2001/XMLSchema</code></p>
   */
  public static final String XML_SCHEMA_URI = "http://www.w3.org/2001/XMLSchema";

  private static final ThreadLocal parser = new ThreadLocal();

  private XSContext data;
  private boolean notValidating;
  private List addedImports = new ArrayList();

  /** <p>Creates a new instance of XSParser.</p>
   */
  public XSParser() {
  }

  /** <p>Sets whether the parser is validating.</p>
   */
  public void setValidating(boolean pValidating) {
    notValidating = !pValidating;
  }

  /** <p>Returns whether the parser is validating.</p>
   */
  public boolean isValidating() {
    return !notValidating;
  }

  /** <p>Adds a schema being imported by the parser. This feature
   * is useful, if a schema silently assumes the presence of additional
   * datatypes. For example, a WSDL definition will contain references
   * to SOAP datatypes without explicit import.</p>
   * @param pNamespace Matches the "xs:import" nodes "namespace" attribute.
   *   In particular it may be null, in which case the imported schema may
   *   not have a targetNamespace.
   * @param pSchemaLocation Matches the "xs:import" nodes "schemaLocation"
   *   attribute. In particular it may be null.
   * @see #addImport(String, Node)
   */
  public void addImport(String pNamespace, String pSchemaLocation) {
    addedImports.add(new XSLogicalParser.AddedImport(pNamespace, pSchemaLocation));
  }

  /** <p>Adds a schema being imported by the parser. The schema is
   * provided as a DOM node. This feature is useful, if a schema
   * silently assumes the presence of additional datatypes. For
   * example, a WSDL definition will contain references to SOAP
   * datatypes without explicit import.</p>
   * @param pNamespace Matches the "xs:import" nodes "namespace"
   *   attribute. In particular it may be null, in which case the
   *   imported schema may not have a targetNamespace.
   * @param pSchemaLocation The imported schemas system ID, if known,
   * or null. Knowing the system ID is important only, if you need
   * to prevent recursive schematas being included more than once.
   * @param pSchema A DOM node with the schema being imported.
   * @see #addImport(String, String)
   */
  public void addImport(String pNamespace, String pSchemaLocation, Node pSchema) {
    addedImports.add(new XSLogicalParser.AddedImport(pNamespace, pSchemaLocation, pSchema));
  }

  /** <p>Provides access to the parsers internal data. Use the
   * {@link #getRunningInstance()} method to find the parser.</p>
   */
  public XSContext getContext() {
    if (data == null) {
      setData(new XSContextImpl());
    }
    return data;
  }

  protected void setData(XSContext pData) { data = pData; }

  /** <p>Parses the given XML schema. and returns a syntactical
   * representation.</p>
   * @see #parse(InputSource)
   */
  public XsESchema parseSyntax(final InputSource pSource) throws ParserConfigurationException, SAXException, IOException {
    XSContext context = getContext();
    parser.set(this);
    FileOutputStream fos = null;
    try {
      XsObjectFactory factory = context.getXsObjectFactory();
      XsSAXParser xsSAXParser = factory.newXsSAXParser();
      context.setCurrentContentHandler(xsSAXParser);
      XMLReader xr = factory.newXMLReader(isValidating());
      String logDir = System.getProperty("org.apache.ws.jaxme.xs.logDir");
      if (logDir != null) {
    	  File tmpFile = File.createTempFile("jaxmexs", ".xsd", new File(logDir));
    	  fos = new FileOutputStream(tmpFile);
    	  LoggingContentHandler lch = new LoggingContentHandler(fos);
    	  lch.setParent(xr);
    	  xr = lch;
    	  String msg = "Read from " + pSource.getPublicId() + ", " + pSource.getSystemId() + " at " + new Date();
    	  lch.comment(msg.toCharArray(), 0, msg.length());
      }
      xr.setContentHandler(xsSAXParser);
      xr.parse(pSource);
      if (fos != null) {
    	  fos.close();
    	  fos = null;
      }
      return (XsESchema) xsSAXParser.getBean();
	} finally {
	  if (fos != null) {
		  try { fos.close(); } catch (Throwable ignore) {}
	  }
      context.setCurrentContentHandler(null);
      parser.set(null);
    }
  }

  protected XSLogicalParser newXSLogicalParser() {
    XSLogicalParser logicalParser = getContext().getXSObjectFactory().newXSLogicalParser();
    logicalParser.setValidating(isValidating());
    for (int i = 0;  i < addedImports.size();  i++) {
      XSLogicalParser.AddedImport addedImport = (XSLogicalParser.AddedImport) addedImports.get(i);
      if (addedImport.getNode() == null) {
        logicalParser.addImport(addedImport.getNamespace(), addedImport.getSchemaLocation());
      } else {
        logicalParser.addImport(addedImport.getNamespace(), addedImport.getSchemaLocation(), addedImport.getNode());
      }
    }
    return logicalParser;
  }

  /** <p>Parses the given XML schema and returns a logical representation.</p>
   */
  public XSSchema parse(InputSource pSource) throws ParserConfigurationException, SAXException, IOException {
    XSContext myData = getContext();
    parser.set(this);
    try {
      return newXSLogicalParser().parse(pSource);
    } finally {
      myData.setCurrentContentHandler(null);
      parser.set(null);
    }
  }

  /** <p>Parses the given DOM node containing an an XML schema and returns
   * a logical representation.</p>
   * @param pNode A node containing a valid XML document. Must be either
   *   an instance of {@link org.w3c.dom.Document}, an instance of
   *   {@link org.w3c.dom.Element}, or an instance of
   *   {@link org.w3c.dom.DocumentFragment}. In the latter case, make
   *   sure, that the fragment contains a single root element.
   */
  public XSSchema parse(Node pNode) throws SAXException {
    XSContext myData = getContext();
    parser.set(this);
    try {
      return newXSLogicalParser().parse(pNode);
    } finally {
      myData.setCurrentContentHandler(null);
      parser.set(null);
    }
  }

  /** <p>Returns an instance of {@link XSContentHandler} for parsing a stream
   * of SAX events.</p>
   */
  public XSContentHandler getXSContentHandler(String pSchemaLocation) throws SAXException {
    parser.set(this);
    return newXSLogicalParser().getXSContentHandler(pSchemaLocation);
  }

  /** <p>Provides access to the currently running instance of <code>XSParser</code>.</p>
   */
  public static XSParser getRunningInstance() {
    return (XSParser) parser.get();
  }
}
