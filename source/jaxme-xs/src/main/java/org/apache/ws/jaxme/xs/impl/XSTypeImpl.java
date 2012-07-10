/*
 * Copyright 2003,2004  The Apache Software Foundation
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
import org.apache.ws.jaxme.xs.XSAttributable;
import org.apache.ws.jaxme.xs.XSComplexType;
import org.apache.ws.jaxme.xs.XSGroup;
import org.apache.ws.jaxme.xs.XSModelGroup;
import org.apache.ws.jaxme.xs.XSObject;
import org.apache.ws.jaxme.xs.XSObjectFactory;
import org.apache.ws.jaxme.xs.XSParticle;
import org.apache.ws.jaxme.xs.XSSchema;
import org.apache.ws.jaxme.xs.XSSimpleContentType;
import org.apache.ws.jaxme.xs.XSSimpleType;
import org.apache.ws.jaxme.xs.XSType;
import org.apache.ws.jaxme.xs.parser.impl.LocSAXException;
import org.apache.ws.jaxme.xs.types.XSAnyType;
import org.apache.ws.jaxme.xs.xml.*;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 * @author <a href="mailto:iasandcb@tmax.co.kr">Ias</a>
 */
public class XSTypeImpl extends XSOpenAttrsImpl implements XSType {
  public abstract class XSComplexTypeImpl implements XSComplexType {
    protected final XSType owner;
    private final XsTComplexType myComplexType;
    protected XSType extendedType, restrictedType;

    public XSComplexTypeImpl(XSType pOwner, XsTComplexType pType) {
      owner = pOwner;
      myComplexType = pType;
    }

    protected XsTComplexType getXsTComplexType() { return myComplexType; }
    protected XSType getOwner() { return owner; }

    public boolean isSequence() { return false; }
    public boolean isChoice() { return false; }
    public boolean isAll() { return false; }
    public boolean hasSimpleContent() { return false; }
    public XSSimpleContentType getSimpleContent() {
      throw new IllegalStateException("This complex type doesn't have simple content.");
    }
    public boolean hasComplexContent() { return false; }
    public boolean isEmpty() { return XsComplexContentType.EMPTY.equals(getComplexContentType()); }
    public boolean isElementOnly() { return XsComplexContentType.ELEMENT_ONLY.equals(getComplexContentType()); }
    public boolean isMixed() { return XsComplexContentType.MIXED.equals(getComplexContentType()); }

    public void validate() throws SAXException {
    }


    public boolean isExtension() { return extendedType != null; }
    public XSType getExtendedType() {
      if (extendedType == null) {
        throw new IllegalStateException("This type is no extension.");
      }
      return extendedType;
    }

    public boolean isRestriction() { return restrictedType != null; }
    public XSType getRestrictedType() {
      if (restrictedType == null) {
        throw new IllegalStateException("This type is no restriction.");
      }
      return restrictedType;
    }
  }

  public class XSSimpleContentImpl extends XSComplexTypeImpl {
    private final XsESimpleContent simpleContent;
    private final XSSimpleContentType simpleContentType;
    private final XSAttributable[] attributes;

    public boolean isElementOnly() { return false; }
	public boolean isEmpty() { return false; }
	public boolean isMixed() { return false; }

