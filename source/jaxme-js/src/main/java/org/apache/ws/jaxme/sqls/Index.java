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


/** <p>Interface of an index declaration.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface Index extends ColumnSet {
   public interface Name extends SQLFactory.Ident {
   }

   /** <p>Sets the index name. Explicit setting of an index name
    * is not required.</p>
    */
   public void setName(Index.Name pName);
   /** <p>Sets the index name. Explicit setting of an index name
    * is not required.</p>
    */
   public void setName(String pName);
   /** <p>Returns the index name. Explicit setting of an index name
    * is not required.</p>
    */
   public Index.Name getName();
   /** <p>Adds a column to the index. The column must have the
    * same table.</p>
    */
   public void addColumn(Column pColumn);

   /** <p>Adds the column with the given name to the index
    * by invoking {@link #addColumn(Column)}.</p>
    */
   public void addColumn(Column.Name pName);

   /** <p>Adds the column with the given name to the index
    * by invoking {@link #addColumn(Column)}.</p>
    */
   public void addColumn(String pName);

   /** <p>Returns whether the index is unique.</p>
    */
   public boolean isUnique();

   /** <p>Returns whether the index is a primary key index.</p>
    */
   public boolean isPrimaryKey();
}
