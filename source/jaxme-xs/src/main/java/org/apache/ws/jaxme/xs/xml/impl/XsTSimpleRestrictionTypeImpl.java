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

import org.apache.ws.jaxme.xs.xml.*;
import org.xml.sax.SAXException;


/** <p>Implementation of <code>xs:simpleRestrictionType</code>,
 * as specified by the following:
 * <pre>
 *  &lt;xs:complexType name="simpleRestrictionType"&gt;
 *    &lt;xs:complexContent&gt;
 *      &lt;xs:restriction base="xs:restrictionType"&gt;
 *        &lt;xs:sequence&gt;
 *          &lt;xs:element ref="xs:annotation" minOccurs="0"/&gt;
 *          &lt;xs:group ref="xs:simpleRestrictionModel" minOccurs="0"/&gt;
 *          &lt;xs:group ref="xs:attrDecls"/&gt;
 *        &lt;/xs:sequence&gt;
 *      &lt;/xs:restriction&gt;
 *    &lt;/xs:complexContent&gt;
 *  &lt;/xs:complexType&gt;
 * </pre></p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XsTSimpleRestrictionTypeImpl extends XsTRestrictionTypeImpl
    implements XsTSimpleRestrictionType {
  private final XsGSimpleRestrictionModel impl;
  private final XsGAttrDecls decls;

  protected XsTSimpleRestrictionTypeImpl(XsObject pParent) {
    super(pParent);
    impl = getObjectFactory().newXsGSimpleRestrictionModel(this);
    decls = getObjectFactory().newXsGAttrDecls(this);
  }

  public XsTLocalSimpleType createSimpleType() throws SAXException {
    return impl.createSimpleType();
  }

  public XsTLocalSimpleType getSimpleType() {
    return impl.getSimpleType();
  }

  public XsEMaxExclusive getMaxExclusive() {
    return impl.getMaxExclusive();
  }

  public XsEMinExclusive createMinExclusive() throws SAXException {
    return impl.createMinExclusive();
  }

  public XsEMinExclusive getMinExclusive() {
    return impl.getMinExclusive();
  }

  public XsEMaxInclusive createMaxInclusive() throws SAXException {
    return impl.createMaxInclusive();
  }

  public XsEMaxInclusive getMaxInclusive() {
    return impl.getMaxInclusive();
  }

  public XsEMinInclusive createMinInclusive() throws SAXException {
    return impl.createMinInclusive();
  }

  public XsEMinInclusive getMinInclusive() {
    return impl.getMinInclusive();
  }

  public XsETotalDigits createTotalDigits() throws SAXException {
    return impl.createTotalDigits();
  }

  public XsETotalDigits getTotalDigits() {
    return impl.getTotalDigits();
  }

  public XsEFractionDigits createFractionDigits() throws SAXException {
    return impl.createFractionDigits();
  }

  public XsEFractionDigits getFractionDigits() {
    return impl.getFractionDigits();
  }

  public XsELength createLength() throws SAXException {
    return impl.createLength();
  }

  public XsELength getLength() {
    return impl.getLength();
  }

  public XsEMinLength createMinLength() throws SAXException {
    return impl.createMinLength();
  }

  public XsEMinLength getMinLength() {
    return impl.getMinLength();
  }

  public XsEMaxLength createMaxLength() throws SAXException {
    return impl.createMaxLength();
  }

  public XsEMaxLength getMaxLength() {
    return impl.getMaxLength();
  }

  public XsEWhiteSpace createWhiteSpace() throws SAXException {
    return impl.createWhiteSpace();
  }

  public XsEWhiteSpace getWhiteSpace() {
    return impl.getWhiteSpace();
  }

  public XsEPattern createPattern() throws SAXException {
    return impl.createPattern();
  }

  public XsEPattern[] getPatterns() {
    return impl.getPatterns();
  }

  public XsEEnumeration createEnumeration() throws SAXException {
    return impl.createEnumeration();
  }

  public XsEEnumeration[] getEnumerations() {
    return impl.getEnumerations();
  }

  public boolean hasFacets() {
    return impl.hasFacets();
  }

  public XsTFacetBase[] getFacets() {
    return impl.getFacets();
  }

  public XsTAttribute createAttribute() {
    return decls.createAttribute();
  }

  public XsTAttribute[] getAttributes() {
    return decls.getAttributes();
  }

  public XsTAttributeGroupRef createAttributeGroup() {
    return decls.createAttributeGroup();
  }

  public XsTAttributeGroupRef[] getAttributeGroups() {
    return decls.getAttributeGroups();
  }

  public XsTWildcard createAnyAttribute() {
    return decls.createAnyAttribute();
  }

  public XsTWildcard getAnyAttribute() {
    return decls.getAnyAttribute();
  }

  public Object[] getAllAttributes() {
    return decls.getAllAttributes();
  }
}
