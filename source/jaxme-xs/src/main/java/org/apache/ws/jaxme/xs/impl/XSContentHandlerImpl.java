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
 
package org.apache.ws.jaxme.xs.impl;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.ws.jaxme.xs.XSContentHandler;
import org.apache.ws.jaxme.xs.XSSchema;
import org.apache.ws.jaxme.xs.parser.XSContext;
import org.apache.ws.jaxme.xs.parser.XsSAXParser;
import org.apache.ws.jaxme.xs.parser.impl.LocSAXException;
import org.apache.ws.jaxme.xs.xml.XsESchema;
import org.apache.ws.jaxme.xs.xml.XsObjectFactory;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
class XSContentHandlerImpl implements XSContentHandler {
  private final XSLogicalParser parser;
  private final XsSAXParser xsSAXParser;
  private boolean finished;
  private int level = 0;
  private final String systemId;

  XSContentHandlerImpl(XSLogicalParser pParser, String pSystemId) throws SAXException {
    parser = pParser;
	systemId = pSystemId;
    XSContext data = parser.getData();
    data.setXSLogicalParser(parser);
    parser.clearSyntaxSchemas();
    XsObjectFactory factory = data.getXsObjectFactory();
    XsESchema mySchema = factory.newXsESchema();
    parser.setSchema(data.getXSObjectFactory().newXSSchema(data, mySchema));
    xsSAXParser = factory.newXsSAXParser(mySchema);
    parser.addSyntaxSchema(mySchema);
    data.setCurrentContentHandler(xsSAXParser);
  }

  public XSSchema getXSSchema() {
    if (!finished) {
      throw new IllegalStateException("The endDocument() method has not yet been invoked.");
    }
    return parser.getSchema();
  }

  public void startDocument() throws SAXException {
    xsSAXParser.startDocument();
  }

  public void endDocument() throws SAXException {
    xsSAXParser.endDocument();
    XsESchema syntaxSchema = (XsESchema) xsSAXParser.getBean();
    parser.removeSyntaxSchema();
    XSContext data = parser.getData();
    data.setCurrentContentHandler(null);

    try {
      parser.parse(syntaxSchema, systemId);
      XSSchema mySchema = parser.getSchema();
      parser.createSubstitutionGroups(mySchema);
      mySchema.validate();
      finished = true;
    } catch (IOException e) {
      throw new SAXException(e);
    } catch (ParserConfigurationException e) {
      throw new SAXException(e);
    } catch (RuntimeException e) {
      Exception ex = e;
      for (;;) {
        UndeclaredThrowableException te = null;
        Throwable t;
        if (ex instanceof UndeclaredThrowableException) {
          te = ((UndeclaredThrowableException) ex);
          t = te.getUndeclaredThrowable();
        } else if (ex instanceof InvocationTargetException) {
          t = ((InvocationTargetException) ex).getTargetException();
        } else {
          break;
        }
        if (t instanceof Exception) {
          ex = (Exception) t;
        } else {
          if (te == null) {
            te = new UndeclaredThrowableException(t);
          }
          t.printStackTrace();
          throw te;
        }
      }
      throw new LocSAXException(ex.getClass().getName() + ": " + ex.getMessage(),
                                 parser.getData().getLocator(), ex);
    }
  }

  public void characters(char[] pChars, int pStart, int pLen) throws SAXException {
    xsSAXParser.characters(pChars, pStart, pLen);
  }

  public void ignorableWhitespace(char[] pChars, int pStart, int pLen) throws SAXException {
    xsSAXParser.ignorableWhitespace(pChars, pStart, pLen);
  }

  public void endPrefixMapping(String pPrefix) throws SAXException {
    xsSAXParser.endPrefixMapping(pPrefix);
  }

  public void skippedEntity(String pEntity) throws SAXException {
    xsSAXParser.skippedEntity(pEntity);
  }

  public void setDocumentLocator(Locator pLocator) {
    xsSAXParser.setDocumentLocator(pLocator);
  }

  public void processingInstruction(String pTarget, String pData) throws SAXException {
    xsSAXParser.processingInstruction(pTarget, pData);
  }

  public void startPrefixMapping(String pPrefix, String pURI) throws SAXException {
    xsSAXParser.startPrefixMapping(pPrefix, pURI);
  }

  public void endElement(String pNamespaceURI, String pLocalName, String pQName) throws SAXException {
    xsSAXParser.endElement(pNamespaceURI, pLocalName, pQName);
    --level;
  }

  public void startElement(String pNamespaceURI, String pLocalName, String pQName, Attributes pAttrs)
      throws SAXException {
    xsSAXParser.startElement(pNamespaceURI, pLocalName, pQName, pAttrs);
  }
}