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

import java.util.ArrayList;
import java.util.List;

import org.apache.ws.jaxme.generator.sg.ObjectSG;
import org.apache.ws.jaxme.generator.sg.PropertySG;
import org.apache.ws.jaxme.generator.sg.PropertySGChain;
import org.apache.ws.jaxme.generator.sg.SGlet;
import org.apache.ws.jaxme.generator.sg.TypeSG;
import org.apache.ws.jaxme.js.DirectAccessible;
import org.apache.ws.jaxme.js.JavaField;
import org.apache.ws.jaxme.js.JavaMethod;
import org.apache.ws.jaxme.js.JavaQName;
import org.apache.ws.jaxme.js.JavaQNameImpl;
import org.apache.ws.jaxme.js.JavaSource;
import org.apache.ws.jaxme.js.LocalJavaField;
import org.apache.ws.jaxme.js.TypedValue;
import org.xml.sax.SAXException;


/** <Implementation of a an objectSG for elements with multiplicity > 1.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class MultiplePropertySG extends PropertySGChainImpl {
	protected static final JavaQName OBJECT_TYPE = JavaQNameImpl.getInstance(Object.class);
	private final int minOccurs, maxOccurs;
	private final ObjectSG objectSG;
	
	/** <p>Creates a new instance of MultipleObjectSG.</p>
	 */
	protected MultiplePropertySG(PropertySGChain pBase, ObjectSG pObjectSG, int pMinOccurs, int pMaxOccurs) {
		super(pBase);
		objectSG = pObjectSG;
		maxOccurs = pMaxOccurs;
		minOccurs = pMinOccurs;
	}
	
	protected int getMinOccurs() { return minOccurs; }
	protected int getMaxOccurs() { return maxOccurs; }
	protected ObjectSG getObjectSG() { return objectSG; }
	protected JavaQName getInstanceClass() throws SAXException {
		return getObjectSG().getTypeSG().getRuntimeType();
	}
	protected boolean isAutoBoxing() throws SAXException {
		return getInstanceClass().isPrimitive();
	}
	protected JavaQName getObjectClass() throws SAXException {
		JavaQName result = getInstanceClass().getObjectType();
		if (result == null) {
			result = getInstanceClass();
		}
		return result;
	}
	protected Object asObject(Object pObject) throws SAXException {
		if (isAutoBoxing()) {
			return new Object[]{"new ", getObjectClass(), "(", pObject, ")"};
		} else {
			return pObject;
		}
	}

	public JavaField getXMLField(PropertySG pController, JavaSource pSource) throws SAXException {
		String fieldName = pController.getXMLFieldName();
		JavaField result = pSource.newJavaField(fieldName, List.class, JavaSource.PRIVATE);
		result.addLine("new ", ArrayList.class, "()");
		return result;
	}
	
	public JavaMethod getXMLSetMethod(PropertySG pController, JavaSource pSource) throws SAXException {
		return null;
	}
	
	public JavaMethod getXMLGetMethod(PropertySG pController, JavaSource pSource) throws SAXException {
		String fieldName = pController.getXMLFieldName();
		String methodName = pController.getXMLGetMethodName();
		JavaMethod result = pSource.newJavaMethod(methodName, List.class, JavaSource.PUBLIC);
		result.addLine("return ", fieldName, ";");
		return result;
	}
	
	public void forAllValues(PropertySG pController, JavaMethod pMethod, DirectAccessible pElement, SGlet pSGlet) throws SAXException {
		LocalJavaField list = pMethod.newJavaField(List.class);
		list.addLine(pController.getValue(pElement));
		DirectAccessible i = pMethod.addForList(list);
		TypeSG typeSG = objectSG.getTypeSG();
		Object v;
		boolean isCasting = !OBJECT_TYPE.equals(typeSG.getRuntimeType());
		if (isCasting  &&  pSGlet instanceof SGlet.TypedSGlet) {
			isCasting = ((SGlet.TypedSGlet) pSGlet).isCasting();
		}
		if (isCasting) {
			v = new Object[]{"(", typeSG.getRuntimeType(), ")", list, ".get(", i, ")"};
		} else {
			v = new Object[]{list, ".get(", i, ")"};
		}
		if (typeSG.isComplex()) {
			pSGlet.generate(pMethod, v);
		} else {
			typeSG.getSimpleTypeSG().forAllValues(pMethod, v, pSGlet);
		}
		pMethod.addEndFor();
	}
	
	public void forAllNonNullValues(PropertySG pController, JavaMethod pMethod, DirectAccessible pElement, SGlet pSGlet) throws SAXException {
		LocalJavaField list = pMethod.newJavaField(List.class);
		list.addLine(pController.getValue(pElement));
		DirectAccessible i = pMethod.addForList(list);
		TypeSG typeSG = objectSG.getTypeSG();
		boolean isCasting = !OBJECT_TYPE.equals(typeSG.getRuntimeType());
		JavaQName qName = typeSG.getRuntimeType();
		if (isCasting  &&  pSGlet instanceof SGlet.TypedSGlet) {
			SGlet.TypedSGlet typedSGlet = (SGlet.TypedSGlet) pSGlet;
			isCasting = typedSGlet.isCasting();
			if (typedSGlet.getType() != null) {
				qName = typedSGlet.getType();
			}
		}
		Object v = new Object[]{list, ".get(", i, ")"};
		if (isCasting) {
			if (qName.isPrimitive()) {
				v = new Object[]{"((", qName.getObjectType(), ")", v, ").", qName.getPrimitiveConversionMethod(), "()"};
			} else {
				v = new Object[]{"(", qName, ")", v};
			}
		}
		if (typeSG.isComplex()) {
			pSGlet.generate(pMethod, v);
		} else {
			typeSG.getSimpleTypeSG().forAllNonNullValues(pMethod, v, pSGlet);
		}
		pMethod.addEndFor();
	}
	
	public void setValue(PropertySG pController, JavaMethod pMethod, DirectAccessible pElement, Object pValue, JavaQName pType) throws SAXException {
		if (pType != null) {
			pValue = new Object[]{"(", pType, ") ", pValue};
		}
		LocalJavaField list = pMethod.newJavaField(List.class);
		list.addLine(pController.getValue(pElement));
		pMethod.addLine(list, ".clear();");
		pMethod.addLine(list, ".addAll(", pValue, ");");
	}
	
	public void addValue(PropertySG pController, JavaMethod pMethod, DirectAccessible pElement, TypedValue pValue, JavaQName pType) throws SAXException {
		pMethod.addLine(pController.getValue(pElement), ".add(", asObject(pValue), ");");
	}
}
