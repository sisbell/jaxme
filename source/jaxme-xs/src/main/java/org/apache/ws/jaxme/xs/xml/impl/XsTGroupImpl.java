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
import org.xml.sax.SAXException;


/** <p>Implementation of the <code>xs:group</code>, type, with the
 * following specification:
 * <pre>
 *  &lt;xs:complexType name="group" abstract="true"&gt;
 *    &lt;xs:annotation&gt;
 *      &lt;xs:documentation&gt;
 *        group type for explicit groups, named top-level groups and
 *        group references
 *      &lt;/xs:documentation&gt;
 *    &lt;/xs:annotation&gt;
 *    &lt;xs:complexContent&gt;
 *      &lt;xs:extension base="xs:annotated"&gt;
 *        &lt;xs:group ref="xs:particle" minOccurs="0" maxOccurs="unbounded"/&gt;
 *        &lt;xs:attributeGroup ref="xs:defRef"/&gt;
 *        &lt;xs:attributeGroup ref="xs:occurs"/&gt;
 *      &lt;/xs:extension&gt;
 *    &lt;/xs:complexContent&gt;
 *  &lt;/xs:complexType&gt;
 * </pre>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XsTGroupImpl extends XsTAnnotatedImpl implements XsTGroup {
  private final XsAGDefRef defRef;
  private final XsAGOccurs occurs;
  private final XsGParticle particle;

  protected XsTGroupImpl(XsObject pParent) {
    super(pParent);
    defRef = getObjectFactory().newXsAGDefRef(this);
    occurs = getObjectFactory().newXsAGOccurs(this);
    particle = getObjectFactory().newXsGParticle(this);
  }

  public void setName(XsNCName pName) {
    defRef.setName(pName);
  }

  public XsNCName getName() {
    return defRef.getName();
  }

  public void setRef(XsQName pRef) {
    defRef.setRef(pRef);
  }

  public void setRef(String pRef) throws SAXException {
    setRef(asXsQName(pRef));
  }

  public XsQName getRef() {
    return defRef.getRef();
  }

  public void setMaxOccurs(String pMaxOccurs) {
    occurs.setMaxOccurs(pMaxOccurs);
  }

  public int getMaxOccurs() {
    return occurs.getMaxOccurs();
  }

  public void setMinOccurs(int pMinOccurs) {
    occurs.setMinOccurs(pMinOccurs);
  }

  public int getMinOccurs() {
    return occurs.getMinOccurs();
  }

  public XsTLocalElement createElement() {
    return particle.createElement();
  }

  public XsTGroupRef createGroup() {
    return particle.createGroup();
  }

  public XsTAll createAll() {
    return particle.createAll();
  }

  public XsESequence createSequence() {
    return particle.createSequence();
  }

  public XsEChoice createChoice() {
    return particle.createChoice();
  }

  public XsEAny createAny() {
    return particle.createAny();
  }

  public XsTParticle[] getParticles() {
    return particle.getParticles();
  }
}
