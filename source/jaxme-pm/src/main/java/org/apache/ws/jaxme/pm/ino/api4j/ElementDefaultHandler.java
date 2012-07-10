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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.ws.jaxme.pm.ino.InoObject;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import com.softwareag.tamino.db.api.objectModel.sax.TSAXElement;
import com.softwareag.tamino.db.api.objectModel.sax.TSAXElementDefaultHandler;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class ElementDefaultHandler extends TSAXElementDefaultHandler {
    static class Data {
        int level = 0;
        boolean inDocument = false;
        boolean iHaveCreatedStartDocument = false;
        final List result = new ArrayList();
    }
    static ThreadLocal data = new ThreadLocal();
    static void initData() {
        data.set(new Data());
    }
    static void resetData() {
        data.set(null);
    }

    private Data getData() {
        return (Data) data.get();
    }

    public TSAXElement getFirstElement() {
        List result = getData().result;
        if (result.size() > 0) {
            return new TJMElement((InoObject) result.get(0));
        } else {
            return null;
        }
    }

    public Iterator getElementIterator() {
        return new Iterator(){
            Iterator inner = getData().result.iterator();
            public void remove() {
                inner.remove();
            }
            public boolean hasNext() {
                return inner.hasNext();
            }
            public Object next() {
                return new TJMElement((InoObject) inner.next());
            }
        };
    }

    public void startDocument() throws SAXException {
        Data d = getData();
        d.iHaveCreatedStartDocument = false;
        d.inDocument = true;
        DocumentDefaultHandler.getUnmarshallerHandler().startDocument();
    }

    public void endDocument() throws SAXException {
        Data d = getData();
        if (d.inDocument) {
            DocumentDefaultHandler.getUnmarshallerHandler().endDocument();
	        try {
	            d.result.add(DocumentDefaultHandler.getUnmarshallerHandler().getResult());
	        } catch (JAXBException e) {
	            throw new SAXException(e);
	        }
	        d.inDocument = false;
	        d.iHaveCreatedStartDocument = false;
        }
    }

    public void startElement(String pNamespaceURI, String pLocalName, String pQName,
							 Attributes pAttr) throws SAXException {
        Data d = getData();
        if (d.level++ == 0  &&  !d.inDocument) {
            startDocument();
            d.iHaveCreatedStartDocument = true;
        }
        DocumentDefaultHandler.getUnmarshallerHandler().startElement(pNamespaceURI, pLocalName, pQName, pAttr);
    }

    public void endElement(String pNamespaceURI, String pLocalName, String pQName)
    		throws SAXException {
        Data d = getData();
        DocumentDefaultHandler.getUnmarshallerHandler().endElement(pNamespaceURI, pLocalName, pQName);
        if (--d.level == 0  &&  d.iHaveCreatedStartDocument) {
            endDocument();
        }
    }

    public void characters(char[] pBuffer, int pOffset, int pLen) throws SAXException {
        Data d = getData();
        if (d.inDocument) {
            DocumentDefaultHandler.getUnmarshallerHandler().characters(pBuffer, pOffset, pLen);
        }
    }

    public void ignorableWhitespace(char[] pBuffer, int pOffset, int pLen) throws SAXException {
        Data d = getData();
        if (d.inDocument) {
            DocumentDefaultHandler.getUnmarshallerHandler().ignorableWhitespace(pBuffer, pOffset, pLen);
        }
    }

    public void processingInstruction(String pTarget, String pData) throws SAXException {
        Data d = getData();
        if (d.inDocument) {
            DocumentDefaultHandler.getUnmarshallerHandler().processingInstruction(pTarget, pData);
        }
    }

    public void skippedEntity(String pEntity) throws SAXException {
        Data d = getData();
        if (d.inDocument) {
            DocumentDefaultHandler.getUnmarshallerHandler().skippedEntity(pEntity);
        }
    }

    public void endPrefixMapping(String pPrefix) throws SAXException {
        Data d = getData();
        if (d.inDocument) {
            DocumentDefaultHandler.getUnmarshallerHandler().endPrefixMapping(pPrefix);
        }
    }

    public void startPrefixMapping(String pPrefix, String pNamespaceURI) throws SAXException {
        Data d = getData();
        if (d.inDocument) {
            DocumentDefaultHandler.getUnmarshallerHandler().startPrefixMapping(pPrefix, pNamespaceURI);
        }
    }

    public void setDocumentLocator(Locator pLocator) {
        Data d = getData();
        if (d.inDocument) {
            DocumentDefaultHandler.getUnmarshallerHandler().setDocumentLocator(pLocator);
        }
    }
}
