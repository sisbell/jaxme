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
package org.apache.ws.jaxme.xs;

import java.util.Map;

import org.apache.ws.jaxme.xs.parser.XSContext;
import org.apache.ws.jaxme.xs.xml.XsAnyURI;
import org.apache.ws.jaxme.xs.xml.XsQName;
import org.xml.sax.SAXException;

/** <p>This interface is what you are probably most interested in: The
 * logical XML Schema representation.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface XSSchema extends XSOpenAttrs {
  /** <p>Returns the schemas object factory.</p>
   */
  public XSObjectFactory getXSObjectFactory();

  /** <p>Returns the schemas context.</p>
   */
  public XSContext getContext();

  /** <p>Returns the schemas target namespace. Note, that a
   * {@link XSSchema logical schema} can combine elements,
   * attributes, groups, and types of various namespaces by
   * importing other {@link org.apache.ws.jaxme.xs.xml.XsESchema
   * syntactical schemas} with different namespaces. Thus
   * the logical schemas target namespace is in fact the
   * target namespace of the outermost syntactical schema.</p>
   * @return Target namespace or null for null (default namespace)
   */
  public XsAnyURI getTargetNamespace();

  /** <p>Returns the array of annotations.</p>
   */
  public XSAnnotation[] getAnnotations();

  /** <p>Returns the array of global types. This includes simple
   * and complex types. The builtin types are not included.</p>
   * @see #getType(XsQName)
   */
  public XSType[] getTypes();

  /** <p>Returns the array of builtin types. This includes simple
   * and complex types.</p>
   * @see #getTypes()
   */
  public XSType[] getBuiltinTypes();

  /** <p>Returns the type with the given name. This may be a builtin
   * type or a type defined by the schema.</p>
   */
  public XSType getType(XsQName pName);

  /** <p>Returns the array of global groups.</p>
   */
  public XSGroup[] getGroups();

  /** <p>Returns the group with the given name.</p>
   */
  public XSGroup getGroup(XsQName pName);

  /** <p>Returns the array of global attribute groups.</p>
   */
  public XSAttributeGroup[] getAttributeGroups();

  /** <p>Returns the attribute group with the given name.</p>
   */
  public XSAttributeGroup getAttributeGroup(XsQName pName);

  /** <p>Returns the array of global elements.</p>
   */
  public XSElement[] getElements();

  /** <p>Returns the element with the given name.</p>
   */
  public XSElement getElement(XsQName pName);

  /** <p>Returns the array of global attributes.</p>
   */
  public XSAttribute[] getAttributes();

  /** <p>Returns the attribute with the given name.</p>
   */
  public XSAttribute getAttribute(XsQName pName);

  /** <p>Returns a map of XSIdentityConstraint objects.
   * The key is the constraints name. The map is immutable.</p>
   */
  public Map getIdentityConstraints();

  /** <p>Returns a map of XSKeyRef objects. The key is the
   * key refs name. The map is immutable.</p>
   * */
  public Map getKeyRefs();

  /** <p>Returns all the schema annotations, types, groups,
   * attribute groups, elements, and attributes, in the
   * order of declaration.</p>
   */
  public Object[] getChilds();

  /** <p>Adds a new annotation to the schema.</p>
   */
  public void add(XSAnnotation pAnnotation);

  /** <p>Adds a new type to the schema.</p>
   */
  public void add(XSType pType) throws SAXException;

  /** <p>Redefines an existing type in the schema.</p>
   */
  public void redefine(XSType pType) throws SAXException;

  /** <p>Adds a new group to the schema.</p>
   */
  public void add(XSGroup pGroup) throws SAXException;

  /** <p>Redefines an existing group in the schema.</p>
   */
  public void redefine(XSGroup pGroup) throws SAXException;

  /** <p>Adds a new attribute group to the schema.</p>
   */
  public void add(XSAttributeGroup pGroup) throws SAXException;

  /** <p>Redefines an existing attribute group in the schema.</p>
   */
  public void redefine(XSAttributeGroup pGroup) throws SAXException;

  /** <p>Adds a new attribute to the schema.</p>
   */
  public void add(XSAttribute pAttribute) throws SAXException;

  /** <p>Adds a new element to the schema.</p>
   */
  public void add(XSElement pElement) throws SAXException;

  /** <p>Adds a new notation to the schema.</p>
   */
  public void add(XSNotation pNotation) throws SAXException;

  /** <p>Adds a new identity constraint to the schema.</p>
   */
  public void add( XSIdentityConstraint ic ) throws SAXException;

  /** <p>Adds a new key ref to the schema.</p>
   */
  public void add( XSKeyRef rf ) throws SAXException;
}