	public XSSimpleContentImpl(XSType pOwner, XsTComplexType pType, XsESimpleContent pSimpleContent)
        throws SAXException {
      super(pOwner, pType);
      simpleContent = pSimpleContent;
      XsTSimpleExtensionType extension = simpleContent.getExtension();
      if (extension == null) {
        XsTSimpleRestrictionType restriction = simpleContent.getRestriction();
        if (restriction == null) {
          throw new LocSAXException("Invalid 'simpleContent', neither of the 'extension' or 'restriction' child elements are present.",
                                       simpleContent.getLocator());
        }
        XsQName restrictedTypesName = restriction.getBase();
        if (restrictedTypesName == null) {
          throw new LocSAXException("Invalid 'restriction': Missing 'base' attribute.", restriction.getLocator());
        }
        restrictedType = getXSSchema().getType(restrictedTypesName);
        if (restrictedType == null) {
          throw new LocSAXException("Invalid 'restriction': The base type " + restrictedTypesName + " is unknown.",
                                     restriction.getLocator());
        }
        restrictedType.validate();
        extendedType = null;
        XSObjectFactory factory = pOwner.getXSSchema().getXSObjectFactory();
        XSType contentType = factory.newXSType(pOwner, restriction);
        simpleContentType = factory.newXSSimpleContentType(pOwner, contentType, restriction);
        attributes = XSAttributeGroupImpl.getAttributes(XSTypeImpl.this, restriction);
      } else {
        XsQName extendedTypesName = extension.getBase();
        if (extendedTypesName == null) {
          throw new LocSAXException("Invalid 'extension': Missing 'base' attribute.",
                                       extension.getLocator());
        }
        extendedType = getXSSchema().getType(extendedTypesName);
        if (extendedType == null) {
          throw new LocSAXException("Invalid 'extension': Unknown 'base' type " + extendedTypesName,
                                       extension.getLocator());
        }
        extendedType.validate();
        restrictedType = null;
        XSAttributable[] inheritedAttributes;
        if (extendedType.isSimple()) {
          simpleContentType = getOwner().getXSSchema().getXSObjectFactory().newXSSimpleContentType(pOwner,
                                                                                     extendedType, extension);
          inheritedAttributes = new XSAttributable[0];
        } else {
          XSComplexType myComplexType = extendedType.getComplexType();
          if (!myComplexType.hasSimpleContent()) {
            throw new LocSAXException("Invalid 'extension': The base type " + extendedTypesName +
                                         " is neither a simple type nor a complex type with simple content.",
                                         extension.getLocator());
          }
          simpleContentType = myComplexType.getSimpleContent();
          inheritedAttributes = myComplexType.getAttributes();
        }
        XSAttributable[] myAttributes = XSAttributeGroupImpl.getAttributes(XSTypeImpl.this, extension);
        attributes = new XSAttributable[inheritedAttributes.length + myAttributes.length];
        System.arraycopy(inheritedAttributes, 0, attributes, 0, inheritedAttributes.length);
        System.arraycopy(myAttributes, 0, attributes, inheritedAttributes.length, myAttributes.length);
      }
    }

    public boolean hasSimpleContent() { return true; }
    public XSSimpleContentType getSimpleContent() { return simpleContentType; }
    public XSAttributable[] getAttributes() { return attributes; }
    public XSParticle getParticle() {
      throw new IllegalStateException("This complex type doesn't have a model group particle.");
    }
    public XsComplexContentType getComplexContentType() {
        throw new IllegalStateException("This complex type (" + this.getClass().getName() +
                                         ") doesn't have complex content.");
    }
  }

  public class XSComplexContentImpl extends XSComplexTypeImpl {
    private final XsEComplexContent complexContent;
    private final XSParticle complexContentParticle;
    private final XsComplexContentType complexContentType;
    private final XSAttributable[] attributes;

    protected XsEComplexContent getComplexContent() {
      return complexContent;
    }

    private int getMinOccursByParticle(XsTTypeDefParticle pParticle) throws SAXException {
        if (pParticle instanceof XsEChoice) {
            return ((XsEChoice) pParticle).getMinOccurs();
        } else if (pParticle instanceof XsESequence) {
            return ((XsESequence) pParticle).getMinOccurs();
        } else if (pParticle instanceof XsTAll) {
            return ((XsTAll) pParticle).getMinOccurs();
        } else if (pParticle instanceof XsTGroupRef) {
            return ((XsTGroupRef) pParticle).getMinOccurs();
        } else {
            throw new IllegalStateException("Unknown TypeDefParticle type: " + pParticle.getClass().getName());
        }
    }

