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


/** <p>Interface of <code>xs:union</code>, following the
 * specification below:
 * <pre>
 *  &lt;xs:element name="union" id="union"&gt;
 *    &lt;xs:complexType&gt;
 *      &lt;xs:annotation&gt;
 *        &lt;xs:documentation
 *            source="http://www.w3.org/TR/xmlschema-2/#element-union"&gt;
 *          memberTypes attribute must be non-empty or there must be
 *          at least one simpleType child
 *        &lt;/xs:documentation&gt;
 *      &lt;/xs:annotation&gt;
 *      &lt;xs:complexContent&gt;
 *        &lt;xs:extension base="xs:annotated"&gt;
 *          &lt;xs:sequence&gt;
 *            &lt;xs:element name="simpleType" type="xs:localSimpleType"
 *                minOccurs="0" maxOccurs="unbounded"/&gt;
 *          &lt;/xs:sequence&gt;
 *          &lt;xs:attribute name="memberTypes" use="optional"&gt;
 *            &lt;xs:simpleType&gt;
 *              &lt;xs:list itemType="xs:QName"/&gt;
 *            &lt;/xs:simpleType&gt;
 *          &lt;/xs:attribute&gt;
 *        &lt;/xs:extension&gt;
 *      &lt;/xs:complexContent&gt;
 *    &lt;/xs:complexType&gt;
 *  &lt;/xs:element&gt;
 * </pre></p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface XsEUnion extends XsTAnnotated {
  /** <p>Creates a new, local simple type as a member type of the union.</p>
   */
  public XsTLocalSimpleType createSimpleType();

  /** <p>Returns an array of member types which have been
   * created using {@link #createSimpleType()}.</p>
   */
  public XsTLocalSimpleType[] getSimpleTypes();

  /** <p>Sets the qualified names of simple types being used as member
   * types of the union.</p>
   */
  public void setMemberTypes(XsQName[] pTypes);

  /** <p>Returns an array of member types which have been added
   * using {@link #setMemberTypes(XsQName[])}. This array may be null,
   * if the method {@link #setMemberTypes(XsQName[])} wasn't invoked
   * at all, or it may be the empty array, if an empty string was
   * passed as argument to the method.</p>
   */
  public XsQName[] getMemberTypes();
}
