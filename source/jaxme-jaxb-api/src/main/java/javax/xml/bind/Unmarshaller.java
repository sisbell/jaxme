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
package javax.xml.bind;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.validation.Schema;

import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/** <p>A <code>Unmarshaller</code> is the opposite of the {@link Marshaller}:
 * It takes as input XML in either of several representations (byte or
 * character stream, DOM node, or a series of SAX events) and returns
 * a JAXB object with the same contents.</p>
 * <p>If the JAXB user demands, the <code>Unmarshaller</code> may also
 * validate the content tree that it sees.</p>
 *
 * @author JSR-31
 * @since JAXB1.0
 * @see JAXBContext
 * @see Marshaller
 * @see Validator
 */
public interface Unmarshaller {
    /**
     * An instance of this class may be registered as
     * an event listener on the {@link Marshaller}.
     * @since JAXB 2.0
     */
    public static abstract class Listener {
        /**
         * This method is invoked before unmarshalling the JAXB object
         * {@code pTarget}.
         * @param pTarget A JAXB object, which has just been created.
         *   No attributes have been set or child elements have been
         *   added.
         * @param pParent The parent object of {@code pTarget}, to
         *   which {@link pTarget} is being added as a child.
         */
        public void beforeUnmarshal(
                @SuppressWarnings("unused") Object pTarget,
                @SuppressWarnings("unused") Object pParent) {
            // Does nothing.
        }

        /**
         * This method is invoked after unmarshalling the JAXB object
         * {@code pTarget}.
         * @param pTarget A JAXB object with its attributes set (the
         *   exception being IDREF attributes) and its child elements
         *   added.
         * @param pParent The parent object of {@code pTarget}, to
         *   which {@link pTarget} is being added as a child. (It
         *   hasn't been added yet.)
         */
        public void afterUnmarshal(
                @SuppressWarnings("unused") Object pTarget,
                @SuppressWarnings("unused") Object parent) {
            // Does nothing.
        }
    }

    /** <p>This method takes opens the given file <code>pFile</code>
     * for input. The Unmarshaller reads the byte stream contained in
     * the file and converts it into an equivalent JAXB object.</p>
     * @param pFile The file being read.
     * @return The JAXB object read from the file.
     * @throws JAXBException An unexpected problem (for example an
     *   IOException) occurred.
     * @throws UnmarshalException The JAXB object is invalid or could
     *   not be read from the byte stream for similar reasons.
     * @throws IllegalArgumentException The parameter was null.
     */
    Object unmarshal(File pFile) throws JAXBException;

    /**
     * <p>The Unmarshaller reads the given byte stream
     * and converts it into an equivalent JAXB object.</p>
     * @param pStream The stream being read.
     * @return The JAXB object read from the byte stream.
     * @throws JAXBException An unexpected problem (for example an
     *   IOException) occurred.
     * @throws UnmarshalException The JAXB object is invalid or could
     *   not be read from the byte stream for similar reasons.
     * @throws IllegalArgumentException The parameter was null.
     */
    Object unmarshal(InputStream pStream) throws JAXBException;

    /**
     * <p>The Unmarshaller reads the given character stream
     * and converts it into an equivalent JAXB object.</p>
     * @param pReader The stream being read.
     * @return The JAXB object read from the character stream.
     * @throws JAXBException An unexpected problem (for example an
     *   IOException) occurred.
     * @throws UnmarshalException The JAXB object is invalid or could
     *   not be read from the byte stream for similar reasons.
     * @throws IllegalArgumentException The parameter was null.
     */
    Object unmarshal(Reader pReader) throws JAXBException;

    /** <p>This method takes connects to the given <code>pURL</code>
     * and opens a byte stream for input. The Unmarshaller reads the
     * byte stream and converts it into an equivalent JAXB object.</p>
     * @param pURL The URL being read.
     * @return The JAXB object read from the URL.
     * @throws JAXBException An unexpected problem (for example an
     *   IOException) occurred.
     * @throws UnmarshalException The JAXB object is invalid or could
     *   not be read from the byte stream for similar reasons.
     * @throws IllegalArgumentException The parameter was null.
     */
    Object unmarshal(URL pURL) throws JAXBException;

