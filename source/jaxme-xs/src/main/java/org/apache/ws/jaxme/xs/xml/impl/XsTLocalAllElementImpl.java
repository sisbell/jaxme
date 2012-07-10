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

import org.apache.ws.jaxme.xs.xml.XsObject;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XsTLocalAllElementImpl extends XsTLocalElementImpl {
  protected XsTLocalAllElementImpl(XsObject pParent) {
    super(pParent);
  }

  public void setMaxOccurs(String pValue) {
    boolean valid = !"unbounded".equals(pValue);
    if (valid) {
      try {
        int i = Integer.parseInt(pValue);
        valid = i == 0  ||  i == 1;
      } catch (Exception e) {
        valid = false;
      }
    }
    if (!valid) {
      throw new IllegalArgumentException("Invalid value for 'maxOccurs': " + pValue + "; must be 0 or 1");
    }
    super.setMaxOccurs(pValue);
  }
  public void setMinOccurs(int pValue) {
    if (pValue != 0  &&  pValue != 1) {
      throw new IllegalArgumentException("Invalid value for 'minOccurs': " + pValue + "; must be 0 or 1");
    }
    super.setMinOccurs(pValue);
  }
}
