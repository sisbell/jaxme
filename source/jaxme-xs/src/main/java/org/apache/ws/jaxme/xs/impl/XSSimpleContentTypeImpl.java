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

import org.apache.ws.jaxme.xs.XSSimpleContentType;
import org.apache.ws.jaxme.xs.XSType;
import org.apache.ws.jaxme.xs.xml.XsObject;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XSSimpleContentTypeImpl implements XSSimpleContentType {
  private final XSType simpleType, complexType;
  private final XsObject baseType;
  
  protected XSSimpleContentTypeImpl(XSType pComplexType, XSType pSimpleType,
                                     XsObject pBaseType) {
    if (!pSimpleType.isSimple()) {
      throw new IllegalStateException("Embedded content type must be simple.");
    }
    simpleType = pSimpleType;
    if (pComplexType.isSimple()) {
      throw new IllegalStateException("Embedded content type must be simple.");
    }
    complexType = pComplexType;
    baseType = pBaseType;
  }

  protected XSType getComplexType() { return complexType; }
  protected XsObject getBaseType() { return baseType; }
  public XSType getType() { return simpleType; }
}
