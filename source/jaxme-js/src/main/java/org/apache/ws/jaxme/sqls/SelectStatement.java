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


/** <p>Interface of a SELECT statement.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface SelectStatement extends ConstrainedStatement {
	/** Provides a single column for an <code>ORDER BY</code>
     * clause.
	 */
    public interface OrderColumn {
      /** <p>Returns the ordered column. Typically, this is
       * a {@link ColumnReference}. However, it may as well
       * be a {@link Function} or a piece of {@link RawSQLCode}.</p>
       */
      public Object getColumn();

      /** <p>Returns whether ascending (false) or descending (true)
       * sorting is requested.</p>
       */
      public boolean isDescending();
   }

   /** <p>Shortcut for <code>(SelectTableReference) getTable()</code>.</p>
    */
   public SelectTableReference getSelectTableReference();

   /** <p>Returns an Iterator over all the table references.</p>
    */
   public Iterator getSelectTableReferences();

   /** <p>Adds a column to the ORDER BY clause.</p>
    */
   public void addOrderColumn(OrderColumn pColumn);

   /** <p>Adds a column to the ORDER BY clause. The column is sorted in
    * ascending order.</p>
    */
   public void addOrderColumn(Object pColumn);

   /** <p>Adds a column to the ORDER BY clause. The column is sorted in
    * ascending or descending order, depending on the parameter
    * <code>pDescending</code>.</p>
    * @param pDescending True for descending or false for ascending
    */
   public void addOrderColumn(Object pColumn, boolean pDescending);

   /** <p>Adds a result column to the statement. By default all columns
    * are returned.</p>
    */
   public void addResultColumn(ColumnReference pColumn);

   /** <p>Returns the list of result columns.</p>
    */
   public Iterator getResultColumns();

   /** <p>Returns the list of order columns. The elements
    * returned by the iterator are instances of
    * {@link SelectStatement.OrderColumn}.</p>
    */
   public Iterator getOrderColumns();

   /** <p>Sets whether the statement should have a DISTINCT clause. By
    * default no DISTINCT clause is present.</p>
    */
   public void setDistinct(boolean pDistinct);

   /** <p>Returns whether the statement should have a DISTINCT clause. By
    * default no DISTINCT clause is present.</p>
    */
   public boolean isDistinct();

   /** <p>Limits the size of the result set to the given number of rows.
    * Defaults to zero, in which case the size of the result set is
    * unlimited.</p>
    */
   public void setMaxRows(int pMaxRows);

   /** <p>Returns the limit of the number of rows in the result set, or
    * zero, if the size of the result set is unlimited.</p>
    */
   public int getMaxRows();

   /** <p>Indicates that the given number of rows should be skipped at the
    * result sets beginning. The default is zero, in which case no rows
    * are skipped.</p>
    */
   public void setSkippedRows(int pSkippedRows);

   /** <p>Returns the number of rows to skip at the result sets beginning.
    * The default is zero, in which case no rows are skipped.</p>
    */
   public int getSkippedRows();

   /** <p>Creates a view, which may be used to embed the statement into
    * a separate query.</p>
    */
   public Table createView(Table.Name pName);


   /** <p>Creates a view, which may be used to embed the statement into
    * a separate query.</p>
    */
   public Table createView(String pName);
}
