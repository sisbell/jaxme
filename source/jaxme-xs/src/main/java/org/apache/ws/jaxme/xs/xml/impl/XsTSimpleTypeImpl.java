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


/** <p>Implementation of the following specification for
 * <code>xs:simpleType</code>:
 * <pre>
 *  &lt;xs:complexType name="simpleType" abstract="true"&gt;
 *    &lt;xs:complexContent&gt;
 *      &lt;xs:extension base="xs:annotated"&gt;
 *        &lt;xs:group ref="xs:simpleDerivation"/&gt;
 *        &lt;xs:attribute name="final" type="xs:simpleDerivationSet"/&gt;
 *        &lt;xs:attribute name="name" type="xs:NCName"&gt;
 *          &lt;xs:annotation&gt;
 *            &lt;xs:documentation&gt;
 *              Can be restricted to required or forbidden
 *            &lt;/xs:documentation&gt;
 *          &lt;/xs:annotation&gt;
 *        &lt;/xs:attribute&gt;
 *      &lt;/xs:extension&gt;
 *    &lt;/xs:complexContent&gt;
 *  &lt;/xs:complexType&gt;
 *
 *  &lt;xs:group name="simpleDerivation"&gt;
 *    &lt;xs:choice&gt;
 *      &lt;xs:element ref="xs:restriction"/&gt;
 *      &lt;xs:element ref="xs:list"/&gt;
 *      &lt;xs:element ref="xs:union"/&gt;
 *    &lt;/xs:choice&gt;
 *  &lt;/xs:group&gt;
 * </pre></p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XsTSimpleTypeImpl extends XsTAnnotatedImpl implements XsTSimpleType {
  private XsERestriction restriction;
  private XsEList list;
  private XsEUnion union;
  private XsSimpleDerivationSet finalSet;
  private XsNCName name;

  protected XsTSimpleTypeImpl(XsObject pParent) {
    super(pParent);
  }

  public XsERestriction createRestriction() throws SAXException {
    if (restriction != null  ||  list != null  ||  union != null) {
      throw new LocSAXException("Only one 'restriction', 'list', or 'union' child element is allowed.", getLocator());
    }
    return restriction = getObjectFactory().newXsERestriction(this);
  }

  public XsERestriction getRestriction() {
    return restriction;
  }

  public XsEList createList() throws SAXException {
    if (restriction != null  ||  list != null  ||  union != null) {
      throw new LocSAXException("Only one 'restriction', 'list', or 'union' child element is allowed.", getLocator());
    }
    return list = getObjectFactory().newXsEList(this);
  }

  public XsEList getList() {
    return list;
  }

  public XsEUnion createUnion() throws SAXException {
    if (restriction != null  ||  list != null  ||  union != null) {
      throw new LocSAXException("Only one 'restriction', 'list', or 'union' child element is allowed.", getLocator());
    }
    return union = getObjectFactory().newXsEUnion(this);
  }

  public XsEUnion getUnion() {
    return union;
  }

  public void setFinal(XsSimpleDerivationSet pSet) throws SAXException {
    finalSet = pSet;
  }

  public XsSimpleDerivationSet getFinal() {
    return finalSet;
  }

  public void setName(XsNCName pName) throws SAXException {
    name = pName;
  }

  public XsNCName getName() {
    return name;
  }

  public void validate() throws SAXException {
    super.validate();
    if (restriction == null  &&  list == null  &&  union == null) {
      throw new LocSAXException("Expected either of 'restriction', 'list' or 'union' child element.", getLocator());
    }
  }
}
