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
package org.apache.ws.jaxme.xs;


/** <p>Interface of an xs:enumeration facet. This could be a simple string,
 * but it is not unusual, that they carry important information in their
 * <code>xs:annotation/xs:appinfo</code> section, thus the inheritance
 * from {@link XSObject}.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface XSEnumeration extends XSObject {
  /** <p>Returns the enumeration facets set of annotations.</p>
   */
  public XSAnnotation[] getAnnotations();

  /** <p>Returns the facets value.</p>
   */
  public String getValue();
}
