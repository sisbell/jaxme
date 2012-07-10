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

import org.xml.sax.SAXException;
import org.apache.ws.jaxme.generator.sg.PropertySG;
import org.apache.ws.jaxme.generator.sg.TypeSG;


/** 
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface SimpleContentSG {
  /** <p>Initializes the SimpleContentSG.</p>
   */
  public void init() throws SAXException;

  /** <p>Creates an instance of
   * {@link org.apache.ws.jaxme.generator.sg.PropertySGChain} generating
   * the content element.</p>
   */
  public Object newPropertySGChain() throws SAXException;

  /** <p>Returns the content elements {@link PropertySG}.</p>
   */
  public PropertySG getPropertySG() throws SAXException;

  /** <p>Returns the complex type, to which this content element
   * belongs.</p>
   */
  public TypeSG getTypeSG() throws SAXException;

  /** <p>Returns the content elements simple type.</p>
   */
  public TypeSG getContentTypeSG() throws SAXException;
}
