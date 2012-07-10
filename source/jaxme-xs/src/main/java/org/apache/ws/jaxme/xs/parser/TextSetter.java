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

/** <p>Interface of an object which is setting character data as
 * a bean property. The default implementation,
 * {@link org.apache.ws.jaxme.xs.parser.impl.TextSetterImpl}, is converting any
 * SAX event of type {@link org.xml.sax.ContentHandler#characters(char[], int, int)}
 * into a call of the method <code>addText(String)</code> on the
 * bean.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface TextSetter {
  /** <p>Called for any {@link org.xml.sax.ContentHandler#characters(char[], int, int)}
   * event. Note, that this implies in particular, that the method may be called
   * multiple times, even for a single block of text.</p>
   *
   * @param pText The text being set.
   */
  public void addText(String pText) throws SAXException; 
}
