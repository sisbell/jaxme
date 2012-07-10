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

import org.apache.ws.jaxme.sqls.Schema;
import org.apache.ws.jaxme.sqls.Table;
import org.apache.ws.jaxme.sqls.impl.TableImpl;


/** <p>Default implementation of a table in a DB2 database.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class DB2TableImpl extends TableImpl implements DB2Table {
  private TableSpace tableSpace, indexTableSpace, longTableSpace;

  public DB2TableImpl(Schema pSchema, Table.Name pName) {
    super(pSchema, pName);
  }

  public void setTableSpace(TableSpace pTableSpace) { indexTableSpace = pTableSpace; }
  public TableSpace getTableSpace() { return tableSpace; }
  public void setIndexTableSpace(TableSpace pTableSpace) { indexTableSpace = pTableSpace; }
  public TableSpace getIndexTableSpace() { return indexTableSpace; }
  public void setLongTableSpace(TableSpace pTableSpace) { indexTableSpace = pTableSpace; }
  public TableSpace getLongTableSpace() { return longTableSpace; }
}
