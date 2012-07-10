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

/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface Statement {
   /** <p>Returns the {@link org.apache.ws.jaxme.sqls.SQLFactory}
    * that created this Statement.</p>
    */
   public SQLFactory getSQLFactory();

   /** <p>Sets the table, for which the statement applies and returns
    * a reference to the table.</p>
    */
   public TableReference setTable(Table pTable);

   /** <p>Returns the table reference, for which the statement applies.</p>
    */
   public TableReference getTableReference();

   /** <p>Creates a new function, which may be added to a
    * {@link org.apache.ws.jaxme.sqls.BooleanConstraint}.</p>
    */
   public Function createFunction(String pName);

   /** Creates a new instance of {@link Case}.
    */
   public Case newCase(Column.Type pType);
}
