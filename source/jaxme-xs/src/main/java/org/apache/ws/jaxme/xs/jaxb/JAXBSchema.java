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

import org.apache.ws.jaxme.xs.XSSchema;


/** <p>Interface of a JAXB schema. It inherits the methods of
 * {@link JAXBGlobalBindings}.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface JAXBSchema extends XSSchema {
  /** <p>Returns the schemas globalBindings. This is guaranteed to be non-null.</p>
   */
  public JAXBGlobalBindings getJAXBGlobalBindings();

  /** <p>Returns the schemas <code>jaxb:version</code> attribute.</p>
   */
  public String getJaxbVersion();

  /** <p>Returns the schemas <code>jaxb:extensionBindingPrefixes</code> list.</p>
   */
  public String[] getJaxbExtensionBindingPrefixes();
}
