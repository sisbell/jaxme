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


import org.apache.ws.jaxme.xs.XSParser;
import org.apache.ws.jaxme.xs.XSType;
import org.apache.ws.jaxme.xs.xml.XsQName;


/** <p>The type xs:anySimpleType.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XSAnySimpleType extends AbstractAtomicType {
  private static final XSAnySimpleType theInstance = new XSAnySimpleType();
  private static final XsQName name = new XsQName(XSParser.XML_SCHEMA_URI, "anySimpleType", null);

  protected XSAnySimpleType() {
  }

  public XsQName getName() { return name; }

  public static XSType getInstance() { return theInstance; }

  public boolean isRestriction() { return false; }
  public XSType getRestrictedType() {
    throw new IllegalStateException("The type " + name +
                                     " is the simple ur type and no restriction of another simple type.");
  }
}
