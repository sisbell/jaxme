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

import java.util.ArrayList;
import java.util.List;

import org.apache.ws.jaxme.generator.sg.AtomicTypeSG;
import org.apache.ws.jaxme.generator.sg.Facet;
import org.apache.ws.jaxme.generator.sg.SimpleTypeSG;
import org.apache.ws.jaxme.generator.sg.SimpleTypeSGChain;
import org.apache.ws.jaxme.xs.XSEnumeration;
import org.apache.ws.jaxme.xs.XSSimpleType;
import org.apache.ws.jaxme.xs.XSType;
import org.xml.sax.SAXException;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class SimpleTypeRestrictionSG extends SimpleTypeSGChainImpl {
  private XSSimpleType simpleType;
  private XSType xsType;
  private Facet[] facets;
  private final AtomicTypeSG atomicTypeSG;

  /** <p>Creates a new instance of SimpleTypeRestrictionSG.</p>
   */
  public SimpleTypeRestrictionSG(SimpleTypeSGChain o, XSType pType, XSSimpleType pSimpleType) {
    super(o);
    simpleType = pSimpleType;
    if (pSimpleType.isAtomic()) {
      atomicTypeSG = new AtomicTypeSGImpl(pSimpleType.getAtomicType());
    } else {
      atomicTypeSG = null;
    }
  }

  public void init(SimpleTypeSG pController) throws SAXException {
    super.init(pController);
    List myFacets = new ArrayList();

    XSEnumeration[] enumerations = simpleType.getEnumerations();
    if (enumerations.length > 0) {
      Facet f = new FacetImpl(xsType, enumerations);
      myFacets.add(f);
    }

    simpleType = null;  // Make this available for garbage collection
    xsType = null;      // Make this available for garbage collection
    facets = (Facet[]) myFacets.toArray(new Facet[myFacets.size()]);
  }

  public Facet[] getFacets(SimpleTypeSG pController) {
    return facets;
  }

  public AtomicTypeSG getAtomicType(SimpleTypeSG pController) {
    if (atomicTypeSG == null) {
      return super.getAtomicType(pController);
    } else {
      return atomicTypeSG;
    }
  }
}
