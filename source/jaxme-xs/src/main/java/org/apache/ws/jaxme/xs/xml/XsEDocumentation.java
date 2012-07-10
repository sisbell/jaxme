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


/** <p>Interface of <code>xs:documentation</code>, as specified
 * by the following:
 * <pre>
 *  &lt;xs:element name="documentation" id="documentation"&gt;
 *    &lt;xs:annotation&gt;
 *      &lt;xs:documentation source="http://www.w3.org/TR/xmlschema-1/#element-documentation"/&gt;
 *    &lt;/xs:annotation&gt;
 *    &lt;xs:complexType mixed="true"&gt;
 *      &lt;xs:sequence minOccurs="0" maxOccurs="unbounded"&gt;
 *        &lt;xs:any processContents="lax"/&gt;
 *      &lt;/xs:sequence&gt;
 *      &lt;xs:attribute name="source" type="xs:anyURI"/&gt;
 *      &lt;xs:attribute ref="xml:lang"/&gt;
 *    &lt;/xs:complexType&gt;
 *  &lt;/xs:element&gt;
 * </pre></p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface XsEDocumentation extends XsObject {
	/** <p>Sets the documentation elements language. Defaults to
     * null.</p>
	 * @param pLang The language abbreviation, for example "en".
	 */
    public void setLang(XmlLang pLang);

    /** <p>Returns the documentation elements language. Defaults to
     * null.</p>
     * @return The language abbreviation, for example "en".
     */
    public XmlLang getLang();

    /** <p>Sets the URI to read for the documentations contents.
     * Defaults to null.</p>
     * @param pSource Source URI
     */
    public void setSource(XsAnyURI pSource);

    /** <p>Returns the URI to read for the documentations contents.
     * Defaults to null.</p>
     * @return Source URI
     */
	public XsAnyURI getSource();
	
	/** <p>Returns the embedded text. The embedded text is specified as the
	 * concatenation of all text nodes. Child elements, if any, are ignored.</p>
	 */
	public String getText();
	
	/** <p>Returns whether the documentation is simple. In other words, whether
	 * it consists of text only. If not, you should use {@link #getChilds()}
	 * rather than {@link #getText()}.</p>
	 */
	public boolean isTextOnly();
	
	/** <p>Returns the array of all child objects. Child objects may either
	 * be Strings or DOM nodes.</p>
	 */
	public Object[] getChilds();
}
