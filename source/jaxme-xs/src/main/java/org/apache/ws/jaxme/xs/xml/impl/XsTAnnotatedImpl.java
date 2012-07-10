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

import org.apache.ws.jaxme.xs.xml.*;


/** <p>Implementation of an annotated element, as specified
 * by the following:
 * <pre>
 *  &lt;xs:complexType name="annotated"&gt;
 *    &lt;xs:annotation&gt;
 *      &lt;xs:documentation&gt;
 *        This type is extended by all types which allow annotation
 *        other than &lt;schema&gt; itself.
 *      &lt;/xs:documentation&gt;
 *    &lt;/xs:annotation&gt;
 *    &lt;xs:complexContent&gt;
 *      &lt;xs:extension base="xs:openAttrs"&gt;
 *        &lt;xs:sequence&gt;
 *          &lt;xs:element ref="xs:annotation" minOccurs="0"/&gt;
 *        &lt;/xs:sequence&gt;
 *        &lt;xs:attribute name="id" type="xs:ID"/&gt;
 *      &lt;/xs:extension&gt;
 *    &lt;/xs:complexContent&gt;
 *  &lt;/xs:complexType&gt;
 * </pre></p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XsTAnnotatedImpl extends XsTOpenAttrsImpl implements XsTAnnotated {
  private XsEAnnotation annotation;
  private XsID id;

  protected XsTAnnotatedImpl(XsObject pParent) {
    super(pParent);
  }

  public XsEAnnotation createAnnotation() {
    if (annotation != null) {
      throw new NullPointerException("Multiple 'annotation' elements are forbidden.");
    }
    return annotation = getObjectFactory().newXsEAnnotation(this);
  }

  public XsEAnnotation getAnnotation() {
    return annotation;
  }

  public void setId(XsID pId) {
    id = pId;
  }

  public XsID getId() {
    return id;
  }
}
