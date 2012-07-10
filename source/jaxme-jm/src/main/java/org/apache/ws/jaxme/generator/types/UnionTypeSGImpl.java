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

import java.util.ArrayList;
import java.util.List;

import org.apache.ws.jaxme.generator.sg.Context;
import org.apache.ws.jaxme.generator.sg.SGFactory;
import org.apache.ws.jaxme.generator.sg.SGlet;
import org.apache.ws.jaxme.generator.sg.SchemaSG;
import org.apache.ws.jaxme.generator.sg.SimpleTypeSG;
import org.apache.ws.jaxme.generator.sg.TypeSG;
import org.apache.ws.jaxme.generator.sg.UnionTypeSG;
import org.apache.ws.jaxme.js.DirectAccessible;
import org.apache.ws.jaxme.js.JavaMethod;
import org.apache.ws.jaxme.js.JavaQName;
import org.apache.ws.jaxme.js.JavaQNameImpl;
import org.apache.ws.jaxme.js.JavaSource;
import org.apache.ws.jaxme.js.LocalJavaField;
import org.apache.ws.jaxme.js.TypedValue;
import org.apache.ws.jaxme.xs.XSType;
import org.apache.ws.jaxme.xs.XSUnionType;
import org.apache.ws.jaxme.xs.parser.impl.LocSAXException;
import org.apache.ws.jaxme.xs.xml.XsQName;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class UnionTypeSGImpl extends SimpleTypeSGImpl {
  public static final JavaQName OBJECT_TYPE = JavaQNameImpl.getInstance(Object.class);
  private final XSUnionType unionType;
  private final TypeSG[] memberTypes;
  private final UnionTypeSG unionTypeSG;

  /** <p>Creates a new instance of UnionTypeSG.</p>
   */
  public UnionTypeSGImpl(SGFactory pFactory, SchemaSG pSchema, XSType pType,
                          Context pClassContext, XsQName pName) throws SAXException {
    super(pFactory, pSchema, pType);
    unionType = pType.getSimpleType().getUnionType();
    XSType[] mTypes = unionType.getMemberTypes();
    List members = new ArrayList();
    for (int i = 0;  i < mTypes.length;  i++) {
      TypeSG typeSG = getFactory().getTypeSG(mTypes[i], pClassContext, pName, null);
      typeSG.getSimpleTypeSG().setNullable(true);
      members.add(typeSG);
    }
    memberTypes = (TypeSG[]) members.toArray(new TypeSG[members.size()]);

    unionTypeSG = new UnionTypeSG(){
      public TypeSG[] getMemberTypes() { return UnionTypeSGImpl.this.getMemberTypes(); }
    };
  }

  protected TypeSG[] getMemberTypes() { return memberTypes; }
  public JavaQName getRuntimeType(SimpleTypeSG pController) { return OBJECT_TYPE; }
  public boolean isUnion(SimpleTypeSG pController) { return true; }

  public TypedValue getCastFromString(SimpleTypeSG pController, String pValue) throws SAXException {
    Exception e = null;
    for (int i = 0;  i < memberTypes.length;  i++) {
      try {
        return memberTypes[i].getSimpleTypeSG().getCastFromString(pValue);
      } catch (Exception ex) {
        if (i == 0) {
          e = ex;
        }
      }
    }
    throw new LocSAXException("Failed to convert value " + pValue, getLocator(), e);
  }

  public TypedValue getCastFromString(SimpleTypeSG pController, JavaMethod pMethod, Object pValue,
                                      Object pData) throws SAXException {
    DirectAccessible value;
    if (pValue instanceof DirectAccessible) {
      value = (DirectAccessible) pValue;
    } else {
      LocalJavaField v = pMethod.newJavaField(String.class);
      v.addLine(pValue);
      value = v;
    }

    LocalJavaField result = pMethod.newJavaField(pController.getRuntimeType());
    for (int i = 0;  i < memberTypes.length;  i++) {
      pMethod.addTry();
      pMethod.addLine(result, " = ", memberTypes[i].getSimpleTypeSG().getCastFromString(pMethod, value, pData), ";");
      pMethod.addCatch(RuntimeException.class);
    }
    pMethod.addThrowNew(IllegalArgumentException.class,
                        JavaSource.getQuoted("Invalid union value: "), " + ", pValue);
    for (int i = 0;  i < memberTypes.length;  i++) {
      pMethod.addEndTry();
    }
    return result;
  }

  public TypedValue getCastToString(SimpleTypeSG pController, JavaMethod pMethod, Object pValue,
                                    DirectAccessible pData) throws SAXException {
    DirectAccessible value;
    if (pValue instanceof DirectAccessible) {
      value = (DirectAccessible) pValue;
    } else {
      LocalJavaField v = pMethod.newJavaField(Object.class);
      v.addLine(pValue);
      value = v;
    }

    LocalJavaField result = pMethod.newJavaField(String.class);
    for (int i = 0;  i < memberTypes.length;  i++) {
      JavaQName memberRuntimeType = memberTypes[i].getSimpleTypeSG().getRuntimeType();
      pMethod.addIf(i == 0, value, " instanceof ", memberRuntimeType);
      Object v = OBJECT_TYPE.equals(memberRuntimeType) ?
        (Object) value :
        new Object[]{"((", memberRuntimeType, ") ", value, ")"};
      pMethod.addLine(result, " = ", memberTypes[i].getSimpleTypeSG().getCastToString(pMethod, v, pData), ";");
    }
    pMethod.addElse();
    pMethod.addThrowNew(IllegalStateException.class,
                        JavaSource.getQuoted("Invalid union object type: "), " + ", value, ".getClass().getName()");
    pMethod.addEndIf();

    return result;
  }

  public UnionTypeSG getUnionType(SimpleTypeSG pController) { return unionTypeSG; }

  public void forAllNonNullValues(SimpleTypeSG pController, JavaMethod pMethod, Object pValue, SGlet pSGlet) throws SAXException {
    LocalJavaField f = pMethod.newJavaField(OBJECT_TYPE);
    f.addLine(pValue);
    pMethod.addIf(f, " != null");
    pSGlet.generate(pMethod, pValue);
    pMethod.addEndIf();
  }

  public void forAllValues(SimpleTypeSG pController, JavaMethod pMethod, Object pValue, SGlet pSGlet) throws SAXException {
    pSGlet.generate(pMethod, pValue);
  }

  public Object getEqualsCheck(SimpleTypeSG pController, JavaMethod pMethod, Object pValue1, Object pValue2) throws SAXException {
    throw new IllegalStateException("Not implemented");
  }

    public boolean isCausingParseConversionEvent(SimpleTypeSG pController) {
		for (int i = 0;  i < memberTypes.length;  i++) {
			if (!memberTypes[i].getSimpleTypeSG().isCausingParseConversionEvent()) {
				return false;
			}
		}
		return true;
	}
}
