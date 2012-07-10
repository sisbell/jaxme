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
package org.apache.ws.jaxme.xs.jaxb.impl;

import org.apache.ws.jaxme.xs.XSObjectFactory;
import org.apache.ws.jaxme.xs.XSParser;
import org.apache.ws.jaxme.xs.jaxb.JAXBXsObjectFactory;
import org.apache.ws.jaxme.xs.parser.XSContext;


/** <p>Subclass of XSParser for parsing a JAXB schema.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class JAXBParser extends XSParser {
  public static final XSObjectFactory JAXB_OBJECT_FACTORY = new JAXBObjectFactoryImpl();
  public static final JAXBXsObjectFactory JAXB_XS_OBJECT_FACTORY = new JAXBXsObjectFactoryImpl();

  /** <p>The JAXB Schema URI: <code>http://java.sun.com/xml/ns/jaxb</code></p>
   */
  public static final String JAXB_SCHEMA_URI = "http://java.sun.com/xml/ns/jaxb";

  /** <p>The XJC Schema URI: <code>http://java.sun.com/xml/ns/jaxb/xjc</code></p>
   */
  public static final String XJC_SCHEMA_URI = "http://java.sun.com/xml/ns/jaxb/xjc";

  public JAXBParser() {
    XSContext data = getContext();
    data.setXsObjectFactory(JAXB_XS_OBJECT_FACTORY);
    data.setXSObjectFactory(JAXB_OBJECT_FACTORY);
  }
}
