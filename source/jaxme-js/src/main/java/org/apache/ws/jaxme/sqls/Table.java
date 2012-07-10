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


/** <p>Abstract description of a table.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface Table {
   public interface Name extends SQLFactory.Ident {
   }

   /** <p>Returns the table name.</p>
    */
   public Name getName();

   /** <p>Returns the table schema.</p>
    */
   public Schema getSchema();

   /** <p>Returns the table columns.</p>
    */
   public Iterator getColumns();

   /** <p>Creates a new column.</p>
    */
   public Column newColumn(Column.Name name, Column.Type pType);

   /** <p>Creates a new column.</p>
    */
   public Column newColumn(String name, Column.Type pType);

   /** <p>Returns the column with the given name or null,
    * if no such column exists.</p>
    */
   public Column getColumn(Column.Name name);

   /** <p>Returns the column with the given name or null,
    * if no such column exists.</p>
    */
   public Column getColumn(String name);

   /** <p>Creates a new, unique index on the table.</p>
    */
   public Index newKey();

   /** <p>Creates a new, non-unique index on the table.</p>
    */
   public Index newIndex();

   /** <p>Creates a new primary key on the table.</p>
    * @throws IllegalStateException A primary key has already been created.
    */
   public Index newPrimaryKey();

   /** <p>Creates a new foreign key referencing the given table.</p>
    */
   public ForeignKey newForeignKey(Table pReferencedTable);

  /** <p>Returns an INSERT statement for filling all the values. In
   * other words: If the table FOO has the columns A, B, and C,
   * then the statement <code>INSERT INTO FOO (A,B,C) VALUES (?, ?, ?)</code>
   * will be returned.</p>
   * @see SQLFactory#newInsertStatement()
   */
  public InsertStatement getInsertStatement();

  /** <p>Returns a SELECT statement for selecting all the columns. In
   * other words: If the table FOO has the columns A, B, and C,
   * then the statement <code>SELECT A, B, C FROM FOO</code> will
   * be returned.</p>
   * @see SQLFactory#newSelectStatement()
   */
  public SelectStatement getSelectStatement();

  /** <p>Returns an UPDATE statement for updating a column in the table.
   * In other words: If the table FOO has the columns A, B, C and D
   * with the primary key columns A and B, then the statement
   * <code>UPDATE FOO SET C = ?, D = ? WHERE A = ? AND B = ?</code>
   * will be returned.</p>
   * @see SQLFactory#newUpdateStatement()
   * @throws IllegalStateException The table doesn't have a primary key.
   */
  public UpdateStatement getUpdateStatement();

  /** <p>Returns an UPDATE statement for updating a column in the table.
   * In other words: If the table FOO has the primary key columns A and B,
   * then the statement <code>DELETE FROM FOO WHERE A = ? AND B = ?</code>
   * will be returned.</p>
   * @see SQLFactory#newDeleteStatement()
   * @throws IllegalStateException The table doesn't have a primary key.
   */
  public DeleteStatement getDeleteStatement();

  /** <p>Returns the tables qualified name, which is
   * <code>getSchema().getName() + "." + getName()</code>. If the
   * schema is the default schema, returns <code>getName()</code>.</p> 
   */
  public String getQName();

  /** <p>Returns the tables primary key, if any, or null, if the table
   * doesn't have a primary key.</p>
   */
  public Index getPrimaryKey();

  /** <p>Returns an {@link Iterator} to the indexes defined on the table.
   * This iterator includes the primary key, if any.</p>
   */
  public Iterator getIndexes();

  /** <p>Returns an {@link Iterator} to the foreign keys defined on the
   * table.</p>
   */
  public Iterator getForeignKeys();
}
