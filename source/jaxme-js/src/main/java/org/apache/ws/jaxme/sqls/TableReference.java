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

/** <p>A table reference is used in the {@link Statement}.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface TableReference {
   /** <p>Returns the {@link Statement} that created the reference.</p>
    */
   public Statement getStatement();

   /** <p>Returns the referenced {@link Table}.</p>
    */
   public Table getTable();

   /** <p>Returns the references alias name. Null indicates that
    * an arbitrary alias name may be choosen.</p>
    */
   public Table.Name getAlias();

   /** <p>Sets the references alias name. Null indicates that
    * an arbitrary alias name may be choosen.</p>
    */
   public void setAlias(Table.Name pName);

   /** <p>Returns the references alias name. Null indicates that
    * an arbitrary alias name may be choosen.</p>
    */
   public void setAlias(String pName);

   /** <p>Returns a reference to the column named <code>pName</code>
    * in the table.</p>
    */
   public ColumnReference newColumnReference(String pName);

   /** <p>Returns a reference to the column named <code>pName</code>
    * in the table.</p>
    */
   public ColumnReference newColumnReference(Column.Name pName);

   /** <p>Returns a reference to the given column in the table.</p>
    */
   public ColumnReference newColumnReference(Column pColumn);
}
