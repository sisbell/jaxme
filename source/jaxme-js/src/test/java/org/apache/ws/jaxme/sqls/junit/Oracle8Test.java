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

import org.apache.ws.jaxme.sqls.BooleanConstraint;
import org.apache.ws.jaxme.sqls.CombinedConstraint;
import org.apache.ws.jaxme.sqls.SQLFactory;
import org.apache.ws.jaxme.sqls.SQLGenerator;
import org.apache.ws.jaxme.sqls.SelectTableReference;
import org.apache.ws.jaxme.sqls.Table;
import org.apache.ws.jaxme.sqls.oracle.OraColumnReference;
import org.apache.ws.jaxme.sqls.oracle.OraSQLFactoryImpl;
import org.apache.ws.jaxme.sqls.oracle.OraSQLGenerator;
import org.apache.ws.jaxme.sqls.oracle.OraSQLGeneratorImpl;
import org.apache.ws.jaxme.sqls.oracle.OraSelectStatement;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class Oracle8Test extends CreateTest {
    /** <p>Creates a new instance of Oracle8Test.</p>
     */
    public Oracle8Test(String pName) {
        super(pName);
    }
    
    protected SQLFactory newSQLFactory() {
        return new OraSQLFactoryImpl();
    }
    
    protected SQLGenerator newSQLGenerator() {
        OraSQLGenerator gen = new OraSQLGeneratorImpl();
        gen.setOracle8Compatibility(true);
        return gen;
    }
    
    protected String getTestJoinResult() {
        return "SELECT OtherTable.MyIndex, RefIndex, Company FROM MySchema.OtherTable, MySchema.MyTable WHERE RefIndex=MyTable.MyIndex AND OtherTable.MyIndex=?";
    }
    
    protected String getTestLeftOuterJoinResult() {
        return "SELECT OtherTable.MyIndex, RefIndex, Company FROM MySchema.OtherTable, MySchema.MyTable WHERE RefIndex=MyTable.MyIndex(+) AND OtherTable.MyIndex=?";
    }

    public void testConnectByPrior() {
        Table table = getBasicTable();
        OraSelectStatement selectStatement = (OraSelectStatement) table.getSelectStatement();
        SelectTableReference ref = selectStatement.getSelectTableReference();
        CombinedConstraint startWith = selectStatement.getStartWith();
        BooleanConstraint bc = startWith.createEQ();
        bc.addPart(ref.newColumnReference("MyIndex"));
        bc.addPart(1);
        CombinedConstraint connectByPrior = selectStatement.getConnectBy();
        bc = connectByPrior.createEQ();
        OraColumnReference oraRef = (OraColumnReference) ref.newColumnReference("MyIndex");
        oraRef.setPrior(true);
        bc.addPart(oraRef);
        bc.addPart(ref.newColumnReference("MyName"));
        SQLGenerator gen = getSQLGenerator();
        String query = gen.getQuery(selectStatement);
        assertEquals("SELECT MyIndex, MyName, MyDate FROM MySchema.MyTable START WITH MyIndex=1 CONNECT BY PRIOR MyIndex=MyName", query);
    }

    protected String getBulkInsertResult() {
    	return "INSERT INTO MySchema.MyTable (MyTable.MyIndex, MyTable.MyName, MyTable.MyDate) (SELECT MyTable0.MyIndex, MyTable0.MyName, MyTable0.MyDate FROM MySchema.MyTable MyTable0)";
    }
}
