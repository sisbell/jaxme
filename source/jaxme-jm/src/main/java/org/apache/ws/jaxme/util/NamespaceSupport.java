/*
 * Copyright 2003,2004  The Apache Software Foundation
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
package org.apache.ws.jaxme.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;


/** <p>Similar to org.xml.sax.NamespaceSupport, but
 * for marshalling and not for parsing XML.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 * @version $Id: NamespaceSupport.java 232067 2005-03-10 10:14:08Z jochen $
 */
public class NamespaceSupport implements NamespaceContext {
    List prefixList;
    String cachedURI, cachedPrefix;
    
    /** <p>Creates a new instance of NamespaceSupport.</p> */
    public NamespaceSupport() {
    }
    
    /** <p>Resets the NamespaceSupport's state for reuse.</p>
     */
    public void reset() {
        cachedURI = cachedPrefix = null;
        if (prefixList != null) {
            prefixList.clear();
        }
    }
    
    /** <p>Declares a new prefix.</p>
     */
    public void declarePrefix(String pPrefix, String pURI) {
        if (pURI == null) { pURI = ""; }
        if (cachedURI == null) {
            cachedURI = pURI;
            cachedPrefix = pPrefix;
        } else {
            if (prefixList == null) { prefixList = new ArrayList(); }
            prefixList.add(cachedPrefix);
            prefixList.add(cachedURI);
            cachedPrefix = pPrefix;
            cachedURI = pURI;
        }
    }

    /** <p>Removes a prefix declaration. Assumes that the prefix is the
     * current prefix. If not, throws a IllegalStateException.</p>
     */
    public void undeclarePrefix(String pPrefix) {
        if (pPrefix.equals(cachedPrefix)) {
            if (prefixList != null  &&  prefixList.size() > 0) {
                cachedURI = prefixList.remove(prefixList.size()-1).toString();
                cachedPrefix = prefixList.remove(prefixList.size()-1).toString();
            } else {
                cachedPrefix = cachedURI = null;
            }
        } else {
            for (int i = prefixList.size()-2;  i >= 0;  i -= 2) {
                if (pPrefix.equals(prefixList.get(i))) {
                    prefixList.remove(i);
                    prefixList.remove(i);
                    return;
                }
            }
            throw new IllegalStateException("Undeclared prefix: " + pPrefix);
        }
    }
    
    /** <p>Given a prefix, returns the URI to which the prefix is
     * currently mapped or null, if there is no such mapping.</p>
     * <p><em>Note</em>: This methods behaviour is precisely
     * defined by {@link NamespaceContext#getNamespaceURI(java.lang.String)}.</p>
     *
     * @param pPrefix The prefix in question
     */
    public String getNamespaceURI(String pPrefix) {
        if (pPrefix == null) {
            throw new IllegalArgumentException("Namespace prefix must not be null");
        }
        if (cachedURI != null) {
            if (cachedPrefix.equals(pPrefix)) { return cachedURI; }
            if (prefixList != null) {
                for (int i = prefixList.size();  i > 0;  i -= 2) {
                    if (pPrefix.equals(prefixList.get(i-2))) {
                        return (String) prefixList.get(i-1);
                    }
                }
            }
        }
        if (XMLConstants.XML_NS_PREFIX.equals(pPrefix)) {
            return XMLConstants.XML_NS_URI;
        } else if (XMLConstants.XMLNS_ATTRIBUTE.equals(pPrefix)) {
            return XMLConstants.XMLNS_ATTRIBUTE_NS_URI;
        }
        return null;
    }
    
