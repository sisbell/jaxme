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

/** <p>Interface of <code>xs:appinfo</code>, as specified by the
 * following:
 * <pre>
 *  &lt;xs:element name="appinfo" id="appinfo"&gt;
 *    &lt;xs:annotation&gt;
 *      &lt;xs:documentation source="http://www.w3.org/TR/xmlschema-1/#element-appinfo"/&gt;
 *    &lt;/xs:annotation&gt;
 *    &lt;xs:complexType mixed="true"&gt;
 *      &lt;xs:sequence minOccurs="0" maxOccurs="unbounded"&gt;
 *        &lt;xs:any processContents="lax"/&gt;
 *      &lt;/xs:sequence&gt;
 *      &lt;xs:attribute name="source" type="xs:anyURI"/&gt;
 *    &lt;/xs:complexType&gt;
 *  &lt;/xs:element&gt;
 * </pre></p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface XsEAppinfo extends XsObject {
  public void setSource(XsAnyURI pSource);
  public XsAnyURI getSource();

  public Object[] getChilds();
}
