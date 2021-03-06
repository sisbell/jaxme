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
package org.apache.ws.jaxme.pm.ino;

import org.xml.sax.SAXException;


/** <p>A SAX exception indicating an error in a Tamino response
 * document.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class InoException extends SAXException {
  String inoErrorCode;
  String inoErrorMessage;

  /**
   * Creates a new instance of <code>InoException</code> with
   * the given error code and message.
   */
  public InoException(String pInoErrorCode, String pInoErrorMessage) {
    super(pInoErrorCode + ": " + pInoErrorMessage);
    inoErrorCode = pInoErrorCode;
    inoErrorMessage = pInoErrorMessage;
  }

  /** <p>Returns the Tamino error code.</p>
   */
  public String getInoErrorCode() {
    return inoErrorCode;
  }

  /** <p>Returns the Tamino error message.</p>
   */
  public String getInoErrorMessage() {
    return inoErrorMessage;
  }
}