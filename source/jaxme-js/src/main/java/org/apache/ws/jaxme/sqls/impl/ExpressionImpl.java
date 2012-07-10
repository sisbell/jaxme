package org.apache.ws.jaxme.sqls.impl;

import org.apache.ws.jaxme.sqls.Expression;
import org.apache.ws.jaxme.sqls.Statement;


/** Default implementation of {@link org.apache.ws.jaxme.sqls.Expression}.
 */
public class ExpressionImpl extends PartsImpl implements Expression {
	/** Default implementation of {@link Expression.Type}.
	 */
	public static class TypeImpl extends SQLFactoryImpl.IdentImpl implements Expression.Type {
		private static final long serialVersionUID = 3834872498671071537L;
		/** Creates a new instance with the given name.
		 */
		public TypeImpl(String pName) {
			super(pName);
		}
	}

	private final Type type;

	protected ExpressionImpl(Statement pStatement, Expression.Type pType) {
		super(pStatement);
		type = pType;
	}

	public Type getType() { return type; }

	public int getMinimumParts() { return 1; }
	public int getMaximumParts() {
		if (Expression.PRODUCT.equals(type)
		    ||  Expression.SUM.equals(type)) {
			return 0;
		} else if (Expression.DIFFERENCE.equals(type)
				   ||  Expression.QUOTIENT.equals(type)) {
			return 2;
		} else {
			throw new IllegalStateException("Invalid type: " + type);
		}
	}
}
