/*
 * Copyright 2003,2004  The Apache Software Foundation
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

import java.util.List;

import org.apache.ws.jaxme.generator.sg.AttributeSG;
import org.apache.ws.jaxme.generator.sg.ObjectSG;
import org.apache.ws.jaxme.generator.sg.PropertySG;
import org.apache.ws.jaxme.generator.sg.PropertySGChain;
import org.apache.ws.jaxme.generator.sg.SGlet;
import org.apache.ws.jaxme.generator.sg.SchemaSG;
import org.apache.ws.jaxme.generator.sg.SimpleTypeSG;
import org.apache.ws.jaxme.generator.sg.TypeSG;
import org.apache.ws.jaxme.generator.util.JavaNamer;
import org.apache.ws.jaxme.js.DirectAccessible;
import org.apache.ws.jaxme.js.JavaField;
import org.apache.ws.jaxme.js.JavaMethod;
import org.apache.ws.jaxme.js.JavaQName;
import org.apache.ws.jaxme.js.JavaQNameImpl;
import org.apache.ws.jaxme.js.JavaSource;
import org.apache.ws.jaxme.js.LocalJavaField;
import org.apache.ws.jaxme.js.TypedValue;
import org.apache.ws.jaxme.xs.XSAttribute;
import org.apache.ws.jaxme.xs.XSElement;
import org.apache.ws.jaxme.xs.XSObject;
import org.apache.ws.jaxme.xs.XSType;
import org.apache.ws.jaxme.xs.jaxb.JAXBProperty;
import org.apache.ws.jaxme.xs.jaxb.JAXBPropertyOwner;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 * @author <a href="mailto:iasandcb@tmax.co.kr">Ias</a>
 */
public class JAXBPropertySG implements PropertySGChain {
	private final String propertyName;
	private final String fieldName;
	private final String collectionType;
	private final boolean generateIsSetMethod;
	private final String defaultValue;
	private final TypeSG typeSG;

	protected JAXBPropertySG(String pDefaultPropertyName, SchemaSG pSchema, XSObject pXSObject,
							 String pDefaultValue, TypeSG pTypeSG) {
        typeSG = pTypeSG;
	    defaultValue = pDefaultValue;
	    String myPropertyName = null;
	    boolean myGeneratedIsSetMethod = pSchema.isGeneratingIsSetMethod();
	    String myCollectionType = null;
	    if (pXSObject instanceof JAXBPropertyOwner) {
	        JAXBPropertyOwner jaxbPropertyOwner = (JAXBPropertyOwner) pXSObject;
	        JAXBProperty jaxbProperty = jaxbPropertyOwner.getJAXBProperty();
	        if (jaxbProperty != null) {
	            myPropertyName = jaxbProperty.getName();
                myCollectionType = jaxbProperty.getCollectionType();
	            Boolean jaxbGeneratedIsSetMethod = jaxbProperty.isGenerateIsSetMethod();
	            if (jaxbGeneratedIsSetMethod != null) {
	                myGeneratedIsSetMethod = jaxbGeneratedIsSetMethod.booleanValue();
	            }
	        }
	    }

	    collectionType = myCollectionType == null ? pSchema.getCollectionType() : myCollectionType;
		if (myPropertyName == null) {
			myPropertyName = JavaNamer.convert(pDefaultPropertyName, pSchema);
			myPropertyName = Character.toLowerCase(myPropertyName.charAt(0)) + myPropertyName.substring(1);
		}
		propertyName = myPropertyName;
		fieldName = "_" + propertyName;
	    generateIsSetMethod = myGeneratedIsSetMethod;
	}

	protected JAXBPropertySG(AttributeSG pAttribute, XSAttribute pXSAttribute) {
	    this(pAttribute.getName().getLocalName(), pAttribute.getSchema(), pXSAttribute, pXSAttribute.getDefault(),
	         pAttribute.getTypeSG());
	}
	
    protected JAXBPropertySG(ObjectSG pElement, XSElement pXSElement) {
        this(pElement.getName().getLocalName(), pElement.getSchema(), pXSElement, pXSElement.getDefault(),
	         pElement.getTypeSG());
	}
	
	protected JAXBPropertySG(TypeSG pComplexType, XSType pType) throws SAXException {
	    this("value", pComplexType.getSchema(), pType, null,
	         pComplexType.getComplexTypeSG().getSimpleContentSG().getContentTypeSG());
	}

	public void init(PropertySG pController) throws SAXException {}
	
	public boolean hasIsSetMethod(PropertySG pController) { return generateIsSetMethod; }
	public String getCollectionType(PropertySG pController) { return collectionType; }
	
	public String getXMLFieldName(PropertySG pController) throws SAXException {
		return fieldName;
	}

	public String getPropertyName(PropertySG pController) throws SAXException {
		return propertyName;
	}

	public String getXMLGetMethodName(PropertySG pController) throws SAXException {
		String prefix;
        if (typeSG != null  &&  !typeSG.isComplex()  &&
            typeSG.getSimpleTypeSG().getRuntimeType().equals(JavaQNameImpl.BOOLEAN)) {
        	prefix = "is";
        } else {
        	prefix = "get";
        }
		String propName = pController.getPropertyName();
		String methodName = prefix + Character.toUpperCase(propName.charAt(0)) + propName.substring(1);
        if (methodName.equals("getClass")) {
        	throw new SAXException("Method name getClass() conflicts with java.lang.Object.getClass(), use jaxb:property to customize the property name.");
        }
		return methodName;
	}
	
