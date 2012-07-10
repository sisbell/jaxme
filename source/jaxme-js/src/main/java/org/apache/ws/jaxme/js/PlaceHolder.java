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


/** <p>Interface of a placeholder. A placeholder is used by one
 * method to note, that another method should insert code "here".
 * A placeholder is created by invoking
 * {@link org.apache.ws.jaxme.js.IndentationEngine#newPlaceHolder(String, boolean)}.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface PlaceHolder {
  /** <p>Returns the placeholders name.</p>
   */
  public String getName();

  /** <p>Returns whether the placeholder is automatically removable.</p>
   */
  public boolean isAutoRemovable();

  /** <p>Removes the placeholder. If a placeholder isn't automatically
   * removable, then it must be removed by an explicit invocation of
   * this method.</p>
   */
  public void remove();

  /** <p>Sets a property.</p>
   */
  public void setProperty(String pName, Object pValue);

  /** <p>Returns a property value. There is no distinction between the
   * property value null and the case where the property isn't set.</p>
   */
  public Object getProperty(String pName);

  /** <p>Returns the placeholders indentation level.</p>
   */
  public int getLevel();
}
