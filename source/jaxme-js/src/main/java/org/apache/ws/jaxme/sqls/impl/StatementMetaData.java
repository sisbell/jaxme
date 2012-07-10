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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.ws.jaxme.sqls.BooleanConstraint;
import org.apache.ws.jaxme.sqls.Case;
import org.apache.ws.jaxme.sqls.Column;
import org.apache.ws.jaxme.sqls.ColumnReference;
import org.apache.ws.jaxme.sqls.CombinedConstraint;
import org.apache.ws.jaxme.sqls.DeleteStatement;
import org.apache.ws.jaxme.sqls.Expression;
import org.apache.ws.jaxme.sqls.Function;
import org.apache.ws.jaxme.sqls.InsertStatement;
import org.apache.ws.jaxme.sqls.JoinReference;
import org.apache.ws.jaxme.sqls.Parts;
import org.apache.ws.jaxme.sqls.RawSQLCode;
import org.apache.ws.jaxme.sqls.SelectStatement;
import org.apache.ws.jaxme.sqls.SetStatement;
import org.apache.ws.jaxme.sqls.Table;
import org.apache.ws.jaxme.sqls.TableReference;
import org.apache.ws.jaxme.sqls.UpdateStatement;
import org.apache.ws.jaxme.sqls.Value;


public class StatementMetaData {
    public static class LocalData {
        private boolean hasWhereClause;
        
        public boolean hasWhereClause() {
            return hasWhereClause;
        }

        public void setWhereClause(boolean pHasWhereClause) {
            hasWhereClause = pHasWhereClause;
        }
    }

    private final Map aliases = new HashMap();
    private final Map columnNames = new HashMap();

    private final List tables = new ArrayList();

    public StatementMetaData(DeleteStatement pQuery) {
        addTable(pQuery.getTableReference());
        addCombinedConstraint(pQuery.getWhere());
        createTableAliases();
        createColumnNames();
    }

    public StatementMetaData(UpdateStatement pQuery, ColumnReference[] pColumns) {
        addSetStatement(pQuery, pColumns);
        addCombinedConstraint(pQuery.getWhere());
        createTableAliases();
        createColumnNames();
    }

    public StatementMetaData(InsertStatement pQuery, ColumnReference[] pColumns) {
    	addSetStatement(pQuery, pColumns);
        SelectStatement subSelect = pQuery.getSubSelect();
        if (subSelect != null) {
        	addSelectStatement(subSelect);
        }
        createTableAliases();
        createColumnNames();
    }

    public StatementMetaData(SelectStatement pQuery) {
      addSelectStatement(pQuery);
      createTableAliases();
      createColumnNames();
    }

    protected void addSetStatement(SetStatement pQuery, ColumnReference[] pColumns) {
        addTable(pQuery.getTableReference());
        for (int i = 0;  i < pColumns.length;  i++) {
            addColumn(pColumns[i]);
        }
        for (Iterator iter = pQuery.getSetValues();  iter.hasNext();  ) {
            SetStatement.SetValue setValue = (SetStatement.SetValue) iter.next();
            addPart(setValue.getValue());
            addPart(setValue.getColumnReference());
        }
    }

    protected void addTable(TableReference pTableReference) {
        Table t = pTableReference.getTable();
        Table.Name alias = pTableReference.getAlias();
        if (alias != null) {
            if (aliases.containsKey(alias.getName())) {
                throw new IllegalStateException("The alias " + alias +
                        " is used twice ");
            }
            aliases.put(alias.getName(), pTableReference);
        }
        tables.add(pTableReference);
        if (t instanceof ViewImpl) {
            ViewImpl v = (ViewImpl) t;
            addSelectStatement(v.getViewStatement());
        }
            
        if (pTableReference instanceof JoinReference) {
            addCombinedConstraint(((JoinReference) pTableReference).getOn());
        }
    }

    protected void addSelectStatement(SelectStatement pQuery) {
        for (Iterator tableIter = pQuery.getSelectTableReferences();  tableIter.hasNext();  ) {
            TableReference ref = (TableReference) tableIter.next();
            addTable(ref);
        }
        addCombinedConstraint(pQuery.getWhere());
        for (Iterator iter = pQuery.getResultColumns();  iter.hasNext();  ) {
            addPart(iter.next());
        }
        for (Iterator iter = pQuery.getOrderColumns();  iter.hasNext();  ) {
        	SelectStatement.OrderColumn orderColumn = (SelectStatement.OrderColumn) iter.next();
            addPart(orderColumn.getColumn());
        }
    }

    protected void addColumn(ColumnReference pColumn) {
        if (pColumn instanceof VirtualColumn) {
            VirtualColumn vc = (VirtualColumn) pColumn;
            Object o = vc.getValue();
            if (o instanceof SelectStatement) {
                addSelectStatement((SelectStatement) o);
            } else if (o instanceof Function) {
                addParts((Function) o);
            } else if (o instanceof String) {
                // Do nothing
            } else {
                throw new IllegalStateException("Invalid type of VirtualColumn: " + o);
            }
            addColumnName(vc.getName());
        }
    }