    /** <p>The Unmarshaller reads the byte stream or character
     * stream referred by the {@link org.xml.sax.InputSource}
     * and converts it into an equivalent JAXB object.</p>
     * @param pSource The {@link InputSource} referring to a byte or
     *   character stream. It is recommended, that the system ID is
     *   set. This may be required, if the XML in the stream refers
     *   to external entities.
     * @return The JAXB object read from the byte or character stream.
     * @throws JAXBException An unexpected problem (for example an
     *   IOException) occurred.
     * @throws UnmarshalException The JAXB object is invalid or could
     *   not be read from the byte stream for similar reasons.
     * @throws IllegalArgumentException The parameter was null.
     */
    Object unmarshal(InputSource pSource) throws JAXBException;

    /**
     * <p>The Unmarshaller converts the given node into an equivalent
     * JAXB object.</p>
     * @param pNode The node to convert. The JAXB provider must support
     *   documents and elements. Other node types may not work
     *   (document fragments) or cannot work (attributes, text nodes,
     *   and so on).
     * @return The JAXB object read from the DOM node.
     * @throws JAXBException An unexpected problem occurred.
     * @throws UnmarshalException The JAXB object is invalid or could
     *   not be read for similar reasons.
     * @throws IllegalArgumentException The parameter was null.
     */
    Object unmarshal(Node pNode) throws JAXBException;

    /**
     * <p>The Unmarshaller converts the given node into an equivalent
     * JAXB object.</p>
     * @param pNode The node to convert. The JAXB provider must support
     *   documents and elements. Other node types may not work
     *   (document fragments) or cannot work (attributes, text nodes,
     *   and so on).
     * @param pType The result objects type.
     * @return The JAXB object read from the DOM node.
     * @throws JAXBException An unexpected problem occurred.
     * @throws UnmarshalException The JAXB object is invalid or could
     *   not be read for similar reasons.
     * @throws IllegalArgumentException The parameter was null.
     * @since JAXB 2.0
     */
    <T> JAXBElement<T> unmarshal(Node pNode, Class<T> pType) throws JAXBException;

    /** <p>The Unmarshaller reads the XML representation from the given
     * {@link Source} and converts it into an equivalent JAXB object.</p>
     * <p>The JAXB provider must support at least
     * {@link javax.xml.transform.sax.SAXSource},
     * {@link javax.xml.transform.dom.DOMSource}, and
     * {@link javax.xml.transform.stream.StreamSource}. A possible
     * implementation could validate whether the argument is actually
     * an instance of these subinterfaces. If so, it might simply
     * act like
     * {@link #getUnmarshallerHandler()},
     * {@link #unmarshal(org.w3c.dom.Node)},
     * {@link #unmarshal(java.io.InputStream)}, or
     * {@link #unmarshal(org.xml.sax.InputSource)}, respectively.</p>
     * <p><em>Note</em>: If you are not using the standard JAXP
     * mechanism for obtaining an {@link org.xml.sax.XMLReader},
     * then you might create your own SAX parser and invoke the
     * <code>pSource</code> arguments
     * {@link javax.xml.transform.sax.SAXSource#setXMLReader(org.xml.sax.XMLReader)}.
     * The JAXB provider will detect and use your SAX parser.</p>
     *
     * @return The JAXB object read from the DOM node.
     * @param pSource The {@link Source} being read.
     * @throws JAXBException An unexpected problem occurred.
     * @throws UnmarshalException The JAXB object is invalid or could
     *   not be read for similar reasons.
     * @throws IllegalArgumentException The parameter was null.
     */
    Object unmarshal(Source pSource) throws JAXBException;

