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

import javax.xml.bind.ValidationEventLocator;

/** <p>Default implementation of a {@link javax.xml.bind.ParseConversionEvent}.</p>
 *
 * @author JSR-31
 * @since JAXB1.0
 */
public class ParseConversionEventImpl extends ValidationEventImpl
  implements javax.xml.bind.ParseConversionEvent {

  /** <p>Creates a new instance of <code>ParseConversionEventImpl</code>.</p>
   */
  public ParseConversionEventImpl(int pSeverity, String pMessage,
                                  ValidationEventLocator pLocator) {
    super(pSeverity, pMessage, pLocator);
  }

  /** <p>Creates a new instance of <code>ParseConversionEventImpl</code>.</p>
   */
  public ParseConversionEventImpl(int pSeverity, String pMessage,
                                  ValidationEventLocator pLocator,
                                  Throwable pLinkedException) {
    super(pSeverity, pMessage, pLocator, pLinkedException);
  }
}
