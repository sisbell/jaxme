/*
 * Copyright 2004  The Apache Software Foundation
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
package org.apache.ws.jaxme.xs.util;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.apache.ws.jaxme.xs.XSSchema;
import org.apache.ws.jaxme.xs.impl.XSLogicalParser;
import org.apache.ws.jaxme.xs.parser.XSContext;
import org.apache.ws.jaxme.xs.parser.impl.XSContextImpl;
import org.apache.ws.jaxme.xs.types.XSAnyType;
import org.apache.ws.jaxme.xs.types.XSEntities;
import org.apache.ws.jaxme.xs.types.XSEntity;
import org.apache.ws.jaxme.xs.types.XSID;
import org.apache.ws.jaxme.xs.types.XSIDREF;
import org.apache.ws.jaxme.xs.types.XSIDREFs;
import org.apache.ws.jaxme.xs.types.XSNMToken;
import org.apache.ws.jaxme.xs.types.XSNMTokens;
import org.apache.ws.jaxme.xs.types.XSNotation;
import org.apache.ws.jaxme.xs.types.XSString;
import org.apache.ws.jaxme.xs.xml.XsAGOccurs;
import org.apache.ws.jaxme.xs.xml.XsAnyURI;
import org.apache.ws.jaxme.xs.xml.XsEChoice;
import org.apache.ws.jaxme.xs.xml.XsEComplexContent;
import org.apache.ws.jaxme.xs.xml.XsEEnumeration;
import org.apache.ws.jaxme.xs.xml.XsERestriction;
import org.apache.ws.jaxme.xs.xml.XsESchema;
import org.apache.ws.jaxme.xs.xml.XsESimpleContent;
import org.apache.ws.jaxme.xs.xml.XsGAttrDecls;
import org.apache.ws.jaxme.xs.xml.XsNCName;
import org.apache.ws.jaxme.xs.xml.XsQName;
import org.apache.ws.jaxme.xs.xml.XsTAttribute;
import org.apache.ws.jaxme.xs.xml.XsTComplexType;
import org.apache.ws.jaxme.xs.xml.XsTExplicitGroup;
import org.apache.ws.jaxme.xs.xml.XsTExtensionType;
import org.apache.ws.jaxme.xs.xml.XsTLocalComplexType;
import org.apache.ws.jaxme.xs.xml.XsTLocalElement;
import org.apache.ws.jaxme.xs.xml.XsTLocalSimpleType;
import org.apache.ws.jaxme.xs.xml.XsTSimpleExtensionType;
import org.apache.ws.jaxme.xs.xml.XsTTopLevelElement;
import org.apache.ws.jaxme.xs.xml.impl.XsESchemaImpl;
import org.apache.ws.jaxme.xs.xml.impl.XsObjectFactoryImpl;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.LocatorImpl;


/** A SAX parser converting a DTD into an instance of XML Schema.
 */
public class DTDParser extends XSLogicalParser {
	/** This class is used to collect the attributes in the
     * DTD temporarily.
	 */
    public class DTDAttribute {
    	private final String name, type, mode, value;
        private final Locator loc;
    	/** Sets the attributes name.
    	 */
        public DTDAttribute(String pName, String pType, String pMode, String pValue) {
        	name = pName;
            type = pType;
            mode = pMode;
            value = pValue;
            Locator l = DTDParser.this.getLocator();
            if (l == null) {
            	loc = null;
            } else {
            	loc = new LocatorImpl(l);
            }
        }
        /** Returns the attributes name.
         */
        public String getName() {
        	return name;
        }
        /** Returns the attributes type.
         */
        public String getType() {
            return type;
        }
        /** Returns the attributes mode.
         */
        public String getMode() {
            return mode;
        }
        /** Returns the attributes value.
         */
        public String getValue() {
            return value;
        }
        /** Returns the attributes locator.
         */
        public Locator getLocator() {
        	return loc;
        }
    }

