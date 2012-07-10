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
import org.apache.ws.jaxme.sqls.ColumnReference;
import org.apache.ws.jaxme.sqls.DeleteStatement;
import org.apache.ws.jaxme.sqls.ForeignKey;
import org.apache.ws.jaxme.sqls.Index;
import org.apache.ws.jaxme.sqls.InsertStatement;
import org.apache.ws.jaxme.sqls.SelectStatement;
import org.apache.ws.jaxme.sqls.Table;
import org.apache.ws.jaxme.sqls.UpdateStatement;
import org.apache.ws.jaxme.sqls.Column.Type;


public class ViewImpl extends TableImpl {
    private class ViewColumnImpl implements Column {
        private final Column.Name colName;
        private final Column col;
        private Object data;
        private ViewColumnImpl(ColumnReference pColumn) {
            col = pColumn.getColumn();
            Column.Name alias = pColumn.getAlias();
            colName = alias == null ? col.getName() : alias;
        }
        public Table getTable() { return ViewImpl.this; }
        public Name getName() { return colName; }
        public String getQName() { return getTable() + "." + getName(); }
        public Type getType() { return col.getType(); }
        public boolean isPrimaryKeyPart() { return false; }
        public void setNullable(boolean pNullable) {
            throw new IllegalStateException("Unable to set a view columns 'Nullable' property.");
        }
        public boolean isNullable() { return col.isNullable(); }
        public boolean isStringColumn() { return col.isStringColumn(); }
        public boolean isBinaryColumn() { return col.isBinaryColumn(); }
        public void setCustomData(Object pData) { data = pData; }
        public Object getCustomData() { return data; }
        public boolean isVirtual() { return false; }
    }

    private final SelectStatement stmt;

	protected ViewImpl(SelectStatement pSelectStatement, Table.Name pName) {
        super(pSelectStatement.getSelectTableReference().getTable().getSchema(),
              pName == null ? pSelectStatement.getTableReference().getTable().getName() : pName);
        stmt = pSelectStatement;
    }

    public Iterator getColumns() {
        List result = new ArrayList();
        for (Iterator iter = stmt.getResultColumns();  iter.hasNext();  ) {
            ColumnReference col = (ColumnReference) iter.next();
            result.add(new ViewColumnImpl(col));
        }
        return result.iterator();
    }

    public Column newColumn(Column.Name pName, Type pType) {
        throw new IllegalStateException("A views columns cannot be changed.");
    }

    public Column newColumn(String pName, Type pType) {
        throw new IllegalStateException("A views columns cannot be changed.");
    }

    public Column getColumn(Column.Name pName) {
        if (pName == null) {
            throw new NullPointerException("Column names must not be null.");
         }
        for (Iterator iter = stmt.getResultColumns();  iter.hasNext();  ) {
            ColumnReference col = (ColumnReference) iter.next();
            Column.Name alias = col.getAlias();
            if (alias == null) {
            	alias = col.getColumn().getName();
            }
            if (alias.equals(pName)) {
                return new ViewColumnImpl(col);
            }
        }
        return null;
    }

    public Index newKey() {
        throw new IllegalStateException("A view cannot have keys.");
    }
    public Index newIndex() {
        throw new IllegalStateException("A view cannot have indexes.");
    }
    public Index newPrimaryKey() {
        throw new IllegalStateException("A view cannot have a primary key.");
    }
    public ForeignKey newForeignKey(Table pReferencedTable) {
        throw new IllegalStateException("A view cannot have foreign keys.");
    }
    public InsertStatement getInsertStatement() {
        throw new IllegalStateException("A view is not updateable.");
    }
    public UpdateStatement getUpdateStatement() {
        throw new IllegalStateException("A view is not updateable.");
    }
    public DeleteStatement getDeleteStatement() {
        throw new IllegalStateException("A view is not updateable.");
    }
    public Index getPrimaryKey() {
        throw new IllegalStateException("A view cannot have a primary key.");
    }
    public Iterator getIndexes() {
        throw new IllegalStateException("A view cannot have indexes.");
    }
    public Iterator getForeignKeys() {
        throw new IllegalStateException("A view cannot have foreign keys.");
    }

    public SelectStatement getViewStatement() {
        return stmt;
    }
}
