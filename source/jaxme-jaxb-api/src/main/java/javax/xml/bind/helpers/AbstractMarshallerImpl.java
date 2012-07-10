/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package javax.xml.bind.helpers;

import java.io.File;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;

import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;


/** <p>Default implementation of a Marshaller. The JAXB provider needs
 * to implement only
 * {@link javax.xml.bind.Marshaller#marshal(Object, javax.xml.transform.Result)}.</p>
 *
 * @author JSR-31
 * @since JAXB1.0
 * @see javax.xml.bind.Marshaller 
 */
public abstract class AbstractMarshallerImpl implements Marshaller {
    private String encoding = "UTF-8";
    private String schemaLocation, noNSSchemaLocation;
    private boolean isFormattedOutput = true;
    private boolean fragment;
    private ValidationEventHandler eventHandler = DefaultValidationEventHandler.theInstance;

    /**
     * <p>Creates a new instance of <code>AbstractMarshallerImpl</code>.</p>
     */
    public AbstractMarshallerImpl() {
        // Does nothing.
    }

    /* @see javax.xml.bind.Marshaller#setValidationEventHandler(javax.xml.bind.ValidationEventHandler)}
     */
    public void setEventHandler(ValidationEventHandler pHandler) throws JAXBException {
        eventHandler = pHandler;
    }

    /* @see javax.xml.bind.Marshaller#getValidationEventHandler()}
     */
    public ValidationEventHandler getEventHandler() throws JAXBException {
        return eventHandler;
    }

    /** <p>Public interface to set the properties defined
     * by the {@link javax.xml.bind.Marshaller} interface.
     * Works by invocation of {@link #setEncoding(String)},
     * {@link #setFormattedOutput(boolean)},
     * {@link #setNoNSSchemaLocation(String)}, and
     * {@link #setSchemaLocation(String)} internally.</p>
     * <p>If you want to support additional properties,
     * you have to override this method in a subclass.</p>
     * @throws PropertyException Unknown property name
     */
    public void setProperty(String pName, Object pValue) throws PropertyException {
        if (pName == null) {
            throw new IllegalArgumentException("The property name must not be null.");
        }
        if (Marshaller.JAXB_ENCODING.equals(pName)) {
            setEncoding((String) pValue);
        } else if (Marshaller.JAXB_FORMATTED_OUTPUT.equals(pName)) {
            if (pValue == null) {
                setFormattedOutput(true);
            } else {
                setFormattedOutput(((Boolean) pValue).booleanValue());
            }
        } else if (Marshaller.JAXB_SCHEMA_LOCATION.equals(pName)) {
            setSchemaLocation((String) pValue);
        } else if (Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION.equals(pName)) {
            setNoNSSchemaLocation((String) pValue);
        } else if (Marshaller.JAXB_FRAGMENT.equals(pName)) {
            if (pValue == null) {
                setFragment(false);
            } else {
                setFragment(((Boolean) pValue).booleanValue());
            }
        } else {
            throw new PropertyException("Unknown property name: " + pName);
        }
    }

    /** <p>Public interface to get the properties defined
     * by the {@link javax.xml.bind.Marshaller} interface.
     * Works by invocation of {@link #getEncoding()},
     * {@link #isFormattedOutput()},
     * {@link #getNoNSSchemaLocation()}, and
     * {@link #getSchemaLocation()} internally.</p>
     * <p>If you want to support additional properties,
     * you have to override this method in a subclass.</p>
     * @throws PropertyException Unknown property name
     */
    public Object getProperty(String pName) throws PropertyException {
        if (pName == null) {
            throw new IllegalArgumentException("The property name must not be null.");
        }
        if (Marshaller.JAXB_ENCODING.equals(pName)) {
            return getEncoding();
        } else if (Marshaller.JAXB_FORMATTED_OUTPUT.equals(pName)) {
            return isFormattedOutput() ? Boolean.TRUE : Boolean.FALSE;
        } else if (Marshaller.JAXB_SCHEMA_LOCATION.equals(pName)) {
            return getSchemaLocation();
        } else if (Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION.equals(pName)) {
            return getNoNSSchemaLocation();
        } else if (Marshaller.JAXB_FRAGMENT.equals(pName)) {
            return isFragment() ? Boolean.TRUE : Boolean.FALSE;
        } else {
            throw new PropertyException("Unknown property name: " + pName);
        }
    }

