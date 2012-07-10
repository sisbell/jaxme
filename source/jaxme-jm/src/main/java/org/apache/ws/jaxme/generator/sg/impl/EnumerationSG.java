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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.ws.jaxme.generator.sg.SGlet;
import org.apache.ws.jaxme.generator.sg.SimpleTypeSG;
import org.apache.ws.jaxme.generator.sg.SimpleTypeSGChain;
import org.apache.ws.jaxme.js.DirectAccessible;
import org.apache.ws.jaxme.js.JavaConstructor;
import org.apache.ws.jaxme.js.JavaField;
import org.apache.ws.jaxme.js.JavaMethod;
import org.apache.ws.jaxme.js.JavaQName;
import org.apache.ws.jaxme.js.JavaQNameImpl;
import org.apache.ws.jaxme.js.JavaSource;
import org.apache.ws.jaxme.js.LocalJavaField;
import org.apache.ws.jaxme.js.TypedValue;
import org.apache.ws.jaxme.js.impl.TypedValueImpl;
import org.apache.ws.jaxme.xs.XSEnumeration;
import org.apache.ws.jaxme.xs.XSSimpleType;
import org.apache.ws.jaxme.xs.XSType;
import org.apache.ws.jaxme.xs.jaxb.JAXBEnumeration;
import org.apache.ws.jaxme.xs.jaxb.JAXBSimpleType;
import org.apache.ws.jaxme.xs.jaxb.JAXBTypesafeEnumClass;
import org.apache.ws.jaxme.xs.jaxb.JAXBTypesafeEnumMember;
import org.apache.ws.jaxme.xs.parser.impl.LocSAXException;
import org.xml.sax.SAXException;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class EnumerationSG extends SimpleTypeSGChainImpl {
  private class EnumValue {
    private final String value;
    private final String name;
    public EnumValue(String pValue, String pName) {
      value = pValue;
      name = pName;
    }
    public String getName() { return name; }
    public String getValue() { return value; }
  }

  private final JavaQName qName;
  private final EnumValue[] values;

  /** <p>Creates a new instance of EnumerationSG.java.</p>
   */
  protected EnumerationSG(SimpleTypeSGChain o, JavaQName pName, XSType pType) throws SAXException {
    super(o);
    qName = pName;

    XSSimpleType simpleType = pType.getSimpleType();
    XSEnumeration[] enumerations = simpleType.getEnumerations();
    List enums = new ArrayList();
    for (int i = 0;  i < enumerations.length;  i++) {
      XSEnumeration en = enumerations[i];
      String name = null;
      String value = en.getValue();
      if (en instanceof JAXBEnumeration) {
        JAXBEnumeration jaxbEnumeration = (JAXBEnumeration) en;
        JAXBTypesafeEnumMember member = jaxbEnumeration.getJAXBTypesafeEnumMember();
        if (member != null) {
          name = member.getName();
        }
      }

      if (name == null) {
        if (simpleType instanceof JAXBSimpleType) {
          JAXBSimpleType jaxbSimpleType = (JAXBSimpleType) simpleType;
          JAXBTypesafeEnumClass jaxbTypesafeEnumClass = jaxbSimpleType.getJAXBTypesafeEnumClass();
          if (jaxbTypesafeEnumClass != null) {
            for (Iterator iter = jaxbTypesafeEnumClass.getTypesafeEnumMember();  iter.hasNext();  ) {
              JAXBTypesafeEnumMember member = (JAXBTypesafeEnumMember) iter.next();
              if (value.equals(member.getValue())) {
                name = member.getName();
                break;
              }
            }
          }
        }

        if (name == null) {
          if ("".equals(value)) {
            name = "EMPTY";
          } else {
            StringBuffer sb = new StringBuffer();
            char c = value.charAt(0);
            int offset;
            if (Character.isJavaIdentifierStart(c)) {
              sb.append(Character.toUpperCase(c));
              offset = 1;
            } else {
              sb.append("V");
              offset = 0;
            }
            for (int j = offset;  j < value.length();  j++) {
              c = value.charAt(j);
              if (Character.isJavaIdentifierPart(c)) {
                sb.append(Character.toUpperCase(c));
              } else {
                sb.append('_');
              }
            }
            name = sb.toString();
          }
        }
      }

      for (int j = 0;  j < enums.size();  j++) {
        EnumValue ev = (EnumValue) enums.get(j);
        if (name.equals(ev.getName())) {
          throw new LocSAXException("An enumeration value named " + name + " already exists.", en.getLocator());
        }
        if (value.equals(ev.getValue())) {
          throw new LocSAXException("An enumeration value " + value + " already exists.", en.getLocator());
        }
      }
      enums.add(new EnumValue(value, name));
    }
    values = (EnumValue[]) enums.toArray(new EnumValue[enums.size()]);
  }

  public TypedValue getCastFromString(SimpleTypeSG pController, JavaMethod pMethod, Object pValue, Object pData) {
    return new TypedValueImpl(new Object[]{qName, ".fromString(", pValue, ")"}, qName);
  }

  public TypedValue getCastFromString(SimpleTypeSG pController, String pValue) throws SAXException {
    for (int i = 0;  i < values.length;  i++) {
      if (values[i].getValue().equals(pValue)) {
        return new TypedValueImpl(new Object[]{qName, ".", values[i].getName()}, qName);
      }
    }
    return super.getCastFromString(pController, pValue);
  }

  public TypedValue getCastToString(SimpleTypeSG pController, JavaMethod pMethod, Object pValue, DirectAccessible pData) {
    return new TypedValueImpl(new Object[]{pValue, ".toString()"}, String.class);
  }

  public JavaQName getRuntimeType(SimpleTypeSG pController) { return qName; }

  public JavaSource getEnumClass(SimpleTypeSG pController) throws SAXException {
    JavaQName valueType = super.getRuntimeType(pController);
    JavaQName qNameArray = JavaQNameImpl.getArray(qName);

    JavaSource js = pController.getSchema().getJavaSourceFactory().newJavaSource(qName, JavaSource.PUBLIC);
    js.addImplements(Serializable.class);

    JavaField name = js.newJavaField("name", String.class, JavaSource.PRIVATE);
    name.setFinal(true);
    JavaField value = js.newJavaField("value", valueType, JavaSource.PRIVATE);
    value.setFinal(true);
    JavaField lexicalValue = js.newJavaField("lexicalValue", String.class, JavaSource.PRIVATE);
    lexicalValue.setFinal(true);

    List instanceParams = new ArrayList();
    for (int i = 0;  i < values.length;  i++) {
      JavaField _f = js.newJavaField("_" + values[i].getName(), String.class, JavaSource.PUBLIC);
      _f.setStatic(true);
      _f.setFinal(true);
      _f.addLine(JavaSource.getQuoted(values[i].getName()));

      JavaField f = js.newJavaField(values[i].getName(), qName, JavaSource.PUBLIC);
      f.addLine("new ", qName, "(", JavaSource.getQuoted(values[i].getName()), ", ",
                super.getCastFromString(pController, values[i].getValue()), ", ",
                JavaSource.getQuoted(values[i].getValue()), ");");
      f.setStatic(true);
      f.setFinal(true);
      if (!instanceParams.isEmpty()) instanceParams.add(", ");
      instanceParams.add(f);
    }
    JavaField instances = js.newJavaField("instances", qNameArray, JavaSource.PRIVATE);
    instances.addLine(new Object[]{"new ", qNameArray, "{", instanceParams, "}"});
    instances.setStatic(true);
    instances.setFinal(true);

    JavaConstructor con = js.newJavaConstructor(JavaSource.PRIVATE);
    DirectAccessible pName = con.addParam(String.class, "pName");
    DirectAccessible pValue = con.addParam(super.getRuntimeType(pController), "pValue");
    DirectAccessible pLexicalValue = con.addParam(String.class, "pLexicalValue");
    con.addLine(name, " = ", pName, ";");
    con.addLine(value, " = ", pValue, ";");
    con.addLine(lexicalValue, " = ", pLexicalValue, ";");

    JavaMethod toStringMethod = js.newJavaMethod("toString", String.class, JavaSource.PUBLIC);
    toStringMethod.addLine("return ", lexicalValue, ";");

    JavaMethod getValueMethod = js.newJavaMethod("getValue", valueType, JavaSource.PUBLIC);
    getValueMethod.addLine("return ", value, ";");

    JavaMethod getNameMethod = js.newJavaMethod("getName", String.class, JavaSource.PUBLIC);
    getNameMethod.addLine("return ", name, ";");

    JavaMethod getInstancesMethod = js.newJavaMethod("getInstances", qNameArray, JavaSource.PUBLIC);
    getInstancesMethod.setStatic(true);
    getInstancesMethod.addLine("return ", instances, ";");

    JavaMethod fromValueMethod = js.newJavaMethod("fromValue", qName, JavaSource.PUBLIC);
    pValue = fromValueMethod.addParam(valueType, "pValue");
    fromValueMethod.setStatic(true);
    DirectAccessible i = fromValueMethod.addForArray(instances);
    fromValueMethod.addIf(pController.getEqualsCheck(fromValueMethod, new Object[]{instances, "[", i, "].value"}, pValue));
    fromValueMethod.addLine("return ", instances, "[", i, "];");
    fromValueMethod.addEndIf();
    fromValueMethod.addEndFor();
    fromValueMethod.addThrowNew(IllegalArgumentException.class, JavaSource.getQuoted("Invalid value: "),
                                " + ", pValue);

    JavaMethod fromNameMethod = js.newJavaMethod("fromName", qName, JavaSource.PUBLIC);
    pName = fromNameMethod.addParam(String.class, "pName");
    fromNameMethod.setStatic(true);
    i = fromNameMethod.addForArray(instances);
    fromNameMethod.addIf(instances, "[", i, "].name.equals(", pName, ")");
    fromNameMethod.addLine("return ", instances, "[", i, "];");
    fromNameMethod.addEndIf();
    fromNameMethod.addEndFor();
    fromNameMethod.addThrowNew(IllegalArgumentException.class, JavaSource.getQuoted("Invalid name: "),
                                " + ", pName);

    JavaMethod fromStringMethod = js.newJavaMethod("fromString", qName, JavaSource.PUBLIC);
    pValue = fromStringMethod.addParam(String.class, "pValue");
    fromStringMethod.setStatic(true);
    fromStringMethod.addLine("return ", fromValueMethod, "(",
                             super.getCastFromString(pController, fromStringMethod, pValue, null), ");");

    if (js.isImplementing(Serializable.class)) {
      JavaMethod readResolveMethod = js.newJavaMethod("readResolve", Object.class, JavaSource.PRIVATE);
      readResolveMethod.addLine("return ", fromValueMethod, "(", value, ");");
    }
    return js;
  }

  public void forAllNonNullValues(SimpleTypeSG pController, JavaMethod pMethod, Object pValue, SGlet pSGlet) throws SAXException {
    LocalJavaField f = pMethod.newJavaField(qName);
    f.addLine(pValue);
    pMethod.addIf(f, " != null");
    pSGlet.generate(pMethod, f);
    pMethod.addEndIf();
  }

  public void generate(SimpleTypeSG pController) throws SAXException {
    super.generate(pController);
    getEnumClass(pController);
  }

  public void generate(SimpleTypeSG pController, JavaSource pSource) throws SAXException {
    super.generate(pController, pSource);
    getEnumClass(pController);
  }

    public boolean isCausingParseConversionEvent(SimpleTypeSG pController) {
		return true;
    }
}
