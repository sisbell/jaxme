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

import java.util.ArrayList;
import java.util.List;

import org.apache.ws.jaxme.xs.xml.*;


/** <p>Interface of <code>xs:simpleRestrictionModel</code>, following
 * this specification:
 * <pre>
 *  &lt;xs:group name="simpleRestrictionModel"&gt;
 *    &lt;xs:sequence&gt;
 *      &lt;xs:element name="simpleType" type="xs:localSimpleType" minOccurs="0"/&gt;
 *      &lt;xs:group ref="xs:facets" minOccurs="0" maxOccurs="unbounded"/&gt;
 *    &lt;/xs:sequence&gt;
 *  &lt;/xs:group&gt;
 *
 *  &lt;xs:group name="facets"&gt;
 *    &lt;xs:annotation&gt;
 *      &lt;xs:documentation&gt;
 *        We should use a substitution group for facets, but
 *        that's ruled out because it would allow users to
 *        add their own, which we're not ready for yet.
 *      &lt;/xs:documentation&gt;
 *    &lt;/xs:annotation&gt;
 *    &lt;xs:choice&gt;
 *      &lt;xs:element ref="xs:minExclusive"/&gt;
 *      &lt;xs:element ref="xs:minInclusive"/&gt;
 *      &lt;xs:element ref="xs:maxExclusive"/&gt;
 *      &lt;xs:element ref="xs:maxInclusive"/&gt;
 *      &lt;xs:element ref="xs:totalDigits"/&gt;
 *      &lt;xs:element ref="xs:fractionDigits"/&gt;
 *      &lt;xs:element ref="xs:length"/&gt;
 *      &lt;xs:element ref="xs:minLength"/&gt;
 *      &lt;xs:element ref="xs:maxLength"/&gt;
 *      &lt;xs:element ref="xs:enumeration"/&gt;
 *      &lt;xs:element ref="xs:whiteSpace"/&gt;
 *      &lt;xs:element ref="xs:pattern"/&gt;
 *    &lt;/xs:choice&gt;
 *  &lt;/xs:group&gt;
 * </pre></p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XsGSimpleRestrictionModelImpl implements XsGSimpleRestrictionModel {
  private final XsObject owner;
  private XsTLocalSimpleType simpleType;
  private List facets;

  protected XsGSimpleRestrictionModelImpl(XsObject pOwner) {
    owner = pOwner;
  }

  public XsTLocalSimpleType createSimpleType() {
    if (simpleType != null) {
      throw new IllegalStateException("Multiple 'simpleType' childs are forbidden.");
    }
    if (facets != null  &&  facets.size() > 0) {
      throw new IllegalStateException("The 'simpleType' child element must precede the '" +
                                       ((XsTFacetBase) facets.get(0)).getFacetName() + "' child element.");
    }
    return simpleType = owner.getObjectFactory().newXsTLocalSimpleType(owner);
  }

  public XsTLocalSimpleType getSimpleType() {
    return simpleType;
  }

  protected XsTFacetBase getFacetByName(String pName) {
    if (facets != null) {
      for (int i = 0;  i < facets.size();  i++) {
        XsTFacetBase facet = (XsTFacetBase) facets.get(i);
        if (pName.equals(facet.getFacetName())) {
          return facet;
        }
      }
    }
    return null;
  }

  protected void addFacet(XsTFacetBase pFacet) {
    if (facets == null) {
      facets = new ArrayList();
    }
    facets.add(pFacet);
  }

  protected void addUniqueFacet(XsTFacetBase pFacet) {
    if (getFacetByName(pFacet.getFacetName()) != null) {
      throw new IllegalStateException("Multiple '" + pFacet.getFacetName() + "' childs are forbidden.");
    }
    addFacet(pFacet);
  }

  public XsEMinExclusive createMinExclusive() {
    XsEMinExclusive minExclusive = owner.getObjectFactory().newXsEMinExclusive(owner);
    addUniqueFacet(minExclusive);
    return minExclusive;
  }

  public XsEMinExclusive getMinExclusive() {
    return (XsEMinExclusive) getFacetByName("minExclusive");
  }

  public XsEMinInclusive createMinInclusive() {
    XsEMinInclusive minInclusive = owner.getObjectFactory().newXsEMinInclusive(owner);
    addUniqueFacet(minInclusive);
    return minInclusive;
  }

  public XsEMinInclusive getMinInclusive() {
    return (XsEMinInclusive) getFacetByName("minInclusive");
  }

  public XsEMaxExclusive createMaxExclusive() {
    XsEMaxExclusive maxExclusive = owner.getObjectFactory().newXsEMaxExclusive(owner);
    addUniqueFacet(maxExclusive);
    return maxExclusive;
  }

  public XsEMaxExclusive getMaxExclusive() {
    return (XsEMaxExclusive) getFacetByName("maxExclusive");
  }

  public XsEMaxInclusive createMaxInclusive() {
    XsEMaxInclusive maxInclusive = owner.getObjectFactory().newXsEMaxInclusive(owner);
    addUniqueFacet(maxInclusive);
    return maxInclusive;
  }

  public XsEMaxInclusive getMaxInclusive() {
    return (XsEMaxInclusive) getFacetByName("maxInclusive");
  }

  public XsETotalDigits createTotalDigits() {
    XsETotalDigits totalDigits = owner.getObjectFactory().newXsETotalDigits(owner);
    addUniqueFacet(totalDigits);
    return totalDigits;
  }

  public XsETotalDigits getTotalDigits() {
    return (XsETotalDigits) getFacetByName("totalDigits");
  }

  public XsEFractionDigits createFractionDigits() {
    XsEFractionDigits fractionDigits = owner.getObjectFactory().newXsEFractionDigits(owner);
    addUniqueFacet(fractionDigits);
    return fractionDigits;
  }

  public XsEFractionDigits getFractionDigits() {
    return (XsEFractionDigits) getFacetByName("fractionDigits");
  }

  public XsELength createLength() {
    XsELength length = owner.getObjectFactory().newXsELength(owner);
    addUniqueFacet(length);
    return length;
  }

  public XsELength getLength() {
    return (XsELength) getFacetByName("length");
  }

  public XsEMinLength createMinLength() {
    XsEMinLength minLength = owner.getObjectFactory().newXsEMinLength(owner);
    addUniqueFacet(minLength);
    return minLength;
  }

  public XsEMinLength getMinLength() {
    return (XsEMinLength) getFacetByName("minLength");
  }

  public XsEMaxLength createMaxLength() {
    XsEMaxLength maxLength = owner.getObjectFactory().newXsEMaxLength(owner);
    addUniqueFacet(maxLength);
    return maxLength;
  }

  public XsEMaxLength getMaxLength() {
    return (XsEMaxLength) getFacetByName("maxLength");
  }

  public XsEWhiteSpace createWhiteSpace() {
    XsEWhiteSpace whiteSpace = owner.getObjectFactory().newXsEWhiteSpace(owner);
    addUniqueFacet(whiteSpace);
    return whiteSpace;
  }

  public XsEWhiteSpace getWhiteSpace() {
    return (XsEWhiteSpace) getFacetByName("whiteSpace");
  }

  public XsEPattern createPattern() {
    XsEPattern pattern = owner.getObjectFactory().newXsEPattern(owner);
    addFacet(pattern);
    return pattern;
  }

  public XsEPattern[] getPatterns() {
    if (facets == null) {
      return new XsEPattern[0];
    }
    List result = new ArrayList();
    for (int i = 0;  i < facets.size();  i++) {
      XsTFacetBase facet = (XsTFacetBase) facets.get(i);
      if ("pattern".equals(facet.getFacetName())) {
        result.add(facet);
      }
    }
    return (XsEPattern[]) result.toArray(new XsEPattern[result.size()]);
  }

  public XsEEnumeration createEnumeration() {
    XsEEnumeration enumeration = owner.getObjectFactory().newXsEEnumeration(owner);
    addFacet(enumeration);
    return enumeration;
  }

  public XsEEnumeration[] getEnumerations() {
    if (facets == null) {
      return new XsEEnumeration[0];
    }
    List result = new ArrayList();
    for (int i = 0;  i < facets.size();  i++) {
      XsTFacetBase facet = (XsTFacetBase) facets.get(i);
      if ("enumeration".equals(facet.getFacetName())) {
        result.add(facet);
      }
    }
    return (XsEEnumeration[]) result.toArray(new XsEEnumeration[result.size()]);
  }

  public boolean hasFacets() {
    return facets != null  &&  facets.size() > 0;
  }

  public XsTFacetBase[] getFacets() {
    if (facets == null) {
      return new XsEEnumeration[0];
    }
    return (XsTFacetBase[]) facets.toArray(new XsTFacetBase[facets.size()]);
  }
}
