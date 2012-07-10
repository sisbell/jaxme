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

import java.util.Iterator;

import org.apache.ws.jaxme.sqls.SQLFactory;


/** <p>Interface of a DB2 TableSpace. An object of this kind is used to
 * create a <code>CREATE TABLESPACE ...</code> statement.</p>
 * <p>A TableSpace can be associated to a {@link BufferPool}. If it
 * is, it inherits certain settings from the {@link BufferPool},
 * in particular the {@link #getPageSize() pageSize}.</p>
 * <p>A tablespace needs to know where data is stored physically. DB2
 * distinguishes between two possible data locations: System managed
 * (Files handled by the operating system) and Database managed (Files
 * handled by the database or Devices). In the latter case the database
 * will preallocate space for the files or devices, giving a slightly
 * faster operation. On the other hand, database managed files cannot grow
 * automatically.</p>
 * <p>A tablespaces data location is called container. Any tablespace must
 * have at least one container. Note, that one tablespace cannot have
 * both system managed containers and database managed containers. If the
 * first container is system managed, then all containers are, and vice
 * versa.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface TableSpace {
  public interface Name extends SQLFactory.Ident {
  }

  /** <p>Interface of a TableSpace Container.</p>
   */
  public interface Container {
    /** <p>Returns whether the container is system managed
     * (aka an operating system file). If this is the case,
     * the container may be casted to a
     * {@link org.apache.ws.jaxme.sqls.db2.TableSpace.SystemManagedContainer}.</p> 
     */
    public boolean isSystemManaged();
    /** <p>Returns whether the container is database managed
     * If this is the case, the container may be casted to a
     * {@link org.apache.ws.jaxme.sqls.db2.TableSpace.DatabaseManagedContainer}.</p> 
     */
    public boolean isDatabaseManaged();
  }

  /** <p>Interface of a system managed container, aka operating
   * system file. A container may be casted to a SystemManagedContainer,
   * if and only if <code>isSystemManaged() == true</code>.</p>
   */
  public interface SystemManagedContainer extends Container {
    /** <p>Returns the containers file name.</p>
     */
    public String getFile();
  }

  /** <p>Interface of a database managed container. The container
   * may be located in an operating system file with preallocated
   * size or in a raw operating system device. A container may be
   * casted to a DatabaseManagedContainer, if and only if
   * <code>isDatabaseManaged() == true</code>.</p>
   */
  public interface DatabaseManagedContainer extends Container {
    /** <p>Returns the containers file name or null, if the
     * container is located in a raw device. In the latter case
     * it is guaranteed, that {@link #getDevice()} returns a
     * non-null value.</p>
     */
    public String getFile();
    /** <p>Returns the containers device name or null, if the
     * container is located in an operating system file. In the
     * latter case it is guaranteed, that {@link #getDevice()}
     * returns a non-null value.</p>
     */
    public String getDevice();
    /** <p>Returns the containers size in pages.</p>
     */
    public long getNumOfPages();
  }

  /** <p>A DB2 TableSpace type. Valid types are either of
   * {@link #REGULAR}, {@link #LONG}, {@link #SYSTEM_TEMPORARY}, or
   * {@link #USER_TEMPORARY}. See the DB2 SQL reference in the section
   * <code>CREATE TABLESPACE</code> for details on types.</p>
   */
  public class Type {
     /** <p>REGULAR: Stores all data except for temporary tables.</p>
      */
    public static final Type REGULAR = new Type("REGULAR");
    /** <p>LONG: Stores long or LOB table columns. It may also store
     * structured type columns. The tablespace must be a DMS tablespace.</p>
     */
    public static final Type LONG = new Type("LONG");
    /** <p>SYSTEM TEMPORARY: Stores temporary tables (work areas used
     * by the database manager to perform operations such as sorts or
     * joins). Note that a database must always have at least one
     * <code>SYSTEM TEMPORARY</code> tablespace, as temporary tables
     * can only be stored in such a tablespace. A temporary tablespace
     * is created automatically when a database is created. See
     * <code>CREATE DATABASE</code> in the Command Reference for more information.</p>
     */
    public static final Type SYSTEM_TEMPORARY = new Type("SYSTEM TEMPORARY");
    /* <p>USER TEMPORARY: Stores declared global temporary tables.
     * Note that no user temporary tablespaces exist when a database
     * is created. At least one user temporary tablespace should be
     * created with appropriate <code>USE</code> privileges, to allow
     * definition of declared temporary tables.</p>
     */
    public static final Type USER_TEMPORARY = new Type("USER TEMPORARY");
    private static final Type[] instances =
      new Type[]{REGULAR, LONG, SYSTEM_TEMPORARY, USER_TEMPORARY};
    private String name;
    private Type(String pName) {
      name = pName;
    }
    public String toString() { return name; }
    public String getName() { return name; }
    public boolean equals(Object pOther) {
      return pOther != null && pOther instanceof Type &&
              name.equals(((Type) pOther).name);
    }
    public int hashCode() { return name.hashCode(); }
    public static Type[] getInstances() { return instances; }
    public static Type valueOf(String pName) {
      for (int i = 0;  i < instances.length;  i++) {
        if (instances[i].name.equalsIgnoreCase(pName)) {
          return instances[i];
        }
      }
      throw new IllegalArgumentException("Invalid type name: " + pName);
    }
  }

  /** <p>Returns the {@link org.apache.ws.jaxme.sqls.SQLFactory} that created this
   * <code>TableSpace</code> object.</p>
   */
  public DB2SQLFactory getSQLFactory();

  /** <p>Returns the tablespace name.
   * Tablespace names must be unique in the database.</p>
   */
  public TableSpace.Name getName();

  /** <p>Returns the tablespace type.</p>
   */
  public TableSpace.Type getType();

  /** <p>Returns the tablespaces page size. If the <code>TableSpace</code>
   * has an associated {@link BufferPool}, returns the BufferPool's page
   * size. Otherwise returns the page size configured via
   * {@link #setPageSize(PageSize)}.</p>
   */
  public PageSize getPageSize();

  /** <p>Sets the tablespaces page size. This value will be ignored,
   * if the <code>TableSpace</code> has an associated {@link BufferPool}.</p>
   */
  public void setPageSize(PageSize pSize);

  /** <p>Returns the number of {@link #getPageSize() pageSize} pages that will
   * be written to a container before skipping to the next container. The
   * database manager cycles repeatedly through the containers as data is
   * stored. Defaults to null, in which case the DB2 configuration
   * parameter DFT_EXTENT_SZ applies.</p>
   * </p>
   */
  public Long getExtentSize();

  /** <p>Sets the number of {@link #getPageSize() pageSize} pages that will
   * be written to a container before skipping to the next container. The
   * database manager cycles repeatedly through the containers as data is
   * stored. Defaults to null, in which case the DB2 configuration
   * parameter DFT_EXTENT_SZ applies.</p>
   * </p>
   */
  public void setExtentSize(Long pSize);

  /** <p>Returns the number of {@link #getPageSize() pageSize} pages that will
   * be read from the tablespace when data prefetching is being performed.
   * Prefetching reads in data needed by a query prior to it being referenced
   * by the query, so that the query need not wait for I/O to be performed.
   * Defaults to null, in which case the DB2 configuration parameter
   * DFT_PREFETCH_SZ applies.</p>
   */
  public Long getPrefetchSize();

  /** <p>Sets the number of {@link #getPageSize() pageSize} pages that will
   * be read from the tablespace when data prefetching is being performed.
   * Prefetching reads in data needed by a query prior to it being referenced
   * by the query, so that the query need not wait for I/O to be performed.
   * Defaults to null, in which case the DB2 configuration parameter
   * DFT_PREFETCH_SZ applies.</p>
   */
  public void setPrefetchSize(Long pSize);

  /** <p>Returns the I/O controller overhead and disk seek and latency time, in
   * milliseconds. The number should be an average for all containers that belong
   * to the tablespace, if not the same for all containers. This value is used to
   * determine the cost of I/O during query optimization. Defaults to null,
   * in which case the DB2 default (24.1) applies.</p>
   * </p>
   */
  public Number getOverhead();

  /** <p>Sets the I/O controller overhead and disk seek and latency time, in
   * milliseconds. The number should be an average for all containers that belong
   * to the tablespace, if not the same for all containers. This value is used to
   * determine the cost of I/O during query optimization. Defaults to null,
   * in which case the DB2 default (24.1) applies.</p>
   * </p>
   */
  public void setOverhead(Number pOverhead);

  /** <p>Returns the transfer rate, which is defined as the time to read one page
   * into memory, in milliseconds. The number shouldbe an average for all
   * containers that belong to the tablespace, if not the same for all
   * containers. This value is used to determine the cost of I/O during query
   * optimization. Defaults to null, in which case the DB2 default (0.9)
   * applies.</p>
   */
  public Number getTransferRate();

  /** <p>Sets the transfer rate, which is defined as the time to read one page
   * into memory, in milliseconds. The number shouldbe an average for all
   * containers that belong to the tablespace, if not the same for all
   * containers. This value is used to determine the cost of I/O during query
   * optimization. Defaults to null, in which case the DB2 default (0.9)
   * applies.</p>
   */
  public void setTransferRate(Number pNumber);

  /** <p>Returns whether dropped tables in the specified tablespace may be
   * recovered using the <code>RECOVER TABLE ON</code> option of the
   * <code>ROLLFORWARD</code> command. This clause can only be specified
   * for a {@link Type#REGULAR} tablespace (SQLSTATE 42613). For more
   * information on recovering dropped tables, refer to the Administration
   * Guide. Defaults to null, in which case the DB2 default applies.</p>
   */
  public Boolean hasDroppedTableRecovery();

  /** <p>Sets whether dropped tables in the specified tablespace may be
   * recovered using the <code>RECOVER TABLE ON</code> option of the
   * <code>ROLLFORWARD</code> command. This clause can only be specified
   * for a {@link Type#REGULAR} tablespace (SQLSTATE 42613). For more
   * information on recovering dropped tables, refer to the Administration
   * Guide. Defaults to null, in which case the DB2 default applies.</p>
   */
  public void setDroppedTableRecovery(Boolean pRecoverable);

  /** <p>Creates a new system managed container with the given file.</p>
   */
  public Container newSystemManagedContainer(String pFile);

  /** <p>Creates a new database managed container with the given file
   * or device and the given number of pages.</p>
   */
  public Container newDatabaseManagedContainerInFile(String pFile, long pNumPages);

  /** <p>Creates a new database managed container with the given raw
   * operating system device and the given number of pages.</p>
   */
  public Container newDatabaseManagedContainerInDevice(String pDevice, long pNumPages);

  /** <p>Returns an {@link Iterator} to the table spaces containers.
   * Any element in the {@link java.util.Iterator} is an instance of
   * {@link org.apache.ws.jaxme.sqls.db2.TableSpace.Container}, which
   * was created using {@link #newSystemManagedContainer(String)},
   * {@link #newDatabaseManagedContainerInFile(String, long)}, or
   * {@link #newDatabaseManagedContainerInDevice(String, long)}.</p>
   */
  public Iterator getContainers();

  /** <p>Sets the {@link BufferPool} used by this {@link TableSpace}.</p>
   */
  public void setBufferPool(BufferPool pBufferPool);

  /** <p>Returns the {@link BufferPool} used by this {@link TableSpace}.</p>
   */
  public BufferPool getBufferPool();

  /** <p>Returns whether this TableSpace is predefined by the system.</p>
   */
  public boolean isPredefined();
}
