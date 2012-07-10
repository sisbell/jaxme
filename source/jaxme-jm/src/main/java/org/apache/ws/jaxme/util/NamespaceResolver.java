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
package org.apache.ws.jaxme.util;

/** <p>An abstract object which is able to split an XML name
 * into its namespace URI and local part.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 * @version $Id: NamespaceResolver.java 231785 2004-02-16 23:39:59Z jochen $
 */
public interface NamespaceResolver {
  /** <p>Splits the XML name <code>pName</code> into its
   * namespace URI, qualified name and local name, which
   * are stored into the array parts:
   * <ul>
   *   <li><code>parts[0]</code>: namespace URI; empty if no namespace is used</li>
   *   <li><code>parts[1]</code>: local name, with the prefix removed</li>
   *   <li><code>parts[2]</code>: qualified name (same as pName)</li>
   * </ul>
   *
   * @return True, if the namespace prefix of <code>pName</code>
   *   was successfully resolved. False otherwise.
   */
  public boolean processName(String pName, String[] parts);
}