    /**
     * <p>The Unmarshaller reads the XML representation from the given
     * {@link Source} and converts it into an equivalent JAXB object.</p>
     * <p>The JAXB provider must support at least
     * {@link javax.xml.transform.sax.SAXSource},
     * {@link javax.xml.transform.dom.DOMSource}, and
     * {@link javax.xml.transform.stream.StreamSource}. A possible
     * implementation could validate whether the argument is actually
     * an instance of these subinterfaces. If so, it might simply
     * act like
     * {@link #getUnmarshallerHandler()},
     * {@link #unmarshal(org.w3c.dom.Node)},
     * {@link #unmarshal(java.io.InputStream)}, or
     * {@link #unmarshal(org.xml.sax.InputSource)}, respectively.</p>
     * <p><em>Note</em>: If you are not using the standard JAXP
     * mechanism for obtaining an {@link org.xml.sax.XMLReader},
     * then you might create your own SAX parser and invoke the
     * <code>pSource</code> arguments
     * {@link javax.xml.transform.sax.SAXSource#setXMLReader(org.xml.sax.XMLReader)}.
     * The JAXB provider will detect and use your SAX parser.</p>
     *
     * @return The JAXB object read from the DOM node.
     * @param pSource The {@link Source} being read.
     * @param pType The actual result objects type.
     * @throws JAXBException An unexpected problem occurred.
     * @throws UnmarshalException The JAXB object is invalid or could
     *   not be read for similar reasons.
     * @throws IllegalArgumentException The parameter was null.
     * @since JAXB 2.0
     */
    <T> JAXBElement<T> unmarshal(Source pSource, Class<T> pType) throws JAXBException;

    /**
     * <p>The Unmarshaller reads an XML element from the given
     * {@link XMLStreamReader}.</p>
     * @param pReader The event stream to read.
     * @return The JAXB object read from the event stream.
     * @throws JAXBException An unexpected problem occurred.
     * @throws UnmarshalException The JAXB object is invalid or could
     *   not be read for similar reasons.
     * @throws IllegalArgumentException The parameter was null.
     * @throws IllegalStateException The readers current event
     *   isn't {@link XMLStreamConstants#START_DOCUMENT}, or
     *   {@link XMLStreamConstants#START_ELEMENT}.
     * @since JAXB 2.0
     */
    Object unmarshal(XMLStreamReader pReader) throws JAXBException;

    /**
     * <p>The Unmarshaller reads an XML element from the given
     * {@link XMLStreamReader}.</p>
     * @param pReader The event stream to read.
     * @param pType The actual result objects type.
     * @return The JAXB object read from the event stream.
     * @throws JAXBException An unexpected problem occurred.
     * @throws UnmarshalException The JAXB object is invalid or could
     *   not be read for similar reasons.
     * @throws IllegalArgumentException The parameter was null.
     * @throws IllegalStateException The readers current event
     *   isn't {@link XMLStreamConstants#START_DOCUMENT}, or
     *   {@link XMLStreamConstants#START_ELEMENT}.
     * @since JAXB 2.0
     */
    <T> JAXBElement<T> unmarshal(XMLStreamReader pReader, Class<T> pType) throws JAXBException;

    /**
     * <p>The Unmarshaller reads an XML element from the given
     * {@link XMLEventReader}.</p>
     * @param pReader The event stream to read.
     * @return The JAXB object read from the event stream.
     * @throws JAXBException An unexpected problem occurred.
     * @throws UnmarshalException The JAXB object is invalid or could
     *   not be read for similar reasons.
     * @throws IllegalArgumentException The parameter was null.
     * @throws IllegalStateException The readers current event
     *   isn't {@link XMLStreamConstants#START_DOCUMENT}, or
     *   {@link XMLStreamConstants#START_ELEMENT}.
     * @since JAXB 2.0
     */
    Object unmarshal(XMLEventReader pReader) throws JAXBException;

