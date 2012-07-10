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
package org.apache.ws.jaxme.generator;

import org.apache.ws.jaxme.generator.sg.SGFactory;
import org.apache.ws.jaxme.generator.sg.SchemaSG;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/** <p>A SchemaReader is responsible for reading a schema
 * definition.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface SchemaReader {
  /** <p>Returns the Generator controlling the SchemaReader.</p>
   */
  public Generator getGenerator();

  /** <p>Sets the Generator controlling the SchemaReader.</p>
   */
  public void setGenerator(Generator pGenerator);

  /** <p>Adds an instance of {@link org.apache.ws.jaxme.generator.sg.SGFactoryChain}
   * to the SGFactory.</p>
   */
  public void addSGFactoryChain(Class pClass);

  /** <p>Returns the {@link SGFactory}.</p>
   */
  public SGFactory getSGFactory() throws SAXException;

  /** <p>Called for parsing a schema definition from the
   * given Reader.</p>
   *
   * @param pSource A SAX Input Source, with the system ID set,
   *   if possible
   */
  public SchemaSG parse(InputSource pSource) throws Exception;
}
