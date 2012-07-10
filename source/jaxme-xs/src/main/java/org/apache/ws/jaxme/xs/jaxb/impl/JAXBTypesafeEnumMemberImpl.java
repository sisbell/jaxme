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
package org.apache.ws.jaxme.xs.jaxb.impl;

import org.apache.ws.jaxme.xs.jaxb.JAXBJavadoc;
import org.apache.ws.jaxme.xs.jaxb.JAXBTypesafeEnumMember;
import org.apache.ws.jaxme.xs.xml.XsObject;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class JAXBTypesafeEnumMemberImpl extends JAXBXsObjectImpl
    implements JAXBTypesafeEnumMember {
  /** <p>Creates a new instance of JAXBTypesafeEnumClassImpl.java.</p>
   */
  protected JAXBTypesafeEnumMemberImpl(XsObject pParent) {
    super(pParent);
  }

  private String name, value;
  private JAXBJavadoc javadoc;

  public void setName(String pName) {
    name = pName;
  }

  public String getName() {
    return name;
  }

  public void setValue(String pValue) {
    value = pValue;
  }

  public String getValue() {
    return value;
  }

  public JAXBJavadoc createJavadoc() {
    if (javadoc != null) {
      throw new IllegalStateException("Multiple javadoc elements are not supported.");
    }
    javadoc = getJAXBXsObjectFactory().newJAXBJavadoc(this);
    return javadoc;
  }

  public JAXBJavadoc getJavadoc() {
    return javadoc;
  }
}