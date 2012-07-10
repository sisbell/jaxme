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
package org.apache.ws.jaxme.generator.sg.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.ws.jaxme.generator.Generator;
import org.apache.ws.jaxme.xs.jaxb.impl.JAXBAppinfoImpl;
import org.apache.ws.jaxme.xs.jaxb.impl.JAXBXsObjectFactoryImpl;
import org.apache.ws.jaxme.xs.xml.XsEAppinfo;
import org.apache.ws.jaxme.xs.xml.XsObject;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class JaxMeSGFactory extends JAXBSGFactory {
  public class JaxMeAppinfoImpl extends JAXBAppinfoImpl {
    JaxMeAppinfoImpl(XsObject pParent) {
      super(pParent);
    }

    public ContentHandler getChildHandler(String pQName, String pNamespaceURI, String pLocalName) throws SAXException {
      List myAnnotationClasses = getAnnotationClasses();
      for (int i = 0;  i < myAnnotationClasses.size();  i +=3) {
        if (((String) myAnnotationClasses.get(i)).equals(pNamespaceURI)  &&
            ((String) myAnnotationClasses.get(i+1)).equals(pLocalName)) {
          Class c = (Class) myAnnotationClasses.get(i+2);
          Object o;
          try {
            Constructor con = c.getConstructor(new Class[]{JaxMeXsObjectFactory.class, Locator.class});
            o = con.newInstance(new Object[]{this, getLocator()});
          } catch (NoSuchMethodException e) {
            throw new SAXException("Invalid appinfo class " + c.getClass().getName() +
                                    ": Doesn't have a constructor taking the arguments " +
                                    JaxMeXsObjectFactory.class.getName() + " and " +
                                    Locator.class.getName());
          } catch (InvocationTargetException e) {
            Throwable t = e.getTargetException();
            String msg = "Failed to create appinfo element of class " + c.getClass().getName() +
                         ": " + t.getClass().getName() + ", " + t.getMessage();
            if (t instanceof Exception) {
              throw new SAXException(msg, (Exception) t);
            } else {
              throw new SAXException(msg, e);
            }
          } catch (Exception e) {
            throw new SAXException("Failed to create appinfo element of class " + c.getClass().getName() +
                                    ": " + e.getClass().getName());
          }
          return getObjectFactory().newXsSAXParser(o);
        }
      }
      return super.getChildHandler(pQName, pNamespaceURI, pLocalName);
    }
  }

  public class JaxMeXsObjectFactory extends JAXBXsObjectFactoryImpl {
    public XsEAppinfo newXsEAppinfo(XsObject pParent) {
      return new JaxMeAppinfoImpl(pParent);
    }
  }

  public JaxMeSGFactory(Generator pGenerator) {
    super(pGenerator);
  }

  private List annotationClasses = new ArrayList();
  protected List getAnnotationClasses() {
    return annotationClasses;
  }

  public void addAnnotationClass(String pNamespaceURI, String pLocalName, Class pClass) {
    if (pNamespaceURI == null  ||  pNamespaceURI.length() == 0) {
      throw new NullPointerException("A nonempty namespace URI must be given.");
    }
    if (pLocalName == null  ||  pLocalName.length() == 0) {
      throw new NullPointerException("A nonempty local name must be given.");
    }
    if (pClass == null) {
      throw new NullPointerException("An annotation class must be given.");
    }
    annotationClasses.add(pNamespaceURI);
    annotationClasses.add(pLocalName);
    annotationClasses.add(pClass);
  }
}
