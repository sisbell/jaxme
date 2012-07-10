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
 
package org.apache.ws.jaxme.pm.ino;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;


/** <p>This is a SAX content handler for an ino:response document.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class InoResponseHandler implements ContentHandler {
  /** <p>The namespace of an INO response document:
   * <samp>http://namespaces.softwareag.com/tamino/response2</samp>.</p>
   */
  public static final String INO_RESPONSE2_URI =
    "http://namespaces.softwareag.com/tamino/response2";
  /** <p>The namespace of the XQL section in an INO response
   * document: <samp>http://metalab.unc.edu/xql/</samp>.</p>
   */
  public static final String XQL_URI = "http://metalab.unc.edu/xql/";


  private boolean inInoMessage = false;
  private boolean inInoMessageText;
  private boolean inXqlResult;
  private int level;
  private String inoErrorCode;
  private StringBuffer inoErrorMessage;
  private Locator locator;
  private ContentHandler resultHandler;

  /** Creates a new InoResponseHandler */
  public InoResponseHandler() {}

  public void setDocumentLocator(Locator l) {
    locator = l;
  }

  /** Returns the document {@link Locator}, that was
   * previously set by the XML parser. May be null, if
   * the parser didn't supply one.
   */
  public Locator getDocumentLocator() {
    return locator;
  }

  public void startDocument() throws org.xml.sax.SAXException {
    inInoMessage = false;
    inInoMessageText = false;
    inXqlResult = false;
    level = 0;
    if (inoObjectIdList != null) {
      inoObjectIdList.clear();
    }
  }

  public void endDocument() throws org.xml.sax.SAXException {
  }
  
  public void startElement(String namespaceUri, String localName,
                           String qName, Attributes attr) throws SAXException {
    if (inXqlResult) {
      if (resultHandler != null) {
        if (level == 2) {
          resultHandler.startDocument();
        }
        resultHandler.startElement(namespaceUri, localName, qName, attr);
      }
    } else if (inInoMessage) {
      if (level == 2) {
        if (INO_RESPONSE2_URI.equals(namespaceUri)  &&
            "messagetext".equals(localName)) {
          String c = attr.getValue(INO_RESPONSE2_URI, "code");
          if (c != null) {
            inoErrorCode = c;
          }
          inInoMessageText = true;
        }
      }
    } else if (level == 1) {
      if (XQL_URI.equals(namespaceUri)  &&  "result".equals(localName)) {
        inXqlResult = true;
      } else if (INO_RESPONSE2_URI.equals(namespaceUri)) {
        if ("message".equals(localName)) {
          String retval = attr.getValue(INO_RESPONSE2_URI, "returnvalue");
          if (retval == null  ||  !retval.equals("0")) {
            inoErrorCode = retval;
            inoErrorMessage = new StringBuffer();
            inInoMessage = true;
          }
        } else if (inoObjectIdList != null && "object".equals(localName)) {
          inoObjectIdList.add(attr.getValue(INO_RESPONSE2_URI, "id"));
        }
      }
    }
    ++level;
  }

  public void endElement(String namespaceUri, String localName,
                         String qName) throws SAXException {
    level--;
    if (inXqlResult) {
      if (level == 1) {
        inXqlResult = false;
      } else {
        if (resultHandler != null) {
          resultHandler.endElement(namespaceUri, localName, qName);
          if (level == 2) {
            resultHandler.endDocument();
          }
        }
      }
    } else if (inInoMessage) {
      if (level == 1) {
        if (inoErrorCode == null) {
          inoErrorCode = "INOUNKNOWN";
        }
        throw new InoException(inoErrorCode, inoErrorMessage.toString());
      } else if (level == 2) {
        if (inInoMessageText) {
          inInoMessageText = false;
        }
      }
    }
  }

  public void startPrefixMapping(String namespaceUri,
                                 String prefix) throws SAXException {
    if (inXqlResult) {
      if (resultHandler != null) {
        resultHandler.startPrefixMapping(namespaceUri, prefix);
      }
    }
  }
  
  public void endPrefixMapping(String prefix) throws SAXException {
    if (inXqlResult) {
      if (resultHandler != null) {
        resultHandler.endPrefixMapping(prefix);
      }
    }
  }
  
  public void ignorableWhitespace(char[] ch, int start, int len) throws SAXException {
    if (inXqlResult) {
      if (resultHandler != null) {
        resultHandler.ignorableWhitespace(ch, start, len);
      }
    }
  }
  
  public void skippedEntity(String entity) throws SAXException {
    if (inXqlResult) {
      if (resultHandler != null) {
        resultHandler.skippedEntity(entity);
      }
    }
  }
  
  public void processingInstruction(String target, String data) throws SAXException {
    if (inXqlResult) {
      if (resultHandler != null) {
        resultHandler.processingInstruction(target, data);
      }
    }
  }
  
  public void characters(char[] ch, int start, int len) throws SAXException {
    if (inXqlResult) {
      if (resultHandler != null) {
        resultHandler.characters(ch, start, len);
      }
    } else if (inInoMessageText) {
      inoErrorMessage.append(ch, start, len);
    }
  }

  /** <p>Sets the result handler. The result handler is another SAX ContentHandler.
   * For any result document the InoResponseHandler finds, that is, for any
   * subelement of xql:result, a stream of SAX events is generated for the
   * result handler.</p>
   * <p>If the response document contains more than one result object, then the
   * result handler must be "restartable". In other words, it must be able to
   * process multiple startDocument ... endDocument startDocument ...
   * endDocument sequences.</p>
   *
   * @param handler The result handler to use or null to disable SAX events
   * @see #getResultHandler
   */
  public void setResultHandler(ContentHandler handler) {
    resultHandler = handler;
  }

  /** <p>Returns a result handler, that was previously set with setResultHandler,
   * or null.</p>
   * <p>The result handler is another SAX ContentHandler.
   * For any result document the InoResponseHandler finds, that is, for any
   * subelement of xql:result, a stream of SAX events is generated for the
   * result handler.</p>
   * <p>If the response document contains more than one result object, then the
   * result handler must be "restartable". In other words, it must be able to
   * process multiple startDocument ... endDocument startDocument ...
   * endDocument sequences.</p>
   *
   * @return The result handler or null, if generating SAX events is disabled.
   */
  public ContentHandler getResultHandler() {
    return resultHandler;
  }

  private java.util.List inoObjectIdList;
  /** <p>The Tamino response document contains object ID's of inserted
   * or updated objects. If you use this method, then the ID's are
   * collected in the given List. A null value disables ID
   * collection. The list will be cleared within <code>startDocument</code>,
   * so it's safe to reuse the list over multiple uses of the handler.</p>
   * <p>More precise, the list will contain all occurences of
   * ino:response/ino:object/@ino:id.</p>
   *
   * @param pList A list where ID's are being collected or null to disable
   *   ID collection.
   * @see #getInoObjectIdList
   */
  public void setInoObjectIdList(java.util.List pList) {
    inoObjectIdList = pList;
  }
  /** <p>Returns the current list for collection of generated ino:id's.</p>
   *
   * @see #setInoObjectIdList
   */
  public java.util.List getInoObjectIdList() { return inoObjectIdList; }
}
