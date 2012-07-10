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
import org.apache.ws.jaxme.js.LocalJavaField;
import org.apache.ws.jaxme.js.TypedValue;
import org.apache.ws.jaxme.js.impl.TypedValueImpl;
import org.apache.ws.jaxme.xs.XSType;
import org.xml.sax.SAXException;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public abstract class PrimitiveTypeSG extends AtomicTypeSGImpl {
  /** <p>Creates a new instance of PrimitiveTypeSG.java.</p>
   */
  protected PrimitiveTypeSG(SGFactory pFactory, SchemaSG pSchema, XSType pType) throws SAXException {
    super(pFactory, pSchema, pType);
  }

  protected boolean isUnsigned() { return false; }
  protected abstract JavaQName getObjectRuntimeType(SimpleTypeSG pController);
  protected abstract JavaQName getPrimitiveRuntimeType(SimpleTypeSG pController);
  public JavaQName getRuntimeType(SimpleTypeSG pController) {
    return pController.isNullable() ? getObjectRuntimeType(pController) : getPrimitiveRuntimeType(pController);
  }

  public TypedValue getCastFromString(SimpleTypeSG pController, JavaMethod pMethod, Object pValue, Object pData) throws SAXException {
    TypedValue v = super.getCastFromString(pController, pMethod, pValue, pData);
    if (pController.isNullable()) {
		JavaQName objectType = getObjectRuntimeType(pController);
		v = new TypedValueImpl(new Object[]{"new ", objectType, "(", v, ")"},
			  			       objectType);
    }
    return v;
  }

  public TypedValue getCastToString(SimpleTypeSG pController, JavaMethod pMethod, Object pValue, DirectAccessible pData) {
    if (pController.isNullable()) {
      pValue = new Object[]{pValue, "." + getPrimitiveRuntimeType(pController).getClassName() +"Value()"};
    }
    return super.getCastToString(pController, pMethod, pValue, pData);
  }

  public void forAllValues(SimpleTypeSG pController, JavaMethod pMethod, Object pValue, SGlet pSGlet) throws SAXException {
    pSGlet.generate(pMethod, pValue);
  }

  public void forAllNonNullValues(SimpleTypeSG pController, JavaMethod pMethod, Object pValue, SGlet pSGlet) throws SAXException {
    if (pController.isNullable()) {
      LocalJavaField f = pMethod.newJavaField(getObjectRuntimeType(pController));
      f.addLine(pValue);
      pMethod.addIf(f, " != null");
      pSGlet.generate(pMethod, pValue);
      pMethod.addEndIf();
    } else {
      pSGlet.generate(pMethod, pValue);
    }
  }

  public Object getEqualsCheck(SimpleTypeSG pController, JavaMethod pMethod, Object pValue1, Object pValue2) throws SAXException {
    if (pController.isNullable()) {
      return super.getEqualsCheck(pController, pMethod, pValue1, pValue2);
    } else {
      return new Object[]{pValue1, " == ", pValue2};
    }
  }
}
