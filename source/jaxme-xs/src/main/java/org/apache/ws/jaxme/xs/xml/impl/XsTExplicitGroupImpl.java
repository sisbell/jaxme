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
import org.apache.ws.jaxme.xs.xml.XsTExplicitGroup;
import org.xml.sax.SAXException;


/** <p>Implementation of the <code>xs:explicitGroup</code>
 * type, as specified by the following:
 * <pre>
 *  <xs:complexType name="explicitGroup">
 *    <xs:annotation>
 *      <xs:documentation>
 *        group type for the three kinds of group
 *      </xs:documentation>
 *    </xs:annotation>
 *    <xs:complexContent>
 *      <xs:restriction base="xs:group">
 *        <xs:sequence>
 *          <xs:element ref="xs:annotation" minOccurs="0"/>
 *          <xs:group ref="xs:nestedParticle" minOccurs="0" maxOccurs="unbounded"/>
 *        </xs:sequence>
 *        <xs:attribute name="name" type="xs:NCName" use="prohibited"/>
 *        <xs:attribute name="ref" type="xs:QName" use="prohibited"/>
 *      </xs:restriction>
 *    </xs:complexContent>
 *  </xs:complexType>
 * </pre></p>
 * <p><em>Implementation note:</em> This interface doesn't specify
 * any additional methods. However, the implementations <code>validate()</code>
 * method must ensure that
 * <ol>
 *   <li>neither of the 'name' or 'ref' attributes are set</li>
 *   <li>Either of the 'element', 'choice', 'sequence', 'any', or 'group'
 *     child elements is set, but not the 'all' element.</li>
 * </ol>
 * </p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XsTExplicitGroupImpl extends XsTGroupImpl implements XsTExplicitGroup {
  protected XsTExplicitGroupImpl(XsObject pParent) {
    super(pParent);
  }

  public void setName(XsNCName pName) {
    throw new IllegalStateException("This group must not have its 'name' attribute set.");
  }

  public void setRef(XsQName pRef) {
    throw new IllegalStateException("This group must not have its 'ref' attribute set.");
  }

  public void setRef(String pRef) throws SAXException {
    setRef(asXsQName(pRef));
  }
}
