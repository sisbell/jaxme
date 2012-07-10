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
package org.apache.ws.jaxme.pm.xmldb;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.bind.Element;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.ws.jaxme.JMManager;
import org.apache.ws.jaxme.JMUnmarshallerHandler;
import org.apache.ws.jaxme.Observer;
import org.apache.ws.jaxme.PMException;
import org.apache.ws.jaxme.PMParams;
import org.apache.ws.jaxme.pm.impl.PMIdImpl;
import org.xml.sax.ContentHandler;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;
import org.xmldb.api.modules.XPathQueryService;


/** This is a persistence manager for the XML::DB interface. In other
 * words, using this persistence manager you may read documents from a
 * database via the XML::DB API or write them into the database using the
 * same API.<br>
 * This persistence manager is generic: The same manager works fine for
 * any JAXB element.
 */
public class XmlDbPM extends PMIdImpl {
  private Class driverClass;
  private String collection;
  private String user;
  private String password;
  private Map databaseProperties;
  private String xPathQueryService = "XPathQueryService";
  private String xPathQueryServiceVersion = "1.0";

  /** Creates a new instance of <code>XmlDbPM</code>.
   */
  public XmlDbPM() {
  }

  public void init(JMManager pManager) throws JAXBException {
    super.init(pManager);

    String driverClassName = pManager.getProperty("xmldb.driver");
    if (driverClassName == null  ||  driverClassName.length() == 0) {
      throw new JAXBException("Missing property: 'xmldb.driver' (driver class name)");
    }

    String coll = pManager.getProperty("xmldb.collection");
    if (coll == null  ||  coll.length() == 0) {
      throw new JAXBException("Missing property: 'xmldb.collection' (collection name)");
    }
    setCollection(coll);
    setUser(pManager.getProperty("xmldb.user"));
    setPassword(pManager.getProperty("xmldb.password"));

    for (int i = 0;  ;  i++) {
      String p = "xmldb.property." + i;
      String v = pManager.getProperty(p);
      if (v == null) {
        break;
      }
      int offset = v.indexOf('=');
      if (offset == -1) {
        throw new JAXBException("Invalid database property value " + p + ": Expected name=value, got " + v);
      }
      String name = v.substring(0, offset);
      String value = v.substring(offset+1);
      if (databaseProperties != null) {
        databaseProperties = new HashMap();
      }
      databaseProperties.put(name, value);
    }

    Class c;
    try {
      c = Class.forName(driverClassName);
    } catch (ClassNotFoundException e) {
      try {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
          cl = getClass().getClassLoader();   
        }
        c = cl.loadClass(driverClassName);
      } catch (ClassNotFoundException f) {
        throw new JAXBException("Failed to load driver class " + driverClassName, e);
      }
    }
    setDriverClass(c);
  }

  /** Returns the collection name.
   */
  public String getCollection() {
    return collection;
  }

  /** Sets the collection name.
   */
  public void setCollection(String pCollection) {
    collection = pCollection;
  }

  /** Returns the driver class.
   */
  public Class getDriverClass() {
    return driverClass;
  }

  /** Sets the driver class.
   */
  public void setDriverClass(Class pDriverClass) {
    driverClass = pDriverClass;
  }

  /** Returns the password.
   */
  public String getPassword() {
    return password;
  }

  /** Sets the password.
   */
  public void setPassword(String pPassword) {
    password = pPassword;
  }

  /** Returns the users name.
   */
  public String getUser() {
    return user;
  }

  /** Sets the users name.
   */
  public void setUser(String pUser) {
    user = pUser;
  }

  /** Returns the name of the XPathQueryService. Defaults to
   * "XPathQueryService".
   */
  public String getXPathQueryService() {
    return xPathQueryService;
  }

  /** Sets the name of the XPathQueryService. Defaults to
   * "XPathQueryService".
   */
  public void setXPathQueryService(String pXpathQueryService) {
    xPathQueryService = pXpathQueryService;
  }

  /** Returns the version of the XPathQueryService. Defaults to
   * "1.0".
   */
  public String getXPathQueryServiceVersion() {
    return xPathQueryServiceVersion;
  }

  /** Sets the version of the XPathQueryService. Defaults to
   * "1.0".
   */
  public void setXPathQueryServiceVersion(String pXpathQueryServiceVersion) {
    xPathQueryServiceVersion = pXpathQueryServiceVersion;
  }

  /** Returns the database collection by invoking
   * {@link DatabaseManager#getCollection(String)}.
   */
  protected Collection getXmlDbCollection()
      throws XMLDBException, IllegalAccessException, InstantiationException {
    Database database = (Database) getDriverClass().newInstance();
    if (databaseProperties != null) {
      for (Iterator iter = databaseProperties.entrySet().iterator();  iter.hasNext();  ) {
        Map.Entry entry = (Map.Entry) iter.next();
        database.setProperty((String) entry.getKey(), (String) entry.getValue());
      }
    }
    DatabaseManager.registerDatabase(database); 
    return DatabaseManager.getCollection(getCollection());
  }


  /* (non-Javadoc)
   * @see org.apache.ws.jaxme.PM#select(org.apache.ws.jaxme.Observer, java.lang.String, org.apache.ws.jaxme.PMParams)
   */
  public void select(Observer pObserver, String pQuery, PMParams pPlaceHolderArgs) throws JAXBException {
    if (pPlaceHolderArgs != null) {
      pQuery = super.parseQuery(pQuery, pPlaceHolderArgs);
    }

    if (pQuery == null) {
      throw new IllegalArgumentException("A query must be specified");
    }

	Collection col;
	try {
		col = getXmlDbCollection();
		XPathQueryService service = (XPathQueryService) col.getService(getXPathQueryService(),
																	   getXPathQueryServiceVersion());
		ResourceSet result = service.query(pQuery);
		if (result != null) {
			ResourceIterator i = result.getIterator();
			if (i.hasMoreResources()) {
				JMUnmarshallerHandler handler = (JMUnmarshallerHandler) getManager().getFactory().createUnmarshaller().getUnmarshallerHandler();
				handler.setObserver(pObserver);
				while(i.hasMoreResources()) {
					XMLResource r = (XMLResource) i.nextResource();
					r.getContentAsSAX(handler);
				}
			}
		}
	} catch (XMLDBException e) {
		throw new PMException(e);
	} catch (IllegalAccessException e) {
		throw new PMException(e);
	} catch (InstantiationException e) {
		throw new PMException(e);
	}
  }

  /* (non-Javadoc)
   * @see org.apache.ws.jaxme.PM#insert(javax.xml.bind.Element)
   */
  public void insert(Element pElement) throws PMException {
    try {
      Collection col = getXmlDbCollection();
      String id = getId(pElement);
      XMLResource resource = (XMLResource) col.createResource(id, XMLResource.RESOURCE_TYPE);
      ContentHandler ch = resource.setContentAsSAX();
      Marshaller marshaller = getManager().getFactory().createMarshaller();
      marshaller.marshal(pElement, ch);
      col.storeResource(resource);
    } catch (XMLDBException e) {
      throw new PMException(e);
    } catch (IllegalAccessException e) {
      throw new PMException(e);
    } catch (InstantiationException e) {
      throw new PMException(e);
    } catch (NoSuchMethodException e) {
      throw new PMException(e);
    } catch (InvocationTargetException e) {
      throw new PMException(e.getTargetException());
    } catch (JAXBException e) {
      if (e instanceof PMException) {
        throw (PMException) e;
      } else {
        throw new PMException(e);
      }
    }
  }

  /* (non-Javadoc)
   * @see org.apache.ws.jaxme.PM#update(javax.xml.bind.Element)
   */
  public void update(Element pElement) throws PMException {
    try {
      Collection col = getXmlDbCollection();
      String id = getId(pElement);
      XMLResource resource = (XMLResource) col.getResource(id);
      ContentHandler ch = resource.setContentAsSAX();
      Marshaller marshaller = getManager().getFactory().createMarshaller();
      marshaller.marshal(pElement, ch);
      col.storeResource(resource);
    } catch (XMLDBException e) {
      throw new PMException(e);
    } catch (IllegalAccessException e) {
      throw new PMException(e);
    } catch (InstantiationException e) {
      throw new PMException(e);
    } catch (NoSuchMethodException e) {
      throw new PMException(e);
    } catch (InvocationTargetException e) {
      throw new PMException(e.getTargetException());
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
  public void delete(Element pElement) throws PMException {
    try {
     Collection col = getXmlDbCollection();
     String id = getId(pElement);
     XMLResource resource = (XMLResource) col.createResource(id, XMLResource.RESOURCE_TYPE);
     col.removeResource(resource);
   } catch (XMLDBException e) {
     throw new PMException(e);
   } catch (IllegalAccessException e) {
     throw new PMException(e);
   } catch (InstantiationException e) {
     throw new PMException(e);
   } catch (InvocationTargetException e) {
     throw new PMException(e.getTargetException());
   } catch (NoSuchMethodException e) {
     throw new PMException(e);
    } catch (JAXBException e) {
      if (e instanceof PMException) {
        throw (PMException) e;
      } else {
        throw new PMException(e);
      }
   }
  }

}
