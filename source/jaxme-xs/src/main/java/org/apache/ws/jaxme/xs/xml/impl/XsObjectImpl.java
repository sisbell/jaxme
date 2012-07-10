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
package org.apache.ws.jaxme.xs.xml.impl;

import org.apache.ws.jaxme.xs.parser.XSContext;
import org.apache.ws.jaxme.xs.parser.impl.LocSAXException;
import org.apache.ws.jaxme.xs.xml.*;
import org.apache.ws.jaxme.xs.xml.XsObject;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.LocatorImpl;
import org.xml.sax.helpers.NamespaceSupport;


/** <p>Base class for all the types, attributes, elements, ...</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XsObjectImpl implements XsObject {
  private final XsObject parent;
  private boolean isValidated;
  private final Locator locator;

  protected XsObjectImpl(XsObject pParent) {
    if (pParent == null) {
      if (!(this instanceof XsESchema)) {
        throw new IllegalStateException("Only the schema may have a null parent.");
      }
    } else {
      if (this instanceof XsESchema) {
        throw new IllegalStateException("The schema must have a null parent.");
      }
    }
    parent = pParent;
    XSContext context = getContext();
    if (context != null) {
      Locator loc = context.getLocator();
      locator = loc == null ? null : new LocatorImpl(getContext().getLocator());
    } else {
      locator = null;
    }
  }

  public XsESchema getXsESchema() {
    if (parent == null) {
      return (XsESchema) this;
    } else {
      return parent.getXsESchema();
    }
  }

  public boolean isTopLevelObject() { return parent == null  ||  parent instanceof XsESchema; }
  public XsObject getParentObject() { return parent; }

  public XsObjectFactory getObjectFactory() { return getContext().getXsObjectFactory(); }
  public Locator getLocator() { return locator; }
  protected NamespaceSupport getNamespaceSupport() { return getContext().getNamespaceSupport(); }
  protected XsQName asXsQName(String pName) throws SAXException {
  	return asXsQName(getXsESchema(), getLocator(), getNamespaceSupport(), pName);
  }
  protected static XsQName asXsQName(XsESchema pSchema, Locator pLocator, NamespaceSupport pNss, String pName) throws SAXException {
    String[] parts = pNss.processName(pName, new String[3], false);
    if (parts == null) {
      throw new LocSAXException("Undeclared namespace prefix: " + pName, pLocator);
    }
    if (pSchema instanceof XsESchemaImpl
        &&  pSchema.getTargetNamespace() == null
        &&  (parts[0] == null  ||  "".equals(parts[0]))) {
        // xs:include may map the target namespace
        return ((XsESchemaImpl) pSchema).newXsQName(parts[1], XsQName.prefixOf(pName));
    } else {
        return new XsQName(parts[0], parts[1], XsQName.prefixOf(pName));
    }
  }
  public XSContext getContext() { return getXsESchema().getContext(); }

  protected final boolean isValidated() {
    return isValidated;
  }

  public void validate() throws SAXException {
    isValidated = true;
  }
}
