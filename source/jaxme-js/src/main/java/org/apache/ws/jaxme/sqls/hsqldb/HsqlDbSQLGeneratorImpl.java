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

import org.apache.ws.jaxme.sqls.Table;
import org.apache.ws.jaxme.sqls.impl.SQLGeneratorImpl;


/** <p>Default implementation of an SQL generator for HsqlDb schemas.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class HsqlDbSQLGeneratorImpl extends SQLGeneratorImpl implements HsqlDbSQLGenerator {
   protected boolean isPrimaryKeyUniqueIndex() {
      return false;
   }

   protected boolean isPrimaryKeyPartOfCreateTable() {
      return true;
   }

   protected boolean isUniqueIndexPartOfCreateTable() {
      return true;
   }

   protected boolean isNonUniqueIndexPartOfCreateTable() {
      return false;
   }

   protected boolean isForeignKeyPartOfCreateTable() {
      return true;
   }

   protected String getCreateTableHeader(Table pTable) {
      if (pTable instanceof HsqlDbTable) {
         HsqlDbTable table = (HsqlDbTable) pTable;
         if (table.isCached()) {
            return "CREATE CACHED TABLE " + pTable.getQName();
         }
      }
      return super.getCreateTableHeader(pTable);
   }
}
