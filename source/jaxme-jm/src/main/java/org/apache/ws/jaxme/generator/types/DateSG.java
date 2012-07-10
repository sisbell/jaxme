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

import java.util.Calendar;

import org.apache.ws.jaxme.generator.sg.SGFactory;
import org.apache.ws.jaxme.generator.sg.SchemaSG;
import org.apache.ws.jaxme.generator.sg.SimpleTypeSG;
import org.apache.ws.jaxme.impl.DatatypeConverterImpl;
import org.apache.ws.jaxme.js.TypedValue;
import org.apache.ws.jaxme.js.impl.TypedValueImpl;
import org.apache.ws.jaxme.xs.XSType;
import org.apache.ws.jaxme.xs.parser.impl.LocSAXException;
import org.apache.ws.jaxme.xs.util.XsDateFormat;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class DateSG extends DateTimeSG {
  /** <p>Creates a new instance of DateSG.</p>
   */
  public DateSG(SGFactory pFactory, SchemaSG pSchema, XSType pType) throws SAXException {
    super(pFactory, pSchema, pType);
  }

  protected String getDatatypeName() { return "Date"; }
  protected Class getFormatClass() { return XsDateFormat.class; }
  
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
}
