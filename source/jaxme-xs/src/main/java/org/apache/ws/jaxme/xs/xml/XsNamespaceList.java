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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;


/** <p>Implementation of the <code>xs:namespaceList</code> type,
 * specified like this:
 * <pre>
 *  &lt;xs:simpleType name="namespaceList"&gt;
 *    &lt;xs:annotation&gt;
 *      &lt;xs:documentation&gt;
 *        A utility type, not for public use
 *      &lt;/xs:documentation&gt;
 *    &lt;/xs:annotation&gt;
 *    &lt;xs:union&gt;
 *      &lt;xs:simpleType&gt;
 *        &lt;xs:restriction base="xs:token"&gt;
 *          &lt;xs:enumeration value="##any"/&gt;
 *          &lt;xs:enumeration value="##other"/&gt;
 *        &lt;/xs:restriction&gt;
 *      &lt;/xs:simpleType&gt;
 *      &lt;xs:simpleType&gt;
 *        &lt;xs:list&gt;
 *          &lt;xs:simpleType&gt;
 *            &lt;xs:union memberTypes="xs:anyURI"&gt;
 *              &lt;xs:simpleType&gt;
 *                &lt;xs:restriction base="xs:token"&gt;
 *                  &lt;xs:enumeration value="##targetNamespace"/&gt;
 *                   &lt;xs:enumeration value="##local"/&gt;
 *                &lt;/xs:restriction&gt;
 *              &lt;/xs:simpleType&gt;
 *            &lt;/xs:union&gt;
 *          &lt;/xs:simpleType&gt;
 *        &lt;/xs:list&gt;
 *      &lt;/xs:simpleType&gt;
 *    &lt;/xs:union&gt;
 *  &lt;/xs:simpleType&gt;
 * </pre></p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public abstract class XsNamespaceList {
	/** <p>The namespace list matching "##any".</p>
	 */
    public static final XsNamespaceList ANY = new XsNamespaceList(){
    	/** @return true
    	 */
        public boolean isAny() { return true; }
        /** @return false
         */
        public boolean isOther() { return false; }
        /** @return null
         */
        public XsAnyURI[] getUris() { return null; }
        /** @return The string "##any"
         */
        public String toString() { return "##any"; }
    };

    /** <p>A namespace list matching "##other" with the given
     * target namespace <code>pTargetNamespace</code>.</p>
     */
    public static class Other extends XsNamespaceList {
    	private final XsAnyURI[] uris;
    	protected Other(XsAnyURI pTargetNamespace) {
    		if (pTargetNamespace == null) {
                pTargetNamespace = new XsAnyURI("");
            }
    		uris = new XsAnyURI[]{pTargetNamespace};
        }
        /** @return false
         */
    	public boolean isAny() { return false; }
    	/** @return true
    	 */
        public boolean isOther() { return true; }
        /** @return An array with a single element (the target namespace or "")
         */
        public XsAnyURI[] getUris() { return uris; }
        /** @return The string "##other".
         */
        public String toString() { return "##other"; }
        /** <p>Returns getUris()[0].hashCode().</p>
         */
        public int hashCode() { return getUris()[0].hashCode(); }
        /** <p>Returns, whether this is an instance of
         * {@link Other} with the same target namespace.</p>
         */
        public boolean equals(Object pOther) {
        	return pOther != null
                && (pOther instanceof Other)
                && getUris()[0].equals(((Other) pOther).getUris()[0]);
        }
    }

    /**
     * Default implementation of {@link XsNamespaceList}.
     */
    public static class Basic extends XsNamespaceList {
        private final XsAnyURI[] uris;
        private final String toStr;
        protected Basic(String pValue, XsAnyURI pTargetNamespace) {
            toStr = pValue;
            List list = new ArrayList();
            if (pTargetNamespace == null) {
            	pTargetNamespace = new XsAnyURI("");
            }
            for (StringTokenizer st = new StringTokenizer(pValue);  st.hasMoreTokens();  ) {
                String s = st.nextToken();
                final XsAnyURI uri;
                if ("##targetNamespace".equals(s)) {
                	uri = pTargetNamespace;
                } else if ("##local".equals(pTargetNamespace)) {
                	uri = new XsAnyURI("");
                } else {
                    uri = new XsAnyURI(s);
                }
                if (!list.contains(uri)) {
                    list.add(uri);
                }
            }
            uris = (XsAnyURI[]) list.toArray(new XsAnyURI[list.size()]);
        }
        /** @return false
         */
        public boolean isAny() { return false; }
        /** @return false
         */
        public boolean isOther() { return false; }
        /** @return An array with the URI's specified in the
         * 'namespace' attribute.
         */
        public XsAnyURI[] getUris() { return uris; }
        /** @return The unmodified value of the 'namespace' attribute.
         */
        public String toString() { return toStr; }
        /** @return An hash code suitable for applying
         * {@link Arrays#equals(Object[], Object[])} on
         * the result of {@link #getUris()}.</p>
         */
        public int hashCode() {
            XsAnyURI[] uris = getUris();
            int hash = uris.length;
            for (int i = 0;  i < uris.length;  i++) {
                hash += uris[i].hashCode();
            }
            return hash;
        }
        /** <p>Implemented with {@link Arrays#equals(Object[], Object[])}
         * and the result of {@link #getUris()}.</p>
         */
        public boolean equals(Object pOther) {
        	return pOther != null
                &&  pOther instanceof Basic
                &&  Arrays.equals(getUris(), ((Basic) pOther).getUris());
        }
    }

    /** <p>Returns a namespace list, matching the 'namespace' attribute
     * given by <code>pValue</code>. The given target namespace is used,
     * if required.</p>
     */
    public static XsNamespaceList valueOf(String pValue, XsAnyURI pTargetNamespace) {
		if ("##any".equals(pValue)) {
			return ANY;
        } else if ("##other".equals(pValue)) {
            return new Other(pTargetNamespace);
        } else {
            return new Basic(pValue, pTargetNamespace);
        }
	}

  /** <p>Returns whether the namespace list matches <code>##any</code>.
   * If this is the case, then {@link #isOther()} returns false
   * and {@link #getUris()} returns null.</p>
   */
  public abstract boolean isAny();

  /** <p>Returns whether the namespace list matches <code>##other</code>.
   * If the result is true, then {@link #getUris()} may be used to
   * obtain an array with a single element, the target namespace.</p>
   */
  public abstract boolean isOther();

  /** <p>Returns the array of URI's specified in the namespace list.
   * If {@link #isAny()} returns true, then the result is null.
   * If {@link #isOther()} returns true, then the result is an
   * array with a single element: The target namespace or "" for
   * an absent namespace.</p>
   */
  public abstract XsAnyURI[] getUris();
}
