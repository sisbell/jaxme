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

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventLocator;


/** <p>Default implementation of a {@link javax.xml.bind.ValidationEvent}.</p>
 *
 * @author JSR-31
 * @since JAXB1.0
 */
public class ValidationEventImpl implements ValidationEvent {
    private int severity;
    private String message;
    private Throwable linkedException;
    private ValidationEventLocator locator;

    /** <p>Creates a new instance of <code>ValidationEventImpl</code>.</p>
     */
    public ValidationEventImpl(int pSeverity, String pMessage,
            ValidationEventLocator pLocator) {
        severity = pSeverity;
        message = pMessage;
        locator = pLocator;
    }

    /** <p>Creates a new instance of <code>ValidationEventImpl</code>.</p>
     */
    public ValidationEventImpl(int pSeverity, String pMessage,
            ValidationEventLocator pLocator,
            Throwable pLinkedException) {
        severity = pSeverity;
        message = pMessage;
        linkedException = pLinkedException;
        locator = pLocator;
    }

    /* @see javax.xml.bind.ValidationEvent#getSeverity()
     */
    public int getSeverity() {
        return severity;
    }

    /** <p>Sets the events severity.</p>
     * @param pSeverity The events severity, either of
     * {@link javax.xml.bind.ValidationEvent#WARNING},
     * {@link javax.xml.bind.ValidationEvent#ERROR}, or
     * {@link javax.xml.bind.ValidationEvent#FATAL_ERROR}.</p> 
     */
    public void setSeverity(int pSeverity) {
        severity = pSeverity;
    }

    /* @see javax.xml.bind.ValidationEvent#getMessage()
     */
    public String getMessage() {
        return message;
    }

    /** <p>Sets the events message.</p>
     */
    public void setMessage(String pMessage) {
        message = pMessage;
    }

    /* @see javax.xml.bind.ValidationEvent#getLinkedException()
     */
    public Throwable getLinkedException() {
        return linkedException;
    }

    /** <p>Sets the exception, which is linked to the event.</p>
     */
    public void setLinkedException(Throwable pLinkedException) {
        linkedException = pLinkedException;
    }

    /* @see javax.xml.bind.ValidationEvent#getLocator()
     */
    public ValidationEventLocator getLocator() {
        return locator;
    }

    /**
     * <p>Sets the events locator.</p>
     */
    public void setLocator(ValidationEventLocator pLocator) {
        locator = pLocator;
    }

    /**
     * Returns a string representation of this object in a format
     * helpful to debugging.
     * 
     * @see Object#equals(Object)
     */
    public String toString() {
        final String s;
        switch(getSeverity()) {
            case WARNING:   s="WARNING"; break;
            case ERROR: s="ERROR"; break;
            case FATAL_ERROR: s="FATAL_ERROR"; break;
            default: s = String.valueOf(getSeverity()); break;
        }
        return "[severity=" + s + ",message=" + getMessage() + ",locator="
            + getLocator() + "]";
    }
}
