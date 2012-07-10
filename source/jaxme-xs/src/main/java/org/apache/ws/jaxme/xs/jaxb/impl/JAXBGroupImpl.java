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

import org.apache.ws.jaxme.xs.XSObject;
import org.apache.ws.jaxme.xs.impl.XSGroupImpl;
import org.apache.ws.jaxme.xs.impl.XSUtil;
import org.apache.ws.jaxme.xs.jaxb.JAXBClass;
import org.apache.ws.jaxme.xs.jaxb.JAXBGroup;
import org.apache.ws.jaxme.xs.jaxb.JAXBSchemaBindings;
import org.apache.ws.jaxme.xs.jaxb.JAXBXsSchema;
import org.apache.ws.jaxme.xs.parser.impl.LocSAXException;
import org.apache.ws.jaxme.xs.xml.XsEChoice;
import org.apache.ws.jaxme.xs.xml.XsESequence;
import org.apache.ws.jaxme.xs.xml.XsTAll;
import org.apache.ws.jaxme.xs.xml.XsTGroupRef;
import org.apache.ws.jaxme.xs.xml.XsTNamedGroup;
import org.xml.sax.SAXException;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class JAXBGroupImpl extends XSGroupImpl implements JAXBGroup {
  private JAXBClass jaxbClass;

  /** <p>Creates a new instance of JAXBGroupImpl.</p>
   */
  protected JAXBGroupImpl(XSObject pParent, XsTGroupRef pBaseGroup)
      throws SAXException {
    super(pParent, pBaseGroup);
  }

  /** <p>Creates a new instance of JAXBGroupImpl.</p>
   */
  protected JAXBGroupImpl(XSObject pParent, XsTNamedGroup pBaseGroup)
      throws SAXException {
    super(pParent, pBaseGroup);
  }

  /** <p>Creates a new instance of JAXBGroupImpl.</p>
   */
  protected JAXBGroupImpl(XSObject pParent, XsEChoice pChoice)
      throws SAXException {
    super(pParent, pChoice);
  }

  /** <p>Creates a new instance of JAXBGroupImpl.</p>
   */
  protected JAXBGroupImpl(XSObject pParent, XsESequence pSequence)
      throws SAXException {
    super(pParent, pSequence);
  }

  /** <p>Creates a new instance of JAXBGroupImpl.</p>
   */
  public JAXBGroupImpl(XSObject pParent, XsTAll pAll)
      throws SAXException {
    super(pParent, pAll);
  }

  public JAXBSchemaBindings getJAXBSchemaBindings() {
    return ((JAXBXsSchema) getXsObject().getXsESchema()).getJAXBSchemaBindings();
  }

  public JAXBClass getJAXBClass() {
    return jaxbClass;
  }

  public void validate() throws SAXException {
    if (isValidated()) {
      return;
    }
    super.validate();
    jaxbClass = (JAXBClass) XSUtil.getSingleAppinfo(getAnnotations(), JAXBClass.class);

    if (!isGlobal()  &&  jaxbClass != null  &&  jaxbClass.getImplClass() != null) {
      throw new LocSAXException("The implClass attribute is valid for global groups only.",
                                 jaxbClass.getLocator());
    }
  }
}
