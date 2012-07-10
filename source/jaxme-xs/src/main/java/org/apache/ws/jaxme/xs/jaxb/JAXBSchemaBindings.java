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


/** <p>This interface implements the JAXB schema bindings.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 * @version $Id: JAXBSchemaBindings.java 231789 2004-02-22 00:52:34Z jochen $
 */
public interface JAXBSchemaBindings {
  /** <p>Configures the package that is used in this schema.</p>
   */
  public interface Package extends XsObject {
    /** <p>Returns the package name.</p>
     */
    public String getName();

    /** <p>Returns the packages JavaDoc documentation.</p>
     */
    public JAXBJavadoc getJavadoc();
  }

  public interface NameTransformation extends XsObject {
    /** <p>Returns the suffix.</p>
     */
    public String getSuffix();
    /** <p>Returns the prefix.</p>
     */
    public String getPrefix();
  }

  public interface NameXmlTransform extends XsObject {
    /** <p>Returns the <code>typeName</code>'s NameTransformation.</p>
     */
    public NameTransformation getTypeName();
    /** <p>Returns the <code>elementName</code>'s NameTransformation.</p>
     */
    public NameTransformation getElementName();
    /** <p>Returns the <code>modelGroupName</code>'s NameTransformation.</p>
     */
    public NameTransformation getModelGroupName();
    /** <p>Returns the <code>anonymousTypeName</code>'s NameTransformation.</p>
     */
    public NameTransformation getAnonymousTypeName();
  }

  /** <p>Returns the package declaration.</p>
   */
  public Package getPackage();

  /** <p>Returns the list of NameXmlTransforms.</p>
   */
  public NameXmlTransform[] getNameXmlTransform();
}
