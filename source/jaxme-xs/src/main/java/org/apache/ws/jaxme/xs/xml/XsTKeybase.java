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
package org.apache.ws.jaxme.xs.xml;

/** <p>Interface of the <code>xs:keybase</code> type, with the
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
public interface XsTKeybase extends XsTAnnotated {
  public XsESelector createSelector();
  public XsESelector getSelector();

  public XsEField createField();
  public XsEField[] getFields();

  public void setName(XsNCName pName);
  public XsNCName getName();
}
