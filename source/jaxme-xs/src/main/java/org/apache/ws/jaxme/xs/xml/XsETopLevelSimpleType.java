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

/** <p>Interface of <code>xs:simpleType>, as specified by the following:
 * <pre>
 *  &lt;xs:element name="simpleType" type="xs:topLevelSimpleType" id="simpleType"&gt;
 *    &lt;xs:annotation&gt;
 *      &lt;xs:documentation
 *        source="http://www.w3.org/TR/xmlschema-2/#element-simpleType"/&gt;
 *    &lt;/xs:annotation&gt;
 *  &lt;/xs:element&gt;
 *
 *  &lt;xs:complexType name="topLevelSimpleType"&gt;
 *    &lt;xs:complexContent&gt;
 *      &lt;xs:restriction base="xs:simpleType"&gt;
 *        &lt;xs:sequence&gt;
 *          &lt;xs:element ref="xs:annotation" minOccurs="0"/&gt;
 *          &lt;xs:group ref="xs:simpleDerivation"/&gt;
 *        &lt;/xs:sequence&gt;
 *        &lt;xs:attribute name="name" use="required" type="xs:NCName"&gt;
 *          &lt;xs:annotation&gt;
 *            &lt;xs:documentation&gt;
 *              Required at the top level
 *            &lt;/xs:documentation&gt;
 *          &lt;/xs:annotation&gt;
 *        &lt;/xs:attribute&gt;
 *      &lt;/xs:restriction&gt;
 *    &lt;/xs:complexContent&gt;
 *  &lt;/xs:complexType&gt;
 * </pre></p>
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface XsETopLevelSimpleType extends XsTSimpleType, XsRedefinable {
}
