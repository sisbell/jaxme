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


/** <p>Implementation of the group <code>xs:complexTypeModel</code>,
 * as specified by the following:
 * <pre>
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
 * </pre></p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XsGComplexTypeModelImpl implements XsGComplexTypeModel {
  private final XsObject owner;
  private XsESimpleContent simpleContent;
  private XsEComplexContent complexContent;
  private boolean otherContent;
  private final XsGTypeDefParticle typeDefParticle;
  private final XsGAttrDecls attrDecls;

  protected XsGComplexTypeModelImpl(XsObject pOwner) {
    owner = pOwner;
    XsObjectFactory factory = owner.getObjectFactory();
    typeDefParticle = factory.newXsGTypeDefParticle(owner);
    attrDecls = factory.newXsGAttrDecls(owner);
  }

  public XsESimpleContent createSimpleContent() {
    if (simpleContent != null) {
      throw new IllegalStateException("Multiple 'simpleContent' child elements are forbidden.");
    }
    if (complexContent != null) {
      throw new IllegalStateException("The 'simpleContent' and 'complexContent' child elements are mutually exclusive.");
    }
    if (otherContent) {
      throw new IllegalStateException("The 'simpleContent' child element and the child elements 'all', 'choice', 'sequence', 'group', 'attribute', and 'attributeGroup' are mutually exclusive.");
    }
    return simpleContent = owner.getObjectFactory().newXsESimpleContent(owner);
  }

  public XsESimpleContent getSimpleContent() {
    return simpleContent;
  }

  public XsEComplexContent createComplexContent() {
    if (complexContent != null) {
      throw new IllegalStateException("Multiple 'complexContent' child elements are forbidden.");
    }
    if (simpleContent != null) {
      throw new IllegalStateException("The 'complexContent' and 'simpleContent' child elements are mutually exclusive.");
    }
    if (otherContent) {
      throw new IllegalStateException("The 'complexContent' child element and the child elements 'all', 'choice', 'sequence', 'group', 'attribute', and 'attributeGroup' are mutually exclusive.");
    }
    return complexContent = owner.getObjectFactory().newXsEComplexContent(owner);
  }

  public XsEComplexContent getComplexContent() {
    return complexContent;
  }

  protected void validateOtherContent() {
    if (simpleContent != null) {
      throw new IllegalStateException("The child elements 'all', 'choice', 'sequence', 'group', 'attribute', 'attributeGroup', and the child element 'simpleContent' are mutually exclusive.");
    }
    if (complexContent != null) {
      throw new IllegalStateException("The child elements 'all', 'choice', 'sequence', 'group', 'attribute', 'attributeGroup', and the child element 'complexContent' are mutually exclusive.");
    }
  }

  public XsTAttribute createAttribute() {
    validateOtherContent();
    return attrDecls.createAttribute();
  }

  public XsTAttribute[] getAttributes() {
    return attrDecls.getAttributes();
  }

  public XsTAttributeGroupRef createAttributeGroup() {
    validateOtherContent();
    return attrDecls.createAttributeGroup();
  }

  public XsTAttributeGroupRef[] getAttributeGroups() {
    return attrDecls.getAttributeGroups();
  }

  public XsTWildcard createAnyAttribute() {
    validateOtherContent();
    return attrDecls.createAnyAttribute();
  }

  public XsTWildcard getAnyAttribute() {
    return attrDecls.getAnyAttribute();
  }

  public Object[] getAllAttributes() {
    return attrDecls.getAllAttributes();
  }

  public void validate() throws SAXException {
    if (!otherContent  &&  (simpleContent == null  &&  complexContent == null)) {
      throw new LocSAXException("You must specify either of the 'simpleContent', 'complexContent', 'all', 'choice', 'sequence', or 'group' child elements.",
                                 owner.getLocator());
    }
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
}
