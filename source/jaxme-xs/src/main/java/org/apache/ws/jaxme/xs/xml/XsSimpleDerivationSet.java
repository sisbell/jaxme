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
package org.apache.ws.jaxme.xs.xml;

import java.util.StringTokenizer;

/** <p>Implementation of <code>xs:derivationset</code>.
 * Follows this specification:
 * <pre>
 *  &lt;xs:simpleType name="simpleDerivationSet"&gt;
 *    &lt;xs:annotation&gt;
 *      &lt;xs:documentation&gt;
 *        #all or (possibly empty) subset of {restriction, union, list}
 *      &lt;/xs:documentation&gt;
 *      &lt;xs:documentation&gt;
 *        A utility type, not for public use
 *      &lt;/xs:documentation&gt;
 *    &lt;/xs:annotation&gt;
 *    &lt;xs:union&gt;
 *      &lt;xs:simpleType&gt;
 *        &lt;xs:restriction base="xs:token"&gt;
 *          &lt;xs:enumeration value="#all"/&gt;
 *        &lt;/xs:restriction&gt;
 *      &lt;/xs:simpleType&gt;
 *      &lt;xs:simpleType&gt;
 *        &lt;xs:restriction base="xs:derivationControl"&gt;
 *          &lt;xs:enumeration value="list"/&gt;
 *          &lt;xs:enumeration value="union"/&gt;
 *          &lt;xs:enumeration value="restriction"/&gt;
 *        &lt;/xs:restriction&gt;
 *      &lt;/xs:simpleType&gt;
 *    &lt;/xs:union&gt;
 *  &lt;/xs:simpleType&gt;
 * </pre></p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XsSimpleDerivationSet {
  final boolean listAllowed, unionAllowed, restrictionAllowed;

  /** <p>Returns whether derivation of lists is allowed.</p>
   */
  public boolean isListAllowed() {
    return listAllowed;
  }

  /** <p>Returns whether derivation of unions is allowed.</p>
   */
  public boolean isUnionAllowed() {
    return unionAllowed;
  }

  /** <p>Returns whether derivation of restrictions is allowed.</p>
   */
  public boolean isRestrictionAllowed() {
    return restrictionAllowed;
  }

  /** <p>Returns a <code>DerivationSet</code> matching the given
   * value.</p>
   */
  public static XsSimpleDerivationSet valueOf(String pValue) {
    return new XsSimpleDerivationSet(pValue);
  }

  /** <p>Creates a new DerivationSet with the given value.</p>
   */
  public XsSimpleDerivationSet(String pValue) {
    if ("#all".equals(pValue)) {
      listAllowed = true;
      unionAllowed = true;
      restrictionAllowed = true;
    } else {
      boolean acceptList = false;
      boolean acceptUnion = false;
      boolean acceptRestriction = false;
      for (StringTokenizer st = new StringTokenizer(pValue, " ");  st.hasMoreTokens();  ) {
        String s = st.nextToken();
        if ("list".equals(s)) {
          acceptList = true;
        } else if ("union".equals(s)) {
          acceptUnion = true;
        } else if ("restriction".equals(s)) {
          acceptRestriction = true;
        } else {
          throw new IllegalArgumentException("Invalid derivation set value: " + pValue + "; the token " + s + " did not resolve to either of 'extension' or 'restriction'");
        }
      }
      listAllowed = acceptList;
      unionAllowed = acceptUnion;
      restrictionAllowed = acceptRestriction;
    }
  }

  public String toString() {
    final StringBuffer sb = new StringBuffer();
    if (listAllowed) {
      sb.append("list");
    }
    if (unionAllowed) {
      if (sb.length() > 0) {
        sb.append(" ");
      }
      sb.append("union");
    }
    if (restrictionAllowed) {
      if (sb.length() > 0) {
        sb.append(" ");
      }
      sb.append("restriction");
    }
    return sb.toString();
  }

  public boolean equals(Object o) {
    if (o == null  ||  !(XsSimpleDerivationSet.class.equals(o.getClass()))) {
      return false;
    }
    XsSimpleDerivationSet ds = (XsSimpleDerivationSet) o;
    return ds.listAllowed == listAllowed  &&
            ds.unionAllowed == unionAllowed  &&
            ds.restrictionAllowed == restrictionAllowed;
  }

  public int hashCode() {
    int result = 0;
    if (listAllowed) { result += 1; }
    if (unionAllowed) { result += 2; }
    if (restrictionAllowed) { result += 4; }
    return result;
  }
}
