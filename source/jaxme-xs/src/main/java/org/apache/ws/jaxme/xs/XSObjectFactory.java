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
package org.apache.ws.jaxme.xs;

import org.apache.ws.jaxme.xs.impl.XSLogicalParser;
import org.apache.ws.jaxme.xs.parser.XSContext;
import org.apache.ws.jaxme.xs.xml.*;
import org.xml.sax.SAXException;


/** <p>An object factory for the logical XSParser.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface XSObjectFactory {
  /** <p>Returns a new instance of {@link XSLogicalParser}.</p>
   */
  public XSLogicalParser newXSLogicalParser();
  /** <p>Returns a new instance of {@link XSSchema}.</p>
   */
  public XSSchema newXSSchema(XSContext pContext, XsESchema pSchema) throws SAXException;
  /** <p>Returns a new instance of {@link XSAnnotation}.</p>
   */
  public XSAnnotation newXSAnnotation(XSObject pParent, XsEAnnotation pAnnotation) throws SAXException;
  /** <p>Returns a new instances of {@link XSAnnotation}.</p>
   */
  public XSAnnotation[] newXSAnnotations(XSObject pParent, XsEAnnotation pAnnotation) throws SAXException;
  /** <p>Returns a new instance of {@link XSAppinfo}.</p>
   */
  public XSAppinfo newXSAppinfo(XSObject pParent, XsEAppinfo pAppinfo);
  /** <p>Returns a new instance of {@link XSSimpleType}.</p>
   */
  public XSSimpleType newXSAtomicType(XSType pParent, XSType pRestrictedType, XsERestriction pRestriction) throws SAXException;
  /** <p>Returns a new instance of {@link XSSimpleType}.</p>
   */
  public XSSimpleType newXSAtomicType(XSType pParent, XSType pRestrictedType, XsTSimpleRestrictionType pRestriction) throws SAXException;
  /** <p>Returns a new instance of {@link XSAttribute}.</p>
   */
  public XSAttribute newXSAttribute(XSObject pParent, XsTAttribute pAttribute) throws SAXException;
  /** <p>Returns a new instance of {@link XSAttributeGroup}.</p>
   */
  public XSAttributeGroup newXSAttributeGroup(XSObject pParent, XsTAttributeGroup pGroup) throws SAXException;
  /** <p>Returns a new instance of {@link XSDocumentation}.</p>
   */
  public XSDocumentation newXSDocumentation(XSObject pParent, XsEDocumentation pDocumentation) throws SAXException;
  /** <p>Returns a new instance of {@link XSElement}.</p>
   */
  public XSElement newXSElement(XSObject pParent, XsTElement pElement) throws SAXException;
  /** <p>Returns a new instance of {@link XSEnumeration}.</p>
   */
  public XSEnumeration newXSEnumeration(XSObject pParent, XsEEnumeration pEnumeration) throws SAXException;
  /** <p>Returns a new instance of {@link XSSimpleType}.</p>
   */
  public XSSimpleType newXSListType(XSType pParent, XsEList pList) throws SAXException;
  /** <p>Returns a new instance of {@link XSSimpleType}.</p>
   */
  public XSSimpleType newXSListType(XSType pParent, XSType pRestrictedType, XsERestriction pRestriction) throws SAXException;
  /** <p>Returns a new instance of {@link XSSimpleType}.</p>
   */
  public XSSimpleType newXSListType(XSType pParent, XSType pRestrictedType, XsTSimpleRestrictionType pRestriction) throws SAXException;
  /** <p>Returns a new instance of {@link XSGroup}.</p>
   */
  public XSGroup newXSGroup(XSObject pParent, XsTNamedGroup pGroup) throws SAXException;
  /** <p>Returns a new instance of {@link XSGroup}.</p>
   */
  public XSGroup newXSGroup(XSObject pParent, XsTAll pAll) throws SAXException;
  /** <p>Returns a new instance of {@link XSGroup}.</p>
   */
  public XSGroup newXSGroup(XSObject pParent, XsTGroupRef pGroup) throws SAXException;
  /** <p>Returns a new instance of {@link XSGroup}.</p>
   */
  public XSGroup newXSGroup(XSObject pParent, XsEChoice pChoice) throws SAXException;
  /** <p>Returns a new instance of {@link XSGroup}.</p>
   */
  public XSGroup newXSGroup(XSObject pParent, XsESequence pSequence) throws SAXException;
  /** <p>Returns a new instance of {@link XSType}.</p>
   */
  public XSType newXSType(XSObject pParent, XsETopLevelSimpleType pType) throws SAXException;
  /** <p>Returns a new instance of {@link XSType}.</p>
   */
  public XSType newXSType(XSObject pParent, XsTComplexType pType) throws SAXException;
  /** <p>Returns a new instance of {@link XSType}.</p>
   */
  public XSType newXSType(XSObject pParent, XsTLocalComplexType pType) throws SAXException;
  /** <p>Returns a new instance of {@link XSType}.</p>
   */
  public XSType newXSType(XSObject pParent, XsTLocalSimpleType pType) throws SAXException;
  /** <p>Returns a new instance of {@link XSType}.</p>
   */
  public XSType newXSType(XSObject pParent, XsTSimpleRestrictionType pType) throws SAXException;
  /** <p>Returns a new instance of {@link XSNotation}.</p>
   */
  public XSNotation newXSNotation(XSObject pParent, XsENotation pType) throws SAXException;
  /** <p>Returns a new instance of {@link XSSimpleContentType}.</p>
   */
  public XSSimpleContentType newXSSimpleContentType(XSType pComplexType, XSType pSimpleType, XsObject pBaseObject) throws SAXException;
  /** <p>Returns a new instance of {@link XSSimpleType}.</p>
   */
  public XSSimpleType newXSUnionType(XSType pParent, XsEUnion pUnion) throws SAXException;
  /** <p>Returns a new instance of {@link XSSimpleType}.</p>
   */
  public XSSimpleType newXSUnionType(XSType pParent, XSType pRestrictedType, XsERestriction pRestriction) throws SAXException;
  /** <p>Returns a new instance of {@link XSSimpleType}.</p>
   */
  public XSSimpleType newXSUnionType(XSType pParent, XSType pRestrictedType, XsTSimpleRestrictionType pRestriction) throws SAXException;
  /** <p>Returns a new instance of {@link XSWildcard}.</p>
   */
  public XSWildcard newXSWildcard(XSObject pParent, XsTWildcard pWildcard) throws SAXException;
  /** <p>Returns a new instance of {@link XSAny}.</p>
   */
  public XSAny newXSAny(XSObject pParent, XsEAny pAny) throws SAXException;
  /** <p>Returns a new instance of {@link XSKeyRef}.</p>
   */
  public XSKeyRef newXSKeyRef(XSElement pParent, XsEKeyref keyRef) throws SAXException;
  /** <p>Returns a new instance of {@link XSIdentityConstraint}.</p>
   */
  public XSIdentityConstraint newXSIdentityConstraint(XSElement pParent,
													  XsEUnique unique) throws SAXException;
  /** <p>Returns a new instance of {@link XSIdentityConstraint}.</p>
   */
  public XSIdentityConstraint newXSIdentityConstraint(XSElement pParent,
													  XsEKey key) throws SAXException;
  /** Returns the object factories schema transformer.
   * May be null, in which case no schema transformation
   * occurs.
   */
  public SchemaTransformer getSchemaTransformer();
}
