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


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface XsSAXParser extends ContentHandler {
  /** <p>Returns the bean configured by the XsSAXParser.</p>
   */
  public Object getBean();

  /** <p>Returns the current elements fully qualified name.</p>
   */
  public String getQName();

  /** <p>Returns the current elements namespace URI.</p>
   */
  public String getNamespaceURI();

  /** <p>Returns the current elements local name.</p>
   */
  public String getLocalName();
}
