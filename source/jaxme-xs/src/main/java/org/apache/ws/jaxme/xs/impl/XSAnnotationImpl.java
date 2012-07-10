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
import org.apache.ws.jaxme.xs.XSAppinfo;
import org.apache.ws.jaxme.xs.XSDocumentation;
import org.apache.ws.jaxme.xs.XSObject;
import org.apache.ws.jaxme.xs.xml.XsEAnnotation;
import org.apache.ws.jaxme.xs.xml.XsEAppinfo;
import org.apache.ws.jaxme.xs.xml.XsEDocumentation;
import org.xml.sax.SAXException;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XSAnnotationImpl extends XSOpenAttrsImpl implements XSAnnotation {
  private final XSAppinfo[] appinfos;
  private final XSDocumentation[] documentations;

  protected XSAnnotationImpl(XSObject pParent, XsEAnnotation pBaseAnnotation)  throws SAXException {
    super(pParent, pBaseAnnotation);

    XsEDocumentation[] xsDocumentations = ((XsEAnnotation) getXsObject()).getDocumentations();
    documentations = new XSDocumentation[xsDocumentations.length];
    for (int i = 0;  i < xsDocumentations.length;  i++) {
      documentations[i] = getXSSchema().getXSObjectFactory().newXSDocumentation(this, xsDocumentations[i]);
    }

    XsEAppinfo[] xsAppinfos = ((XsEAnnotation) getXsObject()).getAppinfos();
    appinfos = new XSAppinfo[xsAppinfos.length];
    for (int i = 0;  i < xsAppinfos.length;  i++) {
      XsEAppinfo xsAppinfo = xsAppinfos[i];
      XSAppinfo appinfo = getXSSchema().getXSObjectFactory().newXSAppinfo(this, xsAppinfo);
      appinfos[i] = appinfo;
    }
  }

  public XSDocumentation[] getDocumentations() {
    return documentations;
  }

  public XSAppinfo[] getAppinfos() {
    return appinfos;
  }

  public void validate() throws SAXException {
  }
}
