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
 
package org.apache.ws.jaxme.pm.ino.api4j;

import java.lang.reflect.UndeclaredThrowableException;

import javax.xml.bind.JAXBException;

import org.apache.ws.jaxme.impl.JMUnmarshallerHandlerImpl;
import org.apache.ws.jaxme.pm.ino.InoObject;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import com.softwareag.tamino.db.api.objectModel.sax.TSAXDocument;
import com.softwareag.tamino.db.api.objectModel.sax.TSAXDocumentDefaultHandler;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class DocumentDefaultHandler extends TSAXDocumentDefaultHandler {
    private static final ThreadLocal handler = new ThreadLocal();

    static JMUnmarshallerHandlerImpl getUnmarshallerHandler() {
        return (JMUnmarshallerHandlerImpl) handler.get();
    }

    static void setUnmarshallerHandler(JMUnmarshallerHandlerImpl pHandler) {
        handler.set(pHandler);
    }

    public void startDocument() throws SAXException {
        getUnmarshallerHandler().startDocument();
    }

    public void endDocument() throws SAXException {
        getUnmarshallerHandler().endDocument();
    }

    public void startElement(String pNamespaceURI, String pLocalName, String pQName,
							 Attributes pAttr) throws SAXException {
        getUnmarshallerHandler().startElement(pNamespaceURI, pLocalName, pQName, pAttr);
    }

    public void endElement(String pNamespaceURI, String pLocalName, String pQName)
    		throws SAXException {
        getUnmarshallerHandler().endElement(pNamespaceURI, pLocalName, pQName);
    }

    public void characters(char[] pBuffer, int pOffset, int pLen) throws SAXException {
        getUnmarshallerHandler().characters(pBuffer, pOffset, pLen);
    }

    public void ignorableWhitespace(char[] pBuffer, int pOffset, int pLen) throws SAXException {
        getUnmarshallerHandler().ignorableWhitespace(pBuffer, pOffset, pLen);
    }

    public void processingInstruction(String pTarget, String pData) throws SAXException {
        getUnmarshallerHandler().processingInstruction(pTarget, pData);
    }

    public void skippedEntity(String pEntity) throws SAXException {
        getUnmarshallerHandler().skippedEntity(pEntity);
    }

    public void endPrefixMapping(String pPrefix) throws SAXException {
        getUnmarshallerHandler().endPrefixMapping(pPrefix);
    }

    public void startPrefixMapping(String pPrefix, String pNamespaceURI) throws SAXException {
        getUnmarshallerHandler().startPrefixMapping(pPrefix, pNamespaceURI);
    }

    public void setDocumentLocator(Locator pLocator) {
        getUnmarshallerHandler().setDocumentLocator(pLocator);
    }

    public TSAXDocument getDocument() {
        try {
	        return new TJMElement((InoObject) getUnmarshallerHandler().getResult());
        } catch (JAXBException e) {
            throw new UndeclaredThrowableException(e);
        }
    }
}
