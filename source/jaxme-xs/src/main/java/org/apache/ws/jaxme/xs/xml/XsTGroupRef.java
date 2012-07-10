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
 * <pre></p>
 * <p><em>Implementation note:</em> This interface does not define
 * any additional methods. However, the <code>validate()</code>
 * method must ensure that the 'ref' attribute is set and the
 * 'name' attribute is not set.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface XsTGroupRef extends XsTRealGroup, XsTTypeDefParticle, XsTNestedParticle {
}
