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


/** <p>An implementation of a Java constructor.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class JavaConstructor extends AbstractJavaMethod {
  /** Creates a new JavaConstructor with the given protection */
  JavaConstructor(String pName, JavaSource.Protection pProtection) {
    super(pName, null, pProtection);
  }

  public String getName() {
  	 String name = super.getName();
  	 int offset = name.lastIndexOf('$');
  	 if (offset > -1) {
  	 	return name.substring(offset+1);
  	 } else {
  	 	return name;
  	 }
  }

  protected void writeHeader(IndentationTarget pTarget) throws IOException {
    if (pTarget.isInterface()) {
      return;
    }
    pTarget.indent(0);
    JavaSource.Protection protection = getProtection();
    if (protection != null  &&  !protection.equals(JavaSource.DEFAULT_PROTECTION)) {
      pTarget.write(getProtection().toString());
      pTarget.write(" ");
    }
    pTarget.write(getName());
    pTarget.write("(");
    Parameter[] params = getParams();
    for (int i = 0;  i < params.length;  i++) {
      if (i > 0) {
        pTarget.write(", ");
      }
      Parameter p = params[i];
      pTarget.write(pTarget.asString(p.getType()));
      pTarget.write(" ");
      pTarget.write(p.getName());
    }
    pTarget.write(")");
    JavaQName[] exceptions = getExceptions();
    for (int i = 0;  i < exceptions.length; i++) {
        if (i > 0) {
          pTarget.write(", ");
        } else {
          pTarget.write(" throws ");
        }
      pTarget.write(pTarget.asString(exceptions[i]));
    }
    pTarget.write(" {");
    pTarget.write();
  }
}
