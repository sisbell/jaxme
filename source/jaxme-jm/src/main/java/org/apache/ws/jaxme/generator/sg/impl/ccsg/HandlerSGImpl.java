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
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.ValidationEvent;
import javax.xml.namespace.QName;

import org.apache.ws.jaxme.IDREF;
import org.apache.ws.jaxme.ValidationEvents;
import org.apache.ws.jaxme.generator.sg.AttributeSG;
import org.apache.ws.jaxme.generator.sg.ComplexTypeSG;
import org.apache.ws.jaxme.generator.sg.PropertySG;
import org.apache.ws.jaxme.generator.sg.TypeSG;
import org.apache.ws.jaxme.generator.types.StringSG;
import org.apache.ws.jaxme.js.DirectAccessible;
import org.apache.ws.jaxme.js.JavaMethod;
import org.apache.ws.jaxme.js.JavaQName;
import org.apache.ws.jaxme.js.JavaQNameImpl;
import org.apache.ws.jaxme.js.JavaSource;
import org.apache.ws.jaxme.js.LocalJavaField;
import org.apache.ws.jaxme.js.Parameter;
import org.apache.ws.jaxme.js.TypedValue;
import org.apache.ws.jaxme.js.impl.TypedValueImpl;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


/** Base implementation of
 * {@link org.apache.ws.jaxme.generator.sg.impl.ccsg.HandlerSG},
 * for derivation of various subclasses.
 */
public abstract class HandlerSGImpl implements HandlerSG {
	protected final ComplexTypeSG ctSG;
	private final JavaSource javaSource;
	private DirectAccessible paramLocalName;
	private DirectAccessible paramNamespaceURI;
	private DirectAccessible paramResult;
	private DirectAccessible paramQName;
	private DirectAccessible paramAttrs;

	protected HandlerSGImpl(ComplexTypeSG pType, JavaSource pJs) {
		ctSG = pType;
		javaSource = pJs;
	}

	/** Returns the class being generated.
	 */
	public JavaSource getJavaSource() {
		return javaSource;
	}

	protected void setParamAttrs(DirectAccessible pParamAttrs) { paramAttrs = pParamAttrs; }

	protected DirectAccessible getParamAttrs() { return paramAttrs; }

	protected void setParamLocalName(DirectAccessible pParamLocalName) { paramLocalName = pParamLocalName; }

	protected DirectAccessible getParamLocalName() { return paramLocalName; }

	protected void setParamNamespaceURI(DirectAccessible pParamNamespaceURI) { paramNamespaceURI = pParamNamespaceURI; }

	protected DirectAccessible getParamNamespaceURI() { return paramNamespaceURI; }

	protected void setParamQName(DirectAccessible pParamQName) { paramQName = pParamQName; }

	protected DirectAccessible getParamQName() { return paramQName; }

	protected void setParamResult(DirectAccessible pParamResult) { paramResult = pParamResult; }

	protected DirectAccessible getParamResult() { return paramResult; }

	/** Returns the set of namespace URI's in <code>pAttributes</code>.
	 */
	private Set createSetOfExplicitURIs(AttributeSG[] pAttributes) {
		Set result = new HashSet();
		for (int i = 0;  i < pAttributes.length;  i++) {
			AttributeSG attr = pAttributes[i];
			if (!attr.isWildcard()) {
				String uri = attr.getName().getNamespaceURI();
				if (uri == null) { uri = ""; }
				result.add(uri);
			}
		}
		return result;
	}

