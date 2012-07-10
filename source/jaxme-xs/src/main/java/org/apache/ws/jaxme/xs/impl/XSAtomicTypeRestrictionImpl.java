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
import org.apache.ws.jaxme.xs.xml.XsEFractionDigits;
import org.apache.ws.jaxme.xs.xml.XsELength;
import org.apache.ws.jaxme.xs.xml.XsEMaxExclusive;
import org.apache.ws.jaxme.xs.xml.XsEMaxInclusive;
import org.apache.ws.jaxme.xs.xml.XsEMaxLength;
import org.apache.ws.jaxme.xs.xml.XsEMinExclusive;
import org.apache.ws.jaxme.xs.xml.XsEMinInclusive;
import org.apache.ws.jaxme.xs.xml.XsEMinLength;
import org.apache.ws.jaxme.xs.xml.XsEPattern;
import org.apache.ws.jaxme.xs.xml.XsETotalDigits;
import org.apache.ws.jaxme.xs.xml.XsEWhiteSpace;
import org.apache.ws.jaxme.xs.xml.XsGSimpleRestrictionModel;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XSAtomicTypeRestrictionImpl extends XSSimpleTypeRestrictionImpl implements XSAtomicType {
	private static final String[] ZERO_PATTERNS = new String[0];
	private final XSAtomicType atomicBaseType;

  /** Creates a new atomic type restricting <code>pBaseType</code>
   * with the constraints <code>pRestriction</code>.
   */
  public XSAtomicTypeRestrictionImpl(XSType pParent,
                                     XSType pBaseType, XsGSimpleRestrictionModel pRestriction)
      throws SAXException {
    super(pParent, pBaseType, pRestriction);
    atomicBaseType = pBaseType.getSimpleType().getAtomicType();
  }

  public boolean isAtomic() { return true; }

  public XSAtomicType getAtomicType() {
    return this;
  }

  public Long getFractionDigits() {
    XsEFractionDigits fractionDigits = getRestriction().getFractionDigits();
    if (fractionDigits == null) {
      return atomicBaseType.getFractionDigits();
    } else {
      return new Long(fractionDigits.getValue());
    }
  }

  public Long getLength() {
    XsELength length = getRestriction().getLength();
    if (length == null) {
      return atomicBaseType.getLength();
    } else {
      return new Long(length.getValue());
    }
  }

  public String getMaxExclusive() {
    XsEMaxExclusive maxExclusive = getRestriction().getMaxExclusive();
    if (maxExclusive == null) {
      return atomicBaseType.getMaxExclusive();
    } else {
      return maxExclusive.getValue();
    }
  }

  public String getMaxInclusive() {
    XsEMaxInclusive maxInclusive = getRestriction().getMaxInclusive();
    if (maxInclusive == null) {
      return atomicBaseType.getMaxInclusive();
    } else {
      return maxInclusive.getValue();
    }
  }

  public Long getMaxLength() {
    XsEMaxLength length = getRestriction().getMaxLength();
    if (length == null) {
      return atomicBaseType.getMaxLength();
    } else {
      return new Long(length.getValue());
    }
  }

  public String getMinExclusive() {
    XsEMinExclusive minExclusive = getRestriction().getMinExclusive();
    if (minExclusive == null) {
      return atomicBaseType.getMinExclusive();
    } else {
      return minExclusive.getValue();
    }
  }

  public String getMinInclusive() {
    XsEMinInclusive minInclusive = getRestriction().getMinInclusive();
    if (minInclusive == null) {
      return atomicBaseType.getMinInclusive();
    } else {
      return minInclusive.getValue();
    }
  }

  public Long getMinLength() {
    XsEMinLength length = getRestriction().getMinLength();
    if (length == null) {
      return atomicBaseType.getMinLength();
    } else {
      return new Long(length.getValue());
    }
  }

  public Long getTotalDigits() {
    XsETotalDigits totalDigits = getRestriction().getTotalDigits();
    if (totalDigits == null) {
      return atomicBaseType.getTotalDigits();
    } else {
      return new Long(totalDigits.getValue());
    }
  }

  public boolean isReplacing() {
    XsEWhiteSpace whiteSpace = getRestriction().getWhiteSpace();
    if (whiteSpace == null) {
      return atomicBaseType.isReplacing();
    } else {
      return whiteSpace.isReplacing() || whiteSpace.isCollapsing();
    }
  }

  public boolean isCollapsing() {
    XsEWhiteSpace whiteSpace = getRestriction().getWhiteSpace();
    if (whiteSpace == null) {
      return atomicBaseType.isReplacing();
    } else {
      return whiteSpace.isCollapsing();
    }
  }

  public String[] getPatterns() {
	  XsEPattern[] patterns = getRestriction().getPatterns();
	  if (patterns == null  ||  patterns.length == 0) {
		  return ZERO_PATTERNS;
	  } else {
		  String[] result = new String[patterns.length];
		  for (int i = 0;  i < patterns.length;  i++) {
			  result[i] = patterns[i].getValue();
		  }
		  return result;
	  }
  }
}
