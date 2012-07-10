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

import org.apache.ws.jaxme.xs.xml.XsESchema;


/** <p>Syntactical representation of a JAXB schema. Most probably
 * you aren't interested in the logical representation,
 * {@link org.apache.ws.jaxme.xs.jaxb.JAXBSchema}, and not in this
 * interface.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface JAXBXsSchema extends XsESchema {
  /** <p>Returns the <code>jaxb:version</code> attribute.</p>
   */
  public String getJaxbVersion();

  /** <p>Returns the list of <code>extension binding prefixes</code>.</p>
   */
  public String[] getJaxbExtensionBindingPrefixes();

  public JAXBSchemaBindings getJAXBSchemaBindings();
}
