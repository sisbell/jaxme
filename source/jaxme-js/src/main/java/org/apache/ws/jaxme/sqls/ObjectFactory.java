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
package org.apache.ws.jaxme.sqls;

import org.apache.ws.jaxme.sqls.Expression.Type;


/** A factory object for creating all the objects used by
 * the SQL factory.
 */
public interface ObjectFactory {
    /** Returns an instance of
     * {@link org.apache.ws.jaxme.sqls.RawSQLCode}.
     */
    public RawSQLCode newRawSQL(String pRawSQLCode);
    
    /** <p>Returns an instance of
     * {@link org.apache.ws.jaxme.sqls.JoinReference}.</p>
     */
    public JoinReference newJoinReference(SelectTableReference pSelectTableReference,
            Table pTable,
            boolean pIsLeftOuterJoin);
    
    /** Returns an instance of {@link org.apache.ws.jaxme.sqls.Function}.
     */
    public Function newFunction(Statement pStatement, String pName);
    
    /** Returns an instance of
     * {@link org.apache.ws.jaxme.sqls.Table}, which allows to embed
     * the given instance of
     * {@link org.apache.ws.jaxme.sqls.SelectStatement} into another
     * SELECT statement.
     */
    public Table newView(SelectStatement pSelectStatement, Table.Name pName);

    /** Creates a new instance of
     * {@link org.apache.ws.jaxme.sqls.Constraint} constraining
     * the given {@link org.apache.ws.jaxme.sqls.ConstrainedStatement}.
     */
    public CombinedConstraint newCombinedConstraint(ConstrainedStatement pStatement,
            										CombinedConstraint.Type pType);

    /** Creates a new instance of {@link org.apache.ws.jaxme.sqls.Case}.
     */
    public Case newCase(Column.Type pType);

    /** Creates a new instance of {@link ColumnReference}.
     */
    public ColumnReference newColumnReference(TableReference pTableReference, Column pColumn);

	/** Creates a new instance of {@link Expression}.
	 */
	public Expression createExpression(Statement pStatement, Type sum);
}
