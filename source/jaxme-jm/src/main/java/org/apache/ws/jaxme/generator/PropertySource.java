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
package org.apache.ws.jaxme.generator;

/** <p>An instance of PropertySource (typically the {@link org.apache.ws.jaxme.generator.Generator}
 * can be queried for properties.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface PropertySource {
  /** <p>Returns the given property value or null, if the property is not
   * set.</p>
   */
  public String getProperty(String pName);

  /** <p>Returns the given property value. If the property is not set, returns
   * the given default value.</p>
   */
  public String getProperty(String pName, String pDefault);

  /** <p>Sets the given property value.</p>
   */
  public void setProperty(String pName, String pValue);
}
