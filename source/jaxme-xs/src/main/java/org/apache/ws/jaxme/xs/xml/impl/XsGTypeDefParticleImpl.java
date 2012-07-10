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
public class XsGTypeDefParticleImpl implements XsGTypeDefParticle {
  private final XsObject owner;
  private XsTTypeDefParticle particle;

  protected XsGTypeDefParticleImpl(XsObject pOwner) {
    owner = pOwner;
  }

  protected void setTypeDefParticle(XsTTypeDefParticle pParticle) {
    if (particle != null) {
      throw new IllegalStateException("Only a single child element of 'all', 'group', 'choice', or 'sequence' is allowed.");
    }
    particle = pParticle;
  }


  public XsTTypeDefParticle getTypeDefParticle() {
    return particle;
  }

  public XsTGroupRef createGroup() {
    XsTGroupRef group = owner.getObjectFactory().newXsTGroupRef(owner);
    setTypeDefParticle(group);
    return group;
  }

  public XsTAll createAll() {
    XsTAll all = owner.getObjectFactory().newXsTAll(owner);
    setTypeDefParticle(all);
    return all;
  }

  public XsESequence createSequence() {
    XsESequence sequence = owner.getObjectFactory().newXsESequence(owner);
    setTypeDefParticle(sequence);
    return sequence;
  }

  public XsEChoice createChoice() {
    XsEChoice choice = owner.getObjectFactory().newXsEChoice(owner);
    setTypeDefParticle(choice);
    return choice;
  }
}
