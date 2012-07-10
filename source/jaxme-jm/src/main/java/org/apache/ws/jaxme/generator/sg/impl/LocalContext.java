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

import org.apache.ws.jaxme.generator.sg.Context;
import org.apache.ws.jaxme.generator.sg.SchemaSG;
import org.apache.ws.jaxme.js.JavaQName;
import org.apache.ws.jaxme.js.JavaQNameImpl;
import org.apache.ws.jaxme.xs.XSObject;
import org.apache.ws.jaxme.xs.jaxb.JAXBClass;
import org.apache.ws.jaxme.xs.jaxb.JAXBClassOwner;
import org.xml.sax.SAXException;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class LocalContext extends AbstractContext {
  /** <p>Creates a new instance of LocalClassContext.java.</p>
   */
  public LocalContext(Context pContext, String pName, XSObject pJAXBClassOwner,
                       String pPrefix, String pSuffix, SchemaSG pSchemaSG) throws SAXException {
    if (pJAXBClassOwner == null) {
      throw new NullPointerException("The XSObject argument must not be null.");
    }

    JAXBClassOwner jaxbClassOwner = null;
    if (pJAXBClassOwner instanceof JAXBClassOwner) {
      jaxbClassOwner = (JAXBClassOwner) pJAXBClassOwner;
    }

    String className = null, implementationName = null;
    JAXBClass jaxbClass = (jaxbClassOwner == null) ? null : jaxbClassOwner.getJAXBClass();
    if (jaxbClass != null) {
      className = jaxbClass.getName();
      implementationName = jaxbClass.getImplClass();
    }

    if (className == null) {
      String prefix = null, suffix = null;
      className = getClassNameFromLocalName(pJAXBClassOwner.getLocator(), pName, pSchemaSG);
      if (prefix == null) { prefix = pPrefix; }
      if (prefix != null) className = prefix + className;
      if (suffix == null) { suffix = pSuffix; }
      if (suffix != null) className += suffix;
    }
    JavaQName xmlInterfaceName = JavaQNameImpl.getInnerInstance(pContext.getXMLInterfaceName(), className);
    setXMLInterfaceName(xmlInterfaceName);

    if (implementationName == null) {
      setXMLImplementationName(JavaQNameImpl.getInnerInstance(pContext.getXMLImplementationName(),
                                                              className + "Impl"));
    } else {
      setXMLImplementationName(JavaQNameImpl.getInstance(implementationName));
    }
    setXMLHandlerName(JavaQNameImpl.getInnerInstance(pContext.getXMLHandlerName(), className + "Handler"));
    setXMLSerializerName(JavaQNameImpl.getInnerInstance(pContext.getXMLSerializerName(), className + "Driver"));
    setXMLValidatorName(JavaQNameImpl.getInstance(xmlInterfaceName.getPackageName() + ".impl",
                                                  xmlInterfaceName.getClassName() + "Validator"));
    setPMName(JavaQNameImpl.getInstance(xmlInterfaceName.getPackageName() + ".impl",
                                        xmlInterfaceName.getClassName() + "PM"));
  }

  public boolean isGlobal() { return false; }
}
