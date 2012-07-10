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

import org.apache.ws.jaxme.xs.XSDocumentation;
import org.apache.ws.jaxme.xs.XSObject;
import org.apache.ws.jaxme.xs.xml.XmlLang;
import org.apache.ws.jaxme.xs.xml.XsAnyURI;
import org.apache.ws.jaxme.xs.xml.XsEDocumentation;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XSDocumentationImpl extends XSObjectImpl implements XSDocumentation {

  /** <p>Creates a new instance of XSDocumentationImpl.java.</p>
   */
  protected XSDocumentationImpl(XSObject pParent, XsEDocumentation pBaseDocumentation) {
    super(pParent, pBaseDocumentation);
  }

  /* @see org.apache.ws.jaxme.xs.XSDocumentation#getLanguage()
   */
  public XmlLang getLanguage() {
    return ((XsEDocumentation) getXsObject()).getLang();
  }

  /* @see org.apache.ws.jaxme.xs.XSDocumentation#getSource()
   */
  public XsAnyURI getSource() {
    return ((XsEDocumentation) getXsObject()).getSource();
  }

  /* @see org.apache.ws.jaxme.xs.XSDocumentation#getChilds()
   */
  public Object[] getChilds() {
    // TODO Auto-generated method stub
    return null;
  }

  /* @see org.apache.ws.jaxme.xs.XSDocumentation#getText()
   */
  public String getText() {
    return ((XsEDocumentation) getXsObject()).getText();
  }
}
