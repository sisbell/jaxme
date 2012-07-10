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
package org.apache.ws.jaxme.sqls;

/** <p>An abstract value.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface Value {
  public class Type {
    private String name;
    protected Type(String pName) {
      name = pName;
    }
    public String toString() { return name; }
    public String getName() { return name; }
    public int hashCode() { return name.hashCode(); }
    public boolean equals(Object o) {
      return o != null  &&  o instanceof Type  &&
          ((Type) o).name.equals(name);
    }
    public final static Type PLACEHOLDER = new Type("PLACEHOLDER");
    public final static Type NULL = new Type("NULL");
    public final static Type BOOLEAN = new Type("BOOLEAN");
    public final static Type FLOAT = new Type("FLOAT");
    public final static Type DOUBLE = new Type("DOUBLE");
    public final static Type BYTE = new Type("BYTE");
    public final static Type SHORT = new Type("SHORT");
    public final static Type INT = new Type("INT");
    public final static Type LONG = new Type("LONG");
    public final static Type DATETIME = new Type("DATETIME");
    public final static Type DATE = new Type("DATE");
    public final static Type TIME = new Type("TIME");
    public final static Type STRING = new Type("STRING");
  }

  /** <p>Returns the values type.</p>
   */
  public Type getType();

  /** <p>Returns the actual value. This may be null.</p>
   */
  public Object getValue();
}
