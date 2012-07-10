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


import java.io.File;
import java.io.FileInputStream;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.ws.jaxme.test.misc.address.Address;
import org.xml.sax.InputSource;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 * @version $Id: AddressPrinter.java 231785 2004-02-16 23:39:59Z jochen $
 */
public class AddressPrinter {
  public static Address getAddress(InputSource pSource) throws JAXBException {
    JAXBContext context = JAXBContext.newInstance("org.apache.ws.jaxme.test.misc.address");
    Unmarshaller unmarshaller = context.createUnmarshaller();
    return (Address) unmarshaller.unmarshal(pSource);
  }

  public static String getAddressAsString(Address pAddress) throws JAXBException {
    StringWriter sw = new StringWriter();
    JAXBContext context = JAXBContext.newInstance("org.apache.ws.jaxme.test.misc.address");
    Marshaller marshaller = context.createMarshaller();
    marshaller.marshal(pAddress, sw);
    return sw.toString();
  }

  public static void main(String[] args) throws Exception {
    File f = new File("Address.xml");
    InputSource isource = new InputSource(new FileInputStream(f));
    isource.setSystemId(f.toURL().toString());
    Address addr = getAddress(isource);

    // A simpler variant might be:
    // Address addr = unmarshaller.unmarshal(f);

    if (addr.getName() == null) {
      System.out.println("Loaded address without name.");
    } else {
      System.out.println("Loaded address " + addr.getName().getLast() +
                         ", " + addr.getName().getFirst() + ".");
    }
    System.out.println("Details:" + getAddressAsString(addr));
  }
}
