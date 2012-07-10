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
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.ws.jaxme.xs.XSParser;
import org.apache.ws.jaxme.xs.parser.DOMBuilder;
import org.apache.ws.jaxme.xs.xml.*;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;


/** <p>Implementation of <code>xs:annotation</code>, as specified
 * by the following:
 * <pre>
 *  &lt;xs:element name="annotation" id="annotation"&gt;
 *    &lt;xs:annotation&gt;
 *      &lt;xs:documentation source="http://www.w3.org/TR/xmlschema-1/#element-annotation"/&gt;
 *    &lt;/xs:annotation&gt;
 *    &lt;xs:complexType&gt;
 *      &lt;xs:complexContent&gt;
 *        &lt;xs:extension base="xs:openAttrs"&gt;
 *          &lt;xs:choice minOccurs="0" maxOccurs="unbounded"&gt;
 *            &lt;xs:element ref="xs:appinfo"/&gt;
 *            &lt;xs:element ref="xs:documentation"/&gt;
 *          &lt;/xs:choice&gt;
 *          &lt;xs:attribute name="id" type="xs:ID"/&gt;
 *        &lt;/xs:extension&gt;
 *      &lt;/xs:complexContent&gt;
 *    &lt;/xs:complexType&gt;
 *  &lt;/xs:element&gt;
 * </pre></p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XsEAnnotationImpl extends XsTOpenAttrsImpl implements XsEAnnotation {
  private XsID id;
  private List childs;

  protected XsEAnnotationImpl(XsObject pParent) {
    super(pParent);
  }

  public void setId(XsID pId) {
    id = pId;
  }

  public XsID getId() {
    return id;
  }

  protected void addChild(Object pChild) {
    if (childs == null) {
      childs = new ArrayList();
    }
    childs.add(pChild);
  }

  public XsEAppinfo createAppinfo() {
    XsEAppinfo appinfo = getObjectFactory().newXsEAppinfo(this);
    addChild(appinfo);
    return appinfo;
  }

  public XsEDocumentation createDocumentation() {
    XsEDocumentation documentation = getObjectFactory().newXsEDocumentation(this);
    addChild(documentation);
    return documentation;
  }

  public XsEAppinfo[] getAppinfos() {
    if (childs == null) {
      return new XsEAppinfo[0];
    } else {
      List result = new ArrayList();
      for (Iterator iter = childs.iterator();  iter.hasNext();  ) {
        Object o = iter.next();
        if (o instanceof XsEAppinfo) {
          result.add(o);
        }
      }
      return (XsEAppinfo[]) result.toArray(new XsEAppinfo[result.size()]);
    }
  }

  public XsEDocumentation[] getDocumentations() {
    if (childs == null) {
      return new XsEDocumentation[0];
    } else {
      List result = new ArrayList();
      for (Iterator iter = childs.iterator();  iter.hasNext();  ) {
        Object o = iter.next();
        if (o instanceof XsEDocumentation) {
          result.add(o);
        }
      }
      return (XsEDocumentation[]) result.toArray(new XsEDocumentation[result.size()]);
    }
  }

  public Object[] getChilds() {
    if (childs == null) {
      return new Object[0];
    } else {
      return childs.toArray();
    }
  }

  public ContentHandler getChildHandler(String pQName,
                                         String pNamespaceURI, String pLocalName) throws SAXException {
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
}
