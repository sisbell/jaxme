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

import org.apache.ws.jaxme.xs.XSAnnotation;
import org.apache.ws.jaxme.xs.XSEnumeration;
import org.apache.ws.jaxme.xs.XSObject;
import org.apache.ws.jaxme.xs.xml.XsEAnnotation;
import org.apache.ws.jaxme.xs.xml.XsEEnumeration;
import org.xml.sax.SAXException;


/** <p>Implementation of an <code>xs:enumeration</code> facet, as specified
 * by {@link org.apache.ws.jaxme.xs.XSEnumeration}.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XSEnumerationImpl extends XSObjectImpl implements XSEnumeration {
  private final XSAnnotation[] annotations;

  /** <p>Creates a new instance of XSEnumerationImpl.java.</p>
   */
  public XSEnumerationImpl(XSObject pParent, XsEEnumeration pBaseEnumeration) throws SAXException {
    super(pParent, pBaseEnumeration);
    if (pBaseEnumeration.getValue() == null) {
      throw new NullPointerException("Missing attribute: 'value'");
    }
    XsEAnnotation xsAnnotation = pBaseEnumeration.getAnnotation();
    if (xsAnnotation == null) {
      annotations = new XSAnnotation[0];
    } else {
      annotations = new XSAnnotation[]{getXSSchema().getXSObjectFactory().newXSAnnotation(this, xsAnnotation)};
    }
  }

  public String getValue() { return ((XsEEnumeration) getXsObject()).getValue(); }

  public XSAnnotation[] getAnnotations() { return annotations; }
}
