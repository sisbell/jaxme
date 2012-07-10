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


/**
 * <p>As of JAXB 2.0, this class is deprecated and optional.</p>
 * <p>A <code>Validator</code> may be used to decide, whether
 * a JAXB object is valid or not. If it is not, the JAXB user
 * may decide to trigger an exception or not (via the
 * {@link javax.xml.bind.ValidationEventHandler} and he may
 * receive information on the problems location (via the
 * event handlers {@link javax.xml.bind.ValidationEventLocator}.</p>
 * <p>A Validator may be present implicitly, invoked by the
 * Unmarshaller. See {@link javax.xml.bind.Unmarshaller#setValidating(boolean)} for
 * more information on that.</p>
 * @since JAXB1.0
 * @author JSR-31
 * @see ValidationEventHandler
 * @see ValidationEvent
 * @deprecated In JAXB 2.0 the validator has been deprecated.
 *   It is now recommended to use the JAXP 1.3 validator framework.
 */
public interface Validator {
    /** <p>Registers an event handler that shall be invoked for
     * notifications on problems detected by the <code>Validator</code>.
     * If this method is not invoked, there is a default event handler.
     * The default event handler will trigger an exception for
     * errors and fatal errors.</p>
     *
     * @param pHandler The event handler being notified or null
     *   to restore the default event handler.
     * @throws JAXBException Setting the event handler failed.
     * @deprecated since JAXB 2.0
     */
    public void setEventHandler(ValidationEventHandler pHandler) throws JAXBException;

    /** <p>Returns an event handler that shall be invoked for
     * notifications on problems detected by the <code>Validator</code>.
     * If no specific event handler was set, returns the default
     * event handler. The default event handler will trigger an
     * exception for errors and fatal errors.</p>
     *
     * @throws JAXBException Getting the event handler failed.
     * @return The event handler previously set or the default
     *   handler.
     * @deprecated since JAXB 2.0
     */
    public ValidationEventHandler getEventHandler() throws JAXBException;

    /** <p>Validates the given JAXB object, invoking its error handler
     * for any problems it detects. Detected problems may cause exceptions,
     * for example, if the event handlers
     * {@link ValidationEventHandler#handleEvent(ValidationEvent)}
     * method returns false.</p>
     *
     * @param pObject The JAXB object being validated.
     * @throws JAXBException An unexpected problem occurred during
     *   validation
     * @throws ValidationException It was detected, that the
     *   object is invalid.
     * @throws IllegalArgumentException The parameter was null.
     * @see #validateRoot(Object)
     * @deprecated since JAXB 2.0
     */
    public boolean validate(Object pObject) throws JAXBException;

    /** <p>Validates the given JAXB object, but not its child
     * elements.</p>
     *
     * @param pObject The JAXB object being validated.
     * @throws JAXBException An unexpected problem occurred during
     *   validation
     * @throws ValidationException It was detected, that the
     *   object is invalid.
     * @throws IllegalArgumentException The parameter was null.
     * @see #validate(Object)
     * @deprecated since JAXB 2.0
     */ 
    public boolean validateRoot(Object pObject) throws JAXBException;

    /** <p>Sets the <code>Validator</code> property <code>pName</code>
     * to <code>pValue</code>.</p>
     * <p><em>Note</em>: The values type depends on the property name.</p>
     *
     * @param pName The property name.
     * @param pValue The property value.
     * @throws PropertyException Setting the property failed.
     * @throws IllegalArgumentException The <code>pName</code> parameter was null.
     * @deprecated since JAXB 2.0
     */
    public void setProperty(String pName, Object pValue) throws PropertyException;

    /** <p>Returns the marshallers property <code>pName</code>.</p>
     * <p><em>Note</em>: The values type depends on the property name.</p>
     *
     * @param pName The property name.
     * @return The property value.
     * @throws PropertyException Fetching the property failed.
     * @throws IllegalArgumentException The parameter was null.
     * @deprecated since JAXB 2.0
     */
    public java.lang.Object getProperty(String pName) throws PropertyException;
}
