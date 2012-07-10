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
package org.apache.ws.jaxme.impl;

import org.apache.ws.jaxme.JMPI;

/** <p>Implementation of a processing instruction.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class JMPIImpl implements JMPI {
  private String target;
  private String data;

  /** <p>Creates a new JMPI.</p>
   */
  public JMPIImpl(String pTarget, String pData) {
  	target = pTarget;
  	data = pData;
  }

  public String getTarget() { return target; }
  public String getData() { return data; }
}
