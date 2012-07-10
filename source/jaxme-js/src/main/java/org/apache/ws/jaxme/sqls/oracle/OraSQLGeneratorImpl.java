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
import org.apache.ws.jaxme.sqls.JoinReference;
import org.apache.ws.jaxme.sqls.SelectStatement;
import org.apache.ws.jaxme.sqls.SelectTableReference;
import org.apache.ws.jaxme.sqls.UpdateStatement;
import org.apache.ws.jaxme.sqls.impl.SQLGeneratorImpl;
import org.apache.ws.jaxme.sqls.impl.StatementMetaData;


/** Oracle specific extension of
 * {@link org.apache.ws.jaxme.sqls.impl.SQLGeneratorImpl}.
 */
public class OraSQLGeneratorImpl extends SQLGeneratorImpl implements OraSQLGenerator {
    private boolean isOracle8Compatibility;
    
    protected boolean isTableAliasUsingAs() { return false; }
    
    /** <p>Enables compatibility mode for Oracle 8.</p>
     */
    public void setOracle8Compatibility(boolean pOracle8Compatibility) {
        isOracle8Compatibility = pOracle8Compatibility;
    }
    
    /** <p>Returns whether compatibility mode for Oracle 8 is enabled.</p>
     */
    public boolean isOracle8Compatibility() {
        return isOracle8Compatibility;
    }
    
    protected StatementMetaData newStatementMetaData(SelectStatement pQuery) {
        return new OraStatementMetaData(pQuery);
    }

    protected StatementMetaData newStatementMetaData(DeleteStatement pQuery) {
        return new OraStatementMetaData(pQuery);
    }

    protected StatementMetaData newStatementMetaData(UpdateStatement pQuery, ColumnReference[] pColumns) {
        return new OraStatementMetaData(pQuery, pColumns);
    }

    protected StatementMetaData newStatementMetaData(InsertStatement pQuery, ColumnReference[] pColumns) {
        return new OraStatementMetaData(pQuery, pColumns);
    }

    protected String getSelectQueryFromClause(SelectStatement pQuery, StatementMetaData pData) {
        if (isOracle8Compatibility()) {
            SelectTableReference tableReference = pQuery.getSelectTableReference();
            if (tableReference == null) {
                return super.getSelectQueryFromClause(pQuery, pData);
            }
            StringBuffer sb = new StringBuffer(" FROM ");
            sb.append(getTableAlias(pData, tableReference));
            for (JoinReference joinReference = tableReference.getRightJoinedTableReference();
            	 joinReference != null;
            	 joinReference = joinReference.getRightJoinedTableReference()) {
                sb.append(", ");
                sb.append(getTableAlias(pData, joinReference));
            }
            return sb.toString();
        } else {
            return super.getSelectQueryFromClause(pQuery, pData);
        }
    }

    protected String getSelectQueryConstraints(SelectStatement pQuery, StatementMetaData pData,
                                               StatementMetaData.LocalData pLocalData) {
        String result;
        if (isOracle8Compatibility()) {
            SelectTableReference tableReference = pQuery.getSelectTableReference();
            if (tableReference == null) {
                result = super.getSelectQuery(pQuery, pData);
            } else {
	            StringBuffer sb = new StringBuffer();
                for (JoinReference joinReference = tableReference.getRightJoinedTableReference();
           	 		 joinReference != null;
           	 		 joinReference = joinReference.getRightJoinedTableReference()) {
	                OraJoinReferenceImpl oraJoin = null;
	                if (joinReference instanceof OraJoinReferenceImpl) {
	                    oraJoin = (OraJoinReferenceImpl) joinReference;
	                }
	                String onClause;
	                if (oraJoin == null) {
	                    onClause = getWhereClause(pData, joinReference.getOn());
	                } else {
	                    oraJoin.setIsOracle8LeftOuterJoin(true);
	                    onClause = getWhereClause(pData, joinReference.getOn());
	                    oraJoin.setIsOracle8LeftOuterJoin(false);
	                }
	                if (onClause != null  &&  onClause.length() > 0) {
                        if (pLocalData.hasWhereClause()) {
                            sb.append(" AND ");
                        } else {
                        	pLocalData.setWhereClause(true);
                        }
		                sb.append(onClause);
	                }
	            }
	            String whereClause = getWhereClause(pData, pQuery.getWhere());
	            if (whereClause != null  &&  whereClause.length() > 0) {
                    if (pLocalData.hasWhereClause()) {
                        sb.append(" AND ");
                    } else {
                        pLocalData.setWhereClause(true);
                    }
	                sb.append(whereClause);
	            }
	            result = sb.toString();
            }
        } else {
            result = super.getSelectQueryConstraints(pQuery, pData, pLocalData);
        }

        if (pQuery instanceof OraSelectStatement) {
            OraSelectStatement oraStatement = (OraSelectStatement) pQuery;
            String s = getWhereClause(pData, oraStatement.getStartWith());
            if (s != null  &&  s.length() > 0) {
                result += " START WITH " + s;
            }
            s = getWhereClause(pData, oraStatement.getConnectBy());
            if (s != null  &&  s.length() > 0) {
                result += " CONNECT BY " + s;
            }
        }

        return result;
    }

    protected String getColumnAlias(StatementMetaData pData, ColumnReference pColumn) {
        String s = super.getColumnAlias(pData, pColumn);
        if (pColumn.getTableReference() instanceof OraJoinReferenceImpl) {
            OraJoinReferenceImpl oraJoin = (OraJoinReferenceImpl) pColumn.getTableReference();
            if (oraJoin.isOracle8LeftOuterJoin()) {
                s = s + "(+)";
            }
        }
        if (pColumn instanceof OraColumnReference) {
            OraColumnReference oraRef = (OraColumnReference) pColumn;
            if (oraRef.isPrior()) {
                s = "PRIOR " + s;
            }
        }
        return s;
    }

    protected String getOrderColumn(StatementMetaData pData, SelectStatement.OrderColumn pColumn) {
        String s = super.getOrderColumn(pData, pColumn);
        if (pColumn instanceof OraSelectStatement.OraOrderColumn) {
            OraSelectStatement.OraOrderColumn ooc = (OraSelectStatement.OraOrderColumn) pColumn;
            if (ooc.isNullsFirst()) {
                s += " NULLS FIRST";
            }
        }
        return s;
    }
}
