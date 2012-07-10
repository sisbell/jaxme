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


/** <p>Interface of a {@link JavaField}, which is local to a
 * {@link JavaMethod}.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface LocalJavaField extends IndentationEngine, DirectAccessible,
    IndentedObject {
  /** <p>Returns the fields name.</p>
   */
  public String getName();

  /** <p>Returns the fields type.</p>
   */
  public JavaQName getType();

  /** <p>Returns whether the field is final. By default it isn't.</p>
   */
  public boolean isFinal();

  /** <p>Sets whether the field is final. By default it isn't.</p>
   */
  public void setFinal(boolean pFinal);
}
