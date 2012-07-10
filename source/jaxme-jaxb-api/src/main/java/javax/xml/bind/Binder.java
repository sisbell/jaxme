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

import org.w3c.dom.Node;

import javax.xml.validation.Schema;

/**
 * <p>A binder is an object, which allows synchronization between
 * different representations of a single XML document.</p>
 * @since JAXB 2.0
 */
public abstract class Binder<XmlNode> {
    /**
     * <p>Unmarshals the given <code>pXmlNode</code> into a
     * JAXB object. This object may later be synchronized
     * forth or back by invoking {@link #updateXML(Object, Object)}
     * or {@link #updateJAXB(Object)}.</p>
     * <p>
     * If {@link #getSchema()} is non-null, <code>pXmlNode</code>
     * and its descendants is validated during this operation.</p>
     * @param pXmlNode A representation of the XML node, which
     *   is being unmarshalled.
     * @return The unmarshalled JAXB object.
     * @throws JAXBException An unexpected error occurred while
     *   unmarshalling the XML node.
     * @throws UnmarshalException An
     *   {@link ValidationEventHandler ValidationEventHandler}
     *   returned false or the XML node could not be unmarshalled.
     * @throws IllegalArgumentException
     *   The <code>pXmlNode</code> parameter is null.
     */
    public abstract Object unmarshal(XmlNode pXmlNode) throws JAXBException;

    /**
     * Similar to {@link Unmarshaller#unmarshal(Node, Class)},
     * except that the unmarshalled object may later be synchronized
     * forth or back by invoking {@link #updateXML(Object, Object)}
     * or {@link #updateJAXB(Object)}.</p>
     * <p>
     * If {@link #getSchema()} is non-null, <code>pXmlNode</code>
     * and its descendants is validated during this operation.</p>
     * @param pXmlNode A representation of the XML node, which
     *   is being unmarshalled.
     * @param pType A suitable JAXB class for unmarshalling the
     *   given <code>pXmlNode</code> into a new instance of this
     *   class.
     * @return The unmarshalled JAXB object.
     * @throws JAXBException An unexpected error occurred while
     *   unmarshalling the XML node.
     * @throws UnmarshalException An
     *   {@link ValidationEventHandler ValidationEventHandler}
     *   returned false or the XML node could not be unmarshalled.
     * @throws IllegalArgumentException
     *   The <code>pXmlNode</code> parameter is null.
     */
    public abstract <T> JAXBElement<T> unmarshal(XmlNode pXmlNode,
            Class<T> pType) throws JAXBException;

    /**
     * <p>Marshals the given JAXB object into a different
     * XML representation. This object may later be synchronized
     * forth and back by using {@link #updateXML(Object, Object)}
     * or {@link #updateJAXB(Object)}.</p>
     * <p>If {@link #getSchema()} is non-null, the marshalled
     * xml content is validated during this operation.</p>
     * @param pJaxbObject The JAXB object, which is being
     *   marshalled.
     * @param pXmlNode The target node, which is being filled
     *   by marshalling the JAXB object.
     * @throws JAXBException An unexpected error occurred while
     *   marshalling the JAXB object.
     * @throws MarshalException An
     *   {@link ValidationEventHandler ValidationEventHandler}
     *   returned false or marshalling the JAXB object failed.
     * @throws IllegalArgumentException A method parameter
     *   was null.
     */
    public abstract void marshal(Object pJaxbObject, XmlNode pXmlNode) throws JAXBException;

    /**
     * Returns an XML element, which is associated with the given
     * JAXB object.
     * @param pJaxbObject An object, which was typically created by
     *   a call
     *   to {@link #unmarshal(Object)}, {@link #unmarshal(Object, Class)},
     *   or {@link #updateJAXB(Object)}.
     * @return Null, if the given JAXB object is unknown. An XML
     *   element otherwise.
     * @throws IllegalArgumentException The parameter is null.
     */
    public abstract XmlNode getXMLNode(Object pJaxbObject);

