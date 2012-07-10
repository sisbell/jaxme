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

import org.apache.ws.jaxme.xs.XSObject;
import org.apache.ws.jaxme.xs.impl.XSTypeImpl;
import org.apache.ws.jaxme.xs.impl.XSUtil;
import org.apache.ws.jaxme.xs.jaxb.JAXBClass;
import org.apache.ws.jaxme.xs.jaxb.JAXBJavaType;
import org.apache.ws.jaxme.xs.jaxb.JAXBSchemaBindings;
import org.apache.ws.jaxme.xs.jaxb.JAXBType;
import org.apache.ws.jaxme.xs.xml.XsETopLevelSimpleType;
import org.apache.ws.jaxme.xs.xml.XsTComplexType;
import org.apache.ws.jaxme.xs.xml.XsTLocalComplexType;
import org.apache.ws.jaxme.xs.xml.XsTLocalSimpleType;
import org.apache.ws.jaxme.xs.xml.XsTSimpleRestrictionType;
import org.xml.sax.SAXException;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class JAXBTypeImpl extends XSTypeImpl implements JAXBType {
  private JAXBClass jaxbClass;
  private JAXBJavaType jaxbJavaType;

  /** <p>Creates a new instance of JAXBTypeImpl.</p>
   */
  protected JAXBTypeImpl(XSObject pParent, XsETopLevelSimpleType pSimpleType) throws SAXException {
    super(pParent, pSimpleType);
  }

  /** <p>Creates a new instance of JAXBTypeImpl.</p>
   */
  protected JAXBTypeImpl(XSObject pParent, XsTLocalSimpleType pSimpleType) throws SAXException {
    super(pParent, pSimpleType);
  }

  /** <p>Creates a new instance of JAXBTypeImpl.</p>
   */
  protected JAXBTypeImpl(XSObject pParent, XsTSimpleRestrictionType pSimpleType) throws SAXException {
    super(pParent, pSimpleType);
  }

  /** <p>Creates a new instance of JAXBTypeImpl.</p>
   */
  protected JAXBTypeImpl(XSObject pParent, XsTComplexType pComplexType) throws SAXException {
    super(pParent, pComplexType);
  }

  /** <p>Creates a new instance of JAXBTypeImpl.</p>
   */
  protected JAXBTypeImpl(XSObject pParent, XsTLocalComplexType pComplexType) throws SAXException {
    super(pParent, pComplexType);
  }

  public JAXBSchemaBindings getJAXBSchemaBindings() {
    return ((JAXBXsSchemaImpl) getXsObject().getXsESchema()).getJAXBSchemaBindings();
  }

  public JAXBClass getJAXBClass() {
    return jaxbClass;
  }

  public JAXBJavaType getJAXBJavaType() {
  	return jaxbJavaType;
  }

  public void validate() throws SAXException {
    if (isValidated()) {
      return;
    }
    super.validate();
    jaxbClass = (JAXBClass) XSUtil.getSingleAppinfo(getAnnotations(), JAXBClass.class);
    jaxbJavaType = (JAXBJavaType) XSUtil.getSingleAppinfo(getAnnotations(), JAXBJavaType.class);
  }
}
