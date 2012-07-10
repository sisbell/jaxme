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

import org.apache.ws.jaxme.xs.*;
import org.apache.ws.jaxme.xs.impl.XSLogicalParser;
import org.apache.ws.jaxme.xs.xml.XsObjectFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.helpers.NamespaceSupport;

/** <p>This interface provides access to the parsers context.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface XSContext {
  /** <p>Returns the parsers object factory for syntax elements.</p>
   */
  public XsObjectFactory getXsObjectFactory();
  /** <p>Sets the parsers object factory for syntax elements.</p>
   */
  public void setXsObjectFactory(XsObjectFactory pFactory);

  /** <p>Returns the parsers object factory for logical elements.</p>
   */
  public XSObjectFactory getXSObjectFactory();
  /** <p>Sets the parsers object factory for logical elements.</p>
   */
  public void setXSObjectFactory(XSObjectFactory pFactory);

  /** <p>Returns the {@link AttributeSetter}, which is used to
   * configure properties.</p>
   */
  public AttributeSetter getAttributeSetter();
  /** <p>Sets the AttributeSetter, which is used to
   * configure properties.</p>
   */
  public void setAttributeSetter(AttributeSetter pSetter);

  /** <p>Returns the {@link ChildSetter}, which is used to create
   * child elements.</p>
   */
  public ChildSetter getChildSetter();
  /** <p>Sets the ChildSetter, which is used to create
   * child elements.</p>
   */
  public void setChildSetter(ChildSetter pSetter);

  /** <p>Returns the {@link TextSetter}, used to add text sections.</p>
   */
  public TextSetter getTextSetter();
  /** <p>Sets the TextSetter, used to add text sections.</p>
   */
  public void setTextSetter(TextSetter pSetter);

  /** <p>Returns the currently running {@link XSParser} or null,
   * if no such instance is available.</p>
   */
  public XSLogicalParser getXSLogicalParser();
  /** <p>Sets the currently active instance of {@link XSParser}, if any.
   * Returns null, if no such instance is available.</p>
   */
  public void setXSLogicalParser(XSLogicalParser pParser);

  /** <p>Returns the {@link XSSchema}, which is currently being parsed,
   * if any.</p>
   */
  public XSSchema getXSSchema();

  /** <p>Returns the locator, used for error messages.</p>
   */
  public Locator getLocator();    
  /** <p>Sets the locator, used for error messages.</p>
   */
  public void setLocator(Locator pLocator);

  /** <p>Returns the namespace handler.</p>
   */
  public NamespaceSupport getNamespaceSupport();
  /** <p>Sets the namespace handler.</p>
   */
  public void setNamespaceSupport(NamespaceSupport pNamespaceSupport);

  /** <p>Sets the currently active instance of {@link XsSAXParser}.</p>
   */
  public ContentHandler getCurrentContentHandler();
  /** <p>Sets the currently active instance of {@link XsSAXParser}.</p>
   */
  public void setCurrentContentHandler(ContentHandler pParser);
}