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
package org.apache.ws.jaxme.xs.xml;

import org.xml.sax.SAXException;


/** <p>Implementation of the <code>xs:element</code> type,
 * as specified by the following:
 * <pre>
 *  <xs:complexType name="element" abstract="true">
 *    <xs:annotation>
 *      <xs:documentation>
 *        The element element can be used either
 *        at the top level to define an element-type binding globally,
 *        or within a content model to either reference a globally-defined
 *        element or type or declare an element-type binding locally.
 *        The ref form is not allowed at the top level.
 *      </xs:documentation>
 *    </xs:annotation>
 *    <xs:complexContent>
 *      <xs:extension base="xs:annotated">
 *        <xs:sequence>
 *          <xs:choice minOccurs="0">
 *            <xs:element name="simpleType" type="xs:localSimpleType"/>
 *            <xs:element name="complexType" type="xs:localComplexType"/>
 *          </xs:choice>
 *          <xs:group ref="xs:identityConstraint" minOccurs="0" maxOccurs="unbounded"/>
 *        </xs:sequence>
 *        <xs:attributeGroup ref="xs:defRef"/>
 *        <xs:attribute name="type" type="xs:QName"/>
 *        <xs:attribute name="substitutionGroup" type="xs:QName"/>
 *        <xs:attributeGroup ref="xs:occurs"/>
 *        <xs:attribute name="default" type="xs:string"/>
 *        <xs:attribute name="fixed" type="xs:string"/>
 *        <xs:attribute name="nillable" type="xs:boolean" use="optional" default="false"/>
 *        <xs:attribute name="abstract" type="xs:boolean" use="optional" default="false"/>
 *        <xs:attribute name="final" type="xs:derivationSet"/>
 *        <xs:attribute name="block" type="xs:blockSet"/>
 *        <xs:attribute name="form" type="xs:formChoice"/>
 *      </xs:extension>
 *    </xs:complexContent>
 *  </xs:complexType>
 * </pre></p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface XsTElement extends XsTAnnotated, XsAGDefRef, XsAGOccurs, XsGIdentityConstraint {
  public XsTLocalSimpleType createSimpleType() throws SAXException;
  public XsTLocalSimpleType getSimpleType();

  public XsTLocalComplexType createComplexType() throws SAXException;
  public XsTLocalComplexType getComplexType();

  public void setType(XsQName pType) throws SAXException;
  public XsQName getType();

  public void setSubstitutionGroup(XsQName pSubstitutionGroup) throws SAXException;
  public XsQName getSubstitutionGroup();

  public void setDefault(String pDefault);
  public String getDefault();

  public void setFixed(String pFixed);
  public String getFixed();

  public void setNillable(boolean pNillable);
  public boolean getNillable();

  public void setAbstract(boolean pAbstract);
  public boolean getAbstract();

  public void setFinal(XsDerivationSet pFinal);
  public XsDerivationSet getFinal();

  public void setBlock(XsBlockSet pBlock);
  public XsBlockSet getBlock();

  public void setForm(XsFormChoice pForm);
  public XsFormChoice getForm();

  public boolean isGlobal();
}
