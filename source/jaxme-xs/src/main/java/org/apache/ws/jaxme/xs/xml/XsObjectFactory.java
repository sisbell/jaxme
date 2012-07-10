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
package org.apache.ws.jaxme.xs.xml;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.ws.jaxme.xs.parser.XsSAXParser;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface XsObjectFactory {
  public Locator getLocator();
  public XMLReader newXMLReader(boolean pValidating) throws ParserConfigurationException, SAXException;
  public XsAGDefRef newXsAGDefRef(XsObject pOwner);
  public XsAGOccurs newXsAGOccurs(XsObject pOwner);
  public XsEAnnotation newXsEAnnotation(XsObject pParent);
  public XsEAny newXsEAny(XsObject pParent);
  public XsEAppinfo newXsEAppinfo(XsObject pParent);
  public XsEComplexContent newXsEComplexContent(XsObject pParent);
  public XsEChoice newXsEChoice(XsObject pParent);
  public XsEDocumentation newXsEDocumentation(XsObject pParent);
  public XsEEnumeration newXsEEnumeration(XsObject pParent);
  public XsEField newXsEField(XsObject pParent);
  public XsEFractionDigits newXsEFractionDigits(XsObject pParent);
  public XsEKey newXsEKey(XsObject pParent);
  public XsEImport newXsEImport(XsObject pParent);
  public XsEInclude newXsEInclude(XsObject pParent);
  public XsEKeyref newXsEKeyref(XsObject pParent);
  public XsELength newXsELength(XsObject pParent);
  public XsEList newXsEList(XsObject pParent);
  public XsEMaxExclusive newXsEMaxExclusive(XsObject pParent);
  public XsEMaxInclusive newXsEMaxInclusive(XsObject pParent);
  public XsEMaxLength newXsEMaxLength(XsObject pParent);
  public XsEMinExclusive newXsEMinExclusive(XsObject pParent);
  public XsEMinInclusive newXsEMinInclusive(XsObject pParent);
  public XsEMinLength newXsEMinLength(XsObject pParent);
  public XsENotation newXsENotation(XsObject pParent);
  public XsEPattern newXsEPattern(XsObject pParent);
  public XsERedefine newXsERedefine(XsObject pParent);
  public XsERestriction newXsERestriction(XsObject pParent);
  public XsESchema newXsESchema();
  public XsESelector newXsESelector(XsObject pParent);
  public XsESequence newXsESequence(XsObject pParent);
  public XsESimpleContent newXsESimpleContent(XsObject pParent);
  public XsETopLevelSimpleType newXsETopLevelSimpleType(XsObject pParent);
  public XsETotalDigits newXsETotalDigits(XsObject pParent);
  public XsEUnion newXsEUnion(XsObject pParent);
  public XsEUnique newXsEUnique(XsObject pParent);
  public XsEWhiteSpace newXsEWhiteSpace(XsObject pParent);
  public XsGAllModel newXsGAllModel(XsObject pParent);
  public XsGAttrDecls newXsGAttrDecls(XsObject pOwner);
  public XsGIdentityConstraint newXsGIdentityConstraint(XsObject pOwner);
  public XsGParticle newXsGParticle(XsObject pOwner);
  public XsGComplexTypeModel newXsGComplexTypeModel(XsObject pOwner);
  public XsGSimpleRestrictionModel newXsGSimpleRestrictionModel(XsObject pOwner);
  public XsGTypeDefParticle newXsGTypeDefParticle(XsObject pOwner);
  public XsSAXParser newXsSAXParser();
  public XsSAXParser newXsSAXParser(Object pBean);
  public XsTAll newXsTAll(XsObject pParent);
  public XsTAnnotated newXsTAnnotated(XsObject pParent);
  public XsTAttribute newXsTAttribute(XsObject pParent);
  public XsTAttributeGroup newXsTAttributeGroup(XsObject pParent);
  public XsTAttributeGroupRef newXsTAttributeGroupRef(XsObject pParent);
  public XsTComplexRestrictionType newXsTComplexRestrictionType(XsObject pParent);
  public XsTComplexType newXsTComplexType(XsObject pParent);
  public XsTExtensionType newXsTExtensionType(XsObject pParent);
  public XsTLocalElement newXsTLocalElement(XsObject pParent);
  /** <p>Creates a new instance of {@link XsTLocalElement}, which
   * is located inside of an <code>xs:all</code> group. The
   * element ensures that its <code>minOccurs</code> and
   * <code>maxOccurs</code> values are 0 or 1.</p>
   */
  public XsTLocalElement newXsTLocalAllElement(XsObject pParent);
  public XsTLocalComplexType newXsTLocalComplexType(XsObject pParent);
  public XsTLocalSimpleType newXsTLocalSimpleType(XsObject pParent);
  public XsTNamedGroup newXsTNamedGroup(XsObject pParent);
  public XsTGroup newXsTGroup(XsObject pParent);
  public XsTGroupRef newXsTGroupRef(XsObject pParent);
  public XsTSimpleExplicitGroup newXsTSimpleExplicitGroup(XsObject pParent);
  public XsTSimpleExtensionType newXsTSimpleExtensionType(XsObject pParent);
  public XsTSimpleRestrictionType newXsTSimpleRestrictionType(XsObject pParent);
  public XsTTopLevelElement newXsTTopLevelElement(XsObject pParent);
  public XsTWildcard newXsTWildcard(XsObject pParent);
}