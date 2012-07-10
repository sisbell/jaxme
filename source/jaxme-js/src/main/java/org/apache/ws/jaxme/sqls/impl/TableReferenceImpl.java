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

import java.util.ArrayList;
import java.util.List;

import org.apache.ws.jaxme.sqls.Column;
import org.apache.ws.jaxme.sqls.ColumnReference;
import org.apache.ws.jaxme.sqls.Statement;
import org.apache.ws.jaxme.sqls.Table;
import org.apache.ws.jaxme.sqls.TableReference;


/** <p>Implementation of a {@link org.apache.ws.jaxme.sqls.TableReference}.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class TableReferenceImpl implements TableReference {
   private Statement statement;
   private Table table;
   private Table.Name alias;
   private List columnReferences = new ArrayList();

	/** <p>Creates a new instance of TableReference.</p>
	 */
	TableReferenceImpl(Statement pStatement, Table pTable) {
      statement = pStatement;
      table = pTable;
	}

	public Statement getStatement() {
      return statement;
	}

	public Table getTable() {
      return table;
	}

	public Table.Name getAlias() {
      return alias;
	}

	public void setAlias(Table.Name pName) {
      alias = pName;
	}

	public void setAlias(String pName) {
      setAlias(new TableImpl.NameImpl(pName));
	}

	public ColumnReference newColumnReference(String pName) {
      return newColumnReference(new ColumnImpl.NameImpl(pName));
	}

	public ColumnReference newColumnReference(Column.Name pName) {
      Column column = getTable().getColumn(pName);
      if (column == null) {
         throw new NullPointerException("Unknown column name in table " + getTable().getName() +
                                         ": " + pName);
      }
      return newColumnReference(column);
	}

	public ColumnReference newColumnReference(Column pColumn) {
      ColumnReference columnReference = getStatement().getSQLFactory().getObjectFactory().newColumnReference(this, pColumn);
      columnReferences.add(columnReference);
      return columnReference;
	}

   public boolean equals(Object o) {
      if (o == null  ||  !(o instanceof TableReference)) {
         return false;
      }
      TableReference ref = (TableReference) o;
      return ref.getStatement().equals(getStatement())  &&
             ref.getTable().equals(getTable());
   }

   public int hashCode() {
      return getStatement().hashCode() + getTable().hashCode();
   }
}
