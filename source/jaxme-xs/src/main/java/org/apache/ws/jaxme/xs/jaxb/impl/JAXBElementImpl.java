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
import org.apache.ws.jaxme.xs.impl.XSElementImpl;
import org.apache.ws.jaxme.xs.impl.XSUtil;
import org.apache.ws.jaxme.xs.jaxb.JAXBClass;
import org.apache.ws.jaxme.xs.jaxb.JAXBElement;
import org.apache.ws.jaxme.xs.jaxb.JAXBProperty;
import org.apache.ws.jaxme.xs.jaxb.JAXBSchemaBindings;
import org.apache.ws.jaxme.xs.parser.impl.LocSAXException;
import org.apache.ws.jaxme.xs.xml.XsTElement;
import org.xml.sax.SAXException;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class JAXBElementImpl extends XSElementImpl implements JAXBElement {
  private JAXBClass jaxbClass;
  private JAXBProperty jaxbProperty;

  /** <p>Creates a new instance of JAXBElementImpl.</p>
   */
  protected JAXBElementImpl(XSObject pParent, XsTElement pBaseElement)
      throws SAXException {
    super(pParent, pBaseElement);
  }

  public JAXBSchemaBindings getJAXBSchemaBindings() {
    return ((JAXBXsSchemaImpl) getXsObject().getXsESchema()).getJAXBSchemaBindings();
  }


  public JAXBClass getJAXBClass() { return jaxbClass; }
  public JAXBProperty getJAXBProperty() { return jaxbProperty; }

  public void validate() throws SAXException {
    if (isValidated()) {
      return;
    }
    super.validate();
    jaxbClass = (JAXBClass) XSUtil.getSingleAppinfo(getAnnotations(), JAXBClass.class);
    jaxbProperty = (JAXBProperty) XSUtil.getSingleAppinfo(getAnnotations(), JAXBProperty.class);

    if (jaxbProperty == null) {
      XsTElement elem = (XsTElement) getXsObject();
      if (elem.getRef() != null) {
        JAXBElement refElem = (JAXBElement) getXSSchema().getElement(elem.getRef());
        if (refElem != null) {
          jaxbProperty = refElem.getJAXBProperty();
        }
      }
    } else {
      if (isGlobal()) {
        if (jaxbProperty != null  &&  jaxbProperty.getBaseType() != null) {
          throw new LocSAXException("The element jaxb:property/jaxb:baseType is forbidden in global elements." +
                                     " You may set it in the reference using the element locally. [JAXB 6.8.1.2.1]",
                                     jaxbProperty.getLocator());
        }
      }
    }

    if (jaxbClass != null  &&  jaxbClass.getImplClass() != null) {
      if (isGlobal()) {
        if (jaxbClass != null  &&  jaxbClass.getImplClass() != null) {
          throw new LocSAXException("The implClass attribute is invalid for global elements. [JAXB 6.7.3.4]",
                                     jaxbClass.getLocator());
        }
      } else {
        if (jaxbClass != null  &&  jaxbClass.getImplClass() != null) {
          throw new LocSAXException("The implClass attribute is invalid for local elements. [JAXB 6.7.3.5]",
                                     jaxbClass.getLocator());
        }
      }
    }
  }
}
