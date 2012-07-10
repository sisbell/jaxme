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


/** <p>Implementation of <code>xs:import</code>, as specified by
 * the following:
 * <pre>
 *  &lt;xs:element name="import" id="import"&gt;
 *    &lt;xs:annotation&gt;
 *      &lt;xs:documentation source="http://www.w3.org/TR/xmlschema-1/#element-import"/&gt;
 *    &lt;/xs:annotation&gt;
 *    &lt;xs:complexType&gt;
 *      &lt;xs:complexContent&gt;
 *        &lt;xs:extension base="xs:annotated"&gt;
 *          &lt;xs:attribute name="namespace" type="xs:anyURI"/&gt;
 *          &lt;xs:attribute name="schemaLocation" type="xs:anyURI"/&gt;
 *        &lt;/xs:extension&gt;
 *      &lt;/xs:complexContent&gt;
 *    &lt;/xs:complexType&gt;
 *  &lt;/xs:element&gt;
 * </pre></p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XsEImportImpl extends XsTAnnotatedImpl implements XsEImport {
  private XsAnyURI namespace;
  private XsAnyURI schemaLocation;

  protected XsEImportImpl(XsObject pParent) {
    super(pParent);
  }

  public void setNamespace(XsAnyURI pNamespace) {
    namespace = pNamespace;
  }

  public XsAnyURI getNamespace() {
    return namespace;
  }

  public void setSchemaLocation(XsAnyURI pSchemaLocation) {
    schemaLocation = pSchemaLocation;
  }

  public XsAnyURI getSchemaLocation() {
    return schemaLocation;
  }
}
