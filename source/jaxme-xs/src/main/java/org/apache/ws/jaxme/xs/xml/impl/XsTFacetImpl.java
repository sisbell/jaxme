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

import org.apache.ws.jaxme.xs.parser.impl.LocSAXException;
import org.apache.ws.jaxme.xs.xml.XsObject;
import org.xml.sax.SAXException;


/** <p>Implementation of a facet, following this specification:
 * <pre>
 *  &lt;xs:complexType name="facet"&gt;
 *    &lt;xs:complexContent&gt;
 *      &lt;xs:extension base="xs:annotated"&gt;
 *        &lt;xs:attribute name="value" use="required"/&gt;
 *        &lt;xs:attribute name="fixed" type="xs:boolean" use="optional"
 *                    default="false"/&gt;
 *      &lt;/xs:extension&gt;
 *    &lt;/xs:complexContent&gt;
 *  &lt;/xs:complexType&gt;
 * </pre></p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public abstract class XsTFacetImpl extends XsTFixedFacetImpl {
  private String value;

  protected XsTFacetImpl(XsObject pParent) {
    super(pParent);
  }

  public void setValue(String pValue) {
    value = pValue;
  }

  public String getValue() {
    return value;
  }

  public void validate() throws SAXException {
    super.validate();
    if (value == null) {
      throw new LocSAXException("Missing 'value' attribute", getLocator());
    }
  }
}
