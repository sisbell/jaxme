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

/** <p>Implementation of <code>xs:NCName</code>.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XsNCName {
  private final String value;

  public XsNCName(String pValue) {
    if (pValue == null  ||  pValue.length() == 0) {
      throw new NullPointerException("An NCName cannot be null or empty.");
    }
    if (pValue.indexOf(':') >= 0) {
    	throw new IllegalArgumentException("NCName " + pValue + " is invalid, because it contains a namespace prefix");
    }
    value = pValue;
  }

  public String toString() { return value; }
  public String getValue() { return value; }
  public int hashCode() { return value.hashCode(); }
  public boolean equals(Object o) {
    return o != null  &&  o instanceof XsNCName   &&  value.equals(((XsNCName) o).value);
  }
}
