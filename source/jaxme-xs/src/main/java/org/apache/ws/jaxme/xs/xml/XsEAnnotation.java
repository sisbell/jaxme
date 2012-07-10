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


/** <p>Interface of <code>xs:annotation</code>, as specified
 * by the following:
 * <pre>
 *  &lt;xs:element name="annotation" id="annotation"&gt;
 *    &lt;xs:annotation&gt;
 *      &lt;xs:documentation source="http://www.w3.org/TR/xmlschema-1/#element-annotation"/&gt;
 *    &lt;/xs:annotation&gt;
 *    &lt;xs:complexType&gt;
 *      &lt;xs:complexContent&gt;
 *        &lt;xs:extension base="xs:openAttrs"&gt;
 *          &lt;xs:choice minOccurs="0" maxOccurs="unbounded"&gt;
 *            &lt;xs:element ref="xs:appinfo"/&gt;
 *            &lt;xs:element ref="xs:documentation"/&gt;
 *          &lt;/xs:choice&gt;
 *          &lt;xs:attribute name="id" type="xs:ID"/&gt;
 *        &lt;/xs:extension&gt;
 *      &lt;/xs:complexContent&gt;
 *    &lt;/xs:complexType&gt;
 *  &lt;/xs:element&gt;
 * </pre></p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface XsEAnnotation extends XsTOpenAttrs {
  public void setId(XsID pId);
  public XsID getId();

  /** <p>Creates a new 'appinfo' child element.</p>
   */
  public XsEAppinfo createAppinfo();
  /** <p>Returns all the 'appinfo' child elements in the
   * order of creation by {@link #createAppinfo()}.</p>
   */
  public XsEAppinfo[] getAppinfos();

  /** <p>Creates a new 'documentation' child element.</p>
   */
  public XsEDocumentation createDocumentation();
  /** <p>Returns all the 'documentation' child elements in
   * the order of creation by {@link #createDocumentation()}.</p>
   */
  public XsEDocumentation[] getDocumentations();

  /** <p>Returns all the annotations child elements in the
   * order of creation.</p>
   */
  public Object[] getChilds();
}
