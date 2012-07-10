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


/** <p>Implementation of <code>xs:restriction</code>, following
 * this specification:
 * <pre>
 *  &lt;xs:element name="restriction" id="restriction"&gt;
 *    &lt;xs:complexType&gt;
 *      &lt;xs:annotation&gt;
 *        &lt;xs:documentation
 *            source="http://www.w3.org/TR/xmlschema-2/#element-restriction"&gt;
 *          base attribute and simpleType child are mutually
 *          exclusive, but one or other is required
 *        &lt;/xs:documentation&gt;
 *      &lt;/xs:annotation&gt;
 *      &lt;xs:complexContent&gt;
 *        &lt;xs:extension base="xs:annotated"&gt;
 *          &lt;xs:group ref="xs:simpleRestrictionModel"/&gt;
 *          &lt;xs:attribute name="base" type="xs:QName" use="optional"/&gt;
 *        &lt;/xs:extension&gt;
 *      &lt;/xs:complexContent&gt;
 *    &lt;/xs:complexType&gt;
 *  &lt;/xs:element&gt;
 *
 *  &lt;xs:group name="simpleRestrictionModel"&gt;
 *    &lt;xs:sequence&gt;
 *      &lt;xs:element name="simpleType" type="xs:localSimpleType" minOccurs="0"/&gt;
 *      &lt;xs:group ref="xs:facets" minOccurs="0" maxOccurs="unbounded"/&gt;
 *    &lt;/xs:sequence&gt;
 *  &lt;/xs:group&gt;
 *
 *  &lt;xs:group name="facets"&gt;
 *    &lt;xs:annotation&gt;
 *      &lt;xs:documentation&gt;
 *        We should use a substitution group for facets, but
 *        that's ruled out because it would allow users to
 *        add their own, which we're not ready for yet.
 *      &lt;/xs:documentation&gt;
 *    &lt;/xs:annotation&gt;
 *    &lt;xs:choice&gt;
 *      &lt;xs:element ref="xs:minExclusive"/&gt;
 *      &lt;xs:element ref="xs:minInclusive"/&gt;
 *      &lt;xs:element ref="xs:maxExclusive"/&gt;
 *      &lt;xs:element ref="xs:maxInclusive"/&gt;
 *      &lt;xs:element ref="xs:totalDigits"/&gt;
 *      &lt;xs:element ref="xs:fractionDigits"/&gt;
 *      &lt;xs:element ref="xs:length"/&gt;
 *      &lt;xs:element ref="xs:minLength"/&gt;
 *      &lt;xs:element ref="xs:maxLength"/&gt;
 *      &lt;xs:element ref="xs:enumeration"/&gt;
 *      &lt;xs:element ref="xs:whiteSpace"/&gt;
 *      &lt;xs:element ref="xs:pattern"/&gt;
 *    &lt;/xs:choice&gt;
 *  &lt;/xs:group&gt;
 * </pre></p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface XsERestriction extends XsTAnnotated, XsGSimpleRestrictionModel {
  public void setBase(XsQName pBase);

  public XsQName getBase();

  public XsTLocalSimpleType createSimpleType() throws SAXException;

  public XsTLocalSimpleType getSimpleType();
}
