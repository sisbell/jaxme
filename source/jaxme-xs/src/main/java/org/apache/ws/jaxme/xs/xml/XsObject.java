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

import org.xml.sax.Locator;
import org.xml.sax.SAXException;


/** Base interface for type, attribute, element, or whatever object.
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface XsObject {
  /** Returns the objects syntactical schema.
   */
  public XsESchema getXsESchema();

  /** Returns whether the object is a top-level object. This is
   * the case for the XsESchema itself and for all its childs only.
   */
  public boolean isTopLevelObject();

  /** Returns information on the parent object.<br>
   * More precisely, returns either of the following:
   * <ul>
   *   <li>If the object is the schema itself, returns null. The
   *     schema doesn't have a parent object.</p>
   *   <li>If the object is a top-level object, returns the
   *     schema.</p>
   *   <li>Otherwise returns the object in which the given object
   *     is embedded.</li>
   * </ul>
   */
  public XsObject getParentObject();

  /** Returns the {@link XsObjectFactory object factory}, that
   * created this instance.
   */ 
  public XsObjectFactory getObjectFactory();

  /** Returns the SAX {@link Locator} with the instances
   * location.
   */
  public Locator getLocator();

  /** Validates the internal state of the type, attribute, element,
   * or whatever object.
   */
  public void validate() throws SAXException;
}
