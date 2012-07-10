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

import javax.xml.namespace.QName;

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
public class QNameSG extends AtomicTypeSGImpl {
  public static final JavaQName QNAME_TYPE = JavaQNameImpl.getInstance(QName.class);

  /** <p>Creates a new instance of QNameSG.</p>
   */
  public QNameSG(SGFactory pFactory, SchemaSG pSchema, XSType pType) throws SAXException {
    super(pFactory, pSchema, pType);
  }

  protected String getDatatypeName() { return "QName"; }
  protected JavaQName getDatatypeType() { return QNAME_TYPE; }

  public JavaQName getRuntimeType(SimpleTypeSG pController) { return QNAME_TYPE; }

  public TypedValue getCastFromString(SimpleTypeSG pController, String pValue) throws SAXException {
    QName qName = QName.valueOf(pValue);
	Object result;
	if(qName.getNamespaceURI() == null  ||  qName.getNamespaceURI().length() == 0) {
      result = new Object[]{ "new ", QNAME_TYPE, "(", JavaSource.getQuoted(qName.getLocalPart()), ")" };
    } else {
      result = new Object[]{ "new ", QNAME_TYPE, "(", JavaSource.getQuoted(qName.getNamespaceURI()),
                            ", ", JavaSource.getQuoted(qName.getLocalPart()), ")" };
    }
	return new TypedValueImpl(result, QNAME_TYPE);
  }

  public TypedValue getCastFromString(SimpleTypeSG pController, JavaMethod pMethod, Object pValue, Object pData) throws SAXException {
    return new TypedValueImpl(new Object[]{ pData, ".getDatatypeConverter().parseQName(", pValue, ", ", pData,
                              ".getNamespaceSupport())" }, QNAME_TYPE);
  }

  public TypedValue getCastToString(SimpleTypeSG pController, JavaMethod pMethod, Object pValue, DirectAccessible pData) {
      if (!(pValue instanceof DirectAccessible)) {
          LocalJavaField value = pMethod.newJavaField(QNAME_TYPE);
          value.addLine(pValue);
          pValue = value;
      }
      return new TypedValueImpl(new Object[]{ pData, ".getElementQName(this, ", pValue, ".getNamespaceURI(), ", pValue,
			        							".getLocalPart())" }, String.class);
  }
  
  public void forAllNonNullValues(SimpleTypeSG pController, JavaMethod pMethod, Object pValue, SGlet pSGlet) throws SAXException {
    LocalJavaField f = pMethod.newJavaField(QNAME_TYPE);
    f.addLine(pValue);
    pMethod.addIf(f, " != null");
    pSGlet.generate(pMethod, pValue);
    pMethod.addEndIf();
  }

  public void forAllValues(SimpleTypeSG pController, JavaMethod pMethod, Object pValue, SGlet pSGlet) throws SAXException {
    pSGlet.generate(pMethod, pValue);
  }
}
