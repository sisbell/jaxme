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

/** <p>Implementation of <code>xml:lang</code>.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XmlLang {
  private String lang;

  public XmlLang(String pLang) {
    if (pLang == null) {
      throw new NullPointerException("An URI must not be null");
    }
    lang = pLang;
  }

  public boolean equals(Object o) {
    return o != null  &&  o instanceof XmlLang  &&  lang.equals(((XmlLang) o).lang);
  }

  public int hashCode() {
    return lang.hashCode();
  }
}
