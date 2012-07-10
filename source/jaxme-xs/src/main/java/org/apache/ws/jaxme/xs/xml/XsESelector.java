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

/** <p>Interface of the <code>xs:selector</code> element, specified
 * by the following:
 * <pre>
 *   <xs:element name="selector" id="selector">
 *     <xs:annotation>
 *       <xs:documentation source="http://www.w3.org/TR/xmlschema-1/#element-selector"/>
 *     </xs:annotation>
 *     <xs:complexType>
 *       <xs:complexContent>
 *         <xs:extension base="xs:annotated">
 *           <xs:attribute name="xpath" use="required">
 *             <xs:simpleType>
 *               <xs:annotation>
 *                 <xs:documentation>
 *                   A subset of XPath expressions for use
 *                   in selectors
 *                 </xs:documentation>
 *                 <xs:documentation>
 *                   A utility type, not for public use
 *                 </xs:documentation>
 *               </xs:annotation>
 *               <xs:restriction base="xs:token">
 *                 <xs:annotation>
 *                   <xs:documentation>
 *                     The following pattern is intended to allow XPath
 *                     expressions per the following EBNF:
 *                       Selector    ::=    Path ( '|' Path )*
 *                       Path        ::=    ('.//')? Step ( '/' Step )*
 *                       Step    ::=    '.' | NameTest
 *                       NameTest    ::=    QName | '*' | NCName ':' '*'
 *                                          child:: is also allowed
 *                   </xs:documentation>
 *                 </xs:annotation>
 *                 <xs:pattern value="(\.//)?(((child::)?((\i\c*:)?(\i\c*|\*)))|\.)(/(((child::)?((\i\c*:)?(\i\c*|\*)))|\.))*(\|(\.//)?(((child::)?((\i\c*:)?(\i\c*|\*)))|\.)(/(((child::)?((\i\c*:)?(\i\c*|\*)))|\.))*)*">
 *                 </xs:pattern>
 *               </xs:restriction>
 *             </xs:simpleType>
 *           </xs:attribute>
 *         </xs:extension>
 *       </xs:complexContent>
 *     </xs:complexType>
 *   </xs:element>
 * </pre></p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface XsESelector extends XsTAnnotated {
  public void setXpath(XsToken pXpath);
  public XsToken getXpath();
}
