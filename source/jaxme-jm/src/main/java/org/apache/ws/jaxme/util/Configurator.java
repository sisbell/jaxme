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
package org.apache.ws.jaxme.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


/** <p>The Configurator is an idea borrowed by the Ant project.
 * It is a SAX2 handler that reads a config file which is
 * represented by a hierarchy of Java beans. For example:</p>
 * <pre>
 *   &lt;outerBean foo="true" bar="Quite right"&gt;
 *     &lt;innerBean whatever="57"&gt;
 *     &lt;/innerBean
 *   &lt;/outerBean&gt;
 * </pre>
 * The example would create an object outerBean and call its
 * methods <code>setFoo(boolean)</code> and
 * <code>setBar(String)</code> to process the attributes.
 * It would also create a bean innerBean by calling the
 * outerBeans method <code>createInnerBean()</code>.
 * Finally the innerBean is configured by calling
 * <code>setWhatever(int)</code>.</p>
 * 
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 * @version $Id: Configurator.java 232067 2005-03-10 10:14:08Z jochen $
 */
public class Configurator implements ContentHandler, NamespaceResolver {
  private static final Class[] zeroClasses = new Class[0];
  private static final Object[] zeroObjects = new Object[0];
  private static final Class[] oneClassString = new Class[]{String.class};
  private static final Class[] oneClassAttributes = new Class[]{Attributes.class};

  private String[] namespaces;
  private List beans = new ArrayList();
  private List names = new ArrayList();
  private Object currentBean;
  private String currentName;
  private Locator locator;
  private Object beanFactory;
  private Object rootObject;
  private Object resultBean;
  private int ignoreLevel = 0;
  private NamespaceSupport nss = new NamespaceSupport();
  boolean nssNeedsContext;

  /** <p>Sets the namespace handled by the configurator. Defaults
   * to no namespace. Shortcut for
   * <code>setNamespace(new String[]{pNamespace})</code>.</p>
   *
   * @param pNamespace The namespace being set
   */
  public void setNamespace(String pNamespace) {
    setNamespaces(new String[]{pNamespace});
  }

  /** <p>Sets the namespaces handled by the configurator. Defaults
   * to no namespace.</p>
   *
   * @param pNamespaces The namespaces being set
   */
  public void setNamespaces(String[] pNamespaces) {
    namespaces = pNamespaces;
  }

  /** <p>Returns the namespaces handled by the configurator. Defaults
   * to no namespace.</p>
   */
  public String[] getNamespaces() {
    return namespaces;
  }

  /** <p>Sets the Locator being used in error messages.</p>
   */
  public void setDocumentLocator(Locator pLocator) {
    locator = pLocator;
  }

  /** <p>Returns the Locator being used in error messages.</p>
   */
  public Locator getDocumentLocator() {
    return locator;
  }

  /** <p>Sets the bean factory, creating the outermost element.
   * The bean factory must have a matching <code>createElementName()</code>
   * method, with <samp>ElementName</samp> being the element name
   * of the document element.</p>
   */
  public void setBeanFactory(Object pFactory) {
    beanFactory = pFactory;
  }

  /** <p>Returns the bean factory, creating the outermost element.
   * The bean factory must have a matching <code>createElementName()</code>
   * method, with <samp>ElementName</samp> being the element name
   * of the document element.</p>
   */
  public Object getBeanFactory() {
    return beanFactory == null ? this : beanFactory;
  }

  /** <p>An alternative to using the bean factory. This object
   * is used as the root object, regardless of its name.</p>
   */
  public void setRootObject(Object pRootObject) {
    rootObject = pRootObject;
  }

  /** <p>An alternative to using the bean factory. This object
   * is used as the root object, regardless of its name.</p>
   */
  public Object getRootObject() {
    return rootObject;
  }

  public void startDocument() throws SAXException {
    currentBean = null;
    currentName = null;
    beans.clear();
    names.clear();
    ignoreLevel = 0;
    nss.reset();
    nssNeedsContext = true;
  }
  public void endDocument() throws SAXException {}

  public void startPrefixMapping(String pPrefix, String pURI)
      throws SAXException {
    nss.declarePrefix(pPrefix, pURI);
  }

  public void endPrefixMapping(String pPrefix)
      throws SAXException {
    nss.undeclarePrefix(pPrefix);
  }

