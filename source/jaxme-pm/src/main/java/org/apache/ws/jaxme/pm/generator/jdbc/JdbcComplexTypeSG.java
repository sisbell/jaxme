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

import org.apache.ws.jaxme.generator.sg.ComplexTypeSG;
import org.apache.ws.jaxme.generator.sg.ComplexTypeSGChain;
import org.apache.ws.jaxme.generator.sg.impl.ComplexTypeSGChainImpl;
import org.apache.ws.jaxme.xs.XSType;
import org.apache.ws.jaxme.xs.impl.XSUtil;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class JdbcComplexTypeSG extends ComplexTypeSGChainImpl {
  private final JaxMeJdbcSG jdbcSG;
  private XSType xsType;

  protected JdbcComplexTypeSG(JaxMeJdbcSG pJdbcSG, ComplexTypeSGChain o, XSType pType) {
    super(o);
    jdbcSG = pJdbcSG;
    if (pType == null) {
      throw new NullPointerException("The XSType argument must not be null.");
    }
    xsType = pType;
  }

  public void init(ComplexTypeSG pTypeSG) throws SAXException {
    super.init(pTypeSG);
    TableDetails tableDetails = (TableDetails)
      XSUtil.getSingleAppinfo(xsType.getAnnotations(), TableDetails.class);
    if (tableDetails != null) {
      CustomTableData data = jdbcSG.addTableData(pTypeSG, xsType, tableDetails);
      pTypeSG.getTypeSG().setProperty(jdbcSG.getKey(), data);
    }
  }
}
