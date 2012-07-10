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


/** <p>Interface of the xs:simpleExplicitGroup type, with the
 * following specification:
 * <pre>
 *  &lt;xs:complexType name="simpleExplicitGroup"&gt;
 *    &lt;xs:complexContent&gt;
 *      &lt;xs:restriction base="xs:explicitGroup"&gt;
 *        &lt;xs:sequence&gt;
 *          &lt;xs:element ref="xs:annotation" minOccurs="0"/&gt;
 *          &lt;xs:group ref="xs:nestedParticle" minOccurs="0" maxOccurs="unbounded"/&gt;
 *        &lt;/xs:sequence&gt;
 *        &lt;xs:attribute name="minOccurs" use="prohibited"/&gt;
 *        &lt;xs:attribute name="maxOccurs" use="prohibited"/&gt;
 *      &lt;/xs:restriction&gt;
 *    &lt;/xs:complexContent&gt;
 *  &lt;/xs:complexType&gt;
 * </pre></p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface XsTSimpleExplicitGroup extends XsTAnnotated {
  public XsTLocalElement createElement();
  public XsTGroupRef createGroup();
  public XsEChoice createChoice();
  public XsESequence createSequence();
  public XsEAny createAny();
  public XsTNestedParticle[] getParticles();
}
