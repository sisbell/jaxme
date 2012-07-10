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

import java.util.ArrayList;
import java.util.List;

import org.apache.ws.jaxme.xs.xml.*;


/** <p>Implementation of the <code>xs:typeDefParticle</code> group,
 * as specified by the following:
 * <pre>
 *  <xs:group name="typeDefParticle">
 *    <xs:annotation>
 *      <xs:documentation>
 *        'complexType' uses this
 *      </xs:documentation>
 *    </xs:annotation>
 *    <xs:choice>
 *      <xs:element name="group" type="xs:groupRef"/>
 *      <xs:element ref="xs:all"/>
 *      <xs:element ref="xs:choice"/>
 *      <xs:element ref="xs:sequence"/>
 *    </xs:choice>
 *  </xs:group>
 * </pre></p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XsGParticleImpl implements XsGParticle {
  private final XsObject owner;
  private List particles;

  protected XsGParticleImpl(XsObject pOwner) {
    owner = pOwner;
  }

  protected void addParticle(XsTParticle pParticle) {
    if (particles == null) {
      particles = new ArrayList();
    }
    particles.add(pParticle);
  }

  public XsTLocalElement createElement() {
    XsTLocalElement element = owner.getObjectFactory().newXsTLocalElement(owner);
    addParticle(element);
    return element;
  }

  public XsTGroupRef createGroup() {
    XsTGroupRef groupRef = owner.getObjectFactory().newXsTGroupRef(owner);
    addParticle(groupRef);
    return groupRef;
  }

  public XsTAll createAll() {
    XsTAll all = owner.getObjectFactory().newXsTAll(owner);
    addParticle(all);
    return all;
  }

  public XsESequence createSequence() {
    XsESequence sequence = owner.getObjectFactory().newXsESequence(owner);
    addParticle(sequence);
    return sequence;
  }

  public XsEChoice createChoice() {
    XsEChoice choice = owner.getObjectFactory().newXsEChoice(owner);
    addParticle(choice);
    return choice;
  }

  public XsEAny createAny() {
    XsEAny any = owner.getObjectFactory().newXsEAny(owner);
    addParticle(any);
    return any;
  }

  public XsTParticle[] getParticles() {
    if (particles == null) {
      return new XsTParticle[0];
    } else {
      return (XsTParticle[]) particles.toArray(new XsTParticle[particles.size()]);
    }
  }
}
