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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.ws.jaxme.sqls.BooleanConstraint;
import org.apache.ws.jaxme.sqls.Column;
import org.apache.ws.jaxme.sqls.ColumnReference;
import org.apache.ws.jaxme.sqls.ColumnSet;
import org.apache.ws.jaxme.sqls.CombinedConstraint;
import org.apache.ws.jaxme.sqls.ConstrainedStatement;
import org.apache.ws.jaxme.sqls.Constraint;
import org.apache.ws.jaxme.sqls.ForeignKey;
import org.apache.ws.jaxme.sqls.ObjectFactory;
import org.apache.ws.jaxme.sqls.SelectStatement;
import org.apache.ws.jaxme.sqls.TableReference;
import org.apache.ws.jaxme.sqls.Value;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class CombinedConstraintImpl extends ConstraintImpl implements CombinedConstraint {
	/** Default implementation of
     * {@link org.apache.ws.jaxme.sqls.CombinedConstraint.Type}.
	 */
    public static class TypeImpl extends SQLFactoryImpl.IdentImpl implements CombinedConstraint.Type {
    	/** Creates a new instance with the given name.
    	 */
        public TypeImpl(String pName) {
            super(pName);
        }
    }

    private boolean not;
    private List parts = new ArrayList();
    private CombinedConstraint.Type type;
    
    protected CombinedConstraintImpl(ConstrainedStatement pConstrainedStatement,
            CombinedConstraint.Type pType) {
        super(pConstrainedStatement);
        type = pType;
    }
    
    public CombinedConstraint.Type getType() {
        return type;
    }
    
    public CombinedConstraint createAndConstraint() {
        ObjectFactory f = getConstrainedStatement().getSQLFactory().getObjectFactory();
        CombinedConstraint result = f.newCombinedConstraint(getConstrainedStatement(),
                CombinedConstraint.Type.AND);
        parts.add(result);
        return result;
    }
    
    public CombinedConstraint createOrConstraint() {
        ObjectFactory f = getConstrainedStatement().getSQLFactory().getObjectFactory();
        CombinedConstraint result = f.newCombinedConstraint(getConstrainedStatement(),
                CombinedConstraint.Type.OR);
        parts.add(result);
        return result;
    }
    
    public BooleanConstraint createEQ() {
        BooleanConstraint result = new BooleanConstraintImpl(this, BooleanConstraint.Type.EQ);
        parts.add(result);
        return result;
    }
    
    public BooleanConstraint createNE() {
        BooleanConstraint result = new BooleanConstraintImpl(this, BooleanConstraint.Type.NE);
        parts.add(result);
        return result;
    }
    
    public BooleanConstraint createLT() {
        BooleanConstraint result = new BooleanConstraintImpl(this, BooleanConstraint.Type.LT);
        parts.add(result);
        return result;
    }
    
    public BooleanConstraint createGT() {
        BooleanConstraint result = new BooleanConstraintImpl(this, BooleanConstraint.Type.GT);
        parts.add(result);
        return result;
    }
    
    public BooleanConstraint createLE() {
        BooleanConstraint result = new BooleanConstraintImpl(this, BooleanConstraint.Type.LE);
        parts.add(result);
        return result;
    }
    
    public BooleanConstraint createGE() {
        BooleanConstraint result = new BooleanConstraintImpl(this, BooleanConstraint.Type.GE);
        parts.add(result);
        return result;
    }
    
    public BooleanConstraint createLIKE() {
        BooleanConstraint result = new BooleanConstraintImpl(this, BooleanConstraint.Type.LIKE);
        parts.add(result);
        return result;
    }
    
    public BooleanConstraint createISNULL() {
        BooleanConstraint result = new BooleanConstraintImpl(this, BooleanConstraint.Type.ISNULL);
        parts.add(result);
        return result;
    }
    
    public BooleanConstraint createIN() {
        BooleanConstraint result = new BooleanConstraintImpl(this, BooleanConstraint.Type.IN);
        parts.add(result);
        return result;
    }
    
    public void createEXISTS(SelectStatement pStatement) {
        BooleanConstraint bc = new BooleanConstraintImpl(this, BooleanConstraint.Type.EXISTS);
        parts.add(bc);
        bc.addPart(pStatement);
    }

    public BooleanConstraint createBETWEEN() {
        BooleanConstraint result = new BooleanConstraintImpl(this, BooleanConstraint.Type.BETWEEN);
        parts.add(result);
		return result;
    }

    public void addColumnSetQuery(ColumnSet pSet, TableReference pTableReference) {
        if (!pTableReference.getTable().equals(pSet.getTable())) {
            throw new IllegalStateException("The foreign keys referencing table is " +
                    pSet.getTable().getQName() +
                    ", but the arguments referencing table is " +
                    pTableReference.getTable().getQName());
        }
        if (!pTableReference.getStatement().equals(getConstrainedStatement())) {
            throw new IllegalStateException("The statement of the table reference is not the same as this constraints statement.");
        }
        for (Iterator iter = pSet.getColumns();  iter.hasNext();  ) {
            Column column = (Column) iter.next();
            BooleanConstraint eq = createEQ();
            eq.addPart(getConstrainedStatement().getSQLFactory().getObjectFactory().newColumnReference(pTableReference, column));
            eq.addPlaceholder();
        }
    }
    
    public void addJoin(ForeignKey pKey, TableReference pReferencingTable,
            TableReference pReferencedTable) {
        if (!pReferencingTable.getTable().equals(pKey.getTable())) {
            throw new IllegalStateException("The foreign keys referencing table is " +
                    pKey.getTable().getQName() +
                    ", but the arguments referencing table is " +
                    pReferencingTable.getTable().getQName());
        }
        if (!pReferencedTable.getTable().equals(pKey.getReferencedTable())) {
            throw new IllegalStateException("The foreign keys referenced table is " +
                    pKey.getReferencedTable().getQName() +
                    ", but the arguments referenced table is " +
                    pReferencedTable.getTable().getQName());
        }
        if (!pReferencingTable.getStatement().equals(getConstrainedStatement())) {
            throw new IllegalStateException("The statement of the referencing table is not the same as this constraints statement.");
        }
        if (!pReferencedTable.getStatement().equals(getConstrainedStatement())) {
            throw new IllegalStateException("The statement of the referenced table is not the same as this constraints statement.");
        }
        
        for (Iterator iter = pKey.getColumnLinks();  iter.hasNext();  ) {
            ForeignKey.ColumnLink columnReference = (ForeignKey.ColumnLink) iter.next();
            BooleanConstraint eq = createEQ();
            ObjectFactory of = getConstrainedStatement().getSQLFactory().getObjectFactory();
            eq.addPart(of.newColumnReference(pReferencingTable, columnReference.getLocalColumn()));
            eq.addPart(of.newColumnReference(pReferencedTable, columnReference.getReferencedColumn()));
        }
    }
    
    public void addJoin(TableReference pReferencingTable,
            ColumnSet pReferencingColumnSet,
            TableReference pReferencedTable,
            ColumnSet pReferencedColumnSet) {
        if (pReferencingTable == null) { throw new NullPointerException("The referencing table must not be null."); }
        if (pReferencingColumnSet == null) { throw new NullPointerException("The referencing column set must not be null."); }
        if (pReferencedTable == null) { throw new NullPointerException("The referenced table must not be null."); }
        if (pReferencedColumnSet == null) { throw new NullPointerException("The referenced column set must not be null."); }
        if (!pReferencingTable.getTable().equals(pReferencingColumnSet.getTable())) {
            throw new IllegalStateException("The referencing column sets table " +
                    pReferencingColumnSet.getTable().getQName() +
                    " doesn't match the referencing table " +
                    pReferencingTable.getTable().getQName());
        }
        if (!pReferencedTable.getTable().equals(pReferencedColumnSet.getTable())) {
            throw new IllegalStateException("The referenced column sets table " +
                    pReferencedColumnSet.getTable().getQName() +
                    " doesn't match the referenced table " +
                    pReferencedTable.getTable().getQName());
        }
        
        Iterator referencedIter = pReferencedColumnSet.getColumns();
        for (Iterator iter = pReferencingColumnSet.getColumns();  iter.hasNext();  ) {
            Column referencingColumn = (Column) iter.next();
            if (!referencedIter.hasNext()) {
                throw new IllegalStateException("The size of the referencing and referenced column sets doesn't match.");
            }
            Column referencedColumn = (Column) referencedIter.next();
            BooleanConstraint eq = createEQ();
            eq.addPart(pReferencingTable.newColumnReference(referencingColumn));
            eq.addPart(pReferencedTable.newColumnReference(referencedColumn));
        }
    }
    
    
    public int getNumParts() {
        return parts.size();
    }
    
    public Iterator getParts() {
        return parts.iterator();
    }
    
    public void addConstraint(Map pMap, Constraint pConstraint) {
        if (pConstraint instanceof CombinedConstraint) {
            CombinedConstraint source = (CombinedConstraint) pConstraint;
            CombinedConstraint target;
            if (source.getType().equals(getType())) {
                target = this;
            } else if (source.getType().equals(CombinedConstraint.Type.AND)) {
                target = createAndConstraint();
            } else if (source.getType().equals(CombinedConstraint.Type.OR)) {
                target = createOrConstraint();
            } else {
                throw new IllegalStateException("Unknown combined constraint type: " +
                        source.getType());
            }
            for (Iterator iter = source.getParts();  iter.hasNext();  ) {
                Object o = iter.next();
                if (o instanceof CombinedConstraint) {
                    target.addConstraint(pMap, (CombinedConstraint) o);
                } else if (o instanceof BooleanConstraint) {
                    target.addConstraint(pMap, (BooleanConstraint) o);
                } else {
                    throw new IllegalArgumentException("Invalid part: " + o.getClass().getName()); 
                }
            }
        } else if (pConstraint instanceof BooleanConstraint) {
            BooleanConstraint source = (BooleanConstraint) pConstraint;
            BooleanConstraint target;
            BooleanConstraint.Type sourceType = source.getType();
            if (BooleanConstraint.Type.EQ.equals(sourceType)) {
                target = createEQ();
            } else if (BooleanConstraint.Type.NE.equals(sourceType)) {
                target = createNE();
            } else if (BooleanConstraint.Type.GT.equals(sourceType)) {
                target = createGT();
            } else if (BooleanConstraint.Type.LT.equals(sourceType)) {
                target = createLT();
            } else if (BooleanConstraint.Type.GE.equals(sourceType)) {
                target = createGE();
            } else if (BooleanConstraint.Type.LE.equals(sourceType)) {
                target = createLE();
            } else if (BooleanConstraint.Type.LIKE.equals(sourceType)) {
                target = createLIKE();
            }  else if (BooleanConstraint.Type.ISNULL.equals(sourceType)) {
                target = createISNULL();
            } else if (BooleanConstraint.Type.IN.equals(sourceType)) {
                target = createIN();
            } else {
                throw new IllegalArgumentException("Invalid boolean constraint type: " + sourceType);
            }
            for (Iterator iter = source.getParts();  iter.hasNext();  ) {
                Object o = iter.next();
                if (o instanceof Value) {
                    target.addPart((Value) o);
                } else if (o instanceof ColumnReference) {
                    ColumnReference colRef = (ColumnReference) o;
                    TableReference tableRef = (TableReference) pMap.get(colRef.getTableReference());
                    if (tableRef == null) {
                        throw new IllegalStateException("Unknown reference to table " + colRef.getTableReference().getTable().getQName());
                    }
                    target.addPart(tableRef.newColumnReference(colRef.getColumn()));
                } else {
                    throw new IllegalStateException("Unknown part type: " + o.getClass().getName());
                }
            }
        } else {
            throw new IllegalStateException("Unknown constraint type: " + pConstraint.getClass().getName());
        }
    }

    public void setNOT(boolean pNot) {
        not = pNot;
    }

    public boolean isNOT() {
        return not;
    }
}
