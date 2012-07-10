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
package org.apache.ws.jaxme.sqls.db2;

import org.apache.ws.jaxme.sqls.Table;


/** <p>Interface of a table in a DB2 database.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface DB2Table extends Table {
  /** <p>Sets the {@link TableSpace}, in which the table should be
   * created physically. Null, if the database may choose a
   * {@link TableSpace}.</p>
   */
  public void setTableSpace(TableSpace pTableSpace);

  /** <p>Returns the {@link TableSpace}, in which the table should be
   * created physically. Null, if the database may choose a
   * {@link TableSpace}.</p>
   */
  public TableSpace getTableSpace();

  /** <p>Sets the {@link TableSpace}, in which the tables indexes
   * should be created physically. Null, if the tables
   * main {@link TableSpace} should be choosen.</p>
   */
  public void setIndexTableSpace(TableSpace pTableSpace);

  /** <p>Returns the {@link TableSpace}, in which the tables
   * indexes should be created physically. Null, if the tables
   * main {@link TableSpace} should be choosen.</p>
   */
  public TableSpace getIndexTableSpace();

  /** <p>Sets the {@link TableSpace}, in which the tables indexes
   * should be created physically. Null, if the tables
   * main {@link TableSpace} should be choosen.</p>
   */
  public void setLongTableSpace(TableSpace pTableSpace);

  /** <p>Returns the {@link TableSpace}, in which the tables
   * LOB data should be physically stored. Null, if the tables
   * main {@link TableSpace} should be choosen.</p>
   */
  public TableSpace getLongTableSpace();
}
