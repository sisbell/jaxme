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
import org.apache.ws.jaxme.xs.XSListType;
import org.apache.ws.jaxme.xs.XSType;
import org.apache.ws.jaxme.xs.xml.XsELength;
import org.apache.ws.jaxme.xs.xml.XsEMaxLength;
import org.apache.ws.jaxme.xs.xml.XsEMinLength;
import org.apache.ws.jaxme.xs.xml.XsGSimpleRestrictionModel;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XSListTypeRestrictionImpl extends XSSimpleTypeRestrictionImpl implements XSListType {
  private final XSListType listBaseType;

  public XSListTypeRestrictionImpl(XSType pParent,
                                    XSType pRestrictedType, XsGSimpleRestrictionModel pRestriction)
      throws SAXException {
    super(pParent, pRestrictedType, pRestriction);
    listBaseType = pRestrictedType.getSimpleType().getListType();
  }

  public boolean isList() { return true; }

  public XSListType getListType() {
    return this;
  }

  public XSType getItemType() {
    return listBaseType.getItemType();
  }

  public Long getLength() {
    XsELength length = getRestriction().getLength();
    if (length == null) {
      return listBaseType.getLength();
    } else {
      return new Long(length.getValue());
    }
  }

  public Long getMinLength() {
    XsEMinLength length = getRestriction().getMinLength();
    if (length == null) {
      return listBaseType.getMinLength();
    } else {
      return new Long(length.getValue());
    }
  }

  public Long getMaxLength() {
    XsEMaxLength length = getRestriction().getMaxLength();
    if (length == null) {
      return listBaseType.getMaxLength();
    } else {
      return new Long(length.getValue());
    }
  }
}
