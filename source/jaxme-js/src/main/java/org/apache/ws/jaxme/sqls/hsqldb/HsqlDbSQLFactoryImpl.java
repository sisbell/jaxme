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
package org.apache.ws.jaxme.sqls.hsqldb;

import org.apache.ws.jaxme.sqls.Column;
import org.apache.ws.jaxme.sqls.SQLGenerator;
import org.apache.ws.jaxme.sqls.Schema;
import org.apache.ws.jaxme.sqls.Table;
import org.apache.ws.jaxme.sqls.impl.SQLFactoryImpl;

/** <p>Default implementation of an SQL factory for HsqlDb databases.
 * This factory ensures that the created implementations of
 * {@link Schema}, {@link Table}, {@link Column}, and {@link SQLGenerator}
 * may be casted to {@link HsqlDbSchema}, {@link HsqlDbTable}, {@link HsqlDbColumn},
 * {@link HsqlDbSQLGenerator}, respectively.</p>
 */
public class HsqlDbSQLFactoryImpl extends SQLFactoryImpl implements HsqlDbSQLFactory {
   public Schema newSchemaImpl(Schema.Name pName) {
     if (pName != null) {
       throw new IllegalArgumentException("The HsqlDb database is supporting the default schema only.");
     }
     return new HsqlDbSchemaImpl(this, pName);
   }

   public Table newTableImpl(Schema pSchema, Table.Name pName) {
     return new HsqlDbTableImpl(pSchema, pName);
   }

   public Column newColumn(Table pTable, Column.Name pName, Column.Type pType) {
     return new HsqlDbColumnImpl(pTable, pName, pType);
   }

   public SQLGenerator newSQLGenerator() {
     return new HsqlDbSQLGeneratorImpl();
   }
}
