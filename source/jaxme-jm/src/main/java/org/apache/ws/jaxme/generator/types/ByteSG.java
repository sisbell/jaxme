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
import org.apache.ws.jaxme.generator.sg.SchemaSG;
import org.apache.ws.jaxme.generator.sg.SimpleTypeSG;
import org.apache.ws.jaxme.impl.DatatypeConverterImpl;
import org.apache.ws.jaxme.js.JavaQName;
import org.apache.ws.jaxme.js.JavaQNameImpl;
import org.apache.ws.jaxme.js.TypedValue;
import org.apache.ws.jaxme.js.impl.TypedValueImpl;
import org.apache.ws.jaxme.xs.XSType;
import org.apache.ws.jaxme.xs.parser.impl.LocSAXException;
import org.xml.sax.SAXException;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class ByteSG extends PrimitiveTypeSG {
  public static final JavaQName BYTE_TYPE = JavaQNameImpl.getInstance(byte.class);
  public static final JavaQName BYTE_OBJECT_TYPE = JavaQNameImpl.getInstance(Byte.class);

  /** <p>Creates a new instance of LongSG.</p>
   */
  public ByteSG(SGFactory pFactory, SchemaSG pSchema, XSType pType) throws SAXException {
    super(pFactory, pSchema, pType);
  }

  public JavaQName getPrimitiveRuntimeType(SimpleTypeSG pController) { return BYTE_TYPE; }
  protected JavaQName getObjectRuntimeType(SimpleTypeSG pController) { return BYTE_OBJECT_TYPE; }
  protected String getDatatypeName() { return "Byte"; }
  protected JavaQName getDatatypeType() { return BYTE_TYPE; }

  public TypedValue getCastFromString(SimpleTypeSG pController, String pValue) throws SAXException {
    try {
      return new TypedValueImpl(new Object[]{"((", BYTE_TYPE, ") ", new Byte(new DatatypeConverterImpl().parseByte(pValue)), ")"},
			  					BYTE_TYPE);
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
}