	public String getXMLSetMethodName(PropertySG pController) throws SAXException {
		String propName = pController.getPropertyName();
		return "set" + Character.toUpperCase(propName.charAt(0)) + propName.substring(1);
	}
	
	public String getXMLIsSetMethodName(PropertySG pController) throws SAXException {
		String propName = pController.getPropertyName();
		return hasIsSetMethod(pController) ?
										   "isSet" + Character.toUpperCase(propName.charAt(0)) + propName.substring(1) : null;
	}

	public JavaField getXMLField(PropertySG pController, JavaSource pSource) throws SAXException {
		return typeSG.getXMLField(pSource, pController.getXMLFieldName(), defaultValue);
	}
	
	public JavaMethod getXMLGetMethod(PropertySG pController, JavaSource pSource) throws SAXException {
		return typeSG.getXMLGetMethod(pSource, pController.getXMLFieldName(),
									  pController.getXMLGetMethodName());
	}
	
	public JavaMethod getXMLSetMethod(PropertySG pController, JavaSource pSource) throws SAXException {
		String propName = pController.getPropertyName();
		String pName = "p" + Character.toUpperCase(propName.charAt(0)) + propName.substring(1);
		return typeSG.getXMLSetMethod(pSource, pController.getXMLFieldName(),
									  pName, pController.getXMLSetMethodName(),
                                      pController.hasIsSetMethod());
	}
	
	public JavaMethod getXMLIsSetMethod(PropertySG pController, JavaSource pSource) throws SAXException {
		return typeSG.getXMLIsSetMethod(pSource, pController.getXMLFieldName(),
				pController.getXMLIsSetMethodName());
	}
	
	public void forAllNonNullValues(PropertySG pController, JavaMethod pMethod,
									DirectAccessible pElement, SGlet pSGlet) throws SAXException {
		boolean hasIsSetMethod = pController.hasIsSetMethod();
		if (hasIsSetMethod) {
			if (pElement == null) {
				pMethod.addIf(pController.getXMLIsSetMethodName(), "()");
			} else {
				pMethod.addIf(pElement, ".", pController.getXMLIsSetMethodName(), "()");
			}
			if (typeSG.isComplex()) {
				pSGlet.generate(pMethod, pController.getValue(pElement));
			} else {
				typeSG.getSimpleTypeSG().forAllValues(pMethod, pController.getValue(pElement), pSGlet);
			}
			pMethod.addEndIf();
		} else {
			if (typeSG.isComplex()) {
				LocalJavaField f = pMethod.newJavaField(typeSG.getComplexTypeSG().getClassContext().getXMLInterfaceName());
				f.addLine(pController.getValue(pElement));
				pMethod.addIf(f, " != null");
				pSGlet.generate(pMethod, pController.getValue(pElement));
				pMethod.addEndIf();
			} else {
				typeSG.getSimpleTypeSG().forAllNonNullValues(pMethod, pController.getValue(pElement), pSGlet);
			}
		}
	}
	public void forAllValues(PropertySG pController, JavaMethod pMethod,
							 DirectAccessible pElement, SGlet pSGlet) throws SAXException {
		SimpleTypeSG simpleTypeSG = typeSG.getSimpleTypeSG();
		simpleTypeSG.forAllValues(pMethod, pController.getValue(pElement), pSGlet);
	}
	
	public Object getValue(PropertySG pController, DirectAccessible pElement) throws SAXException {
		if (pElement == null) {
			return new Object[]{pController.getXMLGetMethodName(), "()"};
		} else {
			return new Object[]{pElement, ".", pController.getXMLGetMethodName(), "()"};
		}
	}

	public void generate(PropertySG pController, JavaSource pSource) throws SAXException {
        if (typeSG != null  &&  !typeSG.isGlobalType()  &&  !typeSG.isGlobalClass()  &&  pSource.isInterface()) {
            typeSG.generate(pSource);
		}
		pController.getXMLField(pSource);
		pController.getXMLGetMethod(pSource);
		pController.getXMLSetMethod(pSource);
		if (hasIsSetMethod(pController)) {
			pController.getXMLIsSetMethod(pSource);
		}
	}
	
	public void setValue(PropertySG pController, JavaMethod pMethod,
                         DirectAccessible pElement, Object pValue, JavaQName pType)
	        throws SAXException {
		if (pType != null) {
			pValue = new Object[]{"((", pType, ") ", pValue, ")"};
		}
		if (typeSG.isComplex()  ||  !typeSG.getSimpleTypeSG().isList()) {
			if (pElement == null) {
				pMethod.addLine(pController.getXMLSetMethodName(), "(", pValue, ");");
			} else {
				pMethod.addLine(pElement, ".", pController.getXMLSetMethodName(), "(", pValue, ");");
			}
		} else {
			LocalJavaField list = pMethod.newJavaField(List.class);
			list.addLine(pController.getValue(pElement));
			pMethod.addLine(list, ".clear();");
			pMethod.addLine(list, ".addAll(", pValue, ");");
		}
	}
	
	public void addValue(PropertySG pController, JavaMethod pMethod, DirectAccessible pElement, TypedValue pValue, JavaQName pType) throws SAXException {
		pController.setValue(pMethod, pElement, pValue, pType);
	}
}
