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
 
package org.apache.ws.jaxme.xs.types;

import org.apache.ws.jaxme.xs.XSAttributable;
import org.apache.ws.jaxme.xs.XSComplexType;
import org.apache.ws.jaxme.xs.XSObject;
import org.apache.ws.jaxme.xs.XSParser;
import org.apache.ws.jaxme.xs.XSParticle;
import org.apache.ws.jaxme.xs.XSSimpleContentType;
import org.apache.ws.jaxme.xs.XSSimpleType;
import org.apache.ws.jaxme.xs.XSType;
import org.apache.ws.jaxme.xs.xml.XsComplexContentType;
import org.apache.ws.jaxme.xs.xml.XsQName;
import org.xml.sax.SAXException;


/** <p>The "anyType" is a type with arbitrary content.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XSAnyType extends AbstractBuiltinType
    implements XSComplexType {
  private static final XsQName qName = new XsQName(XSParser.XML_SCHEMA_URI, "anyType", null);
  private static final XSAttributable[] attributes = new XSAttributable[0];
  private static final XSAnyType theInstance = new XSAnyType();

  private XSAnyType() {
  }

  public static final XSAnyType getInstance() {
    return theInstance;
  }

  public boolean isSimple() { return false; }
  public XSSimpleType getSimpleType() throws SAXException {
    throw new IllegalStateException("The anyType is a complex type.");
  }
  public XSComplexType getComplexType() throws SAXException { return this; }
  public XsQName getName() { return qName; }
  public boolean isTopLevelObject() { return true; }
  public XSObject getParentObject() { return null; }
  public boolean hasSimpleContent() { return false; }
  public boolean isEmpty() { return false; }
  public boolean isElementOnly() { return false; }
  public boolean isMixed() { return true; }
  public XSParticle getParticle() { return null; }
  public XSAttributable[] getAttributes() { return attributes; }
  public boolean isExtension() { return false; }
  public XSType getExtendedType() { return null; }
  public boolean isRestriction() { return false; }
  public XSType getRestrictedType() { return null; }
    
  public XsComplexContentType getComplexContentType() {
    throw new IllegalStateException("The builtin type " + getName() + " does not have an inner instance of XsComplexContentType.");
  }

  public XSSimpleContentType getSimpleContent() {
    throw new IllegalStateException("The builtin type " + getName() + " does not have simple content.");
  }

  public boolean isBuiltin() { return true; }
}
