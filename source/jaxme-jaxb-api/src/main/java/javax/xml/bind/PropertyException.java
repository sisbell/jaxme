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

/** <p>The <code>PropertyException</code> is a subclass of the
 * {@link javax.xml.bind.JAXBException} being thrown if setting
 * or getting a property failed.</p>
 *
 * @author JSR-31
 * @since JAXB1.0
 */
@SuppressWarnings("serial") // Not provided by RI
public class PropertyException extends JAXBException {
  /** <p>Creates a new <code>PropertyException</code> with the specified
   * detail message.</p>
   * @param pMessage The detail message.
   */
  public PropertyException(String pMessage) {
    super(pMessage);
  }

  /** <p>Creates a new <code>PropertyException</code> with the specified
   * detail message and vendor specific error code.</p>
   * @param pMessage The detail message.
   * @param pErrorCode The error code.
   */
  public PropertyException(String pMessage, String pErrorCode) {
    super(pMessage, pErrorCode);
  }

  /** <p>Creates a new <code>PropertyException</code> with the specified
   * linked exception.</p>
   * @param pLinkedException The linked exception.
   */
  public PropertyException(Throwable pLinkedException) {
    super(pLinkedException);
  }

  /** <p>Creates a new <code>PropertyException</code> with the specified
   * detail message and linked exception.</p>
   * @param pMessage The detail message.
   * @param pLinkedException The linked exception.
   */
  public PropertyException(String pMessage, Throwable pLinkedException) {
    super(pMessage, pLinkedException);
  }

  /** <p>Creates a new <code>PropertyException</code> with the specified
   * detail message, error code, and linked exception.</p>
   * @param pMessage The detail message.
   * @param pErrorCode The vendor specific error code.
   * @param pLinkedException The linked exception.
   */
  public PropertyException(String pMessage, String pErrorCode,
                           Throwable pLinkedException) {
    super(pMessage, pErrorCode, pLinkedException);
  }

  /** <p>Creates a new <code>PropertyException> by invoking
   * {@link #PropertyException(String)} with a message derived from
   * <code>pName</code> and <code>pValue.toString()</code>.</p>
   * @param pName A Property name.
   * @param pValue A property value.
   */
  public PropertyException(String pName, Object pValue) {
    super("Property error: name=" + pName + ", value=" + pValue);
  }
}
