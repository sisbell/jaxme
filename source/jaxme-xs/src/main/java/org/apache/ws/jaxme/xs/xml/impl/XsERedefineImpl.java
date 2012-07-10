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
package org.apache.ws.jaxme.xs.xml.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.ws.jaxme.xs.parser.impl.LocSAXException;
import org.apache.ws.jaxme.xs.xml.*;
import org.xml.sax.SAXException;


/** <p>Implementation of <code>xs:redefine</code>, as specified
 * by the following:
 * <pre>
 *  &lt;xs:element name="redefine" id="redefine"&gt;
 *    &lt;xs:annotation&gt;
 *      &lt;xs:documentation source="http://www.w3.org/TR/xmlschema-1/#element-redefine"/&gt;
 *    &lt;/xs:annotation&gt;
 *    &lt;xs:complexType&gt;
 *      &lt;xs:complexContent&gt;
 *        &lt;xs:extension base="xs:openAttrs"&gt;
 *          &lt;xs:choice minOccurs="0" maxOccurs="unbounded"&gt;
 *            &lt;xs:element ref="xs:annotation"/&gt;
 *            &lt;xs:group ref="xs:redefinable"/&gt;
 *          &lt;/xs:choice&gt;
 *          &lt;xs:attribute name="schemaLocation" type="xs:anyURI" use="required"/&gt;
 *          &lt;xs:attribute name="id" type="xs:ID"/&gt;
 *        &lt;/xs:extension&gt;
 *      &lt;/xs:complexContent&gt;
 *    &lt;/xs:complexType&gt;
 *  &lt;/xs:element&gt;
 * </pre></p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XsERedefineImpl extends XsTAnnotatedImpl implements XsERedefine {
  protected XsERedefineImpl(XsObject pParent) {
    super(pParent);
  }

  private List childs;
  private XsAnyURI schemaLocation;

  public void setSchemaLocation(XsAnyURI pSchemaLocation) {
    schemaLocation = pSchemaLocation;
  }

  public XsAnyURI getSchemaLocation() {
    return schemaLocation;
  }

  public XsEAnnotation createAnnotation() {
    XsEAnnotation annotation = getObjectFactory().newXsEAnnotation(this);
    if (childs == null) {
      childs = new ArrayList();
    }
    childs.add(annotation);
    return annotation;
  }

  public XsETopLevelSimpleType createSimpleType() {
    XsETopLevelSimpleType simpleType = getObjectFactory().newXsETopLevelSimpleType(this);
    if (childs == null) {
      childs = new ArrayList();
    }
    childs.add(simpleType);
    return simpleType;
  }

  public XsTComplexType createComplexType() {
    XsTComplexType complexType = getObjectFactory().newXsTComplexType(this);
    if (childs == null) {
      childs = new ArrayList();
    }
    childs.add(complexType);
    return complexType;
  }

  public XsTGroup createGroup() {
    XsTGroup group = getObjectFactory().newXsTGroup(this);
    if (childs == null)  {
      childs = new ArrayList();
    }
    childs.add(group);
    return group;
  }

  public XsTAttributeGroup createAttributeGroup() {
    XsTAttributeGroup attributeGroup = getObjectFactory().newXsTAttributeGroup(this);
    if (childs == null) {
      childs = new ArrayList();
    }
    childs.add(attributeGroup);
    return attributeGroup;
  }

  public Object[] getChilds() {
    if (childs == null) {
      return new Object[0];
    }
    return childs.toArray();
  }

  public void validate() throws SAXException {
    super.validate();
    if (getSchemaLocation() == null) {
      throw new LocSAXException("Missing attribute: 'schemaLocation'", getLocator());
    }
  }
}
