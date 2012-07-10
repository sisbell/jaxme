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
package org.apache.ws.jaxme.js;

import java.io.IOException;

/** <p>Interface of an object which is able to write itself
 * into a given {@link org.apache.ws.jaxme.js.IndentationTarget}.</p>
 * using methods of the given {@link org.apache.ws.jaxme.js.IndentationEngine}.</p>
 *
 * @author <a href="mailto:jwi@softwareag.com">Jochen Wiedmann</a>
 */
public interface IndentedObject {
   public void write(IndentationEngine pEngine, IndentationTarget pTarget)
       throws IOException;
}
