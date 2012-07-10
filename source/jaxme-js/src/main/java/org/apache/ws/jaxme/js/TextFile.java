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


/** <p>Interface of a text file.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface TextFile extends IndentationEngine {
  /** <p>Returns the text files package name. This package name isn't necessarily
   * a valid Java package name, for example it may be "META-INF".</p>
   */
  public String getPackageName();

  /** <p>Returns the text files file name, without any path component.</p>
   */
  public String getFileName();

  /** <p>Returns the text files contents, as a string.</p>
   */
  public String getContents();

  /** <p>Sets the text files contents, as a string.</p>
   */
  public void setContents(String pContents);
}
