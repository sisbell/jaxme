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

import org.xml.sax.SAXException;

/** <p>Interface of the attribute group <code>xs:defRef</code>,
 * as specified by the following:
 * <pre>
 *  &lt;xs:attributeGroup name="defRef"&gt;
 *    &lt;xs:annotation&gt;
 *      &lt;xs:documentation&gt;
 *        for element, group and attributeGroup,
 *        which both define and reference
 *      &lt;/xs:documentation&gt;
 *    &lt;/xs:annotation&gt;
 *    &lt;xs:attribute name="name" type="xs:NCName"/&gt;
 *    &lt;xs:attribute name="ref" type="xs:QName"/&gt;
 *  &lt;/xs:attributeGroup&gt;
 * </pre></p>
 * <p><em>Implementation note:</em> The 'name' and 'ref' attributes
 * are mutually exclusive. This is checked by the 'validate' method.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface XsAGDefRef {
  public void setName(XsNCName pName);
  public XsNCName getName();
  public void setRef(XsQName pRef);
  public XsQName getRef();

  /** <p>Validates whether the attribute groups constraints are met.</p>
   */
  public void validate() throws SAXException;
}
