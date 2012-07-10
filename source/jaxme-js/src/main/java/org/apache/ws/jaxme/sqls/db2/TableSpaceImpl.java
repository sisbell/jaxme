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
import java.util.Iterator;
import java.util.List;

import org.apache.ws.jaxme.sqls.impl.SQLFactoryImpl;


/** <p>Default implementation of a DB2 tablespace.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class TableSpaceImpl implements TableSpace {
  public static class NameImpl extends SQLFactoryImpl.IdentImpl
      implements TableSpace.Name {
    public NameImpl(String pName) { super(pName); }
  }

  public static class SystemManagedContainerImpl implements TableSpace.SystemManagedContainer {
    private String file;
    public boolean isSystemManaged() { return true; }
    public boolean isDatabaseManaged() { return false; }
    public String getFile() { return file; }
    public void setFile(String pFile) { file = pFile; }
  }

  public static class DatabaseManagedContainerImpl implements TableSpace.DatabaseManagedContainer {
    private String file;
    private String device;
    private long numPages;
    public boolean isSystemManaged() { return false; }
    public boolean isDatabaseManaged() { return true; }
    public void setFile(String pFile) { file = pFile; }
    public String getFile() { return file; }
    public void setDevice(String pDevice) { device = pDevice; }
    public String getDevice() { return device; }
    public long getNumOfPages() { return numPages; }
    public void setNumOfPages(long pNumOfPages) { numPages = pNumOfPages; }
  }

  private DB2SQLFactory sqlFactory;
  private BufferPool bufferPool;
  private TableSpace.Name name;
  private TableSpace.Type type;
  private List containers = new ArrayList();
  private Long extentSize, prefetchSize;
  private PageSize pageSize;
  private Number overhead, transferRate;
  private Boolean droppedTableRecovery;

  protected TableSpaceImpl(DB2SQLFactory pFactory, TableSpace.Name pName,
                            TableSpace.Type pType) {
    sqlFactory = pFactory;
    name = pName;
    type = pType;
  }

  public DB2SQLFactory getSQLFactory() { return sqlFactory; }
  public TableSpace.Name getName() { return name; }
  public TableSpace.Type getType() { return type; }
  public PageSize getPageSize() {
     BufferPool bPool = getBufferPool();
     return bPool == null ? pageSize : bPool.getPageSize();
  }
  public void setPageSize(PageSize pSize) { pageSize = pSize; }
  public Long getExtentSize() { return extentSize; }
  public void setExtentSize(Long pSize) { extentSize = pSize; }
  public Long getPrefetchSize() { return prefetchSize; }
  public void setPrefetchSize(Long pSize) { prefetchSize = pSize; }
  public Number getOverhead() { return overhead; }
  public void setOverhead(Number pOverhead) { overhead = pOverhead; }
  public Number getTransferRate() { return transferRate; }
  public void setTransferRate(Number pTransferRate) { transferRate = pTransferRate; }
  public Boolean hasDroppedTableRecovery() { return droppedTableRecovery; }
  public void setDroppedTableRecovery(Boolean pRecoverable) { droppedTableRecovery = pRecoverable; }
  public boolean isPredefined() { return false; }

  public Container newSystemManagedContainer(String pFile) {
    SystemManagedContainerImpl container = new SystemManagedContainerImpl();
    container.setFile(pFile);
    containers.add(container);
    return container;
  }

  public Container newDatabaseManagedContainerInFile(String pFile, long pNumPages) {
    DatabaseManagedContainerImpl container = new DatabaseManagedContainerImpl();
    container.setFile(pFile);
    container.setNumOfPages(pNumPages);
    containers.add(container);
    return container;
  }

  public Container newDatabaseManagedContainerInDevice(String pDevice, long pNumPages) {
    DatabaseManagedContainerImpl container = new DatabaseManagedContainerImpl();
    container.setDevice(pDevice);
    container.setNumOfPages(pNumPages);
    containers.add(container);
    return container;
  }

  public Iterator getContainers() {
    return containers.iterator();
  }

  public void setBufferPool(BufferPool pBufferPool) {
    bufferPool = pBufferPool;
  }

  public BufferPool getBufferPool() {
    return bufferPool;
  }
}
