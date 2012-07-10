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


/** <p>Implements a field that a java class or interface may have.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class JavaField extends JavaSourceObject implements DirectAccessible {
  private class MyTarget implements IndentationTarget {
    private StringBuffer sb = new StringBuffer();
    private IndentationTarget actualTarget;
    private MyTarget(IndentationTarget pActualTarget) {
      actualTarget = pActualTarget;
    }
    public boolean isInterface() {
      return actualTarget.isInterface();
    }
    public String asString(JavaQName pQName) {
      return actualTarget.asString(pQName);
    }
    public void write(String pValue) {
      sb.append(pValue);
    }
    public void write() {
      sb.append("\n");
    }
    public void indent(int i) {
    }
    public String getResult() { return sb.toString(); }
  }

  private boolean isTransient;
  private boolean isNullable = true;

  /** <p>Creates a new JavaField with the given name, type and
   * protection.</p>
   */
  JavaField(String pName, JavaQName pType, JavaSource.Protection pProtection) {
    super(pName, pType, pProtection);
  }

  /** <p>Sets whether the field is transient. By default it isn't.</p>
   */
  public void setTransient(boolean pTransient) {
  	 isTransient = pTransient;
  }

  /** <p>Returns whether the field is transient. By default it isn't.</p>
	*/
  public boolean isTransient() {
	 return isTransient;
  }


  /** <p>Returns a string representation of this field.</p>
   */
  public void write(IndentationTarget pTarget) throws IOException {
    pTarget.indent(0);
    writeNoEol(pTarget);
    pTarget.write();
  }

  protected void writeNoEol(IndentationTarget pTarget) throws IOException {
    if (pTarget.isInterface()) {
      return;
    }
    JavaComment jcon = getComment();
    if (jcon != null) {
      jcon.write(pTarget);
      pTarget.indent(0);
    }
    JavaSource.Protection protection = getProtection();
    if (protection != null  &&  !protection.equals(JavaSource.DEFAULT_PROTECTION)) {
      pTarget.write(protection.toString());
      pTarget.write(" ");
    }
    if (isFinal()) {
      pTarget.write("final ");
    }
    if (isStatic()) {
      pTarget.write("static ");
    }
    if (isTransient()) {
    	pTarget.write("transient ");
    }
    pTarget.write(pTarget.asString(getType()));
    pTarget.write(" ");
    pTarget.write(getName());
    if (isEmpty()) {
      pTarget.write(";");
    } else {
      pTarget.write(" = ");
      MyTarget mt = new MyTarget(pTarget);
      super.write(mt);
      String result = mt.getResult();
      if (result.endsWith("\r\n")) {
        result = result.substring(0, result.length()-2);
      } else if (result.endsWith("\n")) {
        result = result.substring(0, result.length()-1);
      }
      pTarget.write(result);
      if (!result.endsWith(";")) {
        pTarget.write(";");
      }
    }
  }

  public void setValue(Object pValue) {
    clear();
    addLine(pValue);
  }

  public boolean isNullable() { return isNullable; }
  public void setNullable(boolean pNullable) { isNullable = pNullable; }
}
