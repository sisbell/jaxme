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
import org.apache.ws.jaxme.xs.xml.XsQName;


/** <p>This interface implements the JAXB javaType bindings.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 * @version $Id: JAXBJavaType.java 231996 2004-09-30 00:09:30Z jochen $
 */
public interface JAXBJavaType extends XsObject {
  public interface JAXBGlobalJavaType extends JAXBJavaType {
    /** <p>Returns the xmlType.</p>
     */
    public XsQName getXmlType();
  }

  /** <p>Returns the runtime type.</p>
   */
  public String getName();

  /** Returns the XML type.
   */
  public XsQName getXmlType();

  /** <p>Returns whether the <code>print()</code> and/or
   * <code>parse()</code> methods have an additional
   * <code>nsContext</code> attribute.</p>
   */
  public boolean hasNsContext();

  /** <p>Returns the name of the <code>parse()</code> method.</p>
   */
  public String getParseMethod();

  /** <p>Returns the name of the <code>print()</code> method.</p>
   */
  public String getPrintMethod();
}
