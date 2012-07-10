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

/** <p>An instance of <code>ValidationEventHandler</code> allows
 * to customize the reply to instances of
 * {@link javax.xml.bind.ValidationEvent}. By default, there is
 * a standard <code>ValidationEventHandler</code>, converting
 * errors and fatal errors in Exceptions.</p>
 * <p>The JAXB user creates instances of <code>ValidationEventHandler</code>
 * and registers them with the {@link Marshaller}, {@link Unmarshaller},
 * or {@link Validator}. The JAXB provider is required not to throw
 * exceptions directly, but to convert all detected problems into events,
 * which are fired on the <code>ValidationEventHandler</code>.</p>
 *
 * @since JAXB1.0
 * @author JSR-31
 * @see Marshaller
 * @see Unmarshaller
 * @see javax.xml.bind.ValidationEvent
 */
public interface ValidationEventHandler {
   /** <p>The <code>handleEvent</code> method is invoked by the
    * JAXB provider, if a problem was found. The events
    * {@link javax.xml.bind.ValidationEventLocator} may be
    * used to locate the source of the problem.</p>
    *
    * @param pEvent The event being reported to the JAXB user.
    * @return True as an indicator that the JAXB provider should
    *   attempt to continue its current operation. (Marshalling,
    *   Unmarshalling, Validating) This will not always work.
    *   In particular, you cannot expect that the operation
    *   continues, if a fatal error was reported. False to
    *   indicate that the JAXB provider should terminate the
    *   operation and through an appropriate exception.
    * @throws IllegalArgumentException The parameter is null.
    */
   public boolean handleEvent(ValidationEvent pEvent);
}
