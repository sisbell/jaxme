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

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.apache.ws.jaxme.xs.XSParser;
import org.apache.ws.jaxme.xs.parser.XSContext;
import org.apache.ws.jaxme.xs.parser.XsSAXParser;
import org.apache.ws.jaxme.xs.parser.impl.AbstractXsSAXParser;
import org.apache.ws.jaxme.xs.xml.*;


/** <p>The XsObjectFactory, which allows to extend the parser.
 * The various elements and/or attributes use this factory to
 * create new objects.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XsObjectFactoryImpl implements XsObjectFactory {
  private static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
  private static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";

  protected XSContext getContext() {
    return XSParser.getRunningInstance().getContext();
  }

  public Locator getLocator() {
    return getContext().getLocator();
  }

  public XsAGDefRef newXsAGDefRef(XsObject pOwner) {
    return new XsAGDefRefImpl(pOwner);
  }

  public XsAGOccurs newXsAGOccurs(XsObject pOwner) {
    return new XsAGOccursImpl(pOwner);
  }

  public XsEAnnotation newXsEAnnotation(XsObject pParent) {
    return new XsEAnnotationImpl(pParent);
  }

  public XsEAny newXsEAny(XsObject pParent) {
    return new XsEAnyImpl(pParent);
  }

  public XsEAppinfo newXsEAppinfo(XsObject pParent) {
    return new XsEAppinfoImpl(pParent);
  }

  public XsEComplexContent newXsEComplexContent(XsObject pParent) {
    return new XsEComplexContentImpl(pParent);
  }

  public XsEChoice newXsEChoice(XsObject pParent) {
    return new XsEChoiceImpl(pParent);
  }

  public XsEDocumentation newXsEDocumentation(XsObject pParent) {
    return new XsEDocumentationImpl(pParent);
  }

  public XsEEnumeration newXsEEnumeration(XsObject pParent) {
    return new XsEEnumerationImpl(pParent);
  }

  public XsEFractionDigits newXsEFractionDigits(XsObject pParent) {
    return new XsEFractionDigitsImpl(pParent);
  }

  public XsEField newXsEField(XsObject pParent) {
    return new XsEFieldImpl(pParent);
  }

  public XsEImport newXsEImport(XsObject pParent) {
    return new XsEImportImpl(pParent);
  }

  public XsEInclude newXsEInclude(XsObject pParent) {
    return new XsEIncludeImpl(pParent);
  }

  public XsEKey newXsEKey(XsObject pParent) {
    return new XsEKeyImpl(pParent);
  }

  public XsEKeyref newXsEKeyref(XsObject pParent) {
    return new XsEKeyrefImpl(pParent);
  }

  public XsELength newXsELength(XsObject pParent) {
    return new XsELengthImpl(pParent);
  }

  public XsEList newXsEList(XsObject pParent) {
    return new XsEListImpl(pParent);
  }

  public XsEMaxExclusive newXsEMaxExclusive(XsObject pParent) {
    return new XsEMaxExclusiveImpl(pParent);
  }

  public XsEMaxInclusive newXsEMaxInclusive(XsObject pParent) {
    return new XsEMaxInclusiveImpl(pParent);
  }

  public XsEMaxLength newXsEMaxLength(XsObject pParent) {
    return new XsEMaxLengthImpl(pParent);
  }

  public XsEMinExclusive newXsEMinExclusive(XsObject pParent) {
    return new XsEMinExclusiveImpl(pParent);
  }

  public XsEMinInclusive newXsEMinInclusive(XsObject pParent) {
    return new XsEMinInclusiveImpl(pParent);
  }

  public XsEMinLength newXsEMinLength(XsObject pParent) {
    return new XsEMinLengthImpl(pParent);
  }

  public XsENotation newXsENotation(XsObject pParent) {
    return new XsENotationImpl(pParent);
  }

  public XsEPattern newXsEPattern(XsObject pParent) {
    return new XsEPatternImpl(pParent);
  }

  public XsERedefine newXsERedefine(XsObject pParent) {
    return new XsERedefineImpl(pParent);
  }

  public XsERestriction newXsERestriction(XsObject pParent) {
    return new XsERestrictionImpl(pParent);
  }

  public XsESequence newXsESequence(XsObject pParent) {
    return new XsESequenceImpl(pParent);
  }

  public XsESelector newXsESelector(XsObject pParent) {
    return new XsESelectorImpl(pParent);
  }

  public XsESimpleContent newXsESimpleContent(XsObject pParent) {
    return new XsESimpleContentImpl(pParent);
  }

  public XsESchema newXsESchema() {
    return new XsESchemaImpl(getContext());
  }

  public XsETopLevelSimpleType newXsETopLevelSimpleType(XsObject pParent) {
    return new XsETopLevelSimpleTypeImpl(pParent);
  }

  public XsETotalDigits newXsETotalDigits(XsObject pParent) {
    return new XsETotalDigitsImpl(pParent);
  }

  public XsEUnion newXsEUnion(XsObject pParent) {
    return new XsEUnionImpl(pParent);
  }

  public XsEUnique newXsEUnique(XsObject pParent) {
    return new XsEUniqueImpl(pParent);
  }

  public XsEWhiteSpace newXsEWhiteSpace(XsObject pParent) {
    return new XsEWhiteSpaceImpl(pParent);
  }

  public XsGAllModel newXsGAllModel(XsObject pParent) {
    return new XsGAllModelImpl(pParent);
  }

  public XsGAttrDecls newXsGAttrDecls(XsObject pOwner) {
    return new XsGAttrDeclsImpl(pOwner);
  }

  public XsGIdentityConstraint newXsGIdentityConstraint(XsObject pOwner) {
    return new XsGIdentityConstraintImpl(pOwner);
  }

  public XsGParticle newXsGParticle(XsObject pOwner) {
    return new XsGParticleImpl(pOwner);
  }

  public XsGComplexTypeModel newXsGComplexTypeModel(XsObject pOwner) {
    return new XsGComplexTypeModelImpl(pOwner);
  }

  public XsGSimpleRestrictionModel newXsGSimpleRestrictionModel(XsObject pOwner) {
    return new XsGSimpleRestrictionModelImpl(pOwner);
  }

  public XsGTypeDefParticle newXsGTypeDefParticle(XsObject pOwner) {
    return new XsGTypeDefParticleImpl(pOwner);
  }

  public XsTAll newXsTAll(XsObject pParent) {
    return new XsTAllImpl(pParent);
  }

  public XsTAnnotated newXsTAnnotated(XsObject pParent) {
    return new XsTAnnotatedImpl(pParent);
  }

  public XsTAttribute newXsTAttribute(XsObject pParent) {
    return new XsTAttributeImpl(pParent);
  }

  public XsTAttributeGroup newXsTAttributeGroup(XsObject pParent) {
    return new XsTAttributeGroupImpl(pParent);
  }

  public XsTAttributeGroupRef newXsTAttributeGroupRef(XsObject pParent) {
    return new XsTAttributeGroupRefImpl(pParent);
  }

  public XsTComplexRestrictionType newXsTComplexRestrictionType(XsObject pParent) {
    return new XsTComplexRestrictionTypeImpl(pParent);
  }

  public XsTComplexType newXsTComplexType(XsObject pParent) {
    return new XsTComplexTypeImpl(pParent);
  }

  public XsTExtensionType newXsTExtensionType(XsObject pParent) {
    return new XsTExtensionTypeImpl(pParent);
  }

  public XsTLocalElement newXsTLocalElement(XsObject pParent) {
    return new XsTLocalElementImpl(pParent);
  }

  /** <p>Creates a new instance of {@link XsTLocalElement}, which
   * is located inside of an <code>xs:all</code> group. The
   * element ensures that its <code>minOccurs</code> and
   * <code>maxOccurs</code> values are 0 or 1.</p>
   */
  public XsTLocalElement newXsTLocalAllElement(XsObject pParent) {
    return new XsTLocalAllElementImpl(pParent);
  }

  public XsTLocalComplexType newXsTLocalComplexType(XsObject pParent) {
    return new XsTLocalComplexTypeImpl(pParent);
  }

  public XsTLocalSimpleType newXsTLocalSimpleType(XsObject pParent) {
    return new XsTLocalSimpleTypeImpl(pParent);
  }

  public XsTNamedGroup newXsTNamedGroup(XsObject pParent) {
    return new XsTNamedGroupImpl(pParent);
  }

  public XsTGroup newXsTGroup(XsObject pParent) {
    return new XsTGroupImpl(pParent);
  }

  public XsTGroupRef newXsTGroupRef(XsObject pParent) {
    return new XsTGroupRefImpl(pParent);
  }

  public XsTSimpleExplicitGroup newXsTSimpleExplicitGroup(XsObject pParent) {
    return new XsTSimpleExplicitGroupImpl(pParent);
  }

  public XsTSimpleExtensionType newXsTSimpleExtensionType(XsObject pParent) {
    return new XsTSimpleExtensionTypeImpl(pParent);
  }

  public XsTSimpleRestrictionType newXsTSimpleRestrictionType(XsObject pParent) {
    return new XsTSimpleRestrictionTypeImpl(pParent);
  }

  public XsTTopLevelElement newXsTTopLevelElement(XsObject pParent) {
    return new XsTTopLevelElementImpl(pParent);
  }

  public XsTWildcard newXsTWildcard(XsObject pParent) {
    return new XsTWildcardImpl(pParent);
  }

  public XMLReader newXMLReader(boolean pValidating) throws ParserConfigurationException, SAXException {
    SAXParserFactory spf = SAXParserFactory.newInstance();
    spf.setNamespaceAware(true);
    spf.setValidating(pValidating);
    SAXParser sp = spf.newSAXParser();
    if (pValidating) {
      sp.setProperty(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
    }
    XMLReader xr = sp.getXMLReader();
    xr.setErrorHandler(new ErrorHandler(){
      public void error(SAXParseException e) throws SAXException { throw e; }
      public void fatalError(SAXParseException fe) throws SAXException { throw fe; }
      public void warning(SAXParseException w) throws SAXException { throw w; }
    });
    return xr;
  }

  public XsSAXParser newXsSAXParser() {
    return newXsSAXParser(newXsESchema());
  }

  public XsSAXParser newXsSAXParser(Object pBean) {
    return new AbstractXsSAXParser(pBean){
      public XSContext getData(){
        return XSParser.getRunningInstance().getContext();
      }
    };
  }
}