    private int getMaxOccursByParticle(XsTTypeDefParticle pParticle) throws SAXException {
        if (pParticle instanceof XsEChoice) {
            return ((XsEChoice) pParticle).getMaxOccurs();
        } else if (pParticle instanceof XsESequence) {
            return ((XsESequence) pParticle).getMaxOccurs();
        } else if (pParticle instanceof XsTAll) {
            return ((XsTAll) pParticle).getMaxOccurs();
        } else if (pParticle instanceof XsTGroupRef) {
            return ((XsTGroupRef) pParticle).getMaxOccurs();
        } else {
            throw new IllegalStateException("Unknown TypeDefParticle type: " + pParticle.getClass().getName());
        }
    }

    protected XSGroup getGroupByParticle(XsTTypeDefParticle pParticle) throws SAXException {
      XSGroup result;
      XSType myOwner = getOwner();
      XSObjectFactory factory = myOwner.getXSSchema().getXSObjectFactory();
      if (pParticle == null) {
        return null;
      } else if (pParticle instanceof XsEChoice) {
        XsEChoice choice = (XsEChoice) pParticle;
        result = factory.newXSGroup(myOwner, choice);
      } else if (pParticle instanceof XsESequence) {
        XsESequence sequence = (XsESequence) pParticle;
        result = factory.newXSGroup(myOwner, sequence);
      } else if (pParticle instanceof XsTAll) {
        XsTAll all = (XsTAll) pParticle;
        result = factory.newXSGroup(myOwner, all);
      } else if (pParticle instanceof XsTGroupRef) {
        XsTGroupRef groupRef = (XsTGroupRef) pParticle;
        result = factory.newXSGroup(myOwner, groupRef);
      } else {
        throw new IllegalStateException("Unknown TypeDefParticle type: " + pParticle.getClass().getName());
      }
      result.validate();
      return result;
    }

    protected XsComplexContentType getContentTypeByParticle(XsTTypeDefParticle pParticle, XSGroup pGroup) 
        throws SAXException {
      if (pParticle == null) {
        return XsComplexContentType.EMPTY;
      } else if (pParticle instanceof XsEChoice) {
        if (pGroup.getParticles().length == 0) {
          XsEChoice choice = (XsEChoice) pParticle;
          if (choice.getMinOccurs() == 0) {
            return XsComplexContentType.EMPTY;
          } else {
            throw new LocSAXException("Invalid choice: Neither child elements, nor 'minOccurs'=0", choice.getLocator());
          }
        } else {
        }
      } else if (pParticle instanceof XsESequence) {
        if (pGroup.getParticles().length == 0) {
          return XsComplexContentType.EMPTY;
        }
      } else if (pParticle instanceof XsTAll) {
        if (pGroup.getParticles().length == 0) {
          return XsComplexContentType.EMPTY;
        }
      } else if (pParticle instanceof XsTGroupRef) {
      } else {
        throw new IllegalStateException("Unknown TypeDefParticle type: " + pParticle.getClass().getName());
      }
      boolean isMixed;
      if (complexContent.isMixed() == null) {
        isMixed = ((XsTComplexType) getXsObject()).isMixed();
      } else {
        isMixed = complexContent.isMixed().booleanValue();
      }
      return isMixed ? XsComplexContentType.MIXED : XsComplexContentType.ELEMENT_ONLY;
    }

    private class ExtensionGroup implements XSGroup {
      private final XSObject parent;
      private final XSParticle[] particles;
      private ExtensionGroup(XSObject pParent, XSParticle[] pParticles) {
        parent = pParent;
        particles = pParticles;
      }
      public boolean isTopLevelObject() { return false; }
      public boolean isGlobal() { return false; }
      public XsQName getName() { return null; }
      public XSSchema getXSSchema() { return XSTypeImpl.this.getXSSchema(); }
      public Locator getLocator() { return getComplexContent().getLocator(); }
      public XSObjectFactory getXSObjectFactory() { return getXSObjectFactory(); }
      public void validate() throws SAXException {}
      public XSModelGroup.Compositor getCompositor() { return XSModelGroup.SEQUENCE; }
      public boolean isSequence() { return true; }
      public boolean isChoice() { return false; }
      public boolean isAll() { return false; }
      public XSParticle[] getParticles() { return particles; }
      public XSAnnotation[] getAnnotations() { return new XSAnnotation[0]; }
      public void setGlobal(boolean pGlobal) {
        if (pGlobal) {
          throw new IllegalStateException("An extensions model group cannot be made global.");
        }
      }
      public XSObject getParentObject() { return parent; }
      public Attributes getOpenAttributes() { return null; }
    }

