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

import java.io.StringReader;
import java.util.List;

import org.apache.ws.jaxme.js.JavaSource;
import org.apache.ws.jaxme.js.JavaSourceFactory;
import org.apache.ws.jaxme.js.util.JavaParser;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import junit.framework.TestCase;

/**
 * Test for JavaParser.
 */
public class AntlrTest extends TestCase {
	/**
	 * Tests the JavaParser.
	 */
	public void test() throws Throwable {
		final String path = System.getProperty("js.src")
			+ "/org/apache/ws/jaxme/sqls/impl/SQLGeneratorImpl.java";
		org.apache.ws.jaxme.js.util.JavaParser.main(new String[]{path});
	}

    /** Tests, whether imports may use wildcards.
     */
	public void testStarImport() throws RecognitionException, TokenStreamException {
	    JavaSourceFactory jsf = new JavaSourceFactory();
	    JavaParser parser = new JavaParser(jsf);
	    List a = parser.parse(new StringReader("package foo;\nimport java.util.*;\n\npublic class Test {}\n"));

	    assertEquals("Expecting 1 Java source", 1, a.size());
	    JavaSource js = (JavaSource) a.get(0);
	    assertEquals("Expecting 1 import", 1, js.getImports().length);
	    assertEquals("java.util.*", js.getImports()[0].toString());
	}
}
