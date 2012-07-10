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
package org.apache.ws.jaxme.junit;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.Element;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

import org.apache.ws.jaxme.WildcardAttribute;
import org.apache.ws.jaxme.impl.JMMarshallerImpl;
import org.apache.ws.jaxme.impl.OrderedAttributeXMLWriter;
import org.apache.ws.jaxme.test.misc.wildcards.AnyAttribute;
import org.apache.ws.jaxme.test.misc.wildcards.ListAttribute;
import org.apache.ws.jaxme.test.misc.wildcards.ObjectFactory;
import org.apache.ws.jaxme.test.misc.wildcards.OtherAttribute;
import org.xml.sax.InputSource;


/** <p>Test case for wildcard attributes and elements.</p>
 */
public class WildcardTest extends BaseTestCase {
    public WildcardTest(String pName) {
        super(pName);
    }

    protected JAXBContext getJAXBContext() throws JAXBException {
        return JAXBContext.newInstance("org.apache.ws.jaxme.test.misc.wildcards");
    }

    protected String asString(Element pElement) throws JAXBException {
        Marshaller marshaller = getJAXBContext().createMarshaller();
        marshaller.setProperty(JMMarshallerImpl.JAXME_XML_DECLARATION, Boolean.FALSE);
        marshaller.setProperty(JMMarshallerImpl.JAXME_XML_WRITER, OrderedAttributeXMLWriter.class);
        StringWriter sw = new StringWriter();
        marshaller.marshal(pElement, sw);
        return sw.toString();
    }

    protected String getMarshalledAnyAttribute() throws JAXBException {
        ObjectFactory objectFactory = new ObjectFactory();
        AnyAttribute anyAttribute = objectFactory.createAnyAttribute();
        anyAttribute.setAnyAttribute(new QName("foo", "bar"), "value 1");
        anyAttribute.setAnyAttribute(new QName("baz"), "value 2");
        return asString(anyAttribute);
    }

    protected String getMarshalledListAttribute() throws JAXBException {
        ObjectFactory objectFactory = new ObjectFactory();
        ListAttribute listAttribute = objectFactory.createListAttribute();
        listAttribute.setAnyAttribute(new QName("http://ws.apache.org/jaxme/test/misc/wildcards/2", "foo"), "value 1");
        listAttribute.setAnyAttribute(new QName("http://ws.apache.org/jaxme/test/misc/wildcards", "bar"), "value 2");
        return asString(listAttribute);
    }

    protected String getMarshalledOtherAttribute() throws JAXBException {
        ObjectFactory objectFactory = new ObjectFactory();
        OtherAttribute otherAttribute = objectFactory.createOtherAttribute();
        otherAttribute.setAnyAttribute(new QName("foo", "bar"), "value 1");
        otherAttribute.setAnyAttribute(new QName("baz"), "value 2");
        return asString(otherAttribute);
    }

    protected Element getUnmarshalledElement(String pMarshalledElement) throws JAXBException {
        Unmarshaller unmarshaller = getJAXBContext().createUnmarshaller();
        return (Element) unmarshaller.unmarshal(new InputSource(new StringReader(pMarshalledElement)));
    }

    public void testMarshalAnyAttribute() throws Exception {
        String got = getMarshalledAnyAttribute();
        int offsetEx = got.indexOf("xmlns:ex");
        int offsetP = got.indexOf("xmlns:p");
        final String expect;
        if (offsetEx < offsetP) {
        	expect = "<ex:AnyAttribute p:bar=\"value 1\" baz=\"value 2\" xmlns:ex=\"http://ws.apache.org/jaxme/test/misc/wildcards\" xmlns:p=\"foo\"/>";
        } else {
        	expect = "<ex:AnyAttribute p:bar=\"value 1\" baz=\"value 2\" xmlns:p=\"foo\" xmlns:ex=\"http://ws.apache.org/jaxme/test/misc/wildcards\"/>";
        }
        assertEquals(expect, got);
    }

