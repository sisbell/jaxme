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
 
package org.apache.ws.jaxme.xs;

import org.xml.sax.ContentHandler;

/** <p>A SAX Parser for XML Schema. Use this as a SAX ContentHandler.
 * The schema may be retrieved using {@link #getXSSchema()} after
 * invoking {@link org.xml.sax.ContentHandler#endDocument()}.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface XSContentHandler extends ContentHandler {
  /** <p>Returns the schema, which has previously been parsed.</p>
   * @throws IllegalStateException The
   * {@link org.xml.sax.ContentHandler#endDocument()} event
   * has not yet been seen.
   */
  public XSSchema getXSSchema();
}
