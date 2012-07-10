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


/** <p>Interface of the following type:
 * <pre>
 *   &lt;xs:complexType name="all"&gt;
 *     &lt;xs:annotation&gt;
 *       &lt;xs:documentation&gt;
 *         Only elements allowed inside
 *       &lt;/xs:documentation&gt;
 *     &lt;/xs:annotation&gt;
 *     &lt;xs:complexContent&gt;
 *       &lt;xs:restriction base="xs:explicitGroup"&gt;
 *         &lt;xs:group ref="xs:allModel"/&gt;
 *         &lt;xs:attribute name="minOccurs" use="optional" default="1"&gt;
 *           &lt;xs:simpleType&gt;
 *             &lt;xs:restriction base="xs:nonNegativeInteger"&gt;
 *               &lt;xs:enumeration value="0"/&gt;
 *               &lt;xs:enumeration value="1"/&gt;
 *             &lt;/xs:restriction&gt;
 *           &lt;/xs:simpleType&gt;
 *         &lt;/xs:attribute&gt;
 *         &lt;xs:attribute name="maxOccurs" use="optional" default="1"&gt;
 *           &lt;xs:simpleType&gt;
 *             &lt;xs:restriction base="xs:allNNI"&gt;
 *               &lt;xs:enumeration value="1"/&gt;
 *             &lt;/xs:restriction&gt;
 *           &lt;/xs:simpleType&gt;
 *         &lt;/xs:attribute&gt;
 *       &lt;/xs:restriction&gt;
 *     &lt;/xs:complexContent&gt;
 *   &lt;/xs:complexType&gt;
 * </pre></p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface XsTAll extends XsTExplicitGroup, XsTTypeDefParticle {
}
