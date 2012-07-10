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
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.ws.jaxme.sqls.BinaryColumn;
import org.apache.ws.jaxme.sqls.BooleanConstraint;
import org.apache.ws.jaxme.sqls.Case;
import org.apache.ws.jaxme.sqls.Column;
import org.apache.ws.jaxme.sqls.ColumnReference;
import org.apache.ws.jaxme.sqls.CombinedConstraint;
import org.apache.ws.jaxme.sqls.Constraint;
import org.apache.ws.jaxme.sqls.DeleteStatement;
import org.apache.ws.jaxme.sqls.Expression;
import org.apache.ws.jaxme.sqls.ForeignKey;
import org.apache.ws.jaxme.sqls.Function;
import org.apache.ws.jaxme.sqls.Index;
import org.apache.ws.jaxme.sqls.RawSQLCode;
import org.apache.ws.jaxme.sqls.SQLGenerator;
import org.apache.ws.jaxme.sqls.InsertStatement;
import org.apache.ws.jaxme.sqls.JoinReference;
import org.apache.ws.jaxme.sqls.Schema;
import org.apache.ws.jaxme.sqls.SelectStatement;
import org.apache.ws.jaxme.sqls.SelectTableReference;
import org.apache.ws.jaxme.sqls.SetStatement;
import org.apache.ws.jaxme.sqls.Statement;
import org.apache.ws.jaxme.sqls.StringColumn;
import org.apache.ws.jaxme.sqls.Table;
import org.apache.ws.jaxme.sqls.TableReference;
import org.apache.ws.jaxme.sqls.UpdateStatement;
import org.apache.ws.jaxme.sqls.Value;


