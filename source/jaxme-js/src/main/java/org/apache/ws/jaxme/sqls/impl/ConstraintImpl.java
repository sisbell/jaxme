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

import org.apache.ws.jaxme.sqls.ConstrainedStatement;
import org.apache.ws.jaxme.sqls.Constraint;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public abstract class ConstraintImpl implements Constraint {
   private ConstrainedStatement constrainedStatement;

   /** <p>Creates a new instance of ConstraintImpl.</p>
    */
	public ConstraintImpl(ConstrainedStatement pStatement) {
      constrainedStatement = pStatement;
	}

   public ConstrainedStatement getConstrainedStatement() {
      return constrainedStatement;
   }
}
