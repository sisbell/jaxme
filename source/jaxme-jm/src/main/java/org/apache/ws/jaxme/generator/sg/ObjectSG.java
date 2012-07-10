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

import org.apache.ws.jaxme.js.JavaSource;
import org.apache.ws.jaxme.xs.xml.XsQName;
import org.apache.ws.jaxme.generator.sg.Context;
import org.apache.ws.jaxme.generator.sg.SGItem;
import org.apache.ws.jaxme.generator.sg.TypeSG;
import org.xml.sax.SAXException;


/** <p>A source generator for elements or attributes.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface ObjectSG extends SGItem {
  /** <p>Returns the objects name.</p>
   */
  public XsQName getName();

  /** <p>Returns the objects {@link Context}.</p>
   */
  public Context getClassContext();

  /** <p>Returns the objects type.</p>
   */
  public TypeSG getTypeSG();

  /** <p>If the object is global and complex: Generates its XML interface.</p>
   */
  public JavaSource getXMLInterface() throws SAXException;

  /** <p>If the object is global and complex: Generates its XML implementation.</p>
   */
  public JavaSource getXMLImplementation() throws SAXException;

  /** <p>If the object is global and complex: Generates its XML handler.</p>
   */
  public JavaSource getXMLHandler() throws SAXException;

  /** <p>If the object is global and complex: Generates its XML serializer.</p>
   */
  public JavaSource getXMLSerializer() throws SAXException;

  /** <p>Generates the objects sources.</p>
   */
  public void generate() throws SAXException;

  /** Returns whether the <code>ObjectSG</code> is global.
   */
  public boolean isGlobal() throws SAXException;
}