    public XSComplexContentImpl(XSType pOwner, XsTComplexType pType, XsEComplexContent pComplexContent)
        throws SAXException {
      super(pOwner, pType);
      complexContent = pComplexContent;
      XsTExtensionType extension = complexContent.getExtension();
      if (extension == null) {
        XsTComplexRestrictionType restriction = complexContent.getRestriction();
        if (restriction == null) {
          // TODO: Restriction of the ur-type
          throw new LocSAXException("Neither of extension or restriction, aka restriction of the ur-type: Not implemented",
                                       complexContent.getLocator());
        }
        XsQName base = restriction.getBase();
        if (base == null) {
          throw new LocSAXException("Invalid 'restriction': Missing 'base' attribute", getLocator());
        }
        XSType type = getXSSchema().getType(base);
        if (type == null) {
          throw new LocSAXException("Invalid 'restriction': Unknown base type " + base, getLocator());
        }
        if (type.isSimple()) {
          throw new LocSAXException("Invalid 'restriction': The base type " + getName() + " is simple.", getLocator());
        }
        XsTTypeDefParticle particle = restriction.getTypeDefParticle();

        XSGroup group = getGroupByParticle(particle);
        if (group == null) {
          // TODO: Restriction of the ur-type
          complexContentParticle = null;
        } else {
          complexContentParticle = new XSParticleImpl(group);
        }
        complexContentType = getContentTypeByParticle(particle, group);
        attributes = XSAttributeGroupImpl.getAttributes(XSTypeImpl.this, restriction);
        restrictedType = type;
      } else {
        XsQName base = extension.getBase();
        if (base == null) {
          throw new LocSAXException("Invalid 'extension': Missing 'base' attribute", getLocator());
        }
        XSType type = getXSSchema().getType(base);
        if (type == null) {
          throw new LocSAXException("Invalid 'extension': Unknown base type " + base, getLocator());
        }
        if (type.isSimple()) {
          throw new LocSAXException("Invalid 'extension': The base type " + base + " is simple.", getLocator());
        }
        XSComplexType extendedComplexType = type.getComplexType();
        if (extendedComplexType.hasSimpleContent()) {
          throw new LocSAXException("Invalid 'extension': The base type " + base + " has simple content.",
                                       getLocator());
        }
        XsTTypeDefParticle particle = extension.getTypeDefParticle();
        XSGroup group = getGroupByParticle(particle);
        XsComplexContentType groupType = getContentTypeByParticle(particle, group);

        if (XsComplexContentType.EMPTY.equals(groupType)) {
        	if (type == XSAnyType.getInstance()) {
        		complexContentType = null;
        		complexContentParticle = null;
        	} else {
        		complexContentType = extendedComplexType.getComplexContentType();
        		complexContentParticle = extendedComplexType.getParticle();
        	}
        } else if (extendedComplexType.isEmpty()) {
          complexContentType = groupType;
          XSParticleImpl particleImpl = new XSParticleImpl(group);
          particleImpl.setMinOccurs(getMinOccursByParticle(particle));
          particleImpl.setMaxOccurs(getMaxOccursByParticle(particle));
          complexContentParticle = particleImpl;
        } else {
          XSGroup sequenceGroup = new ExtensionGroup(pOwner, new XSParticle[]{extendedComplexType.getParticle(), new XSParticleImpl(group)});
          complexContentParticle = new XSParticleImpl(sequenceGroup);
          complexContentType = groupType;
        }

        XSAttributable[] inheritedAttributes = extendedComplexType.getAttributes();
        XSAttributable[] myAttributes = XSAttributeGroupImpl.getAttributes(XSTypeImpl.this, extension);
        attributes = new XSAttributable[inheritedAttributes.length + myAttributes.length];
        System.arraycopy(inheritedAttributes, 0, attributes, 0, inheritedAttributes.length);
        System.arraycopy(myAttributes, 0, attributes, inheritedAttributes.length, myAttributes.length);
        extendedType = type;
      }
    }

