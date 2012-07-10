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
package org.apache.ws.jaxme.xs.xml.impl;

import org.apache.ws.jaxme.xs.xml.*;


/** <p>Implementation of the <code>xs:realGroup</code> type, as specified
 * by the following:
 * <pre>
 *  &lt;xs:complexType name="realGroup"&gt;
 *    &lt;xs:complexContent&gt;
 *      &lt;xs:restriction base="xs:group"&gt;
 *        &lt;xs:sequence&gt;
 *          &lt;xs:element ref="xs:annotation" minOccurs="0"/&gt;
 *          &lt;xs:choice minOccurs="0" maxOccurs="1"&gt;
 *            &lt;xs:element ref="xs:all"/&gt;
 *            &lt;xs:element ref="xs:choice"/&gt;
 *            &lt;xs:element ref="xs:sequence"/&gt;
 *          &lt;/xs:choice&gt;
 *        &lt;/xs:sequence&gt;
 *      &lt;/xs:restriction&gt;
 *    &lt;/xs:complexContent&gt;
 *  &lt;/xs:complexType&gt;
 * </pre></p>
 * <p><em>Implementation note:</em> This interface does not define
 * any additional methods. However, the <code>validate()</code>
 * method must ensure, that either of <code>xs:all</code>,
 * <code>xs:choice</code>, or <code>xs:sequence</code> is set.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XsTRealGroupImpl extends XsTGroupImpl implements XsTRealGroup {
  protected XsTRealGroupImpl(XsObject pParent) {
    super(pParent);
  }

  public XsTGroupRef createGroup() {
    throw new IllegalStateException("This group must not reference other groups.");
  }

  public XsEAny createAny() {
    throw new IllegalStateException("This group must not have an an 'any' element.");
  }

  public XsTLocalElement createElement() {
    throw new IllegalStateException("This group must not have an 'element' child element.");
  }

  public XsESequence createSequence() {
    return super.createSequence();
  }

  public XsEChoice createChoice() {
    return super.createChoice();
  }

  public XsTAll createAll() {
    return super.createAll();
  }

  public void validate() {
    if (getParticles().length > 1) {
      throw new IllegalStateException("This group must have exactly a single 'sequence', 'choice', or 'all' element.");
    }
  }
}
