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

import org.apache.ws.jaxme.generator.sg.ObjectSG;
import org.apache.ws.jaxme.generator.sg.PropertySG;
import org.apache.ws.jaxme.js.JavaField;
import org.apache.ws.jaxme.js.JavaMethod;
import org.apache.ws.jaxme.js.JavaSource;
import org.apache.ws.jaxme.js.Parameter;
import org.apache.ws.jaxme.xs.XSAny;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class AnyElementPropertySG extends JAXBPropertySG {
    protected AnyElementPropertySG(ObjectSG pObjectSG, XSAny pAny) {
        super("any", pObjectSG.getSchema(), pAny, null, null);
    }

    public JavaField getXMLField(PropertySG pController, JavaSource pSource) throws SAXException {
        String fieldName = pController.getXMLFieldName();
        JavaField result = pSource.newJavaField(fieldName, Object.class, JavaSource.PRIVATE);
        return result;
    }

    public JavaMethod getXMLGetMethod(PropertySG pController, JavaSource pSource) throws SAXException {
        String fieldName = pController.getXMLFieldName();
        String methodName = pController.getXMLGetMethodName();
        JavaMethod result = pSource.newJavaMethod(methodName, Object.class, JavaSource.PUBLIC);
        result.addLine("return ", fieldName, ";");
        return result;
    }

    public JavaMethod getXMLSetMethod(PropertySG pController, JavaSource pSource) throws SAXException {
        String fieldName = pController.getXMLFieldName();
        String methodName = pController.getXMLSetMethodName();
        JavaMethod result = pSource.newJavaMethod(methodName, void.class, JavaSource.PUBLIC);
        Parameter param = result.addParam(Object.class, "p" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1));
        result.addLine(fieldName, " = ", param, ";");
        return result;
    }
}
