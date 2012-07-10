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

/** <p>Interface of the attribute group <code>xs:occurs</code>,
 * as specified by the following:
 * <pre>
 *  &lt;xs:attributeGroup name="occurs"&gt;
 *    &lt;xs:annotation&gt;
 *      &lt;xs:documentation&gt;
 *        for all particles
 *      &lt;/xs:documentation&gt;
 *    &lt;/xs:annotation&gt;
 *    &lt;xs:attribute name="minOccurs" type="xs:nonNegativeInteger"
 *      use="optional" default="1"/&gt;
 *    &lt;xs:attribute name="maxOccurs" type="xs:allNNI"
 *      use="optional" default="1"/&gt;
 *  &lt;/xs:attributeGroup&gt;
 * </pre></p>
 * <p><em>Implementation note:</em> The implementation must ensure
 * that either 'maxOccurs' is unbounded or 'minOccurs' <= 'maxOccurs'.
 * This is checked by the <code>validate</code> method.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface XsAGOccurs {
  /** <p>Either of 'unbounded' or a nonnegative integer value.</p>
   */
  public void setMaxOccurs(String pMaxOccurs);
  /** <p>-1 for 'unbounded'</p>
   */
  public int getMaxOccurs();

  public void setMinOccurs(int pMinOccurs);
  public int getMinOccurs();

  /** <p>Verifies whether the attribute group constraints are met.</p>
   */
  public void validate() throws SAXException;
}
