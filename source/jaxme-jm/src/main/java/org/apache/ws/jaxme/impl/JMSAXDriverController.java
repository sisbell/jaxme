/*
 * Copyright 2005  The Apache Software Foundation
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
package org.apache.ws.jaxme.impl;

import javax.xml.bind.DatatypeConverterInterface;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.bind.helpers.PrintConversionEventImpl;
import javax.xml.bind.helpers.ValidationEventLocatorImpl;

import org.apache.ws.jaxme.XMLConstants;
import org.apache.ws.jaxme.util.NamespaceSupport;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;


/** The controller is created by the
 * {@link org.apache.ws.jaxme.JMMarshaller} for
 * marshalling a given element.
 */
public class JMSAXDriverController {
	private static final Attributes ZERO_ATTRIBUTES = new AttributesImpl();
	private JMMarshallerImpl marshaller;
	private DatatypeConverterInterface converter;
	private ContentHandler target;
    private final NamespaceSupport nss = new NamespaceSupport();
	private int cnt;

	/** Returns the Marshaller, which created the controller.
     */
	public JMMarshallerImpl getJMMarshaller() {
		return marshaller;
	}

	/** Returns the target handler, to which SAX events are
	 * being fired.
	 */
	public ContentHandler getTarget() {
		return target;
	}

    /** Returns an instance of NamespaceSupport.
     */
    public NamespaceSupport getNamespaceContext() {
		return nss;
    }

	/** Initializes the controller by setting marshaller and target.
	 */
	public void init(JMMarshallerImpl pMarshaller, ContentHandler pTarget) throws SAXException {
		marshaller = pMarshaller;
		target = pTarget;
		converter = marshaller.getDatatypeConverter();
	}

	/** Returns the {@link DatatypeConverterInterface} being used for
	 * conversion of atomic values.
	 */
	public DatatypeConverterInterface getDatatypeConverter() {
		return converter;
	}

	protected String getNewPrefix(String pURI, String pSuggestedPrefix) {
		if (pSuggestedPrefix == null  ||  pSuggestedPrefix.length() == 0) {
			if (!nss.isPrefixDeclared("")) {
				nss.declarePrefix("", pURI);
				return "";
			}
			pSuggestedPrefix = "p";
		}
		String pc = pSuggestedPrefix;
		for (;;) {
			if (!nss.isPrefixDeclared(pc)) {
				nss.declarePrefix(pc, pURI);
				return pc;
			}
			pc = pSuggestedPrefix + ++cnt;
		}
	}

	protected String getPreferredPrefix(JMSAXDriver pDriver, String pURI) {
		if (pDriver != null) {
			String prefix = pDriver.getPreferredPrefix(pURI);
			if (prefix != null) {
				return prefix;
			}
		}
		if (XMLConstants.XML_SCHEMA_URI.equals(pURI)) {
			return "xsi";
		} else {
			return null;
		}
	}

	protected String getElementQName(JMSAXDriver pDriver, String pPrefix,
									String pNamespaceURI, String pLocalName) throws SAXException {
		if (pNamespaceURI == null) {
			pNamespaceURI = "";
		}
		String prefix = nss.getPrefix(pNamespaceURI);
		if (prefix == null) {
			if (pPrefix != null) {
				prefix = pPrefix;
			} else {
				prefix = getNewPrefix(pNamespaceURI, getPreferredPrefix(pDriver, pNamespaceURI));
			}
			nss.declarePrefix(prefix, pNamespaceURI);
			getTarget().startPrefixMapping(prefix, pNamespaceURI);
		}
		if (prefix == null  ||  "".equals(prefix)) {
			return pLocalName;
		} else {
			return prefix + ":" + pLocalName;
		}
	}

	/** Returns the qualified name of the element <code>pLocalName</code>
	 * in namespace <code>pNamespaceURI</code>. In other words, it attachs
	 * a prefix, if required.
	 */
	public String getElementQName(JMSAXDriver pDriver, String pNamespaceURI,
								  String pLocalName)
			throws SAXException {
		if (pNamespaceURI == null) {
			pNamespaceURI = "";
		}
		String prefix = nss.getPrefix(pNamespaceURI);
		if (prefix == null) {
			prefix = getNewPrefix(pNamespaceURI, getPreferredPrefix(pDriver, pNamespaceURI));
			nss.declarePrefix(prefix, pNamespaceURI);
			getTarget().startPrefixMapping(prefix, pNamespaceURI);
		}
		if (prefix == null  ||  "".equals(prefix)) {
			return pLocalName;
		} else {
			return prefix + ":" + pLocalName;
		}
	}

	/** Returns the qualified name of the attribute <code>pLocalName</code>.
	 * In other words, attachs a prefix, if required.
	 */
	public String getAttrQName(JMSAXDriver pDriver, String pNamespaceURI,
							   String pLocalName) throws SAXException {
		if (pNamespaceURI == null) {
			pNamespaceURI = "";
		}
		String prefix = nss.getAttributePrefix(pNamespaceURI);
		if (prefix == null) {
			prefix = getPreferredPrefix(pDriver, pNamespaceURI);
			if (prefix == null) {
				prefix = getNewPrefix(pNamespaceURI, "p");
			}
			getTarget().startPrefixMapping(prefix, pNamespaceURI);
		}
		if (prefix == null  ||  "".equals(prefix)) {
			return pLocalName;
		} else {
			return prefix + ":" + pLocalName;
		}
	}

