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
package org.apache.ws.jaxme.generator.sg.impl;

import org.apache.ws.jaxme.generator.sg.ComplexTypeSG;
import org.apache.ws.jaxme.generator.sg.PropertySG;
import org.apache.ws.jaxme.generator.sg.PropertySGChain;
import org.apache.ws.jaxme.generator.sg.SimpleContentSG;
import org.apache.ws.jaxme.generator.sg.SimpleContentSGChain;
import org.apache.ws.jaxme.generator.sg.TypeSG;
import org.apache.ws.jaxme.xs.XSType;
import org.xml.sax.SAXException;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class JAXBSimpleContentTypeSG implements SimpleContentSGChain {
  private XSType xsType;
  private PropertySG propertySG;
  private final TypeSG typeSG, contentTypeSG;

  /** <p>Creates a new instance of JAXBSimpleContentSG.java.</p>
   */
  protected JAXBSimpleContentTypeSG(ComplexTypeSG pComplexTypeSG, XSType pType) throws SAXException {
    xsType = pType;
    typeSG = pComplexTypeSG.getTypeSG();
    XSType theSimpleType = pType.getComplexType().getSimpleContent().getType();
    contentTypeSG = typeSG.getFactory().getTypeSG(theSimpleType, pComplexTypeSG.getClassContext(), null, null);
  }

  public Object newPropertySGChain(SimpleContentSG pController) throws SAXException {
    if (xsType == null) {
      throw new IllegalStateException("A new PropertySG has already been created.");
    }
    PropertySGChain result = new JAXBPropertySG(typeSG, xsType);
    xsType = null;  // Make this available to garbage collection.
    return result;
  }

  public void init(SimpleContentSG pController) throws SAXException {
    PropertySGChain chain = (PropertySGChain) pController.newPropertySGChain();
    propertySG = new PropertySGImpl(chain);
    propertySG.init();
  }

  public PropertySG getPropertySG(SimpleContentSG pController) throws SAXException { return propertySG; }
  public TypeSG getTypeSG(SimpleContentSG pController) throws SAXException { return typeSG; }
  public TypeSG getContentTypeSG(SimpleContentSG pController) throws SAXException { return contentTypeSG; }
}
