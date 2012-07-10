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
import java.io.OutputStream;
import java.io.Writer;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.validation.Schema;

import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;


/** <p>An instance of <code>Marshaller</code> may be obtained by the
 * JAXB user to serialize JAXB objects to various flavours of XML.
 * The created XML may be:
 * <table border="1">
 *   <tr><th>A byte stream<br>{@link java.io.OutputStream}</th>
 *     <td>{@link #marshal(Object, java.io.OutputStream)}</td>
 *   </tr>
 *   <tr><th>A character stream<br>{@link java.io.Writer}</th>
 *     <td>{@link #marshal(Object, java.io.Writer)}</td>
 *   </tr>
 * </table>
 *
 * @author JSR-31
 * @since JAXB1.0
 */
public interface Marshaller {
    /**
     * An instance of this class may be registered as
     * an event listener on the {@link Marshaller}.
     * @since JAXB 2.0
     */
    public static abstract class Listener {
        /**
         * This method is invoked before marshalling the JAXB object
         * {@code pObject}.
         * @param pObject An object, which has been passed to either
         *   of the marshal methods.
         */
        public void beforeMarshal(Object pObject) {
            // Does nothing.
        }

        /**
         * This method is invoked after marshalling the JAXB object
         * {@code pObject}.
         * @param pObject An object, which has been passed to either
         *   of the marshal methods.
         */
        public void afterMarshal(Object pObject) {
            // Does nothing.
        }
    }

    /** <p>Name of the property that allows to choose the encoding.
     * The encoding applies when marshalling to an
     * {@link java.io.OutputStream}, to a {@link java.io.Writer},
     * or to a {@link javax.xml.transform.stream.StreamResult}.
     * It is ignored, if the target is a {@link org.xml.sax.ContentHandler},
     * a DOM {@link org.w3c.dom.Node}, or another flavour of
     * {@link javax.xml.transform.Result}.</p>
     * <p>The encoding may be used both to choose the characters
     * being escaped (as <samp>&amp;#ddd;</samp>) or to convert
     * characters into byte sequences.</p>
     * <p>The property value is the encoding name, for example
     * <samp>UTF-8</samp>, which is the default. Note, that a
     * JAXB implementation need not support other encodings than
     * <samp>UTF-8</samp>, <samp>UTF-16</samp>, and <samp>US-ASCII</samp>.
     * Usually you may expect that the encodings supported by the
     * JVM itself will work for the <code>Marshaller</code> as well.</p>
     *
     * @see #marshal(Object, java.io.OutputStream)
     * @see #marshal(Object, java.io.Writer)
     * @see #marshal(Object, javax.xml.transform.Result)
     */
    public static final String JAXB_ENCODING = "jaxb.encoding";

    /** <p>Name of the property that allows to choose whether the
     * generated XML should be formatted (human readable, indented)
     * or not. By default the generated XML is formatted.
     * The property value is an instance of {@link Boolean},
     * by default {@link Boolean#TRUE}.</p>
     * <p>The formatting property is supported when marshalling to
     * a character or byte stream. It is ignored, if the target is
     * a {@link org.xml.sax.ContentHandler}, a DOM
     * {@link org.w3c.dom.Node}, or an instance of
     * {@link javax.xml.transform.Result}, the exception being a
     * {@link javax.xml.transform.stream.StreamResult}, which is
     * in fact a character or byte stream.</p>
     *
     * @see #marshal(Object, java.io.OutputStream)
     * @see #marshal(Object, java.io.Writer)
     * @see #marshal(Object, javax.xml.transform.Result)
     */
    public static final String JAXB_FORMATTED_OUTPUT = "jaxb.formatted.output";

    /** <p>If this property is set to another value than null,
     * then the <code>Marshaller</code> will create an attribute
     * <samp>xsi:schemaLocation</samp>. The attribute value is
     * the property value. By default the property is set to
     * null and no such attribute is generated.</p>
     *
     * @see #JAXB_NO_NAMESPACE_SCHEMA_LOCATION
     */
    public static final String JAXB_SCHEMA_LOCATION = "jaxb.schemaLocation";

    /** <p>If this property is set to another value than null,
     * then the <code>Marshaller</code> will create an attribute
     * <samp>xsi:noNamespaceSchemaLocation</samp>. The attribute
     * value is the property value. By default the property is set to
     * null and no such attribute is generated.</p>
     *
     * @see #JAXB_SCHEMA_LOCATION
     */
    public static final String JAXB_NO_NAMESPACE_SCHEMA_LOCATION = "jaxb.noNamespaceSchemaLocation";

