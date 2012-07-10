/*
 * Copyright 2005  The Apache Software Foundation
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
package org.apache.ws.jaxme.generator.sg.impl.ccsg;

import org.apache.ws.jaxme.generator.sg.ComplexTypeSG;
import org.apache.ws.jaxme.js.JavaMethod;
import org.apache.ws.jaxme.js.JavaSource;
import org.xml.sax.SAXException;


/** Creates an instance of
 * {@link org.apache.ws.jaxme.impl.JMSAXElementParser},
 * which parses an empty element, aka empty group.
 */
public class EmptyElementHandlerSG extends HandlerSGImpl {
	/** Creates a new instance, which generates a handler
	 * for the given complex type.
	 */
	public EmptyElementHandlerSG(ComplexTypeSG pType, JavaSource pJs) {
		super(pType, pJs);
	}

	public JavaMethod newStartElementMethod() throws SAXException {
		JavaMethod result = super.newStartElementMethod();
		result.addLine("return false;");
		return result;
	}

	public JavaMethod newIsFinishedMethod() throws SAXException {
		JavaMethod jm = super.newIsFinishedMethod();
		jm.addLine("return true;");
		return jm;
	}

	public JavaMethod newIsEmptyMethod() throws SAXException {
		JavaMethod jm = super.newIsEmptyMethod();
		jm.addLine("return ", ctSG.hasSimpleContent() ? "false" : "true", ";");
		return jm;
	}
	
	public JavaMethod newIsAtomicMethod() throws SAXException {
		JavaMethod jm = super.newIsAtomicMethod();
		jm.addLine("return ", ctSG.hasSimpleContent() ? "true" : "false", ";");
		return jm;
	}
}
