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

import org.apache.ws.jaxme.xs.impl.XSSchemaImpl;
import org.apache.ws.jaxme.xs.impl.XSUtil;
import org.apache.ws.jaxme.xs.jaxb.*;
import org.apache.ws.jaxme.xs.jaxb.JAXBGlobalBindings;
import org.apache.ws.jaxme.xs.jaxb.JAXBSchema;
import org.apache.ws.jaxme.xs.jaxb.JAXBXsObjectFactory;
import org.apache.ws.jaxme.xs.parser.XSContext;
import org.apache.ws.jaxme.xs.xml.XsESchema;
import org.xml.sax.SAXException;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class JAXBSchemaImpl extends XSSchemaImpl implements JAXBSchema {
  private JAXBGlobalBindings globalBindings;

  /** <p>Creates a new instance of JAXBSchemaImpl.</p>
   */
  protected JAXBSchemaImpl(XSContext pContext, XsESchema pSchema) throws SAXException {
    super(pContext, pSchema);
  }

  public void validate() throws SAXException {
    if (isValidated()) {
      return;
    }
    super.validate();
    JAXBGlobalBindings gBindings = (JAXBGlobalBindings) XSUtil.getSingleAppinfo(getAnnotations(), JAXBGlobalBindings.class);
    globalBindings = gBindings == null ?
      ((JAXBXsObjectFactory) getXsESchema().getObjectFactory()).newJAXBGlobalBindings(getXsESchema()) : gBindings;
  }

  public JAXBGlobalBindings getJAXBGlobalBindings() {
    return globalBindings;
  }

  public String getJaxbVersion() {
    return ((JAXBXsSchema) getXsESchema()).getJaxbVersion();
  }

  public String[] getJaxbExtensionBindingPrefixes() {
    return ((JAXBXsSchema) getXsESchema()).getJaxbExtensionBindingPrefixes();
  }
}
