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

import org.apache.ws.jaxme.xs.XSParser;
import org.apache.ws.jaxme.xs.XSType;
import org.apache.ws.jaxme.xs.xml.XsQName;


/** <p>The type xs:boolean.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XSBoolean extends AbstractAtomicType {
  private static final XSBoolean theInstance = new XSBoolean();
  private static final XsQName name = new XsQName(XSParser.XML_SCHEMA_URI, "boolean", null);

  protected XSBoolean() {
  }

  public XsQName getName() { return name; }

  public static XSBoolean getInstance() { return theInstance; }

  public boolean isRestriction() { return true; }
  public XSType getRestrictedType() { return XSAnySimpleType.getInstance(); }
}
