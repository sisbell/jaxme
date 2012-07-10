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

import org.apache.ws.jaxme.xs.XSAttributable;
import org.apache.ws.jaxme.xs.XSAttribute;
import org.apache.ws.jaxme.xs.XSAttributeGroup;
import org.apache.ws.jaxme.xs.XSObject;
import org.apache.ws.jaxme.xs.XSWildcard;
import org.apache.ws.jaxme.xs.parser.impl.LocSAXException;
import org.apache.ws.jaxme.xs.xml.XsESchema;
import org.apache.ws.jaxme.xs.xml.XsGAttrDecls;
import org.apache.ws.jaxme.xs.xml.XsNCName;
import org.apache.ws.jaxme.xs.xml.XsQName;
import org.apache.ws.jaxme.xs.xml.XsTAttribute;
import org.apache.ws.jaxme.xs.xml.XsTAttributeGroup;
import org.apache.ws.jaxme.xs.xml.XsTAttributeGroupRef;
import org.apache.ws.jaxme.xs.xml.XsTWildcard;
import org.xml.sax.SAXException;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XSAttributeGroupImpl extends XSOpenAttrsImpl implements XSAttributeGroup {
  private final XsQName name;
  private boolean isValidated;
  private XSAttributable[] attributes;

  protected XsTAttributeGroup getXsTAttributeGroup() {
    return (XsTAttributeGroup) getXsObject();
  }

  protected boolean isReference() {
    return getXsTAttributeGroup().getRef() != null;
  }

  protected XSAttributeGroupImpl(XSObject pParent, XsTAttributeGroup pBaseGroup)
      throws SAXException {
    super(pParent, pBaseGroup);
    XsQName qName;
    if (isReference()) {
      qName = getXsTAttributeGroup().getRef();
    } else {
      XsNCName myName = pBaseGroup.getName();
      if (myName == null) {
        throw new LocSAXException("Invalid attribute group: Neither of its 'name' or 'ref' attributes are set.",
                                     pBaseGroup.getLocator());
      } else {
        XsESchema schema = pBaseGroup.getXsESchema();
        qName = new XsQName(schema.getTargetNamespace(), myName.toString(), schema.getTargetNamespacePrefix());
      }
    }
    name = qName;
  }

  public XsQName getName() {
    return name;
  }

  public void validate() throws SAXException {
    if (isValidated) {
      return;
    } else {
      isValidated = true;
    }

    if (isReference()) {
      XSAttributeGroup referencedGroup = getXSSchema().getAttributeGroup(getName());
      if (referencedGroup == null) {
        throw new LocSAXException("Invalid attribute group: Unknown attribute group " + name + " referenced",
                                   getLocator());
      }
      referencedGroup.validate();
      attributes = referencedGroup.getAttributes();
    } else {
      XsTAttributeGroup attributeGroup = (XsTAttributeGroup) getXsObject();
      attributes = getAttributes(this, attributeGroup);
    }
  }

  public XSAttributable[] getAttributes() {
    return attributes;
  }

  protected static XSAttributable[] getAttributes(XSObjectImpl pObject,
                                                  XsGAttrDecls pAttrDecls) throws SAXException {
    List attributes = new ArrayList();
    Object[] allAttributes = pAttrDecls.getAllAttributes();
    for (int i = 0;  i < allAttributes.length;  i++) {
      Object o = allAttributes[i];
      if (o == null) {
        throw new NullPointerException("Null attribute detected.");
      } else if (o instanceof XsTAttribute) {
        XsTAttribute xsTAttr = (XsTAttribute) o;
        if (XsTAttribute.PROHIBITED.equals(xsTAttr.getUse())) {
        	continue;
        }
        XSAttribute attribute = pObject.getXSSchema().getXSObjectFactory().newXSAttribute(pObject, xsTAttr);
        attribute.validate();
        attributes.add(attribute);
      } else if (o instanceof XsTAttributeGroupRef) {
        XsTAttributeGroupRef agRef = (XsTAttributeGroupRef) o;
        XsQName ref = agRef.getRef();
        if (ref == null) {
          throw new LocSAXException("Invalid attribute group: Missing 'ref' attribute", pObject.getLocator());
        }
        XSAttributeGroup attributeGroup = pObject.getXSSchema().getAttributeGroup(ref);
        if (attributeGroup == null) {
          throw new LocSAXException("Unknown attribute group name: " + ref, pObject.getLocator());
        }
        attributeGroup.validate();
        XSAttributable[] agAttributes = attributeGroup.getAttributes();
        for (int j = 0;  j < agAttributes.length;  j++) {
          attributes.add(agAttributes[j]);
        }
      } else if (o instanceof XsTAttributeGroup) {
        XsTAttributeGroup ag = (XsTAttributeGroup) o;
        XSAttributable[] agAttributes = getAttributes(pObject, ag);
        for (int j = 0;  j < agAttributes.length;  j++) {
          attributes.add(agAttributes[j]);
        }
      } else if (o instanceof XsTWildcard) {
        XSWildcard wildcard = pObject.getXSSchema().getXSObjectFactory().newXSWildcard(pObject, (XsTWildcard) o);
        wildcard.validate();
        attributes.add(wildcard);
      } else {
        throw new IllegalStateException("Unknown attribute type: " + o.getClass().getName());
      }
    }
    return (XSAttributable[]) attributes.toArray(new XSAttributable[attributes.size()]);
  }
}
