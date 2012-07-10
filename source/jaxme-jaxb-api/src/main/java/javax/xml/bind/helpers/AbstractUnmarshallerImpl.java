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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.PropertyException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;

import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;


/** <p>This is an abstract default implementation of an
 * {@link javax.xml.bind.Unmarshaller}. Subclasses only need to implement
 * {@link javax.xml.bind.Unmarshaller#getUnmarshallerHandler()},
 * {@link javax.xml.bind.Unmarshaller#unmarshal(org.w3c.dom.Node)}, and
 * {@link #unmarshal(org.xml.sax.XMLReader, org.xml.sax.InputSource)}.</p>
 * 
 * @author JSR-31
 * @since JAXB1.0
 */
public abstract class AbstractUnmarshallerImpl implements Unmarshaller {
    protected boolean validating;
    private XMLReader xmlReader;
    private static SAXParserFactory saxParserFactory;
    private ValidationEventHandler validationEventHandler = DefaultValidationEventHandler.theInstance;

    static {
        saxParserFactory = SAXParserFactory.newInstance();
        saxParserFactory.setNamespaceAware(true);
        saxParserFactory.setValidating(false);
    }

    /** <p>Creates a new instance of AbstractUnmarshallerImpl.</p>
     */
    public AbstractUnmarshallerImpl() {
        // Does nothing.
    }

    /** <p>Creates a configured {@link org.xml.sax.XMLReader}.
     * Unmarshaller is not re-entrant, so we will use a single instance
     * of {@link org.xml.sax.XMLReader}.</p>
     * @throws JAXBException Encapsulates a
     *   {@link javax.xml.parsers.ParserConfigurationException}
     */
    protected XMLReader getXMLReader() throws JAXBException {
        if (xmlReader == null) {
            try {
                SAXParser sp = saxParserFactory.newSAXParser();
                xmlReader = sp.getXMLReader();
            } catch (ParserConfigurationException e) {
                throw new JAXBException("Failed to create a JAXP compliant SAX parser.", e);
            } catch (SAXException e) {
                throw new JAXBException("Failed to create a JAXP compliant SAX parser.", e);
            }
        }
        return xmlReader;
    }

    /* @see javax.xml.bind.Unmarshaller#unmarshal(javax.xml.transform.Source)
     */
    public Object unmarshal(Source pSource) throws JAXBException {
        if (pSource instanceof SAXSource) {
            SAXSource ss = (SAXSource) pSource;
            InputSource is = ss.getInputSource();
            XMLReader xr = ss.getXMLReader();
            if (xr == null) {
                xr = getXMLReader();
            }
            return unmarshal(xr, is);
        } else if (pSource instanceof StreamSource) {
            StreamSource ss = (StreamSource) pSource;
            InputSource is;
            InputStream istream = ss.getInputStream();
            if (istream == null) {
                Reader r = ss.getReader();
                if (r == null) {
                    throw new JAXBException("The specified StreamSource must have configured either of its InputStream or Reader.");
                }
                is = new InputSource(r);
            } else {
                is = new InputSource(istream);
            }
            is.setSystemId(ss.getSystemId());
            is.setPublicId(ss.getPublicId());
            return unmarshal(getXMLReader(), is);
        } else if (pSource instanceof DOMSource) {
            DOMSource ds = (DOMSource) pSource;
            return unmarshal(ds.getNode());
        } else {
            throw new JAXBException("Unsupported type of " + Source.class.getName() +
                    ", expected either of " +
                    SAXSource.class.getName() + ", " +
                    StreamSource.class.getName() + ", or " +
                    DOMSource.class.getName());
        }
    }

    /** <p>Unmarshals an object by using the given instance
     * of {@link org.xml.sax.XMLReader} to parse the XML
     * document read from the byte or character stream
     * given by the {@link org.xml.sax.InputSource}
     * <code>pSource</code>.</p>
     * <p>The implementation should call the method
     * {@link org.xml.sax.XMLReader#setErrorHandler(org.xml.sax.ErrorHandler)}
     * in order to pass errors provided by the SAX parser to the
     * {@link javax.xml.bind.ValidationEventHandler} provided by
     * the client.</p>
     * @throws JAXBException An error occurred while unmarshalling
     *   the JAXB object.
     */
    protected abstract java.lang.Object unmarshal(XMLReader pReader,
            InputSource pSource)
    throws JAXBException;

    /* @see javax.xml.bind.Unmarshaller#unmarshal(org.xml.sax.InputSource)
     */
    public final Object unmarshal(InputSource pSource) throws JAXBException {
        return unmarshal(getXMLReader(), pSource);
    }

    /* @see javax.xml.bind.Unmarshaller#unmarshal(java.net.URL)
     */
    public final java.lang.Object unmarshal(java.net.URL pURL) throws JAXBException {
        InputSource isource;
        try {
            isource = new InputSource(pURL.openStream());
        } catch (IOException e) {
            throw new JAXBException("Failed to open URL " + pURL + ": " + e.getMessage(), e);
        }
        isource.setSystemId(pURL.toExternalForm());
        return unmarshal(getXMLReader(), isource);
    }

