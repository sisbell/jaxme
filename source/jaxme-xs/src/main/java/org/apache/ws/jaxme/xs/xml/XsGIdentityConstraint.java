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

/** <p>Implementation of the group <code>xs:identityConstraint</code>,
 * specified as follows:
 * <pre>
 *  <xs:group name="identityConstraint">
 *    <xs:annotation>
 *      <xs:documentation>
 *        The three kinds of identity constraints, all with
 *        type of or derived from 'keybase'.
 *      </xs:documentation>
 *    </xs:annotation>
 *    <xs:choice>
 *      <xs:element ref="xs:unique"/>
 *      <xs:element ref="xs:key"/>
 *      <xs:element ref="xs:keyref"/>
 *    </xs:choice>
 *  </xs:group>
 * </pre></p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface XsGIdentityConstraint {
  public XsEUnique createUnique();

  public XsEKey createKey();

  public XsEKeyref createKeyref();

  public XsTIdentityConstraint[] getIdentityConstraints();
}
