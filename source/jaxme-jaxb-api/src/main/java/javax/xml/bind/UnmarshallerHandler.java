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

import org.xml.sax.ContentHandler;


/** <p>This interface is the SAX {@link org.xml.sax.ContentHandler}
 * representation of an {@link javax.xml.bind.Unmarshaller}, as
 * returned by
 * {@link javax.xml.bind.Unmarshaller#getUnmarshallerHandler()}.
 * It can be embedded into a stack of SAX handlers, for example
 * within Apache Cocoon.</p>
 * <p>The <code>UnmarshallerHandler</code> is reusable: The
 * <code>startDocument()</code> method is expected to perform
 * a reinitialization. Like most other SAX handlers, the
 * <code>UnmarshallerHandler</code> is never thread safe.</p>
 *
 * @author JSR-31
 * @since JAXB1.0
 */
public interface UnmarshallerHandler extends ContentHandler {
  /** <p>Returns the unmarshalled object. This method may be invoked
   * after an <code>endDocument()</code> event only. An
   * {@link IllegalStateException} is thrown otherwise.
   * @return The unmarshalled object, never null. (An
   *   {@link IllegalStateException} is thrown, if no data is
   *   available.
   * @throws JAXBException An error occurred. Note, that the
   *   {@link UnmarshallerHandler} throws a
   *   {@link org.xml.sax.SAXException} if an error occurs while
   *   unmarshalling the object. In such cases the
   *   {@link JAXBException} is typically nested within the
   *   {@link org.xml.sax.SAXException}.
   * @throws IllegalStateException An <code>endDocument()</code>
   *   event has not yet been seen and no data is available.
   */
  public Object getResult() throws JAXBException, IllegalStateException;
}
