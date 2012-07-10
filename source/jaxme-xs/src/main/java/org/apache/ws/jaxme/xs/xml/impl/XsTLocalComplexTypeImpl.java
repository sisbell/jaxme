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


/** <p>Interface of a local <code>xs:complexType</code>,
 * following the specification below:
 * <pre>
 *  &lt;xs:complexType name="localComplexType"&gt;
 *    &lt;xs:complexContent&gt;
 *      &lt;xs:restriction base="xs:complexType"&gt;
 *        &lt;xs:sequence&gt;
 *          &lt;xs:element ref="xs:annotation" minOccurs="0"/&gt;
 *          &lt;xs:group ref="xs:complexTypeModel"/&gt;
 *        &lt;/xs:sequence&gt;
 *        &lt;xs:attribute name="name" use="prohibited"/&gt;
 *        &lt;xs:attribute name="abstract" use="prohibited"/&gt;
 *        &lt;xs:attribute name="final" use="prohibited"/&gt;
 *        &lt;xs:attribute name="block" use="prohibited"/&gt;
 *      &lt;/xs:restriction&gt;
 *    &lt;/xs:complexContent&gt;
 *  &lt;/xs:complexType&gt;
 * </pre></p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XsTLocalComplexTypeImpl  extends XsTComplexTypeImpl implements XsTLocalComplexType {
  protected XsTLocalComplexTypeImpl(XsObject pParent) {
    super(pParent);
  }
}
