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

import org.apache.ws.jaxme.xs.xml.*;


/** <p>Implementation of the <code>xs:localElement</code> type, as
 * specified by:
 * <pre>
 *  &lt;xs:complexType name="localElement"&gt;
 *    &lt;xs:complexContent&gt;
 *      &lt;xs:restriction base="xs:element"&gt;
 *        &lt;xs:sequence&gt;
 *          &lt;xs:element ref="xs:annotation" minOccurs="0"/&gt;
 *          &lt;xs:choice minOccurs="0"&gt;
 *            &lt;xs:element name="simpleType" type="xs:localSimpleType"/&gt;
 *            &lt;xs:element name="complexType" type="xs:localComplexType"/&gt;
 *          &lt;/xs:choice&gt;
 *          &lt;xs:group ref="xs:identityConstraint" minOccurs="0" maxOccurs="unbounded"/&gt;
 *        &lt;/xs:sequence&gt;
 *        &lt;xs:attribute name="substitutionGroup" use="prohibited"/&gt;
 *        &lt;xs:attribute name="final" use="prohibited"/&gt;
 *        &lt;xs:attribute name="abstract" use="prohibited"/&gt;
 *      &lt;/xs:restriction&gt;
 *    &lt;/xs:complexContent&gt;
 *  &lt;/xs:complexType&gt;
 * </pre></p>
 * <p><em>Implementation note:</em> This class doesn't specify any
 * additional methods. It only disables the 'substitutionGroup',
 * 'final', and 'abstract' attributes.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XsTLocalElementImpl extends XsTElementImpl implements XsTLocalElement {
  protected XsTLocalElementImpl(XsObject pParent) {
    super(pParent);
  }

  public void setSubstitutionGroup(XsQName pSubstitutionGroup) {
    throw new IllegalArgumentException("A local element must not have its 'substitutionGroup' attribute set.");
  }

  public void setSubstitutionGroup(String pSubstitutionGroup) {
    throw new IllegalArgumentException("A local element must not have its 'substitutionGroup' attribute set.");
  }

  public void setFinal(XsDerivationSet pFinal) {
    throw new IllegalArgumentException("A local element must not have its 'final' attribute set.");
  }

  public void setAbstract(boolean pAbstract) {
    throw new IllegalArgumentException("A local element must not have its 'abstract' attribute set.");
  }

  public boolean isGlobal() { return false; }
}
