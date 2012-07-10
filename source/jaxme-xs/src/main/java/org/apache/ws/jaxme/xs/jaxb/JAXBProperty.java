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
package org.apache.ws.jaxme.xs.jaxb;

import org.apache.ws.jaxme.xs.xml.XsObject;


/** <p>This interface implements the JAXB property bindings.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 * @version $Id: JAXBProperty.java 231785 2004-02-16 23:39:59Z jochen $
 */
public interface JAXBProperty extends XsObject {
  public interface BaseType extends XsObject {
    /** <p>Returns the Java type.</p>
     */
    public JAXBJavaType getJavaType();
  }

  /** <p>Returns the property name.</p>
   */
  public String getName();

  /** <p>Returns the collection type; either of "indexed"
   * or an implementation of <code>java.util.List</code>.
   * The value null indicates, that the attribute has not
   * been set.</p>
   */
  public String getCollectionType();

  /** <p>Returns whether fixed attributes are implemented as a
   * constant property. The value null indicates, that the
   * attribute has not been set.</p>
   */
  public Boolean isFixedAttributeAsConstantProperty();

  /** <p>Returns whether a <code>isSet()</code> method is being
   * generated. The value null indicates, that the attribute
   * has not been set.</p>
   */
  public Boolean isGenerateIsSetMethod();

  /** <p>Returns whether FailFastCheck is enabled. The value null
   * indicates, that the attribute has not been set.</p>
   */
  public Boolean isEnableFailFastCheck();

  /** <p>Returns the getter methods JavaDoc documentation.</p>
   */
  public JAXBJavadoc getJavadoc();

  /** <p>Returns the base type.</p>
   */
  public BaseType getBaseType();
}