    private void addColumnName(Column.Name pName) {
        String key = pName.toString().toUpperCase();
        Integer num = (Integer) columnNames.get(key);
        if (num == null) {
            num = new Integer(1);
        } else {
            num = new Integer(num.intValue() + 1);
        }
        columnNames.put(key, num);
    }

    protected void addCombinedConstraint(CombinedConstraint pConstraint) {
      for (Iterator iter = pConstraint.getParts();  iter.hasNext();  ) {
        Object o = iter.next();
        if (o instanceof CombinedConstraint) {
          addCombinedConstraint((CombinedConstraint) o);
        } else if (o instanceof BooleanConstraint) {
          addBooleanConstraint((BooleanConstraint) o);
        } else {
          throw new IllegalStateException("Invalid part type in CombinedConstraint: " + o);
        }
      }
    }

    protected void addPart(Object pPart) {
        if (pPart instanceof SelectStatement) {
            addSelectStatement((SelectStatement) pPart);
        } else if (pPart instanceof CombinedConstraint) {
            addCombinedConstraint((CombinedConstraint) pPart);
        } else if (pPart instanceof Function) {
            addParts((Function) pPart);
        } else if (pPart instanceof Expression) {
			addParts((Expression) pPart);
        } else if (pPart instanceof ColumnReference
                ||  pPart instanceof Value
                ||  pPart instanceof RawSQLCode) {
            // Ignore me
        } else if (pPart instanceof Case) {
            Case casePart = (Case) pPart;
            addPart(casePart.getCheckedValue());
            Object o = casePart.getElseValue();
            if (o != null) {
            	addPart(o);
            }
            Case.When[] whens = casePart.getWhens();
            for (int i = 0;  i < whens.length;  i++) {
            	Case.When when = whens[i];
            	addPart(when.getCondition());
                addPart(when.getValue());
            }
        } else if (pPart.getClass().isArray()) {
            Object[] o = (Object[]) pPart;
            for (int i = 0;  i < o.length;  i++) {
            	addPart(o[i]);
            }
        } else {
            throw new IllegalStateException("Invalid part type: " + pPart);
        }
    }

    protected void addParts(Parts pParts) {
        for (Iterator iter = pParts.getParts();  iter.hasNext();  ) {
            addPart(iter.next());
        }
    }

    protected void addBooleanConstraint(BooleanConstraint pConstraint) {
      BooleanConstraint.Type type = pConstraint.getType();
      if (BooleanConstraint.Type.EQ.equals(type)
          ||  BooleanConstraint.Type.EXISTS.equals(type)
          ||  BooleanConstraint.Type.GE.equals(type)
          ||  BooleanConstraint.Type.GT.equals(type)
          ||  BooleanConstraint.Type.IN.equals(type)
          ||  BooleanConstraint.Type.ISNULL.equals(type)
          ||  BooleanConstraint.Type.LE.equals(type)
          ||  BooleanConstraint.Type.LIKE.equals(type)
          ||  BooleanConstraint.Type.LT.equals(type)
          ||  BooleanConstraint.Type.NE.equals(type)
          ||  BooleanConstraint.Type.BETWEEN.equals(type)) {
        addParts(pConstraint);
      } else {
        throw new IllegalStateException("Invalid part type in BooleanConstraint: " + type);
      }
    }

    protected String getUniqueAlias(String pSuggestion, Map pAliases) {
        String prefix;
        if (pSuggestion == null) {
            prefix = "";
        } else {
            prefix = pSuggestion;
        }
        if (!pAliases.containsKey(prefix)) {
            return prefix;
        }
        for (char c = '0';  c <= '9';  c++) {
            String s = prefix + c;
            if (!pAliases.containsKey(s)) {
                return s;
            }
        }
        for (char c = 'A';  c <= 'Z';  c++) {
            String s = prefix + c;
            if (!pAliases.containsKey(s)) {
                return s;
            }
        }
        return getUniqueAlias(prefix + '0', pAliases);
    }

    protected void createTableAliases() {
        if (tables.size() > 1) {
            // Make sure that all tables have an alias
            for (Iterator iter = tables.iterator();  iter.hasNext();  ) {
                TableReference tableReference = (TableReference) iter.next();
                if (tableReference.getAlias() == null) {
                    String alias = getUniqueAlias(tableReference.getTable().getName().getName(), aliases);
                    aliases.put(alias, tableReference);
                    if (!alias.equals(tableReference.getTable().getName().getName())) {
                        tableReference.setAlias(alias);
                    }
                }
            }
        }
    }

    protected void createColumnNames() {
      // Create a Map of all column names, that may be referenced.
      // maps key is the column name, and the maps value is the
      // number of possible references. In other words: If an entry
      // in the map has a value > 1, then its column name must be
      // qualified, because it is used in multiple tables.
      for (int i = 0;  i < tables.size();  i++) {
        TableReference table = (TableReference) tables.get(i);
        for (Iterator iter = table.getTable().getColumns();  iter.hasNext();  ) {
          Column col = (Column) iter.next();
          addColumnName(col.getName());
        }
      }
    }

    public Map getColumnNames() {
      return columnNames;
    }
  }