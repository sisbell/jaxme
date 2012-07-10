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

import org.apache.ws.jaxme.sqls.Case;
import org.apache.ws.jaxme.sqls.Column;
import org.apache.ws.jaxme.sqls.ColumnReference;
import org.apache.ws.jaxme.sqls.CombinedConstraint;
import org.apache.ws.jaxme.sqls.ConstrainedStatement;
import org.apache.ws.jaxme.sqls.Expression;
import org.apache.ws.jaxme.sqls.Function;
import org.apache.ws.jaxme.sqls.JoinReference;
import org.apache.ws.jaxme.sqls.ObjectFactory;
import org.apache.ws.jaxme.sqls.RawSQLCode;
import org.apache.ws.jaxme.sqls.SelectStatement;
import org.apache.ws.jaxme.sqls.SelectTableReference;
import org.apache.ws.jaxme.sqls.Statement;
import org.apache.ws.jaxme.sqls.Table;
import org.apache.ws.jaxme.sqls.TableReference;
import org.apache.ws.jaxme.sqls.CombinedConstraint.Type;


/** <p>Default implementation of the object factory.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class ObjectFactoryImpl implements ObjectFactory {
    protected ObjectFactoryImpl() {
    }
    
    public RawSQLCode newRawSQL(String pRawSQLCode) {
        return new RawSQLCodeImpl(pRawSQLCode);
    }
    
    public JoinReference newJoinReference(SelectTableReference pSelectTableReference,
            Table pTable,
            boolean pIsLeftOuterJoin) {
        return new JoinReferenceImpl(pSelectTableReference, pTable, pIsLeftOuterJoin);
    }
    
    public Function newFunction(Statement pStatement, String pName) {
        return new FunctionImpl(pStatement, pName);
    }
    public Table newView(SelectStatement pSelectStatement, Table.Name pName) {
        return new ViewImpl(pSelectStatement, pName);
    }

    public CombinedConstraint newCombinedConstraint(ConstrainedStatement pStatement, Type pType) {
        return new CombinedConstraintImpl(pStatement, pType);
    }

    public Case newCase(Column.Type pType) {
    	return new CaseImpl(pType);
    }

    public ColumnReference newColumnReference(TableReference pTableReference, Column pColumn) {
        return new ColumnReferenceImpl(pTableReference, pColumn);
    }

	public Expression createExpression(Statement pStatement, org.apache.ws.jaxme.sqls.Expression.Type pType) {
		return new ExpressionImpl(pStatement, pType);
	}
}
