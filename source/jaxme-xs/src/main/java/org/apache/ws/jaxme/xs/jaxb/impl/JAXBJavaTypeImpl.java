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

import org.apache.ws.jaxme.xs.jaxb.JAXBJavaType;
import org.apache.ws.jaxme.xs.parser.impl.LocSAXException;
import org.apache.ws.jaxme.xs.xml.XsObject;
import org.apache.ws.jaxme.xs.xml.XsQName;
import org.apache.ws.jaxme.xs.xml.impl.XsObjectImpl;
import org.xml.sax.SAXException;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 * @version $Id: JAXBJavaTypeImpl.java 231996 2004-09-30 00:09:30Z jochen $
 */
public class JAXBJavaTypeImpl extends XsObjectImpl implements JAXBJavaType {
  public static class JAXBGlobalJavaTypeImpl extends JAXBJavaTypeImpl implements JAXBJavaType.JAXBGlobalJavaType {
    /** <p>Creates a new instance of JAXBJavaTypeImpl.java.</p>
     */
    protected JAXBGlobalJavaTypeImpl(XsObject pParent) {
      super(pParent);
    }

    private XsQName xmlType;
    public void setXmlType(XsQName pType) { xmlType = pType; }
    public void setXmlType(String pType) throws SAXException {
      String[] parts = new String[3];
      getNamespaceSupport().processName(pType, parts, false);
      setXmlType(new XsQName(parts[0], parts[1], XsQName.prefixOf(pType)));
    }
    public XsQName getXmlType() { return xmlType; }
    public void validate() throws SAXException {
      super.validate();
      if (getXmlType() == null) {
        throw new LocSAXException("Missing attribute: 'xmlType'", getLocator());
      }
    }
  }

  private String name;
  private boolean hasNsContext;
  private String parseMethod, printMethod;
  private XsQName xmlType;

  /** <p>Creates a new instance of JAXBJavaTypeImpl.java.</p>
   */
  protected JAXBJavaTypeImpl(XsObject pParent) {
    super(pParent);
  }

  public void setName(String pName) { name = pName; }
  public String getName() { return name; }

  public void setHasNsContext(boolean pHasNsContext) {
    hasNsContext = pHasNsContext;
  }

  public boolean hasNsContext() {
    return hasNsContext;
  }

  public void setParseMethod(String pParseMethod) {
    parseMethod = pParseMethod;
  }

  public String getParseMethod() {
    return parseMethod;
  }

  public void setPrintMethod(String pPrintMethod) {
    printMethod = pPrintMethod;
  }

  public String getPrintMethod() {
    return printMethod;
  }

  /** Sets the XML Type being customized by this element.
   */
  public void setXmlType(String pXmlType) throws SAXException {
  	setXmlType(asXsQName(pXmlType));
  }

  /** Sets the XML Type being customized by this element.
   */
  public void setXmlType(XsQName pXmlType) {
  	xmlType = pXmlType;
  }

  public XsQName getXmlType() {
  	return xmlType;
  }
}
