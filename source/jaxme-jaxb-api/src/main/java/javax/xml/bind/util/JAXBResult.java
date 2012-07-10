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
package javax.xml.bind.util;

import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshallerHandler;
import javax.xml.transform.sax.SAXResult;


/** <p>Utility class that allows to catch the result of a
 * stylesheet transformation in a JAXB object.</p>
 *
 * @author JSR-31
 * @since JAXB1.0
 */
public class JAXBResult extends SAXResult {
  /** <p>Creates a new instance of <code>JAXBResult</code>.
   * The instance will use the specified {@link javax.xml.bind.JAXBContext}
   * to create an {@link javax.xml.bind.Unmarshaller}.</p>
   */
  public JAXBResult(javax.xml.bind.JAXBContext pContext) throws JAXBException {
    this(pContext.createUnmarshaller());
  }

  /** <p>Creates a new instance of <code>JAXBResult</code>.
   * The instance will use the given {@link javax.xml.bind.Unmarshaller}
   * to create a {@link org.xml.sax.ContentHandler}.</p>
   * <p>In most cases you will use the constructor taking a
   * {@link javax.xml.bind.JAXBContext} as input. This additional
   * constructor is required, if you want to configure the
   * {@link javax.xml.bind.Unmarshaller}.</p>
   * @param pUnmarshaller The Unmarshaller that may be queried for an
   *   {@link UnmarshallerHandler}.
   */
  public JAXBResult(javax.xml.bind.Unmarshaller pUnmarshaller) throws JAXBException {
    super(pUnmarshaller.getUnmarshallerHandler());
  }

  /** <p>Returns the result of a previous transformation.</p>
   */
  public Object getResult() throws JAXBException {
    return ((UnmarshallerHandler) super.getHandler()).getResult();
  }
}
