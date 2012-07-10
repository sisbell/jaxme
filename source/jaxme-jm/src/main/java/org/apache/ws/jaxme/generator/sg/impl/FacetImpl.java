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
package org.apache.ws.jaxme.generator.sg.impl;

import org.apache.ws.jaxme.generator.sg.Facet;
import org.apache.ws.jaxme.xs.XSEnumeration;
import org.apache.ws.jaxme.xs.XSType;

/** 
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class FacetImpl implements Facet {
  private final Facet.Type type;
  private final String[] values;
  private final String value;
  private final Long numValue;

  /** <p>Creates a new enumeration facet.</p>
   */
  public FacetImpl(XSType pType, XSEnumeration[] pEnumerations) {
    type = Facet.ENUMERATION;
    values = new String[pEnumerations.length];
    for (int i = 0;  i < pEnumerations.length;  i++) {
      values[i] = pEnumerations[i].getValue();
    }
    value = null;
    numValue = null;
  }

  public Type getType() { return type; }
  public String[] getValues() {
    if (values == null) {
      throw new IllegalStateException("The facet type " + type + " doesn't support the getValues() method.");
    }
    return values;
  }

  public String getValue() {
    if (value == null) {
      throw new IllegalStateException("The facet type " + type + " doesn't support the getValue() method.");
    }
    return value;
  }

  public long getNumValue() {
    if (numValue == null) {
      throw new IllegalStateException("The facet type " + type + " doesn't support the getNumValue() method.");
    }
    return numValue.longValue();
  }
}
