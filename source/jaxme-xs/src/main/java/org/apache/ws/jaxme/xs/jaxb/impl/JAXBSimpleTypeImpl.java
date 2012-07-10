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

import org.apache.ws.jaxme.xs.XSAtomicType;
import org.apache.ws.jaxme.xs.XSEnumeration;
import org.apache.ws.jaxme.xs.XSListType;
import org.apache.ws.jaxme.xs.XSSimpleType;
import org.apache.ws.jaxme.xs.XSType;
import org.apache.ws.jaxme.xs.XSUnionType;
import org.apache.ws.jaxme.xs.impl.XSUtil;
import org.apache.ws.jaxme.xs.jaxb.JAXBJavaType;
import org.apache.ws.jaxme.xs.jaxb.JAXBSimpleType;
import org.apache.ws.jaxme.xs.jaxb.JAXBTypesafeEnumClass;
import org.xml.sax.SAXException;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class JAXBSimpleTypeImpl implements JAXBSimpleType {
  private final XSSimpleType baseType;
  private final JAXBJavaType jaxbJavaType;
  private final JAXBTypesafeEnumClass jaxbTypesafeEnumClass;

  /** <p>Creates a new instance of JAXBSimpleTypeImpl.</p>
   */
  protected JAXBSimpleTypeImpl(XSType pMyType, XSSimpleType pBaseType) throws SAXException {
    jaxbJavaType = (JAXBJavaType) XSUtil.getSingleAppinfo(pMyType.getAnnotations(), JAXBJavaType.class);
    jaxbTypesafeEnumClass = (JAXBTypesafeEnumClass) XSUtil.getSingleAppinfo(pMyType.getAnnotations(), JAXBTypesafeEnumClass.class);
    baseType = pBaseType;
  }

  public JAXBJavaType getJAXBJavaType() { return jaxbJavaType; }
  public JAXBTypesafeEnumClass getJAXBTypesafeEnumClass() { return jaxbTypesafeEnumClass; }
  public boolean isAtomic() { return baseType.isAtomic(); }
  public XSAtomicType getAtomicType() { return baseType.getAtomicType(); }
  public boolean isList() { return baseType.isList(); }
  public XSListType getListType() { return baseType.getListType(); }
  public boolean isUnion() { return baseType.isUnion(); }
  public XSUnionType getUnionType() { return baseType.getUnionType(); }
  public String[][] getPattern() { return baseType.getPattern(); }
  public XSEnumeration[] getEnumerations() { return baseType.getEnumerations(); }
  public boolean isRestriction() { return baseType.isRestriction(); }
  public XSType getRestrictedType() { return baseType.getRestrictedType(); }
}
