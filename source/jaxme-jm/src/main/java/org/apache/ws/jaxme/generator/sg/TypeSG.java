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

import org.apache.ws.jaxme.js.JavaField;
import org.apache.ws.jaxme.js.JavaMethod;
import org.apache.ws.jaxme.js.JavaQName;
import org.apache.ws.jaxme.js.JavaSource;
import org.apache.ws.jaxme.xs.xml.XsQName;
import org.apache.ws.jaxme.xs.xml.XsSchemaHeader;
import org.apache.ws.jaxme.generator.sg.ComplexTypeSG;
import org.apache.ws.jaxme.generator.sg.SGItem;
import org.apache.ws.jaxme.generator.sg.SimpleTypeSG;
import org.xml.sax.SAXException;


/** <p>Interface of a source generator for types; applies both to
 * simple and complex types.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface TypeSG extends SGItem {
  /** <p>A property allows to store custom data related to the type.
   * Properties are used by external source generators. For example,
   * the JDBC source generator will use this to store the complex
   * types table name here.</p>
   */
  public void setProperty(String pName, Object pValue);

  /** <p>A property allows to store custom data related to the type.
   * Properties are used by external source generators. For example,
   * the JDBC source generator will use this to store the complex
   * types table name here.</p>
   */
  public Object getProperty(String pName);

  /** <p>Returns whether this is a global type.</p>
   */
  public boolean isGlobalType();

  /** <p>Returns whether this type is generated with a global class.</p>
   */
  public boolean isGlobalClass();

  /** <p>If the type is global: Returns the types name.</p>
   * @throws IllegalStateException The type isn't global.
   */
  public XsQName getName();

  /** <p>Returns whether the type is complex. If so, it is valid to
   * invoke the method {@link #getComplexTypeSG}. Otherwise, you may
   * invoke the method {@link #getSimpleTypeSG}.</p>
   */
  public boolean isComplex();

  /** <p>If the type is simple: Creates a new instance of
   * {@link org.apache.ws.jaxme.generator.sg.SimpleTypeSGChain} generating the type.</p>
   * <p><em>Implementation note</em>: The type
   * {@link org.apache.ws.jaxme.generator.sg.SimpleTypeSGChain}
   * must not be exposed in the interface, because the interface
   * class is used to generate this type. In other words, this
   * interface must be compilable without the
   * {@link org.apache.ws.jaxme.generator.sg.SimpleTypeSGChain}
   * interface.</p>
   */
  public Object newSimpleTypeSG() throws SAXException;

  /** <p>If the type is complex: Creates an instance of
   * {@link org.apache.ws.jaxme.generator.sg.ComplexTypeSGChain} generating the type.</p>
   * <p><em>Implementation note</em>: The type
   * {@link org.apache.ws.jaxme.generator.sg.ComplexTypeSGChain}
   * must not be exposed in the interface, because the interface
   * class is used to generate this type. In other words, this
   * interface must be compilable without the
   * {@link org.apache.ws.jaxme.generator.sg.ComplexTypeSGChain}
   * interface.</p>
   */
  public Object newComplexTypeSG() throws SAXException;

  /** <p>If the type is complex: Returns an instance of
   * {@link ComplexTypeSG} generating the type.</p>
   */
  public ComplexTypeSG getComplexTypeSG();

  /** <p>If the type is simple: Returns an instance of {@link SimpleTypeSG}
   * for generating the type.</p>
   *
   * @throws IllegalStateException The type is complex.
   */
  public SimpleTypeSG getSimpleTypeSG();

  /** <p>Generates a Java field for an instance of this type.</p>
   */
  public JavaField getXMLField(JavaSource pSource, 
                                     String pFieldName, String pDefaultValue) throws SAXException;

  /** <p>Generates a get method returning an instance of this type.</p>
   */
  public JavaMethod getXMLGetMethod(JavaSource pSource,
                                     String pFieldName, String pMethodName) throws SAXException;

  /** <p>Generates a set method returning an instance of this type.</p>
   */
  public JavaMethod getXMLSetMethod(JavaSource pSource, String pFieldName,
		  							String pParamName,
  		                            String pMethodName, boolean pSetIsSet) throws SAXException;

  /** <p>Generates an "isSet" method returning whether the field is set.</p>
   */
  public JavaMethod getXMLIsSetMethod(JavaSource pSource,
                                       String pFieldName, String pMethodName) throws SAXException;

  /** <p>Returns the types runtime type.</p>
   */
  public JavaQName getRuntimeType() throws SAXException;

  /** <p>Generates the types sources.</p>
   */
  public void generate() throws SAXException;

  /** <p>Generates the types sources as an inner class of the given.</p>
   */
  public void generate(JavaSource pSource) throws SAXException;

  /** <p>Returns whether the type is a restriction of another type.</p>
   */
  public boolean isRestriction();

  /** <p>If the type is a restriction: Returns the restricted type.</p>
   * @throws IllegalStateException The type is no restriction.
   */
  public TypeSG getRestrictedType();

  /** <p>Returns whether the type is an extension of another type.</p>
   */
  public boolean isExtension();

  /** <p>If the type is an extension: Returns the extended type.</p>
  * @throws IllegalStateException The type is no extension.
   */
  public TypeSG getExtendedType();

  /** <p>Returns information on the types syntactical context.</p>
   */
  public XsSchemaHeader getSchemaHeader();
}