    /**
     * If this property is set to true, then the marshaller will generate
     * document level events like {@link ContentHandler#startDocument()},
     * or {@link ContentHandler#endDocument()}.
     */
    public static final String JAXB_FRAGMENT = "jaxb.fragment";

    /** <p>Marshals the given JAXB object <code>pObject</code> to the
     * {@link javax.xml.transform.Result} <code>pTarget</code>. All
     * JAXB provider must support {@link javax.xml.transform.dom.DOMResult},
     * {@link javax.xml.transform.sax.SAXResult}, and
     * {@link javax.xml.transform.stream.StreamResult}, which can easily
     * be mapped to {@link #marshal(Object, org.w3c.dom.Node)},
     * {@link #marshal(Object, org.xml.sax.ContentHandler)},
     * {@link #marshal(Object,java.io.OutputStream)}, or
     * {@link #marshal(Object,java.io.Writer)}. The use of a
     * {@link javax.xml.transform.Result} as a target isn't
     * portable beyond these subinterfaces.</p>
     *
     * @param pObject The JAXB object being marshalled.
     * @param pTarget The {@link Result} being created.
     * @throws JAXBException An unexcpected problem occurred. This may be used,
     *   for example, to throw a nested {@link java.io.IOException}.
     * @throws MarshalException Whereever possible, one should throw a
     *   {@link MarshalException}, and not a {@link JAXBException}.
     * @throws IllegalArgumentException Any of the parameters was null.
     * @see #JAXB_SCHEMA_LOCATION
     * @see #JAXB_NO_NAMESPACE_SCHEMA_LOCATION
     */
    void marshal(Object pObject, Result pTarget) throws JAXBException;

    /** <p>Marshals the given JAXB object <code>pObject</code> and
     * serializes it into the byte stream <code>pTarget</code>. Note,
     * that serialization into a byte stream demands the use of an
     * encoding. It may be required to set the parameter
     * {@link #JAXB_ENCODING}. By default the created output is
     * formatted, which may be turned off using
     * {@link #JAXB_FORMATTED_OUTPUT}.</p>
     *
     * @param pObject The JAXB object being marshalled.
     * @param pTarget The output byte stream.
     * @throws JAXBException An unexpected problem occurred. This may be used,
     *   for example, to throw a nested {@link java.io.IOException}.
     * @throws MarshalException Whereever possible, one should prefer the
     *   {@link MarshalException} over the {@link JAXBException}.
     * @throws IllegalArgumentException Any of the parameters was null.
     * @see #JAXB_ENCODING
     * @see #JAXB_FORMATTED_OUTPUT
     * @see #JAXB_NO_NAMESPACE_SCHEMA_LOCATION
     * @see #JAXB_SCHEMA_LOCATION
     */
    void marshal(Object pObject, OutputStream pTarget) throws JAXBException;

    /** <p>Marshals the given JAXB object <code>pObject</code> and
     * serializes it into the character stream <code>pTarget</code>.
     * Unlike serialization to a byte stream, an encoding is not
     * required, but a <code>Marshaller</code> may use the encoding
     * whether to escape a character or not. Use of the 
     * {@link #JAXB_ENCODING} property is still recommended. By
     * default the created output is
     * formatted, which may be turned off using
     * {@link #JAXB_FORMATTED_OUTPUT}.</p>
     *
     * @param pObject The JAXB object being marshalled.
     * @param pTarget The output character stream.
     * @throws JAXBException An unexpected problem occurred. This may be used,
     *   for example, to throw a nested {@link java.io.IOException}.
     * @throws MarshalException Whereever possible, one should prefer the
     *   {@link MarshalException} over the {@link JAXBException}.
     * @throws IllegalArgumentException Any of the parameters was null.
     * @see #JAXB_ENCODING
     * @see #JAXB_FORMATTED_OUTPUT
     * @see #JAXB_NO_NAMESPACE_SCHEMA_LOCATION
     * @see #JAXB_SCHEMA_LOCATION
     */
    void marshal(Object pObject, Writer pTarget) throws JAXBException;

