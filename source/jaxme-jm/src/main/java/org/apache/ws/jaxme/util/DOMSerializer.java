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
package org.apache.ws.jaxme.util;

import javax.xml.XMLConstants;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;

/** <p>Serializes a DOM node into a stream of SAX events.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 * @version $Id: DOMSerializer.java 520366 2007-03-20 11:28:56Z jochen $
 */
public class DOMSerializer {
  private boolean namespaceDeclarationAttribute;
  private boolean parentsNamespaceDeclarationDisabled;

  /** <p>Sets whether XML declarations are being serialized as
   * attributes or as SAX events (default).</p>
   */
  public void setNamespaceDeclarationAttribute(boolean pXmlDeclarationAttribute) {
    namespaceDeclarationAttribute = pXmlDeclarationAttribute;
  }

  /** <p>Returns whether XML declarations are being serialized as
   * attributes or as SAX events (default).</p>
   */
  public boolean isNamespaceDeclarationAttribute() {
    return namespaceDeclarationAttribute;
  }

  /** <p>Returns whether XML declarations present in the parent nodes
   * are being serialized (default) or not. This option takes effect
   * only if the namespace declarations are sent as events. In other
   * words, if the <code>namespaceDeclarationAttribute</code>
   * properts is false.</p>
   */
  public void setParentsNamespaceDeclarationDisabled(boolean pParentsXmlDeclarationDisabled) {
    parentsNamespaceDeclarationDisabled = pParentsXmlDeclarationDisabled;
  }

  /** <p>Sets whether XML declarations present in the parent nodes
   * are being serialized (default) or not. This option takes effect
   * only if the namespace declarations are sent as events. In other
   * words, if the <code>namespaceDeclarationAttribute</code>
   * properts is false.</p>
   */
  public boolean isParentsNamespaceDeclarationDisabled() {
    return parentsNamespaceDeclarationDisabled;
  }

  protected void doSerializeChilds(Node pNode, ContentHandler pHandler)
      throws SAXException {
    for (Node child = pNode.getFirstChild();  child != null;
          child = child.getNextSibling()) {
      doSerialize(child, pHandler);
    }
  }

  protected void parentsStartPrefixMappingEvents(Node pNode, ContentHandler pHandler)
      throws SAXException {
    if (pNode != null) {
      parentsStartPrefixMappingEvents(pNode.getParentNode(), pHandler);
      if (pNode.getNodeType() == Node.ELEMENT_NODE) {
        startPrefixMappingEvents(pNode, pHandler);
      }
    }
  }

  protected void parentsEndPrefixMappingEvents(Node pNode, ContentHandler pHandler)
      throws SAXException {
    if (pNode != null) {
      if (pNode.getNodeType() == Node.ELEMENT_NODE) {
        endPrefixMappingEvents(pNode, pHandler);
      }
      parentsEndPrefixMappingEvents(pNode.getParentNode(), pHandler);
    }
  }

