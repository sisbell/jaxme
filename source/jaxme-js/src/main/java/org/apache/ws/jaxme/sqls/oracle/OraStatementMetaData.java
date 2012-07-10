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
package org.apache.ws.jaxme.sqls.oracle;

import org.apache.ws.jaxme.sqls.ColumnReference;
import org.apache.ws.jaxme.sqls.DeleteStatement;
import org.apache.ws.jaxme.sqls.InsertStatement;
import org.apache.ws.jaxme.sqls.SelectStatement;
import org.apache.ws.jaxme.sqls.UpdateStatement;
import org.apache.ws.jaxme.sqls.impl.StatementMetaData;


/** <p>Oracle specific version of {@link StatementMetaData}.</p>
 */
public class OraStatementMetaData extends StatementMetaData {
	/** <p>Creates new meta data for the given
     * {@link org.apache.ws.jaxme.sqls.SelectStatement}.</p>
     */
    public OraStatementMetaData(SelectStatement pQuery) {
        super(pQuery);
    }

    /** <p>Creates new meta data for the given
     * {@link org.apache.ws.jaxme.sqls.DeleteStatement}.</p>
     */
    public OraStatementMetaData(DeleteStatement pQuery) {
        super(pQuery);
    }

    /** <p>Creates new meta data for the given
     * {@link org.apache.ws.jaxme.sqls.InsertStatement}.</p>
     */
    public OraStatementMetaData(InsertStatement pQuery, ColumnReference[] pColumns) {
        super(pQuery, pColumns);
    }

    /** <p>Creates new meta data for the given
     * {@link org.apache.ws.jaxme.sqls.UpdateStatement}.</p>
     */
    public OraStatementMetaData(UpdateStatement pQuery, ColumnReference[] pColumns) {
        super(pQuery, pColumns);
    }

    protected void addSelectStatement(SelectStatement pQuery) {
        super.addSelectStatement(pQuery);
        if (pQuery instanceof OraSelectStatement) {
            OraSelectStatement oQuery = (OraSelectStatement) pQuery;
            addCombinedConstraint(oQuery.getStartWith());
            addCombinedConstraint(oQuery.getConnectBy());
        }
    }
}