    /** <p>Returns the current output encoding.</p>
     * @see javax.xml.bind.Marshaller#JAXB_ENCODING
     * @return The current encoding, by default "UTF-8".
     */
    protected String getEncoding() {
        return encoding;
    }

    private static final String[] aliases = {
        "UTF-8", "UTF8",
        "UTF-16", "Unicode",
        "UTF-16BE", "UnicodeBigUnmarked",
        "UTF-16LE", "UnicodeLittleUnmarked",
        "US-ASCII", "ASCII",
        "TIS-620", "TIS620",
        
        // taken from the project-X parser
        "ISO-10646-UCS-2", "Unicode",
    
        "EBCDIC-CP-US", "cp037",
        "EBCDIC-CP-CA", "cp037",
        "EBCDIC-CP-NL", "cp037",
        "EBCDIC-CP-WT", "cp037",
    
        "EBCDIC-CP-DK", "cp277",
        "EBCDIC-CP-NO", "cp277",
        "EBCDIC-CP-FI", "cp278",
        "EBCDIC-CP-SE", "cp278",
    
        "EBCDIC-CP-IT", "cp280",
        "EBCDIC-CP-ES", "cp284",
        "EBCDIC-CP-GB", "cp285",
        "EBCDIC-CP-FR", "cp297",
    
        "EBCDIC-CP-AR1", "cp420",
        "EBCDIC-CP-HE", "cp424",
        "EBCDIC-CP-BE", "cp500",
        "EBCDIC-CP-CH", "cp500",
    
        "EBCDIC-CP-ROECE", "cp870",
        "EBCDIC-CP-YU", "cp870",
        "EBCDIC-CP-IS", "cp871",
        "EBCDIC-CP-AR2", "cp918",
        
        // IANA also defines two that JDK 1.2 doesn't handle:
        //  EBCDIC-CP-GR        --> CP423
        //  EBCDIC-CP-TR        --> CP905
    };

    /** <p>Converts the given IANA encoding name into a Java encoding
     * name. This is a helper method for derived subclasses.</p>
     */
    protected String getJavaEncoding(String pEncoding)
            throws UnsupportedEncodingException {
        try {
            "1".getBytes(pEncoding);
            return pEncoding;
        } catch(UnsupportedEncodingException e) {
            // try known alias
            for (int i=0;  i < aliases.length;  i+=2) {
                if (pEncoding.equals(aliases[i])) {
                    "1".getBytes(aliases[i+1]);
                    return aliases[i+1];
                }
            }
            
            throw new UnsupportedEncodingException(pEncoding);
        }
    }

    /** <p>Sets the current output encoding.</p>
     * @see javax.xml.bind.Marshaller#JAXB_ENCODING
     */
    protected void setEncoding(String pEncoding) {
        encoding = pEncoding == null ? "UTF-8" : pEncoding;
    }

    /** <p>Sets the marshallers schema location.
     * Defaults to null.</p>
     * @see javax.xml.bind.Marshaller#JAXB_SCHEMA_LOCATION
     */
    protected void setSchemaLocation(String pSchemaLocation) {
        schemaLocation = pSchemaLocation;
    }

    /** <p>Returns the marshallers schema location.
     * Defaults to null.</p>
     * @see javax.xml.bind.Marshaller#JAXB_SCHEMA_LOCATION
     */
    protected String getSchemaLocation() {
        return schemaLocation;
    }

    /** <p>Sets the marshallers "no namespace" schema location.
     * Defaults to null.</p>
     * @see javax.xml.bind.Marshaller#JAXB_NO_NAMESPACE_SCHEMA_LOCATION
     */
    protected void setNoNSSchemaLocation(String pNoNSSchemaLocation) {
        noNSSchemaLocation = pNoNSSchemaLocation;
    }

    /** <p>Returns the marshallers "no namespace" schema location.
     * Defaults to null.</p>
     * @see javax.xml.bind.Marshaller#JAXB_NO_NAMESPACE_SCHEMA_LOCATION
     */
    protected String getNoNSSchemaLocation() {
        return noNSSchemaLocation;
    }

