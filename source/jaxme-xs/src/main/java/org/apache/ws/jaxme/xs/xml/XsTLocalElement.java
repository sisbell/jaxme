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

/** <p>Interface of the <code>xs:localElement</code> type, as
 * specified by:
 * <pre>
 *  <xs:complexType name="localElement">
 *    <xs:complexContent>
 *      <xs:restriction base="xs:element">
 *        <xs:sequence>
 *          <xs:element ref="xs:annotation" minOccurs="0"/>
 *          <xs:choice minOccurs="0">
 *            <xs:element name="simpleType" type="xs:localSimpleType"/>
 *            <xs:element name="complexType" type="xs:localComplexType"/>
 *          </xs:choice>
 *          <xs:group ref="xs:identityConstraint" minOccurs="0" maxOccurs="unbounded"/>
 *        </xs:sequence>
 *        <xs:attribute name="substitutionGroup" use="prohibited"/>
 *        <xs:attribute name="final" use="prohibited"/>
 *        <xs:attribute name="abstract" use="prohibited"/>
 *      </xs:restriction>
 *    </xs:complexContent>
 *  </xs:complexType>
 * </pre></p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface XsTLocalElement extends XsTElement, XsTNestedParticle {
}
