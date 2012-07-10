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

import org.xml.sax.SAXException;


/** <p>Interface of <code>xs:attribute</code>, following
 * this specification:
 * <pre>
 *  &lt;xs:complexType name="attribute"&gt;
 *    &lt;xs:complexContent&gt;
 *      &lt;xs:extension base="xs:annotated"&gt;
 *        &lt;xs:sequence&gt;
 *          &lt;xs:element name="simpleType" minOccurs="0" type="xs:localSimpleType"/&gt;
 *        &lt;/xs:sequence&gt;
 *        &lt;xs:attributeGroup ref="xs:defRef"/&gt;
 *        &lt;xs:attribute name="type" type="xs:QName"/&gt;
 *        &lt;xs:attribute name="use" use="optional" default="optional"&gt;
 *          &lt;xs:simpleType&gt;
 *            &lt;xs:restriction base="xs:NMTOKEN"&gt;
 *              &lt;xs:enumeration value="prohibited"/&gt;
 *              &lt;xs:enumeration value="optional"/&gt;
 *              &lt;xs:enumeration value="required"/&gt;
 *            &lt;/xs:restriction&gt;
 *          &lt;/xs:simpleType&gt;
 *        &lt;/xs:attribute&gt;
 *        &lt;xs:attribute name="default" type="xs:string"/&gt;
 *        &lt;xs:attribute name="fixed" type="xs:string"/&gt;
 *        &lt;xs:attribute name="form" type="xs:formChoice"/&gt;
 *      &lt;/xs:extension&gt;
 *    &lt;/xs:complexContent&gt;
 *  &lt;/xs:complexType&gt;
 * </pre></p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface XsTAttribute extends XsTAnnotated {
  public static class Use {
    private String value;
    Use(String pValue) { value = pValue; }
    public String toString() { return value; }
    public String getValue() { return value; }
    public static Use valueOf(String pValue) {
      if ("prohibited".equals(pValue)) {
        return PROHIBITED;
      } else if ("optional".equals(pValue)) {
        return OPTIONAL;
      } else if ("required".equals(pValue)) {
        return REQUIRED;
      } else {
        throw new IllegalArgumentException("Invalid value for Use: " + pValue + ", expected 'prohibited', 'optional', or 'use'");
      }
    }
  }

  public static final Use PROHIBITED = new Use("prohibited");
  public static final Use OPTIONAL = new Use("optional");
  public static final Use REQUIRED = new Use("required");

  public XsTLocalSimpleType createSimpleType();

  public XsTLocalSimpleType getSimpleType();

  public void setType(XsQName pType);

  public XsQName getType();

  public void setUse(Use pUse);

  public Use getUse();

  public void setDefault(String pDefault);

  public String getDefault();

  public void setFixed(String pFixed);

  public String getFixed();

  public void setForm(XsFormChoice pForm) throws SAXException;

  public XsFormChoice getForm();

  public void setName(XsNCName pName);

  public XsNCName getName();

  public void setRef(XsQName pRef);

  public XsQName getRef();

  public boolean isGlobal();
}
