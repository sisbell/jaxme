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

import org.apache.ws.jaxme.xs.SchemaTransformer;
import org.apache.ws.jaxme.xs.XSAnnotation;
import org.apache.ws.jaxme.xs.XSAny;
import org.apache.ws.jaxme.xs.XSAppinfo;
import org.apache.ws.jaxme.xs.XSAttribute;
import org.apache.ws.jaxme.xs.XSAttributeGroup;
import org.apache.ws.jaxme.xs.XSDocumentation;
import org.apache.ws.jaxme.xs.XSElement;
import org.apache.ws.jaxme.xs.XSEnumeration;
import org.apache.ws.jaxme.xs.XSGroup;
import org.apache.ws.jaxme.xs.XSIdentityConstraint;
import org.apache.ws.jaxme.xs.XSKeyRef;
import org.apache.ws.jaxme.xs.XSNotation;
import org.apache.ws.jaxme.xs.XSObject;
import org.apache.ws.jaxme.xs.XSObjectFactory;
import org.apache.ws.jaxme.xs.XSSchema;
import org.apache.ws.jaxme.xs.XSSimpleContentType;
import org.apache.ws.jaxme.xs.XSSimpleType;
import org.apache.ws.jaxme.xs.XSType;
import org.apache.ws.jaxme.xs.XSWildcard;
import org.apache.ws.jaxme.xs.parser.XSContext;
import org.apache.ws.jaxme.xs.xml.*;
import org.xml.sax.SAXException;


