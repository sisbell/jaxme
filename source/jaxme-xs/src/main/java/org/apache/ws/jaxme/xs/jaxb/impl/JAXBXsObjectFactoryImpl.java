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

import org.apache.ws.jaxme.xs.jaxb.*;
import org.apache.ws.jaxme.xs.xml.XsEAppinfo;
import org.apache.ws.jaxme.xs.xml.XsESchema;
import org.apache.ws.jaxme.xs.xml.XsObject;
import org.apache.ws.jaxme.xs.xml.impl.XsObjectFactoryImpl;

/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class JAXBXsObjectFactoryImpl extends XsObjectFactoryImpl implements JAXBXsObjectFactory {
  public JAXBProperty.BaseType newBaseType(XsObject pParent) {
    return new JAXBPropertyImpl.BaseTypeImpl(pParent);
  }
  public JAXBClass newJAXBClass(XsObject pParent) {
    return new JAXBClassImpl(pParent);
  }
  public JAXBGlobalBindings newJAXBGlobalBindings(XsObject pParent) {
    return new JAXBGlobalBindingsImpl(pParent);
  }
  public JAXBJavadoc newJAXBJavadoc(XsObject pParent) {
    return new JAXBJavadocImpl(pParent);
  }
  public JAXBJavaType newJAXBJavaType(XsObject pParent) {
    return new JAXBJavaTypeImpl(pParent);
  }
  public JAXBJavaType.JAXBGlobalJavaType newJAXBGlobalJavaType(XsObject pParent) {
    return new JAXBJavaTypeImpl.JAXBGlobalJavaTypeImpl(pParent);
  }
  public JAXBSchemaBindings.NameXmlTransform newNameXmlTransform(XsObject pParent) {
    return new JAXBSchemaBindingsImpl.NameXmlTransformImpl(pParent);
  }
  public JAXBSchemaBindings.NameTransformation newNameTransformation(XsObject pParent) {
    return new JAXBSchemaBindingsImpl.NameXmlTransformImpl.NameTransformationImpl(pParent);
  }
  public JAXBSchemaBindings.Package newPackage(XsObject pParent) {
    return new JAXBSchemaBindingsImpl.PackageImpl(pParent);
  }
  public JAXBProperty newJAXBProperty(XsObject pParent) {
    return new JAXBPropertyImpl(pParent);
  }
  public JAXBSchemaBindings newJAXBSchemaBindings(XsObject pParent) {
    return new JAXBSchemaBindingsImpl(pParent);
  }
  public JAXBTypesafeEnumClass newJAXBTypesafeEnumClass(XsObject pParent) {
    return new JAXBTypesafeEnumClassImpl(pParent);
  }
  public JAXBTypesafeEnumMember newJAXBTypesafeEnumMember(XsObject pParent) {
    return new JAXBTypesafeEnumMemberImpl(pParent);
  }
  public XsEAppinfo newXsEAppinfo(XsObject pParent) {
    return new JAXBAppinfoImpl(pParent);
  }
  public XsESchema newXsESchema() {
    return new JAXBXsSchemaImpl(getContext());
  }
}
