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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;

import org.apache.ws.jaxme.xs.XSParser;
import org.apache.ws.jaxme.xs.parser.XSContext;
import org.apache.ws.jaxme.xs.parser.TextSetter;
import org.apache.ws.jaxme.xs.parser.XsSAXParser;
import org.xml.sax.SAXException;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class TextSetterImpl implements TextSetter {
  private static final Class[] ONE_STRING_CLASS = new Class[]{String.class};

  protected XSContext getData() {
    return XSParser.getRunningInstance().getContext();
  }

  protected boolean isIgnorable(String pText) {
    for (int i = 0;  i < pText.length();  i++) {
      if (!Character.isWhitespace(pText.charAt(i))) {
        return false;
      }
    }
    return true;
  }

  public void addText(String pText) throws SAXException {
    XsSAXParser handler = (XsSAXParser) getData().getCurrentContentHandler();
    Object bean = handler.getBean();
    try {
      Method m = bean.getClass().getMethod("addText", ONE_STRING_CLASS);
      m.invoke(bean, new Object[]{pText});
    } catch (NoSuchMethodException f) {
      if (isIgnorable(pText)) {
        return;
      }
      throw new IllegalStateException("Embedded text is not supported in the '" + handler.getQName() + "' element:" + pText);
    } catch (IllegalAccessException e) {
      throw new IllegalStateException("Embedded text is not supported in the '" + handler.getQName() + "' element:" + pText);
    } catch (InvocationTargetException e) {
      Throwable t = e.getTargetException();
      if (t instanceof RuntimeException) {
        throw (RuntimeException) t;
      } else if (t instanceof SAXException) {
        throw (SAXException) t;
      } else {
        throw new UndeclaredThrowableException(t);
      }
    }
  }
}