    public boolean hasComplexContent() { return true; }

    public XsComplexContentType getComplexContentType() {
      return complexContentType;
    }

    public XSParticle getParticle() {
      return complexContentParticle;
    }

    public XSAttributable[] getAttributes() { return attributes; }
  }


  public abstract class XSBasicComplexTypeImpl extends XSComplexTypeImpl {
    private final XSAttributable[] attributes;
    private XSParticle particle;
    private XsComplexContentType contentType;

    public XSBasicComplexTypeImpl(XSType pOwner, XsTComplexType pType) throws SAXException {
      super(pOwner, pType);
      attributes = XSAttributeGroupImpl.getAttributes(XSTypeImpl.this, pType);
    }

    public void setParticle(XsComplexContentType pType, XSParticle pParticle) {
      contentType = pType;
      particle = pParticle;
    }
    public XSParticle getParticle() { return particle; }
    public XSAttributable[] getAttributes() { return attributes; }
    public XsComplexContentType getComplexContentType() { return contentType; }
  }

  public class XSSequenceComplexTypeImpl extends XSBasicComplexTypeImpl {
    private final XsESequence sequence;

    public XSSequenceComplexTypeImpl(XSType pOwner, XsTComplexType pType) throws SAXException {
      super(pOwner, pType);
      sequence = null;
      setParticle(pType.isMixed() ? XsComplexContentType.MIXED : XsComplexContentType.EMPTY, null);
    }
    public XSSequenceComplexTypeImpl(XSType pOwner, XsTComplexType pType, XsESequence pSequence) throws SAXException {
      super(pOwner, pType);
      sequence = pSequence;
      XSGroup group = pOwner.getXSSchema().getXSObjectFactory().newXSGroup(pOwner, sequence);
      group.validate();
      XSParticleImpl particle = new XSParticleImpl(group);
      particle.setMaxOccurs(pSequence.getMaxOccurs());
      particle.setMinOccurs(pSequence.getMinOccurs());
      if (group.getParticles().length == 0) {
        setParticle(XsComplexContentType.EMPTY, particle);
      } else {
        setParticle(pType.isMixed() ? XsComplexContentType.MIXED : XsComplexContentType.ELEMENT_ONLY, particle);
      }
    }

    public boolean isSequence() { return true; }
  }

  public class XSChoiceComplexTypeImpl extends XSBasicComplexTypeImpl {
    private final XsEChoice choice;

    public XSChoiceComplexTypeImpl(XSType pOwner, XsTComplexType pType, XsEChoice pChoice) throws SAXException {
      super(pOwner, pType);
      choice = pChoice;
      XSGroup group = pOwner.getXSSchema().getXSObjectFactory().newXSGroup(pOwner, choice);
      group.validate();
      XSParticleImpl particle = new XSParticleImpl(group);
      particle.setMaxOccurs(pChoice.getMaxOccurs());
      particle.setMinOccurs(pChoice.getMinOccurs());      
      if (group.getParticles().length == 0) {
        throw new LocSAXException("The complex type must not have an empty element group, as it is a choice.",
                                     choice.getLocator());
      } else {
        setParticle(pType.isMixed() ? XsComplexContentType.MIXED : XsComplexContentType.ELEMENT_ONLY, particle);
      }
    }

    public boolean isChoice() { return true; }
  }

  public class XSAllComplexTypeImpl extends XSBasicComplexTypeImpl {
    private final XsTAll all;

    public XSAllComplexTypeImpl(XSType pOwner, XsTComplexType pType, XsTAll pAll) throws SAXException {
      super(pOwner, pType);
      all = pAll;
      XSGroup group = pOwner.getXSSchema().getXSObjectFactory().newXSGroup(pOwner, all);
      group.validate();
      XSParticleImpl particle = new XSParticleImpl(group);
      particle.setMaxOccurs(pAll.getMaxOccurs());
      particle.setMinOccurs(pAll.getMinOccurs());
      if (group.getParticles().length == 0) {
        setParticle(XsComplexContentType.EMPTY, particle);
      } else {
        setParticle(pType.isMixed() ? XsComplexContentType.MIXED : XsComplexContentType.ELEMENT_ONLY, particle);
      }
    }

