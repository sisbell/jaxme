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

import org.apache.ws.jaxme.xs.XSAtomicType;
import org.apache.ws.jaxme.xs.XSType;
import org.apache.ws.jaxme.xs.XSUnionType;
import org.apache.ws.jaxme.xs.xml.XsGSimpleRestrictionModel;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XSUnionTypeRestrictionImpl extends XSSimpleTypeRestrictionImpl implements XSUnionType {
  private final XSUnionType unionBaseType;

  protected XSUnionTypeRestrictionImpl(XSType pParent, XSType pRestrictedType, XsGSimpleRestrictionModel pRestriction) throws SAXException {
    super(pParent, pRestrictedType, pRestriction);
    unionBaseType = pRestrictedType.getSimpleType().getUnionType();
  }

  public boolean isUnion() { return true; }

  public XSUnionType getUnionType() {
    return this;
  }

  public XSType[] getMemberTypes() {
    return unionBaseType.getMemberTypes();
  }
}
