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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.ws.jaxme.xs.SchemaTransformer;
import org.apache.ws.jaxme.xs.XSContentHandler;
import org.apache.ws.jaxme.xs.XSElement;
import org.apache.ws.jaxme.xs.XSObjectFactory;
import org.apache.ws.jaxme.xs.XSParser;
import org.apache.ws.jaxme.xs.XSSchema;
import org.apache.ws.jaxme.xs.parser.XSContext;
import org.apache.ws.jaxme.xs.parser.XsSAXParser;
import org.apache.ws.jaxme.xs.parser.impl.LocSAXException;
import org.apache.ws.jaxme.xs.util.LoggingContentHandler;
import org.apache.ws.jaxme.xs.xml.XsAnyURI;
import org.apache.ws.jaxme.xs.xml.XsEAnnotation;
import org.apache.ws.jaxme.xs.xml.XsEImport;
import org.apache.ws.jaxme.xs.xml.XsEInclude;
import org.apache.ws.jaxme.xs.xml.XsENotation;
import org.apache.ws.jaxme.xs.xml.XsERedefine;
import org.apache.ws.jaxme.xs.xml.XsESchema;
import org.apache.ws.jaxme.xs.xml.XsETopLevelSimpleType;
import org.apache.ws.jaxme.xs.xml.XsNCName;
import org.apache.ws.jaxme.xs.xml.XsObject;
import org.apache.ws.jaxme.xs.xml.XsObjectFactory;
import org.apache.ws.jaxme.xs.xml.XsQName;
import org.apache.ws.jaxme.xs.xml.XsRedefinable;
import org.apache.ws.jaxme.xs.xml.XsTAttribute;
import org.apache.ws.jaxme.xs.xml.XsTAttributeGroup;
import org.apache.ws.jaxme.xs.xml.XsTComplexType;
import org.apache.ws.jaxme.xs.xml.XsTLocalElement;
import org.apache.ws.jaxme.xs.xml.XsTNamedGroup;
import org.apache.ws.jaxme.xs.xml.XsTSimpleExplicitGroup;
import org.apache.ws.jaxme.xs.xml.XsTTopLevelElement;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.LocatorImpl;


