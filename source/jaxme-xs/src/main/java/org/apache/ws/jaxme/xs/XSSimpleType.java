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
package org.apache.ws.jaxme.xs;


/** <p>Details of a simple type.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface XSSimpleType {
  /** <p>Returns whether the simple type is atomic.</p>
   */
  public boolean isAtomic();
  /** <p>Returns the atomic type details.</p>
   * @throws IllegalStateException The type is a list or union.
   */
  public XSAtomicType getAtomicType();
  /** <p>Returns whether the simple type is a list.</p>
   */
  public boolean isList();
  /** <p>Returns the list type details.</p>
   * @throws IllegalStateException The type is atomic or a union.
   */
  public XSListType getListType();
  /** <p>Returns whether the simple type is a union.</p>
   */
  public boolean isUnion();
  /** <p>Returns the union type details.</p>
   * @throws IllegalStateException The type is atomic or a list.
   */
  public XSUnionType getUnionType();

  /** <p>Returns the value of the "pattern" facet or null, if the pattern has not been set.</p>
   * <p>The returned value is an array of pattern arrays. The latter arrays are the
   * patterns defined in one restriction step. These have to be combined by "OR".
   * The resulting, combined arrays are being grouped by an "AND". This is according to
   * <a href="http://www.w3.org/TR/xmlschema-2/index.html#rf-pattern">
   *   http://www.w3.org/TR/xmlschema-2/index.html#rf-pattern, 4.3.4.3</a>.</p>
   */
  public String[][] getPattern();

  /** <p>Returns the values of the "enumeration" facets.</p>
   */
  public XSEnumeration[] getEnumerations();

  /** <p>Returns whether the type is a restriction of another
   * simple type. (Almost all simple types are, the exception being
   * the ur type {@link org.apache.ws.jaxme.xs.types.XSAnySimpleType}.</p>
   */
  public boolean isRestriction();

  /** <p>If the simple type is a restriction, returns the restricted
   * simple type.</p>
   *
   * @throws IllegalStateException This type is no restriction of
   *   another simple type.
   */
  public XSType getRestrictedType();
}
