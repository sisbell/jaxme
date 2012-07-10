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
package org.apache.ws.jaxme.impl;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import org.apache.ws.jaxme.JMManager;
import org.xml.sax.SAXException;



/** <p>An instance of this class represents a config file.
 * A JAXBContext requires an associated Configuration which
 * is located through the classpath.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 * @version $Id: Configuration.java 479002 2006-11-24 21:19:32Z jochen $
 */
public class Configuration {
  JAXBContextImpl context;
  Manager currentManager;
  Map managers = new HashMap();
  private Class jmMarshallerClass = JMMarshallerImpl.class;
  private Class jmUnmarshallerClass = JMUnmarshallerImpl.class;
  private Class jmValidatorClass = JMValidatorImpl.class;

  public Configuration(JAXBContextImpl pContext) {
    context = pContext;
  }

  public class Manager implements JMManager {
    public class Property {
      private String managerName;
      private String value;

      public String getName() { return managerName; }
      public void setName(String pName) { managerName = pName; }
      public String getValue() { return value; }
      public void setValue(String pValue) { value = pValue; }
      public void finish() throws SAXException {
        if (managerName == null) {
          throw new NullPointerException("Missing 'name' attribute in 'property' element.");
        }
        if (value == null) {
          throw new NullPointerException("Missing 'value' attribute in 'property' element.");
        }
        if (properties == null) {
          properties = new HashMap();
        }
        if (properties.put(managerName, value) != null) {
          throw new IllegalStateException("The property " + managerName + " was specified more than once.");
        }
      }
    }

    private QName name;
    private Class elementInterface;
    private Class elementClass;
    private Class handlerClass;
    private Class driverClass;
    private Class driverControllerClass;
    private Class pmClass;
	private String prefix;
    private Map properties;

	public String getPrefix() {
		return prefix;
	}
	/** Sets the suggested prefix for the elements namespace.
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	public void setQName(QName pName) { name = pName; }
    public QName getQName() { return name; }
    public void setElementClass(String pElementClass) throws ClassNotFoundException {
        elementClass = context.getClassLoader().loadClass(pElementClass);
    }
    public Class getElementClass() { return elementClass; }
    public void setElementInterface(String pElementInterface) throws ClassNotFoundException {
        elementInterface = context.getClassLoader().loadClass(pElementInterface);
    }
    public Class getElementInterface() { return elementInterface; }
	public Class getHandlerClass() { return handlerClass; }
	public void setHandlerClass(String pHandlerClass) throws ClassNotFoundException {
        handlerClass = context.getClassLoader().loadClass(pHandlerClass);
		if (!JMSAXElementParser.class.isAssignableFrom(handlerClass)) {
			throw new IllegalStateException("The class " + handlerClass.getName()
											+ " is not implementing "
											+ JMSAXElementParser.class.getName());
		}
    }
    public void setDriverClass(String pMarshallerClass) throws ClassNotFoundException {
        driverClass = context.getClassLoader().loadClass(pMarshallerClass);
		if (!JMSAXDriver.class.isAssignableFrom(driverClass)) {
			throw new IllegalStateException("The class " + driverClass.getName()
											+ " is not implementing "
											+ JMSAXDriver.class.getName());
		}
    }
	public Class getDriverClass() { return driverClass; }
    public JMSAXDriver getDriver() throws SAXException {
		try {
			return (JMSAXDriver) driverClass.newInstance();
		} catch (InstantiationException e) {
			throw new SAXException("Unable to instantiate driver class " + driverClass.getName(), e);
		} catch (IllegalAccessException e) {
			throw new SAXException("Illegal access to driver class " + driverClass.getName(), e);
		}
	}
	/** Sets the persistence manager class.
	 */
	public void setPmClass(String pPersistencyClass) throws ClassNotFoundException {
        pmClass = context.getClassLoader().loadClass(pPersistencyClass);
    }
    public Class getPmClass() { return pmClass; }
    public JAXBContextImpl getFactory() { return context; }
    public Property createProperty() {
      return new Property();
    }
    public String getProperty(String pName) {
      if (pName == null) {
        throw new IllegalArgumentException("The property name must not be null.");
      }
      if (properties == null) {
        return null;
      }
      return (String) properties.get(pName);
    }
    public void finish() throws SAXException {
      if (currentManager != this) {
        throw new IllegalStateException("currentManager != this");
      }
      try {
		  if (prefix != null) {
			  name = new QName(name.getNamespaceURI(), name.getLocalPart(), prefix);
		  }
		  context.addManager(currentManager);
		  currentManager = null;
      } catch (Exception e) {
        throw new SAXException(e.getMessage(), e);
      }
    }

