/*
 * Copyright 2004  The Apache Software Foundation
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
package org.apache.ws.jaxme;

import javax.xml.namespace.QName;

/** <p>Wildcard attributes (as specified by <code>xs:anyAttribute</code>)
 * are stored in a set, the set elements being instances of
 * <code>WildcardAttribute</code>.</p>
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class WildcardAttribute {
    private final QName name;
    private final String value;

    /** <p>Creates a new instance of <code>WildcardAttribute</code>
     * with the given name and value.</p>
     * @throws NullPointerException Either of the arguments is null.
     */
    public WildcardAttribute(QName pName, String pValue) {
        if (pName == null) {
            throw new NullPointerException("A wildcard attributes name must not be null.");
        }
        if (pValue == null) {
            throw new NullPointerException("A wildcard attributes value must not be null.");
        }
        name = pName;
        value = pValue;
    }
    
    /** <p>Returns the attributes name.</p>
     */
    public QName getName() {
        return name;
    }

    /** <p>Returns the attributes value.</p>
     */
    public String getValue() {
        return value;
    }

    public String toString() {
        return name + "=" + value;
    }

    /** <p>Returns <code>getName().hashCode()</code>.</p>
     */
    public int hashCode() {
        return name.hashCode();
    }

    /** <p>Returns true, if the object <code>pOther</code> is an instance of
     * <code>WildcardAttribute</code> and <code>pOther.getName().equals(getName())</code>.</p>
     */
    public boolean equals(Object pOther) {
        return pOther != null  &&  pOther instanceof WildcardAttribute  &&
        	((WildcardAttribute) pOther).name.equals(name);
    }
}
