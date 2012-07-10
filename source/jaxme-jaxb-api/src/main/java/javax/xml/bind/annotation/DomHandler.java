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
package javax.xml.bind.annotation;

import javax.xml.bind.ValidationEventHandler;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

/**
 * Objects implementing this interface are used to
 * implement the {@link XmlAnyElement} annotation.
 * Basically, the {@link DomHandler} is a factory for objects
 * like {@link DOMResult} or {@link DOMSource}. Using the
 * factory allows to be independent of the DOM API.
 * @since JAXB2.0
 */
public interface DomHandler<ElementT,ResultT extends Result> {
    /**
     * Called by the JAXB provider, if he wants to convert
     * a part of the document into a DOM representation.
     * The created object will receive this part. Finally,
     * the JAXB provider will invoke {@link #getElement(Result)}
     * on the created object in order to receive the
     * DOM element.
     * @param errorHandler The DOM implementation is responsible
     *   for calling the given error handler. The error handler
     *   must not be null.
     * @return The created result object or null, if an error has
     *   occurred. If so, the error must have been reported to the
     *   error handler.
     */
    ResultT createUnmarshaller( ValidationEventHandler errorHandler );

    /**
     * Calling this method finishs use of the result object,
     * which has been created by {@link #createUnmarshaller(ValidationEventHandler)}.
     * @param rt The result object, which has been created by
     *     {@link #createUnmarshaller(ValidationEventHandler)}.
     * @return The created XML element or null, if an error has occurred.
     *     If so, then the error must have been reported to the
     *     error handler.
     */
    ElementT getElement(ResultT rt);

    /**
     * Called by the JAXB provider, if he wants to marshal
     * the DOM representation of an element into an XML document.
     * @param errorHandler Called if any errors occur during the
     *   marshalling process.
     * @return A valid {@link Source} object, which may be embedded
     *   into a larger XML document or null, if an error has occurred.
     *   If so, the error must have been reported to the error handler.
     */
    Source marshal( ElementT n, ValidationEventHandler errorHandler );
}
