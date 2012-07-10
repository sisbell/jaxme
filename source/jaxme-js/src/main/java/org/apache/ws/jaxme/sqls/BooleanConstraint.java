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
package org.apache.ws.jaxme.sqls;

import org.apache.ws.jaxme.sqls.impl.BooleanConstraintImpl;

/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface BooleanConstraint extends Constraint, Parts {
	/** <p>The type of a boolean constraint.</p>
	 */
	public interface Type {
		/** A boolean constraint matching the "equal" condition.
		 */
		public static final Type EQ = new BooleanConstraintImpl.TypeImpl("EQ");
		/** A boolean constraint matching the "not equal" condition.
		 */
		public static final Type NE = new BooleanConstraintImpl.TypeImpl("NE");
		/** A boolean constraint matching the "lower than" condition.
		 */
		public static final Type LT = new BooleanConstraintImpl.TypeImpl("LT");
		/** A boolean constraint matching the "greater than" condition.
		 */
		public static final Type GT = new BooleanConstraintImpl.TypeImpl("GT");
		/** A boolean constraint matching the "lower or equal" condition.
		 */
		public static final Type LE = new BooleanConstraintImpl.TypeImpl("LE");
		/** A boolean constraint matching the "greater or equal" condition.
		 */
		public static final Type GE = new BooleanConstraintImpl.TypeImpl("GE");
		/** A boolean constraint matching the "LIKE" condition.
		 */
		public static final Type LIKE = new BooleanConstraintImpl.TypeImpl("LIKE");
		/** A boolean constraint matching the "IS NULL" condition.
		 */
		public static final Type ISNULL = new BooleanConstraintImpl.TypeImpl("ISNULL");
		/** A boolean constraint matching the "IN" condition.
		 */
		public static final Type IN = new BooleanConstraintImpl.TypeImpl("IN");
		/** A boolean constraint matching the "EXISTS" condition.
		 */
		public static final Type EXISTS = new BooleanConstraintImpl.TypeImpl("EXISTS");
		/** A boolean constraint matching the "BETWEEN" condition.
		 */
		public static final Type BETWEEN = new BooleanConstraintImpl.TypeImpl("BETWEEN");
	}
	
	/** <p>Returns the boolean constraints type.</p>
	 */
	public BooleanConstraint.Type getType();
}
