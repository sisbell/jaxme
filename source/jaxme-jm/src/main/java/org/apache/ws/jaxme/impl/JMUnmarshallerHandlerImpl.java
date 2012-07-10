/*
 * Copyright 2003,2004  The Apache Software Foundation
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.DatatypeConverterInterface;
import javax.xml.bind.JAXBException;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.helpers.ParseConversionEventImpl;
import javax.xml.bind.helpers.ValidationEventImpl;
import javax.xml.bind.helpers.ValidationEventLocatorImpl;
import javax.xml.namespace.QName;

import org.apache.ws.jaxme.IDREF;
import org.apache.ws.jaxme.JMManager;
import org.apache.ws.jaxme.JMUnmarshaller;
import org.apache.ws.jaxme.JMUnmarshallerHandler;
import org.apache.ws.jaxme.Observer;
import org.apache.ws.jaxme.ValidationEvents;
import org.apache.ws.jaxme.util.NamespaceSupport;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


/** <p>Implementation of a JMUnmarshallerHandler; the
 * UnmarshallerHandler receives SAX events which he silently
 * discards, as long as the first <code>startElement</code>
 * event is seen. Depending on namespace URI and local name,
 * the Unmarshallerhandler creates a new instance of JMHandler
 * and from now on forwards all SAX events to the JMHandler.</p>
 */
public class JMUnmarshallerHandlerImpl implements JMUnmarshallerHandler {
    /** State for parsing a simple, atomic element.
     */
    private static final int STATE_SIMPLE_ATOMIC = 1;
    /** State for parsing a complex element. The outer
     * {@link JMSAXGroupParser} will always be parsing
     * a complex element.
     */
    private static final int STATE_COMPLEX_CONTENT = 3;
    /** State for parsing an anonymous group. This is
     * mostly comparable to parsing a complex element,
     * except that the group doesn't have an outer
     * start and end element.
     */
    private static final int STATE_GROUP = 4;

	private final JMUnmarshaller unmarshaller;
    private Locator locator;
    private NamespaceSupport nss = new NamespaceSupport();
    private final List groupParsers = new ArrayList();
    private JMSAXGroupParser activeParser;
    private int level;
    private int endLevel;
    private int state;
    private final StringBuffer sb = new StringBuffer();
	private Observer observer;
	private Object result;
	private final Map idMap = new HashMap();
	private final List idrefs = new ArrayList();

    public int getLevel() { return level; }

    /** Removes the currently active parser from the stack.
     */
    private boolean removeActiveParser() {
        int size = groupParsers.size();
        groupParsers.remove(--size);
        if (size == 0) {
            return false;
        }
        activeParser = (JMSAXGroupParser) groupParsers.get(--size);
        if (activeParser instanceof JMSAXElementParser) {
            state = STATE_COMPLEX_CONTENT;
            endLevel = ((JMSAXElementParser) activeParser).getEndLevel();
        } else {
            state = STATE_GROUP;
        }
        return true;
    }

	/** Sets an observer, which will be notified, when the element has
	 * been parsed.
	 */
	public void setObserver(Observer pObserver) {
		observer = pObserver;
	}

	/** Returns the observer, which will be notified, when the element has
	 * been parsed.
	 */
	public Observer getObserver() {
		return observer;
	}

	/** Creates a new instance, controlled by the given
     * {@link JMUnmarshaller}.
     */
    public JMUnmarshallerHandlerImpl(JMUnmarshaller pUnmarshaller) {
        unmarshaller = pUnmarshaller;
    }

    /** Returns the {@link JMUnmarshaller}, which created this
     * handler.
     */
    public JMUnmarshaller getJMUnmarshaller() {
        return unmarshaller;
    }

    public void setDocumentLocator(Locator pLocator) {
        locator = pLocator;
    }

    public void startDocument() throws SAXException {
    }

    public void endDocument() throws SAXException {
    	for (Iterator i = idrefs.iterator(); i.hasNext();) {
			IDREF idref = (IDREF) i.next();
			idref.validate(idMap);
		}
    }

    public void startPrefixMapping(String pPrefix, String pURI) throws SAXException {
        nss.declarePrefix(pPrefix, pURI);
    }

    public void endPrefixMapping(String pPrefix) throws SAXException {
        nss.undeclarePrefix(pPrefix);
    }

	/** Tests, whether the group parser accepts the element.
	 * If so, adds the group parser to the stack.
	 */
	public boolean testGroupParser(JMSAXGroupParser pParser,
								   String pNamespaceURI, String pLocalName,
								   String pQName, Attributes pAttrs)
			throws SAXException {
		groupParsers.add(pParser);
		activeParser = pParser;
		state = STATE_GROUP;
		if (pParser.startElement(pNamespaceURI, pLocalName, pQName, pAttrs)) {
			return true;
		} else {
			removeActiveParser();
			return false;
		}
	}

