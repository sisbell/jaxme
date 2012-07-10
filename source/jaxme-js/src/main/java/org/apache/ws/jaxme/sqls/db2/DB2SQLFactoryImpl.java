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
package org.apache.ws.jaxme.sqls.db2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.ws.jaxme.sqls.Column;
import org.apache.ws.jaxme.sqls.SQLGenerator;
import org.apache.ws.jaxme.sqls.Schema;
import org.apache.ws.jaxme.sqls.Table;
import org.apache.ws.jaxme.sqls.impl.SQLFactoryImpl;


/** <p>Default implementation of an SQL factory for DB2 databases.
 * This factory ensures that the created implementations of
 * {@link Schema}, {@link Table}, {@link Column}, and {@link SQLGenerator}
 * may be casted to {@link DB2Schema}, {@link DB2Table}, {@link DB2Column},
 * {@link DB2SQLGenerator}, respectively.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class DB2SQLFactoryImpl extends SQLFactoryImpl
    implements DB2SQLFactory {
  /** <p>An immutable, predefined TableSpace.</p>
   */
  public class PredefinedTableSpace implements TableSpace {
    private TableSpace.Name name;
    private TableSpace.Type type;

    PredefinedTableSpace(String pName, TableSpace.Type pType) {
      name = new TableSpaceImpl.NameImpl(pName);
      type = pType;
    }    
    public DB2SQLFactory getSQLFactory() { return DB2SQLFactoryImpl.this; }
    public Name getName() { return name; }
    public Type getType() { return type; }
    public PageSize getPageSize() { return null; }
    public Long getExtentSize() { return null; }
    public Long getPrefetchSize() { return null; }
    public Number getOverhead() { return null; }
    public Number getTransferRate() { return null; }
    public Boolean hasDroppedTableRecovery() { return null; }
    public BufferPool getBufferPool() { return null; }
    public boolean isPredefined() { return true; }
    public void setPageSize(PageSize pSize) {
      throw new IllegalStateException("This tablespace is immutable.");
    }
    public void setExtentSize(Long pSize) {
      throw new IllegalStateException("This tablespace is immutable.");
    }
    public void setPrefetchSize(Long pSize) {
      throw new IllegalStateException("This tablespace is immutable.");
    }
    public void setOverhead(Number pOverhead) {
      throw new IllegalStateException("This tablespace is immutable.");
    }
    public void setTransferRate(Number pNumber) {
      throw new IllegalStateException("This tablespace is immutable.");
    }
    public void setDroppedTableRecovery(Boolean pRecoverable) {
      throw new IllegalStateException("This tablespace is immutable.");
    }
    public Container newSystemManagedContainer(String pFile) {
      throw new IllegalStateException("This tablespace is immutable.");
    }
    public Container newDatabaseManagedContainerInFile(String pFile, long pNumPages) {
      throw new IllegalStateException("This tablespace is immutable.");
    }
    public Container newDatabaseManagedContainerInDevice(String pDevice, long pNumPages) {
      throw new IllegalStateException("This tablespace is immutable.");
    }
    public Iterator getContainers() {
      return Collections.EMPTY_LIST.iterator();
    }
    public void setBufferPool(BufferPool pBufferPool) {
      throw new IllegalStateException("This tablespace is immutable.");
    }
  }

  /** <p>The predefined table space <code>SYSCATSPACE</code>.</p>
   */
  public final TableSpace SYSCATSPACE = new PredefinedTableSpace("SYSCATSPACE", TableSpace.Type.REGULAR);
  /** <p>The predefined table space <code>TEMPSPACE1</code>.</p>
   */
  public final TableSpace TEMPSPACE1 = new PredefinedTableSpace("TEMPSPACE1", TableSpace.Type.SYSTEM_TEMPORARY);
  /** <p>The predefined table space <code>USERSPACE1</code>.</p>
   */
  public final TableSpace USERSPACE1 = new PredefinedTableSpace("USERSPACE1", TableSpace.Type.USER_TEMPORARY);

  private List tableSpaces = new ArrayList();

  public DB2SQLFactoryImpl() {}

  public Schema newSchemaImpl(Schema.Name pName) {
    return new DB2SchemaImpl(this, pName);
  }

  public Table newTableImpl(Schema pSchema, Table.Name pName) {
    return new DB2TableImpl(pSchema, pName);
  }

  public Column newColumn(Table pTable, Column.Name pName, Column.Type pType) {
    return new DB2ColumnImpl(pTable, pName, pType);
  }

  public SQLGenerator newSQLGenerator() {
    return new DB2SQLGeneratorImpl();
  }

  public TableSpace newTableSpace(String pName, TableSpace.Type pType) {
    if (pName == null) {
      throw new NullPointerException("The tablespace name must not be null. Use getDefaultTableSpace() to access the default TableSpace."); 
    }
    return newTableSpace(new TableSpaceImpl.NameImpl(pName), pType);
  }

  public TableSpace newTableSpace(TableSpace.Name pName, TableSpace.Type pType) {
    if (pName == null) {
      throw new NullPointerException("The tablespace name must not be null. Use getDefaultTableSpace() to access the default TableSpace."); 
    }
    if (getTableSpace(pName) != null) {
      throw new IllegalStateException("A TableSpace named " + pName + " already exists.");
    }
    TableSpace result = newTableSpaceImpl(pName, pType);
    tableSpaces.add(result);
    return result;
  }

  protected TableSpace newTableSpaceImpl(TableSpace.Name pName, TableSpace.Type pType) {
    return new TableSpaceImpl(this, pName, pType);
  }

  public TableSpace getTableSpace(TableSpace.Name pName) {
    for (Iterator iter = tableSpaces.iterator();  iter.hasNext();  ) {
      TableSpace tableSpace = (TableSpace) iter.next();
      if (pName.equals(tableSpace.getName())) {
        return tableSpace;
      }
    }
    if (pName.equals(SYSCATSPACE.getName())) {
       return SYSCATSPACE;
    }
    if (pName.equals(TEMPSPACE1.getName())) {
       return TEMPSPACE1;
    }
    if (pName.equals(USERSPACE1.getName())) {
       return USERSPACE1;
    }
    return null;
  }

  public TableSpace getTableSpace(String pName) {
    return getTableSpace(new TableSpaceImpl.NameImpl(pName)); 
  }

  public Iterator getTableSpaces() {
    return tableSpaces.iterator();
  }
}
