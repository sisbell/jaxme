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

import org.apache.ws.jaxme.xs.xml.*;


/** <p>Details of a complex type.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface XSComplexType {
  /** <p>Returns whether the complex type has simple content. If so,
   * you may use the method {@link #getSimpleContent()} to fetch the
   * simple contents type. If not, you may use
   * {@link #getComplexContentType()}, {@link #isEmpty()},
   * {@link #isElementOnly()}, {@link #isMixed()}, and
   * {@link #getParticle()}. The method {@link #getAttributes()}
   * is always valid.</p>
   */
  public boolean hasSimpleContent();

  /** <p>If the complex type has simple content, returns the contents
   * type. This is, of course, a simple type.</p>
   */
  public XSSimpleContentType getSimpleContent();

  /** <p>If the element hasn't simple content: Returns the element contents
   * type.</p>
   * @throws IllegalStateException The element does not have
   *   complex content
   * @see XsComplexContentType#EMPTY
   * @see XsComplexContentType#ELEMENT_ONLY
   * @see XsComplexContentType#MIXED
   * @see #isEmpty()
   * @see #isElementOnly()
   * @see #isMixed()
   */
  public XsComplexContentType getComplexContentType();

  /** <p>If the element hasn't simple content: Returns whether the
   * elements content is empty. Shortcut for
   * <code>getComplexContentType().equals(EMPTY)</code>.</p>
   * @throws IllegalStateException The element does not have
   *   complex content
   * @see XsComplexContentType#EMPTY
   */
  public boolean isEmpty();

  /** <p>If the element hasn't simple content: Returns whether the
   * elements content is elementOnly. Shortcut for
   * <code>getComplexContentType().equals(ELEMENT_ONLY)</code>.</p>
   * @throws IllegalStateException The element does not have
   *   complex content
   * @see XsComplexContentType#ELEMENT_ONLY
   */
  public boolean isElementOnly();

  /** <p>If the element hasn't simple content: Returns whether the
   * elements content is mixed. Shortcut for
   * <code>getComplexContentType().equals(MIXED)</code>.</p>
   * @throws IllegalStateException The element does not have
   *   complex content
   * @see XsComplexContentType#MIXED
   */
  public boolean isMixed();

  /** <p>If the element hasn't simple content: Returns the complex
   * elements particle.</p>
   * @throws IllegalStateException The element has simple content
   */
  public XSParticle getParticle();

  /** <p>Returns the complex types attributes.</p>
   */
  public XSAttributable[] getAttributes();

  /** <p>Returns whether the element is a extension of another element.</p>
   */
  public boolean isExtension();

  /** <p>If the element is an extension: Returns the extended type.</p>
   * @throws IllegalStateException The element is no extension.
   */
  public XSType getExtendedType();

  /** <p>Returns whether the element is a restriction of another element.</p>
   */
  public boolean isRestriction();

  /** <p>If the element is an restriction: Returns the restricted type.</p>
   * @throws IllegalStateException The element is no restriction.
   */
  public XSType getRestrictedType();
}
