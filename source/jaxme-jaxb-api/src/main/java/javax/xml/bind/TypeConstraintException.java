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

/** <p>This is a runtime exception. The desired use is for
 * generated set methods which would like to indicate that the
 * specified value is invalid, for example, because a facet
 * restriction wasn't met.</p>
 * <p>If a generated setter throws a <code>TypeConstraintException</code>,
 * then it is the JAXB providers task to ensure, that the object,
 * on which the setter is invoked, remains unchanged.</p>
 *
 * @author JSR-31
 * @since JAXB 1.0
 */
@SuppressWarnings("serial") // Not provided by RI
public class TypeConstraintException extends RuntimeException {
    private String errorCode;
    private Throwable linkedException;

    /** <p>Creates a new <code>TypeConstraintException</code> with the specified
     * detail message.</p>
     * @param pMessage The detail message.
     */
    public TypeConstraintException(String pMessage) {
        super(pMessage);
    }

    /** <p>Creates a new <code>TypeConstraintException</code> with the specified
     * detail message and vendor specific error code.</p>
     * @param pMessage The detail message.
     * @param pErrorCode The error code.
     */
    public TypeConstraintException(String pMessage, String pErrorCode) {
        super(pMessage);
        errorCode = pErrorCode;
    }

    /** <p>Creates a new <code>TypeConstraintException</code> with the specified
     * linked exception.</p>
     * @param pLinkedException The linked exception.
     */
    public TypeConstraintException(Throwable pLinkedException) {
        linkedException = pLinkedException;
    }

    /** <p>Creates a new <code>TypeConstraintException</code> with the specified
     * detail message and linked exception.</p>
     * @param pMessage The detail message.
     * @param pLinkedException The linked exception.
     */
    public TypeConstraintException(String pMessage, Throwable pLinkedException) {
        super(pMessage);
        linkedException = pLinkedException;
    }

    /** <p>Creates a new <code>TypeConstraintException</code> with the specified
     * detail message, error code, and linked exception.</p>
     * @param pMessage The detail message.
     * @param pErrorCode The vendor specific error code.
     * @param pLinkedException The linked exception.
     */
    public TypeConstraintException(String pMessage, String pErrorCode,
            Throwable pLinkedException) {
        super(pMessage);
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
    @Override
    public String toString() {
        final String s;
        if (errorCode == null  ||  errorCode.length() == 0) {
            s = super.toString();
        } else {
            s = errorCode + ": " + super.toString();
        }
        if (linkedException == null) {
            return s;
        }
        return s + "\n - with linked exception:\n[" +
                                    linkedException.toString()+ "]";
    }

    /**
     * Prints this exceptions stack trace to the stream
     * {@code pStream}. Includes the stack trace of the
     * linked exception.
     * @param pStream Target stream
     */
    @Override
    public void printStackTrace(PrintStream pStream) {
        if (linkedException != null) {
          linkedException.printStackTrace(pStream);
          pStream.println("--------------- linked to ------------------");
        }
        super.printStackTrace(pStream);
    }
 
    /**
     * Prints this exceptions stack trace to System.err
     * {@code pStream}. Includes the stack trace of the
     * linked exception.
     */
    @Override
    public void printStackTrace() {
        printStackTrace(System.err);
    }
}
