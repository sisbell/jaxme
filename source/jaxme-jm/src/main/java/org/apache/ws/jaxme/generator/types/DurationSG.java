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
import org.apache.ws.jaxme.impl.DatatypeConverterImpl;
import org.apache.ws.jaxme.js.DirectAccessible;
import org.apache.ws.jaxme.js.JavaMethod;
import org.apache.ws.jaxme.js.JavaQName;
import org.apache.ws.jaxme.js.JavaQNameImpl;
import org.apache.ws.jaxme.js.LocalJavaField;
import org.apache.ws.jaxme.js.TypedValue;
import org.apache.ws.jaxme.js.impl.TypedValueImpl;
import org.apache.ws.jaxme.util.Duration;
import org.apache.ws.jaxme.xs.XSType;
import org.apache.ws.jaxme.xs.parser.impl.LocSAXException;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class DurationSG extends AtomicTypeSGImpl {
  public static final JavaQName DURATION_TYPE = JavaQNameImpl.getInstance(Duration.class);

  /** <p>Creates a new instance of DateTimeSG.java.</p>
   */
  public DurationSG(SGFactory pFactory, SchemaSG pSchema, XSType pType) throws SAXException {
    super(pFactory, pSchema, pType);
  }

  public JavaQName getRuntimeType(SimpleTypeSG pController) { return DURATION_TYPE; }

  public TypedValue getCastFromString(SimpleTypeSG pController, JavaMethod pMethod, Object pValue, Object pData) {
      return new TypedValueImpl(new Object[]{DURATION_TYPE, ".valueOf(", pValue, ")"}, DURATION_TYPE);
  }

  public TypedValue getCastFromString(SimpleTypeSG pController, String pValue) throws SAXException {
    try {
    	return new TypedValueImpl("org.apache.ws.jaxme.util.Duration.valueOf(\"" + pValue + "\")", DURATION_TYPE);
	} catch (RuntimeException e) {
		try {
			throw new LocSAXException("Failed to convert string value to "
					+ getDatatypeName() + " instance: " + pValue, getLocator());
		} catch (Exception e1) {
			throw new SAXException("Failed to convert string value to "
					+ getDatatypeName() + " instance: " + pValue);
		}
	}
  }

  public TypedValue getCastToString(SimpleTypeSG pController, JavaMethod pMethod, Object pValue, DirectAccessible pData) {
    return new TypedValueImpl(new Object[]{pValue, ".toString()"}, String.class);
  }

  public void forAllNonNullValues(SimpleTypeSG pController, JavaMethod pMethod, Object pValue, SGlet pSGlet) throws SAXException {
    LocalJavaField f = pMethod.newJavaField(DURATION_TYPE);
    f.addLine(pValue);
    pMethod.addIf(f, " != null");
    pSGlet.generate(pMethod, pValue);
    pMethod.addEndIf();
  }

  public void forAllValues(SimpleTypeSG pController, JavaMethod pMethod, Object pValue, SGlet pSGlet) throws SAXException {
    pSGlet.generate(pMethod, pValue);
  }

  protected String getDatatypeName() { return "Duration"; }
  protected JavaQName getDatatypeType() { return DURATION_TYPE; }
}
