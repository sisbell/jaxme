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


/** <p>Base implementation of a complex type, to be shared
 * by {@link org.apache.ws.jaxme.xs.xml.XsTLocalComplexType} and
 * {@link org.apache.ws.jaxme.xs.xml.XsTComplexType}.
 * Follows this specification:
 * <pre>
 *  &lt;xs:complexType name="complexType" abstract="true"&gt;
 *    &lt;xs:complexContent&gt;
 *      &lt;xs:extension base="xs:annotated"&gt;
 *        &lt;xs:group ref="xs:complexTypeModel"/&gt;
 *        &lt;xs:attribute name="name" type="xs:NCName"&gt;
 *          &lt;xs:annotation&gt;
 *            &lt;xs:documentation&gt;
 *              Will be restricted to required or forbidden
 *            &lt;/xs:documentation&gt;
 *          &lt;/xs:annotation&gt;
 *        &lt;/xs:attribute&gt;
 *        &lt;xs:attribute name="mixed" type="xs:boolean" use="optional" default="false"&gt;
 *          &lt;xs:annotation&gt;
 *            &lt;xs:documentation&gt;
 *              Not allowed if simpleContent child is chosen.
 *              May be overriden by setting on complexContent child.
 *            &lt;/xs:documentation&gt;
 *          &lt;/xs:annotation&gt;
 *        &lt;/xs:attribute&gt;
 *        &lt;xs:attribute name="abstract" type="xs:boolean" use="optional" default="false"/&gt;
 *        &lt;xs:attribute name="final" type="xs:derivationSet"/&gt;
 *        &lt;xs:attribute name="block" type="xs:derivationSet"/&gt;
 *      &lt;/xs:extension&gt;
 *    &lt;/xs:complexContent&gt;
 *  &lt;/xs:complexType&gt;
 *
 *  &lt;xs:group name="complexTypeModel"&gt;
 *    &lt;xs:choice&gt;
 *      &lt;xs:element ref="xs:simpleContent"/&gt;
 *      &lt;xs:element ref="xs:complexContent"/&gt;
 *      &lt;xs:sequence&gt;
 *        &lt;xs:annotation&gt;
 *          &lt;xs:documentation&gt;
 *            This branch is short for &amp;lt;complexContent&amp;gt;
 *            &amp;lt;restriction base="xs:anyType"&amp;gt;
 *            ...
 *            &amp;lt;/restriction&amp;gt;
 *            &amp;lt;/complexContent&amp;gt;
 *          &lt;/xs:documentation&gt;
 *        &lt;/xs:annotation&gt;
 *        &lt;xs:group ref="xs:typeDefParticle" minOccurs="0"/&gt;
 *        &lt;xs:group ref="xs:attrDecls"/&gt;
 *      &lt;/xs:sequence&gt;
 *    &lt;/xs:choice&gt;
 *  &lt;/xs:group&gt;
 *
 *  &lt;xs:group name="typeDefParticle"&gt;
 *    &lt;xs:annotation&gt;
 *      &lt;xs:documentation&gt;
 *        'complexType' uses this
 *      &lt;/xs:documentation&gt;
 *    &lt;/xs:annotation&gt;
 *    &lt;xs:choice&gt;
 *      &lt;xs:element name="group" type="xs:groupRef"/&gt;
 *      &lt;xs:element ref="xs:all"/&gt;
 *      &lt;xs:element ref="xs:choice"/&gt;
 *      &lt;xs:element ref="xs:sequence"/&gt;
 *    &lt;/xs:choice&gt;
 *  &lt;/xs:group&gt;
 * </pre></p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XsTComplexTypeImpl extends XsTAnnotatedImpl implements XsTComplexType {
  private XsESimpleContent simpleContent;
  private XsEComplexContent complexContent;
  private final XsGTypeDefParticle particle;
  private final XsGAttrDecls attrDecls;
  private boolean isParticleInUse, isMixed, isAbstract;
  private XsDerivationSet finalSet;
  private XsNCName name;

  protected XsTComplexTypeImpl(XsObject pParent) {
    super(pParent);
    particle = getObjectFactory().newXsGTypeDefParticle(this);
    attrDecls = getObjectFactory().newXsGAttrDecls(this);
  }

  public XsDerivationSet getFinal() {
    return finalSet;
  }

  public void setFinal(XsDerivationSet pFinal) {
    finalSet = pFinal;
  }

  public XsESimpleContent createSimpleContent() {
    if (simpleContent != null) {
      throw new IllegalStateException("Multiple 'simpleContent' elements are forbidden.");
    }
    if (complexContent != null  ||  isParticleInUse) {
      throw new IllegalStateException("The child elements 'simpleContent', 'complexContent', 'group', 'all', 'sequence', or 'choice' are mutually exclusive.");
    }
    return simpleContent = getObjectFactory().newXsESimpleContent(this);
  }

  public XsESimpleContent getSimpleContent() {
    return simpleContent;
  }

  public XsEComplexContent createComplexContent() {
    if (complexContent != null) {
      throw new IllegalStateException("Multiple 'complexContent' elements are forbidden.");
    }
    if (simpleContent != null  ||  isParticleInUse) {
      throw new IllegalStateException("The child elements 'simpleContent', 'complexContent', 'group', 'all', 'sequence', or 'choice' are mutually exclusive.");
    }
    return complexContent = getObjectFactory().newXsEComplexContent(this);
  }

  public XsEComplexContent getComplexContent() {
    return complexContent;
  }

  public XsTGroupRef createGroup() {
    if (simpleContent != null  ||  complexContent != null  ||  isParticleInUse) {
      throw new IllegalStateException("The child elements 'simpleContent', 'complexContent', 'group', 'all', 'sequence', or 'choice' are mutually exclusive.");
    }
    isParticleInUse = true;
    return particle.createGroup();
  }

  public XsTAll createAll() {
    if (simpleContent != null  ||  complexContent != null  ||  isParticleInUse) {
      throw new IllegalStateException("The child elements 'simpleContent', 'complexContent', 'group', 'all', 'sequence', or 'choice' are mutually exclusive.");
    }
    isParticleInUse = true;
    return particle.createAll();
  }

  public XsEChoice createChoice() {
    if (simpleContent != null  ||  complexContent != null  ||  isParticleInUse) {
      throw new IllegalStateException("The child elements 'simpleContent', 'complexContent', 'group', 'all', 'sequence', or 'choice' are mutually exclusive.");
    }
    isParticleInUse = true;
    return particle.createChoice();
  }

  public XsESequence createSequence() {
    if (simpleContent != null  ||  complexContent != null  ||  isParticleInUse) {
      throw new IllegalStateException("The child elements 'simpleContent', 'complexContent', 'group', 'all', 'sequence', or 'choice' are mutually exclusive.");
    }
    isParticleInUse = true;
    return particle.createSequence();
  }

  public XsTTypeDefParticle getTypeDefParticle() {
    return particle.getTypeDefParticle();
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

  public void setName(XsNCName pName) {
    name = pName;
  }

  public XsNCName getName() {
    return name;
  }

  public boolean isAbstract() {
    return isAbstract;
  }

  public void setAbstract(boolean pAbstract) {
    isAbstract = pAbstract;
  }

  public boolean isMixed() {
    return isMixed;
  }

  public void setMixed(boolean pMixed) {
    isMixed = pMixed;
  }
}
