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
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.ws.jaxme.sqls.Column;
import org.apache.ws.jaxme.sqls.Schema;
import org.apache.ws.jaxme.sqls.Table;
import org.apache.ws.jaxme.sqls.impl.SQLGeneratorImpl;


/** <p>Default implementation of an SQL generator for DB2 schemas.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class DB2SQLGeneratorImpl extends SQLGeneratorImpl implements DB2SQLGenerator {
  private boolean isCreatingTableSpaceReferences = true;

  protected boolean isPrimaryKeyPartOfCreateTable() { return true; }
  protected boolean isUniqueIndexPartOfCreateTable() { return true; }
  protected boolean isForeignKeyPartOfCreateTable() { return true; }

  /** <p>Sets whether <code>CREATE TABLE</code> statements will contain
   * table space references or not. The default is to create table
   * space references.</p>
   */
  public void setCreatingTableSpaceReferences(boolean pCreatingTableSpaceReferences) {
     isCreatingTableSpaceReferences = pCreatingTableSpaceReferences;
  }

  /** <p>Returns whether <code>CREATE TABLE</code> statements will contain
   * table space references or not. The default is to create table
   * space references.</p>
   */
  public boolean isCreatingTableSpaceReferences() {
     return isCreatingTableSpaceReferences;
  }
  
  protected String getTypeName(Column.Type pType) {
    if (pType.equals(Column.Type.BINARY)  ||
        pType.equals(Column.Type.VARBINARY)) {
      return "BLOB";
    } else {
      return super.getTypeName(pType);
    }
  }

  public Collection getCreate(BufferPool pBufferPool) {
    StringBuffer sb = new StringBuffer();
    sb.append("CREATE BUFFERPOOL ").append(pBufferPool.getName()).append(" SIZE ");
    sb.append(pBufferPool.getNumberOfPages());
    PageSize pageSize = pBufferPool.getPageSize(); 
    if (pageSize != null) {
      sb.append(" PAGESIZE ").append(pageSize.getSize());
    }
    Boolean extendedStorage = pBufferPool.getExtendedStorage();
    if (extendedStorage != null) {
      if (extendedStorage.booleanValue()) {
        sb.append(" EXTENDED STORAGE");
      } else {
        sb.append(" NOT EXTENDED STORAGE");
      }
    }
    List result = new ArrayList();
    result.add(newStatement(sb.toString()));
    return result;
  }

  public Collection getDrop(BufferPool pBufferPool) {
     List result = new ArrayList();
     result.add(newStatement("DROP BUFFERPOOL " + pBufferPool.getName()));
     return result;
  }

  public Collection getCreate(TableSpace pTableSpace) {
    if (pTableSpace.isPredefined()) {
      return Collections.EMPTY_SET;
    }
    StringBuffer sb = new StringBuffer();
    sb.append("CREATE TABLESPACE ");
    TableSpace.Type type = pTableSpace.getType();
    if (type != null) {
      sb.append(type).append(" ");
    }
    sb.append(pTableSpace.getName().getName());
    BufferPool bufferPool = pTableSpace.getBufferPool();
    PageSize pageSize = (bufferPool == null) ?
      pTableSpace.getPageSize() : bufferPool.getPageSize();
    if (pageSize != null) {
      sb.append(" PAGESIZE ").append(pageSize);
    }
    Iterator iter = pTableSpace.getContainers();
    if (!iter.hasNext()) {
       throw new IllegalStateException("The TableSpace " + pTableSpace.getName() +
                                        " doesn't have any containers.");
    }
    TableSpace.Container container = (TableSpace.Container) iter.next();
    String sep = "(";
    if (container.isSystemManaged()) { 
       sb.append(" MANAGED BY SYSTEM USING ");
       while (container != null) {
         if (!container.isSystemManaged()) {
           throw new IllegalStateException("A TableSpace must not mix system and database managed containers.");
         }
         TableSpace.SystemManagedContainer systemContainer = (TableSpace.SystemManagedContainer) container;
         sb.append(sep);
         sep = ", ";
         sb.append("'").append(systemContainer.getFile()).append("'");
         container = iter.hasNext() ? (TableSpace.Container) iter.next() : null;
       }
    } else {
       sb.append(" MANAGED BY DATABASE USING (");
       while (container != null) {
         if (!container.isDatabaseManaged()) {
           throw new IllegalStateException("A TableSpace must not mix system and database managed containers.");
         }
         TableSpace.DatabaseManagedContainer databaseContainer = (TableSpace.DatabaseManagedContainer) container;
         sb.append(sep);
         sep = ", ";
         if (databaseContainer.getFile() != null) {
            sb.append("FILE '").append(databaseContainer.getFile()).append("'");
         } else {
            sb.append("DEVICE '").append(databaseContainer.getDevice()).append("'");
         }
         container = iter.hasNext() ? (TableSpace.Container) iter.next() : null;
       }
    }
    sb.append(")");
    Long extentSize = pTableSpace.getExtentSize();
    if (extentSize != null) {
      sb.append(" EXTENTSIZE ").append(extentSize);
    }
    Long prefetchSize = pTableSpace.getPrefetchSize();
    if (prefetchSize != null) {
      sb.append(" PREFETCHSIZE ").append(prefetchSize);
    }
    if (bufferPool != null) {
      sb.append(" BUFFERPOOL ").append(bufferPool.getName().getName());
    }
    Number overhead = pTableSpace.getOverhead();
    if (overhead != null) {
      sb.append(" OVERHEAD ").append(overhead);
    }
    Number transferRate = pTableSpace.getTransferRate();
    if (transferRate != null) {
      sb.append(" TRANSFERRATE ").append(transferRate);
    }
    Boolean hasDroppedTableRecovery = pTableSpace.hasDroppedTableRecovery();
    if (hasDroppedTableRecovery != null) {
      sb.append(" DROPPED TABLE RECOVERY ");
      sb.append(hasDroppedTableRecovery.booleanValue() ? "ON" : "OFF");
    }

    List result = new ArrayList();
    result.add(newStatement(sb.toString()));
    return result;
  }

  public Collection getDrop(TableSpace pTableSpace) {
    if (pTableSpace.isPredefined()) {
      return Collections.EMPTY_SET;
    }
    List result = new ArrayList();
    result.add(newStatement("DROP TABLESPACE " + pTableSpace.getName()));
    return result;
  }

  protected String getCreateTableHeader(Table pTable) {
     String statement = super.getCreateTableHeader(pTable);
     if (!isCreatingTableSpaceReferences()  ||
         !(pTable instanceof DB2Table)) {
        return statement;
     }
     StringBuffer sb = new StringBuffer(statement);
     DB2Table table = (DB2Table) pTable;
     TableSpace tableSpace = table.getTableSpace();
     if (tableSpace != null) {
        sb.append(" IN ").append(tableSpace.getName());
     }
     tableSpace = table.getIndexTableSpace();
     if (tableSpace != null) {
        sb.append(" INDEX IN ").append(tableSpace.getName());
     }
     tableSpace = table.getLongTableSpace();
     if (tableSpace != null) {
        sb.append(" LONG IN ").append(tableSpace.getName());
     }
     return sb.toString();
  }

  protected String getCreate(Column pColumn) {
    String result = super.getCreate(pColumn);
    if (pColumn instanceof DB2Column) {
       DB2Column db2Column = (DB2Column) pColumn;
       if (db2Column.getGeneratedAs() != null) {
          result += " GENERATED ALWAYS AS (" + db2Column.getGeneratedAs() + ")";
       }
    }
    return result;
  }

  public Collection getDrop(Schema pSchema) {
     List result = new ArrayList();
     for (Iterator iter = super.getDrop(pSchema).iterator();  iter.hasNext();  ) {
        String s = (String) iter.next();
        if (s.startsWith("DROP SCHEMA ")) {
           s += " RESTRICT";
        }
        result.add(s);
     }
     return result;
  }
}
