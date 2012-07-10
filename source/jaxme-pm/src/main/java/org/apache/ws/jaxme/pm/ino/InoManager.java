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
package org.apache.ws.jaxme.pm.ino;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;

import javax.xml.bind.Element;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.apache.ws.jaxme.JMManager;
import org.apache.ws.jaxme.JMUnmarshallerHandler;
import org.apache.ws.jaxme.Observer;
import org.apache.ws.jaxme.PMException;
import org.apache.ws.jaxme.PMParams;
import org.apache.ws.jaxme.pm.impl.PMIdImpl;



/** <p>An implementation of a JMManager for a Tamino database.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>s
 */
public class InoManager extends PMIdImpl {
  private java.net.URL dbURL;
  private String user, password;
  private String idTag, elementTag;
  private boolean useGet;
  private static final javax.xml.parsers.SAXParserFactory spf;
  private static final IURLEncoder encoder;

  static {
    spf = SAXParserFactory.newInstance();
    spf.setNamespaceAware(true);
    spf.setValidating(false);
    IURLEncoder e;
    try {
        String myClassName = InoManager.class.getName();
        int offset = myClassName.lastIndexOf('.');
        String packageName = myClassName.substring(0, offset);
        e = (IURLEncoder) Class.forName(packageName + ".URLEncoder14").newInstance();
    } catch (Throwable t) {
    	e = new URLEncoder();
    }
    encoder = e;
  }

  protected String getUser() {
      return user;
  }

  protected String getPassword() {
      return password;
  }

  /** Returns the qualified element name of the root element.
   * This is used in delete or select queries for creation
   * of an XPath query.
   */
  public String getElementTag() {
    return elementTag;
  }

  /** Sets the qualified element name of the root element.
   * This is used in delete or select queries for creation
   * of an XQL statement.
   */
  public void setElementTag(String pElementTag) {
    elementTag = pElementTag;
  }

  /** Returns the qualified attribute name of the ID attribute.
   * This is used in delete or update queries for creation
   * of an XQL statement.
   */
  public String getIdTag() {
    return idTag;
  }

  /** Returns the qualified attribute name of the ID attribute.
   * This is used in delete or update queries for creation
   * of an XQL statement.
   */
  public void setIdTag(String pIdTag) {
    idTag = pIdTag;
  }

  public void init(JMManager pManager) throws JAXBException {
    super.init(pManager);
    idTag = pManager.getProperty("ino.idTag");
    if (idTag == null  ||  idTag.length() == 0) {
      throw new JAXBException("Missing property: 'ino.idTag' (Tag name or attribute name of the element ID)");
    }
    elementTag = pManager.getProperty("ino.elementTag");
    if (elementTag == null  ||  elementTag.length() == 0) {
      throw new JAXBException("Missing property: 'ino.elementTag' (Qualified element name, including namespace prefix)");
    }
    String url = pManager.getProperty("ino.url");
    if (url == null  ||  url.length() == 0) {
      throw new JAXBException("Missing property: 'ino.url' (Tamino database URL)");
    }
    user = pManager.getProperty("ino.user");
    password = pManager.getProperty("ino.password");
    useGet = Boolean.valueOf(pManager.getProperty("ino.useGet")).booleanValue();
  }

  /** <p>Returns a query suited for deleting the element.</p>
   */
  protected String getDeleteQuery(Element pElement)
      throws JAXBException, InvocationTargetException, IllegalAccessException,
  			 NoSuchMethodException, UnsupportedEncodingException {
    String id = getId(pElement);
    if (id == null  ||  id.length() == 0) {
      throw new JAXBException("The element being deleted doesn't have an ID.");
    }
    return "_delete=" +
      encoder.encode(getElementTag() + '[' + getIdTag() + '=' + id + ']');
  }