	public JavaMethod newAddAttributeMethod() throws SAXException {
		AttributeSG[] myAttributes = ctSG.getAttributes();
		if (myAttributes.length == 0) {
			return null;
		}
		
		JavaMethod jm = getJavaSource().newJavaMethod("addAttribute", JavaQNameImpl.VOID, JavaSource.PUBLIC);
		Parameter pURI = jm.addParam(String.class, "pURI");
		Parameter pLocalName = jm.addParam(String.class, "pLocalName");
		Parameter pValue = jm.addParam(String.class, "pValue");
		jm.addThrows(SAXException.class);
		jm.addIf(pURI, " == null");
		jm.addLine(pURI, " = \"\";");
		jm.addEndIf();
		
		JavaQName resultType = ctSG.getClassContext().getXMLInterfaceName();
		LocalJavaField result = jm.newJavaField(resultType);
		result.setFinal(true);
		result.addLine("(", resultType, ") result");

		Set uris = createSetOfExplicitURIs(myAttributes);
		boolean first = true;
		for (Iterator iter = uris.iterator();  iter.hasNext();  ) {
			String uri = (String) iter.next();
			jm.addIf(first, JavaSource.getQuoted(uri), ".equals(", pURI, ")");
			first = false;
			boolean firstInNamespace = true;
			for (int i = 0;  i < myAttributes.length;  i++) {
				AttributeSG attr = myAttributes[i];
				if (attr.isWildcard()) {
					continue;
				}
				String jUri = attr.getName().getNamespaceURI();
				if (jUri == null) { jUri = ""; }
				if (!uri.equals(jUri)) {
					continue;
				}
				
				jm.addIf(firstInNamespace, JavaSource.getQuoted(attr.getName().getLocalName()), ".equals(", pLocalName, ")");
				firstInNamespace = false;
				boolean idref = false;
				if (attr.getTypeSG().getSimpleTypeSG().isXsId()) {
					jm.addIf("!getHandler().addId(", pValue, ", ", result, ")");
					jm.addLine("getHandler().validationEvent(",
							   javax.xml.bind.ValidationEvent.class,
							   ".ERROR, ",
							   JavaSource.getQuoted("Duplicate ID: "),
							   " + ", pValue, ", ",
							   ValidationEvents.class,
							   ".EVENT_DUPLICATE_ID, null);");
					jm.addEndIf();
				} else if (attr.getTypeSG().getSimpleTypeSG().isXsIdRef()) {
					idref = true;
					try {
						pValue.setFinal(true);
						LocalJavaField locator = jm.newJavaField(Locator.class);
						locator.setFinal(true);
						locator.addLine("getHandler().getDocumentLocator()");
						
						// FIXME how to properly create anonymous innner class?
						LocalJavaField idrefField = jm.newJavaField(IDREF.class, "idref");
						idrefField.addLine("new ", IDREF.class, "() {");
						idrefField.addLine("  public void validate(", Map.class, " pIds) throws ", SAXException.class, " {");
						idrefField.addLine("    Object o = pIds.get(", pValue, ");");
						idrefField.addLine("    if (o != null) {");
						idrefField.addLine("      ", result, ".", attr.getPropertySG().getXMLSetMethodName(), "(", "o", ");");
						idrefField.addLine("    } else {");
						idrefField.addLine("      final String msg = ",
										   JavaSource.getQuoted("ID attribute '"),
										   " + ", pValue, " + ",
										   JavaSource.getQuoted("' was referenced but never declared"), ";");
						idrefField.addLine("      ", SAXParseException.class, " e = new ",
									       SAXParseException.class, "(msg, ", locator, ");");
						idrefField.addLine("      getHandler().validationEvent(",
										   javax.xml.bind.ValidationEvent.class,
										   ".ERROR, msg,",
										   ValidationEvents.class,
										   ".EVENT_IDREF_UNDECLARED, e);");
						idrefField.addLine("    }");
						idrefField.addLine("  }");
						idrefField.addLine("};");
						jm.addLine("getHandler().addIdref(", idrefField, ");");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (!idref) {
					createSimpleTypeConversion(jm, myAttributes[i].getTypeSG(), pValue,
											   "@" + myAttributes[i].getName(),
											   attr.getPropertySG(), result);
				}
				jm.addLine("return;");
			}
			if (!firstInNamespace) {
				jm.addEndIf();
			}
		}
		if (!first) {
			jm.addEndIf();
		}
		
		AttributeSG wildcard = null;
		for (int i = 0;  i < myAttributes.length;  i++) {
			if (myAttributes[i].isWildcard()) {
				wildcard = myAttributes[i];
				break;
			}
		}
		if (wildcard == null) {
			jm.addLine("super.addAttribute(", pURI, ", ", pLocalName, ", ", pValue, ");");
		} else {
			jm.addTry();
			jm.addLine(result, ".", wildcard.getPropertySG().getXMLSetMethodName(), "(new ",
					QName.class, "(", pURI, ", ", pLocalName, "), ", pValue, ");");
			jm.addCatch(IllegalArgumentException.class);
			jm.addLine("getHandler().validationEvent(", ValidationEvent.class, ".ERROR, ",
					JavaSource.getQuoted("Invalid namespace for anyAttribute: '"),
					" + ", pURI, " + ", JavaSource.getQuoted("', attribute name is '"),
					" + ", pLocalName, " + ", JavaSource.getQuoted("'"),
					", ", ValidationEvents.class, ".EVENT_UNKNOWN_ANY_ATTRIBUTE, null);");
			jm.addEndTry();
		}
		return jm;
	}

	public JavaMethod newStartElementMethod() throws SAXException {
		JavaSource js = getJavaSource();
		JavaMethod jm = js.newJavaMethod("startElement", boolean.class, JavaSource.PUBLIC);
		DirectAccessible pNamespaceURI = jm.addParam(String.class, "pNamespaceURI");
		DirectAccessible pLocalName = jm.addParam(String.class, "pLocalName");
		DirectAccessible pQName = jm.addParam(String.class, "pQName");
		DirectAccessible pAttr = jm.addParam(Attributes.class, "pAttr");
		setParamNamespaceURI(pNamespaceURI);
		setParamLocalName(pLocalName);
		setParamQName(pQName);
		setParamAttrs(pAttr);
		setParamResult(null);
		jm.addThrows(SAXException.class);
		return jm;
	}

	public JavaMethod newEndElementMethod() throws SAXException {
		JavaMethod jm = getJavaSource().newJavaMethod("endElement", void.class, JavaSource.PUBLIC);
		DirectAccessible pNamespaceURI = jm.addParam(String.class, "pNamespaceURI");
		DirectAccessible pLocalName = jm.addParam(String.class, "pLocalName");
		DirectAccessible pQName = jm.addParam(String.class, "pQName");
		DirectAccessible pResult = jm.addParam(Object.class, "pResult");
		setParamNamespaceURI(pNamespaceURI);
		setParamLocalName(pLocalName);
		setParamQName(pQName);
		setParamAttrs(null);
		setParamResult(pResult);
		jm.addThrows(SAXException.class);
		return jm;
	}

	public JavaMethod newIsFinishedMethod() throws SAXException {
		JavaMethod jm = getJavaSource().newJavaMethod("isFinished", boolean.class, JavaSource.PUBLIC);
		return jm;
	}

	public JavaMethod newIsEmptyMethod() throws SAXException {
		JavaMethod jm = getJavaSource().newJavaMethod("isEmpty", boolean.class, JavaSource.PUBLIC);
		return jm;
	}
	
	public JavaMethod newIsAtomicMethod() throws SAXException {
		JavaMethod jm = getJavaSource().newJavaMethod("isAtomic", boolean.class, JavaSource.PUBLIC);
		return jm;
	}

	protected void createSimpleTypeConversion(JavaMethod pJm, TypeSG pType,
											  TypedValue pValue, String pName,
											  PropertySG pPropertySG,
											  DirectAccessible pElement)
			throws SAXException {
		boolean causingParseConversionEvent = pType.getSimpleTypeSG().isCausingParseConversionEvent();
		if (causingParseConversionEvent) {
			pJm.addTry();
		}
		Object s = new Object[]{"(", StringSG.STRING_TYPE, ") ", pValue};
		pType.getSimpleTypeSG().addHandlerValidation(pJm, pValue, s);
		TypedValue result = new TypedValueImpl(pType.getSimpleTypeSG().getCastFromString(pJm, s, "getHandler()"),
							   				   pType.getSimpleTypeSG().getRuntimeType());
		pPropertySG.addValue(pJm, pElement, result, null);
		if (causingParseConversionEvent) {
			DirectAccessible e = pJm.addCatch(Exception.class);
			pJm.addLine("getHandler().parseConversionEvent(",
						JavaSource.getQuoted("Failed to convert value "),
						" + ", pValue, " + ",
						JavaSource.getQuoted(" for " +  pName + ": "),
						" + ", e, ".getMessage(), ", e, ");");
			pJm.addEndTry();
		}
	}

	public void generate() throws SAXException {
		newAddAttributeMethod();
		newStartElementMethod();
		newEndElementMethod();
		newIsFinishedMethod();
		newIsEmptyMethod();
		newIsAtomicMethod();
	}
}
