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
package org.apache.ws.jaxme.xs.impl;

import org.apache.ws.jaxme.xs.XSAppinfo;
import org.apache.ws.jaxme.xs.XSObject;
import org.apache.ws.jaxme.xs.xml.XsAnyURI;
import org.apache.ws.jaxme.xs.xml.XsEAppinfo;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XSAppinfoImpl extends XSObjectImpl implements XSAppinfo {
  /** <p>Creates a new instance of XSAppinfoImpl.</p>
   */
  protected XSAppinfoImpl(XSObject pParent, XsEAppinfo pBaseObject) {
    super(pParent, pBaseObject);
  }

  public XsAnyURI getSource() {
    return ((XsEAppinfo) getXsObject()).getSource();
  }

  public Object[] getChilds() {
    return ((XsEAppinfo) getXsObject()).getChilds();
  }

  public String getText() {
    StringBuffer sb = new StringBuffer();
    Object[] childs = ((XsEAppinfo) getXsObject()).getChilds();
    for (int i = 0;  i < childs.length;  i++) {
      if (childs[i] instanceof String) {
        sb.append((String) childs[i]);
      }
    }
    return sb.toString();
  }
}
