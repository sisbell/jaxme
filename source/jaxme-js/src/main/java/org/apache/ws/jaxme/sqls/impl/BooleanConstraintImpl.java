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

import org.apache.ws.jaxme.sqls.BooleanConstraint;
import org.apache.ws.jaxme.sqls.CombinedConstraint;
import org.apache.ws.jaxme.sqls.ConstrainedStatement;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class BooleanConstraintImpl extends PartsImpl implements BooleanConstraint {
	/** Default implementation of {@link BooleanConstraint.Type}.
	 */
	public static class TypeImpl extends SQLFactoryImpl.IdentImpl implements BooleanConstraint.Type {
		private static final long serialVersionUID = 3762254145096135991L;
		/** Creates a new instance with the given name.
		 */
		public TypeImpl(String pName) {
			super(pName);
		}
	}
	
	private BooleanConstraint.Type type;
	
	protected BooleanConstraintImpl(CombinedConstraint pCombinedConstraint,
			BooleanConstraint.Type pType) {
		super(pCombinedConstraint.getConstrainedStatement());
		if (pType == null) {
			throw new NullPointerException("The type must not be null.");
		}
		type = pType;
	}
	
	public BooleanConstraint.Type getType() {
		return type;
	}
	
	protected void add(Object pPart) {
		Type type = getType();
		if (BooleanConstraint.Type.IN.equals(type)) {
			// Arbitrary number of parts
		} else if (BooleanConstraint.Type.ISNULL.equals(type)) {
			// Exactly one part
			if (getNumParts() == 1) {
				throw new IllegalStateException("An IS NULL clause cannot have more than one part.");
			}
		} else if (BooleanConstraint.Type.BETWEEN.equals(type)) {
			// Exactly three parts
			if (getNumParts() == 3) {
				throw new IllegalStateException("A BETWEEN clause cannot have more than three parts.");
			}
		} else {
			// Exactly two parts
			if (getNumParts() == 2) {
				throw new IllegalStateException("An " + getType() + " clause cannot have more than two parts.");
			}
		}
		super.add(pPart);
	}
	
	public ConstrainedStatement getConstrainedStatement() {
		return (ConstrainedStatement) getStatement();
	}

	public int getMinimumParts() { return 1; }

	public int getMaximumParts() {
        if (BooleanConstraint.Type.IN.equals(type)) {
			return 0;
		} else if (BooleanConstraint.Type.EXISTS.equals(type)
				   ||  BooleanConstraint.Type.ISNULL.equals(type)) {
			return 1;
		} else if (BooleanConstraint.Type.BETWEEN.equals(type)) {
			return 3;
		} else if (BooleanConstraint.Type.EQ.equals(type)
				   ||  BooleanConstraint.Type.NE.equals(type)
				   ||  BooleanConstraint.Type.GT.equals(type)
				   ||  BooleanConstraint.Type.LT.equals(type)
				   ||  BooleanConstraint.Type.GE.equals(type)
				   ||  BooleanConstraint.Type.LE.equals(type)
				   ||  BooleanConstraint.Type.LIKE.equals(type)) {
			return 2;
		} else {
			throw new IllegalStateException("Invalid type: " + type);
        }
	}
}
