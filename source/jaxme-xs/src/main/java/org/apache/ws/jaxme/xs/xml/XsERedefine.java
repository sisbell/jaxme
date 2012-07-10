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


/** <p>Implementation of <code>xs:redefine</code>, as specified
 * by the following:
 * <pre>
 *  &lt;xs:element name="redefine" id="redefine"&gt;
 *    &lt;xs:annotation&gt;
 *      &lt;xs:documentation source="http://www.w3.org/TR/xmlschema-1/#element-redefine"/&gt;
 *    &lt;/xs:annotation&gt;
 *    &lt;xs:complexType&gt;
 *      &lt;xs:complexContent&gt;
 *        &lt;xs:extension base="xs:openAttrs"&gt;
 *          &lt;xs:choice minOccurs="0" maxOccurs="unbounded"&gt;
 *            &lt;xs:element ref="xs:annotation"/&gt;
 *            &lt;xs:group ref="xs:redefinable"/&gt;
 *          &lt;/xs:choice&gt;
 *          &lt;xs:attribute name="schemaLocation" type="xs:anyURI" use="required"/&gt;
 *          &lt;xs:attribute name="id" type="xs:ID"/&gt;
 *        &lt;/xs:extension&gt;
 *      &lt;/xs:complexContent&gt;
 *    &lt;/xs:complexType&gt;
 *  &lt;/xs:element&gt;
 * </pre></p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface XsERedefine extends XsTOpenAttrs {
  public void setId(XsID pID);
  public XsID getId();

  public void setSchemaLocation(XsAnyURI pSchemaLocation);
  public XsAnyURI getSchemaLocation();

  public XsEAnnotation createAnnotation();

  public XsETopLevelSimpleType createSimpleType();

  public XsTComplexType createComplexType();

  public XsTGroup createGroup();

  public XsTAttributeGroup createAttributeGroup();

  /** <p>Returns the child elemens. Any element in the object array is an instance of
   * {@link XsETopLevelSimpleType}, {@link XsTComplexType}, {@link XsTGroup}, or
   * {@link XsTAttributeGroup}.</p>
   */
  public Object[] getChilds();
}