    /** This class is used to collect the elements in the
     * DTD temporarily.
     */
    public class DTDElement {
    	private final String name;
        private Locator loc;
        private String model;
        private Map attributes = new HashMap();
        /** Creates a new element declaration with the given name.
         */
        public DTDElement(String pName) {
        	name = pName;
        }
        /** Sets the elements content model.
         */
        public void setModel(String pModel) {
            Locator l = DTDParser.this.getLocator();
        	if (l == null) {
        		loc = null;
            } else {
            	loc = new LocatorImpl(l);
            }
            model = pModel;
        }
        /** Returns the elements content model.
         */
        public String getModel() {
        	return model;
        }
        /** Returns the elements name.
         */
        public String getName() {
        	return name;
        }
        /** Adds a new attribute to the element.
         */
        public void addAttribute(DTDAttribute pAttribute) throws SAXException {
        	if (attributes.put(pAttribute.getName(), pAttribute) != null) {
        		throw new SAXParseException("Duplicate attribute " + pAttribute.getName()
                                            + " in element " + getName(),
                                            pAttribute.getLocator());
            }
        }
        /** Returns the elements attributes.
         */
        public DTDAttribute[] getAttributes() {
        	return (DTDAttribute[]) attributes.values().toArray(new DTDAttribute[attributes.size()]);
        }
        /** Returns the elements locator.
         */
        public Locator getLocator() {
        	return loc;
        }
    }

    protected static class ChildToken {
        /** Type of a NAME token; see
         * {@link DTDParser#parseChildren(XsTTopLevelElement, String, Locator)}
         * for the definition of SEQUENCE.
         */
        public final static int SEQUENCE = 1;
        /** Type of a NAME token; see
         * {@link DTDParser#parseChildren(XsTTopLevelElement, String, Locator)}
         * for the definition of CHOICE.
         */
        public final static int CHOICE = 2;
        private final int type;
        private final List tokens = new ArrayList();
        private final String multiplicity;
        protected ChildToken(int pType, String pMultiplicity) {
        	type = pType;
            multiplicity = pMultiplicity;
        }
        /** Returns the token type; either of
         * {@link #SEQUENCE}, or {@link #CHOICE}.
         */
        public int getType() { return type; }
        /** Adds a token to the list of tokens.
         */
        public void add(ChildToken pToken) {
        	tokens.add(pToken);
        }
        /** Adds a name to the list of tokens.
         */
        public void add(String pName) {
        	tokens.add(pName);
        }
        /** Returns the tokens childs.
         */
        public Object[] getChilds() {
        	return tokens.toArray();
        }
        /** Returns the tokens multiplicity.
         */
        public String getMultiplicity() {
        	return multiplicity;
        }
    }
    
    /** This class is similar to a StringReader, except that
     * it allows to extend the input dynamically.
     */
    public static class StringBufferReader extends Reader {
    	private final StringBuffer sb = new StringBuffer();

    	/** Appends the given string to the input.
    	 */
        public void append(String pString) {
        	sb.append(pString);
        }

        /** Invoked, if the internal buffer is empty.
         * Subclasses may override this to query for more
         * input.
         */
        public String requestInput() {
            return null;
        }

        public int read(char[] pBuffer, int pOffset, int pLen) throws IOException {
        	if (sb.length() == 0) {
        		String s = requestInput();
                if (s != null  &&  s.length() > 0) {
                	append(s);
                }
                if (sb.length() == 0) {
                	return -1;
                }
            }
        	if (pLen >= sb.length()) {
        		pLen = sb.length();
        	}
        	for (int i = 0;  i < pLen;  i++) {
        		pBuffer[pOffset+i] = sb.charAt(i);
        	}
        	sb.delete(0, pLen);
        	return pLen;
        }

		public void close() throws IOException {
		}        
    }

    /** Implementation of a {@link DeclHandler} for reading
     * the element and attribute declarations.
     */
    public class DtdDeclHandler extends DefaultHandler implements DeclHandler {
        public void setDocumentLocator(Locator pLocator) {
        	setLocator(pLocator);
        }

        public void elementDecl(String pName, String pModel) throws SAXException {
            DTDElement element = (DTDElement) elements.get(pName);
            if (element == null) {
                element = new DTDElement(pName);
            	elements.put(pName, element);
            } else {
            	if (element.getModel() != null) {
            		throw new SAXParseException("Element " + pName
                                                + " declared twice", getLocator());
                }
            }
            element.setModel(pModel);
		}

		public void attributeDecl(String pElementName, String pAttributeName,
                                  String pType, String pMode, String pValue) throws SAXException {
            DTDElement element = (DTDElement) elements.get(pElementName);
            if (element == null) {
            	element = new DTDElement(pElementName);
                elements.put(pElementName, element);
            }
            DTDAttribute attr = new DTDAttribute(pAttributeName, pType, pMode, pValue);
            element.addAttribute(attr);
		}

		public void internalEntityDecl(String pName, String pValue) throws SAXException {
        }

