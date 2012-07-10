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

/** <p>Interface that an IndentationEngine's target must
 * fulfill.</p>
 */
public interface IndentationTarget {
  /** <p>Returns whether the IndentationEngine is creating a Java
   * interface. Creating an interface means, for example, that
   * method bodies are being suppressed.</p>
   */
  public boolean isInterface();
  /** <p>Indents the current line by adding blanks for the given
   * indentation level. This method must be called before any
   * of the <code>write(String)</code> or <code>write()</code>
   * methods or following the line terminating <code>write()</code>
   * method.</p>
   */
  public void indent(int i) throws IOException;
  /** <p>Converts a class name into a string. The string may then be
   * written to the target using <code>write()</code>.</p>
   */
  public String asString(JavaQName pQName);
  /** <p>Writes a string to the target.</p>
   */
  public void write(String pValue) throws IOException;
  /** <p>Terminates a line in the target.</p>
   */
  public void write() throws IOException;
}
