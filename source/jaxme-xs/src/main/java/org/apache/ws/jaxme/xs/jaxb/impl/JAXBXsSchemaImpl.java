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
package org.apache.ws.jaxme.xs.jaxb.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.ws.jaxme.xs.jaxb.*;
import org.apache.ws.jaxme.xs.jaxb.JAXBSchemaBindings;
import org.apache.ws.jaxme.xs.jaxb.JAXBXsObjectFactory;
import org.apache.ws.jaxme.xs.parser.XSContext;
import org.apache.ws.jaxme.xs.parser.impl.LocSAXException;
import org.apache.ws.jaxme.xs.xml.XsEAnnotation;
import org.apache.ws.jaxme.xs.xml.XsEAppinfo;
import org.apache.ws.jaxme.xs.xml.impl.XsESchemaImpl;
import org.xml.sax.SAXException;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class JAXBXsSchemaImpl extends XsESchemaImpl implements JAXBXsSchema {
  private JAXBSchemaBindings schemaBindings;
  private String jaxbVersion;
  private String[] jaxbExtensionBindingPrefixes;

  public void setJaxbVersion(String pVersion) {
    jaxbVersion = pVersion;
  }

  public void setJaxbExtensionBindingPrefixes(String pExtensionBindingPrefixes) {
    List result = new ArrayList();
    for (StringTokenizer st = new StringTokenizer(pExtensionBindingPrefixes);  st.hasMoreTokens();  ) {
      result.add(st.nextToken());
    }
    jaxbExtensionBindingPrefixes = (String[]) result.toArray(new String[result.size()]);
  }

  public String[] getJaxbExtensionBindingPrefixes() {
    return jaxbExtensionBindingPrefixes == null ? new String[0] : jaxbExtensionBindingPrefixes;
  }

  public String getJaxbVersion() {
    return jaxbVersion;
  }

  /** <p>Creates a new instance of JAXBXsSchemaImpl.</p>
   */
  public JAXBXsSchemaImpl(XSContext pContext) {
    super(pContext);
  }

  public void validate() throws SAXException {
    if (isValidated()) {
      return;
    }
    super.validate();
    Object[] childs = getChilds();
    for (int i = 0;  i < childs.length;  i++) {
      if (!(childs[i] instanceof XsEAnnotation)) {
        continue;
      }
      XsEAnnotation annotation = (XsEAnnotation) childs[i];
      XsEAppinfo[] appinfos = annotation.getAppinfos();
      for (int j = 0;  j < appinfos.length;  j++) {
        Object[] appinfoChilds = appinfos[j].getChilds();
        for (int k = 0;  k < appinfoChilds.length;  k++) {
          if (!(appinfoChilds[k] instanceof JAXBSchemaBindings)) {
            continue;
          }
          if (schemaBindings == null) {
            schemaBindings = (JAXBSchemaBindings) appinfoChilds[k];
          } else {
            throw new LocSAXException("A schema must have at most a single instance of 'schemaBindings'",
                                       appinfos[j].getLocator());
          }
        }
      }
    }

    if (schemaBindings == null) {
      schemaBindings = ((JAXBXsObjectFactory) getObjectFactory()).newJAXBSchemaBindings(this);
    }
  }

  public JAXBSchemaBindings getJAXBSchemaBindings() {
    return schemaBindings;
  }

  public boolean setAttribute(String pQName, String pNamespaceURI, String pLocalName,
                                String pValue) throws SAXException {
    if (JAXBParser.JAXB_SCHEMA_URI.equals(pNamespaceURI)) {
      if ("version".equals(pLocalName)) {
        setJaxbVersion(pValue);
        return true;
      } else if ("extensionBindingPrefixes".equals(pLocalName)) {
        setJaxbExtensionBindingPrefixes(pValue);
        return true;
      } else {
        throw new LocSAXException("Invalid attribute: " + pQName, getLocator());
      }
    } else {
      return super.setAttribute(pQName, pNamespaceURI, pLocalName, pValue);
    }
  }
}
