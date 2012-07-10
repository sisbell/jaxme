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

import org.apache.ws.jaxme.xs.XSEnumeration;
import org.apache.ws.jaxme.xs.XSListType;
import org.apache.ws.jaxme.xs.XSType;
import org.apache.ws.jaxme.xs.parser.impl.LocSAXException;
import org.apache.ws.jaxme.xs.xml.XsEList;
import org.apache.ws.jaxme.xs.xml.XsQName;
import org.apache.ws.jaxme.xs.xml.XsTLocalSimpleType;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XSListTypeImpl extends XSSimpleTypeImpl implements XSListType {
  private static final String[][] ZERO_PATTERNS = new String[0][];
  private static final XSEnumeration[] ZERO_ENUMERATIONS = new XSEnumeration[0];
  private final XSType itemType;

  public boolean isRestriction() { return false; }
  public XSType getRestrictedType() {
    throw new IllegalStateException("This is a basic list type and not a restriction of another simple type.");
  }

  public XSListTypeImpl(XSType pOwner, XsEList pBaseList) throws SAXException {
    XsQName itemTypeName = pBaseList.getItemType();
    if (itemTypeName == null) {
      XsTLocalSimpleType simpleType = pBaseList.getSimpleType();
      if (simpleType == null) {
        throw new LocSAXException("You must either set the 'itemType' attribute or add a 'simpleType' element.",
                                   pBaseList.getLocator());
      }
      XSType type = pOwner.getXSSchema().getXSObjectFactory().newXSType(pOwner, simpleType);
      type.validate();
      itemType = type;
    } else {
      XSType type = pOwner.getXSSchema().getType(itemTypeName);
      if (type == null) {
        throw new LocSAXException("Unknown item type: " + itemTypeName, pBaseList.getLocator());
      }
      type.validate();
      if (!type.isSimple()) {
        throw new LocSAXException("The item type " + itemTypeName + " is complex.",
                                     pBaseList.getLocator());
      }
      itemType = type;
    }
  }

  public boolean isList() { return true; }
  public XSListType getListType() { return this; }
  public String[][] getPattern() { return ZERO_PATTERNS; }
  public XSEnumeration[] getEnumerations() { return ZERO_ENUMERATIONS; }
  public Long getLength() { return null; }
  public Long getMinLength() { return null; }
  public Long getMaxLength() { return null; }
  public XSType getItemType() { return itemType; }
}
