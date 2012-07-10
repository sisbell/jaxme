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

/** <p>The <code>ValidationEventLocator</code> is an abstract
 * description of the place where a {@link javax.xml.bind.ValidationEvent}
 * occurred.</p>
 * <p>Depending on the source or target media (Unmarshalling, or Unmarshalling)
 * or the object being validated, you will most probably find that different
 * fields of the <code>ValidationEventHandler</code> are set. For example,
 * if you are using a {@link org.xml.sax.ContentHandler}, you will most
 * probably find that those fields are set, which are common to a
 * {@link org.xml.sax.Locator}.</p>
 *
 * @see javax.xml.bind.ValidationEvent
 * @since JAXB1.0
 * @author JSR-31
 */
public interface ValidationEventLocator {
  /** <p>Returns a URL related to the validation event, if available.
   * For example, when parsing an {@link org.xml.sax.InputSource}, this
   * might be the URL given by {@link org.xml.sax.InputSource#getSystemId()}.</p>
   * @return The validation event URL, if available, or null.
   */
  public java.net.URL getURL();

  /** <p>Returns a byte offset related to the validation event, if
   * available. For example, when parsing an {@link java.io.InputStream},
   * this might be the position where the event occurred.</p>
   * @return Byte offset, if available, or -1.
   */
  public int getOffset();

  /** <p>Returns a line number related to the validation event, if
   * available. For example, when parsing an {@link java.io.InputStream},
   * this might be the line, in which the event occurred.</p>
   * @return Line number, if available, or -1.
   */
  public int getLineNumber();

  /** <p>Returns a column number related to the validation event, if
   * available. For example, when parsing an {@link java.io.InputStream},
   * this might be the column, in which the event occurred.</p>
   * @return Column number, if available, or -1.
   */
  public int getColumnNumber();

  /** <p>Returns an object in the JAXB objects content tree related
   * to the validation event. Usually this is the invalid object or
   * child object.</p>
   * @return Part of a JAXB object tree, if available, or null.
   */
  public java.lang.Object getObject();

  /** <p>Returns a DOM node related to the validation event. For
   * example, this might be an element node with a missing attribute.
   * It might as well be an attribute node with an invalid value.</p>
   * @return Invalid node, if available, or null.
   */
   public Node getNode();
}