    /** <p>Returns a prefix currently mapped to the given URI or
     * null, if there is no such mapping. This method may be used
     * to find a possible prefix for an elements namespace URI. For
     * attributes you should use {@link #getAttributePrefix(String)}.</p>
     * <p><em>Note</em>: This methods behaviour is precisely
     * defined by {@link NamespaceContext#getPrefix(java.lang.String)}.</p>
     *
     * @param pURI The namespace URI in question
     * @throws IllegalArgumentException The namespace URI is null.
     */
    public String getPrefix(String pURI) {
        if (pURI == null) {
            throw new IllegalArgumentException("Namespace URI must not be null");
        }
        if (cachedURI != null) {
            if (cachedURI.equals(pURI)) { return cachedPrefix; }
            if (prefixList != null) {
                for (int i = prefixList.size();  i > 0;  i -= 2) {
                    if (pURI.equals(prefixList.get(i-1))) {
                        return (String) prefixList.get(i-2);
                    }
                }
            }
        }
        if (XMLConstants.XML_NS_URI.equals(pURI)) {
            return XMLConstants.XML_NS_PREFIX;
        } else if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(pURI)) {
            return XMLConstants.XMLNS_ATTRIBUTE;
        }
        return null;
    }
    
    /** <p>Returns a non-empty prefix currently mapped to the given
     * URL or null, if there is no such mapping. This method may be
     * used to find a possible prefix for an attributes namespace
     * URI. For elements you should use {@link #getPrefix(String)}.</p>
     *
     * @param pURI Thhe namespace URI in question
     * @throws IllegalArgumentException The namespace URI is null.
     */
    public String getAttributePrefix(String pURI) {
        if (pURI == null) {
            throw new IllegalArgumentException("Namespace URI must not be null");
        }
        if (pURI.length() == 0) {
            return "";
        }
        if (cachedURI != null) {
            if (cachedURI.equals(pURI)  &&  cachedPrefix.length() > 0) {
                return cachedPrefix;
            }
            if (prefixList != null) {
                for (int i = prefixList.size();  i > 0;  i -= 2) {
                    if (pURI.equals(prefixList.get(i-1))) {
                        String prefix = (String) prefixList.get(i-2);
                        if (prefix.length() > 0) {
                            return prefix;
                        }
                    }
                }
            }
        }    
        if (XMLConstants.XML_NS_URI.equals(pURI)) {
            return XMLConstants.XML_NS_PREFIX;
        } else if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(pURI)) {
            return XMLConstants.XMLNS_ATTRIBUTE;
        }
        return null;
    }
    
    
    
    /** <p>Returns a collection to all prefixes bound to the given
     * namespace URI.
     * <p><em>Note</em>: This methods behaviour is precisely
     * defined by {@link NamespaceContext#getPrefixes(java.lang.String)}.</p>
     *
     * @param pURI The namespace prefix in question
     */
    public Iterator getPrefixes(String pURI) {
        if (pURI == null) {
            throw new IllegalArgumentException("Namespace URI must not be null");
        }
        List list = new ArrayList();
        if (cachedURI != null) {
            if (cachedURI.equals(pURI)) { list.add(cachedPrefix); }
            if (prefixList != null) {
                for (int i = prefixList.size();  i > 0;  i -= 2) {
                    if (pURI.equals(prefixList.get(i-1))) {
                        list.add(prefixList.get(i-2));
                    }
                }
            }
        }
        if (pURI.equals(XMLConstants.XMLNS_ATTRIBUTE_NS_URI)) {
            list.add(XMLConstants.XMLNS_ATTRIBUTE);
        } else if (pURI.equals(XMLConstants.XML_NS_URI)) {
            list.add(XMLConstants.XML_NS_PREFIX);
        }
        return list.iterator();
    }
    
    /** <p>Returns whether a given prefix is currently declared.</p>
     */
    public boolean isPrefixDeclared(String pPrefix) {
        if (cachedURI != null) {
            if (cachedPrefix != null  &&  cachedPrefix.equals(pPrefix)) { return true; }
            if (prefixList != null) {
                for (int i = prefixList.size();  i > 0;  i -= 2) {
                    if (prefixList.get(i-2).equals(pPrefix)) {
                        return true;
                    }
                }
            }
        }
        return "xml".equals(pPrefix);
    }

	/** Returns the current number of assigned prefixes.
	 * Note, that a prefix may be assigned in several nested
	 * elements, in which case every assignment is counted.<br>
	 * This method is typically called before invoking the
	 * method
	 * {@link org.xml.sax.ContentHandler#startElement(String, String, String, org.xml.sax.Attributes)}.
	 * The return value is used as a saveable state. After
	 * invoking 
	 * {@link org.xml.sax.ContentHandler#endElement(String, String, String)},
	 * the state is restored by calling {@link #checkContext(int)}.
	 */
    public int getContext() {
        return (prefixList == null ? 0 : prefixList.size()) +
        	(cachedURI == null ? 0 : 2);
    }

	/** This method is used to restore the namespace state
	 * after an element is created. It takes as input a state,
	 * as returned by {@link #getContext()}.<br>
	 * For any prefix, which was since saving the state,
	 * the prefix is returned and deleted from the internal
	 * list. In other words, a typical use looks like this:
	 * <pre>
	 *   NamespaceSupport nss;
	 *   ContentHandler h;
	 *   int context = nss.getContext();
	 *   h.startElement("foo", "bar", "f:bar", new AttributesImpl());
	 *   ...
	 *   h.endElement("foo", "bar", "f:bar");
	 *   for (;;) {
	 *     String prefix = nss.checkContext(context);
	 *     if (prefix == null) {
	 *       break;
	 *     }
	 *     h.endPrefixMapping(prefix);
	 *   }
	 * </pre>
	 */
	public String checkContext(int i) {
        if (getContext() == i) {
            return null;
        }
        String result = cachedPrefix;
        if (prefixList != null  &&  prefixList.size() > 0) {
            cachedURI = prefixList.remove(prefixList.size()-1).toString();
            cachedPrefix = prefixList.remove(prefixList.size()-1).toString();
        } else {
            cachedURI = null;
            cachedPrefix = null;
        }
        return result;
    }
}
