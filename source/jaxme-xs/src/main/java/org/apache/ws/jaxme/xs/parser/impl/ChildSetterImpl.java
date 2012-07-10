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
package org.apache.ws.jaxme.xs.parser.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.UndeclaredThrowableException;

import org.apache.ws.jaxme.xs.XSParser;
import org.apache.ws.jaxme.xs.parser.*;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;


/** <p>Default implementation of a {@link org.apache.ws.jaxme.xs.parser.ChildSetter}.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class ChildSetterImpl implements ChildSetter {
  private static final Class[] GETCHILDHANDLER_CLASSES = new Class[]{String.class, String.class, String.class};
  private static final Class[] ZERO_CLASSES = new Class[]{};
  private static final Object[] ZERO_OBJECTS = new Object[]{};

  private XSContext getData() {
    return XSParser.getRunningInstance().getContext();
  }

  /** <p>This method invokes the beans <code>pBean</code> method <code>pMethod</code> with
   * the argument array pArgs, returning a child handler for the element <code>pQName</code>.</p>
   */
  protected Object invokeMethod(Object pBean, Method pMethod, String pName, Object[] pArgs) throws SAXException {
    try {
      return pMethod.invoke(pBean, pArgs);
    } catch (InvocationTargetException e) {
      Throwable t = e.getTargetException();
      if (t instanceof RuntimeException) {
        throw (RuntimeException) t;
      } else if (t instanceof SAXException) {
        throw (SAXException) t;
      } else {
        throw new UndeclaredThrowableException(t);
      }
    } catch (IllegalAccessException e) {
      throw new IllegalStateException("Failed to invoke method " + pMethod.getName() +
                                       " of class " + pMethod.getDeclaringClass() +
                                       " with argument " + pBean + ": IllegalAccessException, " +
                                       e.getMessage()); 
    }
  }


  public ContentHandler getChildHandler(String pQName,
                                         String pNamespaceURI, String pLocalName) throws SAXException {
    final XsSAXParser xsSAXParser = (XsSAXParser) getData().getCurrentContentHandler();
    if (xsSAXParser == null) {
      throw new NullPointerException("No XsSAXParser registered.");
    }
    final Object bean = xsSAXParser.getBean();
    final Class beanClass = bean.getClass();
    try {
      Method m = beanClass.getMethod("getChildHandler", GETCHILDHANDLER_CLASSES);
      if (Modifier.isPublic(m.getModifiers())  &&
          ContentHandler.class.isAssignableFrom(m.getReturnType())) {
        Object result = (ContentHandler) invokeMethod(bean, m, pQName, new Object[]{pQName, pNamespaceURI, pLocalName});
        if (result != null) {
          return (ContentHandler) result;
        }
      }
    } catch (NoSuchMethodException e) {
    }

    final ContentHandler result = getChildHandler(xsSAXParser, pQName, pLocalName);
    if (result == null) {
      throw new LocSAXException("Unknown child element '" + pQName + "' for bean " + bean.getClass().getName(),
                                 getData().getLocator());
    }
    return result;
  }

  /** <p>Creates a new instance of {@link XsSAXParser}, inheriting most properties from
   * its parent parser.</p>
   */
  protected ContentHandler newXsSAXParser(XsSAXParser pParent, Object pBean) {
    return getData().getXsObjectFactory().newXsSAXParser(pBean);
  }

  protected ContentHandler getChildHandler(ContentHandler pParent, String pQName, String pLocalName)
      throws SAXException {
    final XsSAXParser xsSAXParser = (XsSAXParser) pParent;
    final Object bean = xsSAXParser.getBean();
    final Class beanClass = bean.getClass();
    final String s = Character.toUpperCase(pLocalName.charAt(0)) + pLocalName.substring(1);
    try {
      Method m = beanClass.getMethod("create" + s, ZERO_CLASSES);
      if (Modifier.isPublic(m.getModifiers())  &&
         !void.class.equals(m.getReturnType())) {
        Object result = invokeMethod(bean, m, pQName, ZERO_OBJECTS);
        return newXsSAXParser(xsSAXParser, result);
      }
    } catch (NoSuchMethodException e) {
    } catch (SAXException e) {
      throw e;
    }

    return null;
  }
}
