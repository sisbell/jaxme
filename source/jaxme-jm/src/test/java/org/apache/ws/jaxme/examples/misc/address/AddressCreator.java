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
package org.apache.ws.jaxme.examples.misc.address;


import java.io.FileWriter;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.ws.jaxme.impl.JMMarshallerImpl;
import org.apache.ws.jaxme.test.misc.address.Address;
import org.apache.ws.jaxme.test.misc.address.ObjectFactory;
import org.apache.ws.jaxme.test.misc.address.AddressType.EmailDetailsType.EmailType;
import org.apache.ws.jaxme.test.misc.address.AddressType.PhoneDetailsType.PhoneType;


public class AddressCreator {
	public static void writeAddress(Writer pWriter) throws JAXBException {
		ObjectFactory f = new ObjectFactory();
		
		// Create the element:
		Address addr = f.createAddress();
		addr.setName(f.createAddressTypeNameType());
		addr.getName().setFirst("Jane");
		addr.getName().setLast("Doe");
		addr.setPostal(f.createAddressTypePostalType());
		addr.getPostal().setStreet("34 Main Street");
		addr.getPostal().setCity("Boston");
		addr.getPostal().setState("MA");
		addr.getPostal().setZIP("02215");
		addr.setEmailDetails(f.createAddressTypeEmailDetailsType());
		
		EmailType email = f.createAddressTypeEmailDetailsTypeEmailType();
		email.setType("Private");
		email.setEmailAddress("jdoe@yourcompany.com");
		addr.getEmailDetails().getEmail().add(email);
		email = f.createAddressTypeEmailDetailsTypeEmailType();
		email.setType("Office");
		email.setEmailAddress("josephdoe@mycompany.com");
		addr.getEmailDetails().getEmail().add(email);
		email = f.createAddressTypeEmailDetailsTypeEmailType();
		email.setType("Office");
		email.setEmailAddress("joseph.doe@mycompany.com");
		addr.getEmailDetails().getEmail().add(email);
		
		addr.setPhoneDetails(f.createAddressTypePhoneDetailsType());
		PhoneType phone = f.createAddressTypePhoneDetailsTypePhoneType();
		phone.setType("Work");
		phone.setPhoneNumber("555.6789");
		addr.getPhoneDetails().getPhone().add(phone);
		phone = f.createAddressTypePhoneDetailsTypePhoneType();
		phone.setType("Fax");
		phone.setPhoneNumber("555.1212");
		addr.getPhoneDetails().getPhone().add(phone);
		
		// And save it into the file "Address.xml"
		JAXBContext context = JAXBContext.newInstance("org.apache.ws.jaxme.test.misc.address");
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(JMMarshallerImpl.JAXME_XML_DECLARATION, Boolean.FALSE);
		marshaller.marshal(addr, pWriter);
	}
	
	public static void main(String[] args) throws Exception {
		FileWriter fw = new FileWriter("Address.xml");
		writeAddress(fw);
		fw.close();
	}
}
