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
package org.apache.ws.jaxme.xs.jaxb.impl;

import org.apache.ws.jaxme.xs.jaxb.JAXBJavaType;
import org.apache.ws.jaxme.xs.jaxb.JAXBJavadoc;
import org.apache.ws.jaxme.xs.jaxb.JAXBProperty;
import org.apache.ws.jaxme.xs.jaxb.JAXBXsObjectFactory;
import org.apache.ws.jaxme.xs.xml.XsObject;


/** <p>Implementation of the Property interface.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 * @version $Id: JAXBPropertyImpl.java 231785 2004-02-16 23:39:59Z jochen $
 */
public class JAXBPropertyImpl extends JAXBXsObjectImpl implements JAXBProperty {
  public static class BaseTypeImpl extends JAXBXsObjectImpl implements JAXBProperty.BaseType {
    /** <p>Creates a new instance of JAXBPropertyImpl.</p>
     */
    protected BaseTypeImpl(XsObject pParent) {
      super(pParent);
    }

    private JAXBJavaType javaType;

    public JAXBJavaType createJavaType() {
      if (javaType != null) {
        throw new IllegalStateException("Only one instance of javaType supported.");
      }
      javaType = ((JAXBXsObjectFactory) getObjectFactory()).newJAXBJavaType(this);
      return javaType;
    }

    public JAXBJavaType getJavaType() {
      return javaType;
    }
  }

  private String name;
  private String collectionType;
  private Boolean fixedAttributeAsConstantProperty, generateIsSetMethod, enableFailFastCheck;
  private JAXBJavadoc javadoc;
  private BaseType baseType;

  /** <p>Creates a new instance of JAXBPropertyImpl.</p>
   */
  protected JAXBPropertyImpl(XsObject pParent) {
    super(pParent);
  }

  public void setName(String pName) {
    name = pName;
  }

  public String getName() {
    return name;
  }

  public void setCollectionType(String pType) throws ClassNotFoundException {
    collectionType = pType;
  }

  public String getCollectionType() { return collectionType; }

  public void setFixedAttributeAsConstantProperty(Boolean pFixedAttributeAsConstantProperty) {
    fixedAttributeAsConstantProperty = pFixedAttributeAsConstantProperty;
  }

  public Boolean isFixedAttributeAsConstantProperty() {
    return fixedAttributeAsConstantProperty;
  }

  public void setGenerateIsSetMethod(Boolean pGenerateIsSetMethod) {
    generateIsSetMethod = pGenerateIsSetMethod;
  }

  public Boolean isGenerateIsSetMethod() {
    return generateIsSetMethod;
  }

  public void setEnableFailFastCheck(Boolean pEnableFailFastCheck) {
    enableFailFastCheck = pEnableFailFastCheck;
  }

  public Boolean isEnableFailFastCheck() {
    return enableFailFastCheck;
  }

  public JAXBJavadoc createJavadoc() {
    if (javadoc != null) {
      throw new IllegalStateException("Multiple javadoc elements are not supported.");
    }
    javadoc = getJAXBXsObjectFactory().newJAXBJavadoc(this);
    return javadoc;
  }

  public JAXBJavadoc getJavadoc() {
    return javadoc;
  }

  public BaseType createBaseType() {
    if (baseType != null) {
      throw new IllegalStateException("Multiple instances of baseType are not supported.");
    }
    baseType = ((JAXBXsObjectFactory) getObjectFactory()).newBaseType(this);
    return baseType;
  }

  public BaseType getBaseType() {
    return baseType;
  }
}