    /** <p>Sets whether the marshaller will create formatted
     * output or not. By default it does.</p>
     * @see javax.xml.bind.Marshaller#JAXB_FORMATTED_OUTPUT
     */
    protected void setFormattedOutput(boolean pFormattedOutput) {
        isFormattedOutput = pFormattedOutput;
    }

    /**
     * <p>Returns whether the marshaller will create formatted
     * output or not. By default it does.</p>
     * @see javax.xml.bind.Marshaller#JAXB_FORMATTED_OUTPUT
     */
    protected boolean isFormattedOutput() {
        return isFormattedOutput;
    }

    /**
     * Returns, whether the marshaller will create root
     * node events.
     * @see javax.xml.bind.Marshaller#JAXB_FRAGMENT
     */
    protected boolean isFragment() {
        return fragment;
    }

    /**
     * Sets, whether the marshaller will create root
     * node events.
     * @see javax.xml.bind.Marshaller#JAXB_FRAGMENT
     */
    protected void setFragment(boolean v) {
        fragment = v;
    }

    /* @see javax.xml.bind.Marshaller#marshal(Object, java.io.OutputStream)
     */
    public final void marshal(Object pObject, OutputStream pStream)
            throws JAXBException {
        StreamResult sr = new StreamResult();
        sr.setOutputStream(pStream);
        marshal(pObject, sr);
    }

    /* @see javax.xml.bind.Marshaller#marshal(Object, java.io.Writer)
     */
    public final void marshal(Object pObject, Writer pWriter)
    throws JAXBException {
        StreamResult sr = new StreamResult();
        sr.setWriter(pWriter);
        marshal(pObject, sr);
    }

    /* @see javax.xml.bind.Marshaller#marshal(Object, org.xml.sax.ContentHandler)
     */
    public final void marshal(Object pObject, ContentHandler pHandler)
            throws JAXBException {
        SAXResult sr = new SAXResult();
        sr.setHandler(pHandler);
        marshal(pObject, sr);
    }

    /* @see javax.xml.bind.Marshaller#marshal(Object, org.w3c.dom.Node)
     */
    public final void marshal(Object pObject, Node pNode)
            throws JAXBException {
        DOMResult dr = new DOMResult();
        dr.setNode(pNode);
        marshal(pObject, dr);
    }

    /*
     * @see javax.xml.bind.Marshaller#marshal(Object, java.io.File)
     */
    public void marshal(Object pObject, File pFile)
            throws JAXBException {
        StreamResult sr = new StreamResult(pFile);
        marshal(pObject, sr);
    }

    /** <p>This method is unsupported in the default implementation
     * and throws an {@link UnsupportedOperationException}.</p>
     * @throws UnsupportedOperationException This method is not available in the
     *   default implementation.
     */
    public Node getNode(Object obj) throws JAXBException {
        throw new UnsupportedOperationException("This operation is unsupported.");
    }

    public void marshal(Object obj, XMLEventWriter writer)
    throws JAXBException {

        throw new UnsupportedOperationException();
    }

    public void marshal(Object obj, XMLStreamWriter writer)
    throws JAXBException {

        throw new UnsupportedOperationException();
    }

    public void setSchema(Schema schema) {
        throw new UnsupportedOperationException();
    }

    public Schema getSchema() {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("unchecked")
    public void setAdapter(XmlAdapter adapter) {
        if (adapter==null)
            throw new IllegalArgumentException();
        setAdapter((Class)adapter.getClass(),adapter);
    }

    @SuppressWarnings("unchecked")
    public <A extends XmlAdapter> void setAdapter(Class<A> type, A adapter) {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("unchecked")
    public <A extends XmlAdapter> A getAdapter(Class<A> type) {
        throw new UnsupportedOperationException();
    }

    public void setAttachmentMarshaller(AttachmentMarshaller am) {
        throw new UnsupportedOperationException();
    }

    public AttachmentMarshaller getAttachmentMarshaller() {
        throw new UnsupportedOperationException();
    }

    public void setListener(Listener listener) {
        throw new UnsupportedOperationException();
    }

    public Listener getListener() {
        throw new UnsupportedOperationException();
    }
}