		public void externalEntityDecl(String pName, String publicId, String pSystemId) throws SAXException {
		}
    }

    private Locator locator;
    private final Map elements = new HashMap();
    private String dummyElementName;
    private XsAnyURI targetNamespace;
    private XSContext context;

    public XSContext getData() {
    	return context;
    }

    /** Returns the document locator.
     */
    public Locator getLocator() {
    	return locator;
    }

    /** Sets the document locator.
     */
    public void setLocator(Locator pLocator) {
    	locator = pLocator;
        XSContext context = getData();
        if (context != null) {
        	context.setLocator(pLocator);
        }
    }

    protected String getDummyElementName() {
    	if (dummyElementName == null) {
            for (int i = 0;  ;  i++) {
            	String name = "dummyElement" + i;
                if (!elements.containsKey(name)) {
                	dummyElementName = name;
                    break;
                }
            }
        }
        return dummyElementName;
    }

    /** Parses the given DTD, filling the parsers
     * temporary map of elements.
     */
    protected void runXMLReader(final InputSource pSource)
            throws ParserConfigurationException, IOException, SAXException {
    	/* We cannot parse the DTD directly. Instead, we create
         * a dummy XML document, which references the DTD as
         * an external entity, and parse the XML document.
         */
        String s = "<!DOCTYPE a SYSTEM 'uri:dtd'><a/>";
        InputSource isource = new InputSource(new StringReader(s));
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setValidating(false);
        XMLReader xr = spf.newSAXParser().getXMLReader();
        xr.setEntityResolver(new EntityResolver(){
			public InputSource resolveEntity(String publicId, String pSystemId) throws SAXException, IOException {
				return "uri:dtd".equals(pSystemId) ? pSource : null;
            }
        });
        DtdDeclHandler handler = new DtdDeclHandler();
        xr.setContentHandler(handler);
        xr.setProperty("http://xml.org/sax/properties/declaration-handler", handler);
        xr.parse(isource);
    }

    private ChildToken addToChildToken(ChildToken pToken, String pTokenValue,
                                       String pMultiplicity, int pType,
									   Locator pLocator)
            throws SAXException {
        if ("".equals(pTokenValue)) {
        	throw new SAXParseException("Expected name, choice, or sequence, found empty string", pLocator);
        }
        if (pToken == null) {
        	pToken = new ChildToken(pType, pMultiplicity);
        } else {
        	if (pType != pToken.getType()) {
        		throw new SAXParseException("Mixed use of ',' and '|' in a choice or sequence", pLocator);
            }
        }
        if (pTokenValue.startsWith("(")) {
            pToken.add(parseChildren(pTokenValue, pLocator));
        } else {
            pToken.add(pTokenValue);
        }
        return pToken;
    }

    /** Returns a tokens multiplicity.
     */
    protected String getMultiplicity(String pToken) {
    	if (pToken.endsWith("*")) {
    		return "*";
        } else if (pToken.endsWith("?")) {
        	return "?";
        } else if (pToken.endsWith("+")) {
        	return "+";
        } else {
        	return "";
        }
    }

    /** Converts a list of children into its tokens.
     */
    protected ChildToken parseChildren(String pModel, Locator pLocator)
            throws SAXException {
    	String model = pModel;
        if (model.startsWith("(")) {
        	model = model.substring(1).trim();
        } else {
        	throw new SAXParseException("A choice or sequence must start with '('",
                                        pLocator);
        }
        String multiplicity = getMultiplicity(model);
        model = model.substring(0, model.length() - multiplicity.length()).trim();
        if (model.endsWith(")")) {
        	model = model.substring(0, model.length()-1);
        } else {
        	throw new SAXParseException("A choice or sequence must end with ')', ')?', ')*', or ')+'",
                                        pLocator);
        }
        ChildToken ct = null;
        int level = 0;
        int offset = 0;
        for (int i = 0;  i < model.length();  i++) {
        	char c = model.charAt(i);
            switch (c) {
                case '(': ++level; break;
                case ')': --level; break;
                case '|':
                case ',':
                    if (level == 0) {
                    	String t = model.substring(offset, i).trim();
                        ct = addToChildToken(ct, t, multiplicity,
                                             (c == '|' ? ChildToken.CHOICE : ChildToken.SEQUENCE),
											 pLocator);
                        offset = i+1;
                    }
            }
        }
        String t = model.substring(offset).trim();
        return addToChildToken(ct, t, multiplicity,
        		               ct == null ? ChildToken.SEQUENCE : ct.getType(),
        		               pLocator);
    }

