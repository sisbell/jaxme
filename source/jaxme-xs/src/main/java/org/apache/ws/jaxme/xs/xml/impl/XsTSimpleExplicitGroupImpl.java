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

import org.apache.ws.jaxme.xs.xml.XsEAny;
import org.apache.ws.jaxme.xs.xml.XsEChoice;
import org.apache.ws.jaxme.xs.xml.XsESequence;
import org.apache.ws.jaxme.xs.xml.XsObject;
import org.apache.ws.jaxme.xs.xml.XsTGroupRef;
import org.apache.ws.jaxme.xs.xml.XsTLocalElement;
import org.apache.ws.jaxme.xs.xml.XsTNestedParticle;
import org.apache.ws.jaxme.xs.xml.XsTSimpleExplicitGroup;

/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XsTSimpleExplicitGroupImpl extends XsTAnnotatedImpl implements XsTSimpleExplicitGroup {
  private final List particles = new ArrayList();

  protected XsTSimpleExplicitGroupImpl(XsObject pParent) {
    super(pParent);
  }

  protected void addParticle(XsTNestedParticle pParticle) {
    particles.add(pParticle);
  }

  public XsTLocalElement createElement() {
    XsTLocalElement element = getObjectFactory().newXsTLocalElement(this);
    addParticle(element);
    return element;
  }

  public XsTGroupRef createGroup() {
    XsTGroupRef groupRef = getObjectFactory().newXsTGroupRef(this);
    addParticle(groupRef);
    return groupRef;
  }

  public XsEChoice createChoice() {
    XsEChoice choice = getObjectFactory().newXsEChoice(this);
    addParticle(choice);
    return choice;
  }

  public XsESequence createSequence() {
    XsESequence sequence = getObjectFactory().newXsESequence(this);
    addParticle(sequence);
    return sequence;
  }

  public XsEAny createAny() {
    XsEAny any = getObjectFactory().newXsEAny(this);
    addParticle(any);
    return any;
  }

  public XsTNestedParticle[] getParticles() {
    return (XsTNestedParticle[]) particles.toArray(new XsTNestedParticle[particles.size()]);
  }
}
