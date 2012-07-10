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

import java.sql.Types;

import org.apache.ws.jaxme.sqls.impl.ColumnImpl;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface Column {
  public interface Name extends SQLFactory.Ident {
  }

  public interface Type {
    /** <p>Returns the types name. The types name is human readable.</p>
     */
    public String getName();
    /** <p>Returns the types JDBC type. The JDBC type is a constant,
     * as specified by {@link java.sql.Types}.</p>
     */
    public int getJDBCType();

    public static final Type BIGINT = new ColumnImpl.TypeImpl("BIGINT", Types.BIGINT);
    public static final Type BINARY = new ColumnImpl.TypeImpl("BINARY", Types.BINARY);
    public static final Type BIT = new ColumnImpl.TypeImpl("BIT", Types.BIT);
    public static final Type CHAR = new ColumnImpl.TypeImpl("CHAR", Types.CHAR);
    public static final Type DATE = new ColumnImpl.TypeImpl("DATE", Types.DATE);
    public static final Type INTEGER = new ColumnImpl.TypeImpl("INTEGER", Types.INTEGER);
    public static final Type FLOAT = new ColumnImpl.TypeImpl("FLOAT", Types.FLOAT);
    public static final Type DOUBLE = new ColumnImpl.TypeImpl("DOUBLE", Types.DOUBLE);
    public static final Type SMALLINT = new ColumnImpl.TypeImpl("SMALLINT", Types.SMALLINT);
    public static final Type TIME = new ColumnImpl.TypeImpl("TIME", Types.TIME);
    public static final Type TIMESTAMP = new ColumnImpl.TypeImpl("TIMESTAMP", Types.TIMESTAMP);
    public static final Type TINYINT = new ColumnImpl.TypeImpl("TINYINT", Types.TINYINT);
    public static final Type VARCHAR = new ColumnImpl.TypeImpl("VARCHAR", Types.VARCHAR);
    public static final Type VARBINARY = new ColumnImpl.TypeImpl("VARBINARY", Types.VARBINARY);
    public static final Type BLOB = new ColumnImpl.TypeImpl("CLOB", Types.BLOB);
    public static final Type OTHER = new ColumnImpl.TypeImpl("OTHER", Types.OTHER);
    public static final Type CLOB = new ColumnImpl.TypeImpl("CLOB", Types.CLOB);
  }

  /** <p>Returns the columns table.</p>
   */
  public Table getTable();

  /** <p>Returns the columns name.</p>
   */
  public Name getName();

  /** <p>Returns the columns fully qualified name, which is
   * <code>getTable().getQName() + "." + getName()</code>.</p>
   */
  public String getQName();

  /** <p>Returns the columns type.</p>
   */
  public Type getType();

  /** <p>Returns whether this column is part of the primary key.</p>
   */
  public boolean isPrimaryKeyPart();

  /** <p>Sets whether the column is nullable. By default columns are not
   * nullable.</p>
   */
  public void setNullable(boolean pNullable);

  /** <p>Returns whether the column is nullable. By default columns are not
   * nullable.</p>
   */
  public boolean isNullable();

  /** <p>Returns whether this Column may be casted to a {@link StringColumn}.</p>
   */
  public boolean isStringColumn();

  /** <p>Returns whether this Column may be casted to a {@link BinaryColumn}.</p>
   */
  public boolean isBinaryColumn();

  /** <p>Allows the user to attach application specific data to the column.</p>
   */
  public void setCustomData(Object pData);

  /** <p>Allows the user to retrieve application specific data, which has
   * previously been attached to the column.</p>
   */
  public Object getCustomData();

  /** <p>Returns whether this column is a true column or a virtual column.</p>
   */
  public boolean isVirtual();
}
