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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.ws.jaxme.generator.sg.ComplexTypeSG;
import org.apache.ws.jaxme.generator.sg.Context;
import org.apache.ws.jaxme.generator.sg.GroupSG;
import org.apache.ws.jaxme.generator.sg.ObjectSG;
import org.apache.ws.jaxme.generator.sg.ParticleSG;
import org.apache.ws.jaxme.generator.sg.SGlet;
import org.apache.ws.jaxme.generator.sg.SimpleContentSG;
import org.apache.ws.jaxme.impl.JMSAXDriver;
import org.apache.ws.jaxme.impl.JMSAXDriverController;
import org.apache.ws.jaxme.js.DirectAccessible;
import org.apache.ws.jaxme.js.JavaMethod;
import org.apache.ws.jaxme.js.JavaQName;
import org.apache.ws.jaxme.js.JavaSource;
import org.apache.ws.jaxme.js.LocalJavaField;
import org.apache.ws.jaxme.js.Parameter;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;


class MarshalChildsMethodGeneratingVisitor extends ParticleVisitorImpl {
	private final Parameter element, controller, handler;
	private ComplexTypeSG ctSG;
	private LocalJavaField castedElement;
	private final JavaMethod jm;
	private boolean isMixed;
	private Set mixedTypesMap;
	private LocalJavaField object;

	MarshalChildsMethodGeneratingVisitor(JavaSource pJs) {
		jm = pJs.newJavaMethod("marshalChilds", void.class, JavaSource.PUBLIC);
	    jm.addThrows(SAXException.class);
		controller = jm.addParam(JMSAXDriverController.class, "pController");
		handler = jm.addParam(ContentHandler.class, "pHandler");
		element = jm.addParam(Object.class, "pObject");
	}

	public void simpleContent(ComplexTypeSG pType) throws SAXException {
		JavaQName elementInterface = pType.getClassContext().getXMLInterfaceName();
		castedElement = jm.newJavaField(elementInterface);
	    castedElement.addLine("(", elementInterface, ") ", element);
	    LocalJavaField chars = jm.newJavaField(String.class);
	    SimpleContentSG simpleContent = pType.getSimpleContentSG();
	    Object value = simpleContent.getPropertySG().getValue(castedElement);
	    chars.addLine(simpleContent.getContentTypeSG().getSimpleTypeSG().getCastToString(jm, value, controller));
	    jm.addIf(chars, " != null  &&  ", chars, ".length() > 0");
	    LocalJavaField charArray = jm.newJavaField(char[].class);
	    charArray.addLine(chars, ".toCharArray()");
	    jm.addLine(handler, ".characters(", charArray, ", 0, ", charArray, ".length);");
	    jm.addEndIf();
	}

	public void startComplexContent(ComplexTypeSG pType) throws SAXException {
		ctSG = pType;
		JavaQName elementInterface = pType.getClassContext().getXMLInterfaceName();
		castedElement = jm.newJavaField(elementInterface);
		castedElement.addLine("(", elementInterface, ") ", element);
		isMixed = pType.getComplexContentSG().isMixed();
		if (isMixed) {
			LocalJavaField list = jm.newJavaField(List.class);
			list.addLine(castedElement, ".getContent()");
			DirectAccessible i = jm.addForList(list);
			object = jm.newJavaField(Object.class);
			object.addLine(list, ".get(", i, ")");
			mixedTypesMap = new HashSet();
			jm.addIf(object, " instanceof ", String.class);
			LocalJavaField s = jm.newJavaField(String.class);
			s.addLine("(", String.class, ") ", object);
			jm.addLine(handler, ".characters(", s,
					   ".toCharArray(), 0, ", s, ".length());");
		}
	}

	public void endComplexContent(ComplexTypeSG pType) throws SAXException {
		if (isMixed) {
			jm.addElse();
			jm.addThrowNew(IllegalStateException.class,
						   JavaSource.getQuoted("Invalid type: "),
						   " + ", object, ".getClass().getName()");
			jm.addEndIf();
			jm.addEndFor();
		}
	}

