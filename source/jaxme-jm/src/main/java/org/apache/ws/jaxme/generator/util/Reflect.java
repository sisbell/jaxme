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
package org.apache.ws.jaxme.generator.util;



/** <p>A set of utility methods for using Java reflection.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann
 */
public class Reflect {
  /** <p>Assigns the value <code>pValue</code> to the beans
   * <code>pBean</code> property <code>pPropertyName</code>.
   * For example, if the property name is <samp>type</samp>
   * and the String pValue is <samp>2</samp>, calls
   * <pre>
   *   pBean.setType("2");
   * </pre>
   * or
   * <pre>
   *   pBean.setType(2);
   * </pre>
   * if the respective method exists. Does nothing, if no
   * matching method can be found.</p>
   */
  public static void assignBeanValue(Object pBean,
                                       String pPropertyName,
                                       String pValue)
      throws IllegalAccessException, java.lang.reflect.InvocationTargetException {
    assignBeanValue(pBean, pPropertyName, pValue, null);
  }

  /** <p>Assigns the value <code>pValue</code> to the beans
   * <code>pBean</code> property <code>pPropertyName</code>.
   * For example, if the property name is <samp>type</samp>
   * and the String pValue is <samp>2</samp>, calls
   * <pre>
   *   pBean.setType("2");
   * </pre>
   * or
   * <pre>
   *   pBean.setType(2);
   * </pre>
   * if the respective method exists. Does nothing, if no
   * matching method can be found.</p>
   */
  public static void assignBeanValue(Object pBean,
                                       String pPropertyName,
                                       String pValue,
                                       ReflectResolver pResolver)
      throws IllegalAccessException, java.lang.reflect.InvocationTargetException {
    Class c = pBean.getClass();
    java.lang.reflect.Method[] methods = c.getMethods();
    String methodName = "set" + Character.toUpperCase(pPropertyName.charAt(0)) +
      pPropertyName.substring(1);
    for (int i = 0;  i < methods.length;  i++) {
      java.lang.reflect.Method m = methods[i];
      if (methodName.equals(m.getName())) {
        Class[] parameterTypes = m.getParameterTypes();
        if (parameterTypes.length == 1) {
          Class parameterClass = parameterTypes[0];
          Object[] parameters = null;
          if (pResolver != null) {
            parameters = pResolver.resolve(parameterClass, pValue);
          }
          if (parameters == null) {
            if (parameterClass == String.class) {
              parameters = new Object[]{pValue};
            } else if (parameterClass == Long.class  ||
                        parameterClass == long.class) {
              parameters = new Object[]{new Long(pValue)};
            } else if (parameterClass == Integer.class  ||
                        parameterClass == int.class) {
              parameters = new Object[]{new Integer(pValue)};
            } else if (parameterClass == Short.class  ||
                        parameterClass == short.class) {
              parameters = new Object[]{new Short(pValue)};
            } else if (parameterClass == Byte.class  ||
                        parameterClass == byte.class) {
              parameters = new Object[]{new Byte(pValue)};
            } else if (parameterClass == Double.class  ||
                        parameterClass == double.class) {
              parameters = new Object[]{new Double(pValue)};
            } else if (parameterClass == Float.class  ||
                        parameterClass == float.class) {
              parameters = new Object[]{new Float(pValue)};
            } else if (parameterClass == Boolean.class  ||
                        parameterClass == boolean.class) {
              parameters = new Object[]{new Boolean(pValue)};
            }
          }
          if (parameters != null) {
            m.invoke(pBean, parameters);
            return;
          }
        }
      }
    }
  }
}
