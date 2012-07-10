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
package org.apache.ws.jaxme.xs.xml;


/** <p>Implementation of the <code>xs:particle</code> group,
 * with the following specification:
 * <pre>
 *  &lt;xs:group name="particle"&gt;
 *    &lt;xs:choice&gt;
 *      &lt;xs:element name="element" type="xs:localElement"/&gt;
 *      &lt;xs:element name="group" type="xs:groupRef"/&gt;
 *      &lt;xs:element ref="xs:all"/&gt;
 *      &lt;xs:element ref="xs:choice"/&gt;
 *      &lt;xs:element ref="xs:sequence"/&gt;
 *      &lt;xs:element ref="xs:any"/&gt;
 *    &lt;/xs:choice&gt;
 *  &lt;/xs:group&gt;
 * </pre></p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface XsGParticle {
  public XsTLocalElement createElement();

  public XsTGroupRef createGroup();

  public XsTAll createAll();

  public XsESequence createSequence();

  public XsEChoice createChoice();

  public XsEAny createAny();

  public XsTParticle[] getParticles();
}