    /**
     * <p>The Unmarshaller reads an XML element from the given
     * {@link XMLEventReader}.</p>
     * @param pReader The event stream to read.
     * @param pType The actual result objects type.
     * @return The JAXB object read from the event stream.
     * @throws JAXBException An unexpected problem occurred.
     * @throws UnmarshalException The JAXB object is invalid or could
     *   not be read for similar reasons.
     * @throws IllegalArgumentException The parameter was null.
     * @throws IllegalStateException The readers current event
     *   isn't {@link XMLStreamConstants#START_DOCUMENT}, or
     *   {@link XMLStreamConstants#START_ELEMENT}.
     * @since JAXB 2.0
     */
    <T> JAXBElement<T> unmarshal(XMLEventReader pReader, Class<T> pType) throws JAXBException;

    /** <p>Returns a SAX 2 {@link org.xml.sax.ContentHandler}, which is
     * able to parse a SAX event stream and convert it into a JAXB
     * object. This is particularly useful in a stack of SAX
     * handlers. (Think of Apache Cocoon.)</p>
     * <p><em>Note</em>: The JAXB provider may choose to return the
     * same handler again, if the method is invoked more than once.
     * In other words, if you need to have multiple handlers (for
     * example, because you have multiple threads), then you should
     * create multiple <code>Unmarshallers</code>.</p>
     */
    UnmarshallerHandler getUnmarshallerHandler() throws JAXBException;

    /** <p>Sets whether the <code>Unmarshaller</code> is validating
     * the objects that it reads. The default is false.</p>
     * <p><em>Note</em>: A validating unmarshaller will rarely use
     * a validating SAX parser by default! It does so, because the
     * SAX parsers validation and the Unmarshallers builtin
     * validation would most probably validate the same things,
     * resulting in no additional safety at the cost of wasting
     * performance. Second, a SAX parser is not always in use.
     * For example, you do not need a parser when
     * converting a DOM Node. If you insist in a validating XML
     * parser, then you should create your own
     * {@link org.xml.sax.XMLReader} and use the method
     * {@link #unmarshal(javax.xml.transform.Source)}.</p>
     * @param pValidating Whether the <code>Unmarshaller</code> should validate
     *   or not.
     * @throws JAXBException Setting the property failed.
     * @deprecated Since JAXB 2.0, it is recommended to use the
     *   JAXP 1.3 validation framework. See {@link #setSchema(Schema)}
     *   for registering or {@link #getSchema()} for reading a schema
     *   object.
     */
    void setValidating(boolean pValidating) throws JAXBException;

    /** <p>Sets whether the <code>Unmarshaller</code> is validating
     * the objects that it reads. The default is false.</p>
     * @see #setValidating(boolean)
     * @return True, if the <code>Unmarshaller</code> is validating the objects
     *   that it reads, false otherwise.
     * @throws JAXBException Fetching the property value failed.
     * @deprecated Since JAXB 2.0, it is recommended to use the
     *   JAXP 1.3 validation framework. See {@link #setSchema(Schema)}
     *   for registering or {@link #getSchema()} for reading a schema
     *   object.
     */
    boolean isValidating() throws JAXBException;

    /** <p>An application may customize the Unmarshallers behaviour
     * in case of validation problems by supplying a custom handler
     * for validation events. The default handler will trigger an
     * exception in case of errors and fatal errors.</p>
     * @param pHandler The custom event handler or null to restore
     *   the default event handler.
     * @throws JAXBException Setting the handler failed.
     */
    void setEventHandler(ValidationEventHandler pHandler) throws JAXBException;

    /** <p>If the JAXB application has registered a custom handler
     * for validation events, returns that handler. Otherwise returns
     * the default handler, which is triggering an exception in case
     * of errors and fatal errors.</p>
     */
    ValidationEventHandler getEventHandler() throws JAXBException;

