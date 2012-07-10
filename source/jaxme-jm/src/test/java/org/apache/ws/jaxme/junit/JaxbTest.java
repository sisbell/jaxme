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
package org.apache.ws.jaxme.junit;

import org.apache.ws.jaxme.test.misc.jaxb.impl.SomeClass;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 * @version $Id: JaxbTest.java 231785 2004-02-16 23:39:59Z jochen $
 */
public class JaxbTest extends BaseTestCase {
  public JaxbTest(String name) {
    super(name);
  }

  /** Verify that the interface "SomeClass" with the implementation
   * "SomeClass" exists and has an attribute "SomeAttribute", and
   * a child element "SomeElement".
   */
  public void testJaxbClass() {
    org.apache.ws.jaxme.test.misc.jaxb.SomeClass sc = new SomeClass();
    sc.setSomeAttribute("2");
    sc.setSomeElement(2);
  }
}