/** <p>Default implementation of the {@link XSObjectFactory}.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XSObjectFactoryImpl implements XSObjectFactory {
  private static final XSAnnotation[] NO_ANNOTATIONS = new XSAnnotation[] {};

  public XSLogicalParser newXSLogicalParser() {
    return new XSLogicalParser();
  }

  public XSSchema newXSSchema(XSContext pContext, XsESchema pSchema) throws SAXException {
    return new XSSchemaImpl(pContext, pSchema);
  }

  public XSAnnotation newXSAnnotation(XSObject pParent, XsEAnnotation pAnnotation) throws SAXException {
    return new XSAnnotationImpl(pParent, pAnnotation);
  }

  public XSAnnotation[] newXSAnnotations(XSObject pParent, XsEAnnotation pAnnotation) throws SAXException {
    if ( pAnnotation == null ) {
      return NO_ANNOTATIONS;
    } else {
      return new XSAnnotation[] { newXSAnnotation(pParent, pAnnotation) };
    }
  }


  public XSAppinfo newXSAppinfo(XSObject pParent, XsEAppinfo pAppinfo) {
    return new XSAppinfoImpl(pParent, pAppinfo);
  }

  public XSSimpleType newXSAtomicType(XSType pParent, XSType pRestrictedType, XsERestriction pRestriction) throws SAXException {
    return new XSAtomicTypeRestrictionImpl(pParent, pRestrictedType, pRestriction);
  }

  public XSSimpleType newXSAtomicType(XSType pParent, XSType pRestrictedType, XsTSimpleRestrictionType pRestriction) throws SAXException {
    return new XSAtomicTypeRestrictionImpl(pParent, pRestrictedType, pRestriction);
  }

  public XSAttribute newXSAttribute(XSObject pParent, XsTAttribute pAttribute) throws SAXException {
    return new XSAttributeImpl(pParent, pAttribute);
  }

  public XSAttributeGroup newXSAttributeGroup(XSObject pParent, XsTAttributeGroup pGroup) throws SAXException {
    return new XSAttributeGroupImpl(pParent, pGroup);
  }

  public XSDocumentation newXSDocumentation(XSObject pParent, XsEDocumentation pDocumentation) throws SAXException {
    return new XSDocumentationImpl(pParent, pDocumentation);
  }

  public XSElement newXSElement(XSObject pParent, XsTElement pElement) throws SAXException {
    return new XSElementImpl(pParent, pElement);
  }

  public XSEnumeration newXSEnumeration(XSObject pParent, XsEEnumeration pEnumeration) throws SAXException {
    return new XSEnumerationImpl(pParent, pEnumeration);
  }

  public XSGroup newXSGroup(XSObject pParent, XsTAll pAll) throws SAXException {
    return new XSGroupImpl(pParent, pAll);
  }

  public XSGroup newXSGroup(XSObject pParent, XsEChoice pChoice) throws SAXException {
    return new XSGroupImpl(pParent, pChoice);
  }

  public XSGroup newXSGroup(XSObject pParent, XsESequence pSequence) throws SAXException {
    return new XSGroupImpl(pParent, pSequence);
  }

  public XSGroup newXSGroup(XSObject pParent, XsTGroupRef pGroupRef) throws SAXException {
    return new XSGroupImpl(pParent, pGroupRef);
  }

  public XSGroup newXSGroup(XSObject pParent, XsTNamedGroup pNamedGroup) throws SAXException {
    return new XSGroupImpl(pParent, pNamedGroup);
  }

  public XSSimpleType newXSListType(XSType pParent, XsEList pList) throws SAXException {
    return new XSListTypeImpl(pParent, pList);
  }

  public XSSimpleType newXSListType(XSType pParent, XSType pRestrictedType, XsERestriction pRestriction) throws SAXException {
    return new XSListTypeRestrictionImpl(pParent, pRestrictedType, pRestriction);
  }

  public XSSimpleType newXSListType(XSType pParent, XSType pRestrictedType, XsTSimpleRestrictionType pRestriction) throws SAXException {
    return new XSListTypeRestrictionImpl(pParent, pRestrictedType, pRestriction);
  }

  public XSType newXSType(XSObject pParent, XsETopLevelSimpleType pType) throws SAXException {
    return new XSTypeImpl(pParent, pType);
  }

  public XSType newXSType(XSObject pParent, XsTComplexType pType) throws SAXException {
    return new XSTypeImpl(pParent, pType);
  }

  public XSType newXSType(XSObject pParent, XsTLocalComplexType pType) throws SAXException {
    return new XSTypeImpl(pParent, pType);
  }

  public XSType newXSType(XSObject pParent, XsTLocalSimpleType pType) throws SAXException {
    return new XSTypeImpl(pParent, pType);
  }

  public XSType newXSType(XSObject pParent, XsTSimpleRestrictionType pType) throws SAXException {
    return new XSTypeImpl(pParent, pType);
  }

  public XSSimpleType newXSUnionType(XSType pParent, XsEUnion pUnion) throws SAXException {
    return new XSUnionTypeImpl(pParent, pUnion);
  }

  public XSSimpleType newXSUnionType(XSType pParent, XSType pRestrictedType, XsERestriction pRestriction) throws SAXException {
    return new XSUnionTypeRestrictionImpl(pParent, pRestrictedType, pRestriction);
  }

  public XSSimpleType newXSUnionType(XSType pParent, XSType pRestrictedType, XsTSimpleRestrictionType pRestriction) throws SAXException {
    return new XSUnionTypeRestrictionImpl(pParent, pRestrictedType, pRestriction);
  }

  public XSNotation newXSNotation(XSObject pParent, XsENotation pNotation) throws SAXException {
    return new XSNotationImpl(pParent, pNotation);
  }

  public XSAny newXSAny(XSObject pParent, XsEAny pAny) throws SAXException {
    return new XSAnyImpl(pParent, pAny);
  }

  public XSSimpleContentType newXSSimpleContentType(XSType pParent, XSType pSimpleType, XsObject pBaseType) throws SAXException {
    return new XSSimpleContentTypeImpl(pParent, pSimpleType, pBaseType);
  }

  public XSWildcard newXSWildcard(XSObject pParent, XsTWildcard pWildcard) throws SAXException {
    return new XSWildcardImpl(pParent, pWildcard);
  }

  public XSIdentityConstraint newXSIdentityConstraint( 
    XSElement pParent, 
    XsEKey key 
  )
    throws SAXException 
  {
    return new XSIdentityConstraintImpl( pParent, key );
  }

  public XSKeyRef newXSKeyRef( 
    XSElement pParent, 
    XsEKeyref keyRef 
  ) throws SAXException {
    return new XSKeyRefImpl( pParent, keyRef );
  }

  public XSIdentityConstraint newXSIdentityConstraint( 
    XSElement pParent, 
    XsEUnique unique 
  ) throws SAXException {
    return new XSIdentityConstraintImpl( pParent, unique );
  }

  public SchemaTransformer getSchemaTransformer() {
	return null;
  }
}
