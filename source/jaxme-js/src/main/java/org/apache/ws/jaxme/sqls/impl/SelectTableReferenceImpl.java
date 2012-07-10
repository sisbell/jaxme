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

import org.apache.ws.jaxme.sqls.JoinReference;
import org.apache.ws.jaxme.sqls.SelectStatement;
import org.apache.ws.jaxme.sqls.SelectTableReference;
import org.apache.ws.jaxme.sqls.Table;

/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class SelectTableReferenceImpl extends TableReferenceImpl implements SelectTableReference {
   private JoinReference joinReference;

	/** <p>Creates a new instance of SelectTableReferenceImpl referencing
    * the given {@link SelectStatement}.</p>
	 */
	public SelectTableReferenceImpl(SelectStatement pStatement, Table pTable) {
		super(pStatement, pTable);
	}

	public JoinReference join(Table pTable) {
	  if (joinReference != null) {
	    throw new IllegalStateException("The table is already involved in a left join.");
	  }
	  joinReference = getStatement().getSQLFactory().getObjectFactory().newJoinReference(this, pTable, false);
	  return joinReference;
	}
	
	public JoinReference leftOuterJoin(Table pTable) {
	  if (joinReference != null) {
	    throw new IllegalStateException("The table is already involved in a left join.");
	  }
	  joinReference = getStatement().getSQLFactory().getObjectFactory().newJoinReference(this, pTable, true);
	  return joinReference;
	}
	
	public SelectStatement getSelectStatement() {
	  return (SelectStatement) getStatement();
	}
	
	public JoinReference getRightJoinedTableReference() {
	  return joinReference;
	}
}
