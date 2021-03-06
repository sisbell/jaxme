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

public class SchemaOption {
  private final String name;
  private final String target;
  private final String value;

  /** Creates a new instance of SchemaOption */
  public SchemaOption(String pName, String pValue, String pTarget) {
    name = pName;
    target = (pTarget == null  ||  pTarget.length() == 0) ? null : pTarget;
    value = pValue;
  }

  public String getName() { return name; }
  public String getTarget() { return target; }
  public String getValue() { return value; }

  public String toString() {
    if (target == null) {
      return name + "=" + value;
    } else {
      return name + "=" + target + "=" + value;
    }
  }
}
