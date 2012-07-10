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

import org.apache.ws.jaxme.sqls.SelectTableReference;
import org.apache.ws.jaxme.sqls.Table;
import org.apache.ws.jaxme.sqls.impl.JoinReferenceImpl;

/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class OraJoinReferenceImpl extends JoinReferenceImpl {
  private boolean isOracle8LeftOuterJoin;

  protected OraJoinReferenceImpl(SelectTableReference pLeftTableReference, Table pRightTable, boolean pOuterJoin) {
    super(pLeftTableReference, pRightTable, pOuterJoin);
  }

  public void setIsOracle8LeftOuterJoin(boolean pOracle8LeftOuterJoin) {
    isOracle8LeftOuterJoin = pOracle8LeftOuterJoin;
  }

  public boolean isOracle8LeftOuterJoin() {
    return isOracle8LeftOuterJoin;
  }
}
