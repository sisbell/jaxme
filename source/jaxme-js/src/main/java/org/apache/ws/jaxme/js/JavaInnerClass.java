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


/** <p>Implements an inner class.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class JavaInnerClass extends JavaSource {
  /** <p>Creates a new JavaInnerClass with the given protection.</p>
   *
   * @param pPackage The package name; null or empty string indicates
   *   the root package
   * @param pName The class or interface name
   * @param pProtection null, "public", "protected" or "private"
   */
  JavaInnerClass(JavaSource pOuterClass, JavaQName pName, Protection pProtection) {
    super(pOuterClass.getFactory(), pName, pProtection);
    outerClass = pOuterClass;
  }

  JavaSource outerClass;
  /** <p>Returns the outer JavaSource instance.</p>
   */
  public JavaSource getOuterClass() {
    return outerClass;
  }

  /** <p>Throws an exception, as inner classes don't have import
   * statements.</p>
   */
  public void addImport(String s) {
    throw new IllegalArgumentException("Inner classes must not use the import statement");
  }

  /** <p>Returns whether this inner class is static.</p>
   */
  public boolean getStatic() {
    return super.getStatic();
  }

  /** <p>Sets whether this inner class is static.</p>
   */
  public void setStatic(boolean pStatic) {
    super.setStatic(pStatic);
  }

  /** <p>Returns, whether this is an inner class.</p>
	*/
  public boolean isInnerClass() {
	  return true;
  }
}
