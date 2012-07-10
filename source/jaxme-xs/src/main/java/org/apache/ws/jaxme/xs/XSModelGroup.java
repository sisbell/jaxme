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
import org.xml.sax.SAXException;


/** <p>Interface of a model group. A model group is, for example,
 * defined by an <code>xs:group</code> element, or by a complex type
 * without simpleContent or complexContent.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface XSModelGroup {
  public class Compositor {
    private String name;
    Compositor(String pName) {
      name = pName;
    }
    public String toString() { return name; }
    public boolean equals(Object o) {
      return o != null  &&  o instanceof Compositor  &&  ((Compositor) o).name.equals(name);
    }
    public int hashCode() { return name.hashCode(); }
  }

  /** <p>The sequence compositor.</p>
   */
  public static final Compositor SEQUENCE = new Compositor("sequence");

  /** <p>The choice compositor.</p>
   */
  public static final Compositor CHOICE = new Compositor("choice");

  /** <p>The all compositor.</p>
   */
  public static final Compositor ALL = new Compositor("all");

  /** <p>Returns the model groups compositor.</p>
   */
  public Compositor getCompositor();

  /** <p>Shortcut for <code>getCompositor().equals(SEQUENCE)</code>.</p>
   * @see #getCompositor()
   * @see #SEQUENCE
   */
  public boolean isSequence();

  /** <p>Shortcut for <code>getCompositor().equals(CHOICE)</code>.</p>
   * @see #getCompositor()
   * @see #CHOICE
   */
  public boolean isChoice();

  /** <p>Shortcut for <code>getCompositor().equals(ALL)</code>.</p>
   * @see #getCompositor()
   * @see #ALL
   */
  public boolean isAll();

  /** <p>Returns the model groups particles.</p>
   */
  public XSParticle[] getParticles();

  /** <p>Returns the model groups locator.</p>
   */
  public Locator getLocator();

  /** <p>Validates the particles contents.</p>
   */
  public void validate() throws SAXException;
}
