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


/** <p>Interface of the <code>xs:field</code> element, with the
 * following specification:
 * <pre>
 *   <xs:element name="field" id="field">
 *     <xs:annotation>
 *       <xs:documentation source="http://www.w3.org/TR/xmlschema-1/#element-field"/>
 *     </xs:annotation>
 *     <xs:complexType>
 *       <xs:complexContent>
 *         <xs:extension base="xs:annotated">
 *           <xs:attribute name="xpath" use="required">
 *             <xs:simpleType>
 *               <xs:annotation>
 *                 <xs:documentation>
 *                   A subset of XPath expressions for use in fields
 *                 </xs:documentation>
 *                 <xs:documentation>
 *                   A utility type, not for public use
 *                 </xs:documentation>
 *               </xs:annotation>
 *               <xs:restriction base="xs:token">
 *                 <xs:annotation>
 *                   <xs:documentation>
 *                     The following pattern is intended to allow XPath
 *                     expressions per the same EBNF as for selector,
 *                     with the following change:
 *                       Path    ::=    ('.//')? ( Step '/' )* ( Step | '@' NameTest ) 
 *                   </xs:documentation>
 *                 </xs:annotation>
 *                 <xs:pattern value="(\.//)?((((child::)?((\i\c*:)?(\i\c*|\*)))|\.)/)*((((child::)?((\i\c*:)?(\i\c*|\*)))|\.)|((attribute::|@)((\i\c*:)?(\i\c*|\*))))(\|(\.//)?((((child::)?((\i\c*:)?(\i\c*|\*)))|\.)/)*((((child::)?((\i\c*:)?(\i\c*|\*)))|\.)|((attribute::|@)((\i\c*:)?(\i\c*|\*)))))*">
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
public class XsEFieldImpl extends XsTAnnotatedImpl implements XsEField {
  private XsToken xpath;

  protected XsEFieldImpl(XsObject pParent) {
    super(pParent);
  }

  public void setXpath(XsToken pXpath) {
    xpath = pXpath;
  }

  public XsToken getXpath() {
    return xpath;
  }

  public void validate() {
    if (getXpath() == null) {
      throw new NullPointerException("Missing attribute: xpath");
    }
  }
}
