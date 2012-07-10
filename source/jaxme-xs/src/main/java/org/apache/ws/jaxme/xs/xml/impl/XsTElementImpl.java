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


/** <p>Implementation of the <code>xs:element</code> type,
 * as specified by the following:
 * <pre>
 *  &lt;xs:complexType name="element" abstract="true"&gt;
 *    &lt;xs:annotation&gt;
 *      &lt;xs:documentation&gt;
 *        The element element can be used either
 *        at the top level to define an element-type binding globally,
 *        or within a content model to either reference a globally-defined
 *        element or type or declare an element-type binding locally.
 *        The ref form is not allowed at the top level.
 *      &lt;/xs:documentation&gt;
 *    &lt;/xs:annotation&gt;
 *    &lt;xs:complexContent&gt;
 *      &lt;xs:extension base="xs:annotated"&gt;
 *        &lt;xs:sequence&gt;
 *          &lt;xs:choice minOccurs="0"&gt;
 *            &lt;xs:element name="simpleType" type="xs:localSimpleType"/&gt;
 *            &lt;xs:element name="complexType" type="xs:localComplexType"/&gt;
 *          &lt;/xs:choice&gt;
 *          &lt;xs:group ref="xs:identityConstraint" minOccurs="0" maxOccurs="unbounded"/&gt;
 *        &lt;/xs:sequence&gt;
 *        &lt;xs:attributeGroup ref="xs:defRef"/&gt;
 *        &lt;xs:attribute name="type" type="xs:QName"/&gt;
 *        &lt;xs:attribute name="substitutionGroup" type="xs:QName"/&gt;
 *        &lt;xs:attributeGroup ref="xs:occurs"/&gt;
 *        &lt;xs:attribute name="default" type="xs:string"/&gt;
 *        &lt;xs:attribute name="fixed" type="xs:string"/&gt;
 *        &lt;xs:attribute name="nillable" type="xs:boolean" use="optional" default="false"/&gt;
 *        &lt;xs:attribute name="abstract" type="xs:boolean" use="optional" default="false"/&gt;
 *        &lt;xs:attribute name="final" type="xs:derivationSet"/&gt;
 *        &lt;xs:attribute name="block" type="xs:blockSet"/&gt;
 *        &lt;xs:attribute name="form" type="xs:formChoice"/&gt;
 *      &lt;/xs:extension&gt;
 *    &lt;/xs:complexContent&gt;
 *  &lt;/xs:complexType&gt;
 * </pre></p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public abstract class XsTElementImpl extends XsTAnnotatedImpl implements XsTElement {
  private XsTLocalSimpleType simpleType;
  private XsTLocalComplexType complexType;
  private XsQName type;
  private XsQName substitutionGroup;
  private String defaultValue;
  private String fixedValue;
  private boolean isNillable;
  private boolean isAbstract;
  private XsDerivationSet finalSet;
  private XsBlockSet blockSet;
  private XsFormChoice formChoice;
  private final XsAGDefRef defRef;
  private final XsAGOccurs occurs;
  private XsGIdentityConstraint constraint;

  protected XsTElementImpl(XsObject pParent) {
    super(pParent);
    defRef = getObjectFactory().newXsAGDefRef(this);
    occurs = getObjectFactory().newXsAGOccurs(this);
    constraint = getObjectFactory().newXsGIdentityConstraint(this);
  }

  public XsTLocalSimpleType createSimpleType() throws SAXException {
    if (simpleType != null) {
      throw new LocSAXException("Multiple 'simpleType' childs are forbidden.", getLocator());
    }
    if (complexType != null) {
      throw new LocSAXException("The 'simpleType' and 'complexType' childs are mutually exclusive.", getLocator());
    }
    if (type != null) {
      throw new LocSAXException("The 'simpleType' child and the 'type' attribute are mutually exclusive.", getLocator());
    }
    return simpleType = getObjectFactory().newXsTLocalSimpleType(this);
  }

  public XsTLocalSimpleType getSimpleType() {
    return simpleType;
  }

  public XsTLocalComplexType createComplexType() throws SAXException {
    if (complexType != null) {
      throw new LocSAXException("Multiple 'complexType' childs are forbidden.", getLocator());
    }
    if (simpleType != null) {
      throw new LocSAXException("The 'simpleType' and 'complexType' childs are mutually exclusive.", getLocator());
    }
    if (type != null) {
      throw new LocSAXException("The 'complexType' child and the 'type' attribute are mutually exclusive.", getLocator());
    }
    return complexType = getObjectFactory().newXsTLocalComplexType(this);
  }

  public XsTLocalComplexType getComplexType() {
    return complexType;
  }

  public void setType(XsQName pType) throws SAXException {
    if (simpleType != null) {
      throw new LocSAXException("The 'simpleType' child element and the 'type' attribute are mutually exclusive.", getLocator());
    }
    if (complexType != null) {
      throw new LocSAXException("The 'complexType' child element and the 'type' attribute are mutually exclusive.", getLocator());
    }
    type = pType;
  }

  public void setType(String pType) throws SAXException {
    setType(asXsQName(pType));
  }

  public XsQName getType() {
    return type;
  }

  public void setSubstitutionGroup(XsQName pSubstitutionGroup) throws SAXException {
    substitutionGroup = pSubstitutionGroup;
  }

  public void setSubstitutionGroup(String pSubstitutionGroup) throws SAXException {
    setSubstitutionGroup(asXsQName(pSubstitutionGroup));
  }

  public XsQName getSubstitutionGroup() {
    return substitutionGroup;
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

  public void setNillable(boolean pNillable) {
    isNillable = pNillable;
  }

  public boolean getNillable() {
    return isNillable;
  }

  public void setAbstract(boolean pAbstract) {
    isAbstract = pAbstract;
  }

  public boolean getAbstract() {
    return isAbstract;
  }

  public void setFinal(XsDerivationSet pFinal) {
    finalSet = pFinal;
  }

  public XsDerivationSet getFinal() {
    return finalSet;
  }

  public void setBlock(XsBlockSet pBlock) {
    blockSet = pBlock;
  }

  public XsBlockSet getBlock() {
    return blockSet;
  }

  public void setForm(XsFormChoice pForm) {
    formChoice = pForm;
  }

  public XsFormChoice getForm() {
    return formChoice;
  }

  public void validate() throws SAXException {
    super.validate();
    if (type == null  &&  simpleType == null  &&  complexType == null  &&  getRef() == null) {
      setType(org.apache.ws.jaxme.xs.types.XSAnyType.getInstance().getName());
    }
    defRef.validate();
    occurs.validate();
  }

  public void setName(XsNCName pName) {
    defRef.setName(pName);
  }

  public XsNCName getName() {
    return defRef.getName();
  }

  public void setRef(XsQName pRef) {
    defRef.setRef(pRef);
  }

  public void setRef(String pRef) throws SAXException {
    setRef(asXsQName(pRef));
  }

  public XsQName getRef() {
    return defRef.getRef();
  }

  public void setMaxOccurs(String pMaxOccurs) {
    occurs.setMaxOccurs(pMaxOccurs);
  }

  public int getMaxOccurs() {
    return occurs.getMaxOccurs();
  }

  public void setMinOccurs(int pMinOccurs) {
    occurs.setMinOccurs(pMinOccurs);
  }

  public int getMinOccurs() {
    return occurs.getMinOccurs();
  }

  public XsEUnique createUnique() {
    return constraint.createUnique();
  }

  public XsEKey createKey() {
    return constraint.createKey();
  }

  public XsEKeyref createKeyref() {
    return constraint.createKeyref();
  }

  public XsTIdentityConstraint[] getIdentityConstraints() {
    return constraint.getIdentityConstraints();
  }
}
