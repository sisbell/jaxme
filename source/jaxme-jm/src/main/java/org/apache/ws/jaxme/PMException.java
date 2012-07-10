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
 
package org.apache.ws.jaxme;

import javax.xml.bind.JAXBException;

/** <p>Exception being thrown in case of persistency problems.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class PMException extends JAXBException {
  /** <p>Creates a new instance of PMException.</p>
   */
  public PMException(String pMessage) {
    super(pMessage);
  }

  /** <p>Creates a new instance of PMException.</p>
   */
  public PMException(String pMessage, String pErrorCode) {
    super(pMessage, pErrorCode);
  }

  /** <p>Creates a new instance of PMException.</p>
   */
  public PMException(Throwable pLinkedException) {
    super(pLinkedException);
  }

  /** <p>Creates a new instance of PMException.</p>
   */
  public PMException(String pMessage, Throwable pLinkedException) {
    super(pMessage, pLinkedException);
  }

  /** <p>Creates a new instance of PMException.</p>
   */
  public PMException(
    String pMessage,
    String pErrorCode,
    Throwable pLinkedException) {
    super(pMessage, pErrorCode, pLinkedException);
  }
}
