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


/** <p>Implementation of the <code>xs:keybase</code> type, with the
 * following specification:
 * <pre>
 *  &lt;xs:complexType name="keybase"&gt;
 *    &lt;xs:complexContent&gt;
 *      &lt;xs:extension base="xs:annotated"&gt;
 *        &lt;xs:sequence&gt;
 *          &lt;xs:element ref="xs:selector"/&gt;
 *          &lt;xs:element ref="xs:field" minOccurs="1" maxOccurs="unbounded"/&gt;
 *        &lt;/xs:sequence&gt;
 *        &lt;xs:attribute name="name" type="xs:NCName" use="required"/&gt;
 *      &lt;/xs:extension&gt;
 *    &lt;/xs:complexContent&gt;
 *  &lt;/xs:complexType&gt;
 * </pre></p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XsTKeybaseImpl extends XsTAnnotatedImpl implements XsTKeybase {
  private XsESelector selector;
  private List fields = new ArrayList();
  private XsNCName name;

  protected XsTKeybaseImpl(XsObject pParent) {
    super(pParent);
  }

  public XsESelector createSelector() {
    if (selector != null) {
      throw new IllegalStateException("Multiple 'selector' child elements are forbidden.");
    }
    if (fields.size() > 0) {
      throw new IllegalStateException("The 'selector' child element must precede the 'field' child elements.");
    }
    selector = getObjectFactory().newXsESelector(this);
    return selector;
  }

  public XsESelector getSelector() {
    return selector;
  }

  public XsEField createField() {
    if (selector == null) {
      throw new NullPointerException("This 'field' child element must be preceded by a 'selector' child element.");
    }
    XsEField field = getObjectFactory().newXsEField(this);
    fields.add(field);
    return field;
  }

  public XsEField[] getFields() {
    return (XsEField[]) fields.toArray(new XsEField[fields.size()]);
  }

  public void setName(XsNCName pName) {
    name = pName;
  }

  public XsNCName getName() {
    return name;
  }

  public void validate() {
    if (name == null) {
      throw new NullPointerException("Missing attribute: 'name'");
    }
    if (selector == null) {
      throw new NullPointerException("Missing child element: 'selector'");
    }
    if (fields.size() == 0) {
      throw new NullPointerException("Missing child element: 'field'");
    }
  }
}