	protected void addSchemaLocationAttributes(JMSAXDriver pDriver,
											   AttributesImpl pAttrs) throws SAXException {
		JMMarshallerImpl m = getJMMarshaller();
		String schemaLocation = m.getSchemaLocation();
		String schemaLocationAttribute;
		if (schemaLocation != null) {
			schemaLocationAttribute = XMLConstants.XML_SCHEMA_NS_ATTR;
		} else {
			schemaLocation = m.getNoNamespaceSchemaLocation();
			if (schemaLocation != null) {
				schemaLocationAttribute = XMLConstants.XML_SCHEMA_NO_NS_ATTR;
			} else {
				schemaLocationAttribute = null;
			}
		}
		if (schemaLocation != null) {
			String qName = getAttrQName(pDriver, XMLConstants.XML_SCHEMA_URI, schemaLocationAttribute);
			pAttrs.addAttribute(XMLConstants.XML_SCHEMA_URI, schemaLocationAttribute,
								qName, "CDATA", schemaLocation);
		}
	}

	/** Marshals the given object, creating a root element with
	 * the given namespace URI and local name.
	 * @param pElement The element being marshalled. It must be
	 *    an instance of the class associated to this specific
	 *    JMXmlSerializer.
	 */
	public void marshal(JMSAXDriver pDriver, String pPrefix,
						String pNamespaceURI, String pLocalName,
						Object pElement) throws SAXException {
		int context = nss.getContext();
		String qName = getElementQName(pDriver, pPrefix, pNamespaceURI, pLocalName);
		AttributesImpl attrs = pDriver.getAttributes(this, pElement);
		addSchemaLocationAttributes(pDriver, attrs);
		ContentHandler h = getTarget();
		h.startElement(pNamespaceURI, pLocalName, qName, attrs);
		pDriver.marshalChilds(this, h, pElement);
		h.endElement(pNamespaceURI, pLocalName, qName);
		restoreContext(context);
	}
	
	/** Marshals the given object, creating an element with
	 * the given namespace URI and local name.
	 * @param pElement The element being marshalled. It must be
	 *    an instance of the class associated to this specific
	 *    JMXmlSerializer.
	 */
	public void marshal(JMSAXDriver pDriver, String pNamespaceURI,
						String pLocalName, Object pElement) throws SAXException {
		int context = nss.getContext();
		String qName = getElementQName(pDriver, pNamespaceURI, pLocalName);
		AttributesImpl attrs = pDriver.getAttributes(this, pElement);
		ContentHandler h = getTarget();
		h.startElement(pNamespaceURI, pLocalName, qName, attrs);
		pDriver.marshalChilds(this, h, pElement);
		h.endElement(pNamespaceURI, pLocalName, qName);
		restoreContext(context);
	}

	/** Called by the driver for creating a simple child.
	 */
	public void marshalSimpleChild(JMSAXDriver pDriver, String pNamespaceURI,
								   String pLocalName, String pValue)
			throws SAXException {
		int context = nss.getContext();
		String qName = getElementQName(pDriver, pNamespaceURI, pLocalName);
		ContentHandler h = getTarget();
		h.startElement(pNamespaceURI, pLocalName, qName, ZERO_ATTRIBUTES);
		if (pValue != null  &&  pValue.length() > 0) {
			h.characters(pValue.toCharArray(), 0, pValue.length());
		}
		h.endElement(pNamespaceURI, pLocalName, qName);
		restoreContext(context);
	}

	private void restoreContext(int pContext) throws SAXException {
		NamespaceSupport nss = getNamespaceContext();
		ContentHandler h = getTarget();
		for (;;) {
			String prefix = nss.checkContext(pContext);
			if (prefix == null) {
				return;
			}
			h.endPrefixMapping(prefix);
		}
	}

	/** Fires a {@link javax.xml.bind.PrintConversionEvent}.
	 */
	public void printConversionEvent(Object pObject, String pMsg, Exception pException) throws SAXException {
		ValidationEventHandler handler = getJMMarshaller().getEventHandler();
		if (handler != null) {
			ValidationEventLocator locator = new ValidationEventLocatorImpl(pObject);
			PrintConversionEventImpl event = new PrintConversionEventImpl(ValidationEvent.FATAL_ERROR, pMsg, locator);
			if (handler.handleEvent(event)) {
				return;
			}
		}
		if (pException instanceof SAXException) {
			throw (SAXException) pException;
		} else {
			throw new SAXException(pMsg, pException);
		}
	}

	/** Returns an ID for the object <code>pObject</code>.
	 * The default implementation returns null. This method
	 * is being overwritten by subclasses.
	 * @param pObject The object for which an ID is being queried.
	 */
	public String getXsIdAttribute(Object pObject) { return null; }
}
