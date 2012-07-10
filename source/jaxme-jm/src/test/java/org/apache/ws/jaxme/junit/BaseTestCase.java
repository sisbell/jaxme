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
package org.apache.ws.jaxme.junit;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;

import junit.framework.TestCase;

import org.apache.ws.jaxme.JMElement;
import org.apache.ws.jaxme.impl.JMMarshallerImpl;
import org.apache.ws.jaxme.impl.ValidationEventImpl;
import org.xml.sax.InputSource;


/** <p>A base class for JUnit tests.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 * @version $Id: BaseTestCase.java 453572 2006-10-06 12:19:58Z jochen $
 */
public abstract class BaseTestCase extends TestCase {
	protected static class EventDetector implements ValidationEventHandler {
		private ValidationEvent event;
		public boolean handleEvent(ValidationEvent pEvent) {
			if (event == null) {
				event = pEvent;
			}
			return true;
		}
		boolean isSuccess() { return event == null; }
		ValidationEventImpl getEvent() { return (ValidationEventImpl) event; }
	}

    protected BaseTestCase() {
    }

    protected BaseTestCase(String pName) {
    	super(pName);
    }

    protected BaseTestCase(Class c) {
    	this(c.getName());
    }

    protected String getNamespaceURI(JMElement pElement) {
    	return pElement.getQName().getNamespaceURI();
    }

    protected String getPackageName(Class pClass) {
        String className = pClass.getName();
        int offset = className.lastIndexOf('.');
        if (offset == -1) {
        	throw new IllegalStateException("Unable to parse package name: " + className);
        } else {
        	return className.substring(0, offset);
        }
    }

    protected JAXBContext getJAXBContext(Class pClass) throws JAXBException {
        return JAXBContext.newInstance(getPackageName(pClass));
    }

    // Asserts equality of the two given byte arrays.
    protected void assertEquals(byte[] pExpect, byte[] pGot) {
    	if (pExpect.length != pGot.length) {
    		fail("Expected " + pExpect.length + " bytes, got " + pGot.length);
    	} else {
    		for (int i = 0;  i < pExpect.length;  i++) {
    			if (pExpect[i] != pGot[i]) {
    				fail("Expected byte " + ((int) pExpect[i]) + " at offset " + i +
    						", got byte " + ((int) pGot[i]));
    			}
    		}
    	}
    }

    protected void unmarshalMarshalUnmarshal(Class pClass, String pXML, boolean pIndenting)
			throws JAXBException {
        Object o = unmarshal(pClass, pXML);
        assertNotNull(o);
        String s = marshal(o, pClass, pIndenting);
        assertEquals(pXML, s);
        Object comp = unmarshal(pClass, s);
        assertNotNull(comp);
    }

	protected void unmarshalMarshalUnmarshal(Class pClass, String pXML) throws JAXBException {
		unmarshalMarshalUnmarshal(pClass, pXML, true);
    }

    protected Object unmarshal(Class pClass, String pXML) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(getPackageName(pClass));
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return unmarshaller.unmarshal(new InputSource(new StringReader(pXML)));
    }

    protected String marshal(Object o, Class pClass) throws JAXBException {
		return marshal(o, pClass, true);
    }

	protected String marshal(Object o, Class pClass, boolean pIndenting) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(getPackageName(pClass));
        Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, pIndenting ? Boolean.TRUE : Boolean.FALSE);
        marshaller.setProperty(JMMarshallerImpl.JAXME_XML_DECLARATION, Boolean.FALSE);
        StringWriter sw = new StringWriter();
        marshaller.marshal(o, sw);
        return sw.toString();
    }
}
