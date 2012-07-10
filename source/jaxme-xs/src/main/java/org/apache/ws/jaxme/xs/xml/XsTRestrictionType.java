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


/** <p>Implementation of <code>xs:restrictionType</code>,
 * as specified by the following:
 * <pre>
 *  &lt;xs:complexType name="restrictionType"&gt;
 *    &lt;xs:complexContent&gt;
 *      &lt;xs:extension base="xs:annotated"&gt;
 *        &lt;xs:sequence&gt;
 *          &lt;xs:choice&gt;
 *            &lt;xs:group ref="xs:typeDefParticle" minOccurs="0"/&gt;
 *            &lt;xs:group ref="xs:simpleRestrictionModel" minOccurs="0"/&gt;
 *          &lt;/xs:choice&gt;
 *          &lt;xs:group ref="xs:attrDecls"/&gt;
 *        &lt;/xs:sequence&gt;
 *        &lt;xs:attribute name="base" type="xs:QName" use="required"/&gt;
 *      &lt;/xs:extension&gt;
 *    &lt;/xs:complexContent&gt;
 *  &lt;/xs:complexType&gt;
 * </pre></p>
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface XsTRestrictionType extends XsTAnnotated, XsGTypeDefParticle, XsGSimpleRestrictionModel, XsGAttrDecls {
  public void setBase(XsQName pBase);
  public XsQName getBase();
}
