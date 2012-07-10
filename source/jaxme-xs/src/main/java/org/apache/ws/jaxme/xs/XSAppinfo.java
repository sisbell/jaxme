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

import org.apache.ws.jaxme.xs.xml.XsAnyURI;


/** <p>Interface of an appinfo element.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface XSAppinfo extends XSObject {
  /** <p>Returns the appinfos source element or null,
   * if no such attribute is defined.</p>
   */
  public XsAnyURI getSource();

  /** <p>Returns the appinfos content. The objects in the
   * array are either of:
   * <ul>
   *   <li>A String, indicating character data, or</li>
   *   <li>A DOM document containing a single child element.</li>
   * </ul>
   * @see #getText()
   */
  public Object[] getChilds();

  /** <p>Merges the String elements returned by {@link #getChilds()}
   * into a single String.</p>
   * @see #getChilds()
   */
  public String getText();
}
