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
package org.apache.ws.jaxme.generator.types;

import org.apache.ws.jaxme.generator.sg.impl.JaxMeSchemaReader;
import org.apache.ws.jaxme.xs.XSType;
import org.apache.ws.jaxme.xs.types.XSString;
import org.apache.ws.jaxme.xs.xml.XsQName;

/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class JMChar extends XSString {
  private static final JMChar theInstance = new JMChar();
  private static final XsQName name = new XsQName(JaxMeSchemaReader.JAXME_SCHEMA_URI, "char");
  private static final Long theLength = new Long(1);

  protected JMChar() {
  }

  public XsQName getName() { return name; }

  public static XSType getInstance() { return theInstance; }

  public XSType getRestrictedType() { return XSString.getInstance(); }

  public Long getLength() { return theLength; }
  public Long getMaxLength() { return theLength; }
  public Long getMinLength() { return theLength; }
}
