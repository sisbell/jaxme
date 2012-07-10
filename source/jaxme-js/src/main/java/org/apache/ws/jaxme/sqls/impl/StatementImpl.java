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
import org.apache.ws.jaxme.sqls.Function;
import org.apache.ws.jaxme.sqls.SQLFactory;
import org.apache.ws.jaxme.sqls.Statement;
import org.apache.ws.jaxme.sqls.Table;
import org.apache.ws.jaxme.sqls.TableReference;


/** <p>A common base class for {@link org.apache.ws.jaxme.sqls.SelectStatement},
 * {@link org.apache.ws.jaxme.sqls.InsertStatement},
 * {@link org.apache.ws.jaxme.sqls.UpdateStatement}, and
 * {@link org.apache.ws.jaxme.sqls.DeleteStatement}.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public abstract class StatementImpl implements Statement {
  private SQLFactory factory;
  private TableReference tableReference;
  
  protected StatementImpl(SQLFactory pFactory) {
    factory = pFactory;
  }
  
  public SQLFactory getSQLFactory() {
    return factory;
  }
  
  protected TableReference newTableReference(Table pTable) {
    return new TableReferenceImpl(this, pTable);
  }
  
  public TableReference setTable(Table pTable) {
    if (pTable == null) {
      throw new NullPointerException("The table on which the statement operates must not be null.");
    }
    if (tableReference != null) {
      throw new IllegalStateException("The table on which the statement operates was already set.");
    }
    tableReference = newTableReference(pTable);
    return tableReference;
  }
  
  public TableReference getTableReference() {
    return tableReference;
  }
  
  public Function createFunction(String pName) {
    return getSQLFactory().getObjectFactory().newFunction(this, pName);
  }

    public Case newCase(Column.Type pType) {
    	return getSQLFactory().getObjectFactory().newCase(pType);
    }
}
