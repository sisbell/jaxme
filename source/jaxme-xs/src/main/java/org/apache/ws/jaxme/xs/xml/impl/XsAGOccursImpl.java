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
import org.apache.ws.jaxme.xs.xml.XsAGOccurs;
import org.apache.ws.jaxme.xs.xml.XsObject;
import org.xml.sax.SAXException;


/** <p>Implementation of the attribute group <code>xs:occurs</code>,
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
 * that either 'maxOccurs' is unbounded or 'minOccurs' <= 'maxOccurs'.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XsAGOccursImpl implements XsAGOccurs {
  private final XsObject owner;
  private int minOccurs = 1, maxOccurs = 1;

  public XsAGOccursImpl(XsObject pOwner) {
    owner = pOwner;
  }

  public void setMaxOccurs(String pMaxOccurs) {
    if ("unbounded".equals(pMaxOccurs)) {
      maxOccurs = -1;
    } else {
      try {
        maxOccurs = Integer.parseInt(pMaxOccurs);
        if (maxOccurs < 0) {
          throw new IllegalArgumentException();
        }
      } catch (Exception e) {
        throw new IllegalArgumentException("Invalid value for maxOccurs: " +
                                            pMaxOccurs +
                                            "; expected 'unbounded' or a nonnegative integer value.");
      }
    }
  }

  public int getMaxOccurs() {
    return maxOccurs;
  }

  public void setMinOccurs(int pMinOccurs) {
    if (pMinOccurs < 0) {
      throw new IllegalArgumentException("Invalid value for minOccurs: " +
                                          pMinOccurs +
                                          "; expected a nonnegative integer value.");      
    }
    minOccurs = pMinOccurs;
  }

  public int getMinOccurs() {
    return minOccurs;
  }

  public void validate() throws SAXException {
    if (maxOccurs != -1  &&  minOccurs > maxOccurs) {
      throw new LocSAXException("The 'minOccurs' attribute value (" +
                                 minOccurs +
                                 ") is greater than the 'maxOccurs' attribute value (" +
                                 maxOccurs + ")", owner.getLocator());
    }
  }
}
