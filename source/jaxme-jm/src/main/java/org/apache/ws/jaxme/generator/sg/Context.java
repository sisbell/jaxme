/*
 * Copyright 2003-2006  The Apache Software Foundation
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
package org.apache.ws.jaxme.generator.sg;

import org.apache.ws.jaxme.js.JavaQName;
import org.apache.ws.jaxme.xs.xml.XsQName;


/** <p>A common base interface for items that may enclose a local
 * type. This is used to construct inner class names.</p>
 */
public interface Context {
  /**
   * Returns the qualified name, from which Java class names
   * have been derived.
   */
  public XsQName getName();

  /** <p>Returns the contexts XML interface name.</p>
   */
  public JavaQName getXMLInterfaceName();

  /** <p>Returns the contexts XML implementation name.</p>
   */
  public JavaQName getXMLImplementationName();

  /** <p>Returns the contexts XML handler name.</p>
   */
  public JavaQName getXMLHandlerName();

  /** <p>Returns the contexts XML serializer name.</p>
   */
  public JavaQName getXMLSerializerName();

  /** <p>Returns the contexts XML validator name.</p>
   */
  public JavaQName getXMLValidatorName();

  /** <p>Returns the contexts PM name.</p>
   */
  public JavaQName getPMName();

  /** <p>Returns whether the class context is global.</p>
   */
  public boolean isGlobal();
}
