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
package org.apache.ws.jaxme.generator.types;

import org.apache.ws.jaxme.generator.sg.AtomicTypeSG;
import org.apache.ws.jaxme.generator.sg.Facet;
import org.apache.ws.jaxme.generator.sg.ListTypeSG;
import org.apache.ws.jaxme.generator.sg.SGFactory;
import org.apache.ws.jaxme.generator.sg.SchemaSG;
import org.apache.ws.jaxme.generator.sg.SimpleTypeSG;
import org.apache.ws.jaxme.generator.sg.SimpleTypeSGChain;
import org.apache.ws.jaxme.generator.sg.UnionTypeSG;
import org.apache.ws.jaxme.generator.sg.impl.JAXBSGItem;
import org.apache.ws.jaxme.js.DirectAccessible;
import org.apache.ws.jaxme.js.JavaMethod;
import org.apache.ws.jaxme.js.JavaQNameImpl;
import org.apache.ws.jaxme.js.JavaSource;
import org.apache.ws.jaxme.js.TypedValue;
import org.apache.ws.jaxme.xs.XSType;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public abstract class SimpleTypeSGImpl extends JAXBSGItem implements SimpleTypeSGChain {
  private boolean isNullable;

  /** <p>Creates a new instance of SimpleTypeSGImpl.</p>
   */
  protected SimpleTypeSGImpl(SGFactory pFactory, SchemaSG pSchema, XSType pType) throws SAXException {
    super(pFactory, pSchema, pType);
    if (!pType.isSimple()) {
      throw new IllegalStateException("The given type is complex.");
    }
  }

  protected XSType getXSType() { return (XSType) super.getXSObject(); }
  public SGFactory getFactory(SimpleTypeSG pController) { return getFactory(); }
  public SchemaSG getSchema(SimpleTypeSG pController) { return getSchema(); }
  public Locator getLocator(SimpleTypeSG pController) { return getLocator(); }
  public boolean isAtomic(SimpleTypeSG pController) { return false; }
  public boolean isList(SimpleTypeSG pController) { return false; }
  public boolean isUnion(SimpleTypeSG pController) { return false; }
  public boolean isXsId(SimpleTypeSG pController) { return false; }
  public boolean isXsIdRef(SimpleTypeSG pController) { return false; }
  public boolean hasSetMethod(SimpleTypeSG pController) { return true; }
  public void setNullable(SimpleTypeSG pController, boolean pNullable) {
    setNullable(pNullable);
  }
  /** Sets whether the data type is nullable.
   */
  public void setNullable(boolean pNullable) { isNullable = pNullable; }
  public boolean isNullable(SimpleTypeSG pController) { return isNullable; }

  public void init(SimpleTypeSG pController) throws SAXException {
  }

  public String getCollectionType(SimpleTypeSG pController) {
    return getSchema().getCollectionType();
  }

  public UnionTypeSG getUnionType(SimpleTypeSG pController) {
    throw new IllegalStateException("This type is no union.");
  }

  public ListTypeSG getListType(SimpleTypeSG pController) {
    throw new IllegalStateException("This type is no list.");
  }

  public AtomicTypeSG getAtomicType(SimpleTypeSG pController) {
    throw new IllegalStateException("This type is not atomic.");
  }

  public Facet[] getFacets(SimpleTypeSG pController) {
    return new Facet[0];
  }

  public Facet getFacet(SimpleTypeSG pController, Facet.Type pType) {
    Facet[] facets = pController.getFacets();
    for (int i = 0;  i < facets.length;  i++) {
      Facet f = facets[i];
      if (f.getType().equals(pType)) {
        return f;
      }
    }
    return null;
  }

  public void generate(SimpleTypeSG pController) {
  }

  public void generate(SimpleTypeSG pController, JavaSource pSource) {
  }

  public Object getInitialValue(SimpleTypeSG pController, JavaSource pSource) throws SAXException {
    return null;
  }

  public JavaMethod getXMLSetMethod(SimpleTypeSG pController, JavaSource pSource,
                                    String pFieldName, String pParamName, String pMethodName) throws SAXException {
    if (pController.hasSetMethod()) {
      JavaMethod jm = pSource.newJavaMethod(pMethodName, JavaQNameImpl.VOID, JavaSource.PUBLIC);
      DirectAccessible param = jm.addParam(pController.getRuntimeType(), pParamName);
      if (!pSource.isInterface()) {
        pController.addValidation(jm, param);
        jm.addLine(pFieldName, " = ", param, ";");
      }
      return jm;
    } else {
      return null;
    }                                 
  }

  public void addValidation(SimpleTypeSG pController, JavaMethod pMethod, DirectAccessible pValue) throws SAXException {
  }

	public void addHandlerValidation(SimpleTypeSG pController, JavaMethod pJm, TypedValue pValue, Object pStringValue)
			throws SAXException {
	}
}
