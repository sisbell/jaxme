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


/** <p>Interface of <code>xs:attrDecls</code>, with the
 * following specification:
 * <pre>
 *  &lt;xs:group name="attrDecls"&gt;
 *    &lt;xs:sequence&gt;
 *      &lt;xs:choice minOccurs="0" maxOccurs="unbounded"&gt;
 *        &lt;xs:element name="attribute" type="xs:attribute"/&gt;
 *        &lt;xs:element name="attributeGroup" type="xs:attributeGroupRef"/&gt;
 *      &lt;/xs:choice&gt;
 *      &lt;xs:element ref="xs:anyAttribute" minOccurs="0"/&gt;
 *    &lt;/xs:sequence&gt;
 *  &lt;/xs:group&gt;
 * </pre></p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface XsGAttrDecls {
  /** <p>Creates a new instance of {@link XsTAttribute}.</p>
   */
  public XsTAttribute createAttribute();

  /** <p>Returns an array of all attributes created by
   * {@link #createAttribute()}.</p>
   */
  public XsTAttribute[] getAttributes();

  /** <p>Creates a new instance of {@link XsTAttributeGroup}.</p>
   */
  public XsTAttributeGroupRef createAttributeGroup();

  /** <p>Returns an array of all attribute groups created by
   * {@link #createAttributeGroup()};
   */
  public XsTAttributeGroupRef[] getAttributeGroups();

  /** <p>Creates a new instance of {@link XsTWildcard}.</p>
   */
  public XsTWildcard createAnyAttribute();

  /** <p>Returns the instance created by {@link #createAnyAttribute()},
   * or null, if the method wasn't called.</p>
   */
  public XsTWildcard getAnyAttribute();

  /** <p>Returns all objects created by {@link #createAttribute()},
   * {@link #createAttributeGroup()}, or {@link #createAnyAttribute()},
   * in the order of the corresponding method calls.</p>
   */
  public Object[] getAllAttributes();
}
