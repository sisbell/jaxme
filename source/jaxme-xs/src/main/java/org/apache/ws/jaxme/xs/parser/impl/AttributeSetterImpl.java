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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.UndeclaredThrowableException;

import org.apache.ws.jaxme.xs.XSParser;
import org.apache.ws.jaxme.xs.parser.*;
import org.xml.sax.SAXException;


/** <p>Default implementation of the {@link org.apache.ws.jaxme.xs.parser.AttributeSetter}
 * interface.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class AttributeSetterImpl implements AttributeSetter {
  static final Class[] ONE_STRING_CLASS = new Class[]{String.class};
  private static final Class[] FOUR_STRING_CLASSES =
    new Class[]{String.class, String.class, String.class, String.class};

  protected XSContext getData() {
    XSContext result = XSParser.getRunningInstance().getContext();
    if (result == null) {
      throw new IllegalStateException("Parser data is not set.");
    }
    return result;
  }

  /** <p>This method configures the bean <code>pBean</code> as follows:
   * <ol>
   *    <li>If the bean has a method
   *      <code>setAttribute(String, String, String)</code>, it is invoked
   *      with the following arguments:
   *      <ul>
   *        <li>The attributes namespace URI (empty string for the default
   *          namespace),</li>
   *        <li>the attributes local name,</li>
   *        <li>and the property value</li>
   *      </ul>
   *    </li>
   *    <li>Otherwise invokes its own method {@link #setProperty(Object, String, String, String)}.</li>
   * </ol>
   */
  public void setAttribute(String pQName, String pNamespaceURI, String pLocalName, String pValue)
      throws SAXException {
    XsSAXParser handler = ((XsSAXParser) getData().getCurrentContentHandler());
    if (handler == null) {
      throw new IllegalStateException("Current XsSAXParser is null.");
    }
    Object bean = ((XsSAXParser) getData().getCurrentContentHandler()).getBean();
    try {
      Method m = bean.getClass().getMethod("setAttribute", FOUR_STRING_CLASSES);
      if (Modifier.isPublic(m.getModifiers())) {
        Object[] o = new Object[]{pQName, pNamespaceURI, pLocalName, pValue};
        Object result = invokeMethod(bean, m, pQName, o);
        if (!boolean.class.equals(m.getReturnType())  ||  ((Boolean) result).booleanValue()) {
          return;
        }
      }
    } catch (NoSuchMethodException e) {
    }

    if (!setProperty(bean, pQName, pLocalName, pValue)) {
      throw new IllegalStateException("Unknown attribute of " + bean.getClass().getName() + ": " + pQName);
    }
  }


  /** <p>This method invokes the beans <code>pBean</code> method <code>pMethod</code>,
   * setting the attribute <code>pName</code> to the value <code>pArgs</code>.</p>
   */
  protected Object invokeMethod(Object pBean, Method pMethod, String pName, Object[] pArgs) throws SAXException {
    try {
      return pMethod.invoke(pBean, pArgs);
    } catch (InvocationTargetException e) {
      Throwable t = e.getTargetException();
      if (t instanceof SAXException) {
        throw (SAXException) t;
      } else if (t instanceof RuntimeException) {
        throw (RuntimeException) t;
      } else {
        throw new UndeclaredThrowableException(t);
      }
    } catch (IllegalAccessException e) {
      StringBuffer sb = new StringBuffer("Failed to invoke method ");
      sb.append(pMethod.getName()).append(" of class ").append(pBean.getClass().getName());
      sb.append(" with argument ");
      for (int i = 0;  i < pArgs.length;  i++) {
        if (i > 0) {
          sb.append(", ");
        }
        sb.append(pArgs[i]);
      }
      sb.append(": ").append(e.getClass().getName()).append(", ").append(e.getMessage());
      throw new IllegalStateException(sb.toString());
    }
  }

  private interface ParameterClass {
    public Object matches(Class pClass);
    public void invoke(AttributeSetterImpl pAttributeSetter, Object pBean, String pValue,
                        Method pMethod, Object pMethodObject, String pQName)
      throws SAXException;
  }

  private static class StringClass implements ParameterClass {
    public Object matches(Class pClass) {
      return String.class.equals(pClass) ? Boolean.TRUE : null;
    }
    public void invoke(AttributeSetterImpl pAttributeSetter, Object pBean, String pValue,
                        Method pMethod, Object pMethodObject, String pQName) throws SAXException {
      pAttributeSetter.invokeMethod(pBean, pMethod, pQName, new Object[]{pValue});
    }
  }

  private static class ValueOfParameterClass implements ParameterClass {
    public Object matches(Class pClass) {
      try {
        Method valueOfMethod = pClass.getMethod("valueOf", ONE_STRING_CLASS);
        if (Modifier.isPublic(valueOfMethod.getModifiers())  &&  !void.class.equals(valueOfMethod.getReturnType())) {
          return valueOfMethod;
        }
      } catch (NoSuchMethodException e) {
      }
      return null;
    }
    public void invoke(AttributeSetterImpl pAttributeSetter, Object pBean, String pValue,
                        Method pMethod, Object pMethodObject, String pQName) throws SAXException {
      Method m = (Method) pMethodObject; 
      Object o;
      try {
        o = m.invoke(null, new Object[]{pValue});
      } catch (InvocationTargetException e) {
        throw new IllegalArgumentException("Illegal argument for attribute '" + pQName + "': " + pValue +
                                            "; " + e.getTargetException().getClass().getName() +
                                            ", " + e.getTargetException().getMessage());
      } catch (IllegalAccessException e) {
        throw new IllegalStateException("Invalid access to method " + m.getName() + " of class " +
                                         pBean.getClass() + ": IllegalAccessException, " + e.getMessage());
      }
      pAttributeSetter.invokeMethod(pBean, pMethod, pQName, new Object[]{o});
    }
  }

  private static class StringConstructorClass implements ParameterClass {
    public Object matches(Class pClass) {
      try {
        Constructor con = pClass.getConstructor(ONE_STRING_CLASS);
        if (Modifier.isPublic(con.getModifiers())) {
          return con;
        }
      } catch (NoSuchMethodException e) {
      }
      return null;
    }
    public void invoke(AttributeSetterImpl pAttributeSetter, Object pBean, String pValue,
                        Method pMethod, Object pMethodObject, String pQName) throws SAXException {
      Constructor con = (Constructor) pMethodObject; 
      Object o;
      try {
        o = con.newInstance(new Object[]{pValue});
      } catch (InvocationTargetException e) {
        throw new IllegalArgumentException("Illegal argument for attribute '" + pQName + "': " + pValue +
                                            "; " + e.getTargetException().getClass().getName() +
                                            ", " + e.getTargetException().getMessage());
      } catch (InstantiationException e) {
        throw new IllegalStateException("Invalid access to constructor " + pBean.getClass().getName() +
                                         "(): " + e.getClass().getName() + ", " + e.getMessage());
      } catch (IllegalAccessException e) {
        throw new IllegalStateException("Invalid access to constructor " + pBean.getClass().getName() +
                                         "(): " + e.getClass().getName() + ", " + e.getMessage());
      }
      pAttributeSetter.invokeMethod(pBean, pMethod, pQName, new Object[]{o});
    }
  }

  private static class PrimitiveParameterClass extends StringConstructorClass {
    private final Class primitiveClass;
    private final Class nonPrimitiveClass;
    private final Constructor stringConstructor;
    private PrimitiveParameterClass(Class pPrimitiveClass, Class pNonPrimitiveClass) {
      primitiveClass = pPrimitiveClass;
      nonPrimitiveClass = pNonPrimitiveClass;
      try {
        stringConstructor = pNonPrimitiveClass.getConstructor(ONE_STRING_CLASS);
      } catch (NoSuchMethodException e) {
        throw new IllegalStateException("The primitive class " + pNonPrimitiveClass.getName() +
                                         " doesn't have a string valued constructor!");
      }
    }
    public Object matches(Class pClass) {
      return (primitiveClass.equals(pClass) || nonPrimitiveClass.equals(pClass)) ? stringConstructor : null;
    }
  }

  private static class CharacterClass implements ParameterClass {
    public Object matches(Class pClass) {
      return (Character.TYPE.equals(pClass)  ||  Character.class.equals(pClass)) ? Boolean.TRUE : null;
    }

    public void invoke(AttributeSetterImpl pAttributeSetter, Object pBean, String pValue, Method pMethod, Object pMethodObject, String pQName) throws SAXException {
      if (pValue.length() != 1) {
        throw new IllegalArgumentException("Invalid value for '" + pQName +"': " + pValue +
                                            "; must have exactly one character.");
      }
      pAttributeSetter.invokeMethod(pBean, pMethod, pQName, new Object[]{new Character(pValue.charAt(0))});
    }
  }

  private static class BooleanClass implements ParameterClass {
    public Object matches(Class pClass) {
      return (Boolean.TYPE.equals(pClass)  ||  Boolean.class.equals(pClass)) ? Boolean.TRUE : null;
    }

    public void invoke(AttributeSetterImpl pAttributeSetter, Object pBean, String pValue, Method pMethod, Object pMethodObject, String pQName) throws SAXException {
      Boolean b = ("true".equals(pValue) || "1".equals(pValue)) ? Boolean.TRUE : Boolean.FALSE;
      pAttributeSetter.invokeMethod(pBean, pMethod, pQName, new Object[]{b});
    }
  }

  private static final ParameterClass[] knownClasses = new ParameterClass[]{
    new BooleanClass(),
    new StringClass(),
    new ValueOfParameterClass(),
    new StringConstructorClass(),
    new CharacterClass(),
    new PrimitiveParameterClass(long.class, Long.class),
    new PrimitiveParameterClass(int.class, Integer.class),
    new PrimitiveParameterClass(short.class, Short.class),
    new PrimitiveParameterClass(byte.class, Byte.class),
    new PrimitiveParameterClass(double.class, Double.class),
    new PrimitiveParameterClass(float.class, Float.class),
    new CharacterClass(),
  };

  /** <p>This method is invoked from within {@link #setAttribute(String, String, String, String)}.
   * It configures the bean <code>pBean</code> as follows;
   * <ol>
   *    <li>If the bean has a method <code>setProperty(String)</code>
   *      this method is invoked with the attribute value.</li>
   *   <li>If the bean has a method <code>setProperty(T)</code>, and
   *     the class <code>T</code> has either of a method
   *     <code>public static T valueOf(String)</code> or a constructor
   *     <code>public T(String)</code> (in that order), then the method
   *     <code>setProperty(T)</code> is invoked with the value obtained
   *     by an invocation of the method <code>valueOf()</code>, or
   *     the constructor, respectively. Note, that this applies in
   *     particular to the classes {@link Long}, {@link Integer},
   *     {@link Short}, {@link Byte}, {@link Double}, {@link Float},
   *     <code>java.math.BigInteger</code>, <code>java.math.BigDecimal</code>,
   *     {@link java.io.File}, and {@link java.lang.StringBuffer}.</li>
   *    <li>If the bean has a method <code>setProperty(boolean)</code>,
   *      the method will be invoked with the value <i>true</i>
   *      (the value specified in the XML file is either of
   *      <code>true</code>, or <code>1</code>, otherwise with the
   *      value <code>false</code>.</li>
   *   <li>If the bean has a method <code>setProperty(char)</code>,
   *     or <code>setProperty(Character)</code>, the method will be
   *     invoked with the first character of the value specified in
   *     the XML file. If the value contains zero or multiple characters,
   *     an {@link IllegalArgumentException} is thrown.</li>
   *   <li>If the bean has either of the following methods, in that order:
   *     <ul>
   *       <li><code>setProperty(long)</code></li>
   *       <li><code>setProperty(int)</code></li>
   *       <li><code>setProperty(short)</code></li>
   *       <li><code>setProperty(byte)</code></li>
   *       <li><code>setProperty(double)</code></li>
   *       <li><code>setProperty(float)</code></li>
   *     </ul>
   *     then the property value is converted into the respective type
   *     and the method is invoked. An {@link IllegalArgumentException}
   *     is thrown, if the conversion fails.</li>
   *   <li>If the bean has a method <code>java.lang.Class</code>, the
   *     <code>XsSAXParser</code> will interpret the value given in the
   *     XML file as a Java class name and load the named class from its
   *     class loader. If the class cannot be loaded, it will also try
   *     to use the current threads context class loader. An
   *     exception is thrown, if neither of the class loaders can
   *     load the class.</li>
   * </ol>
   * </p>
   *
   * @return True, if a method for setting the property was found. Otherwise
   *   false.
   */
  protected boolean setProperty(Object pBean, String pQName, String pName, String pValue)
      throws SAXException {
    Class c = pBean.getClass();
    String s = "set" + Character.toUpperCase(pName.charAt(0)) + pName.substring(1);
    int parameterClassNum = knownClasses.length;
    Method[] methods = c.getMethods();
    Method method = null;
    Object methodObject = null;
    for (int i = 0;  i < methods.length;  i++) {
      Method m = methods[i];
      if (!s.equals(m.getName())  ||  !Modifier.isPublic(m.getModifiers())) {
        continue;
      }

      Class[] params = m.getParameterTypes();
      if (params.length != 1) {
        continue;
      }

      Class paramsClass = params[0];
      for (int j = 0;  j < parameterClassNum;  j++) {
        ParameterClass parameterClass = knownClasses[j];
        Object o = parameterClass.matches(paramsClass);
        if (o != null) {
          parameterClassNum = j;
          method = m;
          methodObject = o;
          break;
        }
      }
    }

    if (method == null) {
      return false;
    } else {
      knownClasses[parameterClassNum].invoke(this, pBean, pValue, method, methodObject, pQName);
      return true;
    }
  }
}
