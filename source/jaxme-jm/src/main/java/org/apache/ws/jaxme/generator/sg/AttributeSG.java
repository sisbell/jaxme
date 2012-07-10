/*
 * Copyright 2003,2004  The Apache Software Foundation
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

import org.apache.ws.jaxme.js.DirectAccessible;
import org.apache.ws.jaxme.js.JavaMethod;
import org.apache.ws.jaxme.xs.xml.XsNamespaceList;
import org.apache.ws.jaxme.xs.xml.XsQName;
import org.apache.ws.jaxme.xs.xml.XsTWildcard;
import org.apache.ws.jaxme.generator.sg.PropertySG;
import org.apache.ws.jaxme.generator.sg.SGItem;
import org.apache.ws.jaxme.generator.sg.SGlet;
import org.apache.ws.jaxme.generator.sg.TypeSG;
import org.xml.sax.SAXException;


/** <p>A source generator for attributes.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface AttributeSG extends SGItem {
  /** <p>Returns the attribute name.</p>
   */
  public XsQName getName();

  /** <p>Returns whether the attribute is required.</p>
   */
  public boolean isRequired();

  /** <p>Invokes the given {@link SGlet} on any attribute value,
   * assuming they are non-null.</p>
   */
  public void forAllValues(JavaMethod pMethod, DirectAccessible pElement, SGlet pSGlet) throws SAXException;

  /** <p>Invokes the given {@link SGlet} on any non null attribute
   * value.</p>
   */
  public void forAllNonNullValues(JavaMethod pMethod, DirectAccessible pElement, SGlet pSGlet) throws SAXException;

  /** <p>Creates a new instance of {@link org.apache.ws.jaxme.generator.sg.PropertySGChain}.</p>
   */
  public Object newPropertySGChain();

  /** <p>Returns an instance of {@link org.apache.ws.jaxme.generator.sg.PropertySG}.</p>
   */
  public PropertySG getPropertySG();

  /** <p>Returns the attributes type.</p>
   */
  public TypeSG getTypeSG();

  /** <p>Returns whether this is a "wildcard" attribute.</p>
   */
  public boolean isWildcard();

  /** <p>If this is a "wildcard" attribute: Returns the namespace
   * list.</p>
   */
  public XsNamespaceList getNamespaceList();

  /** <p>If this is a "wildcard" attribute: Returns how to process
   * unknown attributes.</p>
   */
  public XsTWildcard.ProcessContents getProcessContents();
}
