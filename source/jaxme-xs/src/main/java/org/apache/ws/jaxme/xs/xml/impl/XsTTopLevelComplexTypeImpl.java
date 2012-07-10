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


/** <p>Implementation of a top level <code>xs:complexType</code>,
 * following the specification below:
 * <pre>
 *  &lt;xs:complexType name="topLevelComplexType"&gt;
 *    &lt;xs:complexContent&gt;
 *      &lt;xs:restriction base="xs:complexType"&gt;
 *        &lt;xs:sequence&gt;
 *          &lt;xs:element ref="xs:annotation" minOccurs="0"/&gt;
 *          &lt;xs:group ref="xs:complexTypeModel"/&gt;
 *        &lt;/xs:sequence&gt;
 *        &lt;xs:attribute name="name" type="xs:NCName" use="required"/&gt;
 *      &lt;/xs:restriction&gt;
 *    &lt;/xs:complexContent&gt;
 *  &lt;/xs:complexType&gt;
 * </pre></p>
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XsTTopLevelComplexTypeImpl extends XsTComplexTypeImpl implements XsTTopLevelComplexType {
  private XsNCName name;
  private boolean isMixed, isAbstract;
  private XsDerivationSet finalSet;
  private XsDerivationSet blockSet;

  protected XsTTopLevelComplexTypeImpl(XsObject pParent) {
    super(pParent);
  }

  public void setName(XsNCName pName) {
    name = pName;
  }

  public XsNCName getName() {
    return name;
  }

  public void setMixed(boolean pMixed) {
    isMixed = pMixed;
  }

  public boolean isMixed() {
    return isMixed;
  }

  public void setAbstract(boolean pAbstract) {
    isAbstract = pAbstract;
  }

  public boolean isAbstract() {
    return isAbstract;
  }

  public void setFinal(XsDerivationSet pFinal) {
    finalSet = pFinal;
  }

  public XsDerivationSet getFinal() {
    return finalSet;
  }

  public void setBlock(XsDerivationSet pBlock) {
    blockSet = pBlock;
  }

  public XsDerivationSet getBlock() {
    return blockSet;
  }

  public void validate() throws SAXException {
    super.validate();
    if (name == null) {
      throw new LocSAXException("Missing attribute: 'name'", getLocator());
    }
  }
}