  /** <p>Returns whether a namespace is matching the configured
   * namespace.</p>
   */
  protected boolean isNamespaceMatching(String pNamespace) {
    if (pNamespace == null) {
      pNamespace = "";
    }
    String[] myNamespaces = getNamespaces();
    if (myNamespaces == null  ||  myNamespaces.length == 0) {
      return pNamespace.length() == 0;
    } else {
      for (int i = 0;  i < myNamespaces.length;  i++) {
        String s = myNamespaces[i];
        if (s == null) {
          s = "";
        }
        if (s.equals(pNamespace)) {
          return true;
        }
      }
      return false;
    }
  }

  /** <p>Given a prefix and a name, creates a method name matching
   * the prefix and the name.</p>
   */
  protected String getMethodNameFor(String pPrefix, String pName) {
    StringBuffer result = new StringBuffer(pPrefix);
    for (int i = 0;  i < pName.length();  i++) {
      char c = pName.charAt(i);
      if (i == 0) {
        if (Character.isJavaIdentifierStart(c)) {
          result.append(Character.toUpperCase(c));
        } else {
          result.append('_');
        }
      } else {
        if (Character.isJavaIdentifierPart(c)) {
          result.append(c);
        } else {
          result.append('_');
        }
      }
    }
    return result.toString();
  }

