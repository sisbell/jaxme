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

package org.apache.ws.jaxme.pm.generator.jdbc;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.ws.jaxme.xs.XSSchema;
import org.apache.ws.jaxme.xs.impl.XSUtil;
import org.apache.ws.jaxme.xs.parser.impl.LocSAXException;
import org.apache.ws.jaxme.xs.xml.XsObject;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class TableDetails extends ConnectionDetails {
  protected TableDetails(JaxMeJdbcSG pJdbcSG, XsObject pParent) throws SAXException {
    super(pJdbcSG, pParent);

    XSSchema schema = pParent.getXsESchema().getContext().getXSSchema();
    if (schema != null) {
      ConnectionDetails details = (ConnectionDetails)
        XSUtil.getSingleAppinfo(schema.getAnnotations(), ConnectionDetails.class);
      if (details != null) {
        cloneFrom(details);
      }
    }
  }

  private String name;
  private List keys;
  /** Sets the tables name.
   */
  public void setName(String pName) {
    name = pName;
  }
  /** Returns the tables name.
   */
  public String getName() {
    return name;
  }
  /** Sets the names of the primary key columns.
   */
  public void setKeys(String pKeys) {
    if (keys == null) {
      keys = new ArrayList();
    }
    for (StringTokenizer st = new StringTokenizer(pKeys, ", ");  st.hasMoreTokens();  ) {
      keys.add(st.nextToken());
    }
  }
  /** Returns the names of the primary key columns.
   */
  public List getKeys() {
    return keys;
  }
  /** Copies the current details from the given.
   */
  public void cloneFrom(TableDetails pFrom) {
    super.cloneFrom(pFrom);
    name = pFrom.name;
  }

  public void validate() throws SAXException {
    super.validate();
    if (name == null  ||  name.length() == 0) {
      throw new LocSAXException("Missing or empty 'table' attribute.", getLocator());
    }
  }
}