    public JMSAXElementParser getHandler() throws SAXException {
		try {
			return (JMSAXElementParser) handlerClass.newInstance();
		} catch (InstantiationException e) {
			throw new SAXException("Unable to instantiate handler class "
								   + jmUnmarshallerClass.getName(), e);
		} catch (IllegalAccessException e) {
			throw new SAXException("Illegal access to handler class "
								   + jmUnmarshallerClass.getName(), e);
		}
    }

	public Object getElementJ() throws JAXBException {
		try {
			return elementClass.newInstance();
		} catch (InstantiationException e) {
			throw new JAXBException("Unable to instantiate handler class "
								    + jmUnmarshallerClass.getName(), e);
		} catch (IllegalAccessException e) {
			throw new JAXBException("Illegal access to handler class "
								    + jmUnmarshallerClass.getName(), e);
		}
    }

	public Object getElementS() throws SAXException {
		try {
			return elementClass.newInstance();
		} catch (InstantiationException e) {
			throw new SAXException("Unable to instantiate element class "
								   + elementClass.getName(), e);
		} catch (IllegalAccessException e) {
			throw new SAXException("Illegal access to element class "
								   + elementClass.getName(), e);
		}
    }

	public JMSAXDriverController getDriverController() throws SAXException {
		try {
			return (JMSAXDriverController) driverControllerClass.newInstance();
		} catch (InstantiationException e) {
			throw new SAXException("Unable to instantiate driver controller class "
								   + driverControllerClass.getName(), e);
		} catch (IllegalAccessException e) {
			throw new SAXException("Illegal access to driver controller class "
								   + driverControllerClass.getName(), e);
		}
	}

	/** Returns the subclass of {@link JMSAXDriverController},
	 * which is being used for marshalling this object.
	 */
	public Class getDriverControllerClass() {
		return driverControllerClass;
	}

	/** Sets the subclass of {@link JMSAXDriverController},
	 * which is being used for marshalling this object.
	 */
	public void setDriverControllerClass(String pDriverControllerClass) throws ClassNotFoundException {
		driverControllerClass = context.getClassLoader().loadClass(pDriverControllerClass);
		if (!JMSAXDriverController.class.isAssignableFrom(driverControllerClass)) {
			throw new IllegalStateException("The class " + driverControllerClass.getName()
											+ " is not implementing "
											+ JMSAXDriverController.class.getName());
		}
	}
  }

  /** <p>Creates a new Manager.</p>
   */
  public Manager createManager() {
    if (currentManager != null) {
      throw new IllegalStateException("currentManager != null");
    }
    currentManager = new Manager();
    return currentManager;
  }

  /** <p>Sets the JMMarshaller class.</p>
   */
  public void setJMMarshallerClass(Class pJMMarshallerClass) {
    jmMarshallerClass = pJMMarshallerClass;
  }

  /** <p>Returns the JMMarshaller class.</p>
   */
  public Class getJMMarshallerClass() {
    return jmMarshallerClass;
  }

  /** <p>Sets the JMUnmarshaller class.</p>
   */
  public void setJMUnmarshallerClass(Class pJMUnmarshallerClass) {
    jmUnmarshallerClass = pJMUnmarshallerClass;
  }

  /** <p>Returns the JMUnmarshaller class.</p>
   */
  public Class getJMUnmarshallerClass() {
    return jmUnmarshallerClass;
  }

  /** <p>Sets the JMValidator class.</p>
   */
  public void setJMValidatorClass(Class pJMValidatorClass) {
    jmValidatorClass = pJMValidatorClass;
  }

  public Class getJMValidatorClass() {
    return jmValidatorClass;
  }
}
