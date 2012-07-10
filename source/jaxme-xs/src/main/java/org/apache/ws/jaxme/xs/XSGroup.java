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


/** <p>Interface of a group.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface XSGroup extends XSOpenAttrs, XSModelGroup {
  /** <p>Returns the array of annotations.</p>
   */
  public XSAnnotation[] getAnnotations();

  /** <p>Sets whether the group is global or not.</p>
   */
  public void setGlobal(boolean pGlobal);

  /** <p>Returns whether the group is global or not.</p>
   */
  public boolean isGlobal();

  /** <p>If the group is global: Returns the groups name. Otherwise returns
   * null.</p>
   */
  public XsQName getName();
}