    public boolean isAll() { return true; }
  }

  public class XSGroupComplexTypeImpl extends XSBasicComplexTypeImpl {
    private final XSGroup group;

    public XSGroupComplexTypeImpl(XSType pOwner, XsTComplexType pType, XSGroup pGroup,
    							  XsTGroupRef pRef) throws SAXException {
      super(pOwner, pType);
      group = pGroup;
      XSParticleImpl particle = new XSParticleImpl(group);
      particle.setMinOccurs(pRef.getMinOccurs());
      particle.setMaxOccurs(pRef.getMaxOccurs());
      if (group.getParticles().length == 0) {
        setParticle(XsComplexContentType.EMPTY, particle);
      } else {
        setParticle(pType.isMixed() ? XsComplexContentType.MIXED : XsComplexContentType.ELEMENT_ONLY, particle);
      }
    }

    public boolean isAll() { return group.isAll(); }
    public boolean isChoice() { return group.isChoice(); }
    public boolean isSequence() { return group.isSequence(); }
  }

  private final boolean isSimple;
  private final XsQName name;
  private boolean isGlobal;
  private XSSimpleType simpleType;
  private XSComplexType complexType;
  private boolean isValidated;
  private final XsEAnnotation xsAnnotation;
  private XSAnnotation[] annotations;

  protected boolean isValidated() { return isValidated; }

  protected XSTypeImpl(XSObject pParent, XsETopLevelSimpleType pSimpleType)
       throws SAXException {
    super(pParent, pSimpleType);
    isSimple = true;
    XsNCName myName = pSimpleType.getName();
    if (myName == null) {
      throw new LocSAXException("Invalid simple type: Missing 'name' attribute.",
                                   pSimpleType.getLocator());
    }
    XsESchema schema = pSimpleType.getXsESchema();
    this.name = new XsQName(schema.getTargetNamespace(), myName.toString(), schema.getTargetNamespacePrefix());
    xsAnnotation = pSimpleType.getAnnotation();
  }

  protected XSTypeImpl(XSObject pParent, XsTLocalSimpleType pSimpleType) {
    super(pParent, pSimpleType);
    isSimple = true;
    name = null;
    xsAnnotation = pSimpleType == null ? null : pSimpleType.getAnnotation();
  }

  protected XSTypeImpl(XSObject pParent, XsTComplexType pComplexType)
      throws SAXException {
    super(pParent, pComplexType);
    isSimple = false;
    XsNCName myName = pComplexType.getName();
    if (myName == null) {
      throw new LocSAXException("Invalid complex type: Missing 'name' attribute.",
                                   pComplexType.getLocator());
    }
    XsESchema schema = pComplexType.getXsESchema();
    this.name = new XsQName(schema.getTargetNamespace(), myName.toString(), schema.getTargetNamespacePrefix());
    xsAnnotation = pComplexType.getAnnotation();
  }

  protected XSTypeImpl(XSObject pParent, XsTLocalComplexType pComplexType) {
    super(pParent, pComplexType);
    isSimple = false;
    name = null;
    xsAnnotation = pComplexType.getAnnotation();
  }

