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
package org.apache.ws.jaxme.pm.generator.jdbc;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class CustomColumnData {
  private final String name;
  private final Object sg;
  /** Creates a new instance with the given name, which is
   * attached to the given <code>pSG</code>.
   */
  public CustomColumnData(String pName, Object pSG) {
    name = pName;
    sg = pSG;
  }
  /** Returns the custom column datas name.
   */
  public String getName() { return name; }
  /** Returns the custom column datas attached object.
   */
  public Object getSG() { return sg; }
}
