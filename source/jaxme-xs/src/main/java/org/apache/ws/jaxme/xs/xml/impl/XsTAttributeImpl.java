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

import org.apache.ws.jaxme.xs.parser.impl.LocSAXException;
import org.apache.ws.jaxme.xs.xml.*;
import org.xml.sax.SAXException;


/** <p>Implementation of <code>xs:attribute</code>, following
 * this specification:
 * <pre>
 *  &lt;xs:complexType name="attribute"&gt;
 *    &lt;xs:complexContent&gt;
 *      &lt;xs:extension base="xs:annotated"&gt;
 *        &lt;xs:sequence&gt;
 *          &lt;xs:element name="simpleType" minOccurs="0" type="xs:localSimpleType"/&gt;
 *        &lt;/xs:sequence&gt;
 *        &lt;xs:attributeGroup ref="xs:defRef"/&gt;
 *        &lt;xs:attribute name="type" type="xs:QName"/&gt;
 *        &lt;xs:attribute name="use" use="optional" default="optional"&gt;
 *          &lt;xs:simpleType&gt;
 *            &lt;xs:restriction base="xs:NMTOKEN"&gt;
 *              &lt;xs:enumeration value="prohibited"/&gt;
 *              &lt;xs:enumeration value="optional"/&gt;
 *              &lt;xs:enumeration value="required"/&gt;
 *            &lt;/xs:restriction&gt;
 *          &lt;/xs:simpleType&gt;
 *        &lt;/xs:attribute&gt;
 *        &lt;xs:attribute name="default" type="xs:string"/&gt;
 *        &lt;xs:attribute name="fixed" type="xs:string"/&gt;
 *        &lt;xs:attribute name="form" type="xs:formChoice"/&gt;
 *      &lt;/xs:extension&gt;
 *    &lt;/xs:complexContent&gt;
 *  &lt;/xs:complexType&gt;
 * </pre></p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XsTAttributeImpl extends XsTAnnotatedImpl implements XsTAttribute {
  private XsTLocalSimpleType simpleType;
  private XsQName type;
  private Use use = OPTIONAL;
  private String defaultValue;
  private String fixedValue;
  private XsFormChoice form;
  private XsNCName name;
  private XsQName ref;

  protected XsTAttributeImpl(XsObject pParent) {
    super(pParent);
  }

  public XsTLocalSimpleType createSimpleType() {
    if (simpleType != null) {
      throw new IllegalStateException("Multiple 'simpleType' child elements are forbidden.");
    }
    if (type != null) {
      throw new IllegalStateException("The 'type' attribute and the 'simpleType' child element are mutually exclusive.");
    }
    return simpleType = getObjectFactory(). newXsTLocalSimpleType(this);
  }

  public XsTLocalSimpleType getSimpleType() {
    return simpleType;
  }

  public void setType(XsQName pType) {
    if (simpleType != null) {
      throw new IllegalStateException("The 'type' attribute and the 'simpleType' child element are mutually exclusive.");
    }
    type = pType;
  }

  public void setType(String pType) throws SAXException {
    setType(asXsQName(pType));
  }

  public XsQName getType() {
    return type;
  }

  public void setUse(Use pUse) {
    use = pUse;
  }

  public Use getUse() {
    return use;
  }

  public void setDefault(String pDefault) {
    defaultValue = pDefault;
  }

  public String getDefault() {
    return defaultValue;
  }

  public void setFixed(String pFixed) {
    fixedValue = pFixed;
  }

  public String getFixed() {
    return fixedValue;
  }

  public void setForm(XsFormChoice pForm) throws SAXException {
    if (isGlobal()  &&  pForm != null) {
      throw new LocSAXException("The 'form' attribute is valid for local attributes only.",
                                 getLocator());
    }
    form = pForm;
  }

  public XsFormChoice getForm() {
    return form;
  }

  public void setName(XsNCName pName) {
    name = pName;
  }

  public XsNCName getName() {
    return name;
  }

  public void setRef(XsQName pRef) {
    ref = pRef;
  }

  public void setRef(String pRef) throws SAXException {
    setRef(asXsQName(pRef));
  }

  public XsQName getRef() {
    return ref;
  }

  public boolean isGlobal() { return isTopLevelObject(); }
}