  /** <p>Creates a new bean, matching the element name <code>pLocalName</code>.
   * If this is the outermost bean, calls the bean factorys
   * <code>createBeanName()</code> method, otherwise calls the current beans
   * <code>createBeanName()</code> method, with <samp>beanName</samp>
   * being the value of the <code>pLocalName</code> parameter.</p>
   */
  public void startElement(String pNamespaceURI, String pQName,
                            String pLocalName, Attributes pAttr) throws SAXException {
    if (ignoreLevel > 0) {
      ++ignoreLevel;
      return;
    }
    
    if (!isNamespaceMatching(pNamespaceURI)) {
      if (currentBean == null) {
        String[] myNamespaces = getNamespaces();
        if (myNamespaces == null  ||  myNamespaces.length == 0) {
          throw new SAXParseException("The document element must have the default namespace.",
                                       getDocumentLocator());
        } else {
          StringBuffer sb = new StringBuffer("The document element must have either of the namespaces: ");
          for (int i = 0;  i < myNamespaces.length;  i++) {
            if (i > 0) sb.append(",");
            String s = myNamespaces[i];
            if (s == null) {
              s = "";
            } 
            sb.append('"').append(s).append('"');
          }
          throw new SAXParseException(sb.toString(), getDocumentLocator());
        }
      }

      ++ignoreLevel;
      return; // Namespace not matching, ignore this element
    }

    Object o;
    if (currentBean == null  &&  rootObject != null) {
      o = rootObject;
    } else {
      o = null;
      Object factory = (currentBean == null) ? beanFactory : currentBean;
      String methodName = getMethodNameFor("create", pLocalName);
      try {
        o = invokeMethod(methodName, factory, oneClassAttributes,
                         new Object[]{pAttr});
      } catch (SAXParseException e) {
        if (e.getException() != null  &&
            e.getException() instanceof NoSuchMethodException) {
          try {
            o = invokeMethod(methodName, factory, zeroClasses, zeroObjects);
            e = null;
          } catch (SAXParseException f) {
            if (f.getException() != null  &&
                f.getException() instanceof NoSuchMethodException) {
              if (currentBean == null) {
                throw new SAXParseException("Invalid document element: " + pQName,
                                             getDocumentLocator());
              } else {
                throw new SAXParseException("Invalid child element name: " + pQName,
                                             getDocumentLocator());
              }
            }
            throw f;
          }
        }
        if (e != null) {
           throw e;
        }
      }
      if (o == null) {
        throw new SAXParseException("Method " + methodName + " of class " +
                                     factory.getClass().getName() +
                                     " did not return an object.",
                                     getDocumentLocator());
      }
    }

    if (currentBean == null) {
      resultBean = o;
    } else {
      beans.add(currentBean);
      names.add(currentName);
    }
    currentBean = o;
    currentName = pQName;

    if (pAttr != null) {
      for (int i = 0;  i < pAttr.getLength();  i++) {
        String uri = pAttr.getURI(i);
        if (uri == null  ||  uri.length() == 0  ||  isNamespaceMatching(uri)) {
          String value = pAttr.getValue(i);
          String qName = pAttr.getQName(i);
          String setMethodName = getMethodNameFor("set", pAttr.getLocalName(i));
          Method[] methods = currentBean.getClass().getMethods();
          for (int j = 0;  j < methods.length;  j++) {
            Method method = methods[j];
            if (!setMethodName.equals(method.getName())) {
              continue;
            }
            Class[] argClasses = method.getParameterTypes();
            if (argClasses.length != 1) {
              continue;
            }
            Object[] args = null;
            if (argClasses[0] == String.class) {
              args = new Object[]{value};
            } else if (argClasses[0] == Integer.class  ||
                        argClasses[0] == Integer.TYPE) {
              try {
                args = new Object[]{new Integer(value)};
              } catch (Exception e) {
                throw new SAXParseException("Attribute " + qName +
                                             " contains an invalid Integer value: " +
                                             value, getDocumentLocator());
              }
            } else if (argClasses[0] == Long.class  ||
                        argClasses[0] == Long.TYPE) {
              try {
                args = new Object[]{new Long(value)};
              } catch (Exception e) {
                throw new SAXParseException("Attribute " + qName +
                                             " contains an invalid Long value: " +
                                             value, getDocumentLocator());
              }
            } else if (argClasses[0] == Short.class  ||
                        argClasses[0] == Short.TYPE) {
              try {
                args = new Object[]{new Short(value)};
              } catch (Exception e) {
                throw new SAXParseException("Attribute " + qName +
                                             " contains an invalid Short value: " +
                                             value, getDocumentLocator());
              }
            } else if (argClasses[0] == Byte.class  ||
                        argClasses[0] == Byte.TYPE) {
              try {
                args = new Object[]{new Byte(value)};
              } catch (Exception e) {
                throw new SAXParseException("Attribute " + qName +
                                             " contains an invalid Byte value: " +
                                             value, getDocumentLocator());
              }
            } else if (argClasses[0] == Boolean.class  ||
                        argClasses[0] == Boolean.TYPE) {
              try {
                args = new Object[]{Boolean.valueOf(value)};
              } catch (Exception e) {
                throw new SAXParseException("Attribute " + qName +
                                             " contains an invalid Boolean value: " +
                                             value, getDocumentLocator());
              }
            } else if (argClasses[0] == Character.class  ||
                        argClasses[0] == Character.TYPE) {
              if (value.length() != 1) {
                throw new SAXParseException("Attribute " + qName +
                                             " contains an invalid Character value: " +
                                             value, getDocumentLocator());
              }
              args = new Object[]{new Character(value.charAt(0))};
            } else if (argClasses[0] == Class.class) {
              Class c;
              try {
                c = ClassLoader.getClass(value);
              } catch (Exception e) {
                throw new SAXParseException("Failed to load class " + value,
                                             getDocumentLocator(), e);
              }
              args = new Object[]{c};
            } else if (argClasses[0] == QName.class) {
              try {
                QName name = QName.valueOf(value);
                args = new Object[]{name};
              } catch (Exception e) {
                throw new SAXParseException("Failed to parse QName " + value,
                                             getDocumentLocator());
              }
            } else {
              // Try a constructor class(String)
              try {
                Constructor con = argClasses[0].getConstructor(oneClassString);
                args = new Object[]{con.newInstance(new Object[]{value})};
              } catch (InvocationTargetException e) {
                Throwable t = e.getTargetException();
                throw new SAXParseException("Failed to invoke constructor of class " +
                                             argClasses[0].getClass().getName(),
                                             getDocumentLocator(),
                                             (t instanceof Exception) ? ((Exception) t) : e);
              } catch (NoSuchMethodException e) {
                throw new SAXParseException("Attribute " + qName +
                                             " has an invalid type: " +
                                             argClasses[0].getClass().getName(),
                                             getDocumentLocator());
              } catch (IllegalAccessException e) {
                throw new SAXParseException("Illegal access to constructor of class " +
                                             argClasses[0].getClass().getName(),
                                             getDocumentLocator(), e);
              } catch (InstantiationException e) {
                throw new SAXParseException("Failed to instantiate class " +
                                             argClasses[0].getClass().getName(),
                                             getDocumentLocator(), e);
              }
            }

            try {
              method.invoke(currentBean, args);
            } catch (IllegalAccessException e) {
              throw new SAXParseException("Illegal access to method " + setMethodName +
                                             " of class " + currentBean.getClass().getName(),
                                             getDocumentLocator(), e);
            } catch (InvocationTargetException e) {
              Throwable t = e.getTargetException();
              throw new SAXParseException("Failed to invoke method " + setMethodName +
                                           " of class " + currentBean.getClass().getName(),
                                           getDocumentLocator(),
                                           (t instanceof Exception) ? ((Exception) t) : e);
            }
          }
        }
      }
    }
  }