    protected void assertEquals(WildcardAttribute[] pExpect, WildcardAttribute[] pGot) {
        assertEquals(pExpect.length, pGot.length);
        Map mapGot = new HashMap();
        for (int i = 0;  i < pGot.length;  i++) {
            mapGot.put(pGot[i].getName(), pGot[i].getValue());
        }
        if (mapGot.size() < pGot.length) {
            fail("Expected " + pGot.length + " elements in result Map, got " + mapGot.size());
        }
        for (int i = 0;  i < pExpect.length;  i++) {
            WildcardAttribute wa = pExpect[i];
            String value = (String) mapGot.get(wa.getName());
            if (value == null) {
                fail("Expected name " + wa.getName() + " in result Map.");
            } else {
                assertEquals(wa.getValue(), value);
            }
        }
    }

    public void testUnmarshalAnyAttribute() throws Exception {
        AnyAttribute anyAttribute = (AnyAttribute) getUnmarshalledElement(getMarshalledAnyAttribute());
        WildcardAttribute[] attrs = anyAttribute.getAnyAttributeArray();
        assertEquals(new WildcardAttribute[]{
			new WildcardAttribute(new QName("foo", "bar"), "value 1"),
			new WildcardAttribute(new QName("baz"), "value 2"),
		}, attrs);
    }

    public void testMarshalListAttribute() throws Exception {
        String got = getMarshalledListAttribute();
        int offsetEx = got.indexOf("xmlns:ex");
        int offsetP = got.indexOf("xmlns:p");
        final String expect;
        if (offsetEx < offsetP) {
        	expect = "<ex:ListAttribute p:foo=\"value 1\" ex:bar=\"value 2\" xmlns:ex=\"http://ws.apache.org/jaxme/test/misc/wildcards\" xmlns:p=\"http://ws.apache.org/jaxme/test/misc/wildcards/2\"/>";
        } else {
        	expect = "<ex:ListAttribute p:foo=\"value 1\" ex:bar=\"value 2\" xmlns:p=\"http://ws.apache.org/jaxme/test/misc/wildcards/2\" xmlns:ex=\"http://ws.apache.org/jaxme/test/misc/wildcards\"/>";
        }
        assertEquals(expect, got);
    }

    public void testUnmarshalListAttribute() throws Exception {
        ListAttribute listAttribute = (ListAttribute) getUnmarshalledElement(getMarshalledListAttribute());
        WildcardAttribute[] attrs = listAttribute.getAnyAttributeArray();
        assertEquals(new WildcardAttribute[]{
			new WildcardAttribute(new QName("http://ws.apache.org/jaxme/test/misc/wildcards/2", "foo"), "value 1"),
			new WildcardAttribute(new QName("http://ws.apache.org/jaxme/test/misc/wildcards", "bar"), "value 2")
		}, attrs);
    }

    public void testMarshalOtherAttribute() throws Exception {
        String got = getMarshalledOtherAttribute();
        int offsetEx = got.indexOf("xmlns:ex");
        int offsetP = got.indexOf("xmlns:p");
        final String expect;
        if (offsetEx < offsetP) {
        	expect = "<ex:OtherAttribute p:bar=\"value 1\" baz=\"value 2\" xmlns:ex=\"http://ws.apache.org/jaxme/test/misc/wildcards\" xmlns:p=\"foo\"/>";
        } else {
        	expect = "<ex:OtherAttribute p:bar=\"value 1\" baz=\"value 2\" xmlns:p=\"foo\" xmlns:ex=\"http://ws.apache.org/jaxme/test/misc/wildcards\"/>";
        }
        assertEquals(expect, got);
    }

    public void testUnmarshalOtherAttribute() throws Exception {
        OtherAttribute otherAttribute = (OtherAttribute) getUnmarshalledElement(getMarshalledOtherAttribute());
        WildcardAttribute[] attrs = otherAttribute.getAnyAttributeArray();
        assertEquals(new WildcardAttribute[]{
			new WildcardAttribute(new QName("foo", "bar"), "value 1"),
			new WildcardAttribute(new QName("baz"), "value 2")
        }, attrs);
    }
}
