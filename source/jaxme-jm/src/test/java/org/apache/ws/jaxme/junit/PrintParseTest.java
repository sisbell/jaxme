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

import org.apache.ws.jaxme.impl.JMMarshallerImpl;
import org.apache.ws.jaxme.tests.printparse.Test;
import org.apache.ws.jaxme.tests.printparse.impl.TestImpl;
import org.xml.sax.InputSource;

import junit.framework.TestCase;


/** Test case for the
 * <code>printMethod</code> and <code>parseMethod</code>
 * attributes in <code>jaxb:javaType</code>.
 */
public class PrintParseTest extends TestCase {
	private String getNamespace() {
		TestImpl test = new TestImpl();
        return test.getQName().getNamespaceURI();
    }

    private String getPackageName() {
		String testClassName = Test.class.getName();
        int offset = testClassName.lastIndexOf('.');
        return testClassName.substring(0, offset);
    }

    private JAXBContext getJAXBContext() throws JAXBException {
    	return JAXBContext.newInstance(getPackageName());
    }

    /** Tests the use of <code>jaxb:javaType/@print</code>.
     */
    public void testPrint() throws Exception {
    	boolean[] bools = new boolean[]{false, true};
        int[] ints = new int[]{0,1};
        for (int i = 0;  i < bools.length;  i++) {
            Test test = new TestImpl();
            test.setBool(bools[i]);
            StringWriter sw = new StringWriter();
            Marshaller m = getJAXBContext().createMarshaller();
            m.setProperty(JMMarshallerImpl.JAXME_XML_DECLARATION, Boolean.FALSE);
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);
            m.marshal(test, sw);
            String expect =
              "<Test xmlns=\"" + getNamespace() + "\"><Bool>" + ints[i] +
              "</Bool></Test>";
            String got = sw.toString();
            assertEquals(expect, got);
        }
    }

    /** Tests the use of <code>jaxb:javaType/@parse</code>.
     */
    public void testParse() throws Exception {
        boolean[] bools = new boolean[]{false, true};
        int[] ints = new int[]{0,1};
        for (int i = 0;  i < bools.length;  i++) {
            String input =
                "<Test xmlns=\"" + getNamespace() + "\"><Bool>" + ints[i] +
                "</Bool></Test>";
            Unmarshaller u = getJAXBContext().createUnmarshaller();
            Test test = (Test) u.unmarshal(new InputSource(new StringReader(input)));
            assertEquals(bools[i], test.isBool());
        }
    }
}
