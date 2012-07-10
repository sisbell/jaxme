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

import java.util.ArrayList;
import java.util.List;

import org.apache.ws.jaxme.xs.XSAnnotation;
import org.apache.ws.jaxme.xs.XSAppinfo;
import org.apache.ws.jaxme.xs.parser.impl.LocSAXException;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XSUtil {
  /** <p>Returns all childs of xs:annotation/xs:appinfo implementing
   * or extending the given class.</p>
   */
  public static List getAppinfos(XSAnnotation[] annotations, Class pClass) {
    List result = new ArrayList();
    for (int i = 0;  i < annotations.length;  i++) {
      XSAppinfo[] appinfos = annotations[i].getAppinfos();
      for (int j = 0;  j < appinfos.length;  j++) {
        Object[] childs = appinfos[j].getChilds();
        for (int k = 0;  k < childs.length;  k++) {
          if (pClass.isAssignableFrom(childs[k].getClass())) {
            result.add(childs[k]);
          }
        }
      }
    }
    return result;
  }

  /** <p>Returns the first child of xs:annotation/xs:appinfo implementing
   * or extending the given class. Ensures that the child is unique.</p>
   * @return The unique child or null, if no such child exists.
   */
  public static Object getSingleAppinfo(XSAnnotation[] annotations, Class pClass) throws SAXException {
    Object result = null;
    if (annotations != null) {
      for (int i = 0;  i < annotations.length;  i++) {
        XSAppinfo[] appinfos = annotations[i].getAppinfos();
        for (int j = 0;  j < appinfos.length;  j++) {
          Object[] childs = appinfos[j].getChilds();
          for (int k = 0;  k < childs.length;  k++) {
            if (pClass.isAssignableFrom(childs[k].getClass())) {
              if (result == null) {
                result = childs[k];
              } else {
                throw new LocSAXException("Multiple instances of " + pClass.getName() +
                                           " in xs:annotation/xs:appinfo are forbidden.",
                                           appinfos[j].getLocator());
              }
            }
          }
        }
      }
    }
    return result;
  }
}
