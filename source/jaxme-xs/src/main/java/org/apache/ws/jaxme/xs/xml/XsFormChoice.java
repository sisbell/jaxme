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


/** <p>Implementation of the following simple type:
 * <pre>
 *  &lt;xs:simpleType name="formChoice"&gt;
 *    &lt;xs:annotation&gt;
 *      &lt;xs:documentation&gt;
 *        A utility type, not for public use
 *      &lt;/xs:documentation&gt;
 *    &lt;/xs:annotation&gt;
 *    &lt;xs:restriction base="xs:NMTOKEN"&gt;
 *      &lt;xs:enumeration value="qualified"/&gt;
 *      &lt;xs:enumeration value="unqualified"/&gt;
 *    &lt;/xs:restriction&gt;
 *  &lt;/xs:simpleType&gt;
 * </pre></p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XsFormChoice {
  public static final XsFormChoice QUALIFIED = new XsFormChoice("qualified");
  public static final XsFormChoice UNQUALIFIED = new XsFormChoice("unqualified");
  private final String value;
  private XsFormChoice(String pValue) {
    value = pValue;
  }
  public String toString() { return value; }
  public String getValue() { return value; }
  public int hashCode() {
    return value.hashCode();
  }
  public boolean equals(Object o) {
    return o != null  &&  XsFormChoice.class.equals(o.getClass())  &&
      value.equals(((XsFormChoice) o).value);
  }
  public static XsFormChoice valueOf(String pValue) {
    if ("qualified".equals(pValue)) {
      return QUALIFIED;
    } else if ("unqualified".equals(pValue)) {
      return UNQUALIFIED;
    } else {
      throw new IllegalArgumentException("Invalid form value: " + pValue + ", expected either of 'qualified' or 'unqualified'" );
    }
  }
}