  protected void startPrefixMappingEvents(Node pNode, ContentHandler pHandler)
      throws SAXException {
    NamedNodeMap nnm = pNode.getAttributes();
    if (nnm != null) {
      for (int i = 0;  i < nnm.getLength();  i++) {
        Node attr = nnm.item(i);
        if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(attr.getNamespaceURI())) {
          String prefix;
          if (XMLConstants.XMLNS_ATTRIBUTE.equals(attr.getPrefix())) {
            prefix = attr.getLocalName();
          } else if (XMLConstants.XMLNS_ATTRIBUTE.equals(attr.getNodeName())) {
            prefix = "";
          } else {
            throw new IllegalStateException("Unable to parse namespace declaration: " + attr.getNodeName());
          }
          String uri = attr.getNodeValue();
          if (uri == null) {
            uri = "";
          }
          pHandler.startPrefixMapping(prefix, uri);
        }
      }
    }
  }

  protected void endPrefixMappingEvents(Node pNode, ContentHandler pHandler)
      throws SAXException {
    NamedNodeMap nnm = pNode.getAttributes();
    if (nnm != null) {
      for (int i = nnm.getLength()-1;  i >= 0;  i--) {
        Node attr = nnm.item(i);
        if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(attr.getNamespaceURI())) {
          String prefix = attr.getLocalName();
          pHandler.endPrefixMapping(prefix);
        }
      }
    }
  }

  /** Converts the given node <code>pNode</code> into a
   * stream of SAX events, which are fired into the
   * content handler <code>pHandler</code>.
   */
  public void serialize(Node pNode, ContentHandler pHandler)
      throws SAXException {
    if (!isNamespaceDeclarationAttribute()  &&
        !isParentsNamespaceDeclarationDisabled()) {
      parentsStartPrefixMappingEvents(pNode.getParentNode(), pHandler);
    }
    doSerialize(pNode, pHandler);
    if (!isNamespaceDeclarationAttribute()  &&
        !isParentsNamespaceDeclarationDisabled()) {
      parentsEndPrefixMappingEvents(pNode.getParentNode(), pHandler);
    }
  }

  protected void doSerialize(Node pNode, ContentHandler pHandler)
      throws SAXException {
    switch (pNode.getNodeType()) {
      case Node.DOCUMENT_NODE:
        pHandler.startDocument();
        doSerializeChilds(pNode, pHandler);
        pHandler.endDocument();
        break;
      case Node.DOCUMENT_FRAGMENT_NODE:
        doSerializeChilds(pNode, pHandler);
        break;
      case Node.ELEMENT_NODE:
        AttributesImpl attr = new AttributesImpl();
        boolean isNamespaceDeclarationAttribute = isNamespaceDeclarationAttribute();
        if (!isNamespaceDeclarationAttribute) {
          startPrefixMappingEvents(pNode, pHandler);
        }
        NamedNodeMap nnm = pNode.getAttributes();
        if (nnm != null) {
          for (int i = 0;  i < nnm.getLength();  i++) {
            Node a = nnm.item(i);
            if (isNamespaceDeclarationAttribute  ||
                !XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(a.getNamespaceURI())) {
              String aUri = a.getNamespaceURI();
              String aLocalName = a.getLocalName();
              String aNodeName = a.getNodeName();
              if (aLocalName == null) {
                  if (aUri == null  ||  aUri.length() == 0) {
                      aLocalName = aNodeName;
                  } else {
                      throw new IllegalStateException("aLocalName is null");
                  }
              }
              attr.addAttribute(aUri == null ? "" : aUri, aLocalName,
                                aNodeName, "CDATA", a.getNodeValue());
            }
          }
        }
        String nUri = pNode.getNamespaceURI();
        if (nUri == null) {
            nUri = "";
        }
        pHandler.startElement(nUri, pNode.getLocalName(),
                              pNode.getNodeName(), attr);
        doSerializeChilds(pNode, pHandler);
        pHandler.endElement(nUri, pNode.getLocalName(),
                            pNode.getNodeName());
        if (!isNamespaceDeclarationAttribute) {
          endPrefixMappingEvents(pNode, pHandler);
        }
        break;
      case Node.TEXT_NODE:
      case Node.CDATA_SECTION_NODE:
        {
          String s = pNode.getNodeValue();
          pHandler.characters(s.toCharArray(), 0, s.length());
        }
        break;
      case Node.PROCESSING_INSTRUCTION_NODE:
        pHandler.processingInstruction(pNode.getNodeName(), pNode.getNodeValue());
        break;
      case Node.ENTITY_REFERENCE_NODE:
        pHandler.skippedEntity(pNode.getNodeName());
        break;
      case Node.COMMENT_NODE:
        if (pHandler instanceof LexicalHandler) {
          String s = pNode.getNodeValue();
          ((LexicalHandler) pHandler).comment(s.toCharArray(), 0, s.length());
        }
        break;
      default:
        throw new IllegalStateException("Unknown node type: " + pNode.getNodeType());
    }
  }
}
