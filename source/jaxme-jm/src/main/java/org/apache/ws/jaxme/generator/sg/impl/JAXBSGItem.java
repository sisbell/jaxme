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
package org.apache.ws.jaxme.generator.sg.impl;

import org.apache.ws.jaxme.generator.sg.SGFactory;
import org.apache.ws.jaxme.generator.sg.SchemaSG;
import org.apache.ws.jaxme.xs.XSObject;
import org.xml.sax.Locator;


/** <p>Default implementation of {@link org.apache.ws.jaxme.generator.sg.SGItem}.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public abstract class JAXBSGItem {
  private final SGFactory factory;
  private final XSObject object;
  private final SchemaSG schema;

  protected JAXBSGItem(SGFactory pFactory, SchemaSG pSchema, XSObject pObject) {
    if (pSchema == null) {
      throw new NullPointerException("The SchemaSG argument must not be null.");
    }
    if (pObject == null) {
      throw new NullPointerException("The XSObject argument must not be null.");
    }
    factory = pFactory;
    schema = pSchema;
    object = pObject;
  }

  public SGFactory getFactory() { return factory; }
  public SchemaSG getSchema() { return schema; }
  public XSObject getXSObject() { return object; }
  public Locator getLocator() { return getXSObject().getLocator(); }
}