    /** Sets the objects multiplicity.
     */
    protected void setMultiplicity(XsAGOccurs pOccurs, String pMultiplicity) {
        if ("?".equals(pMultiplicity)) {
            pOccurs.setMinOccurs(0);
        } else if ("*".equals(pMultiplicity)) {
            pOccurs.setMinOccurs(0);
            pOccurs.setMaxOccurs("unbounded");
        } else if ("+".equals(pMultiplicity)) {
            pOccurs.setMaxOccurs("unbounded");
        } else if (!"".equals(pMultiplicity)) {
            throw new IllegalArgumentException("Invalid multiplicity: " + pMultiplicity);
        }
    }

    /** Adds the childs to a group.
     */
    protected void addChildren(XsTTopLevelElement pElement,
                               XsTExplicitGroup pGroup, ChildToken pToken,
                               Locator pLocator)
            throws SAXException {
        setMultiplicity(pGroup, pToken.getMultiplicity());
        Object[] tokens = pToken.getChilds();
        for (int i = 0;  i < tokens.length;  i++) {
        	Object o = tokens[i];
            if (o instanceof String) {
        		String name = (String) o;
                String multiplicity = getMultiplicity(name);
                name = name.substring(0, name.length()-multiplicity.length()).trim();
                if (!elements.containsKey(name)) {
                	throw new SAXParseException("Element " + pElement.getName()
                                                + " references an undeclared element " + name,
                                                pLocator);
                }
                XsTLocalElement e = pGroup.createElement();
                e.setRef(new XsQName(getTargetNamespace(), getLocalPart(name)));
                setMultiplicity(e, multiplicity);
            } else if (o instanceof ChildToken) {
                ChildToken ct = (ChildToken) o;
                XsTExplicitGroup group;
                if (ct.type == ChildToken.SEQUENCE) {
                	group = pGroup.createSequence();
                } else {
                	group = pGroup.createChoice();
                }
                addChildren(pElement, group, ct, pLocator);
            } else {
            	throw new IllegalStateException("Unknown token type: " + tokens[i].getClass().getName());
            }
        }
    }

    /** Parses a content model with children. This content
     * model is specified as follows:
     * <pre>
     *   children ::= (choice | seq) ('?' | '*' | '+')?
     *   cp ::= (Name | choice | seq) ('?' | '*' | '+')?
     *   choice ::= '(' S? cp ( S? '|' S? cp )+ S? ')'
     *   seq ::= '(' S? cp ( S? ',' S? cp )* S? ')'
     * </pre>
     */
    protected XsGAttrDecls parseChildren(XsTTopLevelElement pElement, String pModel,
                                 Locator pLocator) throws SAXException {
        ChildToken ct = parseChildren(pModel, pLocator);
        XsTLocalComplexType complexType = pElement.createComplexType();
        XsTExplicitGroup group;
        if (ct.getType() == ChildToken.SEQUENCE) {
        	group = complexType.createSequence();
        } else {
            group = complexType.createChoice();
        }
        addChildren(pElement, group, ct, pLocator);
        return complexType;
    }

