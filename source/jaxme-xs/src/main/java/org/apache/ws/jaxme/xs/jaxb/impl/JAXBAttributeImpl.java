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
import org.apache.ws.jaxme.xs.impl.XSAttributeImpl;
import org.apache.ws.jaxme.xs.impl.XSUtil;
import org.apache.ws.jaxme.xs.jaxb.JAXBAttribute;
import org.apache.ws.jaxme.xs.jaxb.JAXBProperty;
import org.apache.ws.jaxme.xs.jaxb.JAXBSchemaBindings;
import org.apache.ws.jaxme.xs.jaxb.JAXBXsSchema;
import org.apache.ws.jaxme.xs.parser.impl.LocSAXException;
import org.apache.ws.jaxme.xs.xml.XsTAttribute;
import org.xml.sax.SAXException;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class JAXBAttributeImpl extends XSAttributeImpl implements JAXBAttribute {
  private JAXBProperty jaxbProperty;

  /** <p>Creates a new instance of JAXBAttributeImpl.</p>
   */
  protected JAXBAttributeImpl(XSObject pParent, XsTAttribute pBaseAttribute)
      throws SAXException {
    super(pParent, pBaseAttribute);
  }

  public JAXBSchemaBindings getJAXBSchemaBindings() {
    return ((JAXBXsSchema) getXsObject().getXsESchema()).getJAXBSchemaBindings();
  }

  public JAXBProperty getJAXBProperty() { return jaxbProperty; }

  public void validate() throws SAXException {
    if (isValidated()) {
      return;
    } else {
      super.validate();
      jaxbProperty = (JAXBProperty) XSUtil.getSingleAppinfo(getAnnotations(), JAXBProperty.class);
    }

    if (jaxbProperty == null) {
      XsTAttribute attr = (XsTAttribute) getXsObject();
      if (attr.getRef() != null) {
        JAXBAttribute refAttr = (JAXBAttribute) getXSSchema().getAttribute(attr.getRef());
        if (refAttr != null) {
          jaxbProperty = refAttr.getJAXBProperty();
        }
      }
    } else {
      if (isGlobal()  &&  jaxbProperty != null  &&  jaxbProperty.getBaseType() != null) {
        throw new LocSAXException("The element jaxb:property/jaxb:baseType is forbidden in global attributes." +
                                   " You may set it in the reference using the attribute locally. [JAXB 6.8.1.2.1]",
                                   jaxbProperty.getLocator());
      }
    }
  }
}
