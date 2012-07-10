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

/** <p>An instance of <code>ValidationEvent</code> indicates some
 * error condition, which occurred when validating a JAXB object.
 * The purpose of the {@link ValidationEventHandler} is to
 * customize the reply on instances of <code>ValidationEvent</code>.
 * The default event handler will throw an exception in case of
 * events, but application specific validation event handlers need
 * not do the same.</p>
 * 
 * @see Validator
 * @see ValidationEventHandler
 * @since JAXB1.0
 * @author JSR-31
 */
public interface ValidationEvent {
  /** <p>In contrast to errors or fatal errors, this indicates an
   * event which can possibly be ignored. This constant has the
   * value 0. See section 1.2 of the W3C XML 1.0 Recommendation for
   * details.</p>
   * @see #ERROR
   * @see #FATAL_ERROR
   * @see #getSeverity()
   */
  public static final int WARNING = 0;

  /** <p>This value indicates an "error", as specified by section
   * 1.2 of the W3C XML 1.0 Recommendation. The constant value is
   * 1.</p>
   * @see #WARNING
   * @see #FATAL_ERROR
   * @see #getSeverity()
   */
  public static final int ERROR = 1;

  /** <p>This value indicates a "fatal error", as specified by section
   * 1.2 of the W3C XML 1.0 Recommendation. The constant value is
   * 2.</p>
   * @see #WARNING
   * @see #ERROR
   * @see #getSeverity()
   */
  public static final int FATAL_ERROR = 2;

  /** <p>Returns the events severity: Either of
   * {@link #WARNING}, {@link #ERROR}, or {@link #FATAL_ERROR}.</p>
   * @return Returns the events severity.
   */
  public int getSeverity();

  /** <p>Returns a textual description of the event.</p>
   */
  public java.lang.String getMessage();

  /** <p>Returns a {@link Throwable} related to the event. In most cases
   * an exception causing the event.</p>
   */
  public java.lang.Throwable getLinkedException();

  /** <p>Returns a description of the location, where the event
   * occurred.</p>
   */
  public ValidationEventLocator getLocator();
}
