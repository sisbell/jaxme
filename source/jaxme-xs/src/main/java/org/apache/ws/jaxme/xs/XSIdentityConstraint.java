/*
 * Copyright 2004  The Apache Software Foundation
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



/** 
 * An identity constraint restricts the contents of an elements children.
 * An XSElement may have one or more constraints associated with it that
 * restrict the values that can be stored in an XML file that is described
 * by this schema. For more information read into xs:key, xs:key-ref and
 * xs:unique.
 *
 * @see XSElement
 *
 * @author <a href="mailto:mrck1996@yahoo.co.uk">Chris Kirk</a>
 */
public interface XSIdentityConstraint extends XSOpenAttrs {
  /** 
   * Returns the array of annotations.
   */
  public XSAnnotation[] getAnnotations();

  /** 
   * Returns the name of this constraint.
   */
  public String getName();

  /**
   * Return true if every element of the key is required when matching a
   * node.
   */
  public boolean isUnique();

  /**
   * Returns an array of references to element and attributes. All references
   * are relative to the element that owns this constraint. <p>
   *
   * The result is a two dimensional array, the first dimension corresponds to
   * each xs:field used to declare the constraint. The second dimension is
   * for each 'or' used within the fields xpath query. <p>
   *
   * Only tags and attributes that were matched by the xpath will be in the
   * result, any xpath that fails to match anything will not be stored
   * in this array.<p>
   */
  public XSElementOrAttrRef[][] getMatchCriteria();
}