    /**
     * Returns a JAXB object, which is associated with the given
     * XML element.
     * @param pXmlNode An object, which was typically created by
     *   a call to {@link #marshal(Object, Object)},
     *   {@link #updateXML(Object)}, or {@link #updateXML(Object, Object)}.
     * @return Null, if the given XML element is unknown. A
     *   JAXB object otherwise.
     * @throws IllegalArgumentException The parameter is null.
     */
    public abstract Object getJAXBNode(XmlNode pXmlNode);

    /**
     * Updates the XML node, which is associated with the
     * given JAXB object. Shortcut for
     * <pre>updateXML(pJaxbObject, getXMLNode(pJaxbObject))</pre>
     * @throws JAXBException An error occurred while updating the
     * XML node.
     * @throws IllegalArgumentException The parameter is null.
     */
    public abstract XmlNode updateXML(Object pJaxbObject) throws JAXBException;

    /**
     * Updates the contents of the given XML node to match the
     * JAXB objects contents. Additionally, the associations
     * between JAXB objects and XML nodes are updated.
     * @param pJaxbObject A JAXB object, which is associated with the
     *   given XML node.
     * @param pXmlNode    An XML node, which is associated with the
     *   given JAXB object.
     * @return
     *   The updated XML node, typically the same as the input
     *   XML node. However, this isn't necessarily the case and
     *   a new XML node may have been created.
     * @throws JAXBException An error occurred while updating the
     *   XML node.
     * @throws IllegalArgumentException Either of the parameters was null.
     */
    public abstract XmlNode updateXML(Object pJaxbObject, XmlNode pXmlNode) throws JAXBException;

    /**
     * Updates the contents of the JAXB object, which is associated
     * with the given XML node to match the XML nodes contents.
     * Additionally, the associations
     * between JAXB objects and XML nodes are updated.
     * @return The updated JAXB object, typically the same object,
     *   that was already associated with the XML node. However,
     *   this isn't necessarily the case and a new JAXB object
     *   may have been created.
     * @throws JAXBException An error occurred while updating the
     *   JAXB object.
     * @throws IllegalArgumentException The parameter is null.
     */
    public abstract Object updateJAXB(XmlNode xmlNode) throws JAXBException;


    /**
     * Specifies, whether the marshal, unmarshal, or update
     * methods are validating.
     * @param schema A non-null argument specifies, that XML contents
     *   are being validated against the given schema object. Null
     *   disables validation.
     * @see Unmarshaller#setSchema(Schema)
     */
    public abstract void setSchema( Schema schema );

    /**
     * Returns a schema object, which is used by the marshal,
     * unmarshal or update methods for validating the XML
     * contents.
     * @return A non-null argument indicates, that the returned
     *   object is used for validating. Null is returned, if
     *   validation is disabled.
     */
    public abstract Schema getSchema();

    /**
     * Registers an event handler, which will be notified in case of
     * validation errors.
     * @param pHandler A non-null value registers the given handler as
     *   the new event handler. Null restores a default event handler.
     * @throws JAXBException An error occurred while registering the
     *   event handler.
     */
    public abstract void setEventHandler(ValidationEventHandler pHandler) throws JAXBException;

    /**
     * Returns an event handler, which is currently registered for
     * notifications in case of validation errors.
     * @return The currently registered event handler, if any, or
     *   a default handler.
     * @throws JAXBException An error occurred while returning the
     *   event handler.
     */
    public abstract ValidationEventHandler getEventHandler() throws JAXBException;

    /**
     * Sets a {@link Binder} property. For example, this may be a
     * {@link Marshaller} or {@link Unmarshaller} property.
     * @param pName Property name
     * @param pValue Property value
     * @throws PropertyException The property name is unknown or
     *   the value is invalid.
     * @throws IllegalArgumentException The pName parameter is null.
     */
    abstract public void setProperty(String pName, Object pValue) throws PropertyException;


    /**
     * Returns the requested {@link Binder} property. For example,
     * this may be a
     * {@link Marshaller} or {@link Unmarshaller} property.
     * @param pName Property name
     * @throws PropertyException The property name is unknown or
     *   reading the value failed.
     * @throws IllegalArgumentException The pName parameter is null.
     */
    abstract public Object getProperty(String pName) throws PropertyException;

}
