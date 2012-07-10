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


/** <p>Interface of a schema attribute.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface XSAttribute extends XSOpenAttrs, XSAttributable {
  /** <p>Returns whether the attribute is global.</p>
   */
  public boolean isGlobal();

  /** <p>Returns the attributes name. Note, that an attribute
   * always has a name, unlike types.</p>
   */
  public XsQName getName();

  /** <p>Returns the attributes type.</p>
   */
  public XSType getType();

  /** <p>Returns whether the attribute is optional.</p>
   */
  public boolean isOptional();

  /** <p>Returns the attributes set of annotations.</p>
   */
  public XSAnnotation[] getAnnotations();

  /** <p>Returns the attributes "default" value or null, if no such
   * attribute is set.</p>
   */
  public String getDefault();

  /** <p>Returns the attributes "fixed" value or null, if no such
   * attribute is set.</p>
   */
  public String getFixed();
}
