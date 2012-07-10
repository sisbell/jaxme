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
import java.util.List;

import org.apache.ws.jaxme.xs.XSAnnotation;
import org.apache.ws.jaxme.xs.XSElement;
import org.apache.ws.jaxme.xs.XSGroup;
import org.apache.ws.jaxme.xs.XSIdentityConstraint;
import org.apache.ws.jaxme.xs.XSKeyRef;
import org.apache.ws.jaxme.xs.XSObject;
import org.apache.ws.jaxme.xs.XSObjectFactory;
import org.apache.ws.jaxme.xs.XSSchema;
import org.apache.ws.jaxme.xs.XSType;
import org.apache.ws.jaxme.xs.parser.impl.LocSAXException;
import org.apache.ws.jaxme.xs.xml.XsAnyURI;
import org.apache.ws.jaxme.xs.xml.XsBlockSet;
import org.apache.ws.jaxme.xs.xml.XsEKey;
import org.apache.ws.jaxme.xs.xml.XsEKeyref;
import org.apache.ws.jaxme.xs.xml.XsESchema;
import org.apache.ws.jaxme.xs.xml.XsEUnique;
import org.apache.ws.jaxme.xs.xml.XsFormChoice;
import org.apache.ws.jaxme.xs.xml.XsNCName;
import org.apache.ws.jaxme.xs.xml.XsQName;
import org.apache.ws.jaxme.xs.xml.XsTElement;
import org.apache.ws.jaxme.xs.xml.XsTIdentityConstraint;
import org.apache.ws.jaxme.xs.xml.XsTTopLevelElement;
import org.xml.sax.SAXException;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XSElementImpl extends XSOpenAttrsImpl implements XSElement {
  private static final XSIdentityConstraint[] NO_CONSTRAINTS
  = new XSIdentityConstraint[] {};

  private static final XSKeyRef[] NO_KEY_REFS = new XSKeyRef[] {};

  private final XsQName name;
  private final boolean isGlobal;
  private XSAnnotation[] annotations;
  private boolean isValidated;
  private boolean isNillable;
  private XSType type;
  private XSGroup substitutionGroup;
  private XSIdentityConstraint[] identityConstraints;
  private XSKeyRef[] keyReferences;

  protected XsTElement getXsTElement() {
    return (XsTElement) getXsObject();
  }

  public boolean isReference() {
    return getXsTElement().getRef() != null;
  }

  protected boolean isInnerSimpleType() {
    return getXsTElement().getSimpleType() != null;
  }

  protected boolean isInnerComplexType() {
    return getXsTElement().getComplexType() != null;
  }

  protected XSElementImpl(XSObject pParent, XsTElement pBaseElement)
      throws SAXException 
  {
    super(pParent, pBaseElement);

    XsQName qName;

    if (isReference()) {
      qName = pBaseElement.getRef();
    } else {
      XsNCName myName = pBaseElement.getName();
      if (myName == null) {
        throw new LocSAXException("Invalid element: Must have either of its 'ref' or 'name' attributes set.",
                                     getLocator());
      }

      XsESchema schema = pBaseElement.getXsESchema();
      XsAnyURI namespace;
      String namespacePrefix;
      boolean qualified = pBaseElement.isGlobal();
      if (!qualified) {
        XsFormChoice form = pBaseElement.getForm();
        if (form == null) {
          form = schema.getElementFormDefault();
        }
        qualified = XsFormChoice.QUALIFIED.equals(form);
      }
      if (qualified) {
        namespace = schema.getTargetNamespace();
        namespacePrefix = schema.getTargetNamespacePrefix();
      } else {
        namespace = null;
        namespacePrefix = null;
      }
      qName = new XsQName(namespace, myName.toString(), namespacePrefix);

      configureIdentityConstraints(pParent, pBaseElement);
    }

    name = qName;
    isGlobal = isReference()  ||  (pBaseElement instanceof XsTTopLevelElement);
    isNillable = pBaseElement.getNillable();

    annotations = getXSSchema().getXSObjectFactory().newXSAnnotations(
      this, 
      pBaseElement.getAnnotation()
    );
  }

  public boolean isGlobal() {
    return isGlobal;
  }
  
  public boolean isNillable() {
    return isNillable;
  }  

  public XsQName getName() {
    return name;
  }

  public XSType getType() {
    return type;
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

    XSSchema schema = getXSSchema();
    XSObjectFactory factory = schema.getXSObjectFactory();

    XSType myType;
    if (isReference()) {
      XSElement element = schema.getElement(getName());
      if (element == null) {
        throw new LocSAXException("Invalid element reference: " + getName() + " is not defined.",
                                     getLocator());
      }
      element.validate();
      isNillable = element.isNillable();    
      myType = element.getType();
      
    } else {
      XsTElement element = getXsTElement();
      if (isInnerSimpleType()) {
        myType = factory.newXSType(this, element.getSimpleType());
      } else if (isInnerComplexType()) {
        myType = factory.newXSType(this, element.getComplexType());
      } else {
        XsQName typeName = element.getType();
        if (typeName == null) {
          throw new LocSAXException("Invalid element: Either of its 'type' or 'ref' attributes or its 'simpleType' or 'complexType' children must be set.",
                                       getLocator());
        }
        myType = schema.getType(typeName);
        if (myType == null) {
          throw new LocSAXException("Invalid element: The type " + typeName + " is not defined.",
                                       getLocator());
        }
      }
    }
    this.type = myType;
    

    myType.validate();
    validateAllIn( annotations );
    validateAllIn( identityConstraints );
    validateAllIn( keyReferences );
  }

  public XSAnnotation[] getAnnotations() {
    return annotations;
  }

  public String getDefault() {
    return getXsTElement().getDefault();
  }

  public String getFixed() {
    return getXsTElement().getFixed();
  }

  public XsQName getSubstitutionGroupName() {
    return getXsTElement().getSubstitutionGroup();
  }

  public boolean isBlockedForSubstitution() {
    XsBlockSet blockSet = getXsTElement().getBlock();
    if (blockSet == null) {
      blockSet = getXsTElement().getXsESchema().getBlockDefault();
    }
    return !blockSet.isSubstitutionAllowed();
  }

  public boolean isAbstract() {
    return getXsTElement().getAbstract();
  }

  public void setSubstitutionGroup(XSGroup pGroup) {
    substitutionGroup = pGroup;
  }

  public XSGroup getSubstitutionGroup() {
    return substitutionGroup;
  }

  public XSIdentityConstraint[] getIdentityConstraints() {
    return identityConstraints;
  }

  public XSKeyRef[] getKeyRefs() {
    return keyReferences;
  }

  /**
   * Process the unique, key and keyref identity constraints into instances
   * of XSIdentityConstraint and XSKeyRef.
   */
  private void configureIdentityConstraints(XSObject pParent, XsTElement base)
    throws SAXException
  {
    XsTIdentityConstraint[] rawConstraints = base.getIdentityConstraints();
    final int numRawConstraints = rawConstraints.length;

    if ( numRawConstraints == 0 ) {
      identityConstraints = NO_CONSTRAINTS;
      keyReferences = NO_KEY_REFS;

      return;
    }

    XSSchema schema = getXSSchema();
    XSObjectFactory factory = schema.getXSObjectFactory();
    List constraints = new ArrayList(1);
    List refKeys  = new ArrayList(1);

    for ( int i=0; i<numRawConstraints; i++ ) {
      XsTIdentityConstraint raw = rawConstraints[i];

      if ( raw instanceof XsEKeyref ) {
        XSKeyRef keyRef = factory.newXSKeyRef( this, (XsEKeyref) raw );

        refKeys.add( keyRef );
        schema.add( keyRef );
      } else if ( raw instanceof XsEKey ) {
        XSIdentityConstraint ic = factory.newXSIdentityConstraint( 
          this, 
          (XsEKey) raw 
        );

        constraints.add( ic );
        schema.add( ic );
      } else if( raw instanceof XsEUnique ) {
        XSIdentityConstraint ic = factory.newXSIdentityConstraint( 
          this, 
          (XsEUnique) raw 
        );

        constraints.add( ic );
        schema.add( ic );
      }
    }

    int numConstraints = constraints.size();
    if ( numConstraints == 0 ) {
      identityConstraints =  NO_CONSTRAINTS;
    } else {
      identityConstraints = (XSIdentityConstraint[]) constraints.toArray( 
        new XSIdentityConstraint[numConstraints] 
      );
    }

    int numKeyRefs = refKeys.size();
    if ( numKeyRefs == 0 ) {
      keyReferences =  NO_KEY_REFS;
    } else {
      keyReferences = (XSKeyRef[]) refKeys.toArray(new XSKeyRef[numKeyRefs]);
    }
  }
}
