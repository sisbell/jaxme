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

import org.apache.ws.jaxme.sqls.SQLFactory;
import org.apache.ws.jaxme.sqls.db2.DB2SQLFactoryImpl;


/**
 * @author <a href="mailto:jwi@softwareag.com">Jochen Wiedmann</a>
 */
public class DB2Test extends CreateTest {
   public DB2Test(String pName) { super(pName); }

   protected SQLFactory newSQLFactory() {
      return new DB2SQLFactoryImpl();
   }

   protected String getCreateForeignKeyResult() {
      return "CREATE TABLE MySchema.OtherTable (" +
        "  MyIndex INT NOT NULL," +
        "  RefIndex INT NOT NULL," +
        "  Company VARCHAR(60) NOT NULL,"+
        "  PRIMARY KEY (MyIndex)," +
        "  FOREIGN KEY (RefIndex) REFERENCES MySchema.MyTable (MyIndex))";
   }
}
