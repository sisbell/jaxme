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

import org.apache.ws.jaxme.xs.XSAnnotation;
import org.apache.ws.jaxme.xs.XSAny;
import org.apache.ws.jaxme.xs.XSElement;
import org.apache.ws.jaxme.xs.XSGroup;
import org.apache.ws.jaxme.xs.XSModelGroup;
import org.apache.ws.jaxme.xs.XSObject;
import org.apache.ws.jaxme.xs.XSParticle;
import org.apache.ws.jaxme.xs.parser.impl.LocSAXException;
import org.apache.ws.jaxme.xs.xml.XsAGOccurs;
import org.apache.ws.jaxme.xs.xml.XsEAnnotation;
import org.apache.ws.jaxme.xs.xml.XsEAny;
import org.apache.ws.jaxme.xs.xml.XsEChoice;
import org.apache.ws.jaxme.xs.xml.XsESchema;
import org.apache.ws.jaxme.xs.xml.XsESequence;
import org.apache.ws.jaxme.xs.xml.XsNCName;
import org.apache.ws.jaxme.xs.xml.XsObject;
import org.apache.ws.jaxme.xs.xml.XsQName;
import org.apache.ws.jaxme.xs.xml.XsTAll;
import org.apache.ws.jaxme.xs.xml.XsTGroupRef;
import org.apache.ws.jaxme.xs.xml.XsTLocalElement;
import org.apache.ws.jaxme.xs.xml.XsTNamedGroup;
import org.apache.ws.jaxme.xs.xml.XsTNestedParticle;
import org.apache.ws.jaxme.xs.xml.XsTParticle;
import org.apache.ws.jaxme.xs.xml.impl.XsTGroupRefImpl;
import org.xml.sax.SAXException;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XSGroupImpl extends XSOpenAttrsImpl implements XSGroup {
  private final XsQName name;
  private final XsTNestedParticle[] nestedParticles;
  private final XsTParticle[] particles;
  private final XsEAnnotation xsAnnotation;
  private XSAnnotation[] annotations;
  private boolean isGlobal;
  private XSModelGroup modelGroup;
  private final XSModelGroupImpl modelGroupImpl;
  private boolean isValidated;

  protected XSGroupImpl(XSObject pParent, XsTGroupRef pBaseGroup)
      throws SAXException {
    super(pParent, pBaseGroup);
    XsQName qName = pBaseGroup.getRef();
    if (qName == null) {
      qName = getQName(pBaseGroup.getName());
    } else {
      setGlobal(true);
    }
    name = qName;
    modelGroupImpl = null;
    nestedParticles = null;
    particles = null;
    xsAnnotation = pBaseGroup.getAnnotation();
  }

  protected XSGroupImpl(XSObject pParent, XsTNamedGroup pBaseGroup) throws SAXException {
    super(pParent, pBaseGroup);
    name = getQName(pBaseGroup.getName());
    XSModelGroupImpl myModelGroup;
    XsTNamedGroup group = (XsTNamedGroup) getXsObject();
    if (group.getSequence() != null) {
      myModelGroup = modelGroupImpl = new XSModelGroupImpl(XSModelGroup.SEQUENCE, pBaseGroup.getLocator());
      nestedParticles = group.getSequence().getParticles();
      particles = null;
    } else if (group.getChoice() != null) {
      myModelGroup = modelGroupImpl = new XSModelGroupImpl(XSModelGroup.CHOICE, pBaseGroup.getLocator());
      nestedParticles = group.getChoice().getParticles();
      particles = null;
    } else if (group.getAll() != null) {
      myModelGroup = modelGroupImpl = new XSModelGroupImpl(XSModelGroup.ALL, pBaseGroup.getLocator());
      nestedParticles = group.getAll().getElements();
      particles = null;
    } else {
      throw new IllegalStateException("Invalid group: Neither of 'sequence', 'choice'. or 'all' elements is set.");
    }
    this.modelGroup = myModelGroup;
    xsAnnotation = pBaseGroup.getAnnotation();
  }

  protected XSGroupImpl(XSObject pParent, XsEChoice pChoice) throws SAXException {
    super(pParent, pChoice);
    name = null;
    modelGroup = modelGroupImpl = new XSModelGroupImpl(XSModelGroup.CHOICE, pChoice.getLocator());
    particles = pChoice.getParticles();
    nestedParticles = null;
    xsAnnotation = pChoice.getAnnotation();
  }

  protected XSGroupImpl(XSObject pParent, XsESequence pSequence) throws SAXException {
    super(pParent, pSequence);
    name = null;
    modelGroup = modelGroupImpl = new XSModelGroupImpl(XSModelGroup.SEQUENCE, pSequence.getLocator());
    particles = pSequence.getParticles();
    nestedParticles = null;
    xsAnnotation = pSequence.getAnnotation();
  }

  protected XSGroupImpl(XSObject pParent, XsTAll pAll) throws SAXException {
    super(pParent, pAll);
    name = null;
    modelGroup = modelGroupImpl = new XSModelGroupImpl(XSModelGroup.ALL, pAll.getLocator());
    particles = pAll.getParticles();
    nestedParticles = null;
    xsAnnotation = pAll.getAnnotation();
  }

  protected XsQName getQName(XsNCName pName) throws SAXException {
    if (pName == null) {
      throw new LocSAXException("Invalid group: Either of its 'ref' or 'name' attributes must be set.",
                                   getLocator());
    }
    XsESchema syntaxSchema = getXsObject().getXsESchema();
    return new XsQName(syntaxSchema.getTargetNamespace(), pName.toString(), syntaxSchema.getTargetNamespacePrefix());
  }

  protected void initParticles(XSModelGroupImpl pModelGroup, XsTNestedParticle[] pParticles) throws SAXException {
    for (int i = 0;  i < pParticles.length;  i++) {
      addParticle(pModelGroup, pParticles[i]);
    }
  }

  protected void initParticles(XSModelGroupImpl pModelGroup, XsTParticle[] pParticles) throws SAXException {
    for (int i = 0;  i < pParticles.length;  i++) {
      addParticle(pModelGroup, pParticles[i]);
    }
  }

  protected void addParticle(XSModelGroupImpl pModelGroup, XsTParticle pParticle) throws SAXException {
    XsAGOccurs occurs;
    XSParticleImpl p;
    if (pParticle instanceof XsTAll) {
      XsTAll all = (XsTAll) pParticle;
      occurs = all;
      XSGroup group = getXSSchema().getXSObjectFactory().newXSGroup(this, all);
      group.validate();
      p = new XSParticleImpl(group);
    } else if (pParticle instanceof XsTNestedParticle) {
      addParticle(pModelGroup, (XsTNestedParticle) pParticle);
      return;
    } else {
      throw new IllegalStateException("Unknown particle type: " + pParticle.getClass().getName());
    }
    pModelGroup.addParticle(p);
    p.setMaxOccurs(occurs.getMaxOccurs());
    p.setMinOccurs(occurs.getMinOccurs());
  }

  protected void addParticle(XSModelGroupImpl pModelGroup, XsTNestedParticle pParticle) throws SAXException {
    XsAGOccurs occurs;
    XSParticleImpl p;
    if (pParticle instanceof XsEAny) {
      XsEAny any = (XsEAny) pParticle;
      occurs = any;
      XSAny wildcard = getXSSchema().getXSObjectFactory().newXSAny(this, any);
      wildcard.validate();
      p = new XSParticleImpl(wildcard);
    } else if (pParticle instanceof XsEChoice) {
      XsEChoice choice = (XsEChoice) pParticle;
      occurs = choice;
      XSGroup group = getXSSchema().getXSObjectFactory().newXSGroup(this, choice);
      group.validate();
      p = new XSParticleImpl(group);
    } else if (pParticle instanceof XsESequence) {
      XsESequence sequence = (XsESequence) pParticle;
      occurs = sequence;
      XSGroup group = getXSSchema().getXSObjectFactory().newXSGroup(this, sequence);
      group.validate();
      p = new XSParticleImpl(group);
    } else {
      XsTGroupRef groupRef = null;
      XsTLocalElement localElement = null;
      if (pParticle instanceof XsTGroupRef) {
        groupRef = (XsTGroupRef) pParticle;
      } else if (pParticle instanceof XsTLocalElement) {
        localElement = (XsTLocalElement) pParticle;
        // May be the referenced element is the head of a substitution group
        XsQName ref = localElement.getRef();
        if (ref != null) {
          XSElement referencedElement = getXSSchema().getElement(ref);
          if (referencedElement == null) {
            throw new LocSAXException("The referenced element " + ref + " is undefined.", localElement.getLocator());
          }
          XSGroup substitutedGroup = referencedElement.getSubstitutionGroup();
          if (substitutedGroup != null  &&  substitutedGroup != this) {
            XsObject parent = localElement.getParentObject();
            XsTGroupRefImpl groupRefImpl = (XsTGroupRefImpl) localElement.getObjectFactory().newXsTGroupRef(parent);
            groupRefImpl.setRef(substitutedGroup.getName());
            int maxOccurs = localElement.getMaxOccurs();
            if (maxOccurs == -1) {
              groupRefImpl.setMaxOccurs("unbounded");
            } else {
              groupRefImpl.setMaxOccurs(Integer.toString(maxOccurs));
            }
            groupRefImpl.setMinOccurs(localElement.getMinOccurs());
            groupRef = groupRefImpl;
          }
        }
      } else {
        throw new IllegalStateException("Unknown particle type: " + pParticle.getClass().getName());
      }
      if (groupRef == null) {
        occurs = localElement;
        XSElement element = getXSSchema().getXSObjectFactory().newXSElement(this, localElement);
        element.validate();
        p = new XSParticleImpl(element);
      } else {
        occurs = groupRef;
        XSGroup group = getXSSchema().getXSObjectFactory().newXSGroup(this, groupRef);
        group.validate();
        p = new XSParticleImpl(group);
      }
    }
    pModelGroup.addParticle(p);
    p.setMaxOccurs(occurs.getMaxOccurs());
    p.setMinOccurs(occurs.getMinOccurs());
  }

  public boolean isGlobal() {
    return isGlobal;
  }

  public void setGlobal(boolean pGlobal) {
    isGlobal = pGlobal;
  }

  public XsQName getName() {
    return name;
  }

  protected boolean isValidated() {
    return isValidated;
  }

  public void validate() throws SAXException {
    if (isValidated()) {
      return;
    } else {
      isValidated = true;
    }

    if (xsAnnotation == null) {
      annotations = new XSAnnotation[0];
    } else {
      XSAnnotation result = getXSSchema().getXSObjectFactory().newXSAnnotation(this, xsAnnotation);
      result.validate();
      annotations = new XSAnnotation[]{result};
    }

    if (modelGroup == null) {
      XsQName myName = getName();
      if (myName == null) {
        throw new NullPointerException("Missing group name");
      }
      XSGroup group = getXSSchema().getGroup(myName);
      if (group == null) {
        throw new LocSAXException("Unknown group: " + myName, getLocator());
      }
      modelGroup = group;
    }

    if (particles != null) {
      initParticles(modelGroupImpl, particles);
    } else if (nestedParticles != null) {
      initParticles(modelGroupImpl, nestedParticles);
    }

    modelGroup.validate();
  }

  public Compositor getCompositor() {
    return modelGroup.getCompositor();
  }

  public XSParticle[] getParticles() {
    return modelGroup.getParticles();
  }

  public boolean isSequence() {
    return modelGroup.isSequence();
  }

  public boolean isChoice() {
    return modelGroup.isChoice();
  }

  public boolean isAll() {
    return modelGroup.isAll();
  }

  public XSAnnotation[] getAnnotations() {
    return annotations;
  }
}