    /** Parses a mixed content model. The mixed content model
     * is specified as follows:
     * <pre>
     *   Mixed ::= '(' S? '#PCDATA' (S? '|' S? Name)* S? ')*'
     *       | '(' S? '#PCDATA' S? ')' 
     * </pre>
     */
    protected XsGAttrDecls parseMixed(XsTTopLevelElement pElement, String pModel,
                                      Locator pLocator, boolean pHasAttributes)
            throws SAXException {
        if (!pModel.startsWith("(")) {
        	throw new SAXParseException("Mixed content model must start with '(#PCDATA'",
                                        pLocator);
        }
        pModel = pModel.substring(1).trim();
        if (!pModel.startsWith("#PCDATA")) {
            throw new SAXParseException("Mixed content model must start with '(#PCDATA'",
                    pLocator);
        }
        pModel = pModel.substring("#PCDATA".length()).trim();
        boolean unbounded;
        if (pModel.endsWith("*")) {
            pModel = pModel.substring(0, pModel.length()-1).trim();
            unbounded = true;
        } else {
        	unbounded = false;
        }
        if (!pModel.endsWith(")")) {
            throw new SAXParseException("Mixed content model must end with ')' or ')*'",
            		                    pLocator);
        }
        pModel = pModel.substring(0, pModel.length()-1);
        if ("".equals(pModel)) {
            XsQName qName = XSString.getInstance().getName();
            qName = new XsQName(qName.getNamespaceURI(), qName.getLocalName(), "xs");
            if (pHasAttributes) {
                XsTLocalComplexType complexType = pElement.createComplexType();
                XsESimpleContent simpleContent = complexType.createSimpleContent();
                XsTSimpleExtensionType ext = simpleContent.createExtension();
                ext.setBase(qName);
                return ext;
            } else {
            	pElement.setType(qName);
                return null;
            }
        } else if (!unbounded) {
        	throw new SAXParseException("Mixed content must be either #PCDATA or have multiplicity '*'",
                                        pLocator);
        } else {
        	XsTLocalComplexType complexType = pElement.createComplexType();
            complexType.setMixed(true);
            XsEChoice choice = complexType.createChoice();
            choice.setMinOccurs(0);
            choice.setMaxOccurs("unbounded");
            while (!"".equals(pModel)) {
            	if (pModel.startsWith("|")) {
            		pModel = pModel.substring(1).trim();
                } else {
                	throw new SAXParseException("Expected '|' while parsing mixed content", pLocator);
                }
                int offset = pModel.indexOf('|');
                String name;
                if (offset == -1) {
                    name = pModel.trim();
                    pModel = "";
                } else {
                	name = pModel.substring(0, offset).trim();
                    pModel = pModel.substring(offset);
                }
                if (elements.containsKey(name)) {
                	XsTLocalElement e = choice.createElement();
                    e.setRef(new XsQName(getTargetNamespace(), getLocalPart(name)));
                } else {
                	throw new SAXParseException("Element " + pElement.getName()
                                                + " references element " + name
                                                + ", which is not declared",
                                                pLocator);
                }
            }
            return complexType;
        }
    }

    /** Creates a new attribute.
     */
    protected void createAttribute(XsGAttrDecls pAttrDecls, DTDAttribute pAttribute)
            throws SAXException {
        XsTAttribute attr = pAttrDecls.createAttribute();
        attr.setName(new XsNCName(getLocalPart(pAttribute.getName())));
        String type = pAttribute.getType();
        XsQName qName;
        if ("CDATA".equals(type)) {
        	qName = XSString.getInstance().getName();
        } else if ("ID".equals(type)) {
        	qName = XSID.getInstance().getName();
        } else if ("IDREF".equals(type)) {
            qName = XSIDREF.getInstance().getName();
        } else if ("IDREFS".equals(type)) {
            qName = XSIDREFs.getInstance().getName();
        } else if ("ENTITY".equals(type)) {
            qName = XSEntity.getInstance().getName();
        } else if ("ENTITIES".equals(type)) {
            qName = XSEntities.getInstance().getName();
        } else if ("NMTOKEN".equals(type)) {
            qName = XSNMToken.getInstance().getName();
        } else if ("NMTOKENS".equals(type)) {
            qName = XSNMTokens.getInstance().getName();
        } else {
            if (type.startsWith("NOTATION")  &&
                Character.isWhitespace(type.charAt("NOTATION".length()))) {
            	qName = XSNotation.getInstance().getName();
            } else {
            	qName = XSNMToken.getInstance().getName();
            }
            XsTLocalSimpleType simpleType = attr.createSimpleType();
            XsERestriction restriction = simpleType.createRestriction();
            restriction.setBase(new XsQName(qName.getNamespaceURI(), qName.getLocalName(), "xs"));
            if (type.startsWith("(")) {
            	type = type.substring(1).trim();
            } else {
            	throw new SAXParseException("The enumeration in the type of attribute "
                                            + pAttribute.getName()
                                            + " must begin with an '('.",
                                            pAttribute.getLocator());
            }
            if (type.endsWith(")")) {
                type = type.substring(0, type.length()-1).trim();
            } else {
                throw new SAXParseException("The enumeration in the type of attribute "
                                            + pAttribute.getName()
                                            + " must begin with an '('.",
                                            pAttribute.getLocator());
            }
            StringTokenizer st = new StringTokenizer(type, "|");
            if (!st.hasMoreTokens()) {
            	throw new SAXParseException("The enumeration in the type of attribute "
                                            + pAttribute.getName()
                                            + " contains no tokens.",
                                            pAttribute.getLocator());
            }
            while (st.hasMoreTokens()) {
                String token = st.nextToken().trim();
                if ("".equals(token)) {
                	throw new SAXParseException("The enumeration in the type of attribute "
                                                + pAttribute.getName()
                                                + " contains an empty token.",
                                                pAttribute.getLocator());
                }
                XsEEnumeration enumeration = restriction.createEnumeration();
                enumeration.setValue(token);
            }
            qName = null;
        }
        if (qName != null) {
            attr.setType(new XsQName(qName.getNamespaceURI(), qName.getLocalName(), "xs"));
        }
    }

