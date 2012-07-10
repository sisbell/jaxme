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

import org.apache.ws.jaxme.xs.xml.XsObject;
import org.apache.ws.jaxme.xs.xml.XsQName;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;


/** <p>Interface of a an object creating beans.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface XsObjectCreator {
  /** <p>Asks the bean creator whether he is willing to create a bean
   * for the given namespace URI and local name. The bean creator
   * may do either of the following:
   * <ol>
   *   <li>Return a new bean.</li>
   *   <li>Return null in order to indicate, that the bean creator
   *     delegates the decision to the next bean creator.</li>
   *   <li>Throw a SAX Exception in order to indicate that the
   *     combination of namespace URI and local name is invalid.</p>
   * </ol>
   */
  public XsObject newBean(XsObject pParent, Locator pLocator, XsQName pQName) throws SAXException;
}
