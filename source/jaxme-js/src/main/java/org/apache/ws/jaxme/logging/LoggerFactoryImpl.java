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
package org.apache.ws.jaxme.logging;

import java.util.HashMap;
import java.util.Map;


/** <p>Default implementation of a {@link LoggerFactory}. The default
 * implementation holds an internal Map of Loggers.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public abstract class LoggerFactoryImpl implements LoggerFactory {
   private Map loggers = new HashMap();

   /** <p>Creates a new {@link Logger} with the given name. The logger
    * will be added to an internal {@link Map} and the next call to
    * {@link #getLogger(String)} with the same name will return
    * this {@link Logger}.</p>
    */
   public abstract Logger newLogger(String pName);

   /** <p>Returns a {@link Logger} with the name <code>pName</code>.
    * If the internal {@link Map} of loggers already contains a {@link Logger}
    * with name <code>pName</code>, returns that one. Otherwise creates
    * a new instance by calling {@link #newLogger(String)}, stores
    * the instance in the internal {@link Map} and returns it.</p> 
    */
	public Logger getLogger(String pName) {
      Logger result = (Logger) loggers.get(pName);
      if (result == null) {
         result = newLogger(pName);
         loggers.put(pName, result);
      }
      return result;
	}
}
