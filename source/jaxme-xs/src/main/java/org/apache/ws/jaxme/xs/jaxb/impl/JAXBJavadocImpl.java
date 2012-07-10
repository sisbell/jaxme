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
import org.apache.ws.jaxme.xs.xml.XsObject;
import org.apache.ws.jaxme.xs.xml.impl.XsObjectImpl;


/** <p>Implementation of the Javadoc interface.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 * @version $Id: JAXBJavadocImpl.java 231785 2004-02-16 23:39:59Z jochen $
 */
public class JAXBJavadocImpl extends XsObjectImpl implements JAXBJavadoc {
  /** <p>Creates a new instance of JAXBJavadocImpl.</p>
   */
  protected JAXBJavadocImpl(XsObject pParent) {
    super(pParent);
  }

  private String text;

  public void addText(String pText) {
    text = pText;
  }

  public String getText() {
    return text;
  }
}
