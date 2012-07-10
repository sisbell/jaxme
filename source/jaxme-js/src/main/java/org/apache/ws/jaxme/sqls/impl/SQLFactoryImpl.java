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
package org.apache.ws.jaxme.sqls.impl;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.ws.jaxme.logging.Logger;
import org.apache.ws.jaxme.logging.LoggerAccess;
import org.apache.ws.jaxme.sqls.BinaryColumn;
import org.apache.ws.jaxme.sqls.Column;
import org.apache.ws.jaxme.sqls.DeleteStatement;
import org.apache.ws.jaxme.sqls.ForeignKey;
import org.apache.ws.jaxme.sqls.Index;
import org.apache.ws.jaxme.sqls.InsertStatement;
import org.apache.ws.jaxme.sqls.ObjectFactory;
import org.apache.ws.jaxme.sqls.SQLFactory;
import org.apache.ws.jaxme.sqls.SQLGenerator;
import org.apache.ws.jaxme.sqls.Schema;
import org.apache.ws.jaxme.sqls.SelectStatement;
import org.apache.ws.jaxme.sqls.StringColumn;
import org.apache.ws.jaxme.sqls.Table;
import org.apache.ws.jaxme.sqls.UpdateStatement;


/** <p>Default implementation of an SQLFactory.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class SQLFactoryImpl implements SQLFactory {
  private static final Logger logger = LoggerAccess.getLogger(SQLFactoryImpl.class);

  /** Base class for deriving identifiers.
   */
  public static class IdentImpl implements SQLFactory.Ident, Serializable {
    private String name;

    protected IdentImpl(String pName) {
      if (pName == null) {
        throw new NullPointerException("An ident's string representation must not be null.");
      }
      name = pName;
    }

    public String getName() {
      return name;
    }

    public String toString() {
      return name;
    }

    public boolean equals(Object o) {
      if (o == null  ||  !(o instanceof SQLFactory.Ident)) {
        return false;
      }
      return name.equalsIgnoreCase(((SQLFactory.Ident) o).getName());
    }
    public int hashCode() {
      return name.hashCode();
    }
  }

  private List schemas = new ArrayList();
  private Integer maxTableNameLength, maxSchemaNameLength, maxColumnNameLength;
  private boolean tableNameCaseSensitive, schemaNameCaseSensitive, columnNameCaseSensitive;
  private Schema defaultSchema;
  private ObjectFactory objectFactory = newObjectFactory();

  /** <p>Creates a new instance of SQLFactoryImpl.</p>
   */
  public SQLFactoryImpl() {
  }

  protected ObjectFactory newObjectFactory() {
    return new ObjectFactoryImpl();
  }

  public ObjectFactory getObjectFactory() {
    return objectFactory;
  }

  protected void setObjectFactory(ObjectFactory pFactory) {
    objectFactory = pFactory;
  }

  /** <p>Sets the maximum length of a table name.</p>
   * @param pMaxLength The maximum length or null to disable checks for
   *   valid table name length.
   */
  public void setMaxTableNameLength(Integer pMaxLength) {
    maxTableNameLength = pMaxLength;
  }

  public Integer getMaxTableNameLength() {
    return maxTableNameLength;
  }

  /** <p>Sets whether table names are case sensitive or not. Defaults
   * to false.</p>
   * @see SQLFactory#isTableNameCaseSensitive()
   */
  public void setTableNameCaseSensitive(boolean pTableNameCaseSensitive) {
    tableNameCaseSensitive = pTableNameCaseSensitive;
  }

  public boolean isTableNameCaseSensitive() {
    return tableNameCaseSensitive;
  }

  /** <p>Sets the maximum length of a column name.</p>
   * @param pMaxLength The maximum length or null to disable checks for
   *   valid column name length.
   */
  public void setMaxColumnNameLength(Integer pMaxLength) {
    maxColumnNameLength = pMaxLength;
  }

  public Integer getMaxColumnNameLength() {
    return maxColumnNameLength;
  }

  /** <p>Sets whether column names are case sensitive or not. Defaults
   * to false.</p>
   * @see SQLFactory#isColumnNameCaseSensitive()
   */
  public void setColumnNameCaseSensitive(boolean pColumnNameCaseSensitive) {
    columnNameCaseSensitive = pColumnNameCaseSensitive;
  }
 
  public boolean isColumnNameCaseSensitive() {
    return columnNameCaseSensitive;
  }

  /** <p>Sets whether schema names are case sensitive or not. Defaults
   * to false.</p>
   * @see SQLFactory#isSchemaNameCaseSensitive()
   */
  public void setSchemaNameCaseSensitive(boolean pSchemaNameCaseSensitive) {
    schemaNameCaseSensitive = pSchemaNameCaseSensitive;
  }

  public boolean isSchemaNameCaseSensitive() {
    return schemaNameCaseSensitive;
  }

  /** <p>Sets the maximum length of a schema name.</p>
   * @param pMaxLength The maximum length or null to disable checks for
   *   valid schema name length.
   */
  public void setMaxSchemaNameLength(Integer pMaxLength) {
    maxSchemaNameLength = pMaxLength;
  }

  public Integer getMaxSchemaNameLength() {
    return maxSchemaNameLength;
  }

  /** Converths the given string into an SQL identifier.
   */
  public SQLFactory.Ident newIdent(String pName) {
    if (pName == null) {
      throw new NullPointerException("An SQL identifier must not be null.");
    }
    if (pName.length() == 0) {
      throw new IllegalArgumentException("An SQL identifier must not be empty.");
    }
    char c = pName.charAt(0);
    if ((c < 'A'  ||  c > 'Z')  &&  (c < 'a'  ||  c > 'z')) {
      throw new IllegalArgumentException("An SQL identifiers first character must be A..Z");
    }
    for (int i = 1;  i < pName.length();  i++) {
      c = pName.charAt(i);
      if ((c < 'A'  ||  c > 'Z')  &&  (c < 'a'  ||  c > 'z')  &&  (c < '0'  ||  c > '9')) {
        throw new IllegalArgumentException("An SQL identifier must not contain the character " +
					  c + ", only A..Z, a..z, or 0..9 are allowed.");
      }
    }
    return new IdentImpl(pName);
  }

  public Schema newSchema(String pName) {
    if (pName == null) {
      throw new NullPointerException("A schema name must not be null.");
    }
    return newSchema(new SchemaImpl.NameImpl(pName));
  }

  public Schema newSchema(Schema.Name pName) {
    if (pName == null) {
      throw new NullPointerException("A schema name must not be null.");
    }
    Integer maxLength = getMaxSchemaNameLength();
    if (maxLength != null  &&
	pName.getName().length() > maxLength.intValue()) {
      throw new IllegalArgumentException("The length of the schema name " + pName +
					 " exceeds the valid maximum of " + maxLength);
    }
    Schema schema = getSchema(pName);
    if (schema != null) {
      throw new IllegalStateException("A schema named " + schema.getName() + " already exists.");  
    }
    schema = newSchemaImpl(pName);
    schemas.add(schema);
    return schema;
  }

  public Schema getDefaultSchema() {
    if (defaultSchema == null) {
      defaultSchema = newSchemaImpl(null);
      schemas.add(defaultSchema);
    }
    return defaultSchema;
   }

  public Schema getSchema(String pName) {
    return getSchema(new SchemaImpl.NameImpl(pName));
  }

  public Schema getSchema(Schema.Name pName) {
    if (pName == null) {
      throw new NullPointerException("A schema name must not be null.");
    }
    for (Iterator iter = getSchemas(); iter.hasNext();) {
      Schema schema = (Schema) iter.next();
      if (isSchemaNameCaseSensitive()) {
        if (pName.getName().equalsIgnoreCase(schema.getName().getName())) {
          return schema;
        }
      } else {
        if (pName.equals(schema.getName())) {
          return schema;
        }
      }
    }
    return null;
  }

  public Iterator getSchemas() {
    return schemas.iterator();
  }

  public SelectStatement newSelectStatement() {
    return new SelectStatementImpl(this);
  }

  public InsertStatement newInsertStatement() {
    return new InsertStatementImpl(this);
  }

  public UpdateStatement newUpdateStatement() {
    return new UpdateStatementImpl(this);
  }

  public DeleteStatement newDeleteStatement() {
    return new DeleteStatementImpl(this);
  }

  protected Schema newSchemaImpl(Schema.Name pName) {
    return new SchemaImpl(this, pName);
  }

  protected Table newTableImpl(Schema pSchema, Table.Name pName) {
    return new TableImpl(pSchema, pName);
  }

  protected Column newColumnImpl(Table pTable, Column.Name pName, Column.Type pType) {
    return new ColumnImpl(pTable, pName, pType);
  }

  public SQLGenerator newSQLGenerator() {
    return new SQLGeneratorImpl();
  }

  public Schema getSchema(Connection pConn, String pName) throws SQLException {
     return getSchema(pConn, pName == null ? null : new SchemaImpl.NameImpl(pName));
  }

  private class JDBCTable {
    private final String catalogName, schemaName, tableName, tableType;
    private final String toStringValue;
    private Table table;
    /** Creates a new instance loading data from the given catalog, schema
     * and table.
     */
    public JDBCTable(String pCatalogName, String pSchemaName, String pTableName,
                     String pTableType) {
      catalogName = pCatalogName;
      schemaName = pSchemaName;
      tableName = pTableName;
      tableType = pTableType;
      String s = tableName;
      if ((schemaName != null  &&  schemaName.length() > 0)  ||
          (catalogName != null  &&  catalogName.length() > 0)) {
        s = catalogName + "." + schemaName + "." + tableName;
        
      }
      toStringValue = s;
    }
    /** Returns the catalog name.
     */
    public String getCatalogName() { return catalogName; }
    /** Returns the schema name.
     */
    public String getSchemaName() { return schemaName; }
    /** Returns the table name.
     */
    public String getTableName() { return tableName; }
    /** Returns the table type.
     */
    public String getTableType() { return tableType; }
    public String toString() { return toStringValue; }
    /** Sets the associated instance of {@link Table}.
     */
    public void setTable(Table pTable) { table = pTable; }
    /** Returns the associated instance of {@link Table}.
     */
    public Table getTable() { return table; }

    public int hashCode() {
      int result = 0;
      if (catalogName != null) result += catalogName.hashCode();
      if (schemaName != null) result += schemaName.hashCode();
      if (tableName != null) result += tableName.hashCode();
      return result;
    }
    public boolean equals(Object o) {
      if (o == null  ||  !(o instanceof JDBCTable)) return false;
      JDBCTable t = (JDBCTable) o;
      if (catalogName == null) {
        if (t.catalogName != null) return false;
      } else {
        if (!catalogName.equals(t.catalogName)) return false;
      }
      if (schemaName == null) {
        if (t.schemaName != null) return false;
      } else {
        if (!schemaName.equals(t.schemaName)) return false;
      }
      if (tableName == null) {
        return t.tableName == null;
      } else {
        return tableName.equals(t.tableName);
      }
    }
  }

  protected Schema makeSchema(Schema.Name pName) {
    Schema schema;
    if (pName == null) {
       schema = getDefaultSchema();
    } else {
       schema = getSchema(pName);
       if (schema == null) {
          schema = newSchema(pName);
       }
    }
    return schema;
  }

  protected JDBCTable[] readTables(DatabaseMetaData pData, Schema.Name pSchema, Table.Name pTable) throws SQLException {
    final String mName = "readTables";
    List tables = new ArrayList();
    {
      ResultSet rs = pData.getTables(null, pSchema == null ? null : pSchema.getName(),
                                     pTable == null ? null : pTable.getName(), null);
      boolean isRsClosed = false;
      try {
        while (rs.next()) {
          JDBCTable table = new JDBCTable(rs.getString(1), rs.getString(2),
                                          rs.getString(3), rs.getString(4));
          if (!"TABLE".equals(table.getTableType())) {
            continue;
          }
   
          if (pSchema != null) {
            if (!table.getSchemaName().equals(pSchema.toString())) {
              continue;
            }
          }
          logger.finest(mName, "Found table " + table);
          tables.add(table);
        }
        isRsClosed = true;
        rs.close();
      } finally {
        if (!isRsClosed) { try { rs.close(); } catch (Throwable ignore) {} }
      }
    }
    return (JDBCTable[]) tables.toArray(new JDBCTable[tables.size()]);
  }

  protected Column readColumn(Table pTable, String pColumnName, int pDataType, String pTypeName,
                               long pColumnSize, int pDecimalDigits, int pNullable) {
                               Column.Type type;
    final String mName = "readColumn";
    switch (pDataType) {
      case Types.BIGINT:        type = Column.Type.BIGINT;    break;
      case Types.BINARY:        type = Column.Type.BINARY;    break;
      case Types.BIT:           type = Column.Type.BIT;       break;
      case Types.BLOB:          type = Column.Type.BLOB;      break;
      case 16:  /* Types.BOOLEAN, introduced in Java 1.4 */
      	                        type = Column.Type.BIT;       break;
      case Types.CHAR:          type = Column.Type.CHAR;      break;
      case Types.CLOB:          type = Column.Type.CLOB;      break;
      case Types.DATE:          type = Column.Type.DATE;      break;
      case Types.DOUBLE:        type = Column.Type.DOUBLE;    break;
      case Types.FLOAT:         type = Column.Type.FLOAT;     break;
      case Types.REAL:          type = Column.Type.DOUBLE;    break;
      case Types.INTEGER:       type = Column.Type.INTEGER;   break;
      case Types.LONGVARBINARY: type = Column.Type.VARBINARY; break;
      case Types.LONGVARCHAR:   type = Column.Type.VARCHAR;   break;
      case Types.OTHER:         type = Column.Type.OTHER;     break;
      case Types.SMALLINT:      type = Column.Type.SMALLINT;  break;
      case Types.TIMESTAMP:     type = Column.Type.TIMESTAMP; break;
      case Types.TIME:          type = Column.Type.TIME;      break;
      case Types.TINYINT:       type = Column.Type.TINYINT;   break;
      case Types.VARBINARY:     type = Column.Type.VARBINARY; break;
      case Types.VARCHAR:       type = Column.Type.VARCHAR;   break;
      default: throw new IllegalArgumentException("Column " + pColumnName +
                                                    " in table " + pTable.getQName() +
                                                    " has unknown JDBC data type " +
                                                    pDataType);
    }
    Column column = pTable.newColumn(pColumnName, type);
    logger.finest(mName, "Found column " + pColumnName);
    if (column instanceof StringColumn) {
      ((StringColumn) column).setLength(pColumnSize);
    } else if (column instanceof BinaryColumn) {
      ((BinaryColumn) column).setLength(pColumnSize);
    }
    if (pNullable == DatabaseMetaData.columnNullable) {
      column.setNullable(true);
    }
    return column;
  }

  protected Table readTable(DatabaseMetaData pData, Schema pSchema, JDBCTable pTable)
      throws SQLException {
    final String mName = "readTable";
    ResultSet rs = pData.getColumns(pTable.getCatalogName(), pTable.getSchemaName(),
                                    pTable.getTableName(), null);
    boolean isRsClosed = false;
    try {
      Table sqlTable = pSchema.newTable(pTable.getTableName());
      logger.finest(mName, "Looking for columns of " + pTable + "=" + sqlTable.getQName());
      while (rs.next()) {
        String columnName = rs.getString(4);
        int dataType = rs.getInt(5);
        String typeName = rs.getString(6);
        long columnSize = rs.getLong(7);
        int decimalDigits = rs.getInt(9);
        int isNullable = rs.getInt(11);
        readColumn(sqlTable, columnName, dataType, typeName, columnSize, decimalDigits, isNullable);
      }
      pTable.setTable(sqlTable);

      isRsClosed = true;
      rs.close();
      return sqlTable;
    } finally {
      if (!isRsClosed) { try { rs.close(); } catch (Throwable ignore) {} }
    }
  }

  protected Index readPrimaryKey(DatabaseMetaData pData, JDBCTable pTable) throws SQLException {
    final String mName = "readPrimaryKey";
    logger.finest(mName, "Looking for primary keys of " + pTable + "=" + pTable.getTable().getQName());
    ResultSet rs = pData.getPrimaryKeys(pTable.getCatalogName(),
                                        pTable.getSchemaName(),
                                        pTable.getTableName());
    Index primaryKey = null;
    boolean isRsClosed = false;
    try {
      while (rs.next()) {
        if (primaryKey == null) {
          primaryKey = pTable.getTable().newPrimaryKey();
        } 

        String columnName = rs.getString(4);
        logger.finest(mName, "Found column " + columnName);
        primaryKey.addColumn(columnName);
      }

      isRsClosed = true;
      rs.close();
    } finally {
      if (!isRsClosed) { try { rs.close(); } catch (Throwable ignore) {} }
    }
    return primaryKey;
  }

  protected ForeignKey[] readForeignKeys(DatabaseMetaData pData, JDBCTable pTable, JDBCTable[] pTables)
      throws SQLException {
    final String mName = "readForeignKeys";
    logger.finest(mName, "Looking for foreign keys of " + pTable + "=" + pTable.getTable().getQName());
    List result = new ArrayList();
    ResultSet rs = pData.getImportedKeys(pTable.getCatalogName(),
                                         pTable.getSchemaName(),
                                         pTable.getTableName());
    ForeignKey foreignKey = null;
    boolean isRsClosed = false;
    try {
      while (rs.next()) {
        JDBCTable referencedTable = new JDBCTable(rs.getString(1), rs.getString(2),
                                                  rs.getString(3), "TABLE");
        String referencedColumnName = rs.getString(4);
        String localColumnName = rs.getString(8);
        logger.finest(mName, "Found column " + localColumnName + " referencing " +
                      referencedColumnName + " in " + referencedTable);
        for (int i = 0;  i < pTables.length;  i++) {
          JDBCTable refIterTable = pTables[i];
          if (refIterTable.equals(referencedTable)) {
            referencedTable.setTable(refIterTable.getTable());
            break;
          }
        }
        if (referencedTable.getTable() == null) {
          logger.finest(mName, "Unknown table, ignoring");
          continue;
        }

        short seq = rs.getShort(9);
        if (seq == 1) {
          foreignKey = pTable.getTable().newForeignKey(referencedTable.getTable());
          result.add(foreignKey);
        }
        foreignKey.addColumnLink(localColumnName, referencedColumnName);
      }

      isRsClosed = true;
      rs.close();
    } finally {
      if (!isRsClosed) { try { rs.close(); } catch (Throwable ignore) {} }
    }
    return (ForeignKey[]) result.toArray(new ForeignKey[result.size()]);
  }

  public Schema getSchema(Connection pConn, Schema.Name pName) throws SQLException {
    final String mName = "getSchema(Connection,Schema.Name)";
    logger.finest(mName, "->", new Object[]{pConn, pName});

    Schema schema = makeSchema(pName);
    DatabaseMetaData metaData = pConn.getMetaData();
    JDBCTable[] tables = readTables(metaData, pName, null);
    for (int i = 0;  i < tables.length;  i++) {
      readTable(metaData, schema, tables[i]);
      readPrimaryKey(metaData, tables[i]);
    }

    for (int i = 0;  i < tables.length;  i++) {
      readForeignKeys(metaData, tables[i], tables);
    }

    logger.finest(mName, "<-", schema);
    return schema;
  }

  public Table getTable(Connection pConnection, Schema.Name pSchema, Table.Name pTable) throws SQLException {
    final String mName = "getSchema(Connection,Schema.Name)";
    logger.finest(mName, "->", new Object[]{pConnection, pSchema, pTable});

    Schema schema = makeSchema(pSchema);
    DatabaseMetaData metaData = pConnection.getMetaData();
    JDBCTable[] tables = readTables(metaData, pSchema, pTable);
    JDBCTable jdbcTable;
    if (tables.length == 0) {
      String tableName = pTable.toString();
      String schemaName = pSchema == null ? null : pSchema.toString();
      String ucTableName = tableName.toUpperCase();
      String ucSchemaName = schemaName == null ? null : schemaName.toUpperCase();
      if (tableName.equals(ucTableName)  &&
          (schemaName == null  ||  schemaName.equals(ucSchemaName))) {
        throw new IllegalStateException("No table named " + pTable + " found in schema " + pSchema);
      }
      return getTable(pConnection, ucSchemaName, ucTableName);
    } else if (tables.length == 1) {
      jdbcTable = tables[0];
    } else {
      throw new IllegalStateException("Multiple tables named " + pTable + " found in schema " + pSchema);
    }
    Table result = readTable(metaData, schema, jdbcTable);
    readPrimaryKey(metaData, jdbcTable);
    return result;
  }

  public Table getTable(Connection pConnection, String pSchema, String pTable) throws SQLException {
    return getTable(pConnection, pSchema == null ? null : new SchemaImpl.NameImpl(pSchema), new TableImpl.NameImpl(pTable));
  }
}
