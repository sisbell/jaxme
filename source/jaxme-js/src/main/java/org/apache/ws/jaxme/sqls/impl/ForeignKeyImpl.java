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
import org.apache.ws.jaxme.sqls.ForeignKey;
import org.apache.ws.jaxme.sqls.Table;


/** <p>Implementation of a foreign key.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class ForeignKeyImpl extends ColumnSetImpl implements ForeignKey {
  public static class ColumnReferenceImpl implements ForeignKey.ColumnLink {
    private final Column localColumn, referencedColumn; 
    public ColumnReferenceImpl(Column pLocalColumn, Column pReferencedColumn) {
      if (pLocalColumn == null) {
         throw new NullPointerException("The local column must not be null.");
      }
      if (pReferencedColumn == null) {
        throw new NullPointerException("The referenced column must not be null.");
      }
      localColumn = pLocalColumn;
      referencedColumn = pReferencedColumn;
    }
    public Column getLocalColumn() {
      return localColumn;
    }
    public Column getReferencedColumn() {
      return referencedColumn;
    }
  }

  private Table referencedTable;
  private ForeignKey.Mode onDelete, onUpdate;
  public List references = new ArrayList();

  protected ForeignKeyImpl(Table pTable, Table pReferencedTable) {
    super(pTable);
    if (pReferencedTable == null) {
      throw new NullPointerException("The referenced table must not be null.");
    }
    referencedTable = pReferencedTable;
  }

	public Table getReferencedTable() {
      return referencedTable;
	}

	public void setOnDelete(ForeignKey.Mode pMode) {
      onDelete = pMode;
	}

	public ForeignKey.Mode getOnDelete() {
      return onDelete;
	}

	public void setOnUpdate(ForeignKey.Mode pMode) {
      onUpdate = pMode;
	}

	public ForeignKey.Mode getOnUpdate() {
      return onUpdate;
	}

	public void addColumnLink(Column pColumn, Column pReferencedColumn) {
    addColumnReference(new ColumnReferenceImpl(pColumn, pReferencedColumn));
  }

  public void addColumnReference(ForeignKey.ColumnLink pReference) {
    Column localColumn = pReference.getLocalColumn();
    if (!getTable().equals(localColumn.getTable())) {
      throw new IllegalStateException("The local column " + localColumn.getQName() +
                                       " is not from the local table " + getTable().getQName());
    }
    Column referencedColumn = pReference.getReferencedColumn();
    if (!getReferencedTable().equals(referencedColumn.getTable())) {
      throw new IllegalStateException("The referenced column " + referencedColumn.getQName() +
                                       " is not from the referenced table " + getReferencedTable().getQName());
    }
    references.add(pReference);
	}

	public void addColumnLink(Column.Name pName, Column.Name pReferencedName) {
      Column localColumn = getTable().getColumn(pName);
      if (localColumn == null) {
        throw new NullPointerException("The local table " + getTable().getQName() +
                                        " doesn't contain a column " + pName);
      }
      Column referencedColumn = getReferencedTable().getColumn(pReferencedName);
      if (referencedColumn == null) {
         throw new NullPointerException("The referenced table " + getReferencedTable().getName() +
                                         " doesn't contain a column " + pReferencedName);
      }
      addColumnLink(localColumn, referencedColumn);
	}

	public void addColumnLink(String pName, String pReferencedName) {
      addColumnLink(new ColumnImpl.NameImpl(pName), new ColumnImpl.NameImpl(pReferencedName));
	}

   public Iterator getColumnLinks() {
      return references.iterator();
   }

	public Iterator getColumns() {
      return new Iterator(){
         Iterator inner = getColumnLinks();
         public boolean hasNext() { return inner.hasNext(); }
         public void remove() { inner.remove(); }
         public Object next() {
            ForeignKey.ColumnLink link = (ForeignKey.ColumnLink) inner.next();
            return link.getLocalColumn();
         }
      };
	}

   public ColumnSet getReferencedColumns() {
     return new ColumnSet(){
       public Table getTable() { return getReferencedTable(); }
       public Iterator getColumns() {
         return new Iterator() {
            private Iterator inner = getColumnLinks();
            public Object next() {
              return ((ColumnLink) inner.next()).getReferencedColumn();
            }
            public boolean hasNext() { return inner.hasNext(); }
            public void remove() { inner.remove(); }
         };
       }
     };
   }
}
