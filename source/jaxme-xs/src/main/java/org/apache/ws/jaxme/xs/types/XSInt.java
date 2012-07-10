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


/** <p>The xs:int type</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XSInt extends XSLong {
  private static final XSInt theInstance = new XSInt();
  private static final XsQName name = new XsQName(XSParser.XML_SCHEMA_URI, "int", null);

  protected XSInt() {
  }

  public XsQName getName() { return name; }

  public String getMaxInclusive() { return "2147483647"; }

  public String getMinInclusive() { return "-2147483648"; }

  public static XSType getInstance() { return theInstance; }

  public XSType getRestrictedType() { return XSLong.getInstance(); }
}
