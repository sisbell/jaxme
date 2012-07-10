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

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;


/** Interface of an object, which is able to convert another
 * object into SAX events.
 */
public interface JMSAXDriver {
	/** Returns the objects attributes.
	 */
	public AttributesImpl getAttributes(JMSAXDriverController pController, Object pObject) throws SAXException;

	/** Creates the objects content into SAX events, which
	 * are being fired into the given content handler.
	 */
	public void marshalChilds(JMSAXDriverController pController, ContentHandler pHandler, Object pObject) throws SAXException;

	/** Returns a suggested prefix for the namespace
	 * <code>pNamespaceURI</code>, or null, if no suggestion
	 * is available.
	 */
	public String getPreferredPrefix(String pNamespaceURI);
}
