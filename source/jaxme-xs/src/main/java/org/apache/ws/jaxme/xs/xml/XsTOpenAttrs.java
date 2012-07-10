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
package org.apache.ws.jaxme.xs.xml;

import org.xml.sax.Attributes;


/** <p>A common base type for most of the schema elements.
 * Implements the following specification:
 * <pre>
 *  &lt;xs:complexType name="openAttrs">
 *    &lt;xs:annotation>
 *      &lt;xs:documentation>
 *        This type is extended by almost all schema types
 *        to allow attributes from other namespaces to be
 *        added to user schemas.
 *      &lt;/xs:documentation>
 *    &lt;/xs:annotation>
 *    &lt;xs:complexContent>
 *      &lt;xs:restriction base="xs:anyType">
 *        &lt;xs:anyAttribute namespace="##other" processContents="lax"/>
 *      &lt;/xs:restriction>
 *    &lt;/xs:complexContent>
 *  &lt;/xs:complexType&gt;
 * </pre>
 * 
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface XsTOpenAttrs extends XsObject {
  /** <p>Returns the attributes having other namespaces.</p>
   */
  public Attributes getOpenAttributes();
}
