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

/** <p>Interface of a named top-level group, with the following
 * specification:
 * <pre>
 *  &lt;xs:complexType name="namedGroup"&gt;
 *    &lt;xs:annotation&gt;
 *      &lt;xs:documentation&gt;
 *        Should derive this from realGroup, but too complicated for now
 *      &lt;/xs:documentation&gt;
 *    &lt;/xs:annotation&gt;
 *    &lt;xs:sequence&gt;
 *      &lt;xs:element ref="xs:annotation" minOccurs="0"/&gt;
 *      &lt;xs:choice minOccurs="1" maxOccurs="1"&gt;
 *        &lt;xs:element name="all"&gt;
 *          &lt;xs:complexType&gt;
 *            &lt;xs:complexContent&gt;
 *              &lt;xs:restriction base="xs:all"&gt;
 *                &lt;xs:group ref="xs:allModel"/&gt;
 *                &lt;xs:attribute name="minOccurs" use="prohibited"/&gt;
 *                &lt;xs:attribute name="maxOccurs" use="prohibited"/&gt;
 *              &lt;/xs:restriction&gt;
 *            &lt;/xs:complexContent&gt;
 *          &lt;/xs:complexType&gt;
 *        &lt;/xs:element&gt;
 *        &lt;xs:element name="choice" type="xs:simpleExplicitGroup"/&gt;
 *        &lt;xs:element name="sequence" type="xs:simpleExplicitGroup"/&gt;
 *      &lt;/xs:choice&gt;
 *    &lt;/xs:sequence&gt;
 *    &lt;xs:attribute name="name" use="required" type="xs:NCName"/&gt;
 *    &lt;xs:attribute name="ref" use="prohibited"/&gt;
 *    &lt;xs:attribute name="minOccurs" use="prohibited"/&gt;
 *    &lt;xs:attribute name="maxOccurs" use="prohibited"/&gt;
 *  &lt;/xs:complexType&gt;
 * &lt;/pre>&lt;/p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface XsTNamedGroup extends XsTAnnotated, XsRedefinable {
  public void setName(XsNCName pName);
  public XsNCName getName();
  public XsGAllModel createAll();
  public XsGAllModel getAll();
  public XsTSimpleExplicitGroup createSequence();
  public XsTSimpleExplicitGroup getSequence();
  public XsTSimpleExplicitGroup createChoice();
  public XsTSimpleExplicitGroup getChoice();
}
