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


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XsComplexContentType {
  private final String name;
  private XsComplexContentType(String pName) {
    name = pName;
  }
  public String toString() { return name; }
  public boolean equals(Object o) {
    return o != null  &&  (o instanceof XsComplexContentType)  &&  ((XsComplexContentType) o).name.equals(name);
  }
  public int hashCode() { return name.hashCode(); }

  /** <p>For elements with 'complexContent' model: The content type empty.</p>
   * @see org.apache.ws.jaxme.xs.XSComplexType#isEmpty()
   * @see org.apache.ws.jaxme.xs.XSComplexType#getComplexContentType()
   */
  public static final XsComplexContentType EMPTY = new XsComplexContentType("empty");

  /** <p>For elements with 'complexContent' model: The content type elementOnly.</p>
   * @see org.apache.ws.jaxme.xs.XSComplexType#isElementOnly()
   * @see org.apache.ws.jaxme.xs.XSComplexType#getComplexContentType()
   */
  public static final XsComplexContentType ELEMENT_ONLY = new XsComplexContentType("elementOnly");

  /** <p>For elements with 'complexContent' model: The content type mixed.</p>
   * @see org.apache.ws.jaxme.xs.XSComplexType#isMixed()
   * @see org.apache.ws.jaxme.xs.XSComplexType#getComplexContentType()
   */
  public static final XsComplexContentType MIXED = new XsComplexContentType("mixed");

  private static final XsComplexContentType[] instances = new XsComplexContentType[]{ EMPTY, ELEMENT_ONLY, MIXED };

  public static XsComplexContentType valueOf(String pValue) {
    for (int i = 0;  i < instances.length;  i++) {
      if (instances[i].toString().equals(pValue)) {
        return instances[i];
      }
    }
    throw new IllegalArgumentException("Invalid complexContent type: " + pValue);
  }
}