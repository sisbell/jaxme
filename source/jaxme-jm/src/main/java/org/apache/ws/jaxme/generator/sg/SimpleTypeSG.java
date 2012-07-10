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
import org.apache.ws.jaxme.js.JavaMethod;
import org.apache.ws.jaxme.js.JavaQName;
import org.apache.ws.jaxme.js.JavaSource;
import org.apache.ws.jaxme.js.TypedValue;
import org.apache.ws.jaxme.generator.sg.AtomicTypeSG;
import org.apache.ws.jaxme.generator.sg.ListTypeSG;
import org.apache.ws.jaxme.generator.sg.UnionTypeSG;
import org.apache.ws.jaxme.generator.sg.Facet;
import org.apache.ws.jaxme.generator.sg.Facet.Type;
import org.apache.ws.jaxme.generator.sg.SGItem;
import org.apache.ws.jaxme.generator.sg.SGlet;
import org.xml.sax.SAXException;


/** <p>Interface of a source generator for complex types.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface SimpleTypeSG extends SGItem {
  /** <p>Returns whether the simple type is atomic.</p>
   */
  public boolean isAtomic();

  /** <p>Returns whether the simple type is a list.</p>
   */
  public boolean isList();

  /** <p>Returns whether the simple type is a union.</p>
   */
  public boolean isUnion();

  /** <p>Returns whether the simple type is nullable.</p>
   */
  public boolean isNullable();

  /** <p>Sets whether the simple type is nullable.</p>
   */
  public void setNullable(boolean pNullable);

  /** <p>If the simple type is atomic: Returns its atomic type details.</p>
   */
  public AtomicTypeSG getAtomicType();

  /** <p>If the simple type is a list: Returns its item type details.</p>
   * @throws IllegalStateException The type is no list.
   */
  public ListTypeSG getListType();

  /** <p>If the simple type is a union: Returns its union type details.</p>
   * @throws IllegalStateException The type is no union.
   */
  public UnionTypeSG getUnionType();

  /** <p>Returns the data types runtime type.</p>
   */
  public JavaQName getRuntimeType();

  /** Returns whether converting this type from a string can cause a
   * {@link javax.xml.bind.ParseConversionEvent}.
   */
  public boolean isCausingParseConversionEvent();

  /** <p>Returns a piece of Java code converting the string <code>pValue</code>
   * into the runtime type. Conversion occurs at runtime, using the
   * given instance of {@link org.apache.ws.jaxme.JMUnmarshallerHandler}.</p>
   * @param pMethod The method performing the type convertion.
   * @param pValue The value being casted
   * @param pData A piece of Java code holding an instance of
   * {@link org.apache.ws.jaxme.JMUnmarshallerHandler};
   *  may be used to support the conversion.
   */
  public TypedValue getCastFromString(JavaMethod pMethod, Object pValue, Object pData) throws SAXException;

  /** <p>Returns a piece of Java code converting the runtime type
   * <code>pValue</code> into a string. Conversion occurs at runtime, using the
   * given instance of {@link org.apache.ws.jaxme.impl.JMUnmarshallerHandlerImpl}.</p>
   */
  public TypedValue getCastToString(JavaMethod pMethod, Object pValue, DirectAccessible pData) throws SAXException;

  /** <p>Returns a piece of Java code converting the string <code>pValue</code>
   * into the runtime type. Conversion occurs at compile time.</p>
   */
  public TypedValue getCastFromString(String pValue) throws SAXException;

  /** <p>Returns whether the simple type does have a "set" method.</p>
   */
  public boolean hasSetMethod() throws SAXException;

  /** <p>Returns the collection type, which is either of "indexed" (an array)
   * or a list implementation, as specified by JAXB's property tag.</p>
   */
  public String getCollectionType();

  /** <p>Returns all of the simple types facets.</p>
   */
  public Facet[] getFacets();

  /** <p>Returns the simple types facets with the given type or null, if no
   * such facet exists.</p>
   */
  public Facet getFacet(Type pType);

  /** <p>Invokes the given {@link SGlet} on any value, assuming they
   * are non null.</p>
   */
  public void forAllValues(JavaMethod pMethod, Object pValue, SGlet pSGlet) throws SAXException;

  /** <p>Invokes the given {@link SGlet} on any non null value.</p>
   */
  public void forAllNonNullValues(JavaMethod pMethod, Object pValue, SGlet pSGlet) throws SAXException;

  /** <p>Generates helper classes required by the simple type.</p>
   */
  public void generate() throws SAXException;

  /** <p>Generates helper classes required by the simple type.
   * The generated classes are inner classes of the given.</p>
   */
  public void generate(JavaSource pSource) throws SAXException;

  /** <p>Returns code creating a boolean value indicating whether the given values
   * are equal.</p>
   */
  public Object getEqualsCheck(JavaMethod pMethod, Object pValue1, Object pValue2) throws SAXException;

  /** <p>Returns the types initial value, as created by the constructor.</p>
   */
  public Object getInitialValue(JavaSource pSource) throws SAXException;

  /** <p>Generates a set method for the simple type.</p>
   */
  public JavaMethod getXMLSetMethod(JavaSource pSource, String pFieldName,
		  							String pParamName, String pMethodName) throws SAXException;

  /** <p>Adds code for validating the value <code>pValue</code> to the "add" or
   * "set" method <code>pMethod</code>.</p>
   */
  public void addValidation(JavaMethod pMethod, DirectAccessible pValue) throws SAXException;

  	/** Adds code for validating the value <code>pValue</code> within the
  	 * handler.
  	 */
	public void addHandlerValidation(JavaMethod pJm, TypedValue pValue, Object pStringValue) throws SAXException;

	/** Returns, whether the simple type is an instance of
	 * <code>xs:id</code>.
	 */
	public boolean isXsId();

	/** Returns, whether the simple type is an instance of
	 * <code>xs:idref</code>.
	 */
	public boolean isXsIdRef();
}
