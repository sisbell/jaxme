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

import java.util.Collection;


/** <p>An SQL generator.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface SQLGenerator {
  /** <p>Generates a <code>CREATE SCHEMA</code> statement. Doesn't create
   * <code>CREATE TABLE</code> or similar statements.</p>
   */
  public Collection getCreate(Schema pSchema);

  /** <p>Generates <code>CREATE</code> statements for the schema.</p>
   * @param pAll If this parameter is set to true, then the method is
   *    equivalent to  {@link #getCreate(Schema)}. Otherwise the returned
   *    collection will also include <code>CREATE</code> statements for
   *    all the tables and indexes in the schema. These additional statements
   *    are created by invoking {@link #getCreate(Table,boolean)} for all the
   *    tables in the schema.</p>
   */
  public Collection getCreate(Schema pSchema, boolean pAll);

  /** <p>Generates a DROP SCHEMA statement. Doesn't create
   * <code>DROP TABLE</code> or similar statements.</p>
   */
  public Collection getDrop(Schema pSchema);

  /** <p>Generates <code>DROP</code> statements for the schema.</p>
   * @param pAll If this parameter is set to true, then the method is
   *    equivalent to  {@link #getDrop(Schema)}. Otherwise the returned
   *    collection will also include <code>DROP</code> statements for
   *    all the tables and indexes in the schema. These additional statements
   *    are created by invoking {@link #getDrop(Table,boolean)} for all the
   *    tables in the schema.</p>
   */
  public Collection getDrop(Schema pSchema, boolean pAll);

  /** <p>Generates a CREATE TABLE statement. Doesn't create
   * <code>CREATE INDEX</code> or similar statements.</p>
   */
  public Collection getCreate(Table pTable);

  /** <p>Generates <code>CREATE</code> statements for the table.</p>
   * @param pAll If this parameter is set to true, then the method is
   *    equivalent to  {@link #getCreate(Table)}. Otherwise the returned
   *    collection will also include <code>CREATE</code> statements for
   *    the indexes, which are defined on the table. These additional
   *    statements are created by invoking {@link #getCreate(Index)}
   *    and {@link #getCreate(ForeignKey)} for all the indexes in the
   *    schema.</p>
   */
  public Collection getCreate(Table pTable, boolean pAll);

  /** <p>Generates a DROP TABLE statement. Doesn't create
   * <code>DROP INDEX</code> or similar statements.</p>
   */
  public Collection getDrop(Table pTable);

  /** <p>Generates <code>DROP</code> statements for the table.</p>
   * @param pAll If this parameter is set to true, then the method is
   *    equivalent to  {@link #getDrop(Table)}. Otherwise the returned
   *    collection will also include <code>DROP</code> statements for
   *    the indexes, which are defined on the table. These additional
   *    statements are created by invoking {@link #getDrop(Index)}
   *    and {@link #getDrop(ForeignKey)} for all the indexes in the
   *    schema.</p>
   */
  public Collection getDrop(Table pTable, boolean pAll);

  /** <p>Generates a CREATE INDEX statement.</p>
   */
  public Collection getCreate(Index pIndex);

  /** <p>Generates a DROP INDEX statement.</p>
   */
  public Collection getDrop(Index pIndex);

  /** <p>Generates a CREATE FOREIGN KEY statement.</p>
   */
  public Collection getCreate(ForeignKey pKey);

  /** <p>Generates a DROP FOEIGN KEY statement.</p>
   */
  public Collection getDrop(ForeignKey pKey);

  /** <p>Generates an INSERT, UPDATE, DELETE or SELECT statement.</p>
   */
  public String getQuery(Statement pStatement);

  /** <p>Generates the WHERE clause of a SELECT, UPDATE, or DELETE statement.</p>
   */
  public String getConstraint(Constraint pConstraint);

  /** <p>Returns the <code>WHERE ... ORDER BY ...</code> part
   * of the SELECT statement.</p>
   */
  public String getWhereClause(SelectStatement pQuery);

  /** <p>Sets the statement terminator. A non-null value will be
   * appended to all generated statements. Defaults to null.</p>
   */
  public void setStatementTerminator(String pTerminator);

  /** <p>Returns the statement terminator. A non-null value will be
   * appended to all generated statements. Defaults to null.</p>
   */
  public String getStatementTerminator();

  /** <p>Sets the line terminator. A non-null value indicates that
   * the generated statements should be made human readable by splitting
   * them over multiple lines. A null value ensures that a statement
   * consists of a single line only. Defaults to "\n".</p>
   */
  public void setLineTerminator(String pTerminator);

  /** <p>Returns the line terminator. A non-null value indicates that
   * the generated statements should be made human readable by splitting
   * them over multiple lines. A null value ensures that a statement
   * consists of a single line only. Defaults to "\n".</p>
   */
  public String getLineTerminator();
}
