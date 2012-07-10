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

import org.apache.ws.jaxme.sqls.Column;
import org.apache.ws.jaxme.sqls.ColumnReference;
import org.apache.ws.jaxme.sqls.JoinReference;
import org.apache.ws.jaxme.sqls.SelectTableReference;
import org.apache.ws.jaxme.sqls.Table;
import org.apache.ws.jaxme.sqls.TableReference;
import org.apache.ws.jaxme.sqls.impl.ObjectFactoryImpl;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class OraObjectFactoryImpl extends ObjectFactoryImpl {
    public JoinReference newJoinReference(SelectTableReference pSelectTableReference,
                                         Table pTable,
                                         boolean pIsLeftOuterJoin) {
    if (pIsLeftOuterJoin) {
      return new OraJoinReferenceImpl(pSelectTableReference, pTable, pIsLeftOuterJoin);
    } else {
      return super.newJoinReference(pSelectTableReference, pTable, pIsLeftOuterJoin);
    }
  }
    public ColumnReference newColumnReference(TableReference pTableReference, Column pColumn) {
        return new OraColumnReferenceImpl(pTableReference, pColumn);
    }
}
