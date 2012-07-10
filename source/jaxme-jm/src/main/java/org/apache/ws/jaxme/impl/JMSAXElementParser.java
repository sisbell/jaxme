/*
 * Copyright 2005-2006  The Apache Software Foundation
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

import javax.xml.bind.ValidationEvent;

import org.apache.ws.jaxme.ValidationEvents;
import org.apache.ws.jaxme.XMLConstants;
import org.apache.ws.jaxme.xs.xml.XsQName;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;


/** A subclass of {@link org.apache.ws.jaxme.impl.JMSAXGroupParser}
 * for parsing complex elements.
 */
public abstract class JMSAXElementParser extends JMSAXGroupParser {
	private JMUnmarshallerHandlerImpl handler;
	protected Object result;
	private String namespaceURI, localName;
    private int level;

	public JMUnmarshallerHandlerImpl getHandler() {
		return handler;
	}

	/** Returns, whether the element has atomic content.
	 */
	public boolean isAtomic() { return false; }

	/** Returns, whether the element is empty.
	 */
	public boolean isEmpty() { return false; }

	/** Returns the namespace URI of the element being parsed.
     */
    public String getNamespaceURI() { return namespaceURI; }
    /** Returns the local name of the element being parsed.
     */
    public String getLocalName() { return localName; }
    /** Returns the end elements level (number of nested
     * elements enclosing this element).
     */
    public int getEndLevel() { return level; }

	/** Initializes the element parser by setting the required data.
	 */
	public void init(JMUnmarshallerHandlerImpl pHandler, Object pObject,
					 String pNamespaceURI, String pLocalName, int pLevel) {
		handler = pHandler;
		result = pObject;
		namespaceURI = pNamespaceURI;
        localName = pLocalName;
        level = pLevel;
	}

	/** Sets the attribute with the namespace
	 * <code>pNamespace</code> and the local name <code>pLocalName</code>
	 * to the value <code>pValue</code>.
	 */
	public void addAttribute(String pNamespaceURI, String pLocalName,
							 String pValue) throws SAXException {
	  	if (javax.xml.XMLConstants.XML_NS_URI.equals(pNamespaceURI)
			||  javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(pNamespaceURI)
			||  XMLConstants.XML_SCHEMA_URI.equals(pNamespaceURI)) {
			// Ignore attributes in the xsi namespace
	    } else {
			XsQName qName = new XsQName(pNamespaceURI, pLocalName);
			handler.validationEvent(ValidationEvent.WARNING, ValidationEvents.EVENT_UNKNOWN_ATTRIBUTE,
									"Unknown attribute '" + qName + "' with value '"
									+ pValue + "'",  null);
	    }
	}

	/** Invokes {@link #addAttribute(String, String, String)} for
	 * all the attributes in the list <code>pAttrs</code>.
	 */
	public void setAttributes(Attributes pAttrs) throws SAXException {
		if (pAttrs != null) {
			for (int i = 0;  i < pAttrs.getLength();  i++) {
				addAttribute(pAttrs.getURI(i), pAttrs.getLocalName(i),
							 pAttrs.getValue(i));
			}
		}
	}
}
