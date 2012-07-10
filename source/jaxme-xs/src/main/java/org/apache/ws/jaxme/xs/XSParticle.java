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

import org.xml.sax.Locator;


/** <p>Interface of a model groups particle.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface XSParticle {
  public class Type {
    private final String name;
    public Type(String pName) {
      name = pName;
    }
    public String toString() { return name; }
    public boolean equals(Object o) {
      return o != null  &&  o instanceof Type  &&  ((Type) o).name.equals(name);
    }
    public int hashCode() { return name.hashCode(); }
  }

  /** <p>The particle type "group".</p>
   */
  public Type GROUP = new Type("group");

  /** <p>The particle type "wildcard".</p>
   */
  public Type WILDCARD = new Type("wildcard");

  /** <p>The particle type "element".</p>
   */
  public Type ELEMENT = new Type("element");

  /** <p>Returns the particle type.</p>
   */
  public Type getType();

  /** <p>Shortcut for <code>getType().equals(GROUP)</code>.</p>
   * @see #getType()
   * @see #GROUP
   */
  public boolean isGroup();

  /** <p>If the particle type is group: Returns the group.</p>
   * @throws IllegalStateException {@link #isGroup()} returns false
   */
  public XSGroup getGroup();

  /** <p>Shortcut for <code>getType().equals(WILDCARD)</code>.</p>
   * @see #getType()
   * @see #WILDCARD
   */
  public boolean isWildcard();

  /** <p>If the particle type is wildcard: Returns the wildcard.</p>
   * @throws IllegalStateException {@link #isWildcard()} returns false
   */
  public XSAny getWildcard();

  /** <p>Shortcut for <code>getType().equals(ELEMENT)</code>.</p>
   * @see #getType()
   * @see #ELEMENT
   */
  public boolean isElement();

  /** <p>If the particle type is element: Returns the element.</p>
   * @throws IllegalStateException {@link #isElement()} returns false
   */
  public XSElement getElement();

  /** <p>Returns the particles minOccurs value.</p>
   */
  public int getMinOccurs();

  /** <p>Returns the particles maxOccurs value or -1 for unbounded.</p>
   */
  public int getMaxOccurs();

  /** <p>Returns the particles Locator.</p>
   */
  public Locator getLocator();
}
