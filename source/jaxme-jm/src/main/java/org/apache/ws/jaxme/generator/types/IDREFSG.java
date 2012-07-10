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
package org.apache.ws.jaxme.generator.types;

import org.apache.ws.jaxme.generator.sg.SGFactory;
import org.apache.ws.jaxme.generator.sg.SGlet;
import org.apache.ws.jaxme.generator.sg.SchemaSG;
import org.apache.ws.jaxme.generator.sg.SimpleTypeSG;
import org.apache.ws.jaxme.js.DirectAccessible;
import org.apache.ws.jaxme.js.JavaMethod;
import org.apache.ws.jaxme.js.JavaQName;
import org.apache.ws.jaxme.js.JavaQNameImpl;
import org.apache.ws.jaxme.js.JavaSource;
import org.apache.ws.jaxme.js.LocalJavaField;
import org.apache.ws.jaxme.js.TypedValue;
import org.apache.ws.jaxme.js.impl.TypedValueImpl;
import org.apache.ws.jaxme.xs.XSType;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class IDREFSG extends SimpleTypeSGImpl {
	private static final JavaQName OBJECT_TYPE = JavaQNameImpl.getInstance(Object.class);

	/** <p>Creates a new instance of IDREFSG.</p>
	 */
	public IDREFSG(SGFactory pFactory, SchemaSG pSchema, XSType pType) throws SAXException {
		super(pFactory, pSchema, pType);
	}

	public JavaQName getRuntimeType(SimpleTypeSG pController) { return OBJECT_TYPE; }
	public boolean isCausingParseConversionEvent(SimpleTypeSG pController) { return false; }
	public boolean isXsIdRef(SimpleTypeSG pController) { return true; }

	public TypedValue getCastFromString(SimpleTypeSG pController, JavaMethod pMethod, Object pValue, Object pData) throws SAXException {
	    return new TypedValueImpl(null, OBJECT_TYPE);
	}

	public TypedValue getCastToString(SimpleTypeSG pController, JavaMethod pMethod, Object pValue, DirectAccessible pData) throws SAXException {
		LocalJavaField s = pMethod.newJavaField(String.class);
		pMethod.addIf(pValue, " == null");
		pMethod.addLine(s, " = null;");
		pMethod.addElse();
		pMethod.addLine(s, " = ", pData, ".getXsIdAttribute(", pValue, ");");
		pMethod.addIf(s, " == null");
		pMethod.addThrowNew(IllegalStateException.class,
							JavaSource.getQuoted("ID attribute not set"));
		pMethod.addEndIf();
		pMethod.addEndIf();
		return s;
	}

	public TypedValue getCastFromString(SimpleTypeSG pController, String pValue) throws SAXException {
		throw new IllegalStateException("Unable to resolve ID's at compile time.");
	}

	public void forAllValues(SimpleTypeSG pController, JavaMethod pMethod, Object pValue, SGlet pSGlet) throws SAXException {
	    pSGlet.generate(pMethod, pValue);
	}

	public void forAllNonNullValues(SimpleTypeSG pController, JavaMethod pMethod, Object pValue, SGlet pSGlet) throws SAXException {
	    LocalJavaField f = pMethod.newJavaField(OBJECT_TYPE);
	    f.addLine(pValue);
	    pMethod.addIf(f, " != null");
	    pSGlet.generate(pMethod, pValue);
	    pMethod.addEndIf();
	}

	public Object getEqualsCheck(SimpleTypeSG pController, JavaMethod pMethod, Object pValue1, Object pValue2) throws SAXException {
	    return new Object[]{pValue1, ".equals(", pValue2, ")"};
	}
}
