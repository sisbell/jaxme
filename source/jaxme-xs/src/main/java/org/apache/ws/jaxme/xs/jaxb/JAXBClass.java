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

import org.apache.ws.jaxme.xs.xml.XsObject;


/** <p>This interface implements the JAXB class bindings.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 * @version $Id: JAXBClass.java 231785 2004-02-16 23:39:59Z jochen $
 */
public interface JAXBClass extends XsObject {
  /** <p>Returns the classes name, without the package. The package
   * name is specified by the schema bindings package declaration.</p>
   */
  public String getName();

  /** <p>Returns the implementations class name, including a package
   * name.</p>
   */
  public String getImplClass();

  /** <p>Returns the classes JavaDoc documentation.</p>
   */
  public JAXBJavadoc getJavadoc();
}
