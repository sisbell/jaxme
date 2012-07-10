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
package org.apache.ws.jaxme.sqls.junit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;
import org.apache.ws.jaxme.sqls.BooleanConstraint;
import org.apache.ws.jaxme.sqls.Column;
import org.apache.ws.jaxme.sqls.ColumnReference;
import org.apache.ws.jaxme.sqls.CombinedConstraint;
import org.apache.ws.jaxme.sqls.DeleteStatement;
import org.apache.ws.jaxme.sqls.Expression;
import org.apache.ws.jaxme.sqls.ForeignKey;
import org.apache.ws.jaxme.sqls.Index;
import org.apache.ws.jaxme.sqls.InsertStatement;
import org.apache.ws.jaxme.sqls.JoinReference;
import org.apache.ws.jaxme.sqls.SQLFactory;
import org.apache.ws.jaxme.sqls.SQLGenerator;
import org.apache.ws.jaxme.sqls.Schema;
import org.apache.ws.jaxme.sqls.SelectStatement;
import org.apache.ws.jaxme.sqls.SelectTableReference;
import org.apache.ws.jaxme.sqls.StringColumn;
import org.apache.ws.jaxme.sqls.Table;
import org.apache.ws.jaxme.sqls.TableReference;
import org.apache.ws.jaxme.sqls.UpdateStatement;
import org.apache.ws.jaxme.sqls.impl.SQLFactoryImpl;
import org.apache.ws.jaxme.sqls.impl.VirtualColumn;


public class CreateTest extends TestCase {
    private SQLFactory sqlFactory;
    private SQLGenerator sqlGenerator;
    private Schema schema;
    
    protected SQLFactory newSQLFactory() {
        return new SQLFactoryImpl();
    }
    
    protected SQLGenerator newSQLGenerator() {
        return sqlFactory.newSQLGenerator();
    }
    
    protected SQLFactory getSQLFactory() {
        return sqlFactory;
    }
    
    protected SQLGenerator getSQLGenerator() {
        return sqlGenerator;
    }
    
    public void setUp() {
        sqlFactory = newSQLFactory();
        sqlGenerator = newSQLGenerator();
        schema = sqlFactory.newSchema("MySchema");
    }
    
    /** <p>Creates a new instance of CreateTest.java.</p>
     */
    public CreateTest(String pName) { super(pName); }
    
    /** <p>Creates a basic table</p>
     */
    protected Table getBasicTable() {
        Table table = schema.newTable("MyTable");
        Column myIndex = table.newColumn("MyIndex", Column.Type.INTEGER);
        assertTrue(!myIndex.isStringColumn());
        assertTrue(!myIndex.isBinaryColumn());
        Column myName = table.newColumn("MyName", Column.Type.VARCHAR);
        assertTrue(myName.isStringColumn()); // myName may be casted to a StringColumn
        assertTrue(!myName.isBinaryColumn());
        ((StringColumn) myName).setLength(60);
        Column myDate = table.newColumn("MyDate", Column.Type.DATE);
        assertTrue(!myDate.isStringColumn());
        assertTrue(!myDate.isBinaryColumn());
        myDate.setNullable(true);
        return table;
    }
    
    /** <p>Creates a table with primary key</p>
     */
    protected Table getPrimaryKeyTable() {
        Table table = getBasicTable();
        Index index = table.newPrimaryKey();
        index.addColumn("MyIndex");
        return table;
    }
    
    protected Table getForeignKeyTable(Table pTable) {
        Table otherTable = pTable.getSchema().newTable("OtherTable");
        Column otherIndex = otherTable.newColumn("MyIndex", Column.Type.INTEGER);
        Column referenceColumn = otherTable.newColumn("RefIndex", Column.Type.INTEGER);
        Column companyColumn = otherTable.newColumn("Company", Column.Type.VARCHAR);
        ((StringColumn) companyColumn).setLength(60);
        otherTable.newPrimaryKey().addColumn(otherIndex);
        
        ForeignKey reference = otherTable.newForeignKey(pTable);
        reference.addColumnLink(referenceColumn, pTable.getColumn("MyIndex"));
        return otherTable;
    }
    
