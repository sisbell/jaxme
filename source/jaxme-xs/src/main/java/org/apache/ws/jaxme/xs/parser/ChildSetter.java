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
package org.apache.ws.jaxme.xs.parser;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/** <p>Implementations of this interface are responsible for the handling
 * of child elements.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface ChildSetter {
  /** <p>Returns a ContentHandler, which is able to handle the given child
   * element.</p>
   *
   * @param pNamespaceURI The child elements namespace URI
   * @param pLocalName The child elements local name
   * @param pQName The child elements fully qualified name
   * @see org.apache.ws.jaxme.xs.XSParser#getContext()
   */
  public ContentHandler getChildHandler(String pQName, String pNamespaceURI,
                                         String pLocalName) throws SAXException;
}
