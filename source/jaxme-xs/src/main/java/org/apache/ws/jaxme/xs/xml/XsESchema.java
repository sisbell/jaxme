/*
 * Copyright 2003,2004  The Apache Software Foundation
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

import org.apache.ws.jaxme.xs.parser.XSContext;


/** <p>Implementation of xs:schema. Follows this specification:
 * <pre>
 *  &lt;xs:element name="schema" id="schema"&gt;
 *    &lt;xs:annotation&gt;
 *      &lt;xs:documentation source="http://www.w3.org/TR/xmlschema-1/#element-schema"/&gt;
 *    &lt;/xs:annotation&gt;
 *    &lt;xs:complexType&gt;
 *      &lt;xs:complexContent&gt;
 *        &lt;xs:extension base="xs:openAttrs"&gt;
 *          &lt;xs:sequence&gt;
 *            &lt;xs:choice minOccurs="0" maxOccurs="unbounded"&gt;
 *              &lt;xs:element ref="xs:include"/&gt;
 *              &lt;xs:element ref="xs:import"/&gt;
 *              &lt;xs:element ref="xs:redefine"/&gt;
 *              &lt;xs:element ref="xs:annotation"/&gt;
 *            &lt;/xs:choice&gt;
 *            &lt;xs:sequence minOccurs="0" maxOccurs="unbounded"&gt;
 *              &lt;xs:group ref="xs:schemaTop"/&gt;
 *              &lt;xs:element ref="xs:annotation" minOccurs="0" maxOccurs="unbounded"/&gt;
 *            &lt;/xs:sequence&gt;
 *          &lt;/xs:sequence&gt;
 *          &lt;xs:attribute name="targetNamespace" type="xs:anyURI"/&gt;
 *          &lt;xs:attribute name="version" type="xs:token"/&gt;
 *          &lt;xs:attribute name="finalDefault" type="xs:derivationSet" use="optional" default=""/&gt;
 *          &lt;xs:attribute name="blockDefault" type="xs:blockSet" use="optional" default=""/&gt;
 *          &lt;xs:attribute name="attributeFormDefault" type="xs:formChoice" use="optional" default="unqualified"/&gt;
 *          &lt;xs:attribute name="elementFormDefault" type="xs:formChoice" use="optional" default="unqualified"/&gt;
 *          &lt;xs:attribute name="id" type="xs:ID"/&gt;
 *          &lt;xs:attribute ref="xml:lang"/&gt;
 *        &lt;/xs:extension&gt;
 *      &lt;/xs:complexContent&gt;
 *    &lt;/xs:complexType&gt;
 *
 *    &lt;xs:key name="element"&gt;
 *      &lt;xs:selector xpath="xs:element"/&gt;
 *      &lt;xs:field xpath="@name"/&gt;
 *    &lt;/xs:key&gt;
 *    &lt;xs:key name="attribute"&gt;
 *      &lt;xs:selector xpath="xs:attribute"/&gt;
 *      &lt;xs:field xpath="@name"/&gt;
 *    &lt;/xs:key&gt;
 *    &lt;xs:key name="type"&gt;
 *      &lt;xs:selector xpath="xs:complexType|xs:simpleType"/&gt;
 *      &lt;xs:field xpath="@name"/&gt;
 *    &lt;/xs:key&gt;
 *    &lt;xs:key name="group"&gt;
 *      &lt;xs:selector xpath="xs:group"/&gt;
 *      &lt;xs:field xpath="@name"/&gt;
 *    &lt;/xs:key&gt;
 *    &lt;xs:key name="attributeGroup"&gt;
 *      &lt;xs:selector xpath="xs:attributeGroup"/&gt;
 *      &lt;xs:field xpath="@name"/&gt;
 *    &lt;/xs:key&gt;
 *    &lt;xs:key name="notation"&gt;
 *      &lt;xs:selector xpath="xs:notation"/&gt;
 *      &lt;xs:field xpath="@name"/&gt;
 *    &lt;/xs:key&gt;
 *    &lt;xs:key name="identityConstraint"&gt;
 *      &lt;xs:selector xpath=".//xs:key|.//xs:unique|.//xs:keyref"/&gt;
 *      &lt;xs:field xpath="@name"/&gt;
 *    &lt;/xs:key&gt;
 *  &lt;/xs:element&gt;
 *
 *  &lt;xs:group name="schemaTop"&gt;
 *    &lt;xs:annotation&gt;
 *      &lt;xs:documentation&gt;
 *        This group is for the
 *        elements which occur freely at the top level of schemas.
 *        All of their types are based on the "annotated" type by extension.
 *      &lt;/xs:documentation&gt;
 *    &lt;/xs:annotation&gt;
 *    &lt;xs:choice&gt;
 *      &lt;xs:group ref="xs:redefinable"/&gt;
 *      &lt;xs:element ref="xs:element"/&gt;
 *      &lt;xs:element ref="xs:attribute"/&gt;
 *      &lt;xs:element ref="xs:notation"/&gt;
 *    &lt;/xs:choice&gt;
 *  &lt;/xs:group&gt;
 * </pre>
 * </p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface XsESchema extends XsTOpenAttrs, XsSchemaHeader {
  public XsEInclude createInclude();

  public XsEImport createImport();

  public XsERedefine createRedefine();

  public XsEAnnotation createAnnotation();

  public XsETopLevelSimpleType createSimpleType();

  public XsTComplexType createComplexType();

  public XsTNamedGroup createGroup();

  public XsTAttributeGroup createAttributeGroup();

  public XsTTopLevelElement createElement();

  public XsTAttribute createAttribute();

  public XsENotation createNotation();

  /** <p>Returns the schema's childs. These are instances of
   * {@link XsEInclude}, {@link XsEImport}, {@link XsERedefine},
   * {@link XsEAnnotation}, {@link XsETopLevelSimpleType},
   * {@link XsTComplexType}, {@link XsTGroup},
   * {@link XsTAttributeGroup}, {@link XsTTopLevelElement}, or
   * {@link XsENotation}, in the order of the document. This
   * order is the same order than by invocation of the
   * corresponding {@link #createInclude()}, {@link #createImport()},
   * ... method calls.</p>
   * <p>Be aware, that a subclass of XsESchema may very well include
   * other objects.</p>
   */
  public Object[] getChilds();

  public void setTargetNamespace(XsAnyURI pAnyURI);
  public void setVersion(XsToken pToken);
  public void setId(XsID pId);
  public void setFinalDefault(XsDerivationSet pSet);
  public void setElementFormDefault(XsFormChoice pChoice);
  public void setBlockDefault(XsBlockSet pSet);
  public void setAttributeFormDefault(XsFormChoice pChoice);
  
  /** <p>Returns the schema context.</p>
   */
  public XSContext getContext();
}
