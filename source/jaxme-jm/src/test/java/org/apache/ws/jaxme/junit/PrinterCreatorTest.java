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
import javax.xml.bind.Marshaller;

import org.apache.ws.jaxme.examples.misc.address.AddressCreator;
import org.apache.ws.jaxme.examples.misc.address.AddressPrinter;
import org.apache.ws.jaxme.impl.JMMarshallerImpl;
import org.apache.ws.jaxme.test.misc.address.Address;
import org.xml.sax.InputSource;


/** Basic test for marshalling and unmarshalling an
 * Address document.
 */
public class PrinterCreatorTest extends BaseTestCase {
	/** Creates a new instance with the given name.
	 */
	public PrinterCreatorTest(String pName) {
		super(pName);
	}
	
	private String getAddress() throws Exception {
		StringWriter sw = new StringWriter();
		AddressCreator.writeAddress(sw);
		sw.close();
		return "<Address xmlns=\"http://ws.apache.org/jaxme/test/misc/address\">\n" +
		"  <Name>\n" +
		"    <First>Jane</First>\n" +
		"    <Last>Doe</Last>\n" +
		"  </Name>\n" +
		"  <Postal>\n" +
		"    <Street>34 Main Street</Street>\n" +
		"    <ZIP>02215</ZIP>\n" +
		"    <City>Boston</City>\n" +
		"    <State>MA</State>\n" +
		"  </Postal>\n" +
		"  <PhoneDetails>\n" +
		"    <Phone type=\"Work\">\n" +
		"      <PhoneNumber>555.6789</PhoneNumber>\n" +
		"    </Phone>\n" +
		"    <Phone type=\"Fax\">\n" +
		"      <PhoneNumber>555.1212</PhoneNumber>\n" +
		"    </Phone>\n" +
		"  </PhoneDetails>\n" +
		"  <EmailDetails>\n" +
		"    <Email type=\"Private\">\n" +
		"      <EmailAddress>jdoe@yourcompany.com</EmailAddress>\n" +
		"    </Email>\n" +
		"    <Email type=\"Office\">\n" +
		"      <EmailAddress>josephdoe@mycompany.com</EmailAddress>\n" +
		"    </Email>\n" +
		"    <Email type=\"Office\">\n" +
		"      <EmailAddress>joseph.doe@mycompany.com</EmailAddress>\n" +
		"    </Email>\n" +
		"  </EmailDetails>\n" +
		"</Address>";
	}
	
	/** Tests marshalling a JaxMe object.
	 */
	public void testCreate() throws Exception {
		StringWriter sw = new StringWriter();
		AddressCreator.writeAddress(sw);
		sw.close();
		String expect = "<Address xmlns=\"http://ws.apache.org/jaxme/test/misc/address\">\n" +
		"  <Name>\n" +
		"    <First>Jane</First>\n" +
		"    <Last>Doe</Last>\n" +
		"  </Name>\n" +
		"  <Postal>\n" +
		"    <Street>34 Main Street</Street>\n" +
		"    <ZIP>02215</ZIP>\n" +
		"    <City>Boston</City>\n" +
		"    <State>MA</State>\n" +
		"  </Postal>\n" +
		"  <PhoneDetails>\n" +
		"    <Phone type=\"Work\">\n" +
		"      <PhoneNumber>555.6789</PhoneNumber>\n" +
		"    </Phone>\n" +
		"    <Phone type=\"Fax\">\n" +
		"      <PhoneNumber>555.1212</PhoneNumber>\n" +
		"    </Phone>\n" +
		"  </PhoneDetails>\n" +
		"  <EmailDetails>\n" +
		"    <Email type=\"Private\">\n" +
		"      <EmailAddress>jdoe@yourcompany.com</EmailAddress>\n" +
		"    </Email>\n" +
		"    <Email type=\"Office\">\n" +
		"      <EmailAddress>josephdoe@mycompany.com</EmailAddress>\n" +
		"    </Email>\n" +
		"    <Email type=\"Office\">\n" +
		"      <EmailAddress>joseph.doe@mycompany.com</EmailAddress>\n" +
		"    </Email>\n" +
		"  </EmailDetails>\n" +
		"</Address>";
		String got = sw.toString();
		assertEquals(expect, got);
	}
	
	/** Tests unmarshalling a string into t JaxMe object.
	 */
	public void testPrint() throws Exception {
		String address = getAddress();
		InputSource isource = new InputSource(new StringReader(address));
		isource.setSystemId("testPrint.xml");
		Address addr = AddressPrinter.getAddress(isource);
		JAXBContext context = JAXBContext.newInstance("org.apache.ws.jaxme.test.misc.address");
		StringWriter sw = new StringWriter();
		Marshaller m = context.createMarshaller();
		m.setProperty(JMMarshallerImpl.JAXME_XML_DECLARATION, Boolean.FALSE);
		m.marshal(addr, sw);
		sw.close();
		String got = sw.toString();
		assertEquals(address, got);
	}
}