    /** <p>Marshals the given JAXB object by emitting SAX events into
     * into the given SAX {@link org.xml.sax.ContentHandler}. This
     * includes the events {@link org.xml.sax.ContentHandler#startDocument()}
     * and {@link org.xml.sax.ContentHandler#endDocument()}.</p>
     *
     * @param pObject The JAXB Object being marshalled.
     * @param pTarget The target event handler.
     * @throws JAXBException An unexpected problem occurred. This may be used,
     *   for example, to throw a nested {@link org.xml.sax.SAXException}.
     * @throws MarshalException Whereever possible, one should prefer the
     *   {@link MarshalException} over the {@link JAXBException}.
     * @throws IllegalArgumentException Any of the parameters was null.
     * @see #JAXB_NO_NAMESPACE_SCHEMA_LOCATION
     * @see #JAXB_SCHEMA_LOCATION
     * @see #JAXB_FRAGMENT
     */
    void marshal(Object pObject, ContentHandler pTarget) throws JAXBException;

    /**
     * <p>Marshals the given JAXB object by creating a DOM tree below
     * the given node.</p>
     *
     * @param pObject The JAXB object being marshalled.
     * @param pTarget The target node. This node must be ready to accept a
     *   child element. For example, it may be a {@link org.w3c.dom.Document},
     *   a {@link org.w3c.dom.DocumentFragment}, or an
     *   {@link org.w3c.dom.Element}.
     * @throws JAXBException An unexpected problem occurred. This may be used,
     *   for example, to throw a nested {@link org.w3c.dom.DOMException}.
     * @throws MarshalException Whereever possible, one should prefer the
     *   {@link MarshalException} over the {@link JAXBException}.
     * @throws IllegalArgumentException Any of the parameters was null.
     * @see #JAXB_NO_NAMESPACE_SCHEMA_LOCATION
     * @see #JAXB_SCHEMA_LOCATION
     */
    void marshal(Object pObject, Node pTarget) throws JAXBException;

    /**
     * <p>Marshals the given JAXB object by creating the given file.</p>
     *
     * @param pObject The JAXB object being marshalled.
     * @param pFile The file being created.
     * @throws JAXBException An unexpected problem occurred.
     * @throws MarshalException Whereever possible, one should prefer the
     *   {@link MarshalException} over the {@link JAXBException}.
     * @throws IllegalArgumentException Any of the parameters was null.
     * @see #JAXB_NO_NAMESPACE_SCHEMA_LOCATION
     * @see #JAXB_SCHEMA_LOCATION
     * @see #JAXB_ENCODING
     * @see #JAXB_FORMATTED_OUTPUT
     */
    void marshal(Object pObject, File pFile) throws JAXBException;

    /**
     * Marshals the given JAXB object by using the given
     * {@link XMLStreamWriter}.
     * 
     * @param pObject The JAXB object being marshalled.
     * @param pWriter The target writer.
     * 
     * @throws JAXBException An unexpected problem occurred.
     * @throws MarshalException Whereever possible, one should prefer the
     *   {@link MarshalException} over the {@link JAXBException}.
     * @throws IllegalArgumentException Either of the parameters was null.
     * @since JAXB 2.0
     * @see #JAXB_FRAGMENT
     * @see #JAXB_NO_NAMESPACE_SCHEMA_LOCATION
     * @see #JAXB_SCHEMA_LOCATION
     */
    void marshal(Object pObject, XMLStreamWriter pWriter) throws JAXBException;

    /**
     * Marshals the given JAXB object by raising events on the
     * given {@link XMLEventWriter}.
     * 
     * @param pObject The JAXB object being marshalled.
     * @param pWriter The event writer to call.
     * 
     * @throws JAXBException An unexpected problem occurred.
     * @throws MarshalException Whereever possible, one should prefer the
     *   {@link MarshalException} over the {@link JAXBException}.
     * @throws IllegalArgumentException Either of the parameters was null.
     * @since JAXB 2.0
     * @see #JAXB_FRAGMENT
     * @see #JAXB_NO_NAMESPACE_SCHEMA_LOCATION
     * @see #JAXB_SCHEMA_LOCATION
     */
    void marshal(Object pObject, XMLEventWriter pWriter) throws JAXBException;

