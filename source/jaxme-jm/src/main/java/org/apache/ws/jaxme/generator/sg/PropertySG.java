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

import org.apache.ws.jaxme.js.DirectAccessible;
import org.apache.ws.jaxme.js.JavaField;
import org.apache.ws.jaxme.js.JavaMethod;
import org.apache.ws.jaxme.js.JavaQName;
import org.apache.ws.jaxme.js.JavaSource;
import org.apache.ws.jaxme.js.TypedValue;
import org.apache.ws.jaxme.generator.sg.SGlet;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface PropertySG {
  /** <p>Initializes the <code>PropertySG</code>.</p>
   */
  public void init() throws SAXException;

  /** <p>Returns whether the property has an "is set" method.</p>
   */
  public boolean hasIsSetMethod();

  /** <p>Returns the properties collection type.</p>
   */
  public String getCollectionType();

  /** <p>Returns the objects field name. By default, this is the
   * property name with the prefix '_'.</p>
   */
  public String getXMLFieldName() throws SAXException;

  /** <p>Returns the objects property name.</p>
   */
  public String getPropertyName() throws SAXException;

  /** <p>Returns the objects getter name.</p>
   */
  public String getXMLGetMethodName() throws SAXException;

  /** <p>Returns the objects setter name.</p>
   */
  public String getXMLSetMethodName() throws SAXException;

  /** <p>Returns the name of the objects "isSet" method.</p>
   */
  public String getXMLIsSetMethodName() throws SAXException;

  /** <p>Creates the Java field holding the objects property value.</p>
   */
  public JavaField getXMLField(JavaSource pSource) throws SAXException;

  /** <p>Creates the getter returning the property value.</p>
   */
  public JavaMethod getXMLGetMethod(JavaSource pSource) throws SAXException;

  /** <p>Creates the setter returning the property value.</p>
   */
  public JavaMethod getXMLSetMethod(JavaSource pSource) throws SAXException;

  /** <p>Creates the isSet returning whether the property value is set or not.</p>
   */
  public JavaMethod getXMLIsSetMethod(JavaSource pSource) throws SAXException;

  /** <p>Returns a piece of Java code with the property value.</p>
   * @param pElement The element on which the value is being set or null for "this".
   */
  public Object getValue(DirectAccessible pElement) throws SAXException;

  /** <p>Creates a piece of Java code setting the elements property value.
   * In the case of an element with multiplicity > 1, the object must be
   * a list or an array.</p>
   *
   * @param pMethod The method being generated.
   * @param pElement The element on which the value is being set or null for "this".
   * @param pType The values type, if a cast is required, or null, if the value
   *   is already casted.
   */
  public void setValue(JavaMethod pMethod, DirectAccessible pElement, Object pValue, JavaQName pType)
    throws SAXException;

  /** <p>Creates a piece of Java code adding an element value. In the case
   * of an element with multiplicity > 1, the object must be the atomic
   * value.</p>
   * @param pElement The element on which the value is being set or null for "this".
   * @param pType The values type, if a cast is required, or null, if the value
   *   is already casted.
   */
  public void addValue(JavaMethod pMethod, DirectAccessible pElement, TypedValue pValue, JavaQName pType)
    throws SAXException;

  /** <p>Invokes the given {@link org.apache.ws.jaxme.generator.sg.SGlet}
   * for any value.</p>
   * @param pElement The element on which the value is being set or null for "this".
   */
  public void forAllValues(JavaMethod pMethod, DirectAccessible pElement, SGlet pSGlet) throws SAXException;

  /** <p>Invokes the given {@link org.apache.ws.jaxme.generator.sg.SGlet}
   * for any non null value.</p>
   * @param pElement The element on which the value is being set or null for "this".
   */
  public void forAllNonNullValues(JavaMethod pMethod, DirectAccessible pElement, SGlet pSGlet) throws SAXException;

  /** <p>Generates the property setters and getters.</p>
   */
  public void generate(JavaSource pSource) throws SAXException;
}
