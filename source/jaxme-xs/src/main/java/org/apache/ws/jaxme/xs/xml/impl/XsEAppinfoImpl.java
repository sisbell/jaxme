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

import javax.xml.parsers.ParserConfigurationException;

import org.apache.ws.jaxme.xs.XSParser;
import org.apache.ws.jaxme.xs.parser.DOMBuilder;
import org.apache.ws.jaxme.xs.parser.XsObjectCreator;
import org.apache.ws.jaxme.xs.xml.*;
import org.apache.ws.jaxme.xs.xml.XsAnyURI;
import org.apache.ws.jaxme.xs.xml.XsEAppinfo;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;


/** <p>Implementation of <code>xs:appinfo</code>, as specified by the
 * following:
 * <pre>
 *  &lt;xs:element name="appinfo" id="appinfo"&gt;
 *    &lt;xs:annotation&gt;
 *      &lt;xs:documentation source="http://www.w3.org/TR/xmlschema-1/#element-appinfo"/&gt;
 *    &lt;/xs:annotation&gt;
 *    &lt;xs:complexType mixed="true"&gt;
 *      &lt;xs:sequence minOccurs="0" maxOccurs="unbounded"&gt;
 *        &lt;xs:any processContents="lax"/&gt;
 *      &lt;/xs:sequence&gt;
 *      &lt;xs:attribute name="source" type="xs:anyURI"/&gt;
 *    &lt;/xs:complexType&gt;
 *  &lt;/xs:element&gt;
 * </pre></p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XsEAppinfoImpl extends XsObjectImpl implements XsEAppinfo {
  private List childs;
  private XsAnyURI source;

  protected XsEAppinfoImpl(XsObject pParent) {
    super(pParent);
  }

  protected void addChild(Object pChild) {
    if (childs == null) {
      childs = new ArrayList();
    }
    childs.add(pChild);
  }

  protected XsObjectCreator[] getXsObjectCreators() {
    return null;
  }

  public Object[] getChilds() {
    if (childs == null) {
      return new Object[0];
    } else {
      return childs.toArray();
    }
  }

  public void setSource(XsAnyURI pSource) {
    source = pSource;
  }

  public XsAnyURI getSource() {
    return source;
  }

  public ContentHandler getChildHandler(String pQName, String pNamespaceURI, String pLocalName) throws SAXException {
    XsObjectCreator[] objectCreators = getXsObjectCreators();
    if (objectCreators != null) {
      XsQName qName = new XsQName(pNamespaceURI, pLocalName, XsQName.prefixOf(pQName));
      for (int i = 0;  i < objectCreators.length;  i++) {
        XsObject result = objectCreators[i].newBean(this, getObjectFactory().getLocator(), qName);
        if (result != null) {
          addChild(result);
          return getObjectFactory().newXsSAXParser(result);
        }
      }
    }

    if (XSParser.XML_SCHEMA_URI.equals(pNamespaceURI)) {
      return null;  // Let the parser handle this element
    }

    try {
      DOMBuilder db = new DOMBuilder();
      addChild(db.getDocument());
      return db;
    } catch (ParserConfigurationException e) {
      throw new SAXException(e);
    }
  }

  /** <p>Adds text to the appinfo contents.</p>
   */
  public void addText(String pText) {
      addChild(pText);
  }
}
