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


/** <p>Implementation of <code>xs:simpleContent</code>,
 * as specified by the following:
 * <pre>
 *  &lt;xs:element name="simpleContent" id="simpleContent"&gt;
 *    &lt;xs:annotation&gt;
 *      &lt;xs:documentation source="http://www.w3.org/TR/xmlschema-1/#element-simpleContent"/&gt;
 *    &lt;/xs:annotation&gt;
 *    &lt;xs:complexType&gt;
 *      &lt;xs:complexContent&gt;
 *        &lt;xs:extension base="xs:annotated"&gt;
 *          &lt;xs:choice&gt;
 *            &lt;xs:element name="restriction" type="xs:simpleRestrictionType"/&gt;
 *            &lt;xs:element name="extension" type="xs:simpleExtensionType"/&gt;
 *          &lt;/xs:choice&gt;
 *        &lt;/xs:extension&gt;
 *      &lt;/xs:complexContent&gt;
 *    &lt;/xs:complexType&gt;
 *  &lt;/xs:element&gt;
 * </pre></p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface XsESimpleContent extends XsTAnnotated {
  public XsTSimpleRestrictionType createRestriction();

  public XsTSimpleRestrictionType getRestriction();

  public XsTSimpleExtensionType createExtension();

  public XsTSimpleExtensionType getExtension();
}
