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
package org.apache.ws.jaxme.xs.parser;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;


/** <p>Converts a stream of SAX events into a DOM node.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 * @version $Id: DOMBuilder.java 231785 2004-02-16 23:39:59Z jochen $
 */
public class DOMBuilder implements ContentHandler {
  private Node currentNode;
  private Element result;
  private Locator locator;
  private Document factory;

  /** <p>Creates a new instance of DOMBuilder, which creates
   * a new document. The document is available via
   * {@link #getDocument()}.</p>
   */
  public DOMBuilder() throws ParserConfigurationException {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setValidating(false);
    dbf.setNamespaceAware(true);
    DocumentBuilder db = dbf.newDocumentBuilder();
    currentNode = factory = db.newDocument();
  }

  /** <p>Creates a new instance of DOMBuilder, which creates
   * a new element node in the given node. The created node
   * is available via {@link #getResult()}.</p>
   */
  public DOMBuilder(Node pTarget) {
    if (pTarget == null) {
      throw new NullPointerException("Target node must not be null.");
    }
    factory = pTarget.getOwnerDocument();
    currentNode = pTarget;
  }

  /** <p>Returns the document being used as object factory. In the case
   * of the empty constructor, this is a new document. Otherwise it is
   * the target nodes owner document.</p>
   */
  public Document getDocument() {
    return factory;
  }

  /** <p>Returns the result element. In the case of the default constructor,
   * this is the document element. Otherwise it is added to the target node
   * via {@link org.w3c.dom.Node#appendChild(org.w3c.dom.Node)}.<(p>
   */
  public Element getResult() {
    return result;
  }

  /** <p>Sets the Locator.</p>
   */
  public void setDocumentLocator(Locator pLocator) {
    locator = pLocator;
  }

  /** <p>Returns the Locator.</p>
   */
  public Locator getDocumentLocator() {
    return locator;
  }

  /**
   * @see org.xml.sax.ContentHandler#startDocument()
   */
  public void startDocument() throws SAXException {
  }

  /**
   * @see org.xml.sax.ContentHandler#endDocument()
   */
  public void endDocument() throws SAXException {
  }

  public void startPrefixMapping(String prefix, String uri)
    throws SAXException {
  }

  public void endPrefixMapping(String prefix) throws SAXException {
  }

  public void startElement(String pNamespaceURI, String pLocalName,
                            String pQName, Attributes pAttr) throws SAXException {
    Document doc = getDocument();
    Element element;
    if (pNamespaceURI == null  ||  pNamespaceURI.length() == 0) {
      element = doc.createElement(pQName);
    } else {
      element = doc.createElementNS(pNamespaceURI, pQName);
    }
    if (pAttr != null) {
      for (int i = 0;  i < pAttr.getLength();  i++) {
        String uri = pAttr.getURI(i);
        String qName = pAttr.getQName(i);
        String value = pAttr.getValue(i);
        if (uri == null  ||  uri.length() == 0) {
          element.setAttribute(qName, value);
        } else {
          element.setAttributeNS(uri, qName, value);
        }
      }
    }

    currentNode.appendChild(element);
    currentNode = element;
    if (result == null) {
      result = element;
    }
  }

  public void endElement(String namespaceURI, String localName, String qName)
      throws SAXException {
    currentNode = currentNode.getParentNode();
  }

  public void characters(char[] ch, int start, int length)
      throws SAXException {
    Node node = currentNode.getLastChild();
    String s = new String(ch, start, length);
    if (node != null  &&  node.getNodeType() == Node.TEXT_NODE) {
      ((Text) node).appendData(s);
    } else {
      Text text = getDocument().createTextNode(s);
      currentNode.appendChild(text);
    }
  }

  public void ignorableWhitespace(char[] ch, int start, int length)
      throws SAXException {
    characters(ch, start, length);
  }

  public void processingInstruction(String pTarget, String pData)
      throws SAXException {
    ProcessingInstruction pi = getDocument().createProcessingInstruction(pTarget, pData);
    currentNode.appendChild(pi);
  }

  public void skippedEntity(String pName) throws SAXException {
    EntityReference entity = getDocument().createEntityReference(pName);
    currentNode.appendChild(entity);
  }
}
