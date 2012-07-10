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


/** <p>Implementation of the <code>xs:localSimpleType</code> type,
 * as specified by the following:
 * <pre>
 *  &lt;xs:complexType name="localSimpleType"&gt;
 *    &lt;xs:complexContent&gt;
 *      &lt;xs:restriction base="xs:simpleType"&gt;
 *        &lt;xs:sequence&gt;
 *          &lt;xs:element ref="xs:annotation" minOccurs="0"/&gt;
 *          &lt;xs:group ref="xs:simpleDerivation"/&gt;
 *        &lt;/xs:sequence&gt;
 *        &lt;xs:attribute name="name" use="prohibited"&gt;
 *          &lt;xs:annotation&gt;
 *            &lt;xs:documentation&gt;
 *              Forbidden when nested
 *            &lt;/xs:documentation&gt;
 *          &lt;/xs:annotation&gt;
 *        &lt;/xs:attribute&gt;
 *        &lt;xs:attribute name="final" use="prohibited"/&gt;
 *      &lt;/xs:restriction&gt;
 *    &lt;/xs:complexContent&gt;
 *  &lt;/xs:complexType&gt;
 * </pre></p>
 * <p><em>Implementation note:</em> The implementation must ensure,
 * that the 'final' and 'name' attributes aren't set.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XsTLocalSimpleTypeImpl extends XsTSimpleTypeImpl implements XsTLocalSimpleType {
  protected XsTLocalSimpleTypeImpl(XsObject pParent) {
    super(pParent);
  }

  public void setName(XsNCName pName) throws SAXException {
    throw new LocSAXException("A local simpleType must not have its 'name' attribute set.", getLocator());
  }

  public void setFinal(XsSimpleDerivationSet pFinal) throws SAXException {
    throw new LocSAXException("A local simpleType must not have its 'final' attribute set.", getLocator());
  }
}