    /* @see javax.xml.bind.Unmarshaller#unmarshal(java.io.File)
     */
    public final java.lang.Object unmarshal(java.io.File pFile) throws JAXBException {
        InputSource isource;
        try {
            isource = new InputSource(new FileInputStream(pFile));
        } catch (IOException e) {
            throw new JAXBException("Failed to open file " + pFile + ": " + e.getMessage(), e);
        }
        try {
            isource.setSystemId(pFile.toURL().toExternalForm());
        } catch (IOException e) {
            throw new JAXBException("Malformed URL: " + pFile, e);
        }
        return unmarshal(getXMLReader(), isource);
    }

    /*
     * @see javax.xml.bind.Unmarshaller#unmarshal(java.io.InputStream)
     */
    public final java.lang.Object unmarshal(java.io.InputStream pSource)
            throws JAXBException {
        return unmarshal(getXMLReader(), new InputSource(pSource));
    }

    /*
     * @see javax.xml.bind.Unmarshaller#unmarshal(java.io.Reader)
     */
    public final java.lang.Object unmarshal(java.io.Reader pReader)
            throws JAXBException {
        return unmarshal(getXMLReader(), new InputSource(pReader));
    }

    /* @see javax.xml.bind.Unmarshaller#isValidating()
     */
    public boolean isValidating() throws JAXBException {
        return validating;
    }

    /* @see javax.xml.bind.Unmarshaller#setValidating(boolean)
     */
    public void setValidating(boolean pValidating) throws JAXBException {
        validating = pValidating;
    }

    /* @see javax.xml.bind.Unmarshaller#getEventHandler()
     */
    public ValidationEventHandler getEventHandler()
    throws JAXBException {
        return validationEventHandler;
    }

    /* @see javax.xml.bind.Unmarshaller#setEventHandler(javax.xml.bind.ValidationEventHandler)
     */
    public void setEventHandler(ValidationEventHandler pHandler)
    throws JAXBException {
        validationEventHandler = pHandler;
    }

    /** <p>Helper method to concert a {@link org.xml.sax.SAXException}
     * into an {@link javax.xml.bind.UnmarshalException}.</p>
     * @param pException If the parameter contains a nested instance of
     *   {@link javax.xml.bind.UnmarshalException}, throws that instance.
     *   Otherwise wraps the parameter in a new
     *   {@link javax.xml.bind.UnmarshalException} and throws that.
     */
    protected UnmarshalException createUnmarshalException(SAXException pException) {
        Exception ex = pException.getException();
        if (ex != null  &&  ex instanceof UnmarshalException) {
            return (UnmarshalException) ex;
        }
        return new UnmarshalException(pException);
    }

    /** <p>Always throws a {@link javax.xml.bind.PropertyException},
     * because the default implementation does not support any
     * properties. If you want to change this, override the class.</p>
     * @throws IllegalArgumentException The property name was null.
     * @throws PropertyException The name was not null. :-)
     */
    public void setProperty(String pName, Object pValue) throws PropertyException {
        if (pName == null) {
            throw new IllegalArgumentException("The property name must not be null.");
        }
        throw new PropertyException("Unsupported property name: " + pName);
    }

    /** <p>Always throws a {@link javax.xml.bind.PropertyException},
     * because the default implementation does not support any
     * properties. If you want to change this, override the class.</p>
     * @throws IllegalArgumentException The property name was null.
     * @throws PropertyException The name was not null. :-)
     */
    public Object getProperty(String pName) throws PropertyException {
        if (pName == null) {
            throw new IllegalArgumentException("The property name must not be null.");
        }
        throw new PropertyException("Unsupported property name: " + pName);
    }

    public Object unmarshal(XMLEventReader reader) throws JAXBException {

        throw new UnsupportedOperationException();
    }

    public Object unmarshal(XMLStreamReader reader) throws JAXBException {

        throw new UnsupportedOperationException();
    }

    public <T> JAXBElement<T> unmarshal(Node node, Class<T> expectedType) throws JAXBException {
        throw new UnsupportedOperationException();
    }

    public <T> JAXBElement<T> unmarshal(Source source, Class<T> expectedType) throws JAXBException {
        throw new UnsupportedOperationException();
    }

    public <T> JAXBElement<T> unmarshal(XMLStreamReader reader, Class<T> expectedType) throws JAXBException {
        throw new UnsupportedOperationException();
    }

    public <T> JAXBElement<T> unmarshal(XMLEventReader reader, Class<T> expectedType) throws JAXBException {
        throw new UnsupportedOperationException();
    }

    public void setSchema(Schema schema) {
        throw new UnsupportedOperationException();
    }

    public Schema getSchema() {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("unchecked")
    public void setAdapter(XmlAdapter pAdapter) {
        if (pAdapter==null)
            throw new IllegalArgumentException();
        setAdapter((Class)pAdapter.getClass(), pAdapter);
    }

    @SuppressWarnings("unchecked")
    public <A extends XmlAdapter> void setAdapter(Class<A> type, A adapter) {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("unchecked")
    public <A extends XmlAdapter> A getAdapter(Class<A> type) {
        throw new UnsupportedOperationException();
    }

    public void setAttachmentUnmarshaller(AttachmentUnmarshaller au) {
        throw new UnsupportedOperationException();
    }

    public AttachmentUnmarshaller getAttachmentUnmarshaller() {
        throw new UnsupportedOperationException();
    }

    public void setListener(Listener listener) {
        throw new UnsupportedOperationException();
    }

    public Listener getListener() {
        throw new UnsupportedOperationException();
    }
}
