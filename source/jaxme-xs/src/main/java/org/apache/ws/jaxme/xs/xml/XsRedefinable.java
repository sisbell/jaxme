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

/** <p>A common base interface for {@link org.apache.ws.jaxme.xs.xml.XsETopLevelSimpleType},
 * {@link org.apache.ws.jaxme.xs.xml.XsTComplexType}, {@link org.apache.ws.jaxme.xs.xml.XsTGroup},
 * and {@link org.apache.ws.jaxme.xs.xml.XsTAttributeGroup}.
 * <pre>
 *  &lt;xs:group name="redefinable"&gt;
 *    &lt;xs:annotation&gt;
 *      &lt;xs:documentation&gt;
 *        This group is for the elements which can self-redefine
 *        (see &lt;redefine&gt; below).
 *      &lt;/xs:documentation&gt;
 *    &lt;/xs:annotation&gt;
 *    &lt;xs:choice&gt;
 *      &lt;xs:element ref="xs:simpleType"/&gt;
 *      &lt;xs:element ref="xs:complexType"/&gt;
 *      &lt;xs:element ref="xs:group"/&gt;
 *      &lt;xs:element ref="xs:attributeGroup"/&gt;
 *    &lt;/xs:choice&gt;
 *  &lt;/xs:group&gt;
 * </pre></p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface XsRedefinable {

}
