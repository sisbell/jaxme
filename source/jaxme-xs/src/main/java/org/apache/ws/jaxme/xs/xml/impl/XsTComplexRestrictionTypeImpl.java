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


/** <p>Implementation of <code>xs:complexRestrictionType</code>,
 * as specified by the following:
 * <pre>
 *  &lt;xs:complexType name="complexRestrictionType"&gt;
 *    &lt;xs:complexContent&gt;
 *      &lt;xs:restriction base="xs:restrictionType"&gt;
 *        &lt;xs:sequence&gt;
 *          &lt;xs:element ref="xs:annotation" minOccurs="0"/&gt;
 *          &lt;xs:group ref="xs:typeDefParticle" minOccurs="0"/&gt;
 *          &lt;xs:group ref="xs:attrDecls"/&gt;
 *        &lt;/xs:sequence&gt;
 *      &lt;/xs:restriction&gt;
 *    &lt;/xs:complexContent&gt;
 *  &lt;/xs:complexType&gt;
 * </pre></p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XsTComplexRestrictionTypeImpl extends XsTRestrictionTypeImpl
    implements XsTComplexRestrictionType {
  protected XsTComplexRestrictionTypeImpl(XsObject pParent) {
    super(pParent);
  }

  public XsTLocalSimpleType createSimpleType() throws SAXException {
    throw new LocSAXException("A restriction with complex content doesn't support the 'simpleType' child element.", getLocator());
  }

  public XsEMinExclusive createMinExclusive() throws SAXException {
    throw new LocSAXException("A restriction with complex content doesn't support the 'minExclusive' child element.", getLocator());
  }

  public XsEMinInclusive createMinInclusive() throws SAXException {
    throw new LocSAXException("A restriction with complex content doesn't support the 'minInclusive' child element.", getLocator());
  }

  public XsEMaxExclusive createMaxExclusive() throws SAXException {
    throw new LocSAXException("A restriction with complex content doesn't support the 'maxExclusive' child element.", getLocator());
  }

  public XsEMaxInclusive createMaxInclusive() throws SAXException {
    throw new LocSAXException("A restriction with complex content doesn't support the 'maxInclusive' child element.", getLocator());
  }

  public XsETotalDigits createTotalDigits() throws SAXException {
    throw new LocSAXException("A restriction with complex content doesn't support the 'totalDigits' child element.", getLocator());
  }

  public XsEFractionDigits createFractionDigits() throws SAXException {
    throw new LocSAXException("A restriction with complex content doesn't support the 'fractionDigits' child element.", getLocator());
  }

  public XsELength createLength() throws SAXException {
    throw new LocSAXException("A restriction with complex content doesn't support the 'length' child element.", getLocator());
  }

  public XsEMinLength createMinLength() throws SAXException {
    throw new LocSAXException("A restriction with complex content doesn't support the 'minLength' child element.", getLocator());
  }

  public XsEMaxLength createMaxLength() throws SAXException {
    throw new LocSAXException("A restriction with complex content doesn't support the 'maxLength' child element.", getLocator());
  }

  public XsEWhiteSpace createWhiteSpace() throws SAXException {
    throw new LocSAXException("A restriction with complex content doesn't support the 'whiteSpace' child element.", getLocator());
  }

  public XsEPattern createPattern() throws SAXException {
    throw new LocSAXException("A restriction with complex content doesn't support the 'pattern' child element.", getLocator());
  }

  public XsEEnumeration createEnumeration() throws SAXException {
    throw new LocSAXException("A restriction with complex content doesn't support the 'enumeration' child element.", getLocator());
  }
}
