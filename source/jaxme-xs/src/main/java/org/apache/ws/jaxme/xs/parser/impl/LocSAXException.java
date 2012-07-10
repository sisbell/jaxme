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
package org.apache.ws.jaxme.xs.parser.impl;

import java.io.PrintStream;
import java.io.PrintWriter;

import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


/** <p>The <code>locatable SAXException</code>, a subclass of the SAXParseException,
 * provides a human readable representation of the location.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class LocSAXException extends SAXParseException {
  public static String formatMsg(String pMsg, String pPublicId, String pSystemId, int pLineNumber, int pColNumber) {
    StringBuffer sb = new StringBuffer();
    if (pSystemId != null) {
      sb.append(pSystemId);
    }
    if (pLineNumber != -1) {
      if (sb.length() > 0) {
        sb.append(", ");
      }
      sb.append("line ").append(pLineNumber);
    }
    if (pColNumber != -1) {
      if (sb.length() > 0) {
        sb.append(", ");
      }
      sb.append("column ").append(pColNumber);
    }
    if (sb.length() == 0) {
      return "" + pMsg;
    } else {
      return "At " + sb + ": " + pMsg;
    }
  }

  private static String formatMsg(String pMsg, Locator pLocator) {
    if (pLocator == null) {
      return pMsg;
    } else {
      return formatMsg(pMsg, pLocator.getPublicId(), pLocator.getSystemId(), pLocator.getLineNumber(), pLocator.getColumnNumber());
    }
  }

  public LocSAXException(String pMsg, String pPublicId, String pSystemId,
                                int pLineNumber, int pColumnNumber, Exception pException) {
    super(formatMsg(pMsg, pPublicId, pSystemId, pLineNumber, pColumnNumber),
           pPublicId, pSystemId, pLineNumber, pColumnNumber, pException);
  }

  public LocSAXException(String pMsg, String pPublicId, String pSystemId,
                                int pLineNumber, int pColumnNumber) {
    super(formatMsg(pMsg, pPublicId, pSystemId, pLineNumber, pColumnNumber),
           pPublicId, pSystemId, pLineNumber, pColumnNumber);
  }

  public LocSAXException(String pMsg, Locator pLocator, Exception pException) {
    super(formatMsg(pMsg, pLocator), pLocator, pException);
  }

  public LocSAXException(String pMsg, Locator pLocator) {
    super(formatMsg(pMsg, pLocator), pLocator);
  }

  public void printStackTrace(PrintStream pStream) {
    super.printStackTrace(pStream);
    Exception e = getException();
    while (e != null) {
      pStream.println("Caused by:");
      e.printStackTrace(pStream);
      if (e instanceof SAXException) {
        e = ((SAXException) e).getException();
      } else {
        e = null;
      }
    }
  }

  public void printStackTrace(PrintWriter pWriter) {
    super.printStackTrace(pWriter);
    Exception e = getException();
    while (e != null) {
      pWriter.println("Caused by:");
      e.printStackTrace(pWriter);
      if (e instanceof SAXException) {
        e = ((SAXException) e).getException();
      } else {
        e = null;
      }
    }
  }
}