  /** <p>Returns a query suited for updating the element.</p>
   */
  protected String getUpdateQuery(Element pElement)
  		throws JAXBException, UnsupportedEncodingException {
    StringWriter sw = new StringWriter();
    Marshaller marshaller = getManager().getFactory().createMarshaller();
    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);
    marshaller.marshal(pElement, sw);
    return "_process=" + encoder.encode(sw.toString());
  }

  /** <p>Returns a query suited for inserting the element.</p>
   */
  protected String getInsertQuery(Element pElement)
  		throws JAXBException, UnsupportedEncodingException {
    StringWriter sw = new StringWriter();
    Marshaller marshaller = getManager().getFactory().createMarshaller();
    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);
    marshaller.marshal(pElement, sw);
    return "_process=" + encoder.encode(sw.toString());
  }

  /** <p>Performs a single database query.</p>
   */
  protected java.net.HttpURLConnection getResponse(String pQuery) throws SAXException {
    java.net.URL connectionURL = dbURL;
    try {
      if (useGet) {
        String dburl = connectionURL.toString();
        String url;
        if (dburl.indexOf('?') > 0) {
          url = dburl + "&" + pQuery;
        } else {
          url = dburl + "?" + pQuery;
        }
        try {
          connectionURL = new java.net.URL(url);
          java.net.HttpURLConnection conn =
            (java.net.HttpURLConnection) connectionURL.openConnection();
          conn.setDoOutput(false);
          conn.setDoInput(true);
          return conn;
        } catch (java.net.MalformedURLException e) {
          throw new SAXException("Malformed database URL: " + url);
        }
      } else {
        java.net.HttpURLConnection conn =
          (java.net.HttpURLConnection) connectionURL.openConnection();
        conn.setDoOutput(true);
        conn.setDoInput(true);

        java.io.OutputStream ostream = conn.getOutputStream();
        java.io.Writer w = new java.io.OutputStreamWriter(ostream);
        w.write(pQuery);
        w.close();
        return conn;
      }
    } catch (java.io.IOException e) {
      throw new SAXException("I/O Error: " + e.getMessage(), e);
    }
  }

  /** <p>Performs a single database query.</p>
   */
  protected InoResponseHandler performQuery(String pQuery, java.util.List pList)
      throws SAXException {
    InoResponseHandler irh = new InoResponseHandler();
    if (pList != null) {
      irh.setInoObjectIdList(pList);
    }
    performQuery(pQuery, irh);
    return irh;
  }

  /** <p>Parses a single INO response document.</p>
   */
  protected void performQuery(String pQuery, InoResponseHandler pHandler)
      throws SAXException {
    java.net.HttpURLConnection conn = getResponse(pQuery);
    try {
      org.xml.sax.InputSource isource = new org.xml.sax.InputSource(conn.getInputStream());
      isource.setEncoding(conn.getContentEncoding());
      org.xml.sax.XMLReader xr;
      javax.xml.parsers.SAXParser sp = spf.newSAXParser();
      xr = sp.getXMLReader();
      xr.setContentHandler(pHandler);
      xr.parse(isource);
    } catch (javax.xml.parsers.ParserConfigurationException e) {
      throw new SAXException("ParserConfigurationException: " + e.getMessage(), e);
    } catch (java.io.IOException e) {
      throw new SAXException("I/O Exception: " + e.getMessage(), e);
    }
  }

  /* (non-Javadoc)
   * @see org.apache.ws.jaxme.PM#select(org.apache.ws.jaxme.Observer, java.lang.String, org.apache.ws.jaxme.PMParams)
   */
  public void select(Observer pObserver, String pQuery, PMParams pPlaceHolderArgs) throws JAXBException {
    pQuery = super.parseQuery(pQuery, pPlaceHolderArgs);
    String q;
    int max = pPlaceHolderArgs.getMaxResultDocuments();
    int skip = pPlaceHolderArgs.getSkippedResultDocuments();
    if (max != 0  ||  skip != 0) {
      q = "_xql(" + (skip+1) + "," + max + ")=";
    } else {
      q = "_xql=";
    }

    InoResponseHandler irh = new InoResponseHandler();
	try {
		q += encoder.encode(pQuery);
	} catch (UnsupportedEncodingException e) {
		throw new PMException(e);
	}
	JMUnmarshallerHandler handler = (JMUnmarshallerHandler) getManager().getFactory().createUnmarshaller().getUnmarshallerHandler();
	handler.setObserver(pObserver);
	irh.setResultHandler(handler);
	try {
		performQuery(q, irh);
	} catch (SAXException e) {
		throw new PMException(e);
	}
  }

  /* (non-Javadoc)
   * @see org.apache.ws.jaxme.PM#insert(javax.xml.bind.Element)
   */
  public void insert(javax.xml.bind.Element pElement) throws PMException {
    try {
      String query = getInsertQuery(pElement);
      java.util.List idList = new java.util.ArrayList();
      performQuery(query, idList);
      if (idList.size() == 0) {
        throw new PMException("Query did not return an ino:id");
      }
    } catch (SAXException e) {
      throw new PMException(e);
    } catch (JAXBException e) {
      if (e instanceof PMException) {
        throw (PMException) e;
      } else {
        throw new PMException(e);
      }
    } catch (UnsupportedEncodingException e) {
        throw new PMException(e);
    }
  }

  /* (non-Javadoc)
   * @see org.apache.ws.jaxme.PM#update(javax.xml.bind.Element)
   */
  public void update(javax.xml.bind.Element pElement) throws PMException {
      try {
          String query = getUpdateQuery(pElement);
          performQuery(query, (java.util.List) null);
      } catch (SAXException e) {
          throw new PMException(e);
      } catch (UnsupportedEncodingException e) {
          throw new PMException(e);
      } catch (JAXBException e) {
          if (e instanceof PMException) {
              throw (PMException) e;
          } else {
              throw new PMException(e);
          }
      }
  }

  /* (non-Javadoc)
   * @see org.apache.ws.jaxme.PM#delete(javax.xml.bind.Element)
   */
  public void delete(javax.xml.bind.Element pElement) throws PMException {
    try {
      String query = getDeleteQuery(pElement);
      performQuery(query, (java.util.List) null);
    } catch (NoSuchMethodException e) {
      throw new PMException(e);
    } catch (IllegalAccessException e) {
      throw new PMException(e);
    } catch (InvocationTargetException e) {
      throw new PMException(e.getTargetException());
    } catch (SAXException e) {
      throw new PMException(e);
    } catch (JAXBException e) {
      if (e instanceof PMException) {
        throw (PMException) e;
      } else {
        throw new PMException(e);
      }
    } catch (UnsupportedEncodingException e) {
        throw new PMException(e);
    }
  }
}
