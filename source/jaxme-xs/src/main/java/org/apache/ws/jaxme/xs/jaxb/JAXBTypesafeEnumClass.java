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
package org.apache.ws.jaxme.xs.jaxb;

import java.util.Iterator;


/** <p>This interface implements the JAXB typesafeEnumClass bindings.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 * @version $Id: JAXBTypesafeEnumClass.java 231785 2004-02-16 23:39:59Z jochen $
 */
public interface JAXBTypesafeEnumClass {
  /** <p>Returns the enumeration classes name, without any package
   * prefix.</p>
   */
  public String getName();

  /** <p>Returns the list of members. Any element in the list is
   * an instance of <code>TypesafeEnumMember</code>.</p>
   */
  public Iterator getTypesafeEnumMember();

  /** <p>Returns the created classess JavaDoc documentation.</p>
   */
  public JAXBJavadoc getJavadoc();
}
