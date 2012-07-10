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

import org.apache.ws.jaxme.js.JavaQName;
import org.apache.ws.jaxme.js.JavaSource;
import org.apache.ws.jaxme.xs.XSAttribute;
import org.apache.ws.jaxme.xs.XSWildcard;
import org.apache.ws.jaxme.generator.sg.AttributeSG;
import org.apache.ws.jaxme.generator.sg.Context;
import org.apache.ws.jaxme.generator.sg.ComplexContentSG;
import org.apache.ws.jaxme.generator.sg.SimpleContentSG;
import org.apache.ws.jaxme.generator.sg.TypeSG;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;


/** <p>Interface of a source generator for complex types.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface ComplexTypeSG {
  /** <p>Initializes the instance.</p>
   */
  public void init() throws SAXException;

  /** <p>Returns the {@link TypeSG} that created this instance.</p>
   */
  public TypeSG getTypeSG();

  /** <p>Returns the types {@link Locator}.</p>
   */
  public Locator getLocator();

  /** <p>Returns the datatypes runtime type, which is the binding
   * interface.</p>
   */
  public Context getClassContext();

  /** <p>Generates the types interface as a standalone class.
   * This is used for global types.</p>
   */
  public JavaSource getXMLInterface() throws SAXException;

  /** <p>Generates the types interface as an inner class of the given.</p>
   */
  public JavaSource getXMLInterface(JavaSource pSource) throws SAXException;

  /** <p>Generates the types implementation as a standalone class.
   * This is used for global types.</p>
   */
  public JavaSource getXMLImplementation() throws SAXException;

  /** <p>Generates the types implementation as an inner class of the given.</p>
   */
  public JavaSource getXMLImplementation(JavaSource pSource) throws SAXException;

  /** <p>Generates the types XML serializer as a standalone class.
   * This is used for global types.</p>
   */
  public JavaSource getXMLSerializer() throws SAXException;

  /** <p>Generates the types XML serializer as an inner class of the given.
   * This is used for local types.</p>
   */
  public JavaSource getXMLSerializer(JavaSource pSource) throws SAXException;

  /** <p>Generates the types XML handler as a standalone class.
   * This is used for global types.</p>
   */
  public JavaSource getXMLHandler(JavaQName pQName) throws SAXException;

  /** <p>Generates the types XML handler as an inner class of the given.
   * This is used for local types.</p>
   */
  public JavaSource getXMLHandler(JavaSource pSource) throws SAXException;

  /** <p>Returns whether the data type has attributes.</p>
   */
  public boolean hasAttributes();

  /** <p>Creates a new instance of
   * {@link org.apache.ws.jaxme.generator.sg.AttributeSGChain}
   * generating the given attribute.</p>
   */
  public Object newAttributeSG(XSAttribute pAttribute) throws SAXException;

  /** <p>Creates a new instance of
   * {@link org.apache.ws.jaxme.generator.sg.AttributeSGChain}
   * generating the given wildcard attributes.</p>
   */
  public Object newAttributeSG(XSWildcard pWildcard) throws SAXException;

  /** <p>Adds the given {@link AttributeSG} to the list of attributes
   * and invokes the method {@link org.apache.ws.jaxme.generator.sg.SGItem#init()}
   * on it.</p>
   */
  public void addAttributeSG(AttributeSG pAttribute) throws SAXException;

  /** <p>Returns the data types array of attributes.</p>
   */
  public AttributeSG[] getAttributes();

  /** <p>Returns whether the data type has simple content.</p>
   */
  public boolean hasSimpleContent();

  /** <p>If the complex type has simple content: Creates an instance of
   * {@link org.apache.ws.jaxme.generator.sg.SimpleContentSGChain} generating the type.</p>
   * <p><em>Implementation note</em>: The type
   * {@link org.apache.ws.jaxme.generator.sg.SimpleContentSGChain}
   * must not be exposed in the interface, because the interface
   * class is used to generate this type. In other words, this
   * interface must be compilable without the
   * {@link org.apache.ws.jaxme.generator.sg.SimpleContentSGChain}
   * interface.</p>
   */
  public Object newSimpleContentTypeSG() throws SAXException;

  /** <p>If the data type has simple content: Returns an instance of
   * {@link SimpleContentSG}. Uses {@link #newSimpleContentTypeSG()}
   * internally.</p>
   */
  public SimpleContentSG getSimpleContentSG();

  /** <p>If the complex type has complex content: Creates an instance of
   * {@link org.apache.ws.jaxme.generator.sg.ComplexContentSGChain} generating the given complex type.</p>
   * <p><em>Implementation note</em>: The type
   * {@link org.apache.ws.jaxme.generator.sg.ComplexContentSGChain}
   * must not be exposed in the interface, because the interface
   * class is used to generate this type. In other words, this
   * interface must be compilable without the
   * {@link org.apache.ws.jaxme.generator.sg.ComplexContentSGChain}
   * interface.</p>
   */
  public Object newComplexContentTypeSG() throws SAXException;

  /** <p>If the data type has complex content: Returns an instance of
   * {@link ComplexContentSG}. Uses {@link #newComplexContentTypeSG()}
   * internally.</p>
   */
  public ComplexContentSG getComplexContentSG();
}
