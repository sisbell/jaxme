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
package org.apache.ws.jaxme.xs.jaxb;

import org.apache.ws.jaxme.xs.xml.XsObject;
import org.apache.ws.jaxme.xs.xml.XsObjectFactory;



/** <p>Implementation of the XsObjectFactory for parsing a
 * JAXB schema.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface JAXBXsObjectFactory extends XsObjectFactory {
  /** <p>Returns a new instance of JAXBProperty.BaseType.</p>
   */
  public JAXBProperty.BaseType newBaseType(XsObject pParent);

  /** <p>Returns a new instance of JAXBClass.</p>
   */
  public JAXBClass newJAXBClass(XsObject pParent);

  /** <p>Returns a new instance of JAXBGlobalBindings.</p>
   */
  public JAXBGlobalBindings newJAXBGlobalBindings(XsObject pParent);

  /** <p>Returns a new instance of JAXBJavadoc.</p>
   */
  public JAXBJavadoc newJAXBJavadoc(XsObject pParent);

  /** <p>Returns a new instance of NameXmlTransform.</p>
   */
  public JAXBSchemaBindings.NameXmlTransform newNameXmlTransform(XsObject pParent);

  /** <p>Returns a new instance of NameTransformation.</p>
   */
  public JAXBSchemaBindings.NameTransformation newNameTransformation(XsObject pParent);

  /** <p>Returns a new instance of Package.</p>
   */
  public JAXBSchemaBindings.Package newPackage(XsObject pParent);

  /** <p>Returns a new instance of JAXBProperty.</p>
   */
  public JAXBProperty newJAXBProperty(XsObject pParent);

  /** <p>Returns a new instance of JAXBJavaType.</p>
   */
  public JAXBJavaType newJAXBJavaType(XsObject pParent);

  /** <p>Returns a new instance of JAXBGlobalJavaType.</p>
   */
  public JAXBJavaType.JAXBGlobalJavaType newJAXBGlobalJavaType(XsObject pParent);

  /** <p>Returns a new instance of JAXBSchemaBindings.</p>
   */
  public JAXBSchemaBindings newJAXBSchemaBindings(XsObject pParent);

  /** <p>Returns a new instance of JAXBTypesafeEnumClass.</p>
   */
  public JAXBTypesafeEnumClass newJAXBTypesafeEnumClass(XsObject pParent);

  /** <p>Returns a new instance of JAXBTypesafeEnumMember.</p>
   */
  public JAXBTypesafeEnumMember newJAXBTypesafeEnumMember(XsObject pParent);
}
