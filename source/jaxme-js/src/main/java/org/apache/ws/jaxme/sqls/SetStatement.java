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

import java.util.Calendar;
import java.util.Iterator;


/** A common base interface for {@link org.apache.ws.jaxme.sqls.InsertStatement}
 * and {@link org.apache.ws.jaxme.sqls.UpdateStatement}.
 */
public interface SetStatement extends Statement {
    /** A tupel of column name and value being assigned.
     */
    public interface SetValue {
        /** Returns the column being set.
         */
        public ColumnReference getColumnReference();
        /** Returns the value to which the column is being set.
         */
        public Object getValue();
    }

    /** Returns an {@link Iterator} to all values being set.
     * Any element in the iterator is an instance of {@link SetValue}.
     */
    public Iterator getSetValues();

    /** Adds a SET clause setting the given column to NULL.
     */
    public void addSetNull(Column pColumn);
    /** Adds a SET clause setting the given column to NULL.
     */
    public void addSetNull(Column.Name pColumn);
    /** Adds a SET clause setting the given column to NULL.
     */
    public void addSetNull(String pColumn);
    
    /** Adds a SET clause setting the given column to a value
     * given by a placeholder.
     */
    public void addSet(Column pColumn);
    /** Adds a SET clause setting the given column to a value
     * given by a placeholder.
     */
    public void addSet(Column.Name pColumn);
    /** Adds a SET clause setting the given column to a value
     * given by a placeholder.
     */
    public void addSet(String pColumn);
    
    /** Adds a SET clause setting the given column to the value
     * <code>pValue<code>.
     */
    public void addSet(Column pColumn, byte pValue);
    /** Adds a SET clause setting the given column to the value
     * <code>pValue</code>.
     */
    public void addSet(Column.Name pColumn, byte pValue);
    /** Adds a SET clause setting the given column to the value
     * <code>pValue</code>.
     */
    public void addSet(String pColumn, byte pValue);

    /** Adds a SET clause setting the given column to the value
     * <code>pValue<code>.
     */
    public void addSet(Column pColumn, short pValue);
    /** Adds a SET clause setting the given column to the value
     * <code>pValue</code>.
     */
    public void addSet(Column.Name pColumn, short pValue);
    /** Adds a SET clause setting the given column to the value
     * <code>pValue</code>.
     */
    public void addSet(String pColumn, short pValue);
    
    /** Adds a SET clause setting the given column to the value
     * <code>pValue<code>.
     */
    public void addSet(Column pColumn, int pValue);
    /** Adds a SET clause setting the given column to the value
     * <code>pValue</code>.
     */
    public void addSet(Column.Name pColumn, int pValue);
    /** Adds a SET clause setting the given column to the value
     * <code>pValue</code>.
     */
    public void addSet(String pColumn, int pValue);
    
    /** Adds a SET clause setting the given column to the value
     * <code>pValue<code>.
     */
    public void addSet(Column pColumn, long pValue);
    /** Adds a SET clause setting the given column to the value
     * <code>pValue</code>.
     */
    public void addSet(Column.Name pColumn, long pValue);
    /** Adds a SET clause setting the given column to the value
     * <code>pValue</code>.
     */
    public void addSet(String pColumn, long pValue);
    
    
    /** Adds a SET clause setting the given column to the value
     * <code>pValue<code>.
     */
    public void addSet(Column pColumn, float pValue);
    /** Adds a SET clause setting the given column to the value
     * <code>pValue</code>.
     */
    public void addSet(Column.Name pColumn, float pValue);
    /** Adds a SET clause setting the given column to the value
     * <code>pValue</code>.
     */
    public void addSet(String pColumn, float pValue);
    
    /** Adds a SET clause setting the given column to the value
     * TRUE or FALSE, depending on <code>pValue<code>.
     */
    public void addSet(Column pColumn, boolean pValue);
    /** Adds a SET clause setting the given column to the value
     * TRUE or FALSE, depending on <code>pValue<code>.
     */
    public void addSet(Column.Name pColumn, boolean pValue);
    /** Adds a SET clause setting the given column to the value
     * TRUE or FALSE, depending on <code>pValue<code>.
     */
    public void addSet(String pColumn, boolean pValue);
    
    /** Adds a SET clause setting the given column to the value
     * <code>pValue<code>.
     */
    public void addSet(Column pColumn, String pValue);
    /** Adds a SET clause setting the given column to the value
     * <code>pValue</code>.
     */
    public void addSet(Column.Name pColumn, String pValue);
    /** Adds a SET clause setting the given column to the value
     * <code>pValue</code>.
     */
    public void addSet(String pColumn, String pValue);
    
    /** Adds a SET clause setting the given column to the datetime
     * value <code>pValue<code>.
     */
    public void addSetDateTime(Column pColumn, Calendar pValue);
    /** Adds a SET clause setting the given column to the datetime
     * value <code>pValue</code>.
     */
    public void addSetDateTime(Column.Name pColumn, Calendar pValue);
    /** Adds a SET clause setting the given column to the datetime
     * value <code>pValue</code>.
     */
    public void addSetDateTime(String pColumn, Calendar pValue);
    
    /** Adds a SET clause setting the given column to the time value
     * <code>pValue<code>. Shortcut for
     * <code>addSet(getTable().newColumnReference(pColumn), pValue)</code>.
     */
    public void addSetTime(Column pColumn, Calendar pValue);
    /** Adds a SET clause setting the given column to the time value
     * <code>pValue</code>. Shortcut for
     * <code>addSet(getTable().newColumnReference(pColumn), pValue)</code>.
     */
    public void addSetTime(Column.Name pColumn, Calendar pValue);
    /** Adds a SET clause setting the given column to the time value
     * <code>pValue</code>.
     */
    public void addSetTime(String pColumn, Calendar pValue);
    
    /** Adds a SET clause setting the given column to the date value
     * <code>pValue<code>.
     */
    public void addSetDate(Column pColumn, Calendar pValue);
    /** Adds a SET clause setting the given column to the date value
     * <code>pValue</code>.
     */
    public void addSetDate(Column.Name pColumn, Calendar pValue);
    /** Adds a SET clause setting the given column to the date value
     * <code>pValue</code>.
     */
    public void addSetDate(String pColumn, Calendar pValue);
    /** Adds a SET clause setting the given column to the given value.
     * The value may be, for example, a subselect, a function, or a
     * piece of raw SQL code.
     */
    public void addSet(Column pColumn, Object pObject);
    /** Adds a SET clause setting the given column to the given value.
     * The value may be, for example, a subselect, a function, or a
     * piece of raw SQL code.
     */
    public void addSet(String pColumn, Object pObject);
    /** Adds a SET clause setting the given column to the given value.
     * The value may be, for example, a subselect, a function, or a
     * piece of raw SQL code.
     */
    public void addSet(Column.Name pColumn, Object pObject);
}
