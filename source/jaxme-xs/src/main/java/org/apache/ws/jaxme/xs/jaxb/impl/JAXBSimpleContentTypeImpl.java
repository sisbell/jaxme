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

import org.apache.ws.jaxme.xs.XSType;
import org.apache.ws.jaxme.xs.impl.XSSimpleContentTypeImpl;
import org.apache.ws.jaxme.xs.impl.XSUtil;
import org.apache.ws.jaxme.xs.jaxb.JAXBProperty;
import org.apache.ws.jaxme.xs.jaxb.JAXBSchemaBindings;
import org.apache.ws.jaxme.xs.jaxb.JAXBSimpleContentType;
import org.apache.ws.jaxme.xs.xml.XsTComplexType;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class JAXBSimpleContentTypeImpl extends XSSimpleContentTypeImpl implements JAXBSimpleContentType {
  private final JAXBProperty jaxbProperty;

  /** <p>Creates a new instance of JAXBSimpleContentTypeImpl.</p>
   */
  protected JAXBSimpleContentTypeImpl(XSType pComplexType, XSType pSimpleType,
                                       XsTComplexType pBaseType) throws SAXException {
    super(pComplexType, pSimpleType, pBaseType);
    jaxbProperty = (JAXBProperty) XSUtil.getSingleAppinfo(pComplexType.getAnnotations(), JAXBProperty.class);
  }

  public JAXBSchemaBindings getJAXBSchemaBindings() {
    return ((JAXBXsSchemaImpl) getBaseType().getXsESchema()).getJAXBSchemaBindings();
  }
  public JAXBProperty getJAXBProperty() { return jaxbProperty; }
}
