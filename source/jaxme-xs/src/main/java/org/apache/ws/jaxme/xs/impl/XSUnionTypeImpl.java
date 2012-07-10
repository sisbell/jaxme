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

import java.util.ArrayList;
import java.util.List;

import org.apache.ws.jaxme.xs.XSEnumeration;
import org.apache.ws.jaxme.xs.XSType;
import org.apache.ws.jaxme.xs.XSUnionType;
import org.apache.ws.jaxme.xs.parser.impl.LocSAXException;
import org.apache.ws.jaxme.xs.xml.XsEUnion;
import org.apache.ws.jaxme.xs.xml.XsQName;
import org.apache.ws.jaxme.xs.xml.XsTLocalSimpleType;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XSUnionTypeImpl extends XSSimpleTypeImpl implements XSUnionType {
  private static final String[][] ZERO_PATTERNS = new String[0][];
  private static final XSEnumeration[] ZERO_ENUMERATIONS = new XSEnumeration[0];
  private final List memberTypes = new ArrayList();

  public XSUnionTypeImpl(XSType pOwner,
                          XsEUnion pBaseUnion) throws SAXException {
    XsQName[] names = pBaseUnion.getMemberTypes();
    if (names != null) {
      for (int i = 0;  i < names.length;  i++) {
        XsQName name = names[i];
        XSType type = pOwner.getXSSchema().getType(name);
        if (type == null) {
          throw new LocSAXException("Unknown member type: " + name, pBaseUnion.getLocator());
        }
        type.validate();
        if (!type.isSimple()) {
          throw new LocSAXException("The member type " + name + " is complex.",
                                       pBaseUnion.getLocator());
        }
        memberTypes.add(type);
      }
    }
    XsTLocalSimpleType[] simpleTypes = pBaseUnion.getSimpleTypes();
    if (simpleTypes != null) {
      for (int i = 0;  i < simpleTypes.length;  i++) {
        XsTLocalSimpleType localSimpleType = simpleTypes[i];
        XSType type = pOwner.getXSSchema().getXSObjectFactory().newXSType(pOwner, localSimpleType);
        type.validate();
        memberTypes.add(type);
      }
    }
    if (memberTypes.size() == 0) {
      throw new LocSAXException("Neither the 'memberTypes' attribute nor the 'simpleType' child elements did define a member type.",
                                   pBaseUnion.getLocator());
    }
  }

  public boolean isUnion() { return true; }
  public boolean isRestriction() { return false; }
  public XSType getRestrictedType() {
    throw new IllegalStateException("This is a basic list type and not a restriction of another simple type.");
  }
  public XSUnionType getUnionType() { return this; }
  public String[][] getPattern() { return ZERO_PATTERNS; }
  public XSEnumeration[] getEnumerations() { return ZERO_ENUMERATIONS; }
  public XSType[] getMemberTypes() {
    return (XSType[]) memberTypes.toArray(new XSType[memberTypes.size()]);
  }
}
