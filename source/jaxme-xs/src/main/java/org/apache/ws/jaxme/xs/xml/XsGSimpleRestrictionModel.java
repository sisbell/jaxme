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


/** <p>Interface of <code>xs:simpleRestrictionModel</code>, following
 * this specification:
 * <pre>
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
public interface XsGSimpleRestrictionModel {
  public XsTLocalSimpleType createSimpleType() throws SAXException;
  public XsTLocalSimpleType getSimpleType();

  public XsEMinExclusive createMinExclusive() throws SAXException;
  public XsEMinExclusive getMinExclusive();

  public XsEMinInclusive createMinInclusive() throws SAXException;
  public XsEMinInclusive getMinInclusive();

  public XsEMaxExclusive createMaxExclusive() throws SAXException;
  public XsEMaxExclusive getMaxExclusive();

  public XsEMaxInclusive createMaxInclusive() throws SAXException;
  public XsEMaxInclusive getMaxInclusive();

  public XsETotalDigits createTotalDigits() throws SAXException;
  public XsETotalDigits getTotalDigits();

  public XsEFractionDigits createFractionDigits() throws SAXException;
  public XsEFractionDigits getFractionDigits();

  public XsELength createLength() throws SAXException;
  public XsELength getLength();

  public XsEMinLength createMinLength() throws SAXException;
  public XsEMinLength getMinLength();

  public XsEMaxLength createMaxLength() throws SAXException;
  public XsEMaxLength getMaxLength();

  public XsEWhiteSpace createWhiteSpace() throws SAXException;
  public XsEWhiteSpace getWhiteSpace();

  public XsEPattern createPattern() throws SAXException;
  public XsEPattern[] getPatterns();

  public XsEEnumeration createEnumeration() throws SAXException;
  public XsEEnumeration[] getEnumerations();

  /** <p>Returns whether any facet has been defined.</p>
   */
  public boolean hasFacets();

  /** <p>Returns an array of all facets.</p>
   */
  public XsTFacetBase[] getFacets();
}
