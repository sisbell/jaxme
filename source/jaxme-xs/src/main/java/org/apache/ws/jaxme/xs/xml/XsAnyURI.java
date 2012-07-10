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

/** <p>Implementation of <code>xs:anyURI</code>.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XsAnyURI {
  private String uri;

  public XsAnyURI(String pURI) {
    if (pURI == null) {
      throw new NullPointerException("An URI must not be null");
    }
    uri = pURI;
  }

  public boolean equals(Object o) {
    return o != null  &&  o instanceof XsAnyURI  &&  uri.equals(((XsAnyURI) o).uri);
  }

  public int hashCode() {
    return uri.hashCode();
  }

  public String toString() {
    return uri;
  }

  public String getURI() {
    return uri;
  }
}
