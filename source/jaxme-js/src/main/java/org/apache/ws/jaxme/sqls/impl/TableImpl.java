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
import java.util.Iterator;
import java.util.List;

import org.apache.ws.jaxme.sqls.Column;
import org.apache.ws.jaxme.sqls.ColumnSet;
import org.apache.ws.jaxme.sqls.DeleteStatement;
import org.apache.ws.jaxme.sqls.ForeignKey;
import org.apache.ws.jaxme.sqls.Index;
import org.apache.ws.jaxme.sqls.InsertStatement;
import org.apache.ws.jaxme.sqls.SQLFactory;
import org.apache.ws.jaxme.sqls.Schema;
import org.apache.ws.jaxme.sqls.SelectStatement;
import org.apache.ws.jaxme.sqls.Table;
import org.apache.ws.jaxme.sqls.TableReference;
import org.apache.ws.jaxme.sqls.UpdateStatement;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class TableImpl implements Table {
   public static class NameImpl extends SQLFactoryImpl.IdentImpl implements Table.Name {
      public NameImpl(String pName) {
         super(pName);
      }
   }

   private Schema schema;
   private Table.Name name;
   private List columns = new ArrayList();
   private List indexes = new ArrayList();
   private List foreignKeys = new ArrayList();

   int indexNameCounter;

   protected TableImpl(Schema pSchema, Table.Name pName) {
      schema = pSchema;
      name = pName;
   }

	public Schema getSchema() {
      return schema;
	}

	public Table.Name getName() {
      return name;
	}

   public String getQName() {
      Schema mySchema = getSchema();
      if (mySchema.getName() == null) {
         return getName().getName();
      } else {
         return mySchema.getName().getName() + "." + getName().getName();
      }
   }

	public Iterator getColumns() {
      return columns.iterator();
	}

   public Column newColumn(String pName, Column.Type pType) {
      return newColumn(new ColumnImpl.NameImpl(pName), pType);
   }

   public Column newColumn(Column.Name pName, Column.Type pType) {
      if (pName == null) {
         throw new NullPointerException("The column name must not be null.");
      }
      Integer maxLength = getSchema().getSQLFactory().getMaxColumnNameLength();
      if (maxLength != null  &&  pName.getName().length() > maxLength.intValue()) {
         throw new IllegalArgumentException("The column name " + pName +
                                             " exceeds the maximum column length of " + maxLength);
      }

      if (pType == null) {
         throw new NullPointerException("The column type must not be null.");
      }
      Column column = getColumn(pName);
      if (column != null) {
         throw new IllegalStateException("A column named " + column.getName() +
                                          " already exists in the table " + getQName());
      }
      column = ((SQLFactoryImpl) getSchema().getSQLFactory()).newColumnImpl(this, pName, pType);
      columns.add(column);
      return column;
   }

   public Column getColumn(Column.Name pName) {
      if (pName == null) {
         throw new NullPointerException("Column names must not be null.");
      }
      for (Iterator iter = getColumns();  iter.hasNext();  ) {
         Column column = (Column) iter.next();
         if (getSchema().getSQLFactory().isColumnNameCaseSensitive()) {
            if (pName.getName().equalsIgnoreCase(column.getName().getName())) {
               return column;
            }
         } else {
            if (pName.equals(column.getName())) {
               return column;
            }
         }
      }
      return null;
   }

   public Column getColumn(String pName) {
      return getColumn(new ColumnImpl.NameImpl(pName));
   }

	public Index newKey() {
      Index result = new IndexImpl(this, true, false);
      indexes.add(result);
      return result;
	}

	public Index newIndex() {
      Index result = new IndexImpl(this, false, false);
      indexes.add(result);
      return result;
	}

   public Index getPrimaryKey() {
      for (Iterator iter = getIndexes();  iter.hasNext();  ) {
         Index index = (Index) iter.next();
         if (index.isPrimaryKey()) {
            return index;
         }
      }
      return null;
   }

   public Iterator getIndexes() {
      return indexes.iterator();
   }

	public Index newPrimaryKey() {
      Index pk = getPrimaryKey();
      if (pk != null) {
         throw new IllegalStateException("A primary key is already defined on table " + getName());
      }
      Index result = new IndexImpl(this, true, true);
      indexes.add(result);
      return result;
	}

   public Iterator getForeignKeys() {
      return foreignKeys.iterator();
   }

   public ForeignKey newForeignKey(Table pTable) {
      ForeignKey foreignKey = new ForeignKeyImpl(this, pTable);
      foreignKeys.add(foreignKey);
      return foreignKey;
   }

   public boolean equals(Object o) {
      if (o == null  ||  !(o instanceof Table)) {
         return false;
      }
      Table other = (Table) o;
      if (!getSchema().equals(other.getSchema())) {
         return false;
      }
      return getName().equals(other.getName());
   }

   public int hashCode() {
      return getSchema().hashCode() + getName().hashCode();
   }

   public InsertStatement getInsertStatement() {
      InsertStatement result = getSchema().getSQLFactory().newInsertStatement();
      result.setTable(this);
      for (Iterator iter = getColumns();  iter.hasNext();  ) {
         result.addSet((Column) iter.next());
      }
      return result;
   }

   public SelectStatement getSelectStatement() {
       SQLFactory factory = getSchema().getSQLFactory();
       SelectStatement result = factory.newSelectStatement();
      result.setTable(this);
      TableReference ref = result.getTableReference();
      for (Iterator iter = getColumns();  iter.hasNext();  ) {
         Column column = (Column) iter.next();
         result.addResultColumn(factory.getObjectFactory().newColumnReference(ref, column));
      }
      return result;
   }

  public UpdateStatement getUpdateStatement() {
    UpdateStatement result = getSchema().getSQLFactory().newUpdateStatement();
    result.setTable(this);
    TableReference ref = result.getTableReference();
    boolean hasPrimaryKey = false;
    for (Iterator iter = getColumns();  iter.hasNext();  ) {
      Column column = (Column) iter.next();
      if (column.isPrimaryKeyPart()) {
        hasPrimaryKey = true;
      } else {
        result.addSet(column);
      }
    }
    if (hasPrimaryKey) {
      result.getWhere().addColumnSetQuery(getPrimaryKey(), ref);
    } else {
      throw new IllegalStateException("Cannot create a default update statement without a primary key.");
    }
    return result;
  }

  public DeleteStatement getDeleteStatement() {
    DeleteStatement result = getSchema().getSQLFactory().newDeleteStatement();
    result.setTable(this);
    ColumnSet primaryKey = getPrimaryKey();
    if (primaryKey == null) {
      throw new IllegalStateException("Cannot create a default delete statement without a primary key.");
    }
    result.getWhere().addColumnSetQuery(primaryKey, result.getTableReference());
    return result;
  }
}
