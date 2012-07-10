/*
 * Copyright 2004  The Apache Software Foundation
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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.ws.jaxme.WildcardAttribute;
import org.apache.ws.jaxme.generator.sg.AttributeSG;
import org.apache.ws.jaxme.generator.sg.PropertySG;
import org.apache.ws.jaxme.js.DirectAccessible;
import org.apache.ws.jaxme.js.JavaClassInitializer;
import org.apache.ws.jaxme.js.JavaComment;
import org.apache.ws.jaxme.js.JavaField;
import org.apache.ws.jaxme.js.JavaMethod;
import org.apache.ws.jaxme.js.JavaQName;
import org.apache.ws.jaxme.js.JavaQNameImpl;
import org.apache.ws.jaxme.js.JavaSource;
import org.apache.ws.jaxme.js.LocalJavaField;
import org.apache.ws.jaxme.js.Parameter;
import org.apache.ws.jaxme.xs.XSWildcard;
import org.apache.ws.jaxme.xs.xml.XsAnyURI;
import org.apache.ws.jaxme.xs.xml.XsNamespaceList;
import org.apache.ws.jaxme.xs.xml.XsSchemaHeader;
import org.xml.sax.SAXException;


/** <p>PropertySG for attribute wildcards.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class AnyAttributePropertySG extends JAXBPropertySG {
    private final XsNamespaceList namespaceList;
    private final XsSchemaHeader schemaHeader;

    protected AnyAttributePropertySG(AttributeSG pAttribute, XSWildcard pWildcard) {
		super("anyAttribute", pAttribute.getSchema(), pWildcard, null, null);
		namespaceList = pWildcard.getNamespaceList();
		schemaHeader = pWildcard.getSchemaHeader();
	}

    protected String getTargetNamespace() {
        String targetNamespace = schemaHeader == null ? "" : schemaHeader.getTargetNamespace().toString();
        if (targetNamespace == null) {
            targetNamespace = "";
        }
        return targetNamespace;
    }

	public JavaField getXMLField(PropertySG pController, JavaSource pSource) throws SAXException {
		if (pSource.isInterface()) {
			return null;
		}
		JavaQName runtimeType = JavaQNameImpl.getInstance(Map.class);
		JavaField jf = pSource.newJavaField(pController.getXMLFieldName(), runtimeType, JavaSource.PRIVATE);
		jf.setFinal(true);
		jf.addLine("new ", HashMap.class, "()");
		return jf;
	}

	protected JavaField getNamespaces(PropertySG pController, JavaSource pSource) throws SAXException {
	    if (namespaceList.isAny()  ||  namespaceList.isOther()) {
	        return null;
	    }
	    String namespaces = pController.getXMLFieldName() + "Namespaces";
	    JavaField jf = pSource.newJavaField(namespaces, Set.class);
	    jf.setStatic(true);
	    jf.setFinal(true);
	    JavaClassInitializer jci = pSource.newJavaClassInitializer();
	    LocalJavaField set = jci.newJavaField(Set.class);
	    set.addLine("new ", HashSet.class, "()");
	    XsAnyURI[] uris = namespaceList.getUris();
	    for (int i = 0;  i < uris.length;  i++) {
	        jci.addLine(set, ".add(", JavaSource.getQuoted(uris[i].toString()), ");");
	    }
	    jci.addLine(jf, " = ", Collections.class, ".unmodifiableSet(", set, ");");
	    return jf;
	}

	protected void getValidNamespaceCheck(PropertySG pController, JavaMethod pMethod, Parameter pName)
			throws SAXException {
	    if (namespaceList.isAny()) {
	        // No checks for namespace
	    } else if (namespaceList.isOther()) {
	        String targetNamespace = getTargetNamespace();
	        pMethod.addIf(JavaSource.getQuoted(targetNamespace), ".equals(", pName, ".getNamespaceURI())");
	        pMethod.addThrowNew(IllegalArgumentException.class,
	                			JavaSource.getQuoted("The namespace of the pName argument must not match the target namespace '" +
	                			        			 targetNamespace + "'."));
	        pMethod.addEndIf();
	    } else {
	        String targetNamespace = getTargetNamespace();
	        String namespaces = pController.getXMLFieldName() + "Namespaces";
	        pMethod.addIf("!", namespaces, ".contains(", pName, ".getNamespaceURI())");
	        pMethod.addThrowNew(IllegalArgumentException.class,
	                			JavaSource.getQuoted("The namespace of the pName argument must not match the target namespace '" +
	                			        			 targetNamespace + "'."));
	        pMethod.addEndIf();
	    }
	}

	public JavaMethod getXMLGetMethod(PropertySG pController, JavaSource pSource) throws SAXException {
	    JavaMethod jm = pSource.newJavaMethod(pController.getXMLGetMethodName(), String.class, JavaSource.PUBLIC);
	    JavaComment jc = jm.newComment();
	    jc.addLine("<p>Returns the value of the 'anyAttribute' named <code>pName</code>.</p>");
	    jc.addLine("@return Attribute value or null, if the attribute is not set.");
	    jc.addLine("@throws NullPointerException The <code>pName</code> argument is null.");
	    if (!namespaceList.isAny()) {
	        jc.addLine("@throws IllegalArgumentException The namespace <code>pName.getNamespaceURI()</code> is invalid.");
	    }
	    Parameter pName = jm.addParam(QName.class, "pName");
	    if (!pSource.isInterface()) {
		    jm.addIf(pName, " == null");
		    jm.addThrowNew(NullPointerException.class, JavaSource.getQuoted("The pName argument must not be null."));
		    jm.addEndIf();
		    getValidNamespaceCheck(pController, jm, pName);
		    jm.addLine("return (", String.class, ") ", pController.getXMLFieldName(), ".get(", pName, ");");
	    }
	    return jm;
	}

	public JavaMethod getXMLSetMethod(PropertySG pController, JavaSource pSource) throws SAXException {
	    JavaMethod jm = pSource.newJavaMethod(pController.getXMLSetMethodName(), void.class, JavaSource.PUBLIC);
	    JavaComment jc = jm.newComment();
	    jc.addLine("<p>Sets the 'anyAttribute' named <code>pName</code> to the value <code>pValue</code>.</p>");
	    if (!namespaceList.isAny()) {
	        jc.addLine("@throws IllegalArgumentException The namespace <code>pName.getNamespaceURI()</code> is invalid.");
	    }
	    jc.addLine("@throws NullPointerException Either of the arguments <code>pName</code> or <code>pValue</code> is null.");
	    Parameter pName = jm.addParam(QName.class, "pName");
	    Parameter pValue = jm.addParam(String.class, "pValue");
	    if (!pSource.isInterface()) {
		    jm.addIf(pName, " == null");
		    jm.addThrowNew(NullPointerException.class, JavaSource.getQuoted("The pName argument must not be null."));
		    jm.addEndIf();
		    jm.addIf(pValue, " == null");
		    jm.addThrowNew(NullPointerException.class, JavaSource.getQuoted("The pValue argument must not be null."));
		    jm.addEndIf();
		    getValidNamespaceCheck(pController, jm, pName);
		    jm.addLine(pController.getXMLFieldName(), ".put(", pName, ", ", pValue, ");");
	    }
	    return jm;
	}

	public JavaMethod getXMLUnsetMethod(PropertySG pController, JavaSource pSource) throws SAXException {
	    JavaMethod jm = pSource.newJavaMethod("un" + pController.getXMLSetMethodName(), boolean.class, JavaSource.PUBLIC);
	    JavaComment jc = jm.newComment();
	    jc.addLine("<p>Removes the 'anyAttribute' named <code>pName</code>.</p>");
	    if (!namespaceList.isAny()) {
	        jc.addLine("@throws IllegalArgumentException The namespace <code>pName.getNamespaceURI()</code> is invalid.");
	    }
	    jc.addLine("@throws NullPointerException Either of the arguments <code>pName</code> or <code>pValue</code> is null.");
	    jc.addLine("@return True, if the attribute was set, otherwise false.");
	    Parameter pName = jm.addParam(QName.class, "pName");
	    if (!pSource.isInterface()) {
		    jm.addIf(pName, " == null");
		    jm.addThrowNew(NullPointerException.class, JavaSource.getQuoted("The pName argument must not be null."));
		    jm.addEndIf();
		    getValidNamespaceCheck(pController, jm, pName);
		    jm.addLine("return ", pController.getXMLFieldName(), ".remove(", pName, ") != null;");
	    }
	    return jm;
	}

	public JavaMethod getXMLGetArrayMethod(PropertySG pController, JavaSource pSource) throws SAXException {
	    JavaMethod jm = pSource.newJavaMethod(pController.getXMLGetMethodName() + "Array", WildcardAttribute[].class, JavaSource.PUBLIC);
	    JavaComment jc = jm.newComment();
	    jc.addLine("<p>Returns the array of 'anyAttributes'.</p>");
	    LocalJavaField size = jm.newJavaField(int.class);
	    size.addLine(pController.getXMLFieldName(), ".size()");
	    LocalJavaField result = jm.newJavaField(jm.getType());
	    result.addLine("new ", WildcardAttribute.class, "[", size, "]");
	    DirectAccessible iter = jm.addForCollection(new Object[]{pController.getXMLFieldName(), ".entrySet()"});
	    LocalJavaField entry = jm.newJavaField(Map.Entry.class);
	    entry.addLine("(", Map.Entry.class, ") ", iter, ".next()");
	    if (!pSource.isInterface()) {
		    jm.addLine(result, "[--", size, "] = new ", WildcardAttribute.class, "((",
		               QName.class, ") ", entry, ".getKey(), (", String.class, ") ",
					   entry, ".getValue());");
		    jm.addEndFor();
		    jm.addLine("return ", result, ";");
	    }
	    return jm;
	}

	public void generate(PropertySG pController, JavaSource pSource) throws SAXException {
	    super.generate(pController, pSource);
	    getXMLUnsetMethod(pController, pSource);
	    getXMLGetArrayMethod(pController, pSource);
	    if (!pSource.isInterface()) {
	        getNamespaces(pController, pSource);
	    }
	}
}
