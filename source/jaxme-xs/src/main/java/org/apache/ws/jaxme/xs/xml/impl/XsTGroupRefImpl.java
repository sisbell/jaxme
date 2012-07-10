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


/** <p>Implementation of the <code>xs:groupRef</code> type,
 * with the following specification:
 * <pre>
 *  <xs:complexType name="groupRef">
 *    <xs:complexContent>
 *      <xs:restriction base="xs:realGroup">
 *        <xs:sequence>
 *          <xs:element ref="xs:annotation" minOccurs="0"/>
 *        </xs:sequence>
 *        <xs:attribute name="ref" use="required" type="xs:QName"/>
 *        <xs:attribute name="name" use="prohibited"/>
 *      </xs:restriction>
 *   </xs:complexContent>
 * </xs:complexType>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XsTGroupRefImpl extends XsTRealGroupImpl implements XsTGroupRef {
  protected XsTGroupRefImpl(XsObject pParent) {
    super(pParent);
  }

  public void validate() {
    super.validate();
    if (getName() != null) {
      throw new IllegalStateException("A group reference must not have a 'name' attribute.");
    }
    if (getRef() == null) {
      throw new NullPointerException("A group reference must have a nonempty 'ref' attribute.");
    }
    if (getParticles().length != 0) {
      throw new NullPointerException("A group reference must not have any 'all', 'sequence', or 'choice' elements.");
    }
  }
}
