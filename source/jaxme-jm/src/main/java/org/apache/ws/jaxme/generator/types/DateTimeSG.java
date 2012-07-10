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

import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;

import org.apache.ws.jaxme.generator.sg.SGFactory;
import org.apache.ws.jaxme.generator.sg.SGlet;
import org.apache.ws.jaxme.generator.sg.SchemaSG;
import org.apache.ws.jaxme.generator.sg.SimpleTypeSG;
import org.apache.ws.jaxme.impl.DatatypeConverterImpl;
import org.apache.ws.jaxme.impl.JMUnmarshallerImpl;
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
import org.apache.ws.jaxme.xs.util.XsDateTimeFormat;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class DateTimeSG extends AtomicTypeSGImpl {
    public static final JavaQName CALENDAR_TYPE = JavaQNameImpl.getInstance(Calendar.class);
    private static final JavaQName OBJECT_TYPE = JavaQNameImpl.getInstance(Object.class);
    
    /** <p>Creates a new instance of DurationSG.</p>
     */
    public DateTimeSG(SGFactory pFactory, SchemaSG pSchema, XSType pType) throws SAXException {
        super(pFactory, pSchema, pType);
    }

    protected String getDatatypeName() { return "DateTime"; }
	protected JavaQName getDatatypeType() { return CALENDAR_TYPE; }
    protected Class getFormatClass() { return XsDateTimeFormat.class; }
    public JavaQName getRuntimeType(SimpleTypeSG pController) { return CALENDAR_TYPE; }

    public TypedValue getCastFromString(SimpleTypeSG pController, String pValue)
			throws SAXException {
		try {
			Calendar calendar = new DatatypeConverterImpl().parseDate(pValue);
			return new TypedValueImpl(
					new Object[] { "new java.util.GregorianCalendar("
							+ calendar.get(Calendar.YEAR) + ","
							+ calendar.get(Calendar.MONTH) + ","
							+ calendar.get(Calendar.DAY_OF_MONTH) + ")" },
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
        boolean mayBeDate;
        Object format;
        if (pData == null) {
            format = new Object[]{"new ", getFormatClass(), "()"};
            mayBeDate = false;
        } else {
            format = new Object[]{"((", JMUnmarshallerImpl.class, ") ", pData,
                    			  ".getJMUnmarshaller()).get" + getDatatypeName(), "Format()"};
            mayBeDate = true;
        }
        if (!(pValue instanceof DirectAccessible)) {
            LocalJavaField v = pMethod.newJavaField(String.class);
            v.addLine(pValue);
            pValue = v;
        }
        LocalJavaField pos = pMethod.newJavaField(ParsePosition.class);
        pos.addLine("new ", ParsePosition.class, "(0)");
        LocalJavaField cal = pMethod.newJavaField(mayBeDate ? OBJECT_TYPE : pController.getRuntimeType());
        cal.addLine(format, ".parseObject(", pValue, ", ", pos, ");");
        pMethod.addIf(cal, " == null");
        pMethod.addThrowNew(IllegalArgumentException.class,
                			JavaSource.getQuoted("Failed to parse dateTime "),
              			  	" + ", pValue, " + ", JavaSource.getQuoted(" at: "),
							" + ", pValue, ".substring(", pos, ".getErrorIndex())");
        pMethod.addEndIf();
        if (mayBeDate) {
            LocalJavaField result = pMethod.newJavaField(pController.getRuntimeType());
            pMethod.addIf(cal, " instanceof ", Calendar.class);
            pMethod.addLine(result, " = (", Calendar.class, ") ", cal, ";");
            pMethod.addElse();
            pMethod.addLine(result, " = ", Calendar.class, ".getInstance();");
            pMethod.addLine(result, ".setTime((", Date.class, ") ", cal, ");");
            pMethod.addEndIf();
            return result;
        } else {
            return cal;
        }
    }

    public final TypedValue getCastToString(SimpleTypeSG pController, JavaMethod pMethod, Object pValue, DirectAccessible pData) {
		Object v = new Object[]{pData, ".getJMMarshaller().get" + getDatatypeName()
								+ "Format().format(", pValue, ")"};
		return new TypedValueImpl(v, StringSG.STRING_TYPE);
	}

	public void forAllNonNullValues(SimpleTypeSG pController, JavaMethod pMethod, Object pValue, SGlet pSGlet) throws SAXException {
		LocalJavaField f = pMethod.newJavaField(CALENDAR_TYPE);
		f.addLine(pValue);
		pMethod.addIf(f, " != null");
		pSGlet.generate(pMethod, pValue);
		pMethod.addEndIf();
	}

	public void forAllValues(SimpleTypeSG pController, JavaMethod pMethod, Object pValue, SGlet pSGlet) throws SAXException {
		pSGlet.generate(pMethod, pValue);
	}
}