    /** <p>Basic test for creating a <code>CREATE TABLE</code>
     * statement.</p>
     */
    public void testBasicCreate() {
        Table table = getBasicTable();
        SQLGenerator generator = getSQLGenerator();
        generator.setLineTerminator("\n");
        Collection statements = generator.getCreate(table.getSchema(), true);
        Iterator iter = statements.iterator();
        assertTrue(iter.hasNext());
        assertEquals("CREATE SCHEMA MySchema", iter.next());
        assertTrue(iter.hasNext());
        assertEquals("CREATE TABLE MySchema.MyTable (\n" +
                "  MyIndex INT NOT NULL,\n" +
                "  MyName VARCHAR(60) NOT NULL,\n" +
                "  MyDate DATE\n" +
                ")\n", iter.next());
        assertTrue(!iter.hasNext());
    }
    
    /** <p>Basic test for creating an <code>INSERT</code> statement.</p>
     */
    public void testBasicInsert() {
        Table table = getBasicTable();
        InsertStatement insertStatement = table.getInsertStatement();
        SQLGenerator generator = getSQLGenerator();
        generator.setLineTerminator("\n");
        String s = generator.getQuery(insertStatement);
        assertEquals("INSERT INTO MySchema.MyTable (MyIndex, MyName, MyDate) VALUES (?, ?, ?)", s);
    }

    protected String getBulkInsertResult() {
        return "INSERT INTO MySchema.MyTable (MyTable.MyIndex, MyTable.MyName, MyTable.MyDate) (SELECT MyTable0.MyIndex, MyTable0.MyName, MyTable0.MyDate FROM MySchema.MyTable AS MyTable0)";
    }

    public void testBulkInsert() {
        Table table = getBasicTable();
        InsertStatement insertStatement = getSQLFactory().newInsertStatement();
        insertStatement.setTable(table);
        SelectStatement st = table.getSelectStatement();
        SQLGenerator generator = getSQLGenerator();
        generator.setLineTerminator("\n");
        insertStatement.setSubSelect(st);
        String got = generator.getQuery(insertStatement);
        String expect = getBulkInsertResult();
        assertEquals(expect, got);
    }

    /** <p>Basic test for creating a <code>SELECT</code> statement.</p>
     */
    public void testBasicSelect() {
        Table table = getBasicTable();
        SelectStatement selectStatement = table.getSelectStatement();
        SQLGenerator generator = getSQLGenerator();
        generator.setLineTerminator("\n");
        String s = generator.getQuery(selectStatement);
        assertEquals("SELECT MyIndex, MyName, MyDate FROM MySchema.MyTable", s);
    }
    
    /** <p>Basic test for creating an <code>UPDATE</code> statement.</p>
     */
    public void testBasicUpdate() {
        Table table = getPrimaryKeyTable();
        UpdateStatement updateStatement = table.getUpdateStatement();
        SQLGenerator generator = getSQLGenerator();
        generator.setLineTerminator("\n");
        String s = generator.getQuery(updateStatement);
        assertEquals("UPDATE MySchema.MyTable SET MyName=?, MyDate=? WHERE MyIndex=?", s);
    }
    
    /** <p>Basic test for creating an <code>DELETE</code> statement.</p>
     */
    public void testBasicDelete() {
        Table table = getPrimaryKeyTable();
        DeleteStatement deleteStatement = table.getDeleteStatement();
        SQLGenerator generator = getSQLGenerator();
        generator.setLineTerminator("\n");
        String s = generator.getQuery(deleteStatement);
        assertEquals("DELETE FROM MySchema.MyTable WHERE MyIndex=?", s);
    }
    
    protected String getCreateForeignKeyResult() {
        return "CREATE TABLE MySchema.OtherTable (  MyIndex INT NOT NULL,  RefIndex INT NOT NULL,  Company VARCHAR(60) NOT NULL)";
    }
    
