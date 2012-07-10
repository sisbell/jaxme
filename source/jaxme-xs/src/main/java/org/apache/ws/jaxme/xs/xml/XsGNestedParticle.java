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

/** <p>Interface of the <code>xs:nestedParticle</code> group,
 * as specified by the following:
 * <pre>
 *  <xs:group name="nestedParticle">
 *    <xs:choice>
 *      <xs:element name="element" type="xs:localElement"/>
 *      <xs:element name="group" type="xs:groupRef"/>
 *      <xs:element ref="xs:choice"/>
 *      <xs:element ref="xs:sequence"/>
 *      <xs:element ref="xs:any"/>
 *    </xs:choice>
 *  </xs:group>
 * </pre></p>
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface XsGNestedParticle {
  public XsTLocalElement createElement();
  public XsTLocalElement getElement();

  public XsTGroupRef createGroupRef();
  public XsTGroupRef getGroupRef();

  public XsEChoice createChoice();
  public XsEChoice getChoice();

  public XsESequence createSequence();
  public XsESequence getSequence();
}
