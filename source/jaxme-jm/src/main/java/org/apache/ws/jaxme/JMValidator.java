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

import javax.xml.bind.Validator;

import org.apache.ws.jaxme.impl.JAXBContextImpl;


/** <p>JaxMe's private extension of a JAXB Validator.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 * @version $Id: JMValidator.java 231785 2004-02-16 23:39:59Z jochen $
 */
public interface JMValidator extends Validator {
  /** <p>Sets the JAXBContext that created this validator.</p>
   */
  public void setJAXBContextImpl(JAXBContextImpl pContext);
  /** <p>Returns the JAXBContext that created this validator.</p>
   */
  public JAXBContextImpl getJAXBContextImpl();
}
