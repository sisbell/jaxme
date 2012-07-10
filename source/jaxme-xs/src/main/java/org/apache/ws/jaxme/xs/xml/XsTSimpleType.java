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


/** <p>Interface of the following specification for
 * <code>xs:simpleType</code>:
 * <pre>
 *  &lt;xs:complexType name="simpleType" abstract="true"&gt;
 *    &lt;xs:complexContent&gt;
 *      &lt;xs:extension base="xs:annotated"&gt;
 *        &lt;xs:group ref="xs:simpleDerivation"/&gt;
 *        &lt;xs:attribute name="final" type="xs:simpleDerivationSet"/&gt;
 *        &lt;xs:attribute name="name" type="xs:NCName"&gt;
 *          &lt;xs:annotation&gt;
 *            &lt;xs:documentation&gt;
 *              Can be restricted to required or forbidden
 *            &lt;/xs:documentation&gt;
 *          &lt;/xs:annotation&gt;
 *        &lt;/xs:attribute&gt;
 *      &lt;/xs:extension&gt;
 *    &lt;/xs:complexContent&gt;
 *  &lt;/xs:complexType&gt;
 *
 *  &lt;xs:group name="simpleDerivation"&gt;
 *    &lt;xs:choice&gt;
 *      &lt;xs:element ref="xs:restriction"/&gt;
 *      &lt;xs:element ref="xs:list"/&gt;
 *      &lt;xs:element ref="xs:union"/&gt;
 *    &lt;/xs:choice&gt;
 *  &lt;/xs:group&gt;
 * </pre></p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface XsTSimpleType extends XsTAnnotated {
  public XsERestriction createRestriction() throws SAXException;
  public XsERestriction getRestriction();

  public XsEList createList() throws SAXException;
  public XsEList getList();

  public XsEUnion createUnion() throws SAXException;
  public XsEUnion getUnion();

  public void setFinal(XsSimpleDerivationSet pSet) throws SAXException;
  public XsSimpleDerivationSet getFinal();

  public void setName(XsNCName pName) throws SAXException;
  public XsNCName getName();
}
