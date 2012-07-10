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
package org.apache.ws.jaxme.js.junit;

import junit.framework.TestCase;


/** <p>This example demonstrates implementation of multiple
 * inheritance with the ProxyGenerator. The class MyObservableList,
 * an extension of {@link org.apache.ws.jaxme.js.junit.ObservableList},
 * is a subclass of {@link java.util.Observable}, but can also be viewed
 * as a subclass of {@link java.util.ArrayList} (or whatever
 * implementation of {@link java.util.List} you choose in the constructor.
 * The {@link java.util.Observer Observers} are notified whenever an object
 * is added to the list.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 * @version $Id: TypesafeEnumerationTest.java 231785 2004-02-16 23:39:59Z jochen $
 */
public class TypesafeEnumerationTest extends TestCase {
  public TypesafeEnumerationTest(String arg0) {
    super(arg0);
  }

  public void testJOE() {
	 assertEquals("JOE", EnumExample.JOE.getName());
	 assertEquals("John Doe", EnumExample.JOE.getValue());
  }

  public void testPOPEYE() {
	 assertEquals("POPEYE", EnumExample.POPEYE.getName());
	 assertEquals("Olivia's Lover", EnumExample.POPEYE.getValue());
  }

  public void testDONALD() {
	 assertEquals("DONALD", EnumExample.DONALD.getName());
	 assertEquals("The Duck King", EnumExample.DONALD.getValue());
  }

  public void testgetInstanceByName() {
	 EnumExample joe = EnumExample.getInstanceByName("JOE");
	 assertEquals(joe, EnumExample.JOE);
	 assertTrue(joe == EnumExample.JOE);
	 EnumExample popeye = EnumExample.getInstanceByName("POPEYE");
	 assertEquals(popeye, EnumExample.POPEYE);
	 assertTrue(popeye == EnumExample.POPEYE);
	 EnumExample donald = EnumExample.getInstanceByName("DONALD");
	 assertEquals(donald, EnumExample.DONALD);
	 assertTrue(donald == EnumExample.DONALD);
	 Throwable ok = null;
	 try {
		EnumExample.getInstanceByName("foo");
	 } catch (Throwable t) {
		ok = t;
	 }
	 assertTrue(ok != null  &&  ok instanceof IllegalArgumentException);
  }

  public void testgetInstanceByValue() {
	 EnumExample joe = EnumExample.getInstanceByValue("John " + "Doe");
	 assertEquals(joe, EnumExample.JOE);
	 assertTrue(joe == EnumExample.JOE);
	 EnumExample popeye = EnumExample.getInstanceByValue("Olivia's " + "Lover");
	 assertEquals(popeye, EnumExample.POPEYE);
	 assertTrue(popeye == EnumExample.POPEYE);
	 EnumExample donald = EnumExample.getInstanceByValue("The Duck" + " King");
	 assertEquals(donald, EnumExample.DONALD);
	 assertTrue(donald == EnumExample.DONALD);
	 Throwable ok = null;
	 try {
		EnumExample.getInstanceByValue("bar");
	 } catch (Throwable t) {
		ok = t;
	 }
	 assertTrue(ok != null  &&  ok instanceof IllegalArgumentException);
  }

  public void testGetInstances() {
  	 EnumExample[] instances = EnumExample.getInstances();
  	 assertEquals(instances.length, 3);
    assertEquals(EnumExample.JOE, instances[0]);
	 assertEquals(EnumExample.POPEYE, instances[1]);
	 assertEquals(EnumExample.DONALD, instances[2]);
  }

  public void testEquals() {
	 assertTrue(!EnumExample.JOE.equals(EnumExample.POPEYE));
	 assertTrue(!EnumExample.JOE.equals(EnumExample.DONALD));
	 assertTrue(!EnumExample.POPEYE.equals(EnumExample.DONALD));
  }

  public void testHashCode() {
	 assertTrue(EnumExample.JOE.hashCode() == EnumExample.JOE.getName().hashCode());
	 assertTrue(EnumExample.POPEYE.hashCode() == EnumExample.POPEYE.getName().hashCode());
	 assertTrue(EnumExample.DONALD.hashCode() == EnumExample.DONALD.getName().hashCode());
  }
}
