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
 *  &lt;xs:simpleType name="derivationSet"&gt;
 *    &lt;xs:annotation&gt;
 *      &lt;xs:documentation&gt;
 *        A utility type, not for public use
 *      &lt;/xs:documentation&gt;
 *      &lt;xs:documentation&gt;
 *        #all or (possibly empty) subset of {extension, restriction}
 *      &lt;/xs:documentation&gt;
 *    &lt;/xs:annotation&gt;
 *    &lt;xs:union&gt;
 *      &lt;xs:simpleType&gt;
 *        &lt;xs:restriction base="xs:token"&gt;
 *          &lt;xs:enumeration value="#all"/&gt;
 *        &lt;/xs:restriction&gt;
 *      &lt;/xs:simpleType&gt;
 *      &lt;xs:simpleType&gt;
 *        &lt;xs:list itemType="xs:reducedDerivationControl"/&gt;
 *      &lt;/xs:simpleType&gt;
 *    &lt;/xs:union&gt;
 *  &lt;/xs:simpleType&gt;
 * </pre>
 * </p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XsDerivationSet {
  boolean extensionAllowed, restrictionAllowed;

  /** <p>Returns whether extension is allowed.</p>
   */
  public boolean isExtensionAllowed() {
    return extensionAllowed;
  }

  /** <p>Sets whether extension is allowed.</p>
   */
  public void setExtensionAllowed(boolean pExtensionAllowed) {
    extensionAllowed = pExtensionAllowed;
  }

  /** <p>Returns whether restriction is allowed.</p>
   */
  public boolean isRestrictionAllowed() {
    return restrictionAllowed;
  }

  /** <p>Sets whether restriction is allowed.</p>
   */
  public void setRestrictionAllowed(boolean pRestrictionAllowed) {
    restrictionAllowed = pRestrictionAllowed;
  }

  /** <p>Returns a <code>DerivationSet</code> matching the given
   * value.</p>
   */
  public static XsDerivationSet valueOf(String pValue) {
    return new XsDerivationSet(pValue);
  }

  /** <p>Creates a new DerivationSet with the given value.</p>
   */
  public XsDerivationSet(String pValue) {
    if ("#all".equals(pValue)) {
      setExtensionAllowed(true);
      setRestrictionAllowed(true);
    } else {
      for (StringTokenizer st = new StringTokenizer(pValue, " ");  st.hasMoreTokens();  ) {
        String s = st.nextToken();
        if ("extension".equals(s)) {
          setExtensionAllowed(true);
        } else if ("restriction".equals(s)) {
          setRestrictionAllowed(true);
        } else {
          throw new IllegalArgumentException("Invalid derivation set value: " + pValue + "; the token " + s + " did not resolve to either of 'extension' or 'restriction'");
        }
      }
    }
  }

  public String toString() {
    if (isExtensionAllowed()) {
      if (isRestrictionAllowed()) {
        return "extension restriction";
      } else {
        return "extension";
      }
    } else {
      if (isRestrictionAllowed()) {
        return "restriction";
      } else {
        return "";
      }
    }
  }

  public boolean equals(Object o) {
    if (o == null  ||  !(XsDerivationSet.class.equals(o.getClass()))) {
      return false;
    }
    XsDerivationSet ds = (XsDerivationSet) o;
    return ds.extensionAllowed == extensionAllowed  &&
            ds.restrictionAllowed == restrictionAllowed;
  }

  public int hashCode() {
    int result = 0;
    if (extensionAllowed) { result += 1; }
    if (restrictionAllowed) { result += 2; }
    return result;
  }
}
