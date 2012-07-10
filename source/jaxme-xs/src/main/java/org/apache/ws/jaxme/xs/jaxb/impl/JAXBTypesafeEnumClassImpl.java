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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.ws.jaxme.xs.jaxb.JAXBJavadoc;
import org.apache.ws.jaxme.xs.jaxb.JAXBTypesafeEnumClass;
import org.apache.ws.jaxme.xs.jaxb.JAXBTypesafeEnumMember;
import org.apache.ws.jaxme.xs.jaxb.JAXBXsObjectFactory;
import org.apache.ws.jaxme.xs.xml.XsObject;


/** <p>Implementation of the TypesafeEnumClass interface.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 * @version $Id: JAXBTypesafeEnumClassImpl.java 231785 2004-02-16 23:39:59Z jochen $
 */
public class JAXBTypesafeEnumClassImpl extends JAXBXsObjectImpl implements JAXBTypesafeEnumClass {
  private String name;
  private JAXBJavadoc javadoc;
  private List typesafeEnumMembers;

  /** <p>Creates a new instance of JAXBTypesafeEnumClassImpl.java.</p>
   */
  protected JAXBTypesafeEnumClassImpl(XsObject pParent) {
    super(pParent);
  }

  public void setName(String pName) {
    name = pName;
  }

  public String getName() {
    return name;
  }

  public JAXBTypesafeEnumMember createTypesafeEnumMember() {
    if (typesafeEnumMembers == null) {
      typesafeEnumMembers = new ArrayList();
    }
    JAXBTypesafeEnumMember result = ((JAXBXsObjectFactory) getObjectFactory()).newJAXBTypesafeEnumMember(this);
    typesafeEnumMembers.add(result);
    return result;
  }

  public Iterator getTypesafeEnumMember() {
    return (typesafeEnumMembers == null ? Collections.EMPTY_LIST : typesafeEnumMembers).iterator();
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
