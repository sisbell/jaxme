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

import org.apache.ws.jaxme.xs.XSAny;
import org.apache.ws.jaxme.xs.XSElement;
import org.apache.ws.jaxme.xs.XSGroup;
import org.apache.ws.jaxme.xs.XSParticle;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;


/** <p>Default implementation of a particle.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XSParticleImpl implements XSParticle {
  private final XSGroup group;
  private final XSAny wildcard;
  private final XSElement element;
  private int minOccurs = 1;
  private int maxOccurs = 1;

  public XSParticleImpl(XSGroup pGroup) throws SAXException {
    if (pGroup == null) {
      throw new NullPointerException("The particle group must not be null.");
    }
    group = pGroup;
    element = null;
    wildcard = null;
  }

  public XSParticleImpl(XSAny pWildcard) {
    if (pWildcard == null) {
      throw new NullPointerException("The particle wildcard must not be null.");
    }
    wildcard = pWildcard;
    group = null;
    element = null;
  }

  public XSParticleImpl(XSElement pElement) {
    if (pElement == null) {
      throw new NullPointerException("The particle element must not be null.");
    }
    element = pElement;
    group = null;
    wildcard = null;
  }

  public XSParticle.Type getType() {
    if (group != null) {
      return XSParticle.GROUP;
    } else if (wildcard != null) {
      return XSParticle.WILDCARD;
    } else if (element != null) {
      return XSParticle.ELEMENT;
    } else {
      throw new IllegalStateException("Neither of the particle group, wildcard, or element has been set.");
    }
  }

  public boolean isGroup() {
    return group != null;
  }

  public XSGroup getGroup() {
    if (group == null) {
      throw new IllegalStateException("This particle doesn't have the group type.");
    }
    return group;
  }

  public boolean isWildcard() {
    return wildcard != null;
  }

  public XSAny getWildcard() {
    if (wildcard == null) {
      throw new IllegalStateException("This particle doesn't have the wildcard type.");
    }
    return wildcard;
  }

  public boolean isElement() {
    return element != null;
  }

  public XSElement getElement() {
    if (element == null) {
      throw new IllegalStateException("This particle doesn't have the element type.");
    }
    return element;
  }

  public int getMinOccurs() {
    return minOccurs;
  }

  public void setMinOccurs(int pMinOccurs) {
    minOccurs = pMinOccurs;
  }

  public int getMaxOccurs() {
    return maxOccurs;
  }

  public void setMaxOccurs(int pMaxOccurs) {
    maxOccurs = pMaxOccurs;
  }

  public Locator getLocator() {
    if (isWildcard()) {
      return getWildcard().getLocator();
    } else if (isElement()) {
      return getElement().getLocator();
    } else if (isGroup()) {
      return getGroup().getLocator();
    } else {
      throw new IllegalStateException("Invalid particle, neither of element, wildcard, or model group.");
    }
  }
}
