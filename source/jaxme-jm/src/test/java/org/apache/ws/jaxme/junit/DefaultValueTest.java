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
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBContext;

import org.apache.ws.jaxme.test.misc.defaults.Persons;
import org.apache.ws.jaxme.test.misc.defaults.PersonsType.PersonType;
import org.apache.ws.jaxme.test.misc.defaults.PersonsType.PersonType.NameType;
import org.apache.ws.jaxme.test.misc.defaults.impl.PersonsTypeImpl.PersonTypeImpl.NameTypeImpl;
import org.xml.sax.InputSource;

/**
 * @author <a href="mailto:iasandcb@tmax.co.kr">Ias</a>
 */
public class DefaultValueTest extends BaseTestCase {
  public DefaultValueTest(String pName) {
    super(pName);
  }

  private String getPersons() throws Exception {
    return "<Persons xmlns=\"http://ws.apache.org/jaxme/test/misc/defaults\">\n"
      + "  <Person>\n"
      + "    <Name>\n"
      + "      <Last>Lee</Last>\n"
      + "    </Name>\n"
      + "  </Person>\n"
      + "  <Person Alias=\"Cb\">\n"
      + "    <Age>30</Age>\n"
      + "  </Person>\n"
      + "</Persons>";
  }

  public void testDefaults() throws Exception {
    String persons = getPersons();
    InputSource isource = new InputSource(new StringReader(persons));
    JAXBContext context =
      JAXBContext.newInstance("org.apache.ws.jaxme.test.misc.defaults");
    Persons unmarshalledPersons =
      (Persons) context.createUnmarshaller().unmarshal(isource);
    List personList = unmarshalledPersons.getPerson();
    Iterator i = personList.iterator();
    i.hasNext();
    PersonType person = (PersonType) i.next();
    NameType name = person.getName();
    assertEquals("Anonymous", name.getFirst());
    assertEquals("Lee", name.getLast());
    assertEquals("Ias", person.getAlias());
    assertEquals(25, person.getAge());

    i.hasNext();
    person = (PersonType) i.next();
    name = person.getName();
    if (name == null) {
        name = new NameTypeImpl();
    }
    assertEquals("Anonymous", name.getFirst());
    assertEquals(null, name.getLast());
    assertEquals("Cb", person.getAlias());
    assertEquals(30, person.getAge());
  }
}
