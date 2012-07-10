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

/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface AtomicTypeSG {
  /** <p>Returns the value of the "maxExclusive" facet or null, if the facet
   * is not set.</p>
   */
  public String getMaxExclusive();
  /** <p>Returns the value of the "maxInclusive" facet or null, if the facet
   * is not set.</p>
   */
  public String getMaxInclusive();
  /** <p>Returns the value of the "minExclusive" facet or null, if the facet
   * is not set.</p>
   */
  public String getMinExclusive();
  /** <p>Returns the value of the "minInclusive" facet or null, if the facet
   * is not set.</p>
   */
  public String getMinInclusive();
  /** <p>Returns the value of the "length" facet or null, if the facet
   * is not set.</p>
   */
  public Long getLength();
  /** <p>Returns the value of the "minLength" facet or null, if the facet
   * is not set.</p>
   */
  public Long getMinLength();
  /** <p>Returns the value of the "maxLength" facet or null, if the facet
   * is not set.</p>
   */
  public Long getMaxLength();
  /** <p>Returns the value of the "fractionDigits" facet or null, if the facet
   * is not set.</p>
   */
  public Long getFractionDigits();
  /** <p>Returns the value of the "totalDigits" facet or null, if the facet
   * is not set.</p>
   */
  public Long getTotalDigits();
    /** Returns the values of the "pattern" facets. Null,
     * if the facet is not set.
     */
  	public String[] getPatterns();
}
