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

import org.apache.ws.jaxme.xs.XSObject;
import org.apache.ws.jaxme.xs.XSSchema;
import org.apache.ws.jaxme.xs.xml.XsObject;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public abstract class XSObjectImpl implements XSObject {
  private final XSObject parent;
  private final XsObject baseObject;

  protected XSObjectImpl(XSObject pParent, XsObject pBaseObject) {
    if (pParent == null) {
      if (!(this instanceof XSSchema)) {
        throw new IllegalStateException("Null parents are allowed for XSSchema objects only.");
      }
    } else {
      if (this instanceof XSSchema) {
        throw new IllegalStateException("An XSSchema object must have a null parent.");
      }
    }
    parent = pParent;
    baseObject = pBaseObject;
  }

  public XSObject getParentObject() { return parent; }
  public XSSchema getXSSchema() {
    if (parent == null) {
      return (XSSchema) this;
    } else {
      return parent.getXSSchema();
    }
  }
  public boolean isTopLevelObject() { return parent == null  ||  parent instanceof XSSchema; }
  public Locator getLocator() { return baseObject.getLocator(); }
  protected XsObject getXsObject() { return baseObject; }
  public void validate() throws SAXException {}


  /**
   * Utility method used to call validate() on every element within an 
   * array.
   *
   * @param objects Array must not have any null elements.
   */
  protected final void validateAllIn( XSObject[] objects ) 
    throws SAXException 
  {
    if ( objects == null ) {
      return;
    }

    int numObjects = objects.length;

    for ( int i=0; i<numObjects; i++ ) {
      objects[i].validate();
    }
  }
}
