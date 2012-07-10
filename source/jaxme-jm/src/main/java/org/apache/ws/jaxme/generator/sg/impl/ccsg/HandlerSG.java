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

import org.apache.ws.jaxme.js.JavaMethod;
import org.apache.ws.jaxme.js.JavaSource;
import org.xml.sax.SAXException;


/** Interface of an object, which generates instances
 * of {@link org.apache.ws.jaxme.impl.JMSAXElementParser},
 * or {@link org.apache.ws.jaxme.impl.JMSAXGroupParser}.
 */
public interface HandlerSG {
	/** Returns the class being generated.
	 */
	public JavaSource getJavaSource();

	/** Creates the handlers method
	 * {@link org.apache.ws.jaxme.impl.JMSAXElementParser#addAttribute(String, String, String)}.
	 */
	public JavaMethod newAddAttributeMethod() throws SAXException;

	/** Creates the handlers method
	 * {@link org.apache.ws.jaxme.impl.JMSAXGroupParser#startElement(String, String, String, org.xml.sax.Attributes)}.
	 */
	public JavaMethod newStartElementMethod() throws SAXException;

	/** Creates the handlers method
	 * {@link org.apache.ws.jaxme.impl.JMSAXGroupParser#endElement(String, String, String, Object)}.
	 */
	public JavaMethod newEndElementMethod() throws SAXException;

	/** Creates the handlers method
	 * {@link org.apache.ws.jaxme.impl.JMSAXGroupParser#isFinished()}.
	 */
	public JavaMethod newIsFinishedMethod() throws SAXException;

	/** Creates the handlers method
	 * {@link org.apache.ws.jaxme.impl.JMSAXElementParser#isEmpty()}.
	 */
	public JavaMethod newIsEmptyMethod() throws SAXException;

	/** Creates the handlers method
	 * {@link org.apache.ws.jaxme.impl.JMSAXElementParser#isAtomic()}.
	 */
	public JavaMethod newIsAtomicMethod() throws SAXException;

	/** Invokes the various "newFooMethod" methods.
	 */
	public void generate() throws SAXException;
}