    /** <p>Returns a DOM view of the given JAXB object. This view is life
     * in the sense that modifications of its DOM tree will trigger
     * updates on the original JAXB object.</p>
     * <p><em>Note</em>: This is an optional feature and not supported
     * by all JAXB providers.</p>
     * @param pObject The JAXB object being viewed.
     * @throws UnsupportedOperationException The JAXB provider does not
     *   support DOM views.
     * @throws JAXBException An unexpected problem occurred. This may be used,
     *   for example, to throw a nested {@link org.w3c.dom.DOMException}.
     * @throws IllegalArgumentException The parameter was null.
     */
    Node getNode(Object pObject) throws JAXBException;

    /** <p>Sets the marshaller property <code>pName</code> to the value
     * <code>pValue</code>. Note, that the value type depends on the
     * property being set. For example, the property
     * {@link #JAXB_ENCODING} requires a string, but the property
     * {@link #JAXB_FORMATTED_OUTPUT} requires a {@link Boolean}.</p>
     * @param pName The property name.
     * @throws PropertyException An error occurred while processing the property.
     * @throws IllegalArgumentException The name parameter was null.
     */
    void setProperty(String pName, Object pValue) throws PropertyException;

    /** <p>Returns the value of the marshaller property <code>pName</code>.
     * Note, that the value type depends on the
     * property being set. For example, the property
     * {@link #JAXB_ENCODING} requires a string, but the property
     * {@link #JAXB_FORMATTED_OUTPUT} requires a {@link Boolean}.</p>
     * @param pName The property name.
     * @throws PropertyException An error occurred while processing the property.
     * @throws IllegalArgumentException The name parameter was null.
     */
    Object getProperty(String pName) throws PropertyException;

    /** <p>Allows the application to set an event handler. The event handler
     * will be invoked in case of a validation event.</p>
     * <p><em>Note</em>: The ability to register an event handler does not
     * indicate that a JAXB provider is required to validate the objects
     * being marshalled. If you want to ensure, that only valid objects
     * are marshalled, you should perform an explicit validation using a
     * {@link javax.xml.bind.Validator}.</p>
     * @param pHandler An application specific event handler or null to
     *   revert to the default event handler. The default handler is
     *   throwing an exception in case of errors.
     */
    void setEventHandler(ValidationEventHandler pHandler) throws JAXBException;

    /** <p>Returns an event handler previously registered by the application.
     * The event handler will be invoked in case of a validation event.</p>
     * <p><em>Note</em>: The ability to register an event handler does not
     * indicate that a JAXB provider is required to validate the objects
     * being marshalled. If you want to ensure, that only valid objects
     * are marshalled, you should perform an explicit validation using a
     * {@link javax.xml.bind.Validator}.</p>
     * @return An event handler previously set by the application or
     *   the default event handler. The default handler is simply throwing
     *   exceptions.
     * @throws JAXBException An error occurred while getting the event
     *   handler.
     */
    ValidationEventHandler getEventHandler() throws JAXBException;

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
	void setAdapter(XmlAdapter adapter);

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
    <A extends XmlAdapter> void setAdapter(Class<A> pType, A pAdapter);

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
     * Sets an attachment handler, which allows to marshal parts
     * of the XML document as binary attachments.
     * @throws IllegalStateException The marshaller is currently in use.
     */
    void setAttachmentMarshaller(AttachmentMarshaller am);

    /**
     * Returns an attachment handler, which allows to marshal parts
     * of the XML document as binary attachments.
     */
    AttachmentMarshaller getAttachmentMarshaller();

    /**
     * Sets a schema, which shall be used to validate objects
     * while marshalling them. Defaults to null (validation is
     * disabled).
     *
     * @param pSchema Non-null schema object to turn on validation
     *   or null to turn off.
     * @throws UnsupportedOperationException The marshaller doesn't
     *   support schema validation.
     * @since JAXB 2.0
     */
    void setSchema(Schema pSchema);

    /**
     * Returns a schema, which shall be used to validate objects
     * while marshalling them. Defaults to null (validation is
     * disabled).
     *
     * @return Non-null schema object, if validation is turned on,
     *   otherwise null.
     * @throws UnsupportedOperationException The marshaller doesn't
     *   support schema validation.
     * @since JAXB 2.0
     */
    Schema getSchema();

    /**
     * Registers an event listener, which is invoked by the marshaller.
     * If an event listener is already registered, the current one
     * will be replaced.
     * @param pListener The event listener to register.
     * @since JAXB 2.0
     */
    void setListener(Listener pListener);

    /**
     * Returns the current event listener.
     * @return The current event listener, if any, or null.
     * @since JAXB 2.0
     */
    Listener getListener();
}
