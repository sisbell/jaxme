/*
 * Copyright 2003,2004  The Apache Software Foundation
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
import org.apache.ws.jaxme.xs.xml.XsSchemaHeader;
import org.xml.sax.SAXException;


/** <p>Interface of an XML Schema type. Includes complex and simple types.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface XSType extends XSOpenAttrs {
  /** <p>Returns the array of annotations.</p>
   */
  public XSAnnotation[] getAnnotations();

  /** <p>Returns whether the type is simple or not.</p>
   */
  public boolean isSimple();

  /** <p>Returns the simple types information.</p>
   * @throws IllegalStateException The type is complex.
   */
  public XSSimpleType getSimpleType() throws SAXException;

  /** <p>Returns the complex types information.</p>
   * @throws IllegalStateException The type is simple
   */
  public XSComplexType getComplexType() throws SAXException;

  /** <p>Returns whether the type is global or not.</p>
   */
  public boolean isGlobal();

  /** <p>Sets whether the type is global or not.</p>
   */
  public void setGlobal(boolean pGlobal);

  /** <p>If the type is global: Returns the types name. Otherwise returns
   * null.</p>
   */
  public XsQName getName();

  /** <p>Returns whether the type is a builtin type of XML Schema.</p>
   */
  public boolean isBuiltin();

  /** <p>Returns the types syntactical context or null, if no such context
   * is available.</p>
   */
  public XsSchemaHeader getSchemaHeader();
}
