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

import org.apache.ws.jaxme.sqls.ColumnReference;
import org.apache.ws.jaxme.sqls.SQLFactory;
import org.apache.ws.jaxme.sqls.SelectStatement;
import org.apache.ws.jaxme.sqls.SelectTableReference;
import org.apache.ws.jaxme.sqls.Table;
import org.apache.ws.jaxme.sqls.TableReference;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class SelectStatementImpl extends ConstrainedStatementImpl implements SelectStatement {
   public static class OrderColumnImpl implements SelectStatement.OrderColumn {
      private final Object columnReference;
      private final boolean descending;
      public OrderColumnImpl(Object pColumnReference, boolean pDescending) {
         columnReference = pColumnReference;
         descending = pDescending;
      }
      public Object getColumn() {
         return columnReference;
      }
      public boolean isDescending() {
         return descending;
      }
   }

   private List orderColumns = new ArrayList();
   private List resultColumns = new ArrayList();
   private boolean distinct;
   private int maxRows, skippedRows;

	/** <p>Creates a new SelectStatement and sets the creating
   * {@link org.apache.ws.jaxme.sqls.SQLFactory}.</p>
	 */
	public SelectStatementImpl(SQLFactory pFactory) {
		super(pFactory);
	}

	public void addOrderColumn(Object pColumn) {
      addOrderColumn(pColumn, false);
	}

	public void addOrderColumn(Object pColumn, boolean pDescending) {
      addOrderColumn(new OrderColumnImpl(pColumn, pDescending));
	}

	public void addOrderColumn(SelectStatement.OrderColumn pColumn) {
	    Object o = pColumn.getColumn();
	    if (o instanceof ColumnReference) {
		    for (Iterator iter = orderColumns.iterator();  iter.hasNext();  ) {
		        SelectStatement.OrderColumn column = (SelectStatement.OrderColumn) iter.next();
		        Object p = column.getColumn();
		        if (p instanceof ColumnReference) {
		            if (o.equals(p)) {
		                throw new NullPointerException("The column "
		                        					   + ((ColumnReference) o).getColumn().getName()
		                        					   + " is  already used for sorting.");
		            }
		        }
		    }
	    }
	    orderColumns.add(pColumn);
	}

	public Iterator getOrderColumns() {
	    return orderColumns.iterator();
	}

	public void addResultColumn(ColumnReference pColumn) {
      resultColumns.add(pColumn);
	}

   public Iterator getResultColumns() {
      return resultColumns.iterator();
   }

	public void setDistinct(boolean pDistinct) {
      distinct = pDistinct;
	}

	public boolean isDistinct() {
      return distinct;
	}

	public void setMaxRows(int pMaxRows) {
      maxRows = pMaxRows;
	}

	public int getMaxRows() {
      return maxRows;
	}

	public void setSkippedRows(int pSkippedRows) {
      skippedRows = pSkippedRows;
	}

	public int getSkippedRows() {
      return skippedRows;
	}

   protected TableReference newTableReference(Table pTable) {
      return new SelectTableReferenceImpl(this, pTable);
   }

   public SelectTableReference getSelectTableReference() {
      return (SelectTableReference) getTableReference();
   }

   public Iterator getSelectTableReferences() {
      return new Iterator() {
         SelectTableReference reference = getSelectTableReference();
         public boolean hasNext() { return reference != null; }
         public Object next() {
            SelectTableReference result = reference;
            reference = result.getRightJoinedTableReference();
            return result;
         }
         public void remove() {
            throw new UnsupportedOperationException();
         }
      };
   }

	public Table createView(Table.Name pName) {
	    return getSQLFactory().getObjectFactory().newView(this, pName);
	}

	public Table createView(String pName) {
	    return createView(pName == null ? null : new TableImpl.NameImpl(pName));
	}
}