    /** <p>Test for a FOREIGN KEY definition.</p>
     */
    public void testCreateForeignKey() {
        Table table = getPrimaryKeyTable();
        Table otherTable = getForeignKeyTable(table);
        SQLGenerator generator = getSQLGenerator();
        Collection c = generator.getCreate(otherTable);
        assertEquals(1, c.size());
        String expect = getCreateForeignKeyResult();
        String got = (String) c.iterator().next();
        assertEquals(expect, got);
    }
    
    protected String getTestJoinResult() {
        return "SELECT OtherTable.MyIndex, RefIndex, Company FROM MySchema.OtherTable JOIN MySchema.MyTable ON RefIndex=MyTable.MyIndex WHERE OtherTable.MyIndex=?";
    }

    protected SelectStatement getJoinStatement() {
        Table table = getPrimaryKeyTable();
        Table otherTable = getForeignKeyTable(table);
        SelectStatement statement = otherTable.getSelectStatement();
        SelectTableReference tableReference = statement.getSelectTableReference();
        JoinReference joinReference = tableReference.join(table);
        
        TableReference refLocal = tableReference;
        TableReference refRef = tableReference.getRightJoinedTableReference();
        
        joinReference.getOn().addJoin((ForeignKey) otherTable.getForeignKeys().next(),
                refLocal, refRef);
        CombinedConstraint cc = statement.getWhere();
        BooleanConstraint bc = cc.createEQ();
        bc.addPart(tableReference.newColumnReference("MyIndex"));
        bc.addPlaceholder();
        return statement;
    }

    /** <p>Test for a JOIN statement.</p>
     */
    public void testJoin() {
        SelectStatement statement = getJoinStatement();
        SQLGenerator generator = getSQLGenerator();
        generator.setLineTerminator("\n");
        String got = generator.getQuery(statement);
        String expect = getTestJoinResult();
        assertEquals(expect, got);
    }
    
    protected String getTestLeftOuterJoinResult() {
        return "SELECT OtherTable.MyIndex, RefIndex, Company FROM MySchema.OtherTable LEFT OUTER JOIN MySchema.MyTable ON RefIndex=MyTable.MyIndex WHERE OtherTable.MyIndex=?";
    }
    
    /** <p>Test for a LEFT OUTER JOIN statement.</p>
     */
    public void testLeftOuterJoin() {
        Table table = getPrimaryKeyTable();
        Table otherTable = getForeignKeyTable(table);
        SelectStatement statement = otherTable.getSelectStatement();
        SelectTableReference tableReference = statement.getSelectTableReference();
        JoinReference joinReference = tableReference.leftOuterJoin(table);
        
        TableReference refLocal = tableReference;
        TableReference refRef = tableReference.getRightJoinedTableReference();
        
        joinReference.getOn().addJoin((ForeignKey) otherTable.getForeignKeys().next(),
                refLocal, refRef);
        CombinedConstraint cc = statement.getWhere();
        BooleanConstraint bc = cc.createEQ();
        bc.addPart(tableReference.newColumnReference("MyIndex"));
        bc.addPlaceholder();
        
        SQLGenerator generator = getSQLGenerator();
        generator.setLineTerminator("\n");
        String got = generator.getQuery(statement);
        String expect = getTestLeftOuterJoinResult();
        assertEquals(expect, got);
    }
    
    /** <p>Test for an EXISTS clause.</p>
     */
    public void testExists() {
        Table table = getPrimaryKeyTable();
        Table otherTable = getForeignKeyTable(table);
        SelectStatement statement = table.getSelectStatement();
        SelectTableReference tableReference = statement.getSelectTableReference();
        SelectStatement existsStatement = otherTable.getSelectStatement();
        SelectTableReference existsTableReference = existsStatement.getSelectTableReference();
        BooleanConstraint bc = existsStatement.getWhere().createEQ();
        bc.addPart(existsTableReference.newColumnReference("RefIndex"));
        bc.addPart(tableReference.newColumnReference("MyIndex"));
        statement.getWhere().createEXISTS(existsStatement);
        
        SQLGenerator generator = getSQLGenerator();
        generator.setLineTerminator("\n");
        String got = generator.getQuery(statement);
        String expect = "SELECT MyTable.MyIndex, MyName, MyDate FROM MySchema.MyTable" +
        " WHERE EXISTS(SELECT OtherTable.MyIndex, RefIndex, Company" +
        " FROM MySchema.OtherTable WHERE RefIndex=MyTable.MyIndex)";
        assertEquals(expect, got);
    }