	/** Adds a parser for an nested element to the stack of parsers.
	 */
	public void addElementParser(JMSAXElementParser pParser) {
		endLevel = pParser.getEndLevel();
		groupParsers.add(pParser);
		activeParser = pParser;
		state = STATE_COMPLEX_CONTENT;
		if (pParser.isAtomic()) {
			sb.setLength(0);
		}
	}

    public void startElement(String pNamespaceURI, String pLocalName,
                             String pQName, Attributes pAttrs) throws SAXException {
		if (level++ == 0) {
			JAXBContextImpl context = unmarshaller.getJAXBContextImpl();
			JMManager manager;
			QName qName = new QName(pNamespaceURI, pLocalName);
			try {
				manager = context.getManager(qName);
			} catch (JAXBException e) {
				throw new SAXException("Unable to instantiate manager for element " + qName, e);
			}
			Object o = manager.getElementS();
			JMSAXElementParser parser = manager.getHandler();
			parser.init(this, o, pNamespaceURI, pLocalName, 1);
			parser.setAttributes(pAttrs);
			groupParsers.clear();
			result = o;
			addElementParser(parser);
		} else {
			if (state == STATE_COMPLEX_CONTENT  ||  state == STATE_GROUP) {
				for (;;) {
					if (activeParser.startElement(pNamespaceURI, pLocalName, pQName, pAttrs)) {
						return;
					}
					if (state == STATE_GROUP) {
						if (removeActiveParser()) {
							continue;
						}
					}
					break;
				}
			}
			QName qName = new QName(pNamespaceURI, pLocalName);
			validationEvent(ValidationEvent.WARNING,
					"Unexpected element: '" + qName + "'",
					ValidationEvents.EVENT_UNEXPECTED_CHILD_ELEMENT,
					null);
		}
    }

    public void endElement(String pNamespaceURI, String pLocalName, String pQName) throws SAXException {
		int lvl = level--;
		switch (state) {
			case STATE_GROUP:
				while (state == STATE_GROUP) {
					if (activeParser.isFinished()) {
						removeActiveParser();
					}
				}
				if (state != STATE_COMPLEX_CONTENT) {
					break;
				}
			case STATE_COMPLEX_CONTENT:
	            JMSAXElementParser elementParser = (JMSAXElementParser) activeParser;
				if (lvl != endLevel) {
					if (endLevel > lvl) {
						validationEvent(ValidationEvent.ERROR,
										"Premature endElement: " + new QName(pNamespaceURI, pLocalName),
										ValidationEvents.EVENT_PREMATURE_END_ELEMENT,
										null);
						while (endLevel > lvl) {
							terminateComplexType(pNamespaceURI, pLocalName, pQName, 
												 elementParser);
						}
					} else {
						throw new IllegalStateException("Expected level " + endLevel
														+ ", got " + lvl);
					}
				}
				if (elementParser.isAtomic()) {
					elementParser.endElement(pNamespaceURI, pLocalName, pQName, sb.toString());
				}
	            if (pNamespaceURI.equals(elementParser.getNamespaceURI())  &&
	                pLocalName.equals(elementParser.getLocalName())) {
	                if (activeParser.isFinished()) {
	                    terminateComplexType(pNamespaceURI, pLocalName, pQName,
	                    					 elementParser);
	                    return;
	                }
	            }
				break;
			case STATE_SIMPLE_ATOMIC: {
				String s = sb.toString();
				resetAtomicState();
				activeParser.endElement(pNamespaceURI, pLocalName, pQName, s);
				return;
			}
			default:
				throw new IllegalStateException("Invalid state: " + state);
        }
        QName qName = new QName(pNamespaceURI, pLocalName);
        validationEvent(ValidationEvent.WARNING,
                        "Unexpected end element: '" + qName + "'",
                        ValidationEvents.EVENT_PREMATURE_END_ELEMENT,
                        null);
    }

    private void terminateComplexType(String pNamespaceURI, String pLocalName, String pQName,
    								  JMSAXElementParser elementParser) throws SAXException {
    	if (removeActiveParser()) {
    		activeParser.endElement(pNamespaceURI, pLocalName, pQName, elementParser.result);
    	} else {
    		if (observer != null) {
    			observer.notify(result);
    		}
    	}
    }

    public void characters(char[] pChars, int pOffset, int pLen) throws SAXException {
        switch (state) {
			case STATE_SIMPLE_ATOMIC:
				sb.append(pChars, pOffset, pLen);
				return;
			case STATE_COMPLEX_CONTENT:
				if (((JMSAXElementParser) activeParser).isAtomic()) {
					sb.append(pChars, pOffset, pLen);
					return;
				}
				// Fall through
			case STATE_GROUP:
				activeParser.addText(pChars, pOffset, pLen);
				break;
			default:
				throw new IllegalStateException("Invalid state: " + state);
		}
    }

