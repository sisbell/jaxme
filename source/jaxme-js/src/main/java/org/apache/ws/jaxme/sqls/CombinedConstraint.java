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
import java.util.Map;

import org.apache.ws.jaxme.sqls.impl.CombinedConstraintImpl;


/** Interface of a <code>WHERE</code> or <code>ON</code>
 * clause.
 */
public interface CombinedConstraint extends Constraint {
	/** Specifies, how the various boolean constraints are
     * combined.
	 */
    public interface Type {
        /** Specifies, that boolean constraints are combined with a
         * boolean <code>AND</code>.
         */
    	public final static Type AND = new CombinedConstraintImpl.TypeImpl("AND");
        /** Specifies, that boolean constraints are combined with a
         * boolean <code>OR</code>.
         */
    	public final static Type OR = new CombinedConstraintImpl.TypeImpl("OR");
    }

   /** Returns the type, either of
    * {@link org.apache.ws.jaxme.sqls.CombinedConstraint.Type#AND} or
    * {@link org.apache.ws.jaxme.sqls.CombinedConstraint.Type#OR}.
    */
   public CombinedConstraint.Type getType();

   /** Creates an AndConstraint and inserts it at the current position.
    */
   public CombinedConstraint createAndConstraint();

   /** Creates an OrConstraint and inserts it at the current position.
    */
   public CombinedConstraint createOrConstraint();

   /** Creates an "equals" condition (=) and inserts it at the current position.
    */
   public BooleanConstraint createEQ();

   /** Creates a "not equals" condition (<>) and inserts it at the current position.
    */
   public BooleanConstraint createNE();

   /** Creates a "lower than" condition (<) and inserts it at the current position.
    */
   public BooleanConstraint createLT();

   /** Creates a "greater than" condition (>) and inserts it at the current position.
    */
   public BooleanConstraint createGT();

   /** Creates a "lower or equal" condition (<=) and inserts it at the current position.
    */
   public BooleanConstraint createLE();

   /** Creates a "greater or equal" condition (>=) and inserts it at the current position.
    */
   public BooleanConstraint createGE();

   /** Creates a "LIKE" condition and inserts it at the current position.
    */
   public BooleanConstraint createLIKE();

   /** Creates an "IS NULL" condition and inserts it at the current position.
    */
   public BooleanConstraint createISNULL();

   /** Creates an "IN" condition and inserts it at the current position.
    */
   public BooleanConstraint createIN();

   /** Creates an "EXISTS" condition with the given select
    * statement and inserts it at the current position.
    */
   public void createEXISTS(SelectStatement pStatement);

   /** Creates a "BETWEEN" condition with the given select
    * statement and inserts it at the current position.
    */
   public BooleanConstraint createBETWEEN();

   /** Creates a JOIN condition matching the given foreign key. In other
    * words, if the foreign key consists of the columns <code>A</code> and
    * <code>B</code> referencing the columns <code>X</code> and <code>Y</code>,
    * then the following will be added: <code>A=X AND B=Y</code>.
    * @param pKey The foreign key being matched.
    * @param pReferencingTable A reference to the table returned by the
    *   foreign keys {@link org.apache.ws.jaxme.sqls.ForeignKey#getTable()}
    *   method.
    * @param pReferencedTable A reference to the table returned by the
    *   foreign keys {@link org.apache.ws.jaxme.sqls.ForeignKey#getReferencedTable()}
    *   method.
    */
   public void addJoin(ForeignKey pKey, TableReference pReferencingTable,
                       TableReference pReferencedTable);

   /** Creates a JOIN condition matching the given column reference.
    * In other words, if the referencing
    * {@link org.apache.ws.jaxme.sqls.ColumnSet} contains the
    * columns A and B, and the referenced column set contains
    * {@link org.apache.ws.jaxme.sqls.ColumnSet},
    * X and Y, then the following will be added: <code>A=X AND B=Y</code>.
    */
   public void addJoin(TableReference pReferencingTable,
                       ColumnSet pReferencingColumnSet,
                       TableReference pReferencedTable,
                       ColumnSet pReferencedColumnSet);

   /** Clones the given {@link org.apache.ws.jaxme.sqls.Constraint},
    * mapping the column references
    * from the given constraint to the values in the given map.
    * @param pMap A Map with the constraints <code>pConstraint</code> table
    *   references as keys. The values are table references of the current
    *   constraints statement.
    * @param pConstraint The constraint being cloned.
    */
   public void addConstraint(Map pMap, Constraint pConstraint);

   /** Adds a check for the columns of the given column set. For example,
    * if the column set consists of the columns <code>A</code> and <code>B</code>,
    * then the following will be added: <code>A=? AND B=?</code>.
    */
   public void addColumnSetQuery(ColumnSet pSet, TableReference pTableReference);

   /** Returns the number of parts, that have been added with the
    * various <code>createSomething()</code> methods.
    */
   public int getNumParts();

   /** Returns an Iterator to the parts, that have been added with the
    * various <code>createSomething()</code> methods.
    */
   public Iterator getParts();

   /** Returns whether the combined constraint is inverted by adding
    * a prepending <code>NOT</code>.
    */
   public boolean isNOT();

   /** Sets whether the combined constraint is inverted by adding
    * a prepending <code>NOT</code>.
    */
   public void setNOT(boolean pNOT);
}
