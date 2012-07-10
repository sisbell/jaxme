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
import org.apache.ws.jaxme.sqls.SelectStatement;


/** Oracle specific interface of {@link org.apache.ws.jaxme.sqls.SelectStatement}.
 */
public interface OraSelectStatement extends SelectStatement {
    /** An extension of {@link SelectStatement} with the
     * ability to specify, whether NULL comes first or last.
     */
    public interface OraOrderColumn extends OrderColumn {
        /** Returns, whether nulls should appear first or last.
         */
        public boolean isNullsFirst();
    }
    /** Implements the <code>START WITH</code> part of a
     * <code>START WITH ... CONNECT BY PRIOR ...</code> clause.
     * @see #getConnectBy()
     */
    public CombinedConstraint getStartWith();

    /** Implements the <code>CONNECT BY PRIOR</code> part of a
     * <code>START WITH ... CONNECT BY PRIOR ...</code> clause.
     * @see #getStartWith()
     */
    public CombinedConstraint getConnectBy();

    /** Adds the given order column with the given values for
     * {@link org.apache.ws.jaxme.sqls.SelectStatement.OrderColumn#isDescending()}
     * and
     * {@link OraSelectStatement.OraOrderColumn#isNullsFirst()}.
     */
    public void addOrderColumn(Object pObject, boolean pDescending, boolean pNullsFirst);
}
