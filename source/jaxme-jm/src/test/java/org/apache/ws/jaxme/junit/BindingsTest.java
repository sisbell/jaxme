/*
 * Copyright 2006  The Apache Software Foundation
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

import java.io.File;

import junit.framework.TestCase;

import org.apache.ws.jaxme.generator.Generator;
import org.apache.ws.jaxme.generator.SchemaReader;
import org.apache.ws.jaxme.generator.impl.GeneratorImpl;
import org.apache.ws.jaxme.generator.sg.impl.JAXBSchemaReader;
import org.apache.ws.jaxme.test.bindings.imported.vo.ImpFooType;
import org.apache.ws.jaxme.test.bindings.vo.FooType;
import org.apache.ws.jaxme.test.bindings.vo.ObjectFactory;
import org.apache.ws.jaxme.test.bindings.vo.YesNoTypeClass;
import org.xml.sax.InputSource;


/**
 * Tests using external binding files.
 */
public class BindingsTest extends TestCase {
	/**
	 * Tests running the generator with a binding file.
	 */
	public void testBindingGenerator() throws Exception {
		Generator g = new GeneratorImpl();
		g.setTargetDirectory(new File("target/tests/binding/src"));
		g.setResourceTargetDirectory(new File("target/tests/binding/src"));
		g.addBindings(new InputSource(new File("src/test/xsd/bindings/bindings.jxb").toURI().toURL().toExternalForm()));
		SchemaReader sr = new JAXBSchemaReader();
		sr.setGenerator(g);
		g.setSchemaReader(sr);
		g.generate(new File("src/test/xsd/bindings/bindings.xsd"));
	}

	/**
	 * Tests, whether the binding has moved the schema to the proper
	 * package and whether the string constants have been created.
	 */
	public void testBindings() throws Exception {
		FooType foo = new ObjectFactory().createFoo();
		foo.setBar(YesNoTypeClass.YES);
		foo.setBool(Boolean.TRUE);
		ImpFooType impFoo = new org.apache.ws.jaxme.test.bindings.imported.vo.ObjectFactory().createImpFoo();
		impFoo.setXyz(org.apache.ws.jaxme.test.bindings.imported.vo.YesNoTypeClass.NO);
		impFoo.setRequiredLong(1L);
        impFoo.setOptionalLong(new Long(1));
	}
}