/** <p>Implementation of a logical parser.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XSLogicalParser {
	/** This class is used to ensure, that schemata aren't loaded
	 * twice. It can also be used for preloading schemata.
	 */
	public static class AddedImport {
		private final String targetNamespace, schemaLocation;
		private final Node node;
		/** Creates a new instance with the given target namespace and
		 * schema location.
		 * @param pTargetNamespace The schemas target namespace.
		 * @param pSchemaLocation The schemas location.
		 */
		public AddedImport(XsAnyURI pTargetNamespace, String pSchemaLocation) {
			this(pTargetNamespace == null ? "" : pTargetNamespace.toString(), pSchemaLocation);
		}
		/** Creates a new instance with the given target namespace and
		 * schema location.
		 * @param pTargetNamespace The schemas target namespace.
		 * @param pSchemaLocation The schemas location.
		 */
		public AddedImport(String pTargetNamespace, String pSchemaLocation) {
			this(pTargetNamespace, pSchemaLocation, null);
		}
		/** Creates a new instance with the given target namespace and
		 * schema location. The schema isn't parsed from the location.
		 * Instead, the supplied nodes contents should be used as a
		 * schema.
		 * @param pTargetNamespace The schemas target namespace.
		 * @param pSchemaLocation The schemas location.
		 * @param pNode The schemas contents as a DOM node.
		 */
		public AddedImport(String pTargetNamespace, String pSchemaLocation, Node pNode) {
			targetNamespace = pTargetNamespace == null ? "" : pTargetNamespace.toString();
			if (pSchemaLocation == null) {
				throw new IllegalStateException("The schemaLocation must not be null.");
			}
			schemaLocation = pSchemaLocation;
			node = pNode;
		}
		public boolean equals(Object pOther) {
			if (pOther instanceof AddedImport) {
				AddedImport other = (AddedImport) pOther;
				return targetNamespace.equals(other.targetNamespace) &&
				schemaLocation.equals(other.schemaLocation);
			} else {
				return false;
			}
		}
		public int hashCode() {
			return targetNamespace.hashCode() + schemaLocation.hashCode();
		}
		
		/** Returns the imported schemas target namespace.
		 */
		public String getNamespace() {
			return targetNamespace;
		}
		
		/** Returns the URL, from which the schema is being loaded.
		 * Returns null, if the schema is loaded from a DOM node.
		 */
		public String getSchemaLocation() {
			return schemaLocation;
		}
		
		/** Returns the DOM node, from which the schema is being loaded.
		 * Returns null, if the schema is loaded from an URL.
		 */
		public Node getNode() {
			return node;
		}
	}
	
	private boolean notValidating;
	private List syntaxSchemas = new ArrayList();
	private XsESchema[] syntaxSchemaArray;
	private XSSchema schema;
	private Set parsedSchemas;
	private List addedImports = new ArrayList();
	
	protected XSContext getData() {
		return XSParser.getRunningInstance().getContext();
	}
	
	/** <p>Sets whether the parser is validating.</p>
	 */
	public void setValidating(boolean pValidating) {
		notValidating = !pValidating;
	}
	
	/** <p>Returns whether the parser is validating.</p>
	 */
	public boolean isValidating() {
		return !notValidating;
	}
	
	/** <p>Adds a schema being imported by the parser. This feature
	 * is useful, if a schema silently assumes the presence of additional
	 * datatypes. For example, a WSDL definition will contain references
	 * to SOAP datatypes without explicit import.</p>
	 * <p>In practice, the declaration will silently create an
	 * "xs:import" node.</p>
	 * @param pNamespace Matches the "xs:import" nodes "namespace" attribute.
	 *   In particular it may be null, in which case the imported schema may
	 *   not have a targetNamespace.
	 * @param pSchemaLocation Matches the "xs:import" nodes "schemaLocation"
	 *   attribute. In particular it may be null.
	 */
	public void addImport(String pNamespace, String pSchemaLocation) {
		addedImports.add(new AddedImport(pNamespace, pSchemaLocation));
	}
	
	/** <p>Adds a schema being imported by the parser. The schema is
	 * provided as a DOM node. This feature is useful, if a schema
	 * silently assumes the presence of additional datatypes. For
	 * example, a WSDL definition will contain references to SOAP
	 * datatypes without explicit import.</p>
	 * @param pNamespace Matches the "xs:import" nodes "namespace"
	 *   attribute. In particular it may be null, in which case the
	 *   imported schema may not have a targetNamespace.
	 * @param pSchemaLocation System ID of the schema being imported,
	 * if known, or null. Knowing the system ID is important only,
	 * if you need to prevent recursive parsing of schemata.
	 * @param pSchema A DOM node with the schema being imported.
	 */
	public void addImport(String pNamespace, String pSchemaLocation, Node pSchema) {
		addedImports.add(new XSLogicalParser.AddedImport(pNamespace, pSchemaLocation, pSchema));
	}
	
	/** <p>Returns the array of added imports, typically empty.</p>
	 */
	public AddedImport[] getAddedImports() {
		return (AddedImport[]) addedImports.toArray(new AddedImport[addedImports.size()]);
	}
	
	/** <p>Returns the schema, which is currently being parsed.</p>
	 */
	public XSSchema getSchema() { return schema; }
	
	/** <p>Sets the schema, which is currently being parsed.</p>
	 */
	protected void setSchema(XSSchema pSchema) {
		schema = pSchema;
	}
	
	protected XsESchema parseSyntax(Node pNode) throws SAXException {
		XSContext data = getData();
		try {
			XsObjectFactory factory = data.getXsObjectFactory();
			XsESchema mySchema = factory.newXsESchema();
			XsSAXParser xsSAXParser = factory.newXsSAXParser(mySchema);
			addSyntaxSchema(mySchema);
			try {
				data.setCurrentContentHandler(xsSAXParser);
				DOMSerializer ds = new DOMSerializer();
				ds.serialize(pNode, xsSAXParser);
				return (XsESchema) xsSAXParser.getBean();
			} finally {
				removeSyntaxSchema();
			}
		} finally {
			data.setCurrentContentHandler(null);
		}
	}
	
	/** <p>Converts the given URI into an instance of InputSource.</p>
	 */
	protected InputSource getInputSource(String pReferencingSystemId, String pURI) throws SAXException {
		URL url = null;
		if (pReferencingSystemId != null) {
			// Try to create the new URI based on the old URI; may be its relative?
			try {
				url = new URL(new URL(pReferencingSystemId), pURI);
			} catch (MalformedURLException e) {
			}
			
			if (url == null) {
				try {
					url = new File(new File(pReferencingSystemId).getParentFile(), pURI).toURL();
				} catch (MalformedURLException e) {
				}
			}
		}
		
		if (url == null) {
			try {
				url = new URL(pURI);
			} catch (MalformedURLException e) {
				try {
					url = new File(pURI).toURL();
				} catch (MalformedURLException f) {
					throw new SAXException("Failed to parse the URI " + pURI);
				}
			}
		}
		
		try {
			InputSource isource = new InputSource(url.openStream());
			isource.setSystemId(url.toString());
			return isource;
		} catch (IOException e) {
			throw new SAXException("Failed to open the URL " + url, e);
		}
	}
	
	
	protected XsESchema parseSyntax(Locator pLocator, String pSchemaLocation)
	throws SAXException, IOException, ParserConfigurationException {
		XsESchema result = getData().getXsObjectFactory().newXsESchema();
		parseSyntax(pLocator, pSchemaLocation, result);
		return result;
	}

	private void runContentHandler(XMLReader pReader, ContentHandler pHandler, InputSource pSource)
			throws SAXException, IOException {
		String logDir = System.getProperty("org.apache.ws.jaxme.xs.logDir");
		FileOutputStream fos = null;
		try {
			SchemaTransformer transformer = getData().getXSObjectFactory().getSchemaTransformer();
			if (transformer != null) {
				transformer.parse(pSource, pReader);
				final InputSource newSource = transformer.getTransformedInputSource();
				newSource.setSystemId(pSource.getSystemId());
				newSource.setPublicId(pSource.getPublicId());
				pSource = newSource;
				pReader = transformer.getTransformedXMLReader();
			}
			if (logDir != null) {
				File tmpFile = File.createTempFile("jaxmexs", ".xsd", new File(logDir));
				fos = new FileOutputStream(tmpFile);
				LoggingContentHandler lch = new LoggingContentHandler(fos);
				lch.setParent(pReader);
				pReader = lch;
				String msg = "Read from " + pSource.getPublicId() + ", " + pSource.getSystemId() + " at " + new Date();
				lch.comment(msg.toCharArray(), 0, msg.length());
			}
			pReader.setContentHandler(pHandler);
			LocatorImpl loc = new LocatorImpl();
			loc.setSystemId(pSource.getSystemId());
			loc.setPublicId(pSource.getPublicId());
            loc.setLineNumber(-1);
            loc.setColumnNumber(-1);
			pHandler.setDocumentLocator(loc);
			pReader.parse(pSource);
			if (fos != null) {
				fos.close();
				fos = null;
			}
		} catch (ParserConfigurationException e) {
			throw new SAXException(e);
		} finally {
			if (fos != null) {
				try { fos.close(); } catch (Throwable ignore) {}
			}
		}
	}

	protected void parseSyntax(Locator pLocator, String pSchemaLocation,
			XsESchema pSchema)
	throws SAXException, IOException, ParserConfigurationException {
		XSContext data = getData();
		try {
			XsObjectFactory factory = data.getXsObjectFactory();
			XMLReader xr = factory.newXMLReader(isValidating());
			EntityResolver entityResolver = xr.getEntityResolver();
			InputSource schemaSource = null;
			if (entityResolver != null) {
				schemaSource = entityResolver.resolveEntity(null, pSchemaLocation);
			}
			if (schemaSource == null) {
				schemaSource = getInputSource(pLocator == null ? null : pLocator.getSystemId(),
						pSchemaLocation);
			}
			
			XsSAXParser xsSAXParser = factory.newXsSAXParser(pSchema);
			addSyntaxSchema(pSchema);
			try {
				data.setCurrentContentHandler(xsSAXParser);
				runContentHandler(xr, xsSAXParser, schemaSource);
			} finally {
				removeSyntaxSchema();
			}
		} finally {
			data.setCurrentContentHandler(null);
		}
	}
	
	/** <p>Redefines the given {@link XsRedefinable}.</p>
	 */
	protected void redefine(XsESchema pSyntaxSchema,
			XsERedefine pRedefine, XsRedefinable pChild) throws SAXException {
		XSSchema mySchema = getSchema();
		XSContext data = getData();
		XSObjectFactory factory = data.getXSObjectFactory();
		if (pChild instanceof XsTAttributeGroup) {
			XsTAttributeGroup attributeGroup = (XsTAttributeGroup) pChild;
			mySchema.redefine(factory.newXSAttributeGroup(mySchema, attributeGroup));
		} else if (pChild instanceof XsTNamedGroup) {
			XsTNamedGroup group = (XsTNamedGroup) pChild;
			mySchema.redefine(factory.newXSGroup(mySchema, group));
		} else if (pChild instanceof XsETopLevelSimpleType) {
			XsETopLevelSimpleType type = (XsETopLevelSimpleType) pChild;
			mySchema.redefine(factory.newXSType(mySchema, type));
		} else if (pChild instanceof XsTComplexType) {
			XsTComplexType type = (XsTComplexType) pChild;
			mySchema.redefine(factory.newXSType(mySchema, type));
		} else {
			Locator locator = (pChild instanceof XsObject) ? ((XsObject) pChild).getLocator() : pRedefine.getLocator();
			throw new LocSAXException("Unknown type for redefinition: " + pChild.getClass().getName() +
					", perhaps you should handle this in a subclass?", locator);
		}
	}
	
	
	
	/** <p>Adds the given object to the schema.</p>
	 * 
	 */
	protected void add(XsESchema pSyntaxSchema, Object pChild) throws SAXException {
		XSSchema mySchema = getSchema();
		XSContext data = getData();
		XSObjectFactory factory = data.getXSObjectFactory();
		if (pChild instanceof XsEAnnotation) {
			XsEAnnotation annotation = (XsEAnnotation) pChild;
			mySchema.add(factory.newXSAnnotation(mySchema, annotation));
		} else if (pChild instanceof XsETopLevelSimpleType) {
			XsETopLevelSimpleType type = (XsETopLevelSimpleType) pChild;
			mySchema.add(factory.newXSType(mySchema, type));
		} else if (pChild instanceof XsTComplexType) {
			XsTComplexType type = (XsTComplexType) pChild;
			mySchema.add(factory.newXSType(mySchema, type));
		} else if (pChild instanceof XsTNamedGroup) {
			XsTNamedGroup group = (XsTNamedGroup) pChild;
			mySchema.add(factory.newXSGroup(mySchema, group));
		} else if (pChild instanceof XsTAttributeGroup) {
			XsTAttributeGroup attributeGroup = (XsTAttributeGroup) pChild;
			mySchema.add(factory.newXSAttributeGroup(mySchema, attributeGroup));
		} else if (pChild instanceof XsTTopLevelElement) {
			XsTTopLevelElement element = (XsTTopLevelElement) pChild;
			mySchema.add(factory.newXSElement(mySchema, element));
		} else if (pChild instanceof XsTAttribute) {
			XsTAttribute attribute = (XsTAttribute) pChild;
			mySchema.add(factory.newXSAttribute(mySchema, attribute));
		} else if (pChild instanceof XsENotation) {
			XsENotation notation = (XsENotation) pChild;
			mySchema.add(factory.newXSNotation(mySchema, notation));
		} else {
			Locator locator = (pChild instanceof XsObject) ?
					((XsObject) pChild).getLocator() : pSyntaxSchema.getLocator();
					throw new LocSAXException("Unknown child type: " + pChild.getClass().getName() +
							", perhaps you should handle this in a subclass?", locator);
		}
	}
	
	/** <p>Handles xs:refefine.</p>
	 */
	protected void redefineSchema(XsESchema pRedefiningSchema,
			XsERedefine pRedefine)
	throws SAXException, IOException, ParserConfigurationException {
		// TODO: Implement redefine
		throw new LocSAXException("Redefine isn't yet implemented.", pRedefine.getLocator());
	}
	
	/** <p>Handles xs:include.</p>
	 */
	protected void includeSchema(XsESchema pIncludingSchema,
			XsEInclude pInclude, Locator pLocator,
			String pSchemaLocation)
	throws SAXException, IOException, ParserConfigurationException {
		final XsAnyURI schemaLocation = pInclude.getSchemaLocation();
		if (schemaLocation == null) {
			throw new LocSAXException("Invalid include: Missing 'schemaLocation' attribute.",
					pInclude.getLocator());
		}
		XsESchema includedSchema = parseSyntax(pLocator, schemaLocation.toString());
		XsAnyURI incNamespace = includedSchema.getTargetNamespace();
		if (incNamespace == null) {
			if (pIncludingSchema.getTargetNamespace() != null) {
				includedSchema.setTargetNamespace(pIncludingSchema.getTargetNamespace());
			}
		} else {
			XsAnyURI myNamespace = includedSchema.getTargetNamespace();
			if (!incNamespace.equals(myNamespace)) {
				throw new LocSAXException("Invalid include: The included schemas target namespace " +
						incNamespace + " and the including schemas target namespace " +
						myNamespace + " do not match.",
						pInclude.getLocator());
			}
		}
		parse(includedSchema, pSchemaLocation);
	}
	
	private void checkValidImportSchema(XsESchema pImportingSchema, String pNamespace,
			Locator pLocator)
	throws SAXException {
		if (pNamespace == null) {
			if (pImportingSchema.getTargetNamespace() == null) {
				throw new LocSAXException("The importing schema has no 'targetNamespace' attribute and" +
						" the 'import' element has no 'namespace' attribute, which is" +
						" forbidden. Perhaps you want to use include?",
						pLocator);
			}
		} else {
			if ("".equals(pNamespace)) {
				throw new LocSAXException("Invalid import: Empty 'namespace' attribute, which is forbidden." +
						" Perhaps you want to omit the attribute to indicate the absence of a namespace?",
						pLocator);
			}
			XsAnyURI targetNamespace = pImportingSchema.getTargetNamespace();
			if (targetNamespace != null  &&
					pNamespace.equals(targetNamespace.toString())) {
				throw new LocSAXException("The importing schema and the imported schema have the same namespace, which is forbidden. Perhaps you want to use include?",
						pLocator);
			}
		}
	}
	
	private void importSchema(XsESchema pImportingSchema, String pNamespace,
			XsESchema pImportedSchema, Locator pLocator,
			String pSchemaLocation)
	throws SAXException, ParserConfigurationException, IOException {
		XsAnyURI impNamespace = pImportedSchema.getTargetNamespace();
		if (pNamespace == null) {
			if (impNamespace != null) {
				throw new LocSAXException("The 'import' element does not have a 'namespace' attribute, but the imported schema has target namespace " +
						impNamespace + ", it ought to match and have none.",
						pLocator);
			}
		} else {
			if (impNamespace == null) {
				throw new LocSAXException("The 'import' element has a 'namespace' attribute (" + pNamespace +
						"), but the imported schema has no 'targetNamespace' attribute.",
						pLocator);
			} else if (!pNamespace.equals(impNamespace.toString())) {
				throw new LocSAXException("The 'import' elements 'namespace' attribute (" + pNamespace +
						") and the imported schemas 'targetNamespace' attribute (" +
						impNamespace + ") do not match.",
						pLocator);
			}
		}
		parse(pImportedSchema, pSchemaLocation);
	}
	
	/** <p>Handles xs:import.</p>
	 */
	protected void importSchema(XsESchema pImportingSchema,
			String pNamespace, String pSchemaLocation,
			Locator pLocator)
	throws SAXException, IOException, ParserConfigurationException {
		if (pSchemaLocation == null) {
			return;
		}
		checkValidImportSchema(pImportingSchema, pNamespace, pLocator);
		
		XsESchema importedSchema = parseSyntax(pLocator, pSchemaLocation);
		importSchema(pImportingSchema, pNamespace, importedSchema, pLocator, pSchemaLocation);
	}
	
	protected void importSchema(XsESchema pImportingSchema, String pNamespace, Node pNode,
			String pSchemaLocation)
	throws SAXException, IOException, ParserConfigurationException {
		checkValidImportSchema(pImportingSchema, pNamespace, null);
		XsESchema importedSchema = parseSyntax(pNode);
		importSchema(pImportingSchema, pNamespace, importedSchema, null, pSchemaLocation);
	}

	/** <p>Parses the given {@link InputSource} syntactically and
	 * converts the objects that it finds into logical objects.
	 * These logical objects are added to the given {@link XSSchema}.</p>
	 */
	protected void parse(XsESchema pSyntaxSchema, String pSchemaLocation)
			throws ParserConfigurationException, SAXException, IOException {
		if (pSchemaLocation != null) {
			AddedImport schema = new AddedImport(pSyntaxSchema.getTargetNamespace(),
									 			 pSchemaLocation);
			if (parsedSchemas == null) {
				parsedSchemas = new HashSet();
			} else if (parsedSchemas.contains(schema)) {
				return; // Already imported/included, ignore it
			}
			parsedSchemas.add(schema);

			for (int i = 0;  i < addedImports.size();  i++) {
				AddedImport addedImport = (AddedImport) addedImports.get(i);
				if (schema.equals(addedImport)) {
					return;
				}
			}
		}
		addSyntaxSchema(pSyntaxSchema);
		try {
			Object[] childs = pSyntaxSchema.getChilds();

			for (int i = 0;  i < childs.length;  i++) {
				Object o = childs[i];
				if (o instanceof XsEInclude) {
					XsEInclude xsEInclude = (XsEInclude) o;
					XsAnyURI schemaLocation = xsEInclude.getSchemaLocation();
					includeSchema(pSyntaxSchema, xsEInclude,
							getImportLocator(xsEInclude, pSchemaLocation),
							schemaLocation == null ? null : schemaLocation.toString());
				} else if (o instanceof XsERedefine) {
					redefineSchema(pSyntaxSchema, (XsERedefine) o);
				} else if (o instanceof XsEImport) {
					XsEImport xsEImport = (XsEImport) o;
					XsAnyURI namespace = xsEImport.getNamespace();
					XsAnyURI schemaLocation = xsEImport.getSchemaLocation();
					importSchema(pSyntaxSchema, namespace == null ? null : namespace.toString(),
							schemaLocation == null ? null : schemaLocation.toString(),
									getImportLocator(xsEImport, pSchemaLocation));
				} else {
					add(pSyntaxSchema, childs[i]);
				}
			}
		} finally {
			removeSyntaxSchema();
		}
	}

	private Locator getImportLocator(XsObject pObject, String pSchemaLocation) {
		Locator result = pObject.getLocator();
		if (result == null  &&  pSchemaLocation != null) {
			LocatorImpl loc = new LocatorImpl();
			loc.setSystemId(pSchemaLocation);
			result = loc;
		}
		return result;
	}
	
	
	private static class SubstitutionGroup {
		private final List members = new ArrayList();
		private final XSElement head;
		
		SubstitutionGroup(XSElement pHead) {
			head = pHead;
		}
		XSElement getHead() { return head; }
		XSElement[] getMembers() {
			return (XSElement[]) members.toArray(new XSElement[members.size()]);
		}
		void addMember(XSElement pElement) {
			members.add(pElement);
		}
	}
	
	protected void createSubstitutionGroups(XSSchema pSchema) throws SAXException {
		Object[] myChilds = pSchema.getChilds();
		
		// Build the Map of substitution groups.
		Map substitutionGroups = new HashMap();
		for (int i = 0;  i < myChilds.length;  i++) {
			if (myChilds[i] instanceof XSElement) {
				XSElement element = (XSElement) myChilds[i];
				XsQName qName = element.getSubstitutionGroupName();
				if (qName != null) {
					SubstitutionGroup group = (SubstitutionGroup) substitutionGroups.get(qName);
					if (group == null) {
						XSElement head = pSchema.getElement(qName);
						if (head == null) {
							throw new LocSAXException("The substituted element " + qName + " is missing in the schema.",
									element.getLocator());
						}
						if (head.isBlockedForSubstitution()) {
							throw new LocSAXException("The substituted element " + qName + " is blocked for substitution.",
									element.getLocator());
						}
						group = new SubstitutionGroup(head);
						if (!head.isAbstract()) {
							group.addMember(head);
						}
						substitutionGroups.put(qName, group);
					}
					group.addMember(element);
				}
			}
		}
		
		// For any substitution group: Build an implicit choice group, which
		// may be used to replace the substitution groups head, if required.
		for (Iterator iter = substitutionGroups.values().iterator();  iter.hasNext();  ) {
			SubstitutionGroup group = (SubstitutionGroup) iter.next();
			XSElementImpl head = (XSElementImpl) group.getHead();
			XsObject object = head.getXsObject();
			XsESchema syntaxSchema = object.getXsESchema();
			
			// Find a name for the group
			String namespace = syntaxSchema.getTargetNamespace().toString();
			String localName = head.getName().getLocalName() + "Group";
			XsQName suggestion = new XsQName(namespace, localName);
			if (pSchema.getGroup(suggestion) != null) {
				for (int i = 0;  ;  i++) {
					suggestion = new XsQName(namespace, localName + i);
					if (pSchema.getGroup(suggestion) == null) {
						break;
					}
				}
			}
			
			XsTNamedGroup namedGroup = object.getObjectFactory().newXsTNamedGroup(syntaxSchema);
			namedGroup.setName(new XsNCName(suggestion.getLocalName()));
			XsTSimpleExplicitGroup choice = namedGroup.createChoice();
			XSElement[] members = group.getMembers();
			for (int j = 0;  j < members.length;  j++) {
				XSElement member = members[j];
				XsTLocalElement memberElement = choice.createElement();
				memberElement.setRef(member.getName());
			}
			
			XSGroupImpl xsGroup = (XSGroupImpl) getSchema().getXSObjectFactory().newXSGroup(pSchema, namedGroup);
			pSchema.add(xsGroup);
			head.setSubstitutionGroup(xsGroup);
		}
	}
	
	/** <p>This is the logical parsers frontend for parsing a stream
	 * of SAX events.</p>
	 * @param pSystemId System Id (schema location of the schema being parsed, if known.
	 * Null otherwise. Knowing the system id is important only, if you want
	 * to prevent recursive includes.
	 */
	public XSContentHandler getXSContentHandler(String pSystemId) throws SAXException {
		return new XSContentHandlerImpl(this, pSystemId);
	}
	
	/** <p>This is the logical parsers frontend for parsing a DOM node.</p>
	 */
	public XSSchema parse(Node pNode) throws SAXException {
		XSContentHandler handler = getXSContentHandler(null);
		DOMSerializer ds = new DOMSerializer();
		ds.serialize(pNode, handler);
		return handler.getXSSchema();
	}
	
	/** <p>This is the logical parsers frontend for parsing the given
	 * {@link InputSource}. If the parsed schema includes or imports other
	 * schemas, they are also parsed and added to the parsers object
	 * tree.</p>
	 * @see #getXSContentHandler()
	 */
	public XSSchema parse(InputSource pSource)
			throws ParserConfigurationException, SAXException, IOException {
		XSContentHandler contentHandler = getXSContentHandler(pSource.getSystemId());
		XSContext data = getData();
		XMLReader xr = data.getXsObjectFactory().newXMLReader(isValidating());
		runContentHandler(xr, contentHandler, pSource);
		return getSchema();
	}
	
	protected void clearSyntaxSchemas() {
		syntaxSchemas.clear();
		syntaxSchemaArray = null;
	}
	
	protected void addSyntaxSchema(XsESchema pSyntaxSchema) {
		syntaxSchemas.add(pSyntaxSchema);
		syntaxSchemaArray = null;
	}
	
	protected void removeSyntaxSchema() {
		syntaxSchemas.remove(syntaxSchemas.size()-1);
		syntaxSchemaArray = null;
	}
	
	/** <p>Provides context information to the schema which is currently being parsed.
	 * The schema with index 0 is the outermost schema, on which the parser is actually
	 * invoked.</p>
	 */
	public XsESchema[] getSyntaxSchemas() {
		if (syntaxSchemaArray == null) {
			syntaxSchemaArray = (XsESchema[]) syntaxSchemas.toArray(new XsESchema[syntaxSchemas.size()]);
		}
		return syntaxSchemaArray;
	}
	
	/** <p>Returns the syntax schema, which is currently being parsed.</p>
	 */
	public XsESchema getCurrentSyntaxSchema() {
		if (syntaxSchemaArray == null  ||  syntaxSchemaArray.length == 0) {
			return null;
		} else {
			return syntaxSchemaArray[syntaxSchemaArray.length-1];
		}
	}
}
