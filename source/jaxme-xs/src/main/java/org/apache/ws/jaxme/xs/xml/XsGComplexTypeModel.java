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

/** <p>Interface of the group <code>xs:complexTypeModel</code>,
 * as specified by the following:
 * <pre>
 *  &lt;xs:group name="complexTypeModel"&gt;
 *    &lt;xs:choice&gt;
 *      &lt;xs:element ref="xs:simpleContent"/&gt;
 *      &lt;xs:element ref="xs:complexContent"/&gt;
 *      &lt;xs:sequence&gt;
 *        &lt;xs:annotation&gt;
 *          &lt;xs:documentation&gt;
 *            This branch is short for &amp;lt;complexContent&amp;gt;
 *            &amp;lt;restriction base="xs:anyType"&amp;gt;
 *            ...
 *            &amp;lt;/restriction&amp;gt;
 *            &amp;lt;/complexContent&amp;gt;
 *          &lt;/xs:documentation&gt;
 *        &lt;/xs:annotation&gt;
 *        &lt;xs:group ref="xs:typeDefParticle" minOccurs="0"/&gt;
 *        &lt;xs:group ref="xs:attrDecls"/&gt;
 *      &lt;/xs:sequence&gt;
 *    &lt;/xs:choice&gt;
 *  &lt;/xs:group&gt;
 * </pre></p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface XsGComplexTypeModel extends XsGTypeDefParticle, XsGAttrDecls {
  public XsESimpleContent createSimpleContent();
  public XsESimpleContent getSimpleContent();

  public XsEComplexContent createComplexContent();
  public XsEComplexContent getComplexContent();
}
