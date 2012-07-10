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
package org.apache.ws.jaxme.generator.sg;

import java.io.Serializable;

/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface Facet {
  public class Type implements Serializable {
    private final String name;
    Type(String pName) { name = pName; }
    public String getName() { return name; }
    public String toString() { return name; }
    private static final Type[] instances = new Type[]{
      ENUMERATION, FRACTION_DIGITS, LENGTH, MAX_EXCLUSIVE, MAX_INCLUSIVE,
      MAX_LENGTH, MIN_EXCLUSIVE, MIN_INCLUSIVE, MIN_LENGTH, PATTERN, TOTAL_DIGITS 
    };
    public static Type valueOf(String pName) {
      for (int i = 0;  i < instances.length;  i++) {
        if (instances[i].name.equals(pName)) {
          return instances[i];
        }
      }
      throw new IllegalArgumentException("Unknown facet type: " + pName);
    }
    public int hashCode() { return name.hashCode(); }
    public boolean equals(Object o) {
      return o != null  &&  (o instanceof Type)  &&  ((Type) o).name.equals(name);
    }
  };

  /** <p>The facet type enumeration. Use the method {@link #getValues}
   * to query for the values.</p>
   */
  public static final Type ENUMERATION = new Type("enumeration");

  /** <p>The facet type fractionDigits. Use the method {@link #getNumValue}
   * to query for the values.</p>
   */
  public static final Type FRACTION_DIGITS = new Type("fractionDigits");

  /** <p>The facet type length. Use the method {@link #getNumValue}
   * to query for the values.</p>
   */
  public static final Type LENGTH = new Type("length");

  /** <p>The facet type maxLength. Use the method {@link #getNumValue}
   * to query for the values.</p>
   */
  public static final Type MAX_LENGTH = new Type("maxLength");

  /** <p>The facet type maxExclusive. Use the method {@link #getValue} to
   * query for the value.</p>
   */
  public static final Type MAX_EXCLUSIVE = new Type("maxExclusive");

  /** <p>The facet type maxInclusive. Use the method {@link #getValue} to
   * query for the value.</p>
   */
  public static final Type MAX_INCLUSIVE = new Type("maxInclusive");

  /** <p>The facet type minLength. Use the method {@link #getNumValue}
   * to query for the values.</p>
   */
  public static final Type MIN_LENGTH = new Type("minLength");

  /** <p>The facet type minExclusive. Use the method {@link #getValue} to
   * query for the value.</p>
   */
  public static final Type MIN_EXCLUSIVE = new Type("minExclusive");

  /** <p>The facet type minInclusive. Use the method {@link #getValue} to
   * query for the value.</p>
   */
  public static final Type MIN_INCLUSIVE = new Type("minInclusive");

  /** <p>The facet type pattern. Use the method {@link #getValues}
   * to query for the values.</p>
   */
  public static final Type PATTERN = new Type("pattern");

  /** <p>The facet type totalDigits. Use the method {@link #getNumValue}
   * to query for the values.</p>
   */
  public static final Type TOTAL_DIGITS = new Type("totalDigits");

  /** <p>Returns the facet type.</p>
   */
  public Type getType();

  /** <p>If the facet has the types {@link #ENUMERATION} or {@link #PATTERN}:
   * Used to fetch the possible values.</p>
   */
  public String[] getValues();

  /** <p>If the facet has the types {@link #MAX_EXCLUSIVE}, {@link #MIN_EXCLUSIVE},
   * {@link #MAX_INCLUSIVE}, or {@link #MIN_INCLUSIVE}: Returns the facet value.</p>
   */
  public String getValue();

  /** <p>If the facet has the types {@link #FRACTION_DIGITS} or {@link #TOTAL_DIGITS}:
   * Returns the facet value.</p>
   */
  public long getNumValue();
}
