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


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class SyntaxTest extends XSTestCase {
	/**
	 * Tests parsing the structures.xsd schema.
	 */
	public void testStructureSyntax() throws Exception {
		parseSyntax("structures.xsd");
	}

	/**
	 * Tests syntax parsing of the datatypes.xsd schema.
	 */
	public void testDatatypesSyntax() throws Exception {
		parseSyntax("datatypes.xsd");
	}

	/**
	 * Tests syntax parsing the xml.xsd schema.
	 */
	public void testXmlSyntax() throws Exception {
		parseSyntax("xml.xsd");
	}

	/**
	 * Tests logical parsing the structures.xsd schema.
	 */
	public void testStructureLogical() throws Exception {
		parseLogical("structures.xsd");
	}
}
