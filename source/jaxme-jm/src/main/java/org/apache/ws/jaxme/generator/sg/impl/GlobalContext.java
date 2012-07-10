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
package org.apache.ws.jaxme.generator.sg.impl;

import org.apache.ws.jaxme.generator.sg.SchemaSG;
import org.apache.ws.jaxme.js.JavaQName;
import org.apache.ws.jaxme.js.JavaQNameImpl;
import org.apache.ws.jaxme.xs.XSObject;
import org.apache.ws.jaxme.xs.XSType;
import org.apache.ws.jaxme.xs.jaxb.JAXBClass;
import org.apache.ws.jaxme.xs.jaxb.JAXBClassOwner;
import org.apache.ws.jaxme.xs.jaxb.JAXBSchemaBindings;
import org.apache.ws.jaxme.xs.xml.XsQName;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class GlobalContext extends AbstractContext {
	/** <p>Creates a new, global class context.</p>
	 */
	public GlobalContext(XsQName pQName, XSObject pJAXBClassOwner,
						 String pPrefix, String pSuffix, SchemaSG pSchemaSG)
			throws SAXException {
		Locator locator;
		if (pJAXBClassOwner instanceof XSType) {
			XSType type = (XSType) pJAXBClassOwner;
			if (type.isBuiltin()) {
				locator = null;
			} else {
				locator = type.getLocator();
			}
		} else {
			locator = pJAXBClassOwner.getLocator();
		}

		if (pQName == null) {
			throw new NullPointerException("The XsQName argument must not be null.");
		}
		if (pJAXBClassOwner == null) {
			throw new NullPointerException("The XSObject argument must not be null.");
		}
		
		JAXBClassOwner jaxbClassOwner = null;
		JAXBSchemaBindings schemaBindings = null;
		
		if (pJAXBClassOwner instanceof JAXBClassOwner) {
			jaxbClassOwner = (JAXBClassOwner) pJAXBClassOwner;
			schemaBindings = jaxbClassOwner.getJAXBSchemaBindings();
		}
		
		String packageName = getPackageName(pSchemaSG, schemaBindings, locator, pQName);
		String className = null, implementationName = null;
		JAXBClass jaxbClass = (jaxbClassOwner == null) ? null : jaxbClassOwner.getJAXBClass();
		if (jaxbClass != null) {
			className = jaxbClass.getName();
			implementationName = jaxbClass.getImplClass();
			if (className != null) {
				pQName = new XsQName(pQName.getNamespaceURI(), className);
			}
		}
		setName(pQName);
		
		if (className == null) {
			String prefix = null, suffix = null;
			className = getClassNameFromLocalName(locator, pQName.getLocalName(), pSchemaSG);
			if (prefix == null) { prefix = pPrefix; }
			if (prefix != null) className = prefix + className;
			if (suffix == null) { suffix = pSuffix; }
			if (suffix != null) className += suffix;
		}
		
		JavaQName xmlInterfaceName = JavaQNameImpl.getInstance(packageName, className);
		setXMLInterfaceName(xmlInterfaceName);
		
		if (implementationName == null) {
			setXMLImplementationName(JavaQNameImpl.getInstance(xmlInterfaceName.getPackageName() + ".impl",
					xmlInterfaceName.getClassName() + "Impl"));
		} else {
			setXMLImplementationName(JavaQNameImpl.getInstance(implementationName));
		}
		setXMLHandlerName(JavaQNameImpl.getInstance(xmlInterfaceName.getPackageName() + ".impl",
				xmlInterfaceName.getClassName() + "Handler"));
		setXMLSerializerName(JavaQNameImpl.getInstance(xmlInterfaceName.getPackageName() + ".impl",
				xmlInterfaceName.getClassName() + "Driver"));
		setXMLValidatorName(JavaQNameImpl.getInstance(xmlInterfaceName.getPackageName() + ".impl",
				xmlInterfaceName.getClassName() + "Validator"));
		setPMName(JavaQNameImpl.getInstance(xmlInterfaceName.getPackageName() + ".impl",
				xmlInterfaceName.getClassName() + "PM"));
	}
	
	public boolean isGlobal() { return true; }
}
