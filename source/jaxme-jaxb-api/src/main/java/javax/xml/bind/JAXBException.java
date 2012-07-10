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

import java.io.PrintStream;
import java.io.PrintWriter;

/** <p>This is the main exception class of JAXB. All other
 * exception classes (except the {@link javax.xml.bind.TypeConstraintException},
 * which is a {@link java.lang.RuntimeException} are derived from the
 * <code>JAXBException</code>.</p>
 *
 * @author JSR-31
 * @since JAXB1.0
 */
public class JAXBException extends Exception {
    private static final long serialVersionUID = -5621384651494307979L;
    private String errorCode;
    private Throwable linkedException;
    
    /** <p>Creates a new <code>JAXBException</code> with the specified
     * detail message.</p>
     * @param pMessage The detail message.
     */
    public JAXBException(String pMessage) {
        super(pMessage);
    }

    private static String formatMessage(String pErrorCode, String pMessage, Throwable pThr) {
    	String msg = null;
        if (pMessage == null) {
            if (pThr != null) {
            	msg = pThr.getMessage();
                if (msg == null) {
                	msg = pThr.getClass().getName();
                }
            }
        } else {
        	msg = pMessage;
        }
        if (pErrorCode == null) {
        	return msg;
        }
        if (msg == null) {
            return pErrorCode;
        }
        return pErrorCode + ": " + msg;
    }

    /** <p>Creates a new <code>JAXBException</code> with the specified
     * detail message and vendor specific error code.</p>
     * @param pMessage The detail message.
     * @param pErrorCode The error code.
     */
    public JAXBException(String pMessage, String pErrorCode) {
        this(pMessage, pErrorCode, null);
    }
    
    /** <p>Creates a new <code>JAXBException</code> with the specified
     * linked exception.</p>
     * @param pLinkedException The linked exception.
     */
    public JAXBException(Throwable pLinkedException) {
        this(null, null, pLinkedException);
    }
    
    /** <p>Creates a new <code>JAXBException</code> with the specified
     * detail message and linked exception.</p>
     * @param pMessage The detail message.
     * @param pLinkedException The linked exception.
     */
    public JAXBException(String pMessage, Throwable pLinkedException) {
        this(pMessage, null, pLinkedException);
    }
    
    /** <p>Creates a new <code>JAXBException</code> with the specified
     * detail message, error code, and linked exception.</p>
     * @param pMessage The detail message.
     * @param pErrorCode The vendor specific error code.
     * @param pLinkedException The linked exception.
     */
    public JAXBException(String pMessage, String pErrorCode,
						 Throwable pLinkedException) {
        super(formatMessage(pErrorCode, pMessage, pLinkedException));
        errorCode = pErrorCode;
        linkedException = pLinkedException;
    }
    
    /** <p>Returns the vendor specific error code, if any, or null.</p>
     */
    public String getErrorCode() {
        return errorCode;
    }
    
    /** <p>Returns the linked exception, if any, or null.</p>
     */
    public Throwable getLinkedException() {
        return linkedException;
    }
    
    /** <p>Sets the linked exception.</p>
     * @param pLinkedException The linked exception or null.
     */
    public void setLinkedException(Throwable pLinkedException) {
        linkedException = pLinkedException;
    }
    
    /** <p>Converts the linked exception into a String. Overridden,
     * because the returned string should contain the vendor specific
     * error code, if any.</p>
     */
    public String toString() {
        if (errorCode == null  ||  errorCode.length() == 0) {
            return super.toString();
        }
        return errorCode + ": " + super.toString();
    }

    public void printStackTrace() {
        printStackTrace(System.err);
    }

    public void printStackTrace(PrintStream pStream) {
        super.printStackTrace(pStream);
        Throwable t = getLinkedException();
        if (t != null) {
            pStream.println("Caused by:");
            t.printStackTrace(pStream);
        }
    }

    public void printStackTrace(PrintWriter pWriter) {
        super.printStackTrace(pWriter);
        Throwable t = getLinkedException();
        if (t != null) {
            pWriter.println("Caused by:");
            t.printStackTrace(pWriter);
        }
    }

    @Override
    public Throwable getCause() {
        return linkedException;
    }
}