    public void ignorableWhitespace(char[] pChars, int pStart, int pLen) throws SAXException {
		characters(pChars, pStart, pLen);
    }

    public void processingInstruction(String pTarget, String pData) throws SAXException {
		validationEvent(ValidationEvent.WARNING,
                        "Don't know how to handle processing instructions.",
                        ValidationEvents.EVENT_PROCESSING_INSTRUCTION,
                        null);
    }

	/** Posts a {@link javax.xml.bind.ParseConversionEvent}.
	 */
	public void parseConversionEvent(String pMsg, Exception pException) throws SAXException {
		ParseConversionEventImpl event = new ParseConversionEventImpl(ValidationEvent.FATAL_ERROR, pMsg, null);
		if (locator != null) {
			event.setLocator(new ValidationEventLocatorImpl(locator));
		}
		handleEvent(event, pException);
	}

	/** Posts a {@link ValidationEvent}.
	 */
	public void validationEvent(int pSeverity, String pMsg, String pErrorCode,
								Exception pException) throws SAXException {
		validationEvent(pSeverity, pMsg, pErrorCode, pException, locator);
	}

	/** Posts a {@link ValidationEvent}.
	 */
	public void validationEvent(int pSeverity, String pMsg, String pErrorCode,
								Exception pException, Locator pLocator) throws SAXException {
		org.apache.ws.jaxme.impl.ValidationEventImpl event = new org.apache.ws.jaxme.impl.ValidationEventImpl(pSeverity, pMsg, null);
		event.setErrorCode(pErrorCode);
		if (pLocator != null) {
			event.setLocator(new ValidationEventLocatorImpl(pLocator));
		}
		handleEvent(event, pException);
	}

	private void handleEvent(ValidationEventImpl pEvent, Exception pException) throws SAXException {
		if (locator != null) {
			pEvent.setLocator(new ValidationEventLocatorImpl(locator));
		}
		if (pException != null) {
			pEvent.setLinkedException(pException);
		}
		ValidationEventHandler eventHandler;
		try {
			eventHandler = unmarshaller.getEventHandler();
		} catch (JAXBException e) {
			throw new SAXException(e);
		}
		if (eventHandler == null  ||  !eventHandler.handleEvent(pEvent)) {
			if (pException != null  &&  pException instanceof SAXParseException) {
				throw (SAXParseException) pException;
			}
			String msg = pEvent.getMessage();
			if (pEvent instanceof org.apache.ws.jaxme.impl.ValidationEventImpl) {
				String errorCode = ((org.apache.ws.jaxme.impl.ValidationEventImpl) pEvent).getErrorCode();
				if (errorCode != null) {
					msg = errorCode + ": " + msg;
				}
			}
			throw new SAXParseException(msg, locator, pException);
		}
	}

	public void skippedEntity(String pName) throws SAXException {
		validationEvent(ValidationEvent.WARNING,
                        "Don't know how to handle skipped entities.",
                        ValidationEvents.EVENT_SKIPPED_ENTITY,
                        null);
    }

    public Object getResult() throws JAXBException, IllegalStateException {
		if (groupParsers.size() > 0  ||  result == null) {
			throw new IllegalStateException("Parsing the element is not yet finished.");
		}
		return result;
    }

	public NamespaceSupport getNamespaceSupport() {
		return nss;
	}

	public Locator getDocumentLocator() {
		return locator;
	}

	public DatatypeConverterInterface getDatatypeConverter() {
		return unmarshaller.getDatatypeConverter();
	}

	/** Indicates, that the handler is parsing a simple, atomic element.
	 */
	public void addSimpleAtomicState() {
		activeParser = null;
		sb.setLength(0);
		state = STATE_SIMPLE_ATOMIC;
	}

	/** Restores the state after parsing an atomic element.
	 */
	private void resetAtomicState() {
		activeParser = (JMSAXGroupParser) groupParsers.get(groupParsers.size()-1);
		if (activeParser instanceof JMSAXElementParser) {
			state = STATE_COMPLEX_CONTENT;
		} else {
			state = STATE_GROUP;
		}
	}
	
	/**
	 * @param id XML ID attribute of JAXB object
	 * @param jaxbObject the JAXB object
	 * @throws SAXException if duplicate ID
	 */
	public boolean addId(String id, Object jaxbObject) throws SAXException {
		if (idMap.containsKey(id)) {
			return false;
		}
		idMap.put(id, jaxbObject);
		return true;
	}
	
	/**
	 * @param idref
	 */
	public void addIdref(IDREF idref) {
		idrefs.add(idref);
	}
	
}
