package org.apache.ws.jaxme.sqls.oracle;

import org.apache.ws.jaxme.sqls.Column;
import org.apache.ws.jaxme.sqls.TableReference;
import org.apache.ws.jaxme.sqls.impl.ColumnReferenceImpl;


/** Default implementation of {@link org.apache.ws.jaxme.sqls.oracle.OraColumnReference}.
 */
public class OraColumnReferenceImpl extends ColumnReferenceImpl
        implements OraColumnReference {
    private boolean prior;

    OraColumnReferenceImpl(TableReference pTableReference, Column pColumn) {
        super(pTableReference, pColumn);
    }

    public boolean isPrior() {
        return prior;
    }

    public void setPrior(boolean pPrior) {
        prior = pPrior;
    }
}
