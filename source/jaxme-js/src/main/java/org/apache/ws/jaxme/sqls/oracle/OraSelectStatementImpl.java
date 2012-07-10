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
package org.apache.ws.jaxme.sqls.oracle;

import org.apache.ws.jaxme.sqls.CombinedConstraint;
import org.apache.ws.jaxme.sqls.ObjectFactory;
import org.apache.ws.jaxme.sqls.SQLFactory;
import org.apache.ws.jaxme.sqls.impl.SelectStatementImpl;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class OraSelectStatementImpl extends SelectStatementImpl
        implements OraSelectStatement {
    /** Default implementation of
     * {@link OraSelectStatement.OraOrderColumn}.
     */
    public static class OraOrderColumnImpl extends OrderColumnImpl
            implements OraOrderColumn {
        private final boolean nullsFirst;
        /** Creates a new instance with the given values for
         * {@link org.apache.ws.jaxme.sqls.SelectStatement.OrderColumn#getColumn()},
         * {@link org.apache.ws.jaxme.sqls.SelectStatement.OrderColumn#isDescending()},
         * and
         * {@link OraSelectStatement.OraOrderColumn#isNullsFirst()}.
         */
        public OraOrderColumnImpl(Object pObject, boolean pDescending,
                                  boolean pNullsFirst) {
            super(pObject, pDescending);
            nullsFirst = pNullsFirst;
        }
        public boolean isNullsFirst() { return nullsFirst; }
    }
    private CombinedConstraint startWith, connectByPrior;
    
    /** Creates a new instance with the given object factory.
     */
    public OraSelectStatementImpl(SQLFactory pFactory) {
        super(pFactory);
    }

	public CombinedConstraint getStartWith() {
	    if (startWith == null) {
	        ObjectFactory f = getSQLFactory().getObjectFactory();
	        startWith = f.newCombinedConstraint(this, CombinedConstraint.Type.AND);
	    }
	    return startWith;
	}

	public CombinedConstraint getConnectBy() {
	    if (connectByPrior == null) {
	        ObjectFactory f = getSQLFactory().getObjectFactory();
	        connectByPrior = f.newCombinedConstraint(this, CombinedConstraint.Type.AND);
	    }
	    return connectByPrior;
	}

    /* (non-Javadoc)
     * @see org.apache.ws.jaxme.sqls.oracle.OraSelectStatement#addOrderColumn(java.lang.Object, boolean, boolean)
     */
    public void addOrderColumn(Object pObject, boolean pDescending, boolean pNullsFirst) {
        OraOrderColumn ooc = new OraOrderColumnImpl(pObject, pDescending, pNullsFirst);
        super.addOrderColumn(ooc);
    }
}
