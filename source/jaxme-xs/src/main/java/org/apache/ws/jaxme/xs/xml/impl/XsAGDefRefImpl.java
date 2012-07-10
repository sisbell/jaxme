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
package org.apache.ws.jaxme.xs.xml.impl;

import org.apache.ws.jaxme.xs.parser.impl.LocSAXException;
import org.apache.ws.jaxme.xs.xml.XsAGDefRef;
import org.apache.ws.jaxme.xs.xml.XsNCName;
import org.apache.ws.jaxme.xs.xml.XsObject;
import org.apache.ws.jaxme.xs.xml.XsQName;
import org.xml.sax.SAXException;


/** <p>Implementation of the attribute group <code>xs:defRef</code>,
 * as specified by the following:
 * <pre>
 *  &lt;xs:attributeGroup name="defRef"&gt;
 *    &lt;xs:annotation&gt;
 *      &lt;xs:documentation&gt;
 *        for element, group and attributeGroup,
 *        which both define and reference
 *      &lt;/xs:documentation&gt;
 *    &lt;/xs:annotation&gt;
 *    &lt;xs:attribute name="name" type="xs:NCName"/&gt;
 *    &lt;xs:attribute name="ref" type="xs:QName"/&gt;
 *  &lt;/xs:attributeGroup&gt;
 * </pre></p>
 * <p><em>Implementation note:</em> The 'name' and 'ref' attributes
 * are mutually exclusive.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XsAGDefRefImpl implements XsAGDefRef {
    private final XsObject owner;
    private XsNCName name;
    private XsQName ref;

  	/** <p>Creates a new instance with the given parent object.</p>
  	 */
  	public XsAGDefRefImpl(XsObject pOwner) {
  	    owner = pOwner;
  	}

  	public void setName(XsNCName pName) {
  	    if (ref != null) {
  	        throw new IllegalStateException("The 'name' and 'ref' attributes are mutually exclusive.");
  	    }
  	    name = pName;
  	}
  	
  	public XsNCName getName() {
  	    return name;
  	}
  	
  	public void setRef(XsQName pRef) {
  	    if (name != null) {
  	        throw new IllegalStateException("The 'name' and 'ref' attributes are mutually exclusive.");
  	    }
  	    ref = pRef;
  	}

  	public void setRef(String pRef) throws SAXException {
  	    setRef(((XsObjectImpl) owner).asXsQName(pRef));
  	}
  	
  	public XsQName getRef() {
  	    return ref;
  	}
  	
  	public void validate() throws SAXException {
  	    if (name == null  &&  ref == null) {
  	        throw new LocSAXException("You must set either of the attributes 'name' or 'ref'.", owner.getLocator());
  	    }
  	}
}
