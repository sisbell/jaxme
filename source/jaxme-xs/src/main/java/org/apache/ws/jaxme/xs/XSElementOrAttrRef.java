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
package org.apache.ws.jaxme.xs;

/** 
 * Specifies an element or attribute relative to the declaring element. The
 * reference cannot be to an element and a attribute, one of the two getters
 * must return null.
 *
 * @see XSElement
 * @see XSIdentityConstraint
 *
 * @author <a href="mailto:mrck1996@yahoo.co.uk">Chris Kirk</a>
 */
public final class XSElementOrAttrRef {
    private final XSElement _element;
    private final XSAttribute _attribute;
    
    public XSElementOrAttrRef( XSElement element ) {
        _element = element;
        _attribute = null;
    }
    
    public XSElementOrAttrRef( XSAttribute attribute ) {
        _element = null;
        _attribute = attribute;
    }
    
    /**
     * Fetches the element that this reference refers to. Returns null when
     * isAttributeRef is true.
     */
    public XSElement getElement() {
        return _element;
    }
    
    /**
     * Fetches the attribute that this reference refers to. Returns null when
     * isAttributeRef is false.
     */
    public XSAttribute getAttribute() {
        return _attribute;
    }
    
    /**
     * Returns true if this reference points at an attribute. Returns false
     * when it references an element.
     */
    public boolean isAttributeRef() {
        return _element == null;
    }
    
    public int hashCode() {
        Object o;
        
        if ( _element == null) {
            o = _attribute;
        } else {
            o = _element;
        }
        
        return o.hashCode();
    }
    
    public boolean equals( Object o ) {
        if ( o == null  || !(o instanceof XSElementOrAttrRef)) {
            return false;
        }
        
        XSElementOrAttrRef other = (XSElementOrAttrRef) o;
        if ( this.isAttributeRef() ) {
            return this._attribute.equals( other._attribute );
        } else {
            return this._element.equals( other._element );
        }
    }
}
