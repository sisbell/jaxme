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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;


/** <p>A factory for generating SQL statements.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface SQLFactory {
  public interface Ident {
    /** <p>Returns the SQL identifiers String representation. Typically
     * same than {@link Object#toString()}.</p>
     */
    public String getName();
  }

  /** <p>Returns the object factory being used.</p>
   */
  public ObjectFactory getObjectFactory();

  /** <p>Returns the maximum length of a table name.</p>
   *
   * @return The maximum length or null, if checks for valid table name
   *   length are disabled.
   */
  public Integer getMaxTableNameLength();

  /** <p>Returns whether table names are case sensitive or not. Defaults to
   * false.</p>
   */
  public boolean isTableNameCaseSensitive();

  /** <p>Returns the maximum length of a schema name.</p>
   *
   * @return The maximum length or null, if checks for valid schema name
   *   length are disabled.
   */
  public Integer getMaxSchemaNameLength();

  /** <p>Returns whether schema names are case sensitive or not. Defaults to
   * false.</p>
   */
  public boolean isSchemaNameCaseSensitive();

  /** <p>Returns the maximum length of a column name.</p>
   *
   * @return The maximum length or null, if checks for valid column name
   *   length are disabled.
   */
  public Integer getMaxColumnNameLength();

  /** <p>Returns whether column names are case sensitive or not. Defaults to
   * false.</p>
   */
  public boolean isColumnNameCaseSensitive();

  /** <p>Creates a new SELECT statement.</p>
   */
  public SelectStatement newSelectStatement();

  /** <p>Creates a new INSERT statement.</p>
   */
  public InsertStatement newInsertStatement();

  /** <p>Creates a new UPDATE statement.</p>
   */
  public UpdateStatement newUpdateStatement();

  /** <p>Creates a new DELETE statement.</p>
   */
  public DeleteStatement newDeleteStatement();

  /** <p>Creates a new {@link Schema} with the given name.</p>
   */
  public Schema newSchema(String pName);

  /** <p>Creates a new {@link Schema} with the given name.</p>
   */
  public Schema newSchema(Schema.Name pName);

  /** <p>Returns the {@link Schema Default schema}. The default
   * schema has the name null.</p>
   */
  public Schema getDefaultSchema();

  /** <p>Returns the schema with the given name or null, if no such
   * schema exists.</p>
   */
  public Schema getSchema(Schema.Name pName);

  /** <p>Returns the schema with the given name or null, if no such
   * schema exists.</p>
   */
  public Schema getSchema(String pName);

  /** <p>Returns a list of all schemas. The list includes the default
   * schema, if {@link #getDefaultSchema()} was called at any time.</p>
   */
  public Iterator getSchemas();

  /** <p>Creates a new {@link SQLGenerator}.</p>
   */
  public SQLGenerator newSQLGenerator();

  /** <p>Reads the schema named <code>pName</code> from the database.</p>
   */
  public Schema getSchema(Connection pConnection, Schema.Name pName) throws SQLException;

  /** <p>Reads the schema named <code>pName</code> from the database.</p>
   */
  public Schema getSchema(Connection pConnection, String pName) throws SQLException;

  /** <p>Reads the table named <code>pTable</code> from the schema
   * named <code>pSchema</code> in the database.</p>
   */
  public Table getTable(Connection pConnection, Schema.Name pSchema, Table.Name pTable) throws SQLException;

  /** <p>Reads the table named <code>pTable</code> from the schema
   * named <code>pSchema</code> in the database.</p>
   */
  public Table getTable(Connection pConnection, String pSchema,String pTable) throws SQLException;
}
