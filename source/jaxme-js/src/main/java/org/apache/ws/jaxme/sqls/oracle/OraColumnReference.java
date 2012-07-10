package org.apache.ws.jaxme.sqls.oracle;

import org.apache.ws.jaxme.sqls.ColumnReference;


/** An extension of {@link org.apache.ws.jaxme.sqls.ColumnReference}
 * supporting the {@link #isPrior()} clause.
 */
public interface OraColumnReference extends ColumnReference {
    /** Returns, whether the column reference has the
     * PRIOR attribute set. This attribute is required for
     * the CONNECT BY clause.
     * @see org.apache.ws.jaxme.sqls.oracle.OraSelectStatement#getConnectBy()
     * @see #setPrior(boolean)
     */
    public boolean isPrior();
    /** Sets, whether the column reference has the
     * PRIOR attribute set. This attribute is required for
     * the CONNECT BY clause.
     * @see org.apache.ws.jaxme.sqls.oracle.OraSelectStatement#getConnectBy()
     * @see #isPrior()
     */
    public void setPrior(boolean pPrior);
}