  protected XSTypeImpl(XSObject pParent, XsTSimpleRestrictionType pRestriction)
      throws SAXException {
    super(pParent, pRestriction);
    XsQName myName = pRestriction.getBase();
    if (myName == null) {
      throw new LocSAXException("Invalid 'restriction': Missing 'base' attribute.",
                                   pRestriction.getLocator());
    }
    XSType type = getXSSchema().getType(myName);
    if (type == null) {
      throw new LocSAXException("Invalid 'restriction': Unknown 'base' type " + myName,
                                   pRestriction.getLocator());
    }
    type.validate();
    if (type.isSimple()) {
      throw new LocSAXException("The 'base' type " + myName + " of 'simpleContent/restriction' is simple." +
                                   " It ought to be a complex type with simple content: ",
                                   pRestriction.getLocator());
    }
    XSComplexType myComplexType = type.getComplexType();
    if (!myComplexType.hasSimpleContent()) {
      throw new LocSAXException("The 'base' type " + myName + " of 'simpleContent/restriction' is complex," +
                                   " but doesn't have simple content: ", pRestriction.getLocator());
    }
    XSObjectFactory factory = pParent.getXSSchema().getXSObjectFactory();
    if (myComplexType.isExtension()) {
      XSType extendedType = myComplexType.getSimpleContent().getType();
      extendedType.validate();
      XSSimpleType extendedSimpleType = extendedType.getSimpleType();
	
      XSSimpleType mySimpleType;
      if (extendedSimpleType.isAtomic()) {
      	mySimpleType = factory.newXSAtomicType(this, extendedType, pRestriction);
      } else if (extendedSimpleType.isList()) {
      	mySimpleType = factory.newXSListType(this, extendedType, pRestriction);
      } else if (extendedSimpleType.isUnion()) {
      	mySimpleType = factory.newXSUnionType(this, extendedType, pRestriction);
      } else {
      	throw new LocSAXException("Unknown restriction type: " + extendedType, 
      			                  pRestriction.getLocator());
      }

      simpleType = mySimpleType;
      //was: setSimpleType( extendedType.getSimpleType() );
    } else {
      XsTLocalSimpleType localSimpleType = pRestriction.getSimpleType();
      XSType restrictedType;
      if (localSimpleType != null) {
        restrictedType = factory.newXSType(this, localSimpleType);
      } else {
        restrictedType = myComplexType.getSimpleContent().getType();
      }
      restrictedType.validate();
      XSSimpleType restrictedSimpleType = restrictedType.getSimpleType();
      if (restrictedSimpleType.isAtomic()) {
        simpleType = factory.newXSAtomicType(this, restrictedType, pRestriction);
      } else if (restrictedSimpleType.isList()) {
        simpleType = factory.newXSListType(this, restrictedType, pRestriction);
      } else if (restrictedSimpleType.isUnion()) {
        simpleType = factory.newXSUnionType(this, restrictedType, pRestriction);
      }
    }
    this.name = null;
    isSimple = true;
    xsAnnotation = pRestriction.getAnnotation();
  }

  public XsQName getName() {
    return name;
  }

  public boolean isSimple() {
    return isSimple;
  }

  public boolean isGlobal() {
    return isGlobal;
  }

  public void setGlobal(boolean pGlobal) {
    isGlobal = pGlobal;
  }

