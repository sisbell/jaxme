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

import java.math.BigInteger;

import org.apache.ws.jaxme.generator.sg.AtomicTypeSG;
import org.apache.ws.jaxme.generator.sg.SGFactory;
import org.apache.ws.jaxme.generator.sg.SGlet;
import org.apache.ws.jaxme.generator.sg.SchemaSG;
import org.apache.ws.jaxme.generator.sg.SimpleTypeSG;
import org.apache.ws.jaxme.impl.DatatypeConverterImpl;
import org.apache.ws.jaxme.js.DirectAccessible;
import org.apache.ws.jaxme.js.JavaMethod;
import org.apache.ws.jaxme.js.JavaQName;
import org.apache.ws.jaxme.js.JavaQNameImpl;
import org.apache.ws.jaxme.js.JavaSource;
import org.apache.ws.jaxme.js.LocalJavaField;
import org.apache.ws.jaxme.js.TypedValue;
import org.apache.ws.jaxme.js.impl.TypedValueImpl;
import org.apache.ws.jaxme.xs.XSType;
import org.apache.ws.jaxme.xs.parser.impl.LocSAXException;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class IntegerSG extends AtomicTypeSGImpl {
  public static final JavaQName INTEGER_TYPE = JavaQNameImpl.getInstance(BigInteger.class);

  /** <p>Creates a new instance of IntegerSG.</p>
   */
  public IntegerSG(SGFactory pFactory, SchemaSG pSchema, XSType pType) throws SAXException {
    super(pFactory, pSchema, pType);
  }

  public JavaQName getRuntimeType(SimpleTypeSG pController) { return INTEGER_TYPE; }
  protected String getDatatypeName() { return "Integer"; }
  protected JavaQName getDatatypeType() { return INTEGER_TYPE; }

  public TypedValue getCastFromString(SimpleTypeSG pController, String pValue) throws SAXException {
    try {
    	return new TypedValueImpl("new java.math.BigInteger(\"" + pValue + "\")", INTEGER_TYPE);
    } catch (NumberFormatException e) {
      throw new LocSAXException("Failed to convert string value to BigInteger: " + pValue, getLocator());
    }
  }

  public void forAllNonNullValues(SimpleTypeSG pController, JavaMethod pMethod, Object pValue, SGlet pSGlet) throws SAXException {
    LocalJavaField f = pMethod.newJavaField(INTEGER_TYPE);
    f.addLine(pValue);
    pMethod.addIf(f, " != null");
    pSGlet.generate(pMethod, pValue);
    pMethod.addEndIf();
  }

  public void forAllValues(SimpleTypeSG pController, JavaMethod pMethod, Object pValue, SGlet pSGlet) throws SAXException {
    pSGlet.generate(pMethod, pValue);
  }

  public void addValidation(SimpleTypeSG pController, JavaMethod pMethod,
		  					final DirectAccessible pValue) {
    AtomicTypeSG atomicType = pController.getAtomicType();
    Long totalDigits = atomicType.getTotalDigits();
    if (totalDigits != null) {
      pMethod.addIf(pValue, ".signum()", " == -1");
      pMethod.addIf(pValue, ".toString().length() - 1", " > ", totalDigits);
      pMethod.addThrowNew(IllegalArgumentException.class,
                          JavaSource.getQuoted("Length of " + totalDigits + " digits exceeded: "), " + ", pValue);
      pMethod.addEndIf();
      pMethod.addElse();
      pMethod.addIf(pValue, ".toString().length()", " > ", totalDigits);
      pMethod.addThrowNew(IllegalArgumentException.class,
                          JavaSource.getQuoted("Length of " + totalDigits + " digits exceeded: "), " + ", pValue);
      pMethod.addEndIf();
      pMethod.addEndIf();
    }
  }
}
