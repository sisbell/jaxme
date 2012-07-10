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
import org.apache.ws.jaxme.generator.sg.SimpleContentSG;
import org.apache.ws.jaxme.js.JavaMethod;
import org.apache.ws.jaxme.js.JavaQName;
import org.apache.ws.jaxme.js.JavaSource;
import org.apache.ws.jaxme.js.LocalJavaField;
import org.xml.sax.SAXException;


/** Creates an instance of
 * {@link org.apache.ws.jaxme.impl.JMSAXElementParser},
 * which parses a complex element with simple content.
 */
public class SimpleContentHandlerSG extends EmptyElementHandlerSG {
	/** Creates a new instance, which generates a handler
	 * for the given complex type.
	 */
	public SimpleContentHandlerSG(ComplexTypeSG pType, JavaSource pJs) {
		super(pType, pJs);
	}

	public JavaMethod newEndElementMethod() throws SAXException {
		JavaMethod result = super.newEndElementMethod();
		JavaQName elementInterface = ctSG.getClassContext().getXMLInterfaceName();
		LocalJavaField element = result.newJavaField(elementInterface);
		element.addLine("(", elementInterface, ") result");
		SimpleContentSG scSG = ctSG.getSimpleContentSG();
		createSimpleTypeConversion(result, scSG.getContentTypeSG(),
								   getParamResult(),
								   scSG.getPropertySG().getPropertyName(),
								   scSG.getPropertySG(), element);
		return result;
	}
}
