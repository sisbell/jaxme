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


/** <p>Implementation of <code>xs:restrictionType</code>,
 * as specified by the following:
 * <pre>
 *  &lt;xs:complexType name="restrictionType"&gt;
 *    &lt;xs:complexContent&gt;
 *      &lt;xs:extension base="xs:annotated"&gt;
 *        &lt;xs:sequence&gt;
 *          &lt;xs:choice&gt;
 *            &lt;xs:group ref="xs:typeDefParticle" minOccurs="0"/&gt;
 *            &lt;xs:group ref="xs:simpleRestrictionModel" minOccurs="0"/&gt;
 *          &lt;/xs:choice&gt;
 *          &lt;xs:group ref="xs:attrDecls"/&gt;
 *        &lt;/xs:sequence&gt;
 *        &lt;xs:attribute name="base" type="xs:QName" use="required"/&gt;
 *      &lt;/xs:extension&gt;
 *    &lt;/xs:complexContent&gt;
 *  &lt;/xs:complexType&gt;
 * </pre></p>
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XsTRestrictionTypeImpl extends XsTAnnotatedImpl
    implements XsTRestrictionType {
  private XsQName base;
  private final XsGTypeDefParticle typeDefParticle;
  private final XsGSimpleRestrictionModel simpleRestrictionModel;
  private final XsGAttrDecls attrDecls;

  protected XsTRestrictionTypeImpl(XsObject pParent) {
    super(pParent);
    typeDefParticle = getObjectFactory().newXsGTypeDefParticle(this);
    simpleRestrictionModel = getObjectFactory().newXsGSimpleRestrictionModel(this);
    attrDecls = getObjectFactory().newXsGAttrDecls(this);
  }

  public void setBase(XsQName pBase) {
    base = pBase;
  }

  public void setBase(String pBase) throws SAXException {
    setBase(asXsQName(pBase));
  }

  public XsQName getBase() {
    return base;
  }

  public XsTGroupRef createGroup() {
    return typeDefParticle.createGroup();
  }

  public XsTAll createAll() {
    return typeDefParticle.createAll();
  }

  public XsESequence createSequence() {
    return typeDefParticle.createSequence();
  }

  public XsEChoice createChoice() {
    return typeDefParticle.createChoice();
  }

  public XsTTypeDefParticle getTypeDefParticle() {
    return typeDefParticle.getTypeDefParticle();
  }

  public XsTLocalSimpleType createSimpleType() throws SAXException {
    return simpleRestrictionModel.createSimpleType();
  }

  public XsTLocalSimpleType getSimpleType() {
    return simpleRestrictionModel.getSimpleType();
  }

  public XsEMinExclusive createMinExclusive() throws SAXException {
    return simpleRestrictionModel.createMinExclusive();
  }

  public XsEMinExclusive getMinExclusive() {
    return simpleRestrictionModel.getMinExclusive();
  }

  public XsEMinInclusive createMinInclusive() throws SAXException {
    return simpleRestrictionModel.createMinInclusive();
  }

  public XsEMinInclusive getMinInclusive() {
    return simpleRestrictionModel.getMinInclusive();
  }

  public XsEMaxExclusive createMaxExclusive() throws SAXException {
    return simpleRestrictionModel.createMaxExclusive();
  }

  public XsEMaxExclusive getMaxExclusive() {
    return simpleRestrictionModel.getMaxExclusive();
  }

  public XsEMaxInclusive createMaxInclusive() throws SAXException {
    return simpleRestrictionModel.createMaxInclusive();
  }

  public XsEMaxInclusive getMaxInclusive() {
    return simpleRestrictionModel.getMaxInclusive();
  }

  public XsETotalDigits createTotalDigits() throws SAXException {
    return simpleRestrictionModel.createTotalDigits();
  }

  public XsETotalDigits getTotalDigits() {
    return simpleRestrictionModel.getTotalDigits();
  }

  public XsEFractionDigits createFractionDigits() throws SAXException {
    return simpleRestrictionModel.createFractionDigits();
  }

  public XsEFractionDigits getFractionDigits() {
    return simpleRestrictionModel.getFractionDigits();
  }

  public XsELength createLength() throws SAXException {
    return simpleRestrictionModel.createLength();
  }

  public XsELength getLength() {
    return simpleRestrictionModel.getLength();
  }

  public XsEMinLength createMinLength() throws SAXException {
    return simpleRestrictionModel.createMinLength();
  }

  public XsEMinLength getMinLength() {
    return simpleRestrictionModel.getMinLength();
  }

  public XsEMaxLength createMaxLength() throws SAXException {
    return simpleRestrictionModel.createMaxLength();
  }

  public XsEMaxLength getMaxLength() {
    return simpleRestrictionModel.getMaxLength();
  }

  public XsEWhiteSpace createWhiteSpace() throws SAXException {
    return simpleRestrictionModel.createWhiteSpace();
  }

  public XsEWhiteSpace getWhiteSpace() {
    return simpleRestrictionModel.getWhiteSpace();
  }

  public XsEPattern createPattern() throws SAXException {
    return simpleRestrictionModel.createPattern();
  }

  public XsEPattern[] getPatterns() {
    return simpleRestrictionModel.getPatterns();
  }

  public XsEEnumeration createEnumeration() throws SAXException {
    return simpleRestrictionModel.createEnumeration();
  }

  public XsEEnumeration[] getEnumerations() {
    return simpleRestrictionModel.getEnumerations();
  }

  public boolean hasFacets() {
    return simpleRestrictionModel.hasFacets();
  }

  public XsTFacetBase[] getFacets() {
    return simpleRestrictionModel.getFacets();
  }

  public XsTAttribute createAttribute() {
    return attrDecls.createAttribute();
  }

  public XsTAttribute[] getAttributes() {
    return attrDecls.getAttributes();
  }

  public XsTAttributeGroupRef createAttributeGroup() {
    return attrDecls.createAttributeGroup();
  }

  public XsTAttributeGroupRef[] getAttributeGroups() {
    return attrDecls.getAttributeGroups();
  }

  public XsTWildcard createAnyAttribute() {
    return attrDecls.createAnyAttribute();
  }

  public XsTWildcard getAnyAttribute() {
    return attrDecls.getAnyAttribute();
  }

  public Object[] getAllAttributes() {
    return attrDecls.getAllAttributes();
  }

  public void validate() throws SAXException {
    super.validate();
    if (base == null) {
      throw new LocSAXException("The 'base' attribute must be set.", getLocator());
    }
  }
}
