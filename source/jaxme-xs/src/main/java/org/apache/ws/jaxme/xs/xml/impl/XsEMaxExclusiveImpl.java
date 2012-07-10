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

import org.apache.ws.jaxme.xs.xml.XsEMaxExclusive;
import org.apache.ws.jaxme.xs.xml.XsObject;


/** <p>Implementation of <code>xs:maxExclusive</code>,
 * following this specification:
 * <pre>
 *  &lt;xs:element name="maxExclusive" id="maxExclusive" type="xs:facet"&gt;
 *    &lt;xs:annotation&gt;
 *      &lt;xs:documentation
 *        source="http://www.w3.org/TR/xmlschema-2/#element-maxExclusive"/&gt;
 *    &lt;/xs:annotation&gt;
 *  &lt;/xs:element&gt;
 * </pre></p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XsEMaxExclusiveImpl extends XsTFacetImpl implements XsEMaxExclusive {
  protected XsEMaxExclusiveImpl(XsObject pParent) {
    super(pParent);
  }

  public String getFacetName() {
    return "maxExclusive";
  }
}