    private String getLocalPart(String pName) {
        int offset = pName.indexOf(':');
        if (offset >= 0) {
            return pName.substring(offset+1);
        } else {
        	return pName;
        }
    }

    /** Creates an element named <code>pName</code> with the
     * content model <code>pModel</code> and the attribute
     * list <code>pAttrs</code> in the schema <code>pSchema</code>.
     */
    protected XsTTopLevelElement createElement(XsESchema pSchema, String pName,
                                               String pModel,
                                               DTDAttribute[] pAttributes,
                                               Locator pLocator)
            throws SAXException {
        XsTTopLevelElement result = pSchema.createElement();
        result.setName(new XsNCName(getLocalPart(pName)));
        final XsGAttrDecls attrDecls;
        if ("EMPTY".equals(pModel)) {
        	attrDecls = result.createComplexType();
        } else if ("ANY".equals(pModel)) {
            XsQName qName = XSAnyType.getInstance().getName();
            qName = new XsQName(qName.getNamespaceURI(), qName.getLocalName(), "xs");
        	if (pAttributes.length == 0) {
                result.setType(qName);
                attrDecls = null;
            } else {
                XsTComplexType complexType = result.createComplexType();
                XsEComplexContent complexContent = complexType.createComplexContent();
                XsTExtensionType extensionType = complexContent.createExtension();
                extensionType.setBase(qName);
                attrDecls = extensionType;
            }
        } else if (pModel.startsWith("(")) {
            String pcData = pModel.substring(1).trim();
            if (pcData.startsWith("#PCDATA")) {
            	attrDecls = parseMixed(result, pModel, pLocator, pAttributes.length > 0);
            } else {
            	attrDecls = parseChildren(result, pModel, pLocator);
            }
        } else {
        	throw new SAXParseException("Invalid content model in element " + pName
                                        + ", expected EMPTY|ANY|(...",
                                        pLocator);
        }
        for (int i = 0;  i < pAttributes.length;  i++) {
        	createAttribute(attrDecls, pAttributes[i]);
        }
        return result;
    }

    /** Parses the given {@link org.xml.sax.InputSource} and
     * converts it into an instance of
     * {@link org.apache.ws.jaxme.xs.xml.XsESchema}.
     */
    protected void parse(XsESchema pSchema, InputSource pSource)
            throws ParserConfigurationException, IOException, SAXException {
    	runXMLReader(pSource);
        for (Iterator iter = elements.values().iterator();  iter.hasNext();  ) {
        	DTDElement element = (DTDElement) iter.next();
        	String name = element.getName();
            String model = element.getModel();
            DTDAttribute[] attrs = element.getAttributes();
            if (attrs.length > 0  &&  model == null) {
            	throw new SAXParseException("The element " + name
                                            + " is referred by attribute "
                                            + attrs[0].getName()
                                            + ", but never declared.",
                                            attrs[0].getLocator());
            }
            createElement(pSchema, name, model, element.getAttributes(),
                          element.getLocator());
        }
    }

    public XSSchema parse(InputSource pInputSource)
            throws ParserConfigurationException, IOException, SAXException {
    	XsObjectFactoryImpl xsObjectFactory = new XsObjectFactoryImpl(){
    		public XSContext getContext() {
    			return getData();
            }
        };
        context = new XSContextImpl();
        context.setXSLogicalParser(this);
        context.setXsObjectFactory(xsObjectFactory);
        clearSyntaxSchemas();
        XsESchema syntaxSchema = new XsESchemaImpl(context){
        };
        parse(syntaxSchema, pInputSource);
        XSSchema schema = context.getXSObjectFactory().newXSSchema(context, syntaxSchema);
        setSchema(schema);
		parse(syntaxSchema, pInputSource.getSystemId());
		schema.validate();
        return schema;
    }

    /** Sets the created schemas target namespace.
     */
    public void setTargetNamespace(XsAnyURI pTargetNamespace) {
    	targetNamespace = pTargetNamespace;
    }

    /** Returns the created schemas target namespace.
     */
    public XsAnyURI getTargetNamespace() {
        return targetNamespace;
    }
}