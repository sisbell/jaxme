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
import org.apache.ws.jaxme.xs.XSSimpleType;
import org.apache.ws.jaxme.xs.XSType;
import org.apache.ws.jaxme.xs.xml.XsEEnumeration;
import org.apache.ws.jaxme.xs.xml.XsEPattern;
import org.apache.ws.jaxme.xs.xml.XsGSimpleRestrictionModel;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public abstract class XSSimpleTypeRestrictionImpl extends XSSimpleTypeImpl {
  private final XSSimpleType baseType;
  private final XSType restrictedType;
  private final XsGSimpleRestrictionModel restriction;
  private final XSEnumeration[] enumerations;

  protected XSSimpleTypeRestrictionImpl(XSType pParent,
                                         XSType pRestrictedType,
                                         XsGSimpleRestrictionModel pRestriction)
      throws SAXException {
    restrictedType = pRestrictedType;
    baseType = pRestrictedType.getSimpleType();
    restriction = pRestriction;

    XsEEnumeration[] enums = restriction.getEnumerations();
    if (enums.length == 0) {
      enumerations = getBaseType().getEnumerations();
    } else {
      enumerations = new XSEnumeration[enums.length];
      for (int i = 0;  i < enums.length;  i++) {
        enumerations[i] = pParent.getXSSchema().getXSObjectFactory().newXSEnumeration(pParent, enums[i]);
      }
    }
  }

  protected XSSimpleType getBaseType() {
    return baseType;
  }

  public boolean isRestriction() { return true; }
  public XSType getRestrictedType() { return restrictedType; }

  protected XsGSimpleRestrictionModel getRestriction() {
    return restriction;
  }

  public String[][] getPattern() {
    XsEPattern[] patterns = restriction.getPatterns();
    String[][] base = getBaseType().getPattern();
    if (patterns.length == 0) {
      return base;
    } else {
      String[][] result;
      if (base == null) {
          result = new String[1][];
      } else {
	      result = new String[base.length+1][];
	      for (int i = 0;  i < base.length;  i++) {
	        result[i+1] = base[i];
	      }
      }
      String[] thisStepsPatterns = new String[patterns.length];
      for (int i = 0;  i < patterns.length;  i++) {
        thisStepsPatterns[i] = patterns[i].getValue();
      }
      result[0] = thisStepsPatterns;
      return result;
    }
  }

  public XSEnumeration[] getEnumerations() {
    return enumerations;
  }
}
