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
import java.util.StringTokenizer;

import org.apache.ws.jaxme.xs.xml.*;
import org.xml.sax.SAXException;


/** <p>Implementation of <code>xs:union</code>, following the
 * specification below:
 * <pre>
 *  &lt;xs:element name="union" id="union"&gt;
 *    &lt;xs:complexType&gt;
 *      &lt;xs:annotation&gt;
 *        &lt;xs:documentation
 *            source="http://www.w3.org/TR/xmlschema-2/#element-union"&gt;
 *          memberTypes attribute must be non-empty or there must be
 *          at least one simpleType child
 *        &lt;/xs:documentation&gt;
 *      &lt;/xs:annotation&gt;
 *      &lt;xs:complexContent&gt;
 *        &lt;xs:extension base="xs:annotated"&gt;
 *          &lt;xs:sequence&gt;
 *            &lt;xs:element name="simpleType" type="xs:localSimpleType"
 *                minOccurs="0" maxOccurs="unbounded"/&gt;
 *          &lt;/xs:sequence&gt;
 *          &lt;xs:attribute name="memberTypes" use="optional"&gt;
 *            &lt;xs:simpleType&gt;
 *              &lt;xs:list itemType="xs:QName"/&gt;
 *            &lt;/xs:simpleType&gt;
 *          &lt;/xs:attribute&gt;
 *        &lt;/xs:extension&gt;
 *      &lt;/xs:complexContent&gt;
 *    &lt;/xs:complexType&gt;
 *  &lt;/xs:element&gt;
 * </pre></p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XsEUnionImpl extends XsTAnnotatedImpl implements XsEUnion {
  public List simpleTypes;
  public List memberTypes;

  protected XsEUnionImpl(XsObject pParent) {
    super(pParent);
  }

  public XsTLocalSimpleType createSimpleType() {
    XsTLocalSimpleType simpleType = getObjectFactory().newXsTLocalSimpleType(this);
    if (simpleTypes == null) {
      simpleTypes = new ArrayList();
    }
    simpleTypes.add(simpleType);
    return simpleType;
  }

  public XsTLocalSimpleType[] getSimpleTypes() {
    if (simpleTypes == null) {
      return new XsTLocalSimpleType[0];
    }
    return (XsTLocalSimpleType[]) simpleTypes.toArray(new XsTLocalSimpleType[simpleTypes.size()]);
  }

  public void setMemberTypes(XsQName[] pTypes) {
    if (pTypes == null) {
      memberTypes = null;
    } else {
      memberTypes = new ArrayList();
      for (int i = 0;  i < pTypes.length;  i++) {
        memberTypes.add(pTypes[i]);
      }
    }
  }

  public void setMemberTypes(String pTypes) throws SAXException {
    if (pTypes == null) {
      setMemberTypes((XsQName[]) null);
    } else {
      List myMemberTypes = new ArrayList();
      for (StringTokenizer st = new StringTokenizer(pTypes);  st.hasMoreTokens();  ) {
        String s = st.nextToken();
        XsQName qName = asXsQName(s);
        myMemberTypes.add(qName);
      }
      setMemberTypes((XsQName[]) myMemberTypes.toArray(new XsQName[myMemberTypes.size()]));
    }
  }

  public XsQName[] getMemberTypes() {
    if (memberTypes == null) {
      return null;
    }
    return (XsQName[]) memberTypes.toArray(new XsQName[memberTypes.size()]);
  }
}
