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
 * This reference represents a key used to look up other elements. It does
 * not infer any extra constraints other than if the match criteria exists
 * then the values matched must exist within the refered identity constraint.
 * For more information please refer to the xs:keyref tag.
 *
 * @see XSElement
 * @see XSIdentityConstraint
 *
 * @author <a href="mailto:mrck1996@yahoo.co.uk">Chris Kirk</a>
 */
public interface XSKeyRef extends XSOpenAttrs {
  /** 
   * Returns the array of annotations.
   */
  public XSAnnotation[] getAnnotations();

  /** 
   * Returns the name of this constraint.
   */
  public String getName();

  /**
   * Returns the name of the unique or key identity constraint that this
   * keyref references. The constraint must either be declared on the same
   * element as this keyref or a descendant.
   */
  public XSIdentityConstraint getIdentityConstraint();

  /**
   * Returns an array of references to element and attributes. All references
   * are relative to the element that declares this keyref.<p>
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