    /** <p>Test for a BETWEEN clause.</p>
     */
    public void testBetween() {
        Table table = getBasicTable();
        SelectStatement statement = table.getSelectStatement();
		BooleanConstraint between = statement.getWhere().createBETWEEN();
		between.addPart(statement.getTableReference().newColumnReference("MyIndex"));
		between.addPart(3);
		between.addPart(5);

		SQLGenerator generator = getSQLGenerator();
        generator.setLineTerminator("\n");
        String got = generator.getQuery(statement);
        String expect = "SELECT MyIndex, MyName, MyDate FROM MySchema.MyTable WHERE MyIndex BETWEEN 3 AND 5";
        assertEquals(expect, got);
    }

    /** <p>Creates a table with a composed primary key.</p>
     */
    protected Table getComposedKeyTable() {
        Table table = getPrimaryKeyTable();
        Column verNumColumn = table.newColumn("VerNum", Column.Type.INTEGER);
        assertTrue(!verNumColumn.isStringColumn());
        assertTrue(!verNumColumn.isBinaryColumn());
        Index index = table.getPrimaryKey();
        index.addColumn("VerNum");
        return table;
    }
    
    /** <p>Test for composed primary keys.</p>
     */
    public void testComposedPrimaryKey() {
        Table table = getComposedKeyTable();
        
        SelectStatement statement = table.getSelectStatement();
        statement.getWhere().addColumnSetQuery(table.getPrimaryKey(), statement.getTableReference());
        SQLGenerator generator = getSQLGenerator();
        generator.setLineTerminator("\n");
        String s = generator.getQuery(statement);
        assertEquals("SELECT MyIndex, MyName, MyDate, VerNum FROM MySchema.MyTable WHERE (MyIndex=? AND VerNum=?)", s);
    }
    
    /** <p>Test for index names.</p>
     */
    public void testIndexNames() {
        SQLGenerator gen = getSQLGenerator();
        Table table = getBasicTable();
        for (int i = 0;  i < 10;  i++) {
            Index index = table.newIndex();
            index.addColumn("MyName");
            String s = (String) gen.getCreate(index).iterator().next();
            assertEquals("CREATE INDEX MyTable_I" + i + " ON MySchema.MyTable (MyName)", s);
        }
        
        Collection coll = gen.getCreate(schema, true);
        String[] cmds = (String[]) coll.toArray(new String[coll.size()]);
        assertEquals(12, cmds.length);
        assertEquals("CREATE SCHEMA MySchema", cmds[0]);
        assertEquals("CREATE TABLE MySchema.MyTable (  MyIndex INT NOT NULL,  MyName VARCHAR(60) NOT NULL,  MyDate DATE)", cmds[1]);
        for (int i = 0;  i < 10;  i++) {
            assertEquals("CREATE INDEX MyTable_I" + i + " ON MySchema.MyTable (MyName)", cmds[i+2]);
        }
    }
    
