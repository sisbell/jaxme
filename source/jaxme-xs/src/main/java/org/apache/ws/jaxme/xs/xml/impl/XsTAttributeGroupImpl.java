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
import org.xml.sax.SAXException;


/** <p>Implementation of an <code>xs:attributeGroup</code>,
 * with the following specification:
 * <pre>
 *  &lt;xs:complexType name="attributeGroup" abstract="true"&gt;
 *    &lt;xs:complexContent&gt;
 *      &lt;xs:extension base="xs:annotated"&gt;
 *        &lt;xs:group ref="xs:attrDecls"/&gt;
 *        &lt;xs:attributeGroup ref="xs:defRef"/&gt;
 *      &lt;/xs:extension&gt;
 *    &lt;/xs:complexContent&gt;
 *  &lt;/xs:complexType&gt;
 * </pre></p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XsTAttributeGroupImpl extends XsTAnnotatedImpl implements XsTAttributeGroup {
  private final XsGAttrDecls decls;
  private final XsAGDefRef defRef;

  protected XsTAttributeGroupImpl(XsObject pParent) {
    super(pParent);
    decls = getObjectFactory().newXsGAttrDecls(this);
    defRef = getObjectFactory().newXsAGDefRef(this);
  }

  public void setName(XsNCName pName) {
    defRef.setName(pName);
  }

  public XsNCName getName() {
    return defRef.getName();
  }

  public void setRef(XsQName pRef) {
    defRef.setRef(pRef);
  }

  public void setRef(String pRef) throws SAXException {
    setRef(asXsQName(pRef));
  }

  public XsQName getRef() {
    return defRef.getRef();
  }

  public XsTAttribute createAttribute() {
    return decls.createAttribute();
  }

  public XsTAttribute[] getAttributes() {
    return decls.getAttributes();
  }

  public XsTAttributeGroupRef createAttributeGroup() {
    return decls.createAttributeGroup();
  }

  public XsTAttributeGroupRef[] getAttributeGroups() {
    return decls.getAttributeGroups();
  }

  public XsTWildcard createAnyAttribute() {
    return decls.createAnyAttribute();
  }

  public XsTWildcard getAnyAttribute() {
    return decls.getAnyAttribute();
  }

  public Object[] getAllAttributes() {
    return decls.getAllAttributes();
  }

  public void validate() throws SAXException {
    super.validate();
    defRef.validate();
  }
}
