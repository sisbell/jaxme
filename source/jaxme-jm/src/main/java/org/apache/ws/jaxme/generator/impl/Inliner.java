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
package org.apache.ws.jaxme.generator.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.ws.jaxme.util.DOMBuilder;
import org.apache.ws.jaxme.util.DOMSerializer;
import org.apache.ws.jaxme.xs.SchemaTransformer;
import org.apache.ws.jaxme.xs.XSParser;
import org.apache.ws.jaxme.xs.jaxb.impl.JAXBParser;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;


/** Performs the modifications on an XML schema file, as
 * specified by an external binding file. This is based
 * on a suggestion from Ortwin Glueck, see
 * <a href="http://wiki.apache.org/ws/JaxMe/ExternalSchemaBindings">
 * http://wiki.apache.org/ws/JaxMe/ExternalSchemaBindings</a>.
 */
public class Inliner implements SchemaTransformer {
	private final Document[] bindings;
	private XMLReader transformedXMLReader;
	private InputSource transformedInputSource;

	/** Creates a new instance with the given bindings.
	 */
	public Inliner(Document[] pBindings) {
		bindings = pBindings;
	}

	private Document read(InputSource pSource, XMLReader pReader)
			throws SAXException, ParserConfigurationException, IOException {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		spf.setNamespaceAware(true);
		spf.setValidating(false);
		XMLReader xr = spf.newSAXParser().getXMLReader();
		xr.setEntityResolver(pReader.getEntityResolver());
		xr.setErrorHandler(pReader.getErrorHandler());
		DOMBuilder db = new DOMBuilder();
		db.setPrefixMappingIsAttribute(true);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(false);
		dbf.setNamespaceAware(true);
		Document doc = dbf.newDocumentBuilder().newDocument();
		db.setDocument(doc);
		db.setTarget(doc);
		xr.setContentHandler(db);
		xr.parse(pSource);
		return doc;
	}

	private boolean isURIMatching(String pURI, Element pBindings) {
		final Attr schemaLocationAttr = pBindings.getAttributeNodeNS(null, "schemaLocation");
		if (schemaLocationAttr == null) {
			return true;
		}
		String value = schemaLocationAttr.getValue();
		if (value.equals(pURI)) {
			return true;
		}
		/* I'd prefer not to use the following. However, fact is that we do see the operating
		 * systems absolute path or a path relative to a projects base directory or stuff like
		 * that in most cases and not the URI, which the user specifies in its project.
		 */
		if (pURI == null) {
			return false;
		}
		if (pURI.endsWith("/" + value)) {
			return true;
		}
		if (!File.separator.equals("/")  &&  pURI.endsWith(File.separator + value)) {
			return true;
		}
		return false;
	}

