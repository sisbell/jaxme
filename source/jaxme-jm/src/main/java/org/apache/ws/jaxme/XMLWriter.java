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
package org.apache.ws.jaxme;

import java.io.Writer;

import javax.xml.bind.JAXBException;

import org.apache.ws.jaxme.impl.JMMarshallerImpl;


/** <p>A simple serializer for XML documents.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface XMLWriter extends org.xml.sax.ContentHandler {
  /** <p>Initializes the XMLWriter by setting the Marshaller which created it.</p>
   */
  public void init(JMMarshallerImpl pMarshaller) throws JAXBException;

  /** <p>Sets the {@link Writer} to which the Marshaller ought to write.</p>
   */
  public void setWriter(Writer pWriter) throws JAXBException;

  /** <p>Returns whether the XMLWriter can encode the character
   * <code>c</code> without an escape sequence like &amp;#ddd;.</p>
   */
  public boolean canEncode(char c);
}
