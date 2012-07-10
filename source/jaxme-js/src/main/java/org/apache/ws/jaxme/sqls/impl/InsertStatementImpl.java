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

import org.apache.ws.jaxme.sqls.Function;
import org.apache.ws.jaxme.sqls.InsertStatement;
import org.apache.ws.jaxme.sqls.SQLFactory;
import org.apache.ws.jaxme.sqls.SelectStatement;
import org.apache.ws.jaxme.sqls.Column.Name;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class InsertStatementImpl extends SetStatementImpl implements InsertStatement {
	private SelectStatement st;

    /** <p>Creates a new instance of InsertStatementImpl.</p>
	 */
	InsertStatementImpl(SQLFactory pFactory) {
		super(pFactory);
	}

	public void setSubSelect(SelectStatement pStatement) {
        st = pStatement;
	}

	public SelectStatement getSubSelect() {
        return st;
	}
}
