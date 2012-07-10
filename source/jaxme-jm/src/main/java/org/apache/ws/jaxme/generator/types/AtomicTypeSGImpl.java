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

import javax.xml.bind.DatatypeConverterInterface;

import org.apache.ws.jaxme.generator.sg.AtomicTypeSG;
import org.apache.ws.jaxme.generator.sg.SGFactory;
import org.apache.ws.jaxme.generator.sg.SchemaSG;
import org.apache.ws.jaxme.generator.sg.SimpleTypeSG;
import org.apache.ws.jaxme.impl.DatatypeConverterImpl;
import org.apache.ws.jaxme.js.DirectAccessible;
import org.apache.ws.jaxme.js.JavaField;
import org.apache.ws.jaxme.js.JavaInnerClass;
import org.apache.ws.jaxme.js.JavaMethod;
import org.apache.ws.jaxme.js.JavaQName;
import org.apache.ws.jaxme.js.JavaQNameImpl;
import org.apache.ws.jaxme.js.JavaSource;
import org.apache.ws.jaxme.js.TypedValue;
import org.apache.ws.jaxme.js.impl.TypedValueImpl;
import org.apache.ws.jaxme.xs.XSType;
import org.apache.ws.jaxme.xs.parser.impl.LocSAXException;
import org.xml.sax.SAXException;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public abstract class AtomicTypeSGImpl extends SimpleTypeSGImpl {
  private final AtomicTypeSG atomicTypeSG;

  /** <p>Creates a new instance of AtomicTypeSGImpl.</p>
   */
  protected AtomicTypeSGImpl(SGFactory pFactory, SchemaSG pSchema, XSType pType) throws SAXException {
    super(pFactory, pSchema, pType);
    atomicTypeSG = new org.apache.ws.jaxme.generator.sg.impl.AtomicTypeSGImpl(pType.getSimpleType().getAtomicType());
  }

  public boolean isAtomic(SimpleTypeSG pController) { return true; }

  protected abstract String getDatatypeName();
  protected abstract JavaQName getDatatypeType();

  public AtomicTypeSG getAtomicType(SimpleTypeSG pController) {
    return atomicTypeSG;
  }

   public TypedValue getCastFromString(SimpleTypeSG pController, String pValue)
			throws SAXException {
		try {
			return new TypedValueImpl(new Object[] {
					"javax.xml.bind.DatatypeConverter",
					".parse" + getDatatypeName() + "(\"", pValue, "\")" },
					getDatatypeType());
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
  
  public TypedValue getCastFromString(SimpleTypeSG pController, JavaMethod pMethod, Object pValue, Object pData) throws SAXException {
	Object value;
	if (pData == null) {
      JavaSource js = pMethod.getJavaSource();
      while (js.isInnerClass()) {
        js = ((JavaInnerClass) js).getOuterClass();
      }
      JavaField[] fields = js.getFields();
      JavaQName qName = JavaQNameImpl.getInstance(DatatypeConverterInterface.class);
      JavaField converter = null;
      for (int i = 0;  i < fields.length;  i++) {
        if (qName.equals(fields[i].getType())  &&  JavaSource.DEFAULT_PROTECTION.equals(fields[i].getProtection())  &&
            fields[i].isStatic()  &&  fields[i].isFinal()) {
          converter = fields[i];
          break;
        }
      }
      if (converter == null) {
        converter = js.newJavaField("__dataTypeConverter", qName);
        converter.setStatic(true);
        converter.setFinal(true);
        converter.addLine("new ", DatatypeConverterImpl.class, "()");
      }
      value = new Object[]{converter, ".parse" + getDatatypeName() + "(", pValue, ")"};
    } else {
      value = new Object[]{pData, ".getDatatypeConverter().parse" + getDatatypeName() + "(", pValue, ")"};
    }
	return new TypedValueImpl(value, getDatatypeType());
  }

  public TypedValue getCastToString(SimpleTypeSG pController, JavaMethod pMethod, Object pValue, DirectAccessible pData) {
      return new TypedValueImpl(new Object[]{pData, ".getDatatypeConverter().print" + getDatatypeName() + "(", pValue, ")"},
              String.class);
  }

  public Object getEqualsCheck(SimpleTypeSG pController, JavaMethod pMethod, Object pValue1, Object pValue2)
      throws SAXException {
    return new Object[]{pValue1, ".equals(", pValue2, ")"};
  }

  	public boolean isCausingParseConversionEvent(SimpleTypeSG pController) {
		return true;
	}
}
