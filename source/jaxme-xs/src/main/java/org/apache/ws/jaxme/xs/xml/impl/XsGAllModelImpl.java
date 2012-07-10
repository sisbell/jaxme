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
import java.util.List;

import org.apache.ws.jaxme.xs.xml.*;


/** <p>Implementation of <code>xs:allModel</code>, as specified by
 * the following:
 * <pre>
 *  &lt;xs:group name="allModel"&gt;
 *    &lt;xs:sequence&gt;
 *      &lt;xs:element ref="xs:annotation" minOccurs="0"/&gt;
 *      &lt;xs:element name="element" minOccurs="0" maxOccurs="unbounded"&gt;
 *        &lt;xs:complexType&gt;
 *          &lt;xs:annotation&gt;
 *            &lt;xs:documentation&gt;restricted max/min&lt;/xs:documentation&gt;
 *          &lt;/xs:annotation&gt;
 *          &lt;xs:complexContent&gt;
 *            &lt;xs:restriction base="xs:localElement"&gt;
 *              &lt;xs:sequence&gt;
 *                &lt;xs:element ref="xs:annotation" minOccurs="0"/&gt;
 *                &lt;xs:choice minOccurs="0"&gt;
 *                  &lt;xs:element name="simpleType" type="xs:localSimpleType"/&gt;
 *                  &lt;xs:element name="complexType" type="xs:localComplexType"/&gt;
 *                &lt;/xs:choice&gt;
 *                &lt;xs:group ref="xs:identityConstraint" minOccurs="0"
 *                    maxOccurs="unbounded"/&gt;
 *              &lt;/xs:sequence&gt;
 *              &lt;xs:attribute name="minOccurs" use="optional" default="1"&gt;
 *                &lt;xs:simpleType&gt;
 *                  &lt;xs:restriction base="xs:nonNegativeInteger"&gt;
 *                    &lt;xs:enumeration value="0"/&gt;
 *                    &lt;xs:enumeration value="1"/&gt;
 *                  &lt;/xs:restriction&gt;
 *                &lt;/xs:simpleType&gt;
 *              &lt;/xs:attribute&gt;
 *              &lt;xs:attribute name="maxOccurs" use="optional" default="1"&gt;
 *                &lt;xs:simpleType&gt;
 *                  &lt;xs:restriction base="xs:allNNI"&gt;
 *                    &lt;xs:enumeration value="0"/&gt;
 *                    &lt;xs:enumeration value="1"/&gt;
 *                  &lt;/xs:restriction&gt;
 *                &lt;/xs:simpleType&gt;
 *              &lt;/xs:attribute&gt;
 *            &lt;/xs:restriction&gt;
 *          &lt;/xs:complexContent&gt;
 *        &lt;/xs:complexType&gt;
 *       &lt;/xs:element&gt;
 *    &lt;/xs:sequence&gt;
 *  &lt;/xs:group&gt;
 * </pre></p>
 * <p><em>Implementation note: The above restriction of a {@link XsTLocalElement}
 * is interpreted as a usual local element, except that the <code>minOccurs</code>
 * and <code>maxOccurs</code> attributes must be 0 or 1.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XsGAllModelImpl extends XsTAnnotatedImpl implements XsGAllModel {
  private List elements;

  protected XsGAllModelImpl(XsObject pParent) {
    super(pParent);
  }

  public XsTLocalElement createElement() {
    XsTLocalElement element = getObjectFactory().newXsTLocalAllElement(this);
    if (elements == null) {
      elements = new ArrayList();
    }
    elements.add(element);
    return element;
  }

  public XsTLocalElement[] getElements() {
    if (elements == null) {
      return new XsTLocalElement[0];
    }
    return (XsTLocalElement[]) elements.toArray(new XsTLocalElement[elements.size()]);
  }
}
