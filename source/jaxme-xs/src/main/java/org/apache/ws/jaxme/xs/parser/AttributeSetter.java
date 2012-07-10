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

import org.xml.sax.SAXException;


/** <p>The attribute setter is used by the {@link org.apache.ws.jaxme.xs.parser.XsSAXParser}
 * to pass an attributes value to the bean. The main purpose of this
 * interface is the ability to choose a custom attribtue setter.</p>
 * <p>The default attribute setter is
 * {@link org.apache.ws.jaxme.xs.parser.impl.AttributeSetterImpl}.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface AttributeSetter {
  /** <p>Sets the attribute with the given namespace URI and local
   * name to the given value.</p>
   */
  public void setAttribute(String pQName,
                            String pNamespaceURI, String pLocalName, String pValue)
    throws SAXException;
}
