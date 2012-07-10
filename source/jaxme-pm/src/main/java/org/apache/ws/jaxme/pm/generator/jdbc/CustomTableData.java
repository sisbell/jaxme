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
import org.apache.ws.jaxme.sqls.Table;

/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class CustomTableData {
  private final Table table;
  private final TableDetails tableDetails;
  private final ComplexTypeSG typeSG;
  /** Creates a new instance-
   * @param pJdbcSG The source generator creating this instance.
   * @param pTable The table, which is currently read by the source generator.
   * @param pTypeSG The complex type being generated.
   * @param pTableDetails The connection details.
   */
  public CustomTableData(JaxMeJdbcSG pJdbcSG, Table pTable, ComplexTypeSG pTypeSG, TableDetails pTableDetails) {
    table = pTable;
    typeSG = pTypeSG;
    tableDetails = pTableDetails;
  }
  /** Returns the table, which is currently being read.
   */
  public Table getTable() { return table; }
  /** Returns the type, which is being generated.
   */
  public ComplexTypeSG getTypeSG() { return typeSG; }
  /** Returns the connection details.
   */
  public TableDetails getTableDetails() { return tableDetails; }
}
