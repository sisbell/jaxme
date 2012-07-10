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

/** <p>A common base interface for all other schema objects.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface XSObject {
  /** <p>Returns the objects schema.</p>
   */
  public XSSchema getXSSchema();

  /** <p>Returns whether the object is a top-level object. This is
   * the case for the XsESchema itself and for all its childs only.</p>
   */
  public boolean isTopLevelObject();

  /** <p>Returns either of the following:
   * <ul>
   *   <li>If the object is the schema itself, returns null. The
   *     schema doesn't have a parent object.</p>
   *   <li>If the object is a top-level object, returns the
   *     schema.</p>
   *   <li>Otherwise returns the object in which the given object
   *     is embedded.</li>
   * </ul>
   */
  public XSObject getParentObject();

  /** <p>Returns the objects location.</p>
   */
  public Locator getLocator();

  /** <p>Validates the objects internal state.</p>
   */
  public void validate() throws SAXException;
}
