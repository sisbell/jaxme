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

import org.apache.ws.jaxme.sqls.Column;
import org.apache.ws.jaxme.sqls.ColumnReference;
import org.apache.ws.jaxme.sqls.TableReference;


/** <p>Implementation of a {@link ColumnReference}.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class ColumnReferenceImpl implements ColumnReference {
    private TableReference tableReference;
    private Column column;
    private Column.Name alias;

	protected ColumnReferenceImpl(TableReference pTableReference, Column pColumn) {
	    if (pTableReference == null) {
	        throw new NullPointerException("The referenced table must not be null.");
	    }
	    if (pColumn == null) {
	        throw new NullPointerException("The referenced column must not be null.");
	    }
	    if (!pTableReference.getTable().equals(pColumn.getTable())) {
	        throw new IllegalStateException("The columns table is not the referenced table.");
	    }
	    tableReference = pTableReference;
	    column = pColumn;
	}

	public TableReference getTableReference() {
      return tableReference;
	}

	public Column getColumn() {
      return column;
	}

	public void setAlias(String pName) {
      setAlias(new ColumnImpl.NameImpl(pName));
	}

	public void setAlias(Column.Name pName) {
      alias = pName;
	}

	public Column.Name getAlias() {
      return alias;
	}

   public boolean equals(Object o) {
      if (o == null  ||  !(o instanceof ColumnReference)) {
         return false;
      }
      ColumnReference ref = (ColumnReference) o;
	  Column c1 = ref.getColumn();
	  Column c2 = getColumn();
	  if (c1 == null  ||  c2 == null) {
		  return super.equals(o);
	  }
	  TableReference t1 = ref.getTableReference();
	  TableReference t2 = ref.getTableReference();
	  if (t1 == null  ||  t2 == null) {
		  return super.equals(o);
	  }
      return t1.equals(t2)  &&  c1.equals(c2);
   }

   public int hashCode() {
      return getTableReference().hashCode() + getColumn().hashCode();
   }
}
