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
import org.apache.ws.jaxme.sqls.ObjectFactory;
import org.apache.ws.jaxme.sqls.SQLFactory;
import org.apache.ws.jaxme.sqls.UpdateStatement;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class UpdateStatementImpl extends SetStatementImpl implements UpdateStatement {
  private CombinedConstraint where = null;

  public UpdateStatementImpl(SQLFactory pFactory) {
    super(pFactory);
  }

  public CombinedConstraint getWhere() {
      if (where == null) {
          ObjectFactory f = getSQLFactory().getObjectFactory();
          where = f.newCombinedConstraint(this, CombinedConstraint.Type.AND);
      }
      return where;
  }
}