  protected Object invokeMethod(String pMethodName, Object pBean,
                                 Class[] pSignature, Object[] pArgs)
     throws SAXException {
    try {
      Method m = pBean.getClass().getMethod(pMethodName, pSignature);
      return m.invoke(pBean, pArgs);
    } catch (IllegalAccessException e) {
      throw new SAXParseException("Illegal access to method " + pMethodName +
                                   " of class " + pBean.getClass().getName(),
                                   getDocumentLocator(), e);
    } catch (NoSuchMethodException e) {
      throw new SAXParseException("No such method in class " +
                                   pBean.getClass().getName() + ": " + pMethodName,
                                   getDocumentLocator(), e);
    } catch (InvocationTargetException e) {
      Throwable t = e.getTargetException();
      throw new SAXParseException("Failed to invoke method " + pMethodName +
                                   " of class " + pBean.getClass().getName(),
                                   getDocumentLocator(),
                                   (t instanceof Exception) ? ((Exception) t) : e);
    }
  }

  /** <p>Terminates parsing the current bean by calling its
   * <code>finish()</code> method, if any.</p>
   */
  public void endElement(String namespaceURI, String qName, String localName)
      throws SAXException {
    if (ignoreLevel > 0) {
      --ignoreLevel;
      return;
    }

    Object previousBean = currentBean;
    if (beans.size() > 0) {
      currentBean = beans.remove(beans.size()-1);
      currentName = names.remove(names.size()-1).toString();
    } else {
      currentBean = null;
      currentName = null;
    }
    try {
      invokeMethod("finish", previousBean, zeroClasses, zeroObjects);
    } catch (SAXParseException e) {
      if (e.getException() == null  ||
          !(e.getException() instanceof NoSuchMethodException)) {
        throw e;
      }
    }
  }

  /** <p>Handles atomic child elements by invoking their method
   * <code>addText(String pText)</code>. Note that it may happen,
   * that this method is invoked multiple times, if the parser
   * splits a piece of text into multiple SAX events.</p>
   */
  public void characters(char[] ch, int start, int length)
      throws SAXException {
    if (ignoreLevel > 0) {
      return;
    }

    String s = new String(ch, start, length);
    try {
      invokeMethod("addText", currentBean, oneClassString, new String[]{s});
    } catch (SAXParseException e) {
      if (e.getException() != null  &&
          (e.getException() instanceof NoSuchMethodException)) {
        boolean allWhitespace = true;
        for (int i = 0;  i < length;  i++) {
          if (!Character.isWhitespace(ch[start+i])) {
            allWhitespace = false;
            break;
          }
        }
        if (allWhitespace) {
          // Ignore this
        } else {
          throw new SAXParseException("Element " + currentName +
                                       " doesn't support embedded text.",
                                       getDocumentLocator());
        }
      } else {
        throw e;
      }
    }      
  }

  public void ignorableWhitespace(char[] ch, int start, int length)
    throws SAXException {
  }

  public void processingInstruction(String target, String data)
    throws SAXException {
  }

  public void skippedEntity(String name) throws SAXException {
  }

  /** Returns the parsed result bean.
   */
  public Object getResult() { return resultBean; }

  public boolean processName(String pName, String[] parts) {
    int offset = pName.indexOf(':');
    if (offset == -1) {
      String uri = nss.getNamespaceURI("");
      if (uri == null) {
        parts[0] = "";
      } else {
        parts[0] = uri;
      }
      parts[1] = pName;
      parts[2] = pName;
      return true;
    } else {
      String uri = nss.getNamespaceURI(pName.substring(0, offset));
      if (uri == null) {
        return false;
      } else {
        parts[0] = uri;
        parts[1] = pName.substring(offset+1);
        parts[2] = pName;
        return true;
      }
    }
  }
}
