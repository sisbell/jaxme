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
import org.apache.ws.jaxme.xs.parser.impl.LocSAXException;
import org.apache.ws.jaxme.xs.xml.XsEAnnotation;
import org.apache.ws.jaxme.xs.xml.XsObject;
import org.apache.ws.jaxme.xs.xml.XsTSimpleType;
import org.apache.ws.jaxme.xs.xml.impl.XsEAppinfoImpl;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class JAXBAppinfoImpl extends XsEAppinfoImpl {
  /** <p>Creates a new instance of JAXBAppinfo.</p>
   */
  public JAXBAppinfoImpl(XsObject pParent) {
    super(pParent);
  }

  protected JAXBXsObjectFactory getJAXBXsObjectFactory() { return (JAXBXsObjectFactory) getObjectFactory(); }

  /** <p>Creates a new instance of {@link JAXBClass}.</p>
   */
  public JAXBClass createClass() {
    JAXBClass jaxbClass = getJAXBXsObjectFactory().newJAXBClass(this);
    addChild(jaxbClass);
    return jaxbClass;
  }

  /** <p>Creates a new instance of {@link JAXBGlobalBindings}.</p>
   */
  public JAXBGlobalBindings createGlobalBindings() {
    JAXBGlobalBindings jaxbGlobalBindings = getJAXBXsObjectFactory().newJAXBGlobalBindings(this);
    addChild(jaxbGlobalBindings);
    return jaxbGlobalBindings;
  }

  /** <p>Creates a new instance of {@link JAXBJavadoc}.</p>
   */
  public JAXBJavadoc createJavadoc() {
    JAXBJavadoc jaxbJavadoc = getJAXBXsObjectFactory().newJAXBJavadoc(this);
    addChild(jaxbJavadoc);
    return jaxbJavadoc;
  }

  /** <p>Creates a new instance of {@link JAXBJavaType}.</p>
   */
  public JAXBJavaType createJavaType() {
    JAXBJavaType jaxbJavaType = getJAXBXsObjectFactory().newJAXBJavaType(this);
    addChild(jaxbJavaType);
    return jaxbJavaType;
  }

  /** <p>Creates a new instance of {@link JAXBProperty}.</p>
   */
  public JAXBProperty createProperty() {
    JAXBProperty jaxbProperty = getJAXBXsObjectFactory().newJAXBProperty(this);
    addChild(jaxbProperty);
    return jaxbProperty;
  }

  /** <p>Creates a new instance of {@link JAXBSchemaBindings}.</p>
   */
  public JAXBSchemaBindings createSchemaBindings() {
    JAXBSchemaBindings jaxbSchemaBindings = getJAXBXsObjectFactory().newJAXBSchemaBindings(this);
    addChild(jaxbSchemaBindings);
    return jaxbSchemaBindings;
  }

  /** <p>Creates a new instance of {@link JAXBTypesafeEnumClass}.</p>
   */
  public JAXBTypesafeEnumClass createTypesafeEnumClass() throws SAXException {
    XsObject annotationParent = getParentObject();
    if (annotationParent instanceof XsEAnnotation) {
      XsObject simpleTypeParent = annotationParent.getParentObject();
      if (simpleTypeParent instanceof XsTSimpleType) {
        JAXBTypesafeEnumClass jaxbTypesafeEnumClass = getJAXBXsObjectFactory().newJAXBTypesafeEnumClass(this);
        addChild(jaxbTypesafeEnumClass);
        return jaxbTypesafeEnumClass;
      }
    }
    throw new LocSAXException("The declaration of a typesafeEnumClass is only allowed in xs:simpleType/xs:annotation/xs:appinfo.", getLocator());
  }

  /** <p>Creates a new instance of {@link JAXBTypesafeEnumMember}.</p>
   */
  public JAXBTypesafeEnumMember createTypesafeEnumMember() {
    JAXBTypesafeEnumMember jaxbTypesafeEnumMember = getJAXBXsObjectFactory().newJAXBTypesafeEnumMember(this);
    addChild(jaxbTypesafeEnumMember);
    return jaxbTypesafeEnumMember;
  }

  public ContentHandler getChildHandler(String pQName, String pNamespaceURI, String pLocalName) throws SAXException {
    if (JAXBParser.JAXB_SCHEMA_URI.equals(pNamespaceURI)) {
      return null;
    } else {
      return super.getChildHandler(pQName, pNamespaceURI, pLocalName);
    }
  }
}
