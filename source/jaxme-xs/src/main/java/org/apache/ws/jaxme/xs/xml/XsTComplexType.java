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


/** <p>Interface of a complex type, to be shared
 * by {@link org.apache.ws.jaxme.xs.xml.XsTLocalComplexType} and
 * {@link XsTComplexType}.
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
public interface XsTComplexType extends XsTAnnotated, XsGTypeDefParticle, XsGAttrDecls, XsRedefinable {
  public void setMixed(boolean pMixed);
  public boolean isMixed();

  public void setAbstract(boolean pAbstract);
  public boolean isAbstract();

  public XsESimpleContent createSimpleContent();

  public XsESimpleContent getSimpleContent();

  public XsEComplexContent createComplexContent();

  public XsEComplexContent getComplexContent();

  public void setName(XsNCName pName);

  public XsNCName getName();
}