  public XSAnnotation[] getAnnotations() {
    return annotations;
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

    if (isSimple()) {
      XSSimpleType mySimpleType;
      Object baseObject = getXsObject();
      if (baseObject instanceof XsTSimpleType) {
      	XsTSimpleType myXsTSimpleType = (XsTSimpleType) baseObject;
      	XsEList list = myXsTSimpleType.getList();
      	if (list == null) {
      		XsEUnion union = myXsTSimpleType.getUnion();
      		if (union == null) {
      			XsERestriction restriction = myXsTSimpleType.getRestriction();
      			if (restriction == null) {
      				throw new LocSAXException("Either of the 'list', 'union', or 'restriction' child elements must be set.",
      						myXsTSimpleType.getLocator());
      			}
      			XsQName myName = restriction.getBase();
      			XSType restrictedType;          
      			if (myName == null) {
      				XsTLocalSimpleType baseType = restriction.getSimpleType();
      				if (baseType == null) {
      					throw new LocSAXException("Neither the 'base' attribute nor an inner 'simpleType' element are present",
      							restriction.getLocator());
      				} else {
      					restrictedType = getXSSchema().getXSObjectFactory().newXSType(this, baseType);
      				}
      			} else {
      				restrictedType = getXSSchema().getType(myName);
      				if (restrictedType == null) {
      					throw new LocSAXException("Unknown base type: " + myName,
      							restriction.getLocator());
      				}
      			}
      			restrictedType.validate();
      			if (!restrictedType.isSimple()) {
      				throw new LocSAXException("The restricted type " + myName + " is complex.",
      						restriction.getLocator());
      			}
      			XSSimpleType baseType = restrictedType.getSimpleType();
      			if (baseType.isAtomic()) {
      				mySimpleType = getXSSchema().getXSObjectFactory().newXSAtomicType(this, restrictedType, restriction);
      			} else if (baseType.isList()) {
      				mySimpleType = getXSSchema().getXSObjectFactory().newXSListType(this, restrictedType, restriction);
      			} else if (baseType.isUnion()) {
      				mySimpleType = getXSSchema().getXSObjectFactory().newXSUnionType(this, restrictedType, restriction);
      			} else {
      				throw new LocSAXException("Unknown restriction type: " + baseType, restriction.getLocator());
      			}
      		} else {
      			mySimpleType = getXSSchema().getXSObjectFactory().newXSUnionType(this, union);
      		}
      	} else {
      		mySimpleType = getXSSchema().getXSObjectFactory().newXSListType(this, list);
      	}
      }
      else {
      	mySimpleType = getSimpleType();
      }
      
      this.simpleType = mySimpleType;
    } else {
      XSComplexTypeImpl myComplexType;
      XsTComplexType myXsTComplexType = (XsTComplexType) getXsObject();
      XsESimpleContent simpleContent = myXsTComplexType.getSimpleContent();
      if (simpleContent == null) {
        XsEComplexContent complexContent = myXsTComplexType.getComplexContent();
        if (complexContent == null) {
          XsTTypeDefParticle particle = myXsTComplexType.getTypeDefParticle();
          if (particle == null) {
            myComplexType = new XSSequenceComplexTypeImpl(this, myXsTComplexType);
          } else if (particle instanceof XsESequence) {
            myComplexType = new XSSequenceComplexTypeImpl(this, myXsTComplexType, (XsESequence) particle);
          } else if (particle instanceof XsEChoice) {
            myComplexType = new XSChoiceComplexTypeImpl(this, myXsTComplexType, (XsEChoice) particle);
          } else if (particle instanceof XsTAll) {
            myComplexType = new XSAllComplexTypeImpl(this, myXsTComplexType, (XsTAll) particle);
          } else if (particle instanceof XsTGroupRef) {
            XsTGroupRef groupRef = (XsTGroupRef) particle;
            XsQName myName = groupRef.getRef();
            if (myName == null) {
              throw new LocSAXException("Missing 'ref' attribute", groupRef.getLocator());
            }
            XSGroup group = getXSSchema().getGroup(myName);
            if (group == null) {
              throw new LocSAXException("Unknown group: " + myName, getLocator());
            }
            group.validate();
            myComplexType = new XSGroupComplexTypeImpl(this, myXsTComplexType, group, groupRef);
          } else {
            throw new IllegalStateException("Invalid particle: " + particle.getClass().getName());
          }
        } else {
          XSComplexContentImpl complexContentImpl = new XSComplexContentImpl(this, myXsTComplexType, complexContent);
          myComplexType = complexContentImpl;
        }
      } else {
        XSSimpleContentImpl simpleContentImpl = new XSSimpleContentImpl(this, myXsTComplexType, simpleContent);
        myComplexType = simpleContentImpl;
      }
      this.complexType = myComplexType;
      myComplexType.validate();
    }
  }

  public XSSimpleType getSimpleType() throws SAXException {
    validate();
    XSSimpleType result = simpleType;
    if (result == null) {
      throw new IllegalStateException("This is a complex type.");
    }
    return result;
  }

  public XSComplexType getComplexType() throws SAXException {
    validate();
    XSComplexType result = complexType;
    if (result == null) {
      if (getName() == null) {
        throw new IllegalStateException("This is a simple type.");
      } else {
        throw new IllegalStateException("The type " + getName() + " is simple.");
      }
    }
    return result;
  }

  public boolean isBuiltin() {
	return false;
  }

  public XsSchemaHeader getSchemaHeader() {
  	return getXsObject().getXsESchema();
  }
}
