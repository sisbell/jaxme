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
 *   &lt;xs:simpleType name="blockSet"&gt;
 *     &lt;xs:annotation&gt;
 *       &lt;xs:documentation&gt;
 *         A utility type, not for public use
 *       &lt;/xs:documentation&gt;
 *       &lt;xs:documentation&gt;
 *         #all or (possibly empty) subset of {substitution, extension,
 *         restriction}
 *       &lt;/xs:documentation&gt;
 *     &lt;/xs:annotation&gt;
 *     &lt;xs:union&gt;
 *       &lt;xs:simpleType&gt;
 *         &lt;xs:restriction base="xs:token"&gt;
 *           &lt;xs:enumeration value="#all"/&gt;
 *         &lt;/xs:restriction&gt;
 *       &lt;/xs:simpleType&gt;
 *       &lt;xs:simpleType&gt;
 *         &lt;xs:list&gt;
 *           &lt;xs:simpleType&gt;
 *             &lt;xs:restriction base="xs:derivationControl"&gt;
 *               &lt;xs:enumeration value="extension"/&gt;
 *               &lt;xs:enumeration value="restriction"/&gt;
 *               &lt;xs:enumeration value="substitution"/&gt;
 *             &lt;/xs:restriction&gt;
 *           &lt;/xs:simpleType&gt;
 *         &lt;/xs:list&gt;
 *       &lt;/xs:simpleType&gt;
 *     &lt;/xs:union&gt;
 *   &lt;/xs:simpleType&gt;
 * </pre>
 * </p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XsBlockSet {
  boolean extensionAllowed, restrictionAllowed, substitutionAllowed;

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

  /** <p>Returns whether restriction is allowed.</p>
   */
  public boolean isSubstitutionAllowed() {
    return substitutionAllowed;
  }

  /** <p>Sets whether restriction is allowed.</p>
   */
  public void setSubstitutionAllowed(boolean pSubstitutionAllowed) {
    substitutionAllowed = pSubstitutionAllowed;
  }

  /** <p>Returns a <code>DerivationSet</code> matching the given
   * value.</p>
   */
  public static XsBlockSet valueOf(String pValue) {
    return new XsBlockSet(pValue);
  }

  /** <p>Creates a new DerivationSet with the given value.</p>
   */
  public XsBlockSet(String pValue) {
    if ("#all".equals(pValue)) {
      setExtensionAllowed(false);
      setRestrictionAllowed(false);
      setSubstitutionAllowed(false);
    } else {
      setExtensionAllowed(true);
      setRestrictionAllowed(true);
      setSubstitutionAllowed(true);
      for (StringTokenizer st = new StringTokenizer(pValue, " ");  st.hasMoreTokens();  ) {
        String s = st.nextToken();
        if ("extension".equals(s)) {
          setExtensionAllowed(false);
        } else if ("restriction".equals(s)) {
          setRestrictionAllowed(false);
        } else if ("substitution".equals(s)) {
          setSubstitutionAllowed(false);
        } else {
          throw new IllegalArgumentException("Invalid block set value: " + pValue + "; the token " + s + " did not resolve to either of 'extension', 'restriction', or 'substitution'.");
        }
      }
    }
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    if (extensionAllowed) {
      sb.append("extension");
    }
    if (restrictionAllowed) {
      if (sb.length() > 0) {
        sb.append(" ");
      }
      sb.append("restriction");
    }
    if (substitutionAllowed) {
      if (sb.length() > 0) {
        sb.append(" ");
      }
      sb.append("substitution");
    }
    return sb.toString();
  }

  public boolean equals(Object o) {
    if (o == null  ||  !(XsBlockSet.class.equals(o.getClass()))) {
      return false;
    }
    XsBlockSet bs = (XsBlockSet) o;
    return bs.extensionAllowed == extensionAllowed  &&
            bs.restrictionAllowed == restrictionAllowed  &&
            bs.substitutionAllowed == substitutionAllowed;
  }

  public int hashCode() {
    int result = 0;
    if (extensionAllowed) { result += 1; }
    if (restrictionAllowed) { result += 2; }
    if (substitutionAllowed) { result += 4; }
    return result;
  }
}
