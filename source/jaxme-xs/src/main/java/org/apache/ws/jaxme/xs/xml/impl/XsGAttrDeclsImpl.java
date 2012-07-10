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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.ws.jaxme.xs.xml.*;


/** <p>Implementation of <code>xs:attrDecls</code>, with the
 * following specification:
 * <pre>
 *  &lt;xs:group name="attrDecls"&gt;
 *    &lt;xs:sequence&gt;
 *      &lt;xs:choice minOccurs="0" maxOccurs="unbounded"&gt;
 *        &lt;xs:element name="attribute" type="xs:attribute"/&gt;
 *        &lt;xs:element name="attributeGroup" type="xs:attributeGroupRef"/&gt;
 *      &lt;/xs:choice&gt;
 *      &lt;xs:element ref="xs:anyAttribute" minOccurs="0"/&gt;
 *    &lt;/xs:sequence&gt;
 *  &lt;/xs:group&gt;
 * </pre></p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XsGAttrDeclsImpl implements XsGAttrDecls {
  private final XsObject owner;
  private List childs;
  private XsTWildcard anyAttribute;

  protected XsGAttrDeclsImpl(XsObject pOwner) {
    owner = pOwner;
  }

  protected void addChild(Object o) {
    if (o instanceof XsTWildcard) {
      if (anyAttribute != null) {
        throw new IllegalStateException("Multiple 'anyAttribute' child elements are forbidden.");
      }
      anyAttribute = (XsTWildcard) o;
    } else {
      if (anyAttribute != null) {
        throw new IllegalStateException("An 'attribute' or 'attributeGroup' child element is invalid after an 'anyAttribute' child element.");
      }
    }
    if (childs == null) {
      childs = new ArrayList();
    }
    childs.add(o);
  }

  public XsTAttribute createAttribute() {
    XsTAttribute attribute = owner.getObjectFactory().newXsTAttribute(owner);
    addChild(attribute);
    return attribute;
  }

  public XsTAttribute[] getAttributes() {
    if (childs == null  ||  childs.size() == 0) {
      return new XsTAttribute[0];
    }
    List result = new ArrayList();
    for (Iterator iter = childs.iterator();  iter.hasNext();  ) {
      Object o = iter.next();
      if (o instanceof XsTAttribute) {
        result.add(o);
      }
    }
    return (XsTAttribute[]) result.toArray(new XsTAttribute[result.size()]);
  }

  public XsTAttributeGroupRef createAttributeGroup() {
    XsTAttributeGroupRef attributeGroupRef = owner.getObjectFactory().newXsTAttributeGroupRef(owner);
    addChild(attributeGroupRef);
    return attributeGroupRef;
  }

  public XsTAttributeGroupRef[] getAttributeGroups() {
    if (childs == null  ||  childs.size() == 0) {
      return new XsTAttributeGroupRefImpl[0];
    }
    List result = new ArrayList();
    for (Iterator iter = childs.iterator();  iter.hasNext();  ) {
      Object o = iter.next();
      if (o instanceof XsTAttributeGroupRef) {
        result.add(o);
      }
    }
    return (XsTAttributeGroupRef[]) result.toArray(new XsTAttributeGroupRef[result.size()]);
  }

  public XsTWildcard createAnyAttribute() {
    XsTWildcard myAnyAttribute = owner.getObjectFactory().newXsTWildcard(owner);
    addChild(myAnyAttribute);
    return myAnyAttribute;
  }

  public XsTWildcard getAnyAttribute() {
    return anyAttribute;
  }

  public Object[] getAllAttributes() {
    if (childs == null) {
      return new Object[0];
    } else {
      return childs.toArray();
    }
  }
}
