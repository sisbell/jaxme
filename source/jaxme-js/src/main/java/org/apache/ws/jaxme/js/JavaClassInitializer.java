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


/** <p>Implements a Java class constructor.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class JavaClassInitializer extends ConditionalIndentationJavaSourceObject {
  /** Creates a new instance of JavaClassInitializer */
  JavaClassInitializer() {
    super(null, null, null);
  }

  protected void writeHeader(IndentationTarget pTarget) throws IOException {
    if (!pTarget.isInterface()) {
      pTarget.indent(0);
      pTarget.write("static {");
      pTarget.write();
    }
  }
}
