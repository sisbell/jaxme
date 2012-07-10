/*
 * Copyright 2003,2004  The Apache Software Foundation
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

import org.apache.ws.jaxme.xs.XSAnnotation;
import org.apache.ws.jaxme.xs.XSSchema;
import org.apache.ws.jaxme.xs.XSType;
import org.apache.ws.jaxme.xs.xml.XsSchemaHeader;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/** <p>An abstract type, to be extended by the builtin types.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public abstract class AbstractBuiltinType implements XSType {
  private static final Attributes openAttrs = new AttributesImpl();
  private static final XSAnnotation[] annotations = new XSAnnotation[0];
  
  public Attributes getOpenAttributes() { return openAttrs; }
  public XSAnnotation[] getAnnotations() { return annotations; }
  public boolean isGlobal() { return true; }
  public XSSchema getXSSchema() { return null; }
  
  public void setGlobal(boolean pGlobal) {
    if (pGlobal == false) {
      throw new IllegalStateException("A builtin type cannot be local.");
    }
  }

  public Locator getLocator() {
    throw new IllegalStateException("The builtin type " + getName() + " does not have a location.");
  }

  public void validate() throws SAXException {}
public XsSchemaHeader getSchemaHeader() {
	return null;
  }
}
