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

/** <p>A TableReference being used in a SELECT statement. You
 * may cast the result of
 * {@link org.apache.ws.jaxme.sqls.SelectStatement#getTableReference}
 * to an instance of SelectTableReference.</p>
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface SelectTableReference extends TableReference {
   /** <p>Returns the {@link SelectStatement} that created the reference.
    * Shortcut for <code>(SelectStatement) getStatement()</code>.</p>
    */
   public SelectStatement getSelectStatement();

   /** <p>Indicates that the referenced table shall be joined
    * with the given table <code>pTable</code> and returns a
    * reference to that table.</p>
    */
   public JoinReference join(Table pTable);

   /** <p>Indicates that the referenced table shall be joined
    * in a left outer join with the given table <code>pTable</code>
    * and returns a reference to that table.</p>
    */
   public JoinReference leftOuterJoin(Table pTable);

   /** <p>Returns the right table of a possible join or null,
    * if there is no such table.</p>
    */
   public JoinReference getRightJoinedTableReference();
}
