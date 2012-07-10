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

import org.apache.ws.jaxme.xs.xml.XsQName;


/** <p>Interface of a schema element</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface XSElement extends XSOpenAttrs {
  /** <p>Returns the array of annotations.</p>
   */
  public XSAnnotation[] getAnnotations();

  /** <p>Returns whether the element is global or not.</p>
   */
  public boolean isGlobal();
  
  /** <p>Returns whether the element is nillable or not.</p>
   */  
  public boolean isNillable();

  /** <p>Returns the elements name. Note that, unlike types,
   * an element always has a name.</p>
   */
  public XsQName getName();

  /** <p>Returns the elements type.</p>
   */
  public XSType getType();

  /** <p>Returns the elements "default" value or null, if no such
   * attribute is set.</p>
   */
  public String getDefault();

  /** <p>Returns the elements "fixed" value or null, if no such
   * attribute is set.</p>
   */
  public String getFixed();

  /** <p>Returns the name of an element, which may be substituted
   * by this element.</p>
   */
  public XsQName getSubstitutionGroupName();

  /** <p>Returns whether this element is blocked for substitution.</p>
   */
  public boolean isBlockedForSubstitution();

  /** <p>Returns whether the element is abstract or not.</p>
   */
  public boolean isAbstract();

  /** <p>If the element may be substituted by other elements (in other
   * words: if it is the head of a substitution group), sets the choice
   * group of all the elements that may be used to replace the element.
   * The element itself is part of the substitution group if, and only
   * if, the element is not abstract.</p>
   */
  public void setSubstitutionGroup(XSGroup pGroup);

  /** <p>If the element may be substituted by other elements (in other
   * words: if it is the head of a substitution group), returns the choice
   * group of all the elements that may be used to replace the element.
   * The element itself is part of the substitution group if, and only
   * if, the element is not abstract.</p>
   */
  public XSGroup getSubstitutionGroup();

  /**
   * Fetch all of the identity constraints that this element places
   * upon its child tags. Note that a single xs:unique or xs:key tag
   * may expand to several elements in this array depending on their
   * xpath fields.
   */
  public XSIdentityConstraint[] getIdentityConstraints();

  /**
   * Fetch all of the keyref's declared by this element.
   */
  public XSKeyRef[] getKeyRefs();

  /**
   * Returns, whether the element is created by referencing another element.
   */
  public boolean isReference();
}