    /** <p>Sets the unmarshaller property <code>pName</code> to the value
     * <code>pValue</code>. Note, that the value type depends on the
     * property being set.</p>
     * @param pName The property name.
     * @throws PropertyException An error occurred while processing the property.
     * @throws IllegalArgumentException The name parameter was null.
     */
    void setProperty(String pName, Object pValue) throws PropertyException;

    /** <p>Returnss the unmarshaller property <code>pName</code> to the value
     * <code>pValue</code>. Note, that the value type depends on the
     * property being set.</p>
     * @param pName The property name.
     * @throws PropertyException An error occurred while processing the property.
     * @throws IllegalArgumentException The name parameter was null.
     */
    Object getProperty(String pName) throws PropertyException;
    /**
     * Sets an adapter for conversion of Java/XML types.
     * Shortcut for
     * <pre>setAdapter(adapter.getClass(),adapter)</pre>
     *
     * @see #setAdapter(Class,XmlAdapter)
     * @throws IllegalArgumentException The parameter is null.
     * @throws UnsupportedOperationException The marshaller
     *   doesn't support adapters.
     * @since JAXB 2.0
     */
    @SuppressWarnings("unchecked")
	void setAdapter(XmlAdapter pAdapter );

    /**
     * Sets an adapter for conversion of Java/XML types.
     * @param pType The adapter type, as referenced by
     *   {@link javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter#value()}.
     * @param pAdapter The adapter instance or null to remove the adapter
     *   for {@code pType}.
     * @throws IllegalArgumentException The type parameter is null.
     * @throws UnsupportedOperationException The marshaller
     *   doesn't support adapters.
     * @since JAXB 2.0
     */
    @SuppressWarnings("unchecked")
	public <A extends XmlAdapter> void setAdapter(Class<A> pType, A pAdapter);

    /**
     * Returns the adapter, which is currently registered for the
     * given type.
     * @param pType The adapter type, as referenced by
     *   {@link javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter#value()}.
     * @return Currently registered adapter, if any, or null.
     *
     * @throws IllegalArgumentException The type parameter is null.
     * @throws UnsupportedOperationException The marshaller
     *   doesn't support adapters.
     * @since JAXB 2.0
     */
    @SuppressWarnings("unchecked")
	<A extends XmlAdapter> A getAdapter(Class<A> pType);

    /**
     * Sets an attachment handler, which allows to unmarshal parts
     * of the XML document as binary attachments.
     * @throws IllegalStateException The unmarshaller is currently in use.
     */
    void setAttachmentUnmarshaller(AttachmentUnmarshaller au);

    /**
     * Returns an attachment handler, which allows to unmarshal parts
     * of the XML document as binary attachments.
     */
    AttachmentUnmarshaller getAttachmentUnmarshaller();

    /**
     * Sets a schema object, which is being used for validating
     * the source of an unmarshal operation.
     * @param pSchema A non-null schema object for enabling validation
     *   or null to disable validation.
     * @throws UnsupportedOperationException The {@link Unmarshaller}
     *   doesn't support JAXP 1.3 validation.
     * @since JAXB 2.0
     */
    void setSchema(Schema pSchema);

    /**
     * Returns a schema object, which is being used for validating
     * the source of an unmarshal operation.
     * @return A non-null schema object, if validation is enabled,
     *   or null, if validation is disabled.
     * @throws UnsupportedOperationException The {@link Unmarshaller}
     *   doesn't support JAXP 1.3 validation.
     * @since JAXB 2.0
     */
    Schema getSchema();

    /**
     * Registers an event listener, which is being invoked before
     * and after unmarshalling JAXB objects. If there already is
     * a listener registered, the current one will be replaced.
     * @param pListener The listener to register or null to disable
     *   unmarshalling events.
     * @since JAXB 2.0
     */
    void setListener(Listener pListener);

    /**
     * Returns the currently registered event listener, if any,
     * or null. This event listener is invoked before and after
     * unmarshalling JAXB objects.
     * @return The currently registered event listener, if any, or null.
     * @since JAXB 2.0
     */
    public Listener getListener();
}
