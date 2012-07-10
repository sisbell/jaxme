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

import org.apache.ws.jaxme.xs.XSAnnotation;
import org.apache.ws.jaxme.xs.XSAttribute;
import org.apache.ws.jaxme.xs.XSObject;
import org.apache.ws.jaxme.xs.XSType;
import org.apache.ws.jaxme.xs.parser.impl.LocSAXException;
import org.apache.ws.jaxme.xs.types.XSAnySimpleType;
import org.apache.ws.jaxme.xs.xml.XsAnyURI;
import org.apache.ws.jaxme.xs.xml.XsEAnnotation;
import org.apache.ws.jaxme.xs.xml.XsESchema;
import org.apache.ws.jaxme.xs.xml.XsFormChoice;
import org.apache.ws.jaxme.xs.xml.XsNCName;
import org.apache.ws.jaxme.xs.xml.XsQName;
import org.apache.ws.jaxme.xs.xml.XsTAttribute;
import org.apache.ws.jaxme.xs.xml.XsTLocalSimpleType;
import org.xml.sax.SAXException;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XSAttributeImpl extends XSOpenAttrsImpl implements XSAttribute {
  private final XsQName name;
  private final XsEAnnotation xsAnnotation;
  private final boolean isGlobal;
  private XSAnnotation[] annotations;
  private boolean isValidated;
  private XSType type;

  protected XsTAttribute getXsTAttribute() {
    return (XsTAttribute) getXsObject();
  }

  protected boolean isReference() {
    return getXsTAttribute().getRef() != null;
  }

  protected boolean isInnerSimpleType() {
    return getXsTAttribute().getSimpleType() != null;
  }

  protected XSAttributeImpl(XSObject pParent, XsTAttribute pBaseAttribute) throws SAXException {
    super(pParent, pBaseAttribute);
    if (isReference()) {
      this.name = pBaseAttribute.getRef();
      isGlobal = pBaseAttribute.isGlobal();
    } else {
      XsNCName myName = pBaseAttribute.getName();
      if (myName == null) {
        throw new LocSAXException("Invalid attribute: Neither of its 'name' or 'ref' attributes are set.",
                                     pBaseAttribute.getLocator());
      }
      XsESchema schema = pBaseAttribute.getXsESchema();
      XsAnyURI namespace;
      String namespacePrefix;
      boolean qualified = isGlobal = pBaseAttribute.isGlobal();
      if (!qualified) {
        XsFormChoice form = pBaseAttribute.getForm();
        if (form == null) {
          form = schema.getAttributeFormDefault();
        }
        qualified = XsFormChoice.QUALIFIED.equals(form);
      }
      if (qualified) {
        namespace = schema.getTargetNamespace();
        namespacePrefix = schema.getTargetNamespacePrefix();
      } else {
        namespace = null;
        namespacePrefix = null;
      }
      this.name = new XsQName(namespace, myName.toString(), namespacePrefix);
    }

    xsAnnotation = pBaseAttribute.getAnnotation();
  }

  public boolean isGlobal() {
    return isGlobal;
  }

  public XsQName getName() {
    return name;
  }

  public XSType getType() {
    return type;
  }

  public XSAnnotation[] getAnnotations() {
    return annotations;
  }

  protected boolean isValidated() {
    return isValidated;
  }

  public void validate() throws SAXException {
    if (isValidated()) {
      return;
    } else {
      isValidated = true;
    }

    if (xsAnnotation == null) {
      annotations = new XSAnnotation[0];
    } else {
      XSAnnotation ann = getXSSchema().getXSObjectFactory().newXSAnnotation(this, xsAnnotation);
      annotations = new XSAnnotation[]{ ann };
      ann.validate();
    }

    XSType myType;
    if (isReference()) {
      XSAttribute attribute = getXSSchema().getAttribute(getName());
      if (attribute == null) {
          throw new LocSAXException("Invalid attribute reference: No type named " + getName() + " defined.",
                                       getXsTAttribute().getLocator());
      }
      attribute.validate();
      myType = attribute.getType();
      if (myType == null) {
          throw new IllegalStateException("The referenced attributes type must not be null.");
      }
    } else if (isInnerSimpleType()) {
      XsTLocalSimpleType innerSimpleType = getXsTAttribute().getSimpleType();
      myType = getXSSchema().getXSObjectFactory().newXSType(this, innerSimpleType);
      myType.validate();
    } else {
      XsQName typeName = getXsTAttribute().getType();
      if (typeName == null) {
        typeName = XSAnySimpleType.getInstance().getName();
      }
      myType = getXSSchema().getType(typeName);
      if (myType == null) {
        throw new LocSAXException("Invalid attribute: The referenced type " + typeName + " is not defined in the schema.",
                                     getXsTAttribute().getLocator());
      }
    }
    this.type = myType;
  }

  public boolean isOptional() {
    XsTAttribute.Use use = getXsTAttribute().getUse();
    if (XsTAttribute.PROHIBITED.equals(use)) {
      throw new IllegalStateException("This attribute is prohibited.");
    }
    return XsTAttribute.OPTIONAL.equals(use);
  }

  public String getDefault() {
    return getXsTAttribute().getDefault();
  }

  public String getFixed() {
    return getXsTAttribute().getFixed();
  }
}
