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
package org.apache.ws.jaxme.xs.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.ws.jaxme.xs.XSModelGroup;
import org.apache.ws.jaxme.xs.XSParticle;
import org.apache.ws.jaxme.xs.parser.impl.LocSAXException;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XSModelGroupImpl implements XSModelGroup {
  private final XSModelGroup.Compositor compositor;
  private final List particles = new ArrayList();
  private final Locator locator;

  public XSModelGroupImpl(XSModelGroup.Compositor pCompositor, Locator pLocator) {
    if (pCompositor == null) {
      throw new NullPointerException("The model group compositor must not be null.");
    }
    compositor = pCompositor;
    locator = pLocator;
  }

  public Compositor getCompositor() {
    return compositor;
  }

  public boolean isSequence() {
    return XSModelGroup.SEQUENCE.equals(compositor);
  }

  public boolean isChoice() {
    return XSModelGroup.CHOICE.equals(compositor);
  }

  public boolean isAll() {
    return XSModelGroup.ALL.equals(compositor);
  }

  public void addParticle(XSParticle pParticle) throws SAXException {
    if (isAll()) {
      if (pParticle.getMaxOccurs() == -1  ||  pParticle.getMaxOccurs() > 1) {
        throw new LocSAXException("Illegal 'maxOccurs' value inside 'all' group: " + pParticle.getMaxOccurs(),
                                     pParticle.getLocator());
      }
    }
    if (pParticle.getMaxOccurs() != -1  &&  pParticle.getMaxOccurs() < pParticle.getMinOccurs()) {
      throw new LocSAXException("Illegal 'maxOccurs' value, which is lower than 'minOccurs' value: " +
                                   pParticle.getMaxOccurs() + " < " + pParticle.getMinOccurs(),
                                   pParticle.getLocator());
    }
    particles.add(pParticle);
  }

  public XSParticle[] getParticles() {
    return (XSParticle[]) particles.toArray(new XSParticle[particles.size()]);
  }

  public void validate() throws SAXException {
    if (isChoice()  &&  particles.size() == 0) {
      throw new LocSAXException("A 'choice' model group must have at least one 'group', 'any', or 'element'.",
                                   getLocator());
    }
    for (Iterator iter = particles.iterator();  iter.hasNext();  ) {
      XSParticle particle = (XSParticle) iter.next();
      if (particle.isElement()) {
        particle.getElement().validate();
      } else if (particle.isGroup()) {
        particle.getGroup().validate();
      } else if (particle.isWildcard()) {
        particle.getWildcard().validate();
      } else {
        throw new IllegalStateException("Invalid particle");
      }
    }
  }

  public Locator getLocator() {
    return locator;
  }
}
