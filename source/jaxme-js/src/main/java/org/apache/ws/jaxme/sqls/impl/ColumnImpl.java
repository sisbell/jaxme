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
package org.apache.ws.jaxme.sqls.impl;

import java.util.Iterator;

import org.apache.ws.jaxme.sqls.Column;
import org.apache.ws.jaxme.sqls.Index;
import org.apache.ws.jaxme.sqls.Table;


/** <p>Implementation of a column.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class ColumnImpl extends AbstractColumn {
  public static class NameImpl extends SQLFactoryImpl.IdentImpl implements Column.Name {
    public NameImpl(String pName) {
      super(pName);
    }
    public boolean equals(Object o) {
      return o != null  &&  (o instanceof Column.Name)  &&  super.equals(o);
    }
  }
  public static class TypeImpl extends SQLFactoryImpl.IdentImpl implements Column.Type {
    private int jdbcType;
    public TypeImpl(String pName, int pJDBCType) {
      super(pName);
      jdbcType = pJDBCType;
    }
    public int getJDBCType() {
      return jdbcType;
    }
  }


  private Table table;

  protected ColumnImpl(Table pTable, Column.Name pName, Column.Type pType) {
    super(pName, pType);
    table = pTable;
  }

  public Table getTable() { return table; }
  public String getQName() { return getTable().getQName() + "." + getName(); }
  public boolean isVirtual() { return false; }

  public boolean isPrimaryKeyPart() {
    Index index = getTable().getPrimaryKey();
    if (index == null) {
      return false;
    }
    for (Iterator iter = index.getColumns();  iter.hasNext();  ) {
      Column column = (Column) iter.next();
      if (column.equals(this)) {
        return true;
      }
    }
    return false;
  }
}
