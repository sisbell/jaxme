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
package org.apache.ws.jaxme.impl;

import java.text.Format;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.DatatypeConverterInterface;
import javax.xml.bind.PropertyException;
import javax.xml.bind.ValidationEventHandler;

import org.apache.ws.jaxme.xs.util.XsDateFormat;
import org.apache.ws.jaxme.xs.util.XsDateTimeFormat;
import org.apache.ws.jaxme.xs.util.XsTimeFormat;



/** <p>Common subclass for JMMarshallerImpl, JMUnmarshallerImpl and
 * JMValidatorImpl.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 * @version $Id: JMControllerImpl.java 279089 2005-09-06 20:23:34Z jochen $
 */
public abstract class JMControllerImpl {
    /** Property prefix for users private settings:
	 * "jaxme.private.". If a property
	 * name starts with this prefix, then the property value is
	 * stored in an internal Map.
	 */
	public static final String JAXME_PRIVATE = "jaxme.private.";
	
	/** Name of the property for setting the
	 * {@link javax.xml.bind.DatatypeConverterInterface}: "jaxme.datatypeConverter".
	 */
	public static final String JAXME_DATATYPE_CONVERTER = "jaxme.datatypeConverter";
	
	/** Property for setting an instance of {@link java.text.Format},
	 * which is being used for parsing and printing <code>xs:dateTime</code>
	 * values. Defaults to an instance of
	 * {@link org.apache.ws.jaxme.xs.util.XsDateTimeFormat}.
	 */
	public static final String JAXME_FORMAT_DATETIME = "jaxme.format.dateTime";
	
	/** Property for setting an instance of {@link java.text.Format},
	 * which is being used for parsing and printing <code>xs:date</code>
	 * values. Defaults to an instance of
	 * {@link org.apache.ws.jaxme.xs.util.XsDateFormat}.
	 */
	public static final String JAXME_FORMAT_DATE = "jaxme.format.date";
	
	/** Property for setting an instance of {@link java.text.Format},
	 * which is being used for parsing and printing <code>xs:time</code>
	 * values. Defaults to an instance of
	 * {@link org.apache.ws.jaxme.xs.util.XsTimeFormat}.
	 */
	public static final String JAXME_FORMAT_TIME = "jaxme.format.time";
	
	private Map privateMap;
	private JAXBContextImpl context;
	private DatatypeConverterInterface datatypeConverter = new DatatypeConverterImpl();
	private Format dateTimeFormat, dateFormat, timeFormat;
	
	/** Sets the property <code>pProperty</code> to the value
	 * <code>pValue</code>.
	 */
	public void setProperty(String pProperty, Object pValue)
	throws PropertyException {
		if (pProperty.startsWith(JAXME_PRIVATE)) {
			if (privateMap == null) {
				privateMap = new HashMap();
			}
			privateMap.put(pProperty, pValue);
			return;
		} else if (pProperty.equals(JAXME_DATATYPE_CONVERTER)) {
			datatypeConverter = (DatatypeConverterInterface) pValue;
			return;
		} else if (JAXME_FORMAT_DATETIME.equals(pProperty)) {
			setDateTimeFormat((Format) pValue);
			return;
		} else if (JAXME_FORMAT_DATE.equals(pProperty)) {
			setDateFormat((Format) pValue);
			return;
		} else if (JAXME_FORMAT_TIME.equals(pProperty)) {
			setTimeFormat((Format) pValue);
			return;
		}
		
		throw new PropertyException("Unknown property: " + pProperty);
	}
	
	/** Returns the value for property <code>pProperty</code>.
	 */
	public Object getProperty(String pProperty) throws PropertyException {
		if (pProperty.startsWith(JAXME_PRIVATE)) {
			if (privateMap == null) { return null; }
			return privateMap.get(pProperty);
		} else if (pProperty.equals(JAXME_DATATYPE_CONVERTER)) {
			return datatypeConverter;
		} else if (JAXME_FORMAT_DATETIME.equals(pProperty)) {
			return getDateTimeFormat();
		} else if (JAXME_FORMAT_DATE.equals(pProperty)) {
			return getDateFormat();
		} else if (JAXME_FORMAT_TIME.equals(pProperty)) {
			return getTimeFormat();
		}
		
		throw new PropertyException("Unknown property: " + pProperty);
	}
	
