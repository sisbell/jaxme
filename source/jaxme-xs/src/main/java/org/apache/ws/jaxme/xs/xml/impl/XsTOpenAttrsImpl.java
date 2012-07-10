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
package org.apache.ws.jaxme.xs.xml.impl;

import org.apache.ws.jaxme.xs.XSParser;
import org.apache.ws.jaxme.xs.xml.*;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;


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
public class XsTOpenAttrsImpl extends XsObjectImpl implements XsTOpenAttrs {
  private AttributesImpl attributes = new AttributesImpl();

  protected XsTOpenAttrsImpl(XsObject pParent) {
    super(pParent);
  }

  public Attributes getOpenAttributes() {
    return attributes;
  }

  /** <p>This method receives all the attributes, including those from the
   * XML schema namespace. The method refuses to handle the attribute, if
   * it has the XML schema namespace by returning the value false. Otherwise,
   * the attribute is added to the list returned by {@link #getOpenAttributes()}
   * and the value true is returned.</p>
   */
  public boolean setAttribute(String pQName, String pNamespaceURI, String pLocalName, String pValue)
      throws SAXException {
    if (pNamespaceURI == null  ||  "".equals(pNamespaceURI)) {
      return false;
    } else if (XSParser.XML_SCHEMA_URI.equals(pNamespaceURI)) {
      throw new IllegalStateException("Unknown attribute in " + getClass().getName() + ": " + pQName);
    }
    attributes.addAttribute(pNamespaceURI, pLocalName, pQName, "CDATA", pValue);
    return true;
  }
}