    /** <p>Test for subselects.</p>
     */
    public void testSubSelect() {
        SQLGenerator gen = getSQLGenerator();
        Table table = getComposedKeyTable();
        
        Table otherTable = table.getSchema().newTable("OtherTable");
        Column otherIndex = otherTable.newColumn("MyIndex", Column.Type.INTEGER);
        otherTable.newPrimaryKey().addColumn(otherIndex);
        ForeignKey foreignKey = otherTable.newForeignKey(table);
        SelectStatement selectStatement = sqlFactory.newSelectStatement();
        selectStatement.setTable(otherTable);
        DeleteStatement deleteStatement = sqlFactory.newDeleteStatement();
        deleteStatement.setTable(table);
        List columns = new ArrayList();
        for (Iterator iter = table.getColumns();  iter.hasNext();  ) {
            Column column = (Column) iter.next();
            Column refColumn = otherTable.newColumn("Ref" + column.getName(), column.getType());
            foreignKey.addColumnLink(refColumn, column);
            if (column.isPrimaryKeyPart()) {
                selectStatement.addResultColumn(selectStatement.getTableReference().newColumnReference(refColumn));
                columns.add(deleteStatement.getTableReference().newColumnReference(column));
            }
        }
        BooleanConstraint eq = selectStatement.getWhere().createEQ();
        eq.addPart(selectStatement.getTableReference().newColumnReference("RefMyName"));
        eq.addPlaceholder();
        
        BooleanConstraint bc = deleteStatement.getWhere().createIN();
        bc.addPart((ColumnReference[]) columns.toArray(new ColumnReference[columns.size()]));
        bc.addPart(selectStatement);
        String expect = "DELETE FROM MySchema.MyTable WHERE (MyTable.MyIndex, VerNum) IN ((SELECT RefMyIndex, RefVerNum FROM MySchema.OtherTable WHERE RefMyName=?))";
        String got = gen.getQuery(deleteStatement);
        assertEquals(expect, got);
    }
    
    public void testVirtualColumn() {
        Table table = getBasicTable();
        SelectStatement selectStatement = table.getSelectStatement();
        VirtualColumn col = new VirtualColumn("virtCol", Column.Type.VARCHAR);
        selectStatement.addResultColumn(col);
        col.setValue("null");
        SQLGenerator gen = getSQLGenerator();
        String query = gen.getQuery(selectStatement);
        assertEquals("SELECT MyIndex, MyName, MyDate, null AS virtCol FROM MySchema.MyTable", query);
    }

    public void testNOT() {
        Table table = getBasicTable();
        SelectStatement selectStatement = table.getSelectStatement();
        SelectTableReference ref = selectStatement.getSelectTableReference();
        CombinedConstraint and = selectStatement.getWhere();
        BooleanConstraint bc = and.createLIKE();
        bc.addPart(ref.newColumnReference("MyName"));
        bc.addPart("%a%");
        CombinedConstraint or = and.createOrConstraint();
        or.setNOT(true);
        bc = or.createEQ();
        bc.addPart(ref.newColumnReference("MyIndex"));
        bc.addPart(1);
        SQLGenerator gen = getSQLGenerator();
        String query = gen.getQuery(selectStatement);
        assertEquals("SELECT MyIndex, MyName, MyDate FROM MySchema.MyTable WHERE (MyName LIKE '%a%' AND NOT (MyIndex=1))", query);

        bc = or.createEQ();
        bc.addPart(ref.newColumnReference("MyIndex"));
        bc.addPart(2);
        query = gen.getQuery(selectStatement);
        assertEquals("SELECT MyIndex, MyName, MyDate FROM MySchema.MyTable WHERE (MyName LIKE '%a%' AND (NOT (MyIndex=1 OR MyIndex=2)))", query);
    }

	/** Test for expressions.
	 */
	public void testExpressions() {
		Table t = getBasicTable();
		SelectStatement st = t.getSelectStatement();
		SelectTableReference ref = st.getSelectTableReference();
		BooleanConstraint bc = st.getWhere().createGT();
		Expression e1 = bc.createSUM();
		e1.addPart(ref.newColumnReference("MyIndex"));
		e1.addPart(3);
		Expression e2 = bc.createSUM();
		e2.addPart(5);
        SQLGenerator gen = getSQLGenerator();
        String got = gen.getQuery(st);
		String expect = "SELECT MyIndex, MyName, MyDate FROM MySchema.MyTable WHERE (MyIndex+3)>5";
		assertEquals(expect, got);
	}
}
