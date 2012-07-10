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

import org.apache.ws.jaxme.sqls.CombinedConstraint;
import org.apache.ws.jaxme.sqls.JoinReference;
import org.apache.ws.jaxme.sqls.ObjectFactory;
import org.apache.ws.jaxme.sqls.SelectTableReference;
import org.apache.ws.jaxme.sqls.Table;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class JoinReferenceImpl extends SelectTableReferenceImpl implements JoinReference {
    private boolean outerJoin;
    private SelectTableReference leftTableReference;
    private CombinedConstraint onClause;
    
    /** <p>Creates a new instance of JoinReferenceImpl.</p>
     */
    protected JoinReferenceImpl(SelectTableReference pLeftTableReference, Table pRightTable,
            boolean pOuterJoin) {
        super(pLeftTableReference.getSelectStatement(), pRightTable);
        leftTableReference = pLeftTableReference;
        outerJoin = pOuterJoin;
    }
    
    public boolean isJoin() {
        return !outerJoin;
    }
    
    public boolean isLeftOuterJoin() {
        return outerJoin;
    }
    
    public SelectTableReference getLeftJoinedTableReference() {
        return leftTableReference;
    }
    
    public CombinedConstraint getOn() {
        if (onClause == null) {
            ObjectFactory f = getSelectStatement().getSQLFactory().getObjectFactory();
            onClause = f.newCombinedConstraint(getSelectStatement(), CombinedConstraint.Type.AND);
        }
        return onClause;
    }
}
