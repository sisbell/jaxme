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
package org.apache.ws.jaxme.impl;

/** <p>A simple serializer for XML documents
 * extending the default XMLWriter implementation, XMLWriterImpl.
 * The basic difference against its parent is that this writer
 * doesn't escape characters for reasons of the underlying
 * encoding.</p>
 *
 * @author <a href="mailto:iasandcb@hotmail.com">Ias</a>
 */
public class PassThroughXMLWriter extends XMLWriterImpl {
  public boolean canEncode(char c) {
    return true;
  }
}
