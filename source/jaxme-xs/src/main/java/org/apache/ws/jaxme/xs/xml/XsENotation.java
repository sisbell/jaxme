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

/** <p>Interface of the <code>xs:notation</code> element, with
 * the following specification:
 * <pre>
 *  <xs:element name="notation" id="notation">
 *    <xs:annotation>
 *      <xs:documentation source="http://www.w3.org/TR/xmlschema-1/#element-notation"/>
 *    </xs:annotation>
 *    <xs:complexType>
 *      <xs:complexContent>
 *        <xs:extension base="xs:annotated">
 *          <xs:attribute name="name" type="xs:NCName" use="required"/>
 *          <xs:attribute name="public" type="xs:public" use="required"/>
 *          <xs:attribute name="system" type="xs:anyURI"/>
 *        </xs:extension>
 *      </xs:complexContent>
 *    </xs:complexType>
 *  </xs:element>
 * </pre></p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface XsENotation extends XsTAnnotated {
  public void setName(XsNCName pName);
  public XsNCName getName();

  public void setPublic(XsToken pPublic);
  public XsToken getPublic();

  public void setSystem(XsAnyURI pSystem);
  public XsAnyURI getSystem();
}
