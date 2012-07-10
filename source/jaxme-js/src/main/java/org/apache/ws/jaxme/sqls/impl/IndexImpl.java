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
import org.apache.ws.jaxme.sqls.Index;
import org.apache.ws.jaxme.sqls.Table;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class IndexImpl extends ColumnSetImpl implements Index {
  public static class NameImpl extends SQLFactoryImpl.IdentImpl implements Index.Name {
    public NameImpl(String pName) {
      super(pName);
    }
  }

  private final List columns = new ArrayList();
  private final boolean unique;
  private final boolean primaryKey;
  private Index.Name name;

	IndexImpl(Table pTable, boolean pUnique, boolean pPrimaryKey) {
      super(pTable);
      unique = pUnique;
      primaryKey = pPrimaryKey;
      if (primaryKey && !unique) {
         throw new IllegalArgumentException("A primary key must be unique.");
      }
    setName(pTable.getName() + "_" + "I" + ((TableImpl) pTable).indexNameCounter++);
  }

  public void setName(Index.Name pName) {
    if (pName == null) {
      throw new NullPointerException("An index name must not be null.");
    }
    name = pName;
  }

  public void setName(String pName) {
    if (pName == null) {
      throw new NullPointerException("An index name must not be null.");
    }
    setName(new IndexImpl.NameImpl(pName));
  }

  public Index.Name getName() {
    return name;
	}

	public void addColumn(Column pColumn) {
      if (columns.contains(pColumn)) {
         throw new IllegalStateException("The column " + pColumn.getName() + " was already added to the key.");
      }
      columns.add(pColumn);
	}

   public void addColumn(Column.Name pName) {
      Column column = getTable().getColumn(pName);
      if (column == null) {
         throw new NullPointerException("The table " + getTable().getName() + " doesn't contain a column " + pName);
      }
      addColumn(column);
   }

   public void addColumn(String pName) {
      addColumn(new ColumnImpl.NameImpl(pName));
   }

	public boolean isUnique() {
      return unique;
	}

	public boolean isPrimaryKey() {
      return primaryKey;
	}

   public Iterator getColumns() {
      return columns.iterator();
   }
}
