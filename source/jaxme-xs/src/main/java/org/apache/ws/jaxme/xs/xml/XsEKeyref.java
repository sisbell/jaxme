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

/** <p>Interface of the <code>xs:keyref</code> element, with the
 * following specification:
 * <pre>
 *  <xs:element name="keyref" id="keyref">
 *    <xs:annotation>
 *      <xs:documentation source="http://www.w3.org/TR/xmlschema-1/#element-keyref"/>
 *    </xs:annotation>
 *    <xs:complexType>
 *      <xs:complexContent>
 *        <xs:extension base="xs:keybase">
 *          <xs:attribute name="refer" type="xs:QName" use="required"/>
 *        </xs:extension>
 *      </xs:complexContent>
 *    </xs:complexType>
 *  </xs:element>
 * </pre></p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface XsEKeyref extends XsTKeybase, XsTIdentityConstraint {
  public void setRefer(XsQName pRefer);
  public XsQName getRefer();
}