/** <p>Default implementation of an SQL generator.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class SQLGeneratorImpl implements SQLGenerator {
    private String statementTerminator, lineTerminator;
    
    public String getStatementTerminator() {
        return statementTerminator;
    }
    
    public void setStatementTerminator(String pStatementTerminator) {
        statementTerminator = pStatementTerminator;
    }
    
    public String getLineTerminator() {
        return lineTerminator;
    }
    
    public void setLineTerminator(String pLineTerminator) {
        lineTerminator = pLineTerminator;
    }
    
    protected String newStatement(String pStatement) {
        String s = getStatementTerminator();
        return s == null ? pStatement : pStatement + s;
    }
    
    public Collection getCreate(Schema pSchema) {
        if (pSchema.getName() == null) {
            // Default schema cannot be created
            return Collections.EMPTY_LIST;
        }
        List result = new ArrayList();
        result.add(newStatement("CREATE SCHEMA " + pSchema.getName()));
        return result;
    }
    
    public Collection getDrop(Schema pSchema) {
        if (pSchema.getName() == null) {
            // Default schema cannot be dropped
            return Collections.EMPTY_LIST;
        }
        List result = new ArrayList();
        result.add(newStatement("DROP SCHEMA " + pSchema.getName()));
        return result;
    }
    
    protected String getIndent() { return "  "; }
    
    protected String getTypeName(Column.Type pType) {
        if (pType.equals(Column.Type.BIGINT)) {
            return "BIGINT";
        } else if (pType.equals(Column.Type.BINARY)) {
            return "BINARY";
        } else if (pType.equals(Column.Type.BIT)) {
            return "BIT";
        } else if (pType.equals(Column.Type.CHAR)) {
            return "CHAR";
        } else if (pType.equals(Column.Type.DATE)) {
            return "DATE";
        } else if (pType.equals(Column.Type.DOUBLE)) {
            return "DOUBLE";
        } else if (pType.equals(Column.Type.FLOAT)) {
            return "FLOAT";
        } else if (pType.equals(Column.Type.INTEGER)) {
            return "INT";
        } else if (pType.equals(Column.Type.SMALLINT)) {
            return "SMALLINT";
        } else if (pType.equals(Column.Type.TIME)) {
            return "TIME";
        } else if (pType.equals(Column.Type.TIMESTAMP)) {
            return "TIMESTAMP";
        } else if (pType.equals(Column.Type.TINYINT)) {
            return "TINYINT";
        } else if (pType.equals(Column.Type.VARBINARY)) {
            return "VARBINARY";
        } else if (pType.equals(Column.Type.VARCHAR)) {
            return "VARCHAR";
        } else if (pType.equals(Column.Type.CLOB)) {
            return "CLOB";
        } else if (pType.equals(Column.Type.BLOB)) {
            return "BLOB";
        } else if (pType.equals(Column.Type.OTHER)) {
            return "BLOB";
        } else {
            throw new IllegalStateException("Unknown column type: " + pType);
        }
    }
    
    protected String getCreate(Column pColumn) {
        StringBuffer sb = new StringBuffer();
        sb.append(pColumn.getName()).append(" ");
        Column.Type type = pColumn.getType();
        Long length = null;
        if (type.equals(Column.Type.BINARY)
                ||  type.equals(Column.Type.VARBINARY)
                ||  type.equals(Column.Type.BLOB)
                ||  type.equals(Column.Type.OTHER)) {
            length = ((BinaryColumn) pColumn).getLength();
            if (length == null) {
                throw new IllegalStateException("The length of column " + pColumn.getQName() + " is not set.");
            }
        } else if (type.equals(Column.Type.CHAR)
                ||  type.equals(Column.Type.VARCHAR)
                ||  type.equals(Column.Type.CLOB)) {
            length = ((StringColumn) pColumn).getLength();
            if (length == null) {
                throw new IllegalStateException("The length of column " + pColumn.getQName() + " is not set."); 
            }
        }
        sb.append(getTypeName(type));
        if (length != null) {
            sb.append("(").append(length).append(")");
        }
        if (!pColumn.isNullable()) {
            sb.append(" NOT NULL");
        }
        return sb.toString();
    }
    
    /** <p>Returns whether the primary key requires special handling
     * (in which case {@link #isPrimaryKeyPartOfCreateTable()} and
     * {@link #createPrimaryKeyAsPartOfCreateTable(Table)} are used)
     * or nor (in which case {@link #isUniqueIndexPartOfCreateTable()}
     * and {@link #createIndexAsPartOfCreateTable(Index)} apply).</p>
     */
    protected boolean isPrimaryKeyUniqueIndex() { return false; }
    
    /** <p>Returns whether a <code>CREATE TABLE</code> statement may contain
     * a <code>PRIMARY KEY</code> clause.</p>
     */
    protected boolean isPrimaryKeyPartOfCreateTable() { return false; }
    
    /** <p>Returns whether a <code>CREATE TABLE</code> statement may contain
     * a <code>UNIQUE</code> clause.</p>
     */
    protected boolean isUniqueIndexPartOfCreateTable() { return false; }
    
    /** <p>Returns whether a <code>CREATE TABLE</code> statement may contain
     * an <code>INDEX</code> clause.</p>
     */
    protected boolean isNonUniqueIndexPartOfCreateTable() { return false; }
    
    /** <p>Returns whether a <code>CREATE TABLE</code> statement may contain
     * a <code>FOREIGN KEY</code> clause.</p>
     */
    protected boolean isForeignKeyPartOfCreateTable() { return false; }
    
    protected String createPrimaryKeyAsPartOfCreateTable(Table pTable) {
        Index index = pTable.getPrimaryKey();
        if (index == null) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        sb.append("PRIMARY KEY").append(" (");
        boolean first = true;
        for (Iterator iter = index.getColumns();  iter.hasNext();  ) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }
            sb.append(((Column) iter.next()).getName().getName());
        }
        sb.append(")");
        return sb.toString();
    }
    
    protected String createIndexAsPartOfCreateTable(Index pIndex) {
        StringBuffer sb = new StringBuffer();
        sb.append(pIndex.isUnique() ? "UNIQUE" : "KEY").append(" (");
        boolean first = true;
        for (Iterator iter = pIndex.getColumns();  iter.hasNext();  ) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }
            sb.append(((Column) iter.next()).getName().getName());
        }
        sb.append(")");
        return sb.toString();
    }
    
    protected String createForeignKeyAsPartOfCreateTable(ForeignKey pKey) {
        StringBuffer sb = new StringBuffer();
        sb.append("FOREIGN KEY (");
        boolean first = true;
        Iterator iter = pKey.getColumnLinks();
        if (!iter.hasNext()) {
            throw new IllegalStateException("Foreign key on " +
                    pKey.getTable().getQName() +
                    " referencing " +
                    pKey.getReferencedTable().getQName() +
            " doesn't have any columns.");
        }
        while (iter.hasNext()) {
            ForeignKey.ColumnLink link = (ForeignKey.ColumnLink) iter.next();
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }
            sb.append(link.getLocalColumn().getName().getName());
        }
        sb.append(") REFERENCES ");
        sb.append(pKey.getReferencedTable().getQName());
        sb.append(" (");
        first = true;
        iter = pKey.getColumnLinks();
        while (iter.hasNext()) {
            ForeignKey.ColumnLink link = (ForeignKey.ColumnLink) iter.next();
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }
            sb.append(link.getReferencedColumn().getName().getName());
        }
        sb.append(")");
        
        ForeignKey.Mode deleteMode = pKey.getOnDelete();
        if (deleteMode != null) {
            if (ForeignKey.Mode.CASCADE.equals(deleteMode)) {
                sb.append(" ON DELETE CASCADE");
            } else if (ForeignKey.Mode.REJECT.equals(deleteMode)) {
                sb.append(" ON DELETE RESTRICT");
            } else if (ForeignKey.Mode.SETNULL.equals(deleteMode)) {
                sb.append(" ON DELETE SET NULL");
            } else {
                throw new IllegalStateException("Unknown foreign key mode for ON  DELETE: " + deleteMode);
            }
        }
        ForeignKey.Mode updateMode = pKey.getOnUpdate();
        if (updateMode != null) {
            if (ForeignKey.Mode.CASCADE.equals(updateMode)) {
                sb.append(" ON UPDATE CASCADE");
            } else if (ForeignKey.Mode.REJECT.equals(updateMode)) {
                sb.append(" ON UPDATE RESTRICT");
            } else {
                throw new IllegalStateException("Unknown foreign key mode for ON UPDATE: " + updateMode);
            }
        }
        
        return sb.toString();
    }
    
    protected String getCreateTableHeader(Table pTable) {
        return "CREATE TABLE " + pTable.getQName();
    }
    
    public Collection getCreate(Table pTable) {
        String lf = getLineTerminator() == null ? "" : getLineTerminator();
        String indent = lf == null ? "" : getIndent();
        StringBuffer sb = new StringBuffer();
        sb.append(getCreateTableHeader(pTable)).append(" (");
        String s = lf + indent;
        for (Iterator iter = pTable.getColumns();  iter.hasNext();  ) {
            sb.append(s).append(getCreate((Column) iter.next()));
            s = "," + lf + indent;
        }
        
        for (Iterator iter = pTable.getIndexes();  iter.hasNext();  ) {
            Index index = (Index) iter.next();
            String st;
            if (index.isPrimaryKey()  &&  !isPrimaryKeyUniqueIndex()) {
                if (!isPrimaryKeyPartOfCreateTable()) {
                    continue;
                }
                st = createPrimaryKeyAsPartOfCreateTable(pTable);
            } else if (index.isUnique()) {
                if (!isUniqueIndexPartOfCreateTable()) {
                    continue;
                }
                st = createIndexAsPartOfCreateTable(index);
            } else {
                if (!isNonUniqueIndexPartOfCreateTable()) {
                    continue;
                }
                st = createIndexAsPartOfCreateTable(index);
            }
            if (st != null) {
                sb.append(s).append(st);
            }
        }
        if (isForeignKeyPartOfCreateTable()) {
            for (Iterator iter = pTable.getForeignKeys();  iter.hasNext();  ) {
                ForeignKey key = (ForeignKey) iter.next();
                String st = createForeignKeyAsPartOfCreateTable(key);
                if (st != null) {
                    sb.append(s).append(st);
                }
            }
        }
        sb.append(lf).append(")").append(lf);
        List result = new ArrayList();
        result.add(newStatement(sb.toString()));
        return result;
    }
    
    public Collection getDrop(Table pTable) {
        List result = new ArrayList();
        result.add(newStatement("DROP TABLE " + pTable.getQName()));
        return result;
    }

    private ColumnReference[] getSetStatementsColumns(SetStatement pStatement) {
        List result = new ArrayList();
        for (Iterator iter = pStatement.getSetValues();  iter.hasNext();  ) {
            SetStatement.SetValue setValue = (SetStatement.SetValue) iter.next();
            result.add(setValue.getColumnReference());
        }
        return (ColumnReference[]) result.toArray(new ColumnReference[result.size()]);
    }

    private ColumnReference[] getInsertStatementsColumns(InsertStatement pStatement) {
        SelectStatement subSelect = pStatement.getSubSelect();
        if (subSelect == null) {
            return getSetStatementsColumns(pStatement);
        } else {
        	List result = new ArrayList();
            for (Iterator iter = subSelect.getResultColumns();  iter.hasNext();  ) {
        		ColumnReference cRef = (ColumnReference) iter.next();
        		Column.Name name;
        		if (cRef.getAlias() == null) {
                    name = cRef.getColumn().getName();
        		} else {
                    name = cRef.getAlias();
        		}
        		Column col = pStatement.getTableReference().getTable().getColumn(name);
        		if (col == null) {
        			throw new IllegalStateException("A result column " + name
        					+ " is used in the subselect, which is not present in the insert statements table.");
        		}
        		result.add(pStatement.getTableReference().newColumnReference(col));
        	}
            return (ColumnReference[]) result.toArray(new ColumnReference[result.size()]);
        }
    }

    private void addSetValuesToInsertStatement(StringBuffer pBuffer,
                                               StatementMetaData pData,
                                               InsertStatement pQuery) {
        pBuffer.append(" VALUES (");
        boolean first = true;
        for (Iterator iter = pQuery.getSetValues();  iter.hasNext();  ) {
        	if (first) {
        		first = false;
        	} else {
        		pBuffer.append(", ");
        	}
        	SetStatement.SetValue setValue = (SetStatement.SetValue) iter.next();
            Object o = setValue.getValue();
            String s = getBooleanConstraintPart(pData, o);
        	pBuffer.append(s);
        }
        pBuffer.append(")");
    }

    private void addSubSelectToInsertStatement(StringBuffer pBuffer, StatementMetaData pData,
                                               InsertStatement pStatement) {
        pBuffer.append(" (");
        pBuffer.append(getSelectQuery(pStatement.getSubSelect(), pData));
        pBuffer.append(")");
    }

    public String getInsertQuery(InsertStatement pQuery) {
        ColumnReference[] columns = getInsertStatementsColumns(pQuery);
        StatementMetaData smd = newStatementMetaData(pQuery, columns);
        
        StringBuffer result = new StringBuffer();
        result.append("INSERT INTO ");
        result.append(getTableAlias(smd, pQuery.getTableReference()));
        if (columns.length > 0) {
            result.append(" (");
            for (int i = 0;  i < columns.length;  i++) {
            	if (i > 0) {
            		result.append(", ");
                }
                result.append(getColumnAlias(smd, columns[i]));
            }
            result.append(")");
        }
        boolean hasSetValues = pQuery.getSetValues().hasNext();
        boolean hasSubselect = pQuery.getSubSelect() != null;
        if (hasSetValues) {
            if (hasSubselect) {
            	throw new IllegalStateException("Using values (InsertStatement.addFoo(..)) and subselects (InsertStatement.setSubSelect(...)) is mutually exclusive.");
            } else {
                addSetValuesToInsertStatement(result, smd, pQuery);
            }
        } else {
            if (hasSubselect) {
                addSubSelectToInsertStatement(result, smd, pQuery);
            } else {
            	throw new IllegalStateException("Neither values (InsertStatement.addFoo(..)) nor a subselect (InsertStatement.setSubSelect(...)) are set.");
            }
        }
        return newStatement(result.toString());
    }
    
    protected String getValue(Value pValue) {
        Value.Type type = pValue.getType();
        Object o = pValue.getValue();
        if (Value.Type.BOOLEAN.equals(type)) {
            return o == null ? "null" : (((Boolean) o).booleanValue() ? "TRUE" : "FALSE");
        } else if (Value.Type.BYTE.equals(type)   ||
                Value.Type.SHORT.equals(type)  ||
                Value.Type.INT.equals(type)    ||
                Value.Type.LONG.equals(type)   ||
                Value.Type.FLOAT.equals(type)  ||
                Value.Type.DOUBLE.equals(type)) {
            return o == null ? "null" : o.toString();
        } else if (Value.Type.DATE.equals(type)  ||
                Value.Type.DATETIME.equals(type)  ||
                Value.Type.TIME.equals(type)) {
            throw new IllegalStateException("Date/time handling not yet implemented.");
        } else if (Value.Type.DATE.equals(type)) {
            throw new IllegalStateException("Date handling not yet implemented.");
        } else if (Value.Type.PLACEHOLDER.equals(type)) {
            return "?";
        } else if (Value.Type.STRING.equals(type)) {
            return o == null ? "null" : getEscapedString(o.toString());
        } else if (Value.Type.NULL.equals(type)) {
            return "null";
        } else {
            throw new IllegalStateException("Unknown value type: " + type);      
        }
    }
    
    protected String getUpdateQuery(UpdateStatement pQuery) {
        ColumnReference[] columns = getSetStatementsColumns(pQuery);
        StatementMetaData smd = newStatementMetaData(pQuery, columns);

        StringBuffer sb = new StringBuffer();
        sb.append("UPDATE ");
        sb.append(getTableAlias(smd, pQuery.getTableReference()));
        sb.append(" SET ");
        boolean first = true;
        for (Iterator iter = pQuery.getSetValues();  iter.hasNext();  ) {
            UpdateStatement.SetValue setValue = (UpdateStatement.SetValue) iter.next();
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }
            sb.append(getColumnAlias(smd, setValue.getColumnReference()));
            sb.append("=");
            sb.append(getBooleanConstraintPart(smd, setValue.getValue()));
        }
        String s = getWhereClause(smd, pQuery.getWhere());
        if (s != null) {
            sb.append(" WHERE ").append(s);
        }
        return newStatement(sb.toString());
    }
    
    protected String getDeleteQuery(DeleteStatement pQuery) {
        StatementMetaData smd = newStatementMetaData(pQuery);
        
        StringBuffer result = new StringBuffer("DELETE FROM ");
        result.append(getTableAlias(smd, pQuery.getTableReference()));
        String s = getWhereClause(smd, pQuery.getWhere());
        if (s != null) {
            result.append(" WHERE ");
            result.append(s);
        }
        return result.toString();
    }
    
    protected boolean isQualifiedColumn(StatementMetaData pData, ColumnReference pColumn) {
        if (pData == null) {
            return false;
        }
        Integer num = (Integer) pData.getColumnNames().get(pColumn.getColumn().getName().toString().toUpperCase());
        if (num == null) {
            throw new IllegalStateException("Column not in map of column counts: "
                    + pColumn.getColumn().getName());
        }
        return num.intValue() > 1;
    }
    
    protected String getFunction(StatementMetaData pData, Function f) {
        return f.getName() + '(' + getParts(pData, f.getParts()) + ')';
    }
    
    protected String getColumnAlias(StatementMetaData pData, ColumnReference pColumn) {
        Column col = pColumn.getColumn();
        String s = col.getName().toString();
        if (col.isVirtual()) {
            VirtualColumn virtCol = (VirtualColumn) col;
            Object o = virtCol.getValue();
            if (o instanceof SelectStatement) {
                return "(" + getSelectQuery((SelectStatement) o, pData) + ") AS " + s;
            } else if (o instanceof Function) {
                return getFunction(pData, (Function) o) + " AS " + s;
            } else if (o instanceof String) {
                return ((String) o) + " AS " + s;
            } else {
                throw new IllegalStateException("Invalid type of VirtualColumn: " + o);
            }
        } else {
            if (isQualifiedColumn(pData, pColumn)) {
                TableReference tableReference = pColumn.getTableReference();
                if (tableReference.getAlias() != null) {
                    s = tableReference.getAlias().getName() + "." + s;
                } else {
                    s = tableReference.getTable().getName() + "." + s;
                }
            }
            
            if (pColumn.getAlias() != null) {
                s = s + " AS " + pColumn.getAlias().getName();
            }
        }
        return s;
    }
    
    protected String getColumnAlias(StatementMetaData pData, ColumnReference[] pColumn) {
        StringBuffer sb = new StringBuffer("(");
        for (int i = 0;  i < pColumn.length;  i++) {
            if (i > 0) sb.append(", ");
            sb.append(getColumnAlias(pData, pColumn[i]));
        }
        sb.append(")");
        return sb.toString();
    }
    
    protected boolean isTableAliasUsingAs() { return true; }
    
    protected String getTableAlias(StatementMetaData pData, TableReference pTable) {
        Table t = pTable.getTable();
        String tableName;
        Table.Name alias = pTable.getAlias();
        if (t instanceof ViewImpl) {
            ViewImpl v = (ViewImpl) t;
            tableName = "(" + getSelectQuery(v.getViewStatement(), pData) + ")";
        } else {
            tableName = pTable.getTable().getQName();
        }
        if (alias == null) {
            return tableName;
        } else {
            if (isTableAliasUsingAs()) {
                return tableName + " AS " + alias.getName();
            } else {
                return tableName + " " + alias.getName();
            }
        }
    }
    
    protected String getJoinAlias(StatementMetaData pData, JoinReference pJoinReference) {
        StringBuffer result = new StringBuffer();
        if (pJoinReference.isLeftOuterJoin()) {
            result.append(" LEFT OUTER JOIN ");
        } else if (pJoinReference.isJoin()) {
            result.append(" JOIN ");
        } else {
            throw new IllegalStateException("Unknown join type");
        }
        result.append(getTableAlias(pData, pJoinReference));
        String s = getWhereClause(pData, pJoinReference.getOn());
        if (s != null) {
            result.append(" ON ");
            result.append(s);
        }
        return result.toString();
    }
    
    protected String getEscapedString(String s) {
        if (s.indexOf('\n') > -1  ||  s.indexOf('\r') > -1  ||  s.indexOf('\f') > -1) {
            throw new IllegalArgumentException("Don't know how to handle line or page terminators.");
        }
        if (s.indexOf('\'') > -1) {
            throw new IllegalArgumentException("Don't know how to handle the char ' in strings.");
        }
        return "'" + s + "'";
    }
    
    protected String getParts(StatementMetaData pData, Iterator pParts) {
        StringBuffer sb = new StringBuffer();
        while (pParts.hasNext()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(getBooleanConstraintPart(pData, pParts.next()));
        }
        return sb.toString();
    }

    protected String getCase(StatementMetaData pData, Case pCase) {
        StringBuffer sb = new StringBuffer("CASE ");
        sb.append(getBooleanConstraintPart(pData, pCase.getCheckedValue()));
        Case.When[] whens = pCase.getWhens();
        for (int i = 0;  i < whens.length;  i++) {
        	Case.When when = whens[i];
            sb.append(" WHEN ");
            sb.append(getBooleanConstraintPart(pData, when.getCondition()));
            sb.append(" THEN ");
            sb.append(getBooleanConstraintPart(pData, when.getValue()));
        }
        Object o = pCase.getElseValue();
        if (o != null) {
        	sb.append(" ELSE ");
            sb.append(getBooleanConstraintPart(pData, o));
        }
        sb.append(" END");
        return sb.toString();
    }

    protected String getBooleanConstraintPart(StatementMetaData pData, Object o) {
        if (o instanceof Value) {
            return getValue((Value) o);
        } else if (o instanceof ColumnReference) {
            return getColumnAlias(pData, (ColumnReference) o);
        } else if (o instanceof ColumnReference[]) {
            return getColumnAlias(pData, (ColumnReference[]) o);
        } else if (o instanceof SelectStatement) {
            return '(' + getSelectQuery((SelectStatement) o, pData) + ')';
        } else if (o instanceof RawSQLCode) {
            return ((RawSQLCode) o).getRawSQL();
        } else if (o instanceof Function) {
            return getFunction(pData, (Function) o);
        } else if (o instanceof Case) {
            return getCase(pData, (Case) o);
        } else if (o instanceof Expression) {
			return getExpression(pData, (Expression) o);
        } else {
            throw new IllegalArgumentException("Invalid part of a boolean constraint: " + o.getClass().getName());
        }
    }

	protected String getExpression(StatementMetaData pData, Expression pExpr) {
		int parts = pExpr.getNumParts();
		int minParts = pExpr.getMinimumParts();
		if (parts < minParts) {
			throw new IllegalStateException("An expression of type " + pExpr.getType()
											+ " must have at least " + minParts + " parts.");
		}
		int maxParts = pExpr.getMaximumParts();
		if (maxParts > 0  &&  parts > maxParts) {
			throw new IllegalStateException("An expression of type " + pExpr.getType()
											+ " must have at least " + maxParts + " parts.");
		}
		Iterator iter = pExpr.getParts();
		if (parts == 1) {
			return getBooleanConstraintPart(pData, iter.next());
		} else {
			String sep;
			Expression.Type type = pExpr.getType();
			if (Expression.SUM.equals(type)) {
				sep = "+";
			} else if (Expression.PRODUCT.equals(type)) {
				sep = "*";
			} else if (Expression.DIFFERENCE.equals(type)) {
				sep = "-";
			} else if (Expression.QUOTIENT.equals(type)) {
				sep = "/";
			} else {
				throw new IllegalStateException("Invalid type: " + type);
			}
			StringBuffer result = new StringBuffer("(");
			for (int i = 0;  i < parts;  i++) {
				if (i > 0) {
					result.append(sep);
				}
				result.append(getBooleanConstraintPart(pData, iter.next()));
			}
			result.append(")");
			return result.toString();
		}
	}

    protected String getBooleanConstraintType(BooleanConstraint.Type pType) {
        if (BooleanConstraint.Type.EQ.equals(pType)) {
            return "=";
        } else if (BooleanConstraint.Type.NE.equals(pType)) {
            return "<>";
        } else if (BooleanConstraint.Type.GT.equals(pType)) {
            return ">";
        } else if (BooleanConstraint.Type.LT.equals(pType)) {
            return "<";
        } else if (BooleanConstraint.Type.GE.equals(pType)) {
            return ">=";
        } else if (BooleanConstraint.Type.LE.equals(pType)) {
            return "<=";
        } else if (BooleanConstraint.Type.LIKE.equals(pType)) {
            return " LIKE ";
        } else {
            throw new IllegalArgumentException("Invalid type: " + pType);
        }
    }
    
    protected String getBooleanConstraint(StatementMetaData pData,
                                          BooleanConstraint pConstraint) {
        BooleanConstraint.Type type = pConstraint.getType();
        Iterator parts = pConstraint.getParts();
        if (!parts.hasNext()) {
            throw new NullPointerException("A boolean constraint must have its parts set.");
        }
        if (BooleanConstraint.Type.IN.equals(type)) {
            Object o = parts.next();
            return getBooleanConstraintPart(pData, o) + " IN (" + getParts(pData, parts) + ')';
        }
        StringBuffer result = new StringBuffer();
        int expected;
        if (BooleanConstraint.Type.EXISTS.equals(type)) {
            SelectStatement selectStatement = (SelectStatement) parts.next();
            result.append("EXISTS(");
            result.append(getSelectQuery(selectStatement, pData));
            result.append(")");
            expected = 1;
        } else if (BooleanConstraint.Type.BETWEEN.equals(type)) {
			expected = 3;
			if (pConstraint.getNumParts() >= 3) {
				result.append(getBooleanConstraintPart(pData, parts.next()));
				result.append(" BETWEEN ");
				result.append(getBooleanConstraintPart(pData, parts.next()));
				result.append(" AND ");
				result.append(getBooleanConstraintPart(pData, parts.next()));
			}
        } else {
            result.append(getBooleanConstraintPart(pData, parts.next()));
            if (BooleanConstraint.Type.EQ.equals(type)  ||
                    BooleanConstraint.Type.NE.equals(type)  ||
                    BooleanConstraint.Type.GT.equals(type)  ||
                    BooleanConstraint.Type.LT.equals(type)  ||
                    BooleanConstraint.Type.GE.equals(type)  ||
                    BooleanConstraint.Type.LE.equals(type)  ||
                    BooleanConstraint.Type.LIKE.equals(type)) {
                expected = 2;
                if (!parts.hasNext()) {
                    throw new NullPointerException("The boolean constraint " + type +
                    							   " must have exactly two parts set.");
                }
                result.append(getBooleanConstraintType(type));
                result.append(getBooleanConstraintPart(pData, parts.next()));
            } else if (BooleanConstraint.Type.ISNULL.equals(type)) {
                expected = 1;
                result.append(" IS NULL");
            } else {
                throw new IllegalArgumentException("Invalid boolean constraint type: " + type);
            }
        }
        if (expected != 0  &&  parts.hasNext()) {
            throw new NullPointerException("The boolean constraint " + type +
                    					   " must have exactly " + expected +
                    					   " parts set, but has " + pConstraint.getNumParts());
        }
        return result.toString();
    }
    
    protected String getCombinedConstraint(StatementMetaData pData,
            CombinedConstraint pConstraint) {
        if (pConstraint.getNumParts() == 0) {
            return null;
        }
        
        List parts = new ArrayList();
        for (Iterator iter = pConstraint.getParts();  iter.hasNext();  ) {
            String s;
            Object o = iter.next();
            if (o == null) {
                throw new NullPointerException("A CombinedConstraints part must not be null");
            }
            if (o instanceof CombinedConstraint) {
                s = getCombinedConstraint(pData, (CombinedConstraint) o);
            } else if (o instanceof BooleanConstraint) {
                s = getBooleanConstraint(pData, (BooleanConstraint) o);            
            } else {
                throw new IllegalArgumentException("Invalid part: " + o.getClass().getName()); 
            }
            if (s != null) {
                parts.add(s);
            }
        }

        switch (parts.size()) {
            case 0: return null;
            case 1:
                if (pConstraint.isNOT()) {
                    return "NOT (" + parts.get(0) + ")";
                } else {
                    return (String) parts.get(0);
                }
            default:
                StringBuffer sb = new StringBuffer();
            	if (pConstraint.isNOT()) {
            	    sb.append("(NOT ");
            	}
            	for (int i = 0;  i < parts.size();  i++) {
            	    if (i == 0) {
            	        sb.append("(");
            	    } else {
            	        sb.append(" ");
            	        sb.append(pConstraint.getType());
            	        sb.append(" ");
            	    }
            	    sb.append(parts.get(i));
            	}
            	sb.append(")");
            	if (pConstraint.isNOT()) {
            	    sb.append(")");
            	}
            	return sb.toString();
        }
    }
    
    public String getConstraint(StatementMetaData pData, Constraint pConstraint) {
        if (pConstraint instanceof CombinedConstraint) {
            return getWhereClause(pData, (CombinedConstraint) pConstraint);
        } else if (pConstraint instanceof BooleanConstraint) {
            return getBooleanConstraint(pData, (BooleanConstraint) pConstraint);
        } else {
            throw new IllegalArgumentException("Unknown constraint type: " +
                    pConstraint.getClass().getName());
        }
    }
    
    protected String getWhereClause(StatementMetaData pData,
            CombinedConstraint pWhereClause) {
        return getCombinedConstraint(pData, pWhereClause);
    }
    
    protected String getSelectQuery(SelectStatement pQuery) {
        StatementMetaData selectStatementMetaData = newStatementMetaData(pQuery);
        return getSelectQuery(pQuery, selectStatementMetaData);
    }
    

    protected String getSelectQueryResultColumns(SelectStatement pQuery, StatementMetaData pData) {
        StringBuffer sb = new StringBuffer();
        if (pQuery.isDistinct()) {
            sb.append(" DISTINCT");
        }
        Iterator columnIter = pQuery.getResultColumns();
        if (!columnIter.hasNext()) {
            sb.append(" *");
        } else {
            boolean first = true;
            do {
                ColumnReference column = (ColumnReference) columnIter.next();
                if (first) {
                    sb.append(" ");
                    first = false;
                } else {
                    sb.append(", ");
                }
                sb.append(getColumnAlias(pData, column));
            } while (columnIter.hasNext());
        }
        return sb.toString();
    }

    protected String getSelectQueryFromClause(SelectStatement pQuery, StatementMetaData pData) {
        SelectTableReference selectTableReference = pQuery.getSelectTableReference();
        if (selectTableReference == null) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        sb.append(" FROM ");
        sb.append(getTableAlias(pData, selectTableReference));
        for (JoinReference joinReference = selectTableReference.getRightJoinedTableReference();
        joinReference != null;
        joinReference = joinReference.getRightJoinedTableReference()) {
            sb.append(getJoinAlias(pData, joinReference));               
        }
        return sb.toString();
    }

    protected String getSelectQueryConstraints(SelectStatement pQuery, StatementMetaData pData,
                                               StatementMetaData.LocalData pLocalData) {
        String s = getWhereClause(pData, pQuery.getWhere());
        if (pLocalData != null  &&  s != null  &&  s.length() > 0) {
        	pLocalData.setWhereClause(true);
        }
        return s;
    }

    protected String getSelectQuery(SelectStatement pQuery, StatementMetaData pData) {
        StringBuffer sb = new StringBuffer("SELECT");
        String s = getSelectQueryResultColumns(pQuery, pData);
        if (s != null) {
            sb.append(s);
        }

        s = getSelectQueryFromClause(pQuery, pData);
        if (s != null) {
            sb.append(s);
        }

        StatementMetaData.LocalData localData = new StatementMetaData.LocalData();
        s = getSelectQueryConstraints(pQuery, pData, localData);
        if (s != null  &&  s.length() > 0) {
            if (localData.hasWhereClause()) {
                sb.append(" WHERE ");
            }
            sb.append(s);
        }
        
        s = getSelectQueryOrderClause(pData, pQuery);
        if (s != null) {
            sb.append(s);
        }
        return sb.toString();
    }

    protected String getOrderColumn(StatementMetaData pData, SelectStatement.OrderColumn pColumn) {
        String s = getBooleanConstraintPart(pData, pColumn.getColumn());
        if (pColumn.isDescending()) {
            s += " DESC";
        }
        return s;
    }

    protected String getSelectQueryOrderClause(StatementMetaData pData, SelectStatement pQuery) {
        StringBuffer sb = new StringBuffer();
        for (Iterator iter = pQuery.getOrderColumns();  iter.hasNext();  ) {
            SelectStatement.OrderColumn col = (SelectStatement.OrderColumn) iter.next();
            if (sb.length() == 0) {
                sb.append(" ORDER BY ");
            } else {
                sb.append(", ");
            }
            sb.append(getOrderColumn(pData, col));
        }
        return sb.toString();
    }
    
    public String getQuery(Statement pStatement) {
        String s;
        if (pStatement instanceof InsertStatement) {
            s = getInsertQuery((InsertStatement) pStatement);
        } else if (pStatement instanceof UpdateStatement) {
            s = getUpdateQuery((UpdateStatement) pStatement); 
        } else if (pStatement instanceof DeleteStatement) {
            s =  getDeleteQuery((DeleteStatement) pStatement);
        } else if (pStatement instanceof SelectStatement) {
            s = getSelectQuery((SelectStatement) pStatement);
        } else {
            throw new IllegalArgumentException("The Statement is neither an INSERT, UPDATE, DELETE nor a SELECT statement.");
        }
        return newStatement(s);
    }
    
    public Collection getCreate(Schema pSchema, boolean pAll) {
        if (!pAll) { return getCreate(pSchema); }
        List result = new ArrayList();
        result.addAll(getCreate(pSchema));
        for (Iterator iter = pSchema.getTables();  iter.hasNext();  ) {
            Table table = (Table) iter.next();
            result.addAll(getCreate(table, true));
        }
        return result;
    }
    
    public Collection getDrop(Schema pSchema, boolean pAll) {
        if (!pAll) { return getDrop(pSchema); }
        List result = new ArrayList();
        List tables = new ArrayList();
        for (Iterator iter = pSchema.getTables();  iter.hasNext();  ) {
            tables.add(iter.next());
        }
        Collections.reverse(tables);
        for (Iterator iter = tables.iterator();  iter.hasNext();  ) {
            Table table = (Table) iter.next();
            result.addAll(getDrop(table, true));
        }
        result.addAll(getDrop(pSchema));
        return result;
    }
    
    public Collection getCreate(Table pTable, boolean pAll) {
        if (!pAll) { return getCreate(pTable); }
        List result = new ArrayList();
        result.addAll(getCreate(pTable));
        for (Iterator iter = pTable.getIndexes();  iter.hasNext();  ) {
            Index index = (Index) iter.next();
            if (index.isPrimaryKey() && !isPrimaryKeyUniqueIndex()) {
                if (isPrimaryKeyPartOfCreateTable()) {
                    continue;
                }
            } else if (index.isUnique()) {
                if (isUniqueIndexPartOfCreateTable()) {
                    continue;
                }
            } else {
                if (isNonUniqueIndexPartOfCreateTable()) {
                    continue;
                }
            }
            result.addAll(getCreate(index));
        }
        if (!isForeignKeyPartOfCreateTable()) {
            for (Iterator iter = pTable.getForeignKeys();  iter.hasNext();  ) {
                ForeignKey key = (ForeignKey) iter.next();
                result.addAll(getCreate(key));
            }
        }
        return result;
    }
    
    public Collection getDrop(Table pTable, boolean pAll) {
        if (!pAll) { return getDrop(pTable); }
        List result = new ArrayList();
        for (Iterator iter = pTable.getIndexes();  iter.hasNext();  ) {
            Index index = (Index) iter.next();
            result.addAll(getDrop(index));
        }
        for (Iterator iter = pTable.getForeignKeys();  iter.hasNext();  ) {
            ForeignKey key = (ForeignKey) iter.next();
            result.addAll(getDrop(key));
        }
        result.addAll(getDrop(pTable));
        return result;
    }
    
    public Collection getCreate(Index pIndex) {
        List result = new ArrayList();
        String s = createIndexAsPartOfCreateTable(pIndex);
        if (s != null) {
            StringBuffer sb = new StringBuffer();
            sb.append("CREATE");
            if (pIndex.isUnique()) {
                sb.append(" UNIQUE");
            }
            sb.append(" INDEX");
            sb.append(" ");
            sb.append(pIndex.getName());
            sb.append(" ON ").append(pIndex.getTable().getQName()).append(" (");
            boolean first = true;
            for (Iterator iter = pIndex.getColumns();  iter.hasNext();  ) {
                if (first) {
                    first = false;
                } else {
                    sb.append(", ");
                }
                sb.append(((Column) iter.next()).getName().getName());
            }
            sb.append(")");
            result.add(newStatement(sb.toString()));
        }
        return result;
    }
    
    public Collection getDrop(Index pIndex) {
        return Collections.EMPTY_SET;
    }
    
    public Collection getCreate(ForeignKey pKey) {
        List result = new ArrayList();
        String s = createForeignKeyAsPartOfCreateTable(pKey);
        if (s != null) {
            result.add(newStatement("CREATE " + s));
        }
        return result;
    }
    
    public Collection getDrop(ForeignKey pKey) {
        return Collections.EMPTY_SET;
    }
    
    protected StatementMetaData newStatementMetaData(SelectStatement pQuery) {
        return new StatementMetaData(pQuery);
    }

    protected StatementMetaData newStatementMetaData(DeleteStatement pQuery) {
        return new StatementMetaData(pQuery);
    }

    protected StatementMetaData newStatementMetaData(UpdateStatement pQuery, ColumnReference[] pColumns) {
        return new StatementMetaData(pQuery, pColumns);
    }

    protected StatementMetaData newStatementMetaData(InsertStatement pQuery, ColumnReference[] pColumns) {
        return new StatementMetaData(pQuery, pColumns);
    }

    public String getWhereClause(SelectStatement pQuery) {
        StatementMetaData metaData = newStatementMetaData(pQuery);
        StringBuffer sb = new StringBuffer();

        StatementMetaData.LocalData localData = new StatementMetaData.LocalData();
        String s = getSelectQueryConstraints(pQuery, metaData, localData);
        if (s != null) {
            sb.append(s);
        }
        
        s = getSelectQueryOrderClause(metaData, pQuery);
        if (s != null) {
            sb.append(s);
        }

        return sb.toString();
    }

    public String getConstraint(Constraint pConstraint) {
        if (pConstraint instanceof CombinedConstraint) {
            CombinedConstraint cc = (CombinedConstraint) pConstraint;
            Statement st = cc.getConstrainedStatement();
            if (st instanceof SelectStatement) {
                StatementMetaData selectStatementMetaData = newStatementMetaData((SelectStatement) st);
                return getCombinedConstraint(selectStatementMetaData, cc);
            } else {
                return getCombinedConstraint(null, cc);
            }
        } else if (pConstraint instanceof BooleanConstraint) {
            BooleanConstraint bc = (BooleanConstraint) pConstraint;
            Statement st = bc.getConstrainedStatement();
            if (st instanceof SelectStatement) {
                StatementMetaData selectStatementMetaData = newStatementMetaData((SelectStatement) st);
                return getBooleanConstraint(selectStatementMetaData, bc);
            } else {
                return getBooleanConstraint(null, bc);
            }
        } else {
            throw new IllegalStateException("Invalid type of Constraint: " + pConstraint);
        }
    }
}