	protected ValidationEventHandler eventHandler;
	
	/** Returns a users event handler for validation events, if any.
	 * Defaults to null.
	 * @see #setEventHandler(ValidationEventHandler)
	 */
	public ValidationEventHandler getEventHandler() { return eventHandler; }
	
	/** Sets a users event handler for validation events.
	 * Defaults to null.
	 * @see #getEventHandler()
	 */
	public void setEventHandler(ValidationEventHandler pEventHandler) { eventHandler = pEventHandler; }
	
	/** Sets the marshallers or unmarshallers
	 * {@link javax.xml.bind.JAXBContext}. This is used mainly as an
	 * object factory.
	 */
	public void setJAXBContextImpl(JAXBContextImpl pContext) { context = pContext; }
	/** Returns the marshallers or unmarshallers
	 * {@link javax.xml.bind.JAXBContext}. This is used mainly as an
	 * object factory.
	 */
	public JAXBContextImpl getJAXBContextImpl() { return context; }
	
	/** Sets the marshallers or unmarshallers datatype converter.
	 * Defaults to an instance of {@link DatatypeConverterImpl}.
	 */
	public void setDatatypeConverter(DatatypeConverterInterface pConverter) { datatypeConverter = pConverter; }
	/** Returns the marshallers or unmarshallers datatype converter.
	 * Defaults to an instance of {@link DatatypeConverterImpl}.
	 */
	public DatatypeConverterInterface getDatatypeConverter() { return datatypeConverter; }
	
	/** <p>Sets the {@link java.text.Format} for parsing and printing
	 * <code>xs:dateTime</code> values.</p>
	 * @param pFormat An instance of {@link java.text.DateFormat} or an
	 *   instance of {@link org.apache.ws.jaxme.xs.util.XsDateTimeFormat}
	 *   (default).
	 */
	public void setDateTimeFormat(Format pFormat) {
		dateTimeFormat = pFormat;
	}
	
	/** <p>Returns the {@link java.text.Format} for parsing and printing
	 * <code>xs:dateTime</code> values.</p>
	 * @return An instance of {@link java.text.DateFormat} or an
	 *   instance of {@link org.apache.ws.jaxme.xs.util.XsDateTimeFormat}
	 *   (default).
	 */
	public Format getDateTimeFormat() {
		if (dateTimeFormat == null) {
			dateTimeFormat = new XsDateTimeFormat();
		}
		return dateTimeFormat;
	}
	
	/** <p>Sets the {@link java.text.Format} for parsing and printing
	 * <code>xs:date</code> values.</p>
	 * @param pFormat An instance of {@link java.text.DateFormat} or an
	 *   instance of {@link org.apache.ws.jaxme.xs.util.XsDateFormat}
	 *   (default).
	 */
	public void setDateFormat(Format pFormat) {
		dateFormat = pFormat;
	}
	
	/** <p>Returns the {@link java.text.Format} for parsing and printing
	 * <code>xs:date</code> values.</p>
	 * @return An instance of {@link java.text.DateFormat} or an
	 *   instance of {@link org.apache.ws.jaxme.xs.util.XsDateFormat}
	 *   (default).
	 */
	public Format getDateFormat() {
		if (dateFormat == null) {
			dateFormat = new XsDateFormat();
		}
		return dateFormat;
	}
	
	/** <p>Sets the {@link java.text.Format} for parsing and printing
	 * <code>xs:date</code> values.</p>
	 * @param pFormat An instance of {@link java.text.DateFormat} or an
	 *   instance of {@link org.apache.ws.jaxme.xs.util.XsDateFormat}
	 *   (default).
	 */
	public void setTimeFormat(Format pFormat) {
		timeFormat = pFormat;
	}
	
	/** <p>Returns the {@link java.text.Format} for parsing and printing
	 * <code>xs:time</code> values.</p>
	 * @return An instance of {@link java.text.DateFormat} or an
	 *   instance of {@link org.apache.ws.jaxme.xs.util.XsTimeFormat}
	 *   (default).
	 */
	public Format getTimeFormat() {
		if (timeFormat == null) {
			timeFormat = new XsTimeFormat();
		}
		return timeFormat;
	}
}
