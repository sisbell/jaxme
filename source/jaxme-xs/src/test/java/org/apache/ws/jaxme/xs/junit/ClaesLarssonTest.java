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
package org.apache.ws.jaxme.xs.junit;

import org.apache.ws.jaxme.xs.XSComplexType;
import org.apache.ws.jaxme.xs.XSElement;
import org.apache.ws.jaxme.xs.XSGroup;
import org.apache.ws.jaxme.xs.XSParticle;
import org.apache.ws.jaxme.xs.XSSchema;
import org.apache.ws.jaxme.xs.XSType;
import org.apache.ws.jaxme.xs.xml.XsQName;


/**
 * Test for some sample schemas, supplied by Claes Larsson.
 */
public class ClaesLarssonTest extends XSTestCase {
	/**
	 * Basic test.
	 */
	public void testParse() throws Exception {
		XSSchema schema = parseLogical("Claes_Larsson/schema.xsd");

		// Print the names of all global elements:
		XSElement[] elements = schema.getElements();
		assertEquals(154, elements.length);

		XSElement localTransformationElement = schema.getElement(new XsQName((String) null, "local_transformation"));
		assertNotNull(localTransformationElement);
		XSType localTransformationType = localTransformationElement.getType();
		assertTrue(!localTransformationType.isSimple());
		XSComplexType localTransformationComplexType = localTransformationType.getComplexType();
		assertTrue(!localTransformationComplexType.hasSimpleContent());
		XSParticle localTransformationParticle = localTransformationComplexType.getParticle();
		XSGroup localTransformationGroup = localTransformationParticle.getGroup();
		XSParticle[] particles = localTransformationGroup.getParticles();
		assertEquals(0, particles[0].getMinOccurs());
		assertEquals(1, particles[0].getMaxOccurs());
	}
}