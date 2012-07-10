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

import java.util.Iterator;


/** <p>Interface of a database schema.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface Schema {
   public interface Name extends SQLFactory.Ident {
   }

   /** <p>Returns the {@link org.apache.ws.jaxme.sqls.SQLFactory}
    * that created this instance of Schema.</p>
    */
   public SQLFactory getSQLFactory();

   /** <p>Returns the schema name.</p>
    */
   public Name getName();   

   /** <p>Creates a new table with the given name in the schema.</p>
    */
   public Table newTable(String pName);

   /** <p>Creates a new table with the given name in the schema.</p>
    */
   public Table newTable(Table.Name pName);

   /** <p>Returns the table with the given name or null, if no such table exists
    * in the schema.</p>
    */
   public Table getTable(Table.Name pName);

   /** <p>Returns the table with the given name or null, if no such table exists
    * in the schema.</p>
    */
   public Table getTable(String pName);

   /** <p>Returns an {@link Iterator} to all tables in the schema.</p>
    */
   public Iterator getTables();
}
