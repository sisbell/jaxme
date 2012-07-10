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
package org.apache.ws.jaxme.pm.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.xml.bind.Element;
import javax.xml.bind.JAXBException;

import org.apache.ws.jaxme.JMManager;
import org.apache.ws.jaxme.PMException;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public abstract class PMIdImpl extends PMImpl {
  private static final Class[] ZERO_CLASSES = new Class[0];
  private static final Object[] ZERO_OBJECTS = new Object[0];
  private String getIdMethodName;

  public void init(JMManager pManager) throws JAXBException {
    String methodName = pManager.getProperty("xmldb.getIdMethodName");
    if (methodName == null  ||  methodName.length() == 0) {
      throw new JAXBException("Missing property: 'xmldb.getIdMethodName' (method name for reading the object ID)");
    }
    setGetIdMethodName(methodName);
  }

  /** <p>Sets the name of the method fetching the object ID.</p>
   */
  public void setGetIdMethodName(String pProperty) {
    getIdMethodName = pProperty;
  }

  /** <p>Returns the name of the method fetching the object ID.</p>
   */
  public String getGetIdMethodName() {
    return getIdMethodName;
  }

  protected String getId(Element pElement)
      throws NoSuchMethodException, IllegalAccessException, InvocationTargetException,
              PMException {
    String getMethodName = getGetIdMethodName();
    Method m = pElement.getClass().getMethod(getMethodName, ZERO_CLASSES);
    Object o = m.invoke(pElement, ZERO_OBJECTS);
    if (o == null) {
      throw new PMException("The method " + getMethodName + " returned null, which is no valid ID.");
    }
    return o.toString();
  }
}
