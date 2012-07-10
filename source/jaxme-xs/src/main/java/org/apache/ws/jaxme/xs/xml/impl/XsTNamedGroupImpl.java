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


/** <p>Implementation of a named top-level group, with the following
 * specification:
 * <pre>
 *  &lt;xs:complexType name="namedGroup"&gt;
 *    &lt;xs:annotation&gt;
 *      &lt;xs:documentation&gt;
 *        Should derive this from realGroup, but too complicated for now
 *      &lt;/xs:documentation&gt;
 *    &lt;/xs:annotation&gt;
 *    &lt;xs:sequence&gt;
 *      &lt;xs:element ref="xs:annotation" minOccurs="0"/&gt;
 *      &lt;xs:choice minOccurs="1" maxOccurs="1"&gt;
 *        &lt;xs:element name="all"&gt;
 *          &lt;xs:complexType&gt;
 *            &lt;xs:complexContent&gt;
 *              &lt;xs:restriction base="xs:all"&gt;
 *                &lt;xs:group ref="xs:allModel"/&gt;
 *                &lt;xs:attribute name="minOccurs" use="prohibited"/&gt;
 *                &lt;xs:attribute name="maxOccurs" use="prohibited"/&gt;
 *              &lt;/xs:restriction&gt;
 *            &lt;/xs:complexContent&gt;
 *          &lt;/xs:complexType&gt;
 *        &lt;/xs:element&gt;
 *        &lt;xs:element name="choice" type="xs:simpleExplicitGroup"/&gt;
 *        &lt;xs:element name="sequence" type="xs:simpleExplicitGroup"/&gt;
 *      &lt;/xs:choice&gt;
 *    &lt;/xs:sequence&gt;
 *    &lt;xs:attribute name="name" use="required" type="xs:NCName"/&gt;
 *    &lt;xs:attribute name="ref" use="prohibited"/&gt;
 *    &lt;xs:attribute name="minOccurs" use="prohibited"/&gt;
 *    &lt;xs:attribute name="maxOccurs" use="prohibited"/&gt;
 *  &lt;/xs:complexType&gt;
 * &lt;/pre>&lt;/p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XsTNamedGroupImpl extends XsTAnnotatedImpl implements XsTNamedGroup {
  private XsGAllModel all;
  private XsTSimpleExplicitGroup sequence, choice;
  private XsNCName name;

  protected XsTNamedGroupImpl(XsObject pParent) {
    super(pParent);
  }

  public void setName(XsNCName pName) {
    name = pName;
  }

  public XsNCName getName() {
    return name;
  }

  public XsGAllModel createAll() {
    if (all != null) {
      throw new IllegalStateException("Multiple 'all' child elements are forbidden.");
    }
    if (choice != null) {
      throw new IllegalStateException("The 'choice' and 'all' child elements are mutually exclusive.");
    }
    if (sequence != null) {
      throw new IllegalStateException("The 'sequence' and 'all' child elements are mutually exclusive.");
    }
    return all = getObjectFactory().newXsGAllModel(this);
  }

  public XsGAllModel getAll() {
    return all;
  }

  public XsTSimpleExplicitGroup createSequence() {
    if (sequence != null) {
      throw new IllegalStateException("Multiple 'sequence' child elements are forbidden.");
    }
    if (all != null) {
      throw new IllegalStateException("The 'all' and 'sequence' child elements are mutually exclusive.");
    }
    if (choice != null) {
      throw new IllegalStateException("The 'all' and 'sequence' child elements are mutually exclusive.");
    }
    return sequence = getObjectFactory().newXsTSimpleExplicitGroup(this);
  }

  public XsTSimpleExplicitGroup getSequence() {
    return sequence;
  }

  public XsTSimpleExplicitGroup createChoice() {
    if (choice != null) {
      throw new IllegalStateException("Multiple 'choice' elements are forbidden.");
    }
    if (sequence != null) {
      throw new IllegalStateException("The 'sequence' and 'choice' elements are mutually exclusive.");
    }
    if (all != null) {
      throw new IllegalStateException("The 'all' and 'choice' elements are mutually exclusive.");
    }
    return choice = getObjectFactory().newXsTSimpleExplicitGroup(this);
  }

  public XsTSimpleExplicitGroup getChoice() {
    return choice;
  }

  public void validate() {
    XsGAllModel myAll = getAll();
    if (myAll == null) {
      XsTSimpleExplicitGroup mySequence = getSequence();
      if (mySequence == null) {
        XsTSimpleExplicitGroup myChoice = getChoice();
        if (myChoice == null) {
          throw new NullPointerException("Neither of the 'all', 'choice', or 'sequence' elements are given.");
        } else {
        }
      } else {
      }
    } else {
    }
  }
}
