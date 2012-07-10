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

import javax.xml.bind.ValidationEventLocator;

/** JaxMe's extension of the
 * {@link javax.xml.bind.helpers.ValidationEventImpl} class,
 * adding the {@link #getErrorCode()}.
 */
public class ValidationEventImpl extends javax.xml.bind.helpers.ValidationEventImpl {
    private String errorCode;

    /** Creates a new instance with the given severity, message,
     * and locator.
     */
    public ValidationEventImpl(int pSeverity, String pMessage,
                               ValidationEventLocator pLocator) {
        super(pSeverity, pMessage, pLocator);
    }
    /** Creates a new instance with the given severity, message,
     * locator, and throwable.
     */
    public ValidationEventImpl(int pSeverity, String pMessage,
                               ValidationEventLocator pLocator,
                               Throwable pLinkedException) {
        super(pSeverity, pMessage, pLocator, pLinkedException);
    }

    /** Sets the error code.
     */
    public void setErrorCode(String pErrorCode) {
        errorCode = pErrorCode;
    }
    /** Returns the error code.
     */
    public String getErrorCode() {
        return errorCode;
    }
}