	private void marshalSimpleValue(ObjectSG pObjectSG, JavaMethod pMethod, Object pValue) throws SAXException {
		Object value = pObjectSG.getTypeSG().getSimpleTypeSG().getCastToString(pMethod, pValue, controller);
		pMethod.addLine(controller, ".marshalSimpleChild(this, ",
				JavaSource.getQuoted(pObjectSG.getName().getNamespaceURI()), ", ",
				JavaSource.getQuoted(pObjectSG.getName().getLocalName()), ", ",
				value, ");");
	}

	public void simpleElementParticle(GroupSG pGroup, ParticleSG pParticle) throws SAXException {
		final ObjectSG oSG = pParticle.getObjectSG();
		if (isMixed) {
			JavaQName interfaceName = ctSG.getClassContext().getXMLInterfaceName();
			JavaQName qName = GroupUtil.getContentClass(pGroup, pParticle, interfaceName);
			if (mixedTypesMap.contains(qName)) {
				return;
			}
			mixedTypesMap.add(qName);
			jm.addElseIf(object, " instanceof ", qName);
			Object value = new Object[]{"((", qName, ") ", object, ").getValue()"};
			marshalSimpleValue(oSG, jm, value);
		} else {
			SGlet sgLet = new SGlet(){
				public void generate(JavaMethod pMethod, Object pValue) throws SAXException {
					marshalSimpleValue(oSG, pMethod, pValue);
				}
			};
			pParticle.getPropertySG().forAllNonNullValues(jm, castedElement, sgLet);
		}
	}

	private void marshalComplexValue(ObjectSG pObjectSG, JavaMethod pMethod,
									 Object pValue) throws SAXException {
		Context cc = pObjectSG.getTypeSG().getComplexTypeSG().getClassContext();
		JavaQName serializerClass = cc.getXMLSerializerName();
		LocalJavaField driver = pMethod.newJavaField(JMSAXDriver.class);
		if (pObjectSG.getTypeSG().isGlobalClass()) {
			JavaQName elementInterface;
			if (pObjectSG.isGlobal()) {
				elementInterface = pObjectSG.getClassContext().getXMLInterfaceName();
			} else {
				elementInterface = cc.getXMLInterfaceName();
			}
			driver.addLine(controller,
					".getJMMarshaller().getJAXBContextImpl().getManagerS(",
					elementInterface, ".class).getDriver()");
		} else {
			driver.addLine("new ", serializerClass, "();");
		}
		pMethod.addLine(controller, ".marshal(", driver,
				", ", JavaSource.getQuoted(pObjectSG.getName().getNamespaceURI()),
				", ", JavaSource.getQuoted(pObjectSG.getName().getLocalName()),
				", ", pValue, ");");
	}

	public void complexElementParticle(GroupSG pGroup, ParticleSG pParticle) throws SAXException {
		final ObjectSG oSG = pParticle.getObjectSG();
		if (isMixed) {
			JavaQName qName = oSG.getTypeSG().getComplexTypeSG().getClassContext().getXMLInterfaceName();
			if (mixedTypesMap.contains(qName)) {
				return;
			}
			mixedTypesMap.add(qName);
			jm.addElseIf(object, " instanceof ", qName);
			Object value = new Object[]{"((", qName, ") ", object, ")"};
			marshalComplexValue(oSG, jm, value);
		} else {
			SGlet sgLet = new SGlet(){
				public void generate(JavaMethod pMethod, Object pValue) throws SAXException {
					marshalComplexValue(oSG, pMethod, pValue);
				}
			};
			pParticle.getPropertySG().forAllNonNullValues(jm, castedElement, sgLet);
		}
	}

	JavaMethod getMethod() { return jm; }
}