	private void apply(final Node pTarget, Element pBindings, String pURI)
			throws XPathExpressionException, SAXException {
		final Attr nodeAttr = pBindings.getAttributeNodeNS(null, "node");
		if (!isURIMatching(pURI, pBindings)) {
			return;
		}
		if (nodeAttr == null) {
			importChilds(pTarget, pBindings, pURI);
		} else {
			final XPath xpath = XPathFactory.newInstance().newXPath();
			NamespaceContext ctx = new NamespaceContext(){
				public Iterator getPrefixes(String pURI) {
					if (pURI == null) {
						throw new IllegalArgumentException("The URI must not be null.");
					}
					if (XMLConstants.XML_NS_URI.equals(pURI)) {
						return Collections.singletonList(XMLConstants.XML_NS_PREFIX).iterator();
					}
					if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(pURI)) {
						return Collections.singletonList(XMLConstants.XMLNS_ATTRIBUTE).iterator();
					}
					List list = getPrefixDeclarations(pTarget);
					List result = new ArrayList();
					for (int i = 0;  i < list.size();  i += 2) {
						if (pURI.equals(list.get(i+1))) {
							String prefix = (String) list.get(i);
							if (!result.contains(prefix)) {
								result.add(prefix);
							}
						}
					}
					return result.iterator();
				}
				public String getPrefix(String pURI) {
					String result = null;
					Iterator iter = getPrefixes(pURI);
					if (iter.hasNext()) {
						result = (String) iter.next();
					} else {
						if ("".equals(pURI)) {
							result = XMLConstants.DEFAULT_NS_PREFIX;
						}
					}
					return result;
				}
				public String getNamespaceURI(String pPrefix) {
					if (pPrefix == null) {
						throw new IllegalArgumentException("The prefix must not be null.");
					}
					String result = null;
					if (XMLConstants.XML_NS_PREFIX.equals(pPrefix)) {
						result = XMLConstants.XML_NS_URI;
					} else if (XMLConstants.XMLNS_ATTRIBUTE.equals(pPrefix)) {
						result = XMLConstants.XMLNS_ATTRIBUTE_NS_URI;
					} else {
						List list = getPrefixDeclarations(pTarget);
						for (int i = 0;  i < list.size();  i += 2) {
							if (pPrefix.equals(list.get(i))) {
								result = (String) list.get(i+1);
								break;
							}
						}
						if (result == null  &&  pPrefix.equals(XMLConstants.DEFAULT_NS_PREFIX)) {
							result = "";
						}
					}
					return result;
				}
				private List getPrefixDeclarations(Node pTarget) {
					List list = new ArrayList();
					addPrefixDeclarations(pTarget, list);
					return list;
				}
				private void addPrefixDeclarations(Node pNode, List pList) {
					if (pNode == null) {
						return;
					}
					if (pNode.getNodeType() == Node.ELEMENT_NODE) {
						Element e = (Element) pNode;
						if (e.getNamespaceURI() == null  ||  "".equals(e.getNamespaceURI())) {
							pList.add(XMLConstants.DEFAULT_NS_PREFIX);
							pList.add(e.getPrefix());
						} else {
							pList.add(e.getPrefix());
							pList.add(e.getNamespaceURI());
						}
						NamedNodeMap attrs = e.getAttributes();
						for (int i = 0;  i < attrs.getLength();  i++) {
							Attr attr = (Attr) attrs.item(i);
							if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(attr.getNamespaceURI())) {
								pList.add(attr.getPrefix());
								pList.add(attr.getValue());
							} else if (null == attr.getNamespaceURI()  &&  XMLConstants.XMLNS_ATTRIBUTE.equals(attr.getLocalName())) {
								pList.add(attr.getLocalName());
								pList.add(attr.getValue());
							}
						}
					}
					addPrefixDeclarations(pNode.getParentNode(), pList);
				}
			};
			xpath.setNamespaceContext(ctx);
			final NodeList nodes = (NodeList) xpath.evaluate(nodeAttr.getValue(), pTarget, XPathConstants.NODESET);
			for (int i = 0;  i < nodes.getLength();  i++) {
				final Node node = nodes.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE
					&&  XSParser.XML_SCHEMA_URI.equals(node.getNamespaceURI())) {
					importChilds(node, pBindings, pURI);
				}
			}
		}
	}

	private void importChilds(Node pTarget, Node pSource, String pURI)
			throws XPathExpressionException, SAXException {
		final Document doc = pTarget.getOwnerDocument();
		Element appInfoElement = null;
		for (Node child = pSource.getFirstChild();  child != null;  child = child.getNextSibling()) {
			if (child.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			Element e = (Element) child;
			if (JAXBParser.JAXB_SCHEMA_URI.equals(e.getNamespaceURI())
				&&  "bindings".equals(e.getLocalName())) {
				apply(pTarget, e, pURI);
			} else {
				if (appInfoElement == null) {
					if (pTarget.getNodeType() != Node.ELEMENT_NODE) {
						throw new SAXException("Attempt to import childs into a node of type " +
								pTarget.getNodeType() + ", perhaps invalid XPath expression?");
					}
					Element targetElement = (Element) pTarget;
					final String prefix = pTarget.getPrefix();
					Element annotationElement = getChild(targetElement, prefix, "annotation");
					appInfoElement = getChild(annotationElement, prefix, "appinfo");
				}
				appInfoElement.appendChild(doc.importNode(e, true));
			}
		}
	}

	private Element getChild(Element pParent, String pPrefix, String pName) {
		for (Node child = pParent.getFirstChild();  child != null;  child = child.getNextSibling()) {
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				Element e = (Element) child;
				if (pName.equals(e.getLocalName())  &&  XSParser.XML_SCHEMA_URI.equals(e.getNamespaceURI())) {
					return e;
				}
			}
		}

		String qName = (pPrefix == null || pPrefix.length() == 0) ? pName : pPrefix + ":" + pName;
		Element e = pParent.getOwnerDocument().createElementNS(XSParser.XML_SCHEMA_URI, qName);
		pParent.insertBefore(e, pParent.getFirstChild());
		return e;
	}

	public void parse(InputSource pSource, XMLReader pReader) throws ParserConfigurationException, SAXException, IOException {
		final String uri = pSource.getSystemId();
		final Document schema = read(pSource, pReader);
		try {
			for (int i = 0;  i < bindings.length;  i++) {
				Element bindingsElement = bindings[i].getDocumentElement();
				if (!JAXBParser.JAXB_SCHEMA_URI.equals(bindingsElement.getNamespaceURI())
						||  !"bindings".equals(bindingsElement.getLocalName())) {
					throw new SAXException("Expected " +
							new QName(JAXBParser.JAXB_SCHEMA_URI, "bindings") +
							" as a binding files root element, got " +
							new QName(bindingsElement.getNamespaceURI(), bindingsElement.getLocalName()));
				}
				apply(schema.getDocumentElement(), bindingsElement, uri);
			}
		} catch (XPathExpressionException e) {
			throw new SAXException(e);
		}
		transformedInputSource = new InputSource();
		transformedXMLReader = new XMLFilterImpl(){
			public void parse(InputSource pInput) throws SAXException, IOException {
				new DOMSerializer().serialize(schema, this);
			}

			public void parse(String pSystemId) throws SAXException, IOException {
				throw new IllegalStateException("Not implemented");
			}
		};
	}

	public InputSource getTransformedInputSource() {
		return transformedInputSource;
	}

	public XMLReader getTransformedXMLReader() {
		return transformedXMLReader;
	}
}
