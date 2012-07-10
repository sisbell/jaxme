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
package org.apache.ws.jaxme.generator.sg;

import org.apache.ws.jaxme.generator.sg.SchemaSG;
import org.apache.ws.jaxme.generator.sg.SGFactory;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;


/** <p>A common base interface for
 * {@link org.apache.ws.jaxme.generator.sg.ComplexTypeSG},
 * {@link org.apache.ws.jaxme.generator.sg.ObjectSG},
 * {@link org.apache.ws.jaxme.generator.sg.SchemaSG},
 * {@link org.apache.ws.jaxme.generator.sg.SimpleTypeSG}, and
 * {@link org.apache.ws.jaxme.generator.sg.TypeSG}.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface SGItem {
  /** <p>Returns the factory that created this item.</p>
   */
  public SGFactory getFactory();

  /** <p>Returns the items schema.</p>
   */
  public SchemaSG getSchema();

  /** <p>Returns the items location in the schema; useful for
   * error messages.</p>
   */
  public Locator getLocator();

  /** <p>Initializes the item.</p>
   */
  public void init() throws SAXException;
}
