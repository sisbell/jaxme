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
import org.apache.ws.jaxme.xs.xml.*;
import org.xml.sax.SAXException;


/** <p>Implementation of <code>xs:list</code>, following
 * the specification below:
 * <pre>
 *  &lt;xs:element name="list" id="list"&gt;
 *    &lt;xs:complexType&gt;
 *      &lt;xs:annotation&gt;
 *        &lt;xs:documentation
 *            source="http://www.w3.org/TR/xmlschema-2/#element-list"&gt;
 *          itemType attribute and simpleType child are mutually
 *          exclusive, but one or other is required
 *        &lt;/xs:documentation&gt;
 *      &lt;/xs:annotation&gt;
 *      &lt;xs:complexContent&gt;
 *        &lt;xs:extension base="xs:annotated"&gt;
 *          &lt;xs:sequence&gt;
 *            &lt;xs:element name="simpleType" type="xs:localSimpleType"
 *                minOccurs="0"/&gt;
 *          &lt;/xs:sequence&gt;
 *          &lt;xs:attribute name="itemType" type="xs:QName" use="optional"/&gt;
 *        &lt;/xs:extension&gt;
 *      &lt;/xs:complexContent&gt;
 *    &lt;/xs:complexType&gt;
 *  &lt;/xs:element&gt;
 * </pre></p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XsEListImpl extends XsTAnnotatedImpl implements XsEList {
  private XsQName itemType;
  private XsTLocalSimpleType simpleType;

  protected XsEListImpl(XsObject pParent) {
    super(pParent);
  }

  public void setItemType(XsQName pItemType) {
    if (simpleType != null) {
      throw new IllegalStateException("The 'itemType' attribute and the 'simpleType' child element are mutually exclusive.");
    }
    itemType = pItemType;
  }

  public void setItemType(String pItemType) throws SAXException {
    setItemType(asXsQName(pItemType));
  }

  public XsQName getItemType() {
    return itemType;
  }

  public XsTLocalSimpleType createSimpleType() {
    if (itemType != null) {
      throw new IllegalStateException("The 'itemType' attribute and the 'simpleType' child element are mutually exclusive.");
    }
    if (simpleType != null) {
      throw new IllegalStateException("Multiple 'simpleType' child elements are forbidden.");
    }
    return simpleType = getObjectFactory().newXsTLocalSimpleType(this);
  }

  public XsTLocalSimpleType getSimpleType() {
    return simpleType;
  }

  public void validate() throws SAXException {
    super.validate();
    if (itemType == null  &&  simpleType == null) {
      throw new LocSAXException("Either the 'itemType' attribute must be set or a 'simpleType' child element must be present.",
                                 getLocator());
    }
  }
